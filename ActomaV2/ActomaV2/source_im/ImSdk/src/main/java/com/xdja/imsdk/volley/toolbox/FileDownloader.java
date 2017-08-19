package com.xdja.imsdk.volley.toolbox;

import android.text.TextUtils;

import com.xdja.imsdk.volley.CustomRetryPolicy;
import com.xdja.imsdk.volley.RequestQueue;
import com.xdja.imsdk.volley.Response;
import com.xdja.imsdk.volley.VolleyLog;
import com.xdja.imsdk.volley.error.VolleyError;
import com.xdja.imsdk.volley.request.DownloadRequest;
import com.xdja.imsdk.volley.request.RequestManager;

import java.util.LinkedList;


/**
 * 文件下载器
 *
 * @author xdjaxa
 */
public class FileDownloader {

    private static final int STATUS_WAITING = 0;
    private static final int STATUS_DOWNLOADING = 1;
    private static final int STATUS_PAUSE = 2;
    private static final int STATUS_SUCCESS = 3;
    private static final int STATUS_DISCARD = 4;

    /**
     * RequestQueue for dispatching DownloadRequest.
     */
    private final RequestQueue mRequestQueue;

    /**
     * The parallel task count, recommend less than 4.
     */
    private final int mParallelTaskCount;

    private LinkedList<DownloadController> mTaskQueue;

    public FileDownloader(RequestQueue queue, int taskCount) {
        if (taskCount > queue.getThreadPoolSize()) {
            throw new IllegalArgumentException("parallelTaskCount[" + taskCount
                    + "] must less than threadPoolSize["
                    + queue.getThreadPoolSize() + "] of the RequestQueue.");
        }
        this.mRequestQueue = queue;
        this.mParallelTaskCount = taskCount;
        mTaskQueue = new LinkedList<>();
    }

    /**
     * Traverse the Task Queue, count the running task then deploy more if it can be.
     */
    private void schedule() {
        // make sure only one thread able manipulate the Task Queue.
        synchronized (mTaskQueue) {
            // counting ran task.
            int parallelTaskCount = 0;
            for (DownloadController controller : mTaskQueue) {
                if (controller.isDownloading()) parallelTaskCount++;
            }
            if (parallelTaskCount >= mParallelTaskCount) return;

            // try to deploy all Task if they're await.
            for (DownloadController controller : mTaskQueue) {
                if (controller.deploy() && ++parallelTaskCount == mParallelTaskCount) return;
            }
        }
    }

    /**
     * 任务是否已经添加进消息队列
     *
     * @param url 请求url
     * @param tag 请求tag
     */
    private boolean isTaskExist(String url, String tag) {
        for (FileDownloader.DownloadController ctrl : mTaskQueue) {
            if (TextUtils.equals(ctrl.mUrl, url) &&
                    TextUtils.equals(ctrl.mTag, tag)) {
                VolleyLog.d("task is already exist!");
                return true;
            }
        }
        return false;
    }

    public void add(String url, String storePath, String tag,
                    long fileSize, boolean isSupportRange, HttpCallback callback) {
        synchronized (mTaskQueue) {
            if (isTaskExist(url, tag)) {
                return;
            }
            DownloadController controller = new DownloadController(url, fileSize, storePath,
                    tag, isSupportRange, callback);
            mTaskQueue.add(controller);
            schedule();
        }
    }

    /**
     * Scanning the Task Queue, fetch a {@link DownloadController} who match the two parameters.
     *
     * @param url The url which download for.
     * @return The matched {@link DownloadController}.
     */
    public DownloadController get(String url) {
        synchronized (mTaskQueue) {
            VolleyLog.d("mTaskQueue.size:" + mTaskQueue.size() + ",url:" + url);
            for (DownloadController controller : mTaskQueue) {
                VolleyLog.d("DownloadController:" + controller.mUrl);
                if (controller.mUrl.equals(url)) {
                    return controller;
                }
            }
            return null;
        }
    }

    private DownloadController getByTag(String tag) {
        synchronized (mTaskQueue) {
            VolleyLog.d("mTaskQueue.size:" + mTaskQueue.size() + ",tag:" + tag);
            for (DownloadController controller : mTaskQueue) {
                VolleyLog.d("DownloadController:" + controller.mUrl);
                if (controller.mTag.equals(tag)) {
                    return controller;
                }
            }
            return null;
        }
    }

    private void remove(DownloadController controller) {
        synchronized (mTaskQueue) {
            VolleyLog.d("mTaskQueue.remove:" + mTaskQueue.size());
            mTaskQueue.remove(controller);
            schedule();
        }
    }

    /**
     * Clear all tasks, make the Task Queue empty.
     */
    public void clearAll() {
        // make sure only one thread can manipulate the Task Queue.
        synchronized (mTaskQueue) {
            while (!mTaskQueue.isEmpty()) {
                mTaskQueue.get(0).stop();
            }
        }
        mTaskQueue.clear();
    }

    public void cancel(String requestTag) {
        DownloadController controller = getByTag(requestTag);
        if (controller != null) {
            controller.mStatus = STATUS_DISCARD;
        }
    }

    /**
     * 文件下载控制器
     *
     * @author xdjaxa
     */
    public class DownloadController {

        private String mUrl;
        private long mFileSize;
        private String mStorePath;
        private String mTag;
        private boolean bSupportRange;

        private HttpCallback mCallback;
        private DownloadRequest mRequest;

        private int mStatus;

        private int mPercent;

        public DownloadController(String url, long fileSize, String storePath, String tag,
                                  boolean isSupportRange, HttpCallback callback) {
            this.mUrl = url;
            this.mFileSize = fileSize;
            this.mStorePath = storePath;
            this.mTag = tag;
            this.bSupportRange = isSupportRange;
            this.mCallback = callback;
        }

