package com.xdja.contact.http.engine;

import com.xdja.comm.https.HttpsRequest;
import com.xdja.comm.https.HttpsRequstResult;
import com.xdja.comm.https.Property.HttpResultSate;
import com.xdja.comm.server.ActomaController;
import com.xdja.contact.http.wrap.IHttpParams;
import com.xdja.comm.contacttask.ContactAsyncTask;
import com.xdja.comm.contacttask.ITask;
import com.xdja.comm.contacttask.TaskManager;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.dependence.uitls.LogUtil;


/**
 * 循环请求网络服务异步,适用于通讯录更新等操作,
 * 批量循环获取数据的请求方式直到服务器返回数据结束符停止
 * @author hkb
 * @since 2015年8月4日14:02:24
 */
public abstract class HttpTask extends ContactAsyncTask<IHttpParams, Object, Result> implements ITask {

    private OnOperateListener asyncTaskHelper; // 异步协作接口，用于获取待发送数据，处理返回的结果

    public HttpTask() {
        this.asyncTaskHelper = getOperateListener();

    }


    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        if (result.isError()) {
            asyncTaskHelper.onTaskFailed(result);
        }else{
            asyncTaskHelper.onTaskSuccess(result);
        }
        TaskManager.getInstance().removeTask(this);
    }

    protected Result doInBackground(IHttpParams... params) {
        Result result = new Result();
        try {
            boolean isNext = true;
            RequestTask requestTask = new RequestTask(ActomaController.getApp(), params[0]);
            for(int i = 0 ; isNext ; i++){
                HttpsRequstResult subResult = requestTask.execute();
                if (isHttpError(subResult)) {
                    result.setResult(HttpResultSate.FAIL);
                    result.setHttpErrorBean(subResult.httpErrorBean);
                    HttpsRequest.checkTicketError(subResult,!isCancelled(),getTaskId());
                    TaskManager.getInstance().removeTask(this);
                    return result;
                } else {
                    IHttpParams nextParams = asyncTaskHelper.isNeedNext(i, subResult.body);
                    if(!ObjectUtil.objectIsEmpty(nextParams)) {
                        requestTask = new RequestTask(ActomaController.getApp(), nextParams);
                    }else{
                        isNext = false;
                    }
                }
            }
        }catch (Exception e){
            LogUtil.getUtils().e("HttpTask doInBackground Exception "+e.getMessage());
        }
        TaskManager.getInstance().removeTask(this);
        return result;
    }

    private boolean isHttpError(HttpsRequstResult result) {
        return result.result == HttpResultSate.FAIL;
    }

    protected abstract OnOperateListener getOperateListener();

}
