package com.xdja.imsdk.volley.request;

import android.content.Context;
import android.text.TextUtils;

import com.xdja.imsdk.volley.CustomRetryPolicy;
import com.xdja.imsdk.volley.Request;
import com.xdja.imsdk.volley.Request.Method;
import com.xdja.imsdk.volley.RequestQueue;
import com.xdja.imsdk.volley.Response;
import com.xdja.imsdk.volley.VolleyLog;
import com.xdja.imsdk.volley.error.AuthFailureError;
import com.xdja.imsdk.volley.error.VolleyError;
import com.xdja.imsdk.volley.stack.HttpClientStack;
import com.xdja.imsdk.volley.stack.HttpsStackUtil;
import com.xdja.imsdk.volley.toolbox.FileDownloader;
import com.xdja.imsdk.volley.toolbox.FileUploader;
import com.xdja.imsdk.volley.toolbox.HttpCallback;
import com.xdja.imsdk.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

/**
 * 请求管理类
 * Created by xdjaxa on 2016/5/11.
 */
public class RequestManager {

    /**
     * 最大超时时间 20 min
     */
    public static final int MAX_TIMEOUT_MS = 20 * 60 * 1000;

    /**
     * 每次请求超时时间
     */
    public static final int DEFAULT_TIMEOUT_MS = 20 * 1000;

    /**
     * 每次最大请求任务数量, 最大支持4个，目前支持2个，防止一个文件下载失败后，导致后面其他文件无法下载
     */
    public static final int FILE_DOWNLOAD_TASK_COUNT = 4;
    public static final int FILE_UPLOAD_TASK_COUNT = 2;

    private Context mContext;

    private RequestQueue mRequestQueue;

    private RequestQueue mUploadRequestQueue;

    private RequestQueue mDownloadRequestQueue;

    private FileDownloader mFileDownloader;

    private FileUploader mFileUploader;

    boolean isHttpsRequest = false;

    private static class RequestManagerInstance {
        private static final RequestManager mInstance = new RequestManager();
    }

    private RequestManager() {
        super();
    }

    public static RequestManager getInstance() {
        return RequestManagerInstance.mInstance;
    }

    /**
     * 网络请求模块初始化化
     *
     * @param context
     * @param isHttps
     * @param keyStore
     * @param pwd
     */
    public void init(Context context, boolean isHttps, int keyStore, String pwd) {
        mContext = context;
        isHttpsRequest = isHttps;

        //文本消息队列
        if (!isHttpsRequest) {
            mRequestQueue = Volley.newRequestQueue();
        } else {
            mRequestQueue = Volley.newRequestQueue(HttpsStackUtil.getHttpsStack(mContext, keyStore, pwd));
        }

        //文件上传队列
        mUploadRequestQueue = Volley.newRequestQueue(new HttpClientStack());
        mFileUploader = new FileUploader(mUploadRequestQueue, FILE_UPLOAD_TASK_COUNT);

        //文件下载队列
        mDownloadRequestQueue = Volley.newRequestQueue();
        mFileDownloader = new FileDownloader(mDownloadRequestQueue, FILE_DOWNLOAD_TASK_COUNT);
    }

    /**
     * 文件上传队列初始化
     */
    private void initUploadQueue() {
        if (mUploadRequestQueue == null) {
            mUploadRequestQueue = Volley.newRequestQueue(new HttpClientStack());
        }

        if (mFileUploader == null) {
            mFileUploader = new FileUploader(mUploadRequestQueue, FILE_UPLOAD_TASK_COUNT);
        }
    }

    /**
     * 文件下载队列初始化
     */
    private void initDownloadQueue() {
        if (mDownloadRequestQueue == null) {
            mDownloadRequestQueue = Volley.newRequestQueue();
        }

        if (mFileDownloader == null) {
            mFileDownloader = new FileDownloader(mDownloadRequestQueue, FILE_DOWNLOAD_TASK_COUNT);
        }
    }

    /**
     * GET请求，StringRequest
     *
     * @param url        请求URL地址
     * @param paramsMap  发送请求参数
     * @param httpResult 回调
     */
    public void get(String url, Map<String, String> paramsMap, final HttpCallback httpResult) {
        get(new RequestInfo(url, paramsMap), httpResult);
    }

