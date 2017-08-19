/*
 * Copyright (C) 2015 Vince Styling
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xdja.imsdk.volley.request;

import android.text.TextUtils;

import com.xdja.http.Header;
import com.xdja.http.HttpEntity;
import com.xdja.http.HttpResponse;
import com.xdja.http.util.EntityUtils;
import com.xdja.imsdk.volley.NetworkResponse;
import com.xdja.imsdk.volley.Request;
import com.xdja.imsdk.volley.Response;
import com.xdja.imsdk.volley.Response.CanceledListener;
import com.xdja.imsdk.volley.Response.ErrorListener;
import com.xdja.imsdk.volley.Response.Listener;
import com.xdja.imsdk.volley.Response.LoadingListener;
import com.xdja.imsdk.volley.VolleyLog;
import com.xdja.imsdk.volley.error.VolleyError;
import com.xdja.imsdk.volley.toolbox.HttpHeaderParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.zip.GZIPInputStream;

/**
 * Its purpose is provide a big file download impmenetation, support continuous transmission
 * on the breakpoint download if server-side enable 'Content-Range' Header.
 * for example:
 * execute a request and submit header like this : Range=bytes=1000- (1000 means the begin point of the file).
 * response return a header like this Content-Range=bytes 1000-1895834/1895835, that's continuous transmission,
 * also return Accept-Ranges=bytes tell us the server-side supported range transmission.
 * <p/>
 * This request will stay longer in the thread which dependent your download file size,
 * that will fill up your thread poll as soon as possible if you launch many request,
 * if all threads is busy, the high priority request such as {@link StringRequest}
 * might waiting long time, so don't use it alone.
 * we highly recommend you to use it with the {@link com.xdja.imsdk.volley.request.DownloadRequest},
 * FileDownloader maintain a download task queue, let's set the maximum parallel request count, the rest will await.
 * <p/>
 * By the way, this request priority was {@link Priority#LOW}, higher request will jump ahead it.
 */
public class DownloadRequest extends Request<String> {
	
	/** The default buffer size*/
	private static final int BUFFER_SIZE = 6 * 1024;
	
    /** The download file storePath*/
    private File mStoreFile;
    private File mTemporaryFile;

    private long mFileSize;

    private Listener<String> mListener;

    //private LoadingListener mLoadingListener;

    boolean bSupportRange = false;

    public DownloadRequest(String url, Listener<String> listener, ErrorListener errorListener,
    		LoadingListener loadingListener, CanceledListener canceledListener){
    	this(Method.GET, url, listener, errorListener, loadingListener, canceledListener);
    }
    
    public DownloadRequest(int method, String url, Listener<String> listener, ErrorListener errorListener, 
    		LoadingListener loadingListener, CanceledListener canceledListener){
    	super(method, url, errorListener, loadingListener, canceledListener);
    	mListener = listener;
        //mLoadingListener = loadingListener;
    }
    
    public void setTarget(String storePath, long fileSize, boolean isSupportRange){
        bSupportRange = isSupportRange;
    	setTarget(new File(storePath), fileSize);
    }
    
