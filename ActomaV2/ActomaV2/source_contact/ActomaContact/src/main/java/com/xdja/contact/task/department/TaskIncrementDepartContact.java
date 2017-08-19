package com.xdja.contact.task.department;

import android.content.Intent;

import com.xdja.comm.https.ErrorCode.StatusCode;
import com.xdja.comm.https.HttpErrorBean;
import com.xdja.comm.server.ActomaController;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.R;
import com.xdja.contact.bean.Department;
import com.xdja.contact.bean.ErrorPush;
import com.xdja.contact.bean.Member;
import com.xdja.contact.bean.enumerate.ServiceErrorCode;
import com.xdja.contact.callback.OnBatchTaskListener;
import com.xdja.contact.http.proxy.DepartMemberHttpTask;
import com.xdja.contact.http.proxy.DepartmentHttpTask;
import com.xdja.contact.service.ErrorPushService;
import com.xdja.comm.contacttask.ITask;
import com.xdja.comm.contacttask.TaskManager;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.comm.uitl.RegisterActionUtil;

import java.util.List;

/**
 * Created by wanghao on 2015/12/25.
 * 增量更新集团通讯录
 */
public class TaskIncrementDepartContact implements ITask {

    protected final String DEFAULT_REASON = "网络请求未返回数据";

    protected HttpErrorBean httpErrorBean;

    @Override
    public void template() {
        new DepartmentHttpTask(new OnBatchTaskListener<List<Department>, HttpErrorBean>(){

            @Override
            public void onBatchTaskSuccess(List<Department> result) {
                new DepartMemberHttpTask(new OnBatchTaskListener<List<Member>, HttpErrorBean>(){

                    @Override
                    public void onBatchTaskSuccess(List<Member> result) {
                        deleteErrorPush();
                        TaskManager.getInstance().removeTask(TaskIncrementDepartContact.this);
                        ActomaController.getApp().sendBroadcast(new Intent().setAction(RegisterActionUtil.ACTION_DEPARTMENT_DOWNLOAD_SUCCESS));
                        LogUtil.getUtils().e("Actoma contact TaskIncrementDepartContact,template,onBatchTaskSuccess");
                    }

                    @Override
                    public void onBatchTaskFailed(HttpErrorBean result) {
                        if (ServiceErrorCode.isMatch(result.getErrCode()) || result.getStatus() <= StatusCode.NET_ERROR) {
                            setHttpErrorBean(result);
                            saveOrUpdateErrorPush();
                        }
                        TaskManager.getInstance().removeTask(TaskIncrementDepartContact.this);
                        LogUtil.getUtils().e("Actoma contact TaskIncrementDepartContact,template,onBatchTaskFailed");
                    }
                }).template();
            }

            @Override
            public void onBatchTaskFailed(HttpErrorBean result) {
                if (ServiceErrorCode.isMatch(result.getErrCode()) || result.getStatus() <= StatusCode.NET_ERROR) {
                    setHttpErrorBean(result);
                    saveOrUpdateErrorPush();
                }
            }
        }).template();
    }

    protected void deleteErrorPush(){
        ErrorPushService service = new ErrorPushService(ActomaController.getApp());
        ErrorPush errorPush = new ErrorPush();
        errorPush.setTransId(getTransId());
        service.delete(errorPush);
    }



    /**
     * 保存或者更新异常错误表
     * @return
     */
    protected boolean saveOrUpdateErrorPush(){
        ErrorPushService errorPushService = new ErrorPushService(ActomaController.getApp());
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

    public void setHttpErrorBean(HttpErrorBean httpErrorBean) {
        this.httpErrorBean = httpErrorBean;
    }

    public String getTransId() {
        return INCREMENT_DEPART_TASK;
    }

    public String getDefaultReason() {
        if(ObjectUtil.objectIsEmpty(httpErrorBean))return DEFAULT_REASON;
        return " result Code : " + httpErrorBean.getErrCode()  + " result Message :" + httpErrorBean.getMessage();
    }

    @Override
    public String getTaskId() {
        return INCREMENT_DEPART_TASK;
    }

    @Override
    public String getReason() {
        return String.format(ActomaController.getApp().getString(R.string.increment_error_depart), httpErrorBean.getMessage(), httpErrorBean.getStatus());
    }
}
