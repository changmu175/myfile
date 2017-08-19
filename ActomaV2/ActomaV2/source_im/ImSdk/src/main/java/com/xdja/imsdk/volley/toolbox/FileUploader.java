package com.xdja.imsdk.volley.toolbox;

import android.text.TextUtils;

import com.xdja.imsdk.volley.CustomRetryPolicy;
import com.xdja.imsdk.volley.Request;
import com.xdja.imsdk.volley.RequestQueue;
import com.xdja.imsdk.volley.Response;
import com.xdja.imsdk.volley.VolleyLog;
import com.xdja.imsdk.volley.error.VolleyError;
import com.xdja.imsdk.volley.request.RequestManager;
import com.xdja.imsdk.volley.request.UploadRequest;

import java.util.LinkedList;


/**
 * 文件发送器
 * @author xdjaxa
 *
 */
public class FileUploader {

	private static final int STATUS_WAITING = 0;
	private static final int STATUS_DOWNLOADING = 1;
	private static final int STATUS_PAUSE = 2;
	private static final int STATUS_SUCCESS = 3;
	private static final int STATUS_DISCARD = 4;
	
	/**
     * RequestQueue for dispatching DownloadRequest.
     */
    private final RequestQueue mUploadRequestQueue;
    
    /**
     * The parallel task count, recommend less than 3.
     */
    private final int mParallelTaskCount;

    private LinkedList<UploadController> mTaskQueue;
    
	public FileUploader(RequestQueue queue, int taskCount) {
		if (taskCount >= queue.getThreadPoolSize()) {
			throw new IllegalArgumentException("parallelTaskCount[" + taskCount
					+ "] must less than threadPoolSize["
					+ queue.getThreadPoolSize() + "] of the RequestQueue.");
		}
		
		this.mUploadRequestQueue = queue;
		this.mParallelTaskCount = taskCount;
		mTaskQueue = new LinkedList<UploadController>();
	}
	
	/**
     * Traverse the Task Queue, count the running task then deploy more if it can be.
     */
    private void schedule() {
        // make sure only one thread able manipulate the Task Queue.
        synchronized (mTaskQueue) {
            // counting ran task.
            int parallelTaskCount = 0;
            for (UploadController controller : mTaskQueue) {
                if (controller.isDownloading()) parallelTaskCount++;
            }
            if (parallelTaskCount >= mParallelTaskCount) return;

            // try to deploy all Task if they're await.
            for (UploadController controller : mTaskQueue) {
                if (controller.deploy() && ++parallelTaskCount == mParallelTaskCount) return;
            }
        }
    }

	/**
	 * 任务是否已经添加进消息队列
	 * @param url 请求url
	 * @param tag 请求tag
     */
	private boolean isTaskExist(String url, String tag) {
		for (UploadController ctrl : mTaskQueue) {
			if (TextUtils.equals(ctrl.mUrl, url) &&
					TextUtils.equals(ctrl.mTag, tag)) {
				VolleyLog.d("task is already exist!");
				return true;
			}
		}
		return false;
	}
	
	public void add(String url, String filePath, String requestTag,
								HttpCallback callback){
		synchronized (mTaskQueue) {
			//任务已经存在队列中，则不再添加
			if (isTaskExist(url, requestTag)) {
				return;
			}
			UploadController controller = new UploadController(url, filePath, requestTag, callback);
            mTaskQueue.add(controller);
			schedule();
		}
	}
	
	/**
     * Scanning the Task Queue, fetch a {@link UploadController} who match the two parameters.
     *
     * @param tag The request tag.
     * @return The matched {@link UploadController}.
     */
    public UploadController get(String tag) {
    	VolleyLog.d("UploadController.get() tag:" + tag);
        synchronized (mTaskQueue) {
        	VolleyLog.d("mTaskQueue.size:" + mTaskQueue.size());
            for (UploadController controller : mTaskQueue) {
                if (controller.mTag.equals(tag)) {
                	return controller;
                }
            }
        }
        return null;
    }

