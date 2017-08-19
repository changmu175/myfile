package com.xdja.contact.task;

import android.content.Context;

import com.xdja.comm.contacttask.ContactAsyncTask;
import com.xdja.comm.contacttask.TaskManager;
import com.xdja.comm.https.HttpErrorBean;
import com.xdja.comm.https.HttpsRequstResult;
import com.xdja.comm.contacttask.ITask;
import com.xdja.comm.server.ActomaController;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.bean.ErrorPush;
import com.xdja.contact.service.ErrorPushService;
import com.xdja.comm.uitl.ObjectUtil;

/**
 * Created by wanghao on 2015/12/22.
 *
 *
 */
public abstract class AbstractTaskContact extends ContactAsyncTask<Void,Void,HttpsRequstResult> implements ITask {

    protected final String DEFAULT_REASON = "网络请求未返回数据";

    protected Context context;

    protected HttpErrorBean httpErrorBean;

    public AbstractTaskContact(){
        this.context = ActomaController.getApp();
    }

    @Override
    protected void onPostExecute(HttpsRequstResult result) {
        super.onPostExecute(result);
        LogUtil.getUtils().i(getTaskId() + "推送请求下载业务数据线程执行完成");
        onPost(result);
        TaskManager.getInstance().removeTask(this);
    }
    //modify by lwl start for refresh
    @Override
    public void template() {
        if(TaskManager.getInstance().isIncludeTaskPool(this))
            return;
        TaskManager.getInstance().putTask(this);
        execute();
    }
    /**
     * @param refresh
     * 下拉刷新运行的操作 因为下拉刷新本身去重 且要停止一直的转圈 所有每次都执行
     */
    public void template(int refresh){
        TaskManager.getInstance().putTask(this);
        execute();
    }
    //modify by lwl end for refresh
    protected abstract void onPost(HttpsRequstResult result);
    /**
     * 保存或者更新异常错误表
     * 这里是否可以传递给ErrorPush一个接口呢?
     * @return
     */
    protected boolean saveOrUpdateErrorPush(){
        ErrorPushService errorPushService = new ErrorPushService(context);
        ErrorPush errorPush = new ErrorPush();
        errorPush.setTransId(getTransId());
        errorPush.setCreateTime(String.valueOf(System.currentTimeMillis()));
        if(ObjectUtil.stringIsEmpty(getReason())){
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


    public void setHttpErrorBean(HttpErrorBean httpErrorBean) {
        this.httpErrorBean = httpErrorBean;
    }

    protected String getTransId(){
        return getTaskId();
    }

    protected void deleteErrorPush(){
        ErrorPushService service = new ErrorPushService(context);
        ErrorPush errorPush = new ErrorPush();
        errorPush.setTransId(getTransId());
        service.delete(errorPush);
    }
}