    /**
     * GET请求，StringRequest
     *
     * @param requestInfo 请求信息
     * @param httpResult  回调
     */
    public void get(RequestInfo requestInfo, final HttpCallback httpResult) {
        sendRequest(Request.Method.GET, requestInfo, httpResult);
    }

    /**
     * POST请求, StringRequest
     *
     * @param url
     * @param paramsMap
     * @param httpResult
     */
    public void post(final String url, final Map<String, String> paramsMap, final HttpCallback httpResult) {
        post(new RequestInfo(url, paramsMap), httpResult);
    }

    /**
     * POST请求, StringRequest
     * post
     *
     * @param requestInfo
     * @param httpResult
     */
    public void post(RequestInfo requestInfo, final HttpCallback httpResult) {
        sendRequest(Request.Method.POST, requestInfo, httpResult);
    }

    /**
     * POST请求, JsonObjectRequest
     *
     * @param url
     * @param object
     * @param httpResult
     */
    public void post(final String url, JSONObject object, final HttpCallback httpResult) {
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = url;
        post(requestInfo, object, httpResult);
    }

    /**
     * 发送POST请求
     *
     * @param requestInfo
     * @param object
     * @param httpResult
     */
    public void post(RequestInfo requestInfo, JSONObject object, final HttpCallback httpResult) {
        sendJsonRequest(Method.POST, requestInfo, object, httpResult);
    }

    /**
     * @param url
     * @param object
     * @param httpResult
     */
    public void post(final String url, JSONArray object, final HttpCallback httpResult) {

    }

    /**
     * DELETE 请求
     *
     * @param requestInfo
     * @param httpResult
     */
    public void delete(RequestInfo requestInfo, final HttpCallback httpResult) {
        sendRequest(Request.Method.DELETE, requestInfo, httpResult);
    }

    /**
     * PUT 请求
     *
     * @param requestInfo
     * @param httpResult
     */
    public void put(RequestInfo requestInfo, final HttpCallback httpResult) {
        sendRequest(Request.Method.PUT, requestInfo, httpResult);
    }

    /**
     * 开始上传文件
     *
     * @param url
     * @param filePath
     * @param requestTag
     * @param callback
     */
    public void uploadFileStart(String url, String filePath, String requestTag,
                                HttpCallback callback) {
        if (mFileUploader == null) {
            initUploadQueue();
        }
        mFileUploader.add(url, filePath, requestTag, callback);
    }

    /**
     * 恢复文件上传
     *
     * @param tag
     */
    public void uploadFileResume(String tag) {
        if (mFileUploader == null) {
            initUploadQueue();
        }
        FileUploader.UploadController controller = mFileUploader.get(tag);
        if (controller != null) {
            controller.resume();
        } else {
            VolleyLog.d("controller is null.");
        }
    }

    /**
     * 暂停文件上传
     *
     * @param tag
     */
    public void uploadFilePause(String tag) {
        if (mFileUploader == null) {
            initUploadQueue();
        }
        FileUploader.UploadController controller = mFileUploader.get(tag);
        if (controller != null) {
            controller.pause();
        }
    }

    /**
     * 停止文件上传
     *
     * @param tag
     */
    public void uploadFileStop(String tag) {
        if (mFileUploader == null) {
            initUploadQueue();
        }
        FileUploader.UploadController controller = mFileUploader.get(tag);
        if (controller != null) {
            controller.stop();
        }
    }

    /**
     * 下载请求
     *
     * @param url
     * @param target
     * @param callback
     * @return
     */
    public void downloadFile(String url, String target, long fileSize, final HttpCallback callback) {
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = url;
        downloadFile(requestInfo, target, fileSize, callback);
    }

    public void downloadFile(final RequestInfo requestInfo, String target, long fileSize, final HttpCallback callback) {
        if (mFileDownloader == null) {
            initDownloadQueue();
        }

        FileDownloader.DownloadController controller = mFileDownloader.get(requestInfo.url);
        if (controller == null) {
            mFileDownloader.add(requestInfo.url, target, requestInfo.tag, fileSize,
                    requestInfo.isSupportRange, callback);
        } else {
            if (controller.isPause()) {
                controller.resume();
            }
        }

    }

