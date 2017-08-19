package com.xdja.imsdk.volley.request;

import com.xdja.http.HttpEntity;
import com.xdja.http.HttpResponse;
import com.xdja.http.entity.ContentType;
import com.xdja.http.entity.mime.HttpMultipartMode;
import com.xdja.http.entity.mime.MultipartEntityBuilder;
import com.xdja.http.util.EntityUtils;
import com.xdja.imsdk.volley.NetworkResponse;
import com.xdja.imsdk.volley.Request;
import com.xdja.imsdk.volley.Response;
import com.xdja.imsdk.volley.error.VolleyError;
import com.xdja.imsdk.volley.toolbox.HttpHeaderParser;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * 上传文件请求
 * Created by xdjaxa on 2016/5/19.
 */
public class UploadRequest extends Request<String>{

    private Response.Listener mListener;

    public UploadRequest(int method, String url, Response.Listener listener,
                         Response.ErrorListener errorListener,
                         Response.LoadingListener loadingListener) {
        this(method, url, listener, errorListener, loadingListener, null);
    }

    public UploadRequest(int method, String url, Response.Listener listener,
                         Response.ErrorListener errorListener,
                         Response.LoadingListener loadingListener,
                         Response.CanceledListener canceledListener) {
        super(method, url, errorListener, loadingListener, canceledListener);
        mListener = listener;
    }

    /**
     * set fieldId request HttpEntity
     *
     * @param fileName
     */
    public void setFileFid(String fileName) {
        Random rand = new Random();
        String boundary = "_" + rand.nextDouble() + "_BOUNDARY_" + rand.nextDouble() + "_";
        // params
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        //开始文件上传的第一步是获取Fid，不需要真的上传文件内容，所以这里添加的字符流为一个空的byte数组
        builder.addBinaryBody("file", new byte[0], ContentType.APPLICATION_OCTET_STREAM, fileName);
        builder.setBoundary(boundary);
        HttpEntity entity = builder.build();
        setHttpEntity(entity);
    }

    /**
     * @param filePath
     */
    public void setUploadFile(String filePath) {
        File sourceFile = new File(filePath);
        if (sourceFile != null && sourceFile.exists()) {
            setPostFile(sourceFile);
        } else {
            deliverError(new VolleyError("file not exist!"));
        }
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
            e.printStackTrace();
        } catch (NullPointerException e) {
            parsed = "";
            e.printStackTrace();
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    /**
     * http post response
     * @param httpResponse
     * @return
     */
    public byte[] handleResponse(HttpResponse httpResponse)
            throws IOException {
        HttpEntity entity = httpResponse.getEntity();
        if (entity == null) {
            return new byte[0];
        }
        return EntityUtils.toByteArray(entity);
    }

    @Override
    protected void deliverResponse(String response) {
        if (mListener != null){
            mListener.onResponse(response);
        }
    }
}