    public void setTarget(File storeFile, long fileSize){
    	mStoreFile = storeFile;
        mFileSize = fileSize;
    	mTemporaryFile = new File(storeFile + ".tmp");
        if (!bSupportRange && mTemporaryFile.exists()) {
            VolleyLog.d("Illegal temp file is exist, so delete " + mTemporaryFile.delete());
        }
        //create parent path if not exist.
        File dir = mTemporaryFile.getParentFile();
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                VolleyLog.e("create file dir Failed:" + dir.getAbsolutePath());
            }
        }
    }
    
    /**
     * Init or reset the Range header, ensure the begin position always be the temporary file size.
     */
    @Override
    public void prepare() {
        // Note: if the request header "Range" greater than the actual length that server-size have,
        // the response header "Content-Range" will return "bytes */[actual length]", that's wrong.

        if (bSupportRange) {
            if (mTemporaryFile.length() >= mFileSize) {
                addHeader("Range", String.format("bytes=%d-", 0));
                mTemporaryFile.delete();
            } else {
                addHeader("Range", String.format("bytes=%d-", mTemporaryFile.length()));
            }
        } else {
            addHeader("Range", String.format("bytes=%d-", 0));
        }
        //Suppress the HttpStack accept gzip encoding, avoid the progress calculate wrong problem.
        //addHeader("Accept-Encoding", "identity");
    }

    /**
     * Ignore the response content, just rename the TemporaryFile to StoreFile.
     */
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        if (!isCanceled()) {
            if (mTemporaryFile.canRead() && mTemporaryFile.length() > 0 &&
                    mTemporaryFile.length() >= mFileSize) {
                if (mTemporaryFile.renameTo(mStoreFile)) {
                    mTemporaryFile.delete();
                    return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
                } else {
                    return Response.error(new VolleyError("Can't rename the download temporary file!"));
                }
            } else {
                //if the store file exit, so the file download completed.
                if (mStoreFile.exists() && mStoreFile.length() > 0 &&
                        mTemporaryFile.length() >= mFileSize) {
                    return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
                } else {
                    return Response.error(new VolleyError("Download temporary file was invalid!"));
                }
            }
        }
        return Response.error(new VolleyError("Request was Canceled!"));
    }

    private boolean hasError(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode < 200 || statusCode > 299) {
            return true;
        }
        return false;
    }

    /**
     * In this method, we got the Content-Length, with the TemporaryFile length,
     * we can calculate the actually size of the whole file, if TemporaryFile not exists,
     * we'll take the store file length then compare to actually size, and if equals,
     * we consider this download was already done.
     * We used {@link RandomAccessFile} to continue download, when download success,
     * the TemporaryFile will be rename to StoreFile.
     */
    public byte[] handleResponse(HttpResponse httpResponse) throws IOException, VolleyError {

        if (hasError(httpResponse)) {
            throw new IOException();
        }

        HttpEntity entity = httpResponse.getEntity();
        //request entity size
        long fileSize = entity.getContentLength();
        long downloadedSize = mTemporaryFile.length();
        boolean isSupportRange = isSupportRange(httpResponse)/* && bSupportRange*/;
        if (isSupportRange) {
            String realRangeValue = getHeader(httpResponse, "Content-Range");
            if (!TextUtils.isEmpty(realRangeValue)) {
                String assumeRangeValue = "bytes " + downloadedSize + "-";
                VolleyLog.d("assumeRangeValue:" + assumeRangeValue + ",realRangeValue:" + realRangeValue);
                //realRangeValue:bytes 0-19315/19316
                //bytes 179-
                //cannot throw exception, try to correct.

                try {
                    String value = realRangeValue.replace("bytes ", "");
                    int start = Integer.parseInt(value.substring(0, value.indexOf("-")));
                    VolleyLog.d("assumeRangeValue:" + assumeRangeValue + ", start:" + start);
                    if (start == 0) {
                        downloadedSize = 0;
                        mTemporaryFile.delete();
                    } else {
                        if (TextUtils.indexOf(realRangeValue, assumeRangeValue) == -1) {
                            throw new IOException(
                                    "The Content-Range Header is invalid Assume[" + assumeRangeValue + "] vs Real[" + realRangeValue + "], " +
                                            "please remove the temporary file [" + mTemporaryFile + "].");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new IOException(e);
                }
            }
        } else {
            downloadedSize = 0;
            mTemporaryFile.delete();
        }

        //create new temp file.
        if (!mTemporaryFile.exists()) {
            mTemporaryFile.createNewFile();
        }

        RandomAccessFile tmpFileRaf = new RandomAccessFile(mTemporaryFile, "rw");

        // If server-side support range download, we seek to last point of the temporary file.
        if (isSupportRange) {
            tmpFileRaf.seek(downloadedSize);
        } else {
            // If not, truncate the temporary file then start download from beginning.
            tmpFileRaf.setLength(0);
        }

        InputStream in = null;
        try {
            in = entity.getContent();
            // Determine the response gzip encoding, support for HttpClientStack download.
            if (isGzipContent(httpResponse) && !(in instanceof GZIPInputStream)) {
                in = new GZIPInputStream(in);
            }
            byte[] buffer = new byte[BUFFER_SIZE];
            int offset = 0;
            fileSize += downloadedSize;
            while ((offset = in.read(buffer)) != -1) {
                tmpFileRaf.write(buffer, 0, offset);
                downloadedSize += offset;
                deliverLoading(fileSize, downloadedSize);
                if (isCanceled()) {
                    finish("perform-discard-cancelled");
                    throw new VolleyError("request is canceled.");
                }
            }
        } finally {
            try {
                //close the RandomAccessFile
                if (tmpFileRaf != null) {
                    tmpFileRaf.close();
                }
                // Close the InputStream
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
            	VolleyLog.v("Error occured when calling InputStream.close");
            }

            try {
                // release the resources by "consuming the content".
                EntityUtils.consume(entity);
            } catch (Exception e) {
                // This can happen if there was an exception above that left the entity in
                // an invalid state.
                VolleyLog.v("Error occured when calling consumingContent");
            }
        }
        return new byte[0];
    }

    @Override
    protected void deliverResponse(String response) {
        if (mListener != null) {
            mListener.onResponse(response);
        }
    }
    
    private String getHeader(HttpResponse response, String key) {
        Header header = response.getFirstHeader(key);
        return header == null ? null : header.getValue();
    }
    
    private boolean isSupportRange(HttpResponse response) {
        if (TextUtils.equals(getHeader(response, "Accept-Ranges"), "bytes")) {
            return true;
        }
        String value = getHeader(response, "Content-Range");
        return value != null && value.startsWith("bytes");
    }
    
    private boolean isGzipContent(HttpResponse response) {
        return TextUtils.equals(getHeader(response, "Content-Encoding"), "gzip");
    }

    @Override
    public Priority getPriority() {
        return Priority.LOW;
    }
}
