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

package com.xdja.imsdk.volley.stack;

import android.os.SystemClock;

import com.xdja.http.HttpEntity;
import com.xdja.http.HttpResponse;
import com.xdja.http.NameValuePair;
import com.xdja.http.client.HttpClient;
import com.xdja.http.client.methods.CloseableHttpResponse;
import com.xdja.http.client.methods.HttpDelete;
import com.xdja.http.client.methods.HttpEntityEnclosingRequestBase;
import com.xdja.http.client.methods.HttpGet;
import com.xdja.http.client.methods.HttpHead;
import com.xdja.http.client.methods.HttpOptions;
import com.xdja.http.client.methods.HttpPost;
import com.xdja.http.client.methods.HttpPut;
import com.xdja.http.client.methods.HttpTrace;
import com.xdja.http.client.methods.HttpUriRequest;
import com.xdja.http.entity.ByteArrayEntity;
import com.xdja.http.impl.client.CloseableHttpClient;
import com.xdja.http.impl.client.HttpClients;
import com.xdja.http.message.BasicNameValuePair;
import com.xdja.http.util.EntityUtils;
import com.xdja.imsdk.volley.Request;
import com.xdja.imsdk.volley.Request.Method;
import com.xdja.imsdk.volley.RetryPolicy;
import com.xdja.imsdk.volley.VolleyLog;
import com.xdja.imsdk.volley.error.AuthFailureError;
import com.xdja.imsdk.volley.error.VolleyError;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An HttpStack that performs request over an {@link HttpClient}.
 */
public class HttpClientStack  implements HttpStack {


    private static CloseableHttpClient mHttpClient = null;

    private final static String HEADER_CONTENT_TYPE = "Content-Type";

    private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

    public static final int BUFFER_SIZE = 128 * 1024;

    public HttpClientStack() {
    }

    public HttpClientStack(CloseableHttpClient client) {
        mHttpClient = client;
    }

    public static void release() {
        try {
            if (mHttpClient != null) {
                mHttpClient.close();
                mHttpClient = null;
            }
        } catch (Exception e) {
            //do nothing
        }
    }

    @SuppressWarnings("unused")
    private static List<NameValuePair> getPostParameterPairs(Map<String, String> postParams) {
        List<NameValuePair> result = new ArrayList<NameValuePair>(postParams.size());
        for (String key : postParams.keySet()) {
            result.add(new BasicNameValuePair(key, postParams.get(key)));
        }
        return result;
    }

    /**
     * 请求是否取消
     * @param request
     * @throws VolleyError
     */
    private void assertCanceled(Request<?> request) throws VolleyError {
        //请求已经取消
        if (request != null && request.isCanceled()) {
            VolleyLog.d("HttpClientStack performRequest canceled.");
            request.finish("perform-discard-cancelled");
            throw new VolleyError("request is canceled.");
        }
    }

    /**
     * 关闭响应流
     * @param response
     * @throws IOException
     */
    private void closeResponse(CloseableHttpResponse response) throws IOException {
        if (response != null) {
            if (hasError(response)) {
                throw new IOException();
            }
            response.close();
        }
    }

    /**
     * 请求超时
     * @param request
     * @throws VolleyError
     */
    private void attemptRetryOnException(Request<?> request, long connectExpendTime)
            throws VolleyError {
        RetryPolicy retryPolicy = request.getRetryPolicy();

        long elapsedRealTime = retryPolicy.getElapsedTimeTimeoutMs() +
                (SystemClock.elapsedRealtime() - connectExpendTime);

        if (elapsedRealTime > retryPolicy.getInitialTimeoutMs()) {
            VolleyLog.e("upload file timeout, which currently consumes " + elapsedRealTime+ " ms");
            request.addMarker("request_discard_timeout");
            throw new VolleyError("perform request timeout, " + request.toString());
        }
    }