    /**
     * 恢复文件下载，主要用于在文件暂停后恢复下载功能
     *
     * @param url 文件路径
     */
    public void downloadFileResume(String url, String target, long fileSize, final HttpCallback callback) {
        if (mFileDownloader == null) {
            initDownloadQueue();
        }
        FileDownloader.DownloadController controller = mFileDownloader.get(url);
        if (controller != null) {
            controller.resume();
        } else {
            downloadFile(url, target, fileSize, callback);
        }
    }

    /**
     * 暂停文件下载
     *
     * @param url 文件下载路径
     * @param tag 请求队列tag值
     */
    public void downloadFilePause(String url, String tag) {
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = url;
        downloadFilePause(requestInfo, tag);
    }

    /**
     * 暂停文件下载
     *
     * @param requestInfo 请求信息
     * @param tag         请求队列tag值
     */
    public void downloadFilePause(final RequestInfo requestInfo, String tag) {
        if (mFileDownloader == null) {
            initDownloadQueue();
        }
        FileDownloader.DownloadController controller = mFileDownloader.get(requestInfo.url);
        if (controller != null) {
            controller.pause(tag);
        }
    }

    /**
     * 停止文件下载
     *
     * @param url
     * @param target
     */
    public void downloadFileStop(String url, String target) {
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = url;
        downloadFileStop(requestInfo, target);
    }

    /**
     * 停止文件下载
     *
     * @param requestInfo
     * @param target
     */
    public void downloadFileStop(final RequestInfo requestInfo, String target) {
        if (mFileDownloader == null) {
            initDownloadQueue();
        }
        FileDownloader.DownloadController controller = mFileDownloader.get(requestInfo.url);
        if (controller != null) {
            controller.stop();
        }
    }

