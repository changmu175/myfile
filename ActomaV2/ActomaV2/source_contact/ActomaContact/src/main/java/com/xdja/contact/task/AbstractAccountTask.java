package com.xdja.contact.task;

import android.content.Context;
import android.os.SystemClock;

import com.xdja.comm.contacttask.ContactAsyncTask;
import com.xdja.comm.contacttask.TaskManager;
import com.xdja.comm.https.HttpErrorBean;
import com.xdja.comm.https.HttpsRequest;
import com.xdja.comm.https.HttpsRequstResult;
import com.xdja.comm.contacttask.ITask;
import com.xdja.comm.https.Property.HttpResultSate;
import com.xdja.comm.server.ActomaController;
import com.xdja.contact.bean.ErrorPush;
import com.xdja.contact.http.engine.OnOperateListener;
import com.xdja.contact.http.engine.RequestTask;
import com.xdja.contact.http.engine.Result;
import com.xdja.contact.http.response.account.ResponseActomaAccount;
import com.xdja.contact.http.wrap.AbstractHttpParams;
import com.xdja.contact.http.wrap.IHttpParams;
import com.xdja.contact.http.wrap.params.account.AccountIncrementalParam;
import com.xdja.contact.service.ErrorPushService;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.contact.util.PreferenceUtils;
import com.xdja.dependence.uitls.LogUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wanghao on 2015/12/24.
 *
 */
public abstract class AbstractAccountTask extends ContactAsyncTask<Void,Void,Result> implements ITask {

    protected final String DEFAULT_REASON = "网络请求未返回数据";

    private AbstractHttpParams abstractHttpParams;

    private OnOperateListener asyncTaskHelper;

    protected String batchSize = "100";

    protected String lastUpdateId;

    protected Context context;

    protected HttpErrorBean httpErrorBean;

    private List<ResponseActomaAccount> serverAccounts ;


    public AbstractAccountTask(){
        this.context = ActomaController.getApp();
        this.serverAccounts = new ArrayList<>();
        this.lastUpdateId = PreferenceUtils.getLastAccountUpdateId(context);
        this.abstractHttpParams = new AccountIncrementalParam(lastUpdateId, batchSize);
    }

    //modify by lwl start for refresh
    /**
     * 所有非下拉刷新执行的操作 如果异步任务在运行 则不再重复运行
     */
    public void template(){
        if(TaskManager.getInstance().isIncludeTaskPool(this))
            return;
        TaskManager.getInstance().putTask(this);
        asyncTaskHelper = buildCallBack(serverAccounts);
        execute();
    }

    /**
     * @param refresh
     * 下拉刷新运行的操作 因为下拉刷新本身去重 且要停止一直的转圈 所有每次都执行
     */
    public void template(int refresh){
        TaskManager.getInstance().putTask(this);
        asyncTaskHelper = buildCallBack(serverAccounts);
        execute();
    }
    //modify by lwl end for refresh

    @Override
    protected Result doInBackground(Void... params) {
        Result result = new Result();
        try {
            boolean isNext = true;
            RequestTask requestTask = new RequestTask(context, abstractHttpParams);
            for(int i = 0 ; isNext ; i++){
                boolean isCancel = isCancelled();
                LogUtil.getUtils().d("AbstractAccountTask doInBackground------ isCancel "+isCancel);
                if(isCancelled() == false) {
                    HttpsRequstResult subResult = requestTask.execute();
                    if (isHttpError(subResult)) {
                        result.setResult(HttpResultSate.FAIL);
                        result.setHttpErrorBean(subResult.httpErrorBean);
                        HttpsRequest.checkTicketError(subResult,!isCancelled(),getTaskId());
                        TaskManager.getInstance().removeTask(this);
                        return result;
                    } else {
                        IHttpParams nextParams = asyncTaskHelper.isNeedNext(i, subResult.body);
                        isNext = nextParams != null;
                        requestTask = new RequestTask(context, nextParams);
                    }
                }
            }
        }catch (Exception e){
            LogUtil.getUtils().e("AbstractAccountTask doInBackground Exception "+e.getMessage());
        }
        TaskManager.getInstance().removeTask(this);
        return result;
    }

    protected void onPostExecute(Result result) {
        LogUtil.getUtils().d("AbstractAccountTask onPostExecute------");
        super.onPostExecute(result);
        if (result.isError()) {
            asyncTaskHelper.onTaskFailed(result);
        }else{
            asyncTaskHelper.onTaskSuccess(result);
        }
        TaskManager.getInstance().removeTask(this);
    }

    private boolean isHttpError(HttpsRequstResult result) {
        return result.result == HttpResultSate.FAIL;
    }

    protected abstract OnOperateListener buildCallBack(List<ResponseActomaAccount> serverAccounts);

    public void setHttpErrorBean(HttpErrorBean httpErrorBean) {
        this.httpErrorBean = httpErrorBean;
    }

    protected boolean saveOrUpdateErrorPush(){
        ErrorPushService errorPushService = new ErrorPushService(context);
        ErrorPush errorPush = new ErrorPush();
        errorPush.setTransId(getTransId());
        errorPush.setCreateTime(String.valueOf(System.currentTimeMillis()));
        if(ObjectUtil.objectIsEmpty(httpErrorBean)){
            errorPush.setReason(getDefaultReason());
        }else {
            errorPush.setReason(getReason());
        }
        errorPush.setUpdateTime(String.valueOf(System.currentTimeMillis()));
        return errorPushService.saveOrUpdate(errorPush);
    }

    public String getDefaultReason() {
        if(ObjectUtil.objectIsEmpty(httpErrorBean))return DEFAULT_REASON;
        return " result Code : " + httpErrorBean.getErrCode()  + " result Message :" + httpErrorBean.getMessage();
    }

    protected void deleteErrorPush(){
        ErrorPushService service = new ErrorPushService(context);
        ErrorPush errorPush = new ErrorPush();
        errorPush.setTransId(getTransId());
        service.delete(errorPush);
    }


    protected String getTransId(){
        return getTaskId();
    }
}