    @Override
    public CloseableHttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders)
            throws IOException, VolleyError {

        CloseableHttpResponse response = null;
        HttpPost post = new HttpPost(request.getUrl());

        HttpEntity entity = request.getHttpEntity();

        long connectStartTime = SystemClock.elapsedRealtime();

        if (entity != null) {
            setHeader(post, request.getHeaders());
            post.setEntity(entity);
            response = getHttpClient().execute(post);

            closeResponse(response);
            assertCanceled(request);
            attemptRetryOnException(request, connectStartTime);

        } else {

            FileInputStream fileInputStream = null;
            try {
                File uploadFile = request.getPostFile();
                long fileSize = uploadFile.length();

                byte[] buffer = new byte[BUFFER_SIZE];
                int length;
                ByteArrayEntity byteArrayEntity = null;
                fileInputStream = new FileInputStream(request.getPostFile());
                post.setHeader(HEADER_CONTENT_TYPE, APPLICATION_OCTET_STREAM);
                fileInputStream.skip(request.getTransLateSize());

                long transferredSize = request.getTransLateSize();

                while ((length = fileInputStream.read(buffer)) != -1) {
                    // file upload
                    byteArrayEntity = new ByteArrayEntity(buffer, 0, length);
                    post.setEntity(byteArrayEntity);
                    long start = System.currentTimeMillis();
                    response = getHttpClient().execute(post);

                    closeResponse(response);
                    assertCanceled(request);
                    attemptRetryOnException(request, connectStartTime);

                    if (response != null) {

                        // translate callback
                        transferredSize += length;
                        VolleyLog.d("fileName:" + request.getPostFile().getName() +
                                ", fileSize:" + fileSize + ", transferredSize:" + transferredSize +
                                ", requestTime:" + (System.currentTimeMillis() - start) + " ms");
                        request.setTransLateSize(transferredSize);
                        request.deliverLoading(fileSize, transferredSize);
                    }
                }
            } finally {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                EntityUtils.consume(entity);
            }
        }
        return response;
    }

    private synchronized CloseableHttpClient getHttpClient() {

        if (mHttpClient == null) {
            mHttpClient = HttpClients.createDefault();
        }
        return mHttpClient;
    }

    private boolean hasError(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 200 || statusCode == 206) {
            return false;
        }
        VolleyLog.e("handle response, but has error, code is " + statusCode);
        return true;
    }

    private void setHeader(HttpPost post, Map<String, String > headers){
        for (String key : headers.keySet()){
            post.setHeader(key, headers.get(key));
        }
    }

    /**
     * Creates the appropriate subclass of HttpUriRequest for passed in request.
     */
    /* protected */ static HttpUriRequest createHttpRequest(Request<?> request,
            Map<String, String> additionalHeaders) throws AuthFailureError {
        switch (request.getMethod()) {
            case Method.DEPRECATED_GET_OR_POST: {
                // This is the deprecated way that needs to be handled for backwards compatibility.
                // If the request's post body is null, then the assumption is that the request is
                // GET.  Otherwise, it is assumed that the request is a POST.
                byte[] postBody = request.getPostBody();
                if (postBody != null) {
                    HttpPost postRequest = new HttpPost(request.getUrl());
                    postRequest.addHeader(HEADER_CONTENT_TYPE, request.getPostBodyContentType());
                    HttpEntity entity;
                    entity = new ByteArrayEntity(postBody);
                    postRequest.setEntity(entity);
                    return postRequest;
                } else {
                    return new HttpGet(request.getUrl());
                }
            }
            case Method.GET:
                return new HttpGet(request.getUrl());
            case Method.DELETE:
                return new HttpDelete(request.getUrl());
            case Method.POST: {
                HttpPost postRequest = new HttpPost(request.getUrl());
                postRequest.addHeader(HEADER_CONTENT_TYPE, request.getBodyContentType());
                setEntityIfNonEmptyBody(postRequest, request);
                return postRequest;
            }
            case Method.PUT: {
                HttpPut putRequest = new HttpPut(request.getUrl());
                putRequest.addHeader(HEADER_CONTENT_TYPE, request.getBodyContentType());
                setEntityIfNonEmptyBody(putRequest, request);
                return putRequest;
            }
            case Method.HEAD:
                return new HttpHead(request.getUrl());
            case Method.OPTIONS:
                return new HttpOptions(request.getUrl());
            case Method.TRACE:
                return new HttpTrace(request.getUrl());
            case Method.PATCH: {
                HttpPatch patchRequest = new HttpPatch(request.getUrl());
                patchRequest.addHeader(HEADER_CONTENT_TYPE, request.getBodyContentType());
                setEntityIfNonEmptyBody(patchRequest, request);
                return patchRequest;
            }
            default:
                throw new IllegalStateException("Unknown request method.");
        }
    }

    private static void setEntityIfNonEmptyBody(HttpEntityEnclosingRequestBase httpRequest,
            Request<?> request) throws AuthFailureError {
        byte[] body = request.getBody();
        if (body != null) {
            HttpEntity entity = new ByteArrayEntity(body);
            httpRequest.setEntity(entity);
        }
    }

    /**
     * Called before the request is executed using the underlying HttpClient.
     *
     * <p>Overwrite in subclasses to augment the request.</p>
     */
    protected void onPrepareRequest(HttpUriRequest request) throws IOException {
        // Nothing.
    }

    /**
     * The HttpPatch class does not exist in the Android framework, so this has been defined here.
     */
    public static final class HttpPatch extends HttpEntityEnclosingRequestBase {

        public final static String METHOD_NAME = "PATCH";

        public HttpPatch() {
            super();
        }

        public HttpPatch(final URI uri) {
            super();
            setURI(uri);
        }

        /**
         * @throws IllegalArgumentException if the uri is invalid.
         */
        public HttpPatch(final String uri) {
            super();
            setURI(URI.create(uri));
        }

        @Override
        public String getMethod() {
            return METHOD_NAME;
        }

    }
}
