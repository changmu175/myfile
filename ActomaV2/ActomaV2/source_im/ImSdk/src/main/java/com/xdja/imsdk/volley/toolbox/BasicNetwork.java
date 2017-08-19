/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.xdja.imsdk.volley.toolbox;

import android.os.SystemClock;

import com.xdja.http.Header;
import com.xdja.http.HttpEntity;
import com.xdja.http.HttpResponse;
import com.xdja.http.HttpStatus;
import com.xdja.http.StatusLine;
import com.xdja.http.conn.ConnectTimeoutException;
import com.xdja.http.util.EntityUtils;
import com.xdja.imsdk.volley.Cache.Entry;
import com.xdja.imsdk.volley.Network;
import com.xdja.imsdk.volley.NetworkResponse;
import com.xdja.imsdk.volley.Request;
import com.xdja.imsdk.volley.RetryPolicy;
import com.xdja.imsdk.volley.VolleyLog;
import com.xdja.imsdk.volley.error.AuthFailureError;
import com.xdja.imsdk.volley.error.ServerError;
import com.xdja.imsdk.volley.error.TimeoutError;
import com.xdja.imsdk.volley.error.VolleyError;
import com.xdja.imsdk.volley.error.VolleyErrorCode;
import com.xdja.imsdk.volley.request.DownloadRequest;
import com.xdja.imsdk.volley.request.UploadRequest;
import com.xdja.imsdk.volley.stack.HttpStack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * A network performing Volley requests over an {@link HttpStack}.
 */
public class BasicNetwork implements Network {
    private static int SLOW_REQUEST_THRESHOLD_MS = 3000;

    private static int REQUEST_DELAY_MS = 3000;

    private static int DEFAULT_POOL_SIZE = 4096;

    private static int DEFAULT_BUFFER_SIZE = 1024;

    protected final HttpStack mHttpStack;

    protected final ByteArrayPool mPool;

    /**
     * @param httpStack HTTP stack to be used
     */
    public BasicNetwork(HttpStack httpStack) {
        // If a pool isn't passed in, then build a small default pool that will give us a lot of
        // benefit and not use too much memory.
        this(httpStack, new ByteArrayPool(DEFAULT_POOL_SIZE));
    }

    /**
     * @param httpStack HTTP stack to be used
     * @param pool a buffer pool that improves GC performance in copy operations
     */
    public BasicNetwork(HttpStack httpStack, ByteArrayPool pool) {
        mHttpStack = httpStack;
        mPool = pool;
    }