    /**
     * @param method
     * @param requestInfo
     * @param httpResult
     */
    private void sendRequest(final int method, final RequestInfo requestInfo, final HttpCallback httpResult) {
        if (httpResult != null) {
            httpResult.onStart();
        }
        if (requestInfo == null || TextUtils.isEmpty(requestInfo.url)) {
            if (httpResult != null) {
                httpResult.onStart();
                httpResult.onError(new VolleyError("url can not be empty!"));
            }
            return;
        }
        switch (method) {
            case Request.Method.GET:
                requestInfo.url = requestInfo.getFullUrl();
                break;

            case Request.Method.DELETE:
                requestInfo.url = requestInfo.getFullUrl();
                break;

            default:
                break;
        }
        final StringRequest request = new StringRequest(method, requestInfo.url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if (httpResult != null) {
                    httpResult.onResult(response);
                    httpResult.onFinish();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (httpResult != null) {
                    httpResult.onError(error);
                    httpResult.onFinish();
                }
            }
        }, new Response.LoadingListener() {

            @Override
            public void onLoading(long count, long current) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if (method == Request.Method.POST || method == Request.Method.PUT) {
                    VolleyLog.d((method == Request.Method.POST ? "post->%s" : "put->%s"), requestInfo.getUrl()
                            + ",params->" + requestInfo.getParams().toString());
                    return requestInfo.getParams();
                }
                return super.getParams();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return requestInfo.getHeaders();
            }
        };
        request.setTag(this);
        if (mRequestQueue == null) {
            return;
        }
        mRequestQueue.add(request);
    }

    /**
     * 发送JSON请求
     *
     * @param method
     * @param requestInfo
     * @param object
     * @param httpResult
     */
    public void sendJsonRequest(final int method, final RequestInfo requestInfo, final JSONObject object,
                                final HttpCallback httpResult) {

        if (httpResult != null) {
            httpResult.onStart();
        }

        if (TextUtils.isEmpty(requestInfo.url)) {
            if (httpResult != null) {
                httpResult.onStart();
                httpResult.onError(new VolleyError("url can not be empty!"));
            }
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(method, requestInfo.url, object,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        VolleyLog.d("send json request response:" + response.toString());
                        if (httpResult != null) {
                            httpResult.onResult(response.toString());
                            httpResult.onFinish();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("send json request error:" + (error == null ? " " : error.getMessage()));
                if (httpResult != null) {
                    httpResult.onError(error);
                }
            }
        }) {
            @Override
            public void cancel() {
                super.cancel();
                if (httpResult != null) {
                    httpResult.onCanceled();
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return requestInfo.getHeaders();
            }

            @Override
            public void onNetChange(int code, String message) {
                super.onNetChange(code, message);
                if (httpResult != null) {
                    httpResult.onNetChanged(code, message);
                }
            }
        };
        CustomRetryPolicy policy = null;
        if (requestInfo.needRetry) {
            policy = new CustomRetryPolicy(MAX_TIMEOUT_MS, DEFAULT_TIMEOUT_MS);
        } else {
            //默认值只请求一次,超时时间3秒
            policy = new CustomRetryPolicy();
        }
        request.setRetryPolicy(policy);
        request.setTag(requestInfo.tag);
        if (mRequestQueue == null) {
            return;
        }
        mRequestQueue.add(request);
    }

    /**
     * 获取文件fid请求
     *
     * @param fileName
     * @param httpResult
     */
    public void sendFidRequest(String url, String fileName, String requestTag,
                               final HttpCallback httpResult) {

        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(fileName)) {
            if (httpResult != null) {
                httpResult.onStart();
                httpResult.onError(new VolleyError("params error!"));
            }
            return;
        }

        if (httpResult != null) {
            httpResult.onStart();
        }

        UploadRequest request = new UploadRequest(Request.Method.POST, url,
                new Response.Listener() {

                    @Override
                    public void onResponse(Object response) {
                        if (httpResult != null) {
                            httpResult.onResult(response.toString());
                            httpResult.onFinish();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (httpResult != null) {
                    httpResult.onError(error);
                }
            }
        }, new Response.LoadingListener() {

            @Override
            public void onLoading(long count, long current) {
                //不需要处理
                /*if (httpResult != null) {
                    VolleyLog.d("onLoading: count:" + count + ", current:" + current );
                    httpResult.onLoading(count, current);
                    if (current >= count){
                        httpResult.onFinish();
                    }
                }*/
            }
        }) {
            @Override
            public void onNetChange(int code, String message) {
                super.onNetChange(code, message);
                if (httpResult != null) {
                    httpResult.onNetChanged(code, message);
                }
            }
        };

        CustomRetryPolicy policy = new CustomRetryPolicy(MAX_TIMEOUT_MS, DEFAULT_TIMEOUT_MS);
        request.setRetryPolicy(policy);
        request.setFileFid(fileName);
        request.setTag(requestTag);
        mUploadRequestQueue.add(request);
    }

    /**
     * 根据tag取消请求
     *
     * @param tag 请求tag标识
     */
    public void cancelRequest(String tag){
    	if (mRequestQueue != null){
    		mRequestQueue.cancelAll(tag);
    	}
    	
    	if (mUploadRequestQueue != null){
    		mUploadRequestQueue.cancelAll(tag);
    	}

        if (mDownloadRequestQueue != null) {
            mDownloadRequestQueue.cancelAll(tag);
        }

        if (mFileUploader != null){
            mFileUploader.cancel(tag);
        }

        if (mFileDownloader != null){
            mFileDownloader.cancel(tag);
        }
    }

    /**
     * 取消所有的请求加载，只有在完全退出时才可调用
     */
    public void cancelAll() {
        VolleyLog.e("exit...");
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll();
            mRequestQueue.stop();
            mRequestQueue = null;
        }

        if (mUploadRequestQueue != null) {
            mUploadRequestQueue.cancelAll();
            mUploadRequestQueue.stop();
            mUploadRequestQueue = null;
        }

        if (mDownloadRequestQueue != null) {
            mDownloadRequestQueue.cancelAll();
            mDownloadRequestQueue.stop();
            mDownloadRequestQueue = null;
        }

        if (mFileUploader != null) {
            mFileUploader.clearAll();
            mFileUploader = null;
        }

        if (mFileDownloader != null) {
            mFileDownloader.clearAll();
            mFileDownloader = null;
        }
        //[S]lll@xdja.com 2016-10-28 added. 退出安通时，关闭HttpClient连接，review by liming
        HttpClientStack.release();
        //[E]lll@xdja.com 2016-10-28 added. 退出安通时，关闭HttpClient连接，review by liming
    }
}