        private boolean deploy() {

            if (mStatus != STATUS_WAITING) {
                return false;
            }

            if (mRequest == null) {
                mRequest = new DownloadRequest(mUrl,
                    //请求响应返回结果
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            VolleyLog.d("download finished for:" + DownloadController.this.toString()
                                    + "\nResponse:" + response);
                            if (mCallback != null) {
                                mStatus = STATUS_SUCCESS;
                                mCallback.onResult(response);
                                mCallback.onFinish();
                            }
                            remove(DownloadController.this);
                        }
                    },
                    //请求错误回调
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.e("onErrorResponse:" + error.getMessage());

                            if (mCallback != null && mStatus != STATUS_PAUSE) {
                                VolleyLog.d("Error:" + DownloadController.this.toString());
                                mCallback.onError(error);
                                mStatus = STATUS_DISCARD;
                                remove(DownloadController.this);
                                mRequestQueue.cancelAll(mTag);
                            } else {
                                if (mCallback != null) {
                                    VolleyLog.d("Download Canceled for:" + DownloadController.this.toString());
                                    mCallback.onCanceled();
                                }
                            }
                        }
                    },
                    //请求进度加载
                    new Response.LoadingListener() {

                        @Override
                        public void onLoading(long count, long current) {
                            VolleyLog.d("Downloading process for " + mTag + ", fileSize:" + count
                                    + ", translateSize:" + current);
                            if (mCallback != null) {
                                if (mStatus == STATUS_DOWNLOADING && count > 0) {
                                    int percent = (int) (current * 100.0f / count);
                                    if (percent > mPercent || percent == 100) {
                                        mPercent = percent;
                                        mCallback.onLoading(count, current, percent);
                                    }
                                }
                            }
                        }
                    },
                    //请求取消回调
                    new Response.CanceledListener() {

                        @Override
                        public void onCanceled() {
                            VolleyLog.d("Download Canceled for:" + DownloadController.this.toString());
                            //防止多个地方调用cancel，回调给上层多次
                            if (mStatus == STATUS_PAUSE) { //暂停发送
                                if (mCallback != null) {
                                    mCallback.onCanceled();
                                }
                            } else { //删除正在发送的消息
                                mPercent = 0;
                                remove(DownloadController.this);
                            }
                        }
                    }) {
                    @Override
                    public void onNetChange(int code, String message) {
                        super.onNetChange(code, message);
                        if (mCallback != null) {
                            mCallback.onNetChanged(code, message);
                        }
                    }
                };
            }

            //通知开始
            if (mCallback != null) {
                mCallback.onStart();
            }
            mStatus = STATUS_DOWNLOADING;

            //重连策略
            CustomRetryPolicy retryPolicy = new CustomRetryPolicy(RequestManager.MAX_TIMEOUT_MS,
                    RequestManager.DEFAULT_TIMEOUT_MS);
            mRequest.setRetryPolicy(retryPolicy);
            mRequest.setTag(mTag);
            mRequest.setTarget(mStorePath, mFileSize, bSupportRange);
            mRequest.prepare();
            mRequestQueue.add(mRequest);
            return true;
        }

        public int getStatus() {
            return mStatus;
        }

        public boolean isDownloading() {
            return mStatus == STATUS_DOWNLOADING;
        }

        public boolean isPause() {
            return mStatus == STATUS_PAUSE;
        }

        /**
         * Pause this task when it status was DOWNLOADING|WAITING. In fact, we just marked the request should be cancel,
         * http request cannot stop immediately, we assume it will finish soon, thus we set the status as PAUSE,
         * let Task Queue deploy a new Request. That will cause parallel tasks growing beyond maximum task count,
         * but it doesn't matter, we expected that situation never stay longer.
         *
         * @return true if did the pause operation.
         */
        public boolean pause(String tag) {
            VolleyLog.d("Download pause for:" + this.toString());
            switch (mStatus) {
                case STATUS_DOWNLOADING://文件正在下载中
                    mStatus = STATUS_PAUSE;
                    mRequest.cancel();
                    mRequestQueue.cancelAll(tag);
                    break;
                case STATUS_WAITING:
                    mStatus = STATUS_PAUSE;
                    break;
                default:
                    return false;
            }
            return true;
        }

        /**
         * Resume this task when it status was PAUSE, we will turn the status as WAITING, then re-schedule the Task Queue,
         * if parallel counter take an idle place, this task will re-deploy instantly,
         * if not, the status will stay WAITING till idle occur.
         *
         * @return true if did the resume operation.
         */
        public boolean resume() {
            VolleyLog.d("Download resume for:" + this.toString());
            mStatus = STATUS_WAITING;
            bSupportRange = true;
            mRequest.resume();
            schedule();
            return true;
        }

        /**
         * We will discard this task from the Task Queue, if the status was DOWNLOADING,
         * we first cancel the Request, then remove task from the Task Queue,
         * also re-schedule the Task Queue at last.
         *
         * @return true if did the discard operation.
         */
        public boolean stop() {
            VolleyLog.d("DownloadController.discard...");
            if (mStatus == STATUS_DISCARD) return false;
            if (mStatus == STATUS_SUCCESS) return false;
            if (mStatus == STATUS_DOWNLOADING) mRequest.cancel();
            mStatus = STATUS_DISCARD;
            remove(this);
            return true;
        }

        @Override
        public String toString() {
            return "DownloadController{" +
                    "mUrl='" + mUrl + '\'' +
                    ", mFileSize=" + mFileSize +
                    ", mStorePath='" + mStorePath + '\'' +
                    ", mTag='" + mTag + '\'' +
                    ", mStatus=" + mStatus +
                    ", mPercent=" + mPercent +
                    '}';
        }
    }
}