    @Override
    public NetworkResponse performRequest(Request<?> request)
            throws VolleyError {
        long requestStart = SystemClock.elapsedRealtime();
        VolleyLog.d("start perform request (" + Thread.currentThread().getName() + ")");
        while (true) {
			if (request.isCanceled()) {
				request.finish("perform-discard-cancelled");
				throw new VolleyError("request is canceled.");
			}
        	 
            HttpResponse httpResponse = null;
            byte[] responseContents = null;
            Map<String, String> responseHeaders = Collections.emptyMap();
            long connectStartTime = SystemClock.elapsedRealtime();
            try {
                // Gather headers.
                Map<String, String> headers = new HashMap<String, String>();
                httpResponse = mHttpStack.performRequest(request, headers);
                if (httpResponse == null) {
                    VolleyLog.e("performRequest finished, but exception.");
                    throw new IOException();
                }
                VolleyLog.d("http performRequest finished(%d ms).", (SystemClock.elapsedRealtime()
                        - requestStart));

                //网络正常回调
                //[S]modify by lll@xdja.com for net changed 2017/1/6
                request.onNetChange(VolleyErrorCode.VOLLEY_ERROR, null);
                //[E]modify by lll@xdja.com for net changed 2017/1/6

                StatusLine statusLine = httpResponse.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                VolleyLog.d("performRequest code %d for %s", statusCode, request.getUrl());

                responseHeaders = convertHeaders(httpResponse.getAllHeaders());
                // Handle cache validation.
                if (statusCode == HttpStatus.SC_NOT_MODIFIED) {

                    Entry entry = request.getCacheEntry();
                    if (entry == null) {
                        return new NetworkResponse(HttpStatus.SC_NOT_MODIFIED, null,
                                responseHeaders, true,
                                SystemClock.elapsedRealtime() - requestStart);
                    }

                    // A HTTP 304 response does not have all header fields. We
                    // have to use the header fields from the cache entry plus
                    // the new ones from the response.
                    // http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.5
                    entry.responseHeaders.putAll(responseHeaders);
                    return new NetworkResponse(HttpStatus.SC_NOT_MODIFIED, entry.data,
                            entry.responseHeaders, true,
                            SystemClock.elapsedRealtime() - requestStart);
                }

                // Some responses such as 204s do not have content.  We must check.
                if (httpResponse.getEntity() != null) {
                    if (request instanceof DownloadRequest){
                    	DownloadRequest downloadRequest = (DownloadRequest) request;
                        responseContents = downloadRequest.handleResponse(httpResponse);
                    } else if (request instanceof UploadRequest) {
                        UploadRequest uploadRequest = (UploadRequest) request;
                        responseContents = uploadRequest.handleResponse(httpResponse);
                    } else {
                        responseContents = entityToBytes(request, httpResponse.getEntity());
                    }
                } else {
                    // Add 0 byte response as a way of honestly representing a
                    // no-content request.
                    responseContents = new byte[0];
                }

                // if the request is slow, log it.
                long requestLifetime = SystemClock.elapsedRealtime() - requestStart;
                logSlowRequests(requestLifetime, request, responseContents, statusLine);

                if (statusCode < 200 || statusCode > 299) {
                    throw new IOException();
                }
                VolleyLog.d("Handle Response finished.");
                return new NetworkResponse(statusCode, responseContents, responseHeaders, false,
                        SystemClock.elapsedRealtime() - requestStart);
            } catch (SocketTimeoutException e) {
                VolleyLog.d("Request SocketTimeoutException:" + e.getMessage());
                request.onNetChange(VolleyErrorCode.TIMEOUT_ERROR, e.getMessage());
                attemptRetryOnException("socket", request, new TimeoutError(),
                        SystemClock.elapsedRealtime() - connectStartTime);
            } catch (ConnectTimeoutException e) {
                VolleyLog.d("Request ConnectTimeoutException:" + e.getMessage());
                request.onNetChange(VolleyErrorCode.TIMEOUT_ERROR, e.getMessage());
                attemptRetryOnException("connection", request, new TimeoutError(),
                        SystemClock.elapsedRealtime() - connectStartTime);
            } catch (MalformedURLException e) {
            	attemptRetryOnException("connection", request, new TimeoutError(),
                        SystemClock.elapsedRealtime() - connectStartTime);
            } catch (IOException e) {
                int statusCode = 0;
                NetworkResponse networkResponse = null;
                if (httpResponse != null) {
                    statusCode = httpResponse.getStatusLine().getStatusCode();
                } else {
                    //no connect error
                    //throw new NoConnectionError(e);
                    if (request.getRetryPolicy() != null &&
                            request.getRetryPolicy().getElapsedTimeTimeoutMs() > 10 * 1000) {
                        request.onNetChange(VolleyErrorCode.NO_CONNECTION_ERROR, e.getMessage());
                    }
                }
                VolleyLog.d("Unexpected response code %d for %s", statusCode, request.getUrl());

                long connectExpendTime = SystemClock.elapsedRealtime() - connectStartTime;
                if (connectExpendTime < REQUEST_DELAY_MS){
                	try {
						Thread.sleep(REQUEST_DELAY_MS);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
                }

                if (request instanceof DownloadRequest ||
                        request instanceof UploadRequest) {
                	//ticket无效，出现在https请求的过程中，文件上传下载，需要激活重发机制
                } else {
                    //deal with ticket invalid
                    if (statusCode == 400 || statusCode == 401){
                        if (hasTicketError(responseContents)) {
                            networkResponse = new NetworkResponse(statusCode, responseContents,
                                    responseHeaders, false, SystemClock.elapsedRealtime() - requestStart);

                            throw new ServerError(networkResponse);
                        }
                    }
                }

                if (responseContents != null) {
                    networkResponse = new NetworkResponse(statusCode, responseContents,
                            responseHeaders, false, SystemClock.elapsedRealtime() - requestStart);
                    if (statusCode == HttpStatus.SC_UNAUTHORIZED ||
                            statusCode == HttpStatus.SC_FORBIDDEN) {
                        attemptRetryOnException("auth", request, new AuthFailureError(networkResponse),
                                SystemClock.elapsedRealtime() - connectStartTime);
                    } else {
                    	attemptRetryOnException("connection", request, new ServerError(networkResponse),
                                SystemClock.elapsedRealtime() - connectStartTime);
                    }
                } else {
                    VolleyLog.d("Request NoConnectException:" + e.getMessage());
                    attemptRetryOnException("connection", request, new VolleyError(e.getMessage()),
                            SystemClock.elapsedRealtime() - connectStartTime);
                }
            } finally {
                try {
                    if (httpResponse != null) {
                        EntityUtils.consume(httpResponse.getEntity());
                    }
                } catch (IllegalStateException exception) {
                    exception.printStackTrace();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    /**
     * check for ticket invalid error
     * @param responseContents
     */
    private boolean hasTicketError(byte[] responseContents) {
        try {
            String content = String.valueOf(responseContents);
            VolleyLog.w("ERROR:" + content);
            JSONObject object = new JSONObject(content);
            if (object != null) {
                String errCode = object.optString("errCode");
                String message = object.optString("message");
                if ("0x9008".equals(errCode) && "ticket_is_invalid".equals(message)) {
                    VolleyLog.e("ticket invalid ERROR!!");
                    return true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Logs requests that took over SLOW_REQUEST_THRESHOLD_MS to complete.
     */
    private void logSlowRequests(long requestLifetime, Request<?> request,
            byte[] responseContents, StatusLine statusLine) {
        if (VolleyLog.DEBUG || requestLifetime > SLOW_REQUEST_THRESHOLD_MS) {
            VolleyLog.d("HTTP response for request=<%s> [lifetime=%d], [size=%s], " +
                    "[rc=%d], [retryCount=%s]", request, requestLifetime,
                    responseContents != null ? responseContents.length : "null",
                    statusLine.getStatusCode(), request.getRetryPolicy().getCurrentRetryCount());
        }
    }

    /**
     * Attempts to prepare the request for a retry. If there are no more attempts remaining in the
     * request's retry policy, a timeout exception is thrown.
     * @param request The request to use.
     */
    private static void attemptRetryOnException(String logPrefix, Request<?> request,
            VolleyError exception, long connectExpendTime) throws VolleyError {
        RetryPolicy retryPolicy = request.getRetryPolicy();
        int oldTimeout = request.getTimeoutMs();

        try {
            retryPolicy.retry(exception, connectExpendTime);
        } catch (VolleyError e) {
            //20 min request timeout
            request.onNetChange(VolleyErrorCode.TIMEOUT_ERROR, e.getMessage());
            request.addMarker(
                    String.format("%s-timeout-giveup [timeout=%s]", logPrefix, oldTimeout));
            throw e;
        }
        request.addMarker(String.format("%s-retry [timeout=%s]", logPrefix, oldTimeout));
    }

    /** Reads the contents of HttpEntity into a byte[]. */
	private byte[] entityToBytes(Request<?> request, HttpEntity entity)
            throws IOException, VolleyError {

        VolleyLog.d("entityToBytes request:" + request.toString());

        PoolingByteArrayOutputStream bytes =
                new PoolingByteArrayOutputStream(mPool, (int) entity.getContentLength());
        byte[] buffer = null;
        try {
            InputStream in = entity.getContent();
            if (in == null) {
                VolleyLog.d("InputStream is null.");
                throw new IOException();
            }
            buffer = mPool.getBuf(DEFAULT_BUFFER_SIZE);
            long length = entity.getContentLength();
            long current = 0;
            int count = -1;
            while ((count = in.read(buffer)) != -1) {
                if (count > 0 && bytes != null){
                    bytes.write(buffer, 0, count);
                    current += count;
                    if (length > 0) {
                        request.deliverLoading(length, current);
                    } else {
                        request.deliverLoading(length == -1 ? current * 2 : length, current);
                    }
                }
                if (request.isCanceled()) {
                    VolleyLog.d("entityToBytes(request is canceled).");
                    request.finish("perform-discard-cancelled");
                    throw new VolleyError("request is canceled.");
                }
            }
            VolleyLog.d("read data completed!!");
            return bytes.toByteArray();
        } finally {
            try {
                // Close the InputStream and release the resources by "consuming the content".
                EntityUtils.consume(entity);
                mPool.returnBuf(buffer);
                bytes.close();
            } catch (IOException e) {
                // This can happen if there was an exception above that left the entity in
                // an invalid state.
                VolleyLog.v("Error occured when calling consumingContent");
            }
        }
    }

    /**
     * Converts Headers[] to Map<String, String>.
     */
    protected static Map<String, String> convertHeaders(Header[] headers) {
        Map<String, String> result = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < headers.length; i++) {
            result.put(headers[i].getName(), headers[i].getValue());
        }
        return result;
    }
}