	public void remove(UploadController controller) {
		synchronized (mTaskQueue) {
			mTaskQueue.remove(controller);
			VolleyLog.d("remove for :" + controller.toString());
		}
		schedule();
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

	/**
	 * 取消请求，当任务已经添加，但是未添加进消息请求队列时，此时删除消息时，需要同步移除任务
	 * @param requestTag
     */
	public void cancel(String requestTag){
		UploadController controller = get(requestTag);
		if (controller != null){
			controller.mStatus = STATUS_DISCARD;
		}
	}
	
	/**
	 * 文件下载控制器
	 * @author xdjaxa
	 *
	 */
	public class UploadController{
		
		private String mUrl;
		private String mTag;
		private String mFilePath;

		private HttpCallback mCallback;
		private UploadRequest mRequest;

		private int mStatus;

		private int mPercent;

		public UploadController(String url, String filePath, String requestTag,
								HttpCallback callback) {
			this.mUrl = url;
			this.mTag = requestTag;
			this.mFilePath = filePath;
			this.mCallback = callback;
		}
		
		private boolean deploy(){

			if (mStatus != STATUS_WAITING) {
				return false;
			}
			mRequest = new UploadRequest(Request.Method.POST, mUrl,
					//请求响应返回结果
					new Response.Listener() {

						@Override
						public void onResponse(Object response) {
							VolleyLog.d("download finished for:" + FileUploader.UploadController.this.toString()
									+ "\nResponse:" + response);
							if (mCallback != null) {
								mStatus = STATUS_SUCCESS;
								mCallback.onFinish();
							}
							remove(FileUploader.UploadController.this);
						}
					},
					//请求错误回调
					new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							VolleyLog.d("onErrorResponse:" + (error == null ? "" : error.getMessage()));
							if (mCallback != null && mStatus != STATUS_PAUSE){
								VolleyLog.d("remove controller and callback onError. ");
								mCallback.onError(error);
								remove(FileUploader.UploadController.this);
							}
						}
					},
					//请求进度加载
					new Response.LoadingListener() {

						@Override
						public void onLoading(long count, long translate) {
							if (mCallback != null){
								if (!mRequest.isCanceled() && count > 0) {
									int percent = (int) (translate * 100.0f / count);
									if (percent > mPercent || percent == 100) {
										mPercent = percent;
										mCallback.onLoading(count, translate, percent);
									}
								}
							}
						}
					},
					//请求取消回调
					new Response.CanceledListener() {
						@Override
						public void onCanceled() {
							VolleyLog.d("Upload Canceled for:" +
									FileUploader.UploadController.this.toString());
							if (mStatus == STATUS_PAUSE) { //暂停发送
								if (mCallback != null) {
									mCallback.onCanceled();
								}
							} else { //删除正在发送的消息
								mPercent = 0;
								remove(FileUploader.UploadController.this);
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

			if (mCallback != null){
				mCallback.onStart();
			}
			mStatus = STATUS_DOWNLOADING;

	        CustomRetryPolicy policy = new CustomRetryPolicy(RequestManager.MAX_TIMEOUT_MS,
	        		RequestManager.DEFAULT_TIMEOUT_MS);
	        mRequest.setRetryPolicy(policy);
			mRequest.setUploadFile(mFilePath);
	        mRequest.setTag(mTag);
	        mUploadRequestQueue.add(mRequest);
			return true;
		}

		public int getStatus() {
            return mStatus;
        }

        public boolean isDownloading() {
            return mStatus == STATUS_DOWNLOADING;
        }

        /**
         * Pause this task when it status was DOWNLOADING|WAITING. In fact, we just marked the request should be cancel,
         * http request cannot stop immediately, we assume it will finish soon, thus we set the status as PAUSE,
         * let Task Queue deploy a new Request. That will cause parallel tasks growing beyond maximum task count,
         * but it doesn't matter, we expected that situation never stay longer.
         *
         * @return true if did the pause operation.
         */
        public boolean pause() {
        	VolleyLog.d("UploadController.pause...mStatus:" + mStatus);
            switch (mStatus) {
                case STATUS_DOWNLOADING:
					mStatus = STATUS_PAUSE;
                    mRequest.cancel();
					mUploadRequestQueue.cancelAll(mTag);
                case STATUS_WAITING:
                    mStatus = STATUS_PAUSE;
                    return true;
                default:
                    return false;
            }
        }

        /**
         * Resume this task when it status was PAUSE, we will turn the status as WAITING, then re-schedule the Task Queue,
         * if parallel counter take an idle place, this task will re-deploy instantly,
         * if not, the status will stay WAITING till idle occur.
         *
         * @return true if did the resume operation.
         */
        public boolean resume() {
        	VolleyLog.d("UploadController.resume...");
            if (mStatus == STATUS_PAUSE) {
                mStatus = STATUS_WAITING;
                mRequest.resume();
                mRequest.prepare();
                schedule();
                return true;
            }
            return false;
        }

        /**
         * We will discard this task from the Task Queue, if the status was DOWNLOADING,
         * we first cancel the Request, then remove task from the Task Queue,
         * also re-schedule the Task Queue at last.
         *
         * @return true if did the discard operation.
         */
        public boolean stop() {
        	VolleyLog.d("UploadController.discard...");
            if (mStatus == STATUS_DISCARD) return false;
            if (mStatus == STATUS_SUCCESS) return false;
            if (mStatus == STATUS_DOWNLOADING) mRequest.cancel();
			mStatus = STATUS_DISCARD;
            remove(this);
            return true;
        }

		@Override
		public String toString() {
			return "UploadController{" +
					"mUrl='" + mUrl + '\'' +
					", mTag='" + mTag + '\'' +
					", mFilePath='" + mFilePath + '\'' +
					", mStatus=" + mStatus +
					", mPercent=" + mPercent +
					'}';
		}
	}
}
