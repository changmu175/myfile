package com.xdja.contact.task.configuration;

import com.alibaba.fastjson.JSON;
import com.xdja.comm.contacttask.TaskManager;
import com.xdja.comm.https.ErrorCode.StatusCode;
import com.xdja.comm.https.HttpsRequest;
import com.xdja.comm.https.HttpsRequstResult;
import com.xdja.comm.https.Property.HttpResultSate;
import com.xdja.contact.bean.enumerate.ServiceErrorCode;
import com.xdja.contact.exception.http.FriendHttpException;
import com.xdja.contact.http.ConfigurationHttpServiceHelper;
import com.xdja.contact.http.response.configuration.ResponseContactConfig;
import com.xdja.contact.task.AbstractTaskContact;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.contact.util.PreferenceUtils;
import com.xdja.dependence.uitls.LogUtil;

/**
 * Created by wanghao on 2016/5/16.
 * 获取联系人模块对应的数据配置信息
 */
public class TaskContactConfiguration extends AbstractTaskContact{


    @Override
    protected HttpsRequstResult doInBackground(Void... params) {
        try {
            HttpsRequstResult httpsRequstResult =  ConfigurationHttpServiceHelper.getContactConfiguration();
            HttpsRequest.checkTicketError(httpsRequstResult,!isCancelled(), getTaskId());
            TaskManager.getInstance().removeTask(this);
            return httpsRequstResult;
        } catch (FriendHttpException e) {
            LogUtil.getUtils().e("TaskContactConfiguration HttpsRequstResult exception ="+e.getMessage());
        }
        TaskManager.getInstance().removeTask(this);
        return null;
    }

    @Override
    protected void onPost(HttpsRequstResult result) {
        if(ObjectUtil.objectIsEmpty(result)){
            serviceException();
        }else if(result.result != HttpResultSate.SUCCESS){
            setHttpErrorBean(result.httpErrorBean);
            serviceException();
        }else{
            try{
                ResponseContactConfig contactConfig = JSON.parseObject(result.body, ResponseContactConfig.class);
                contactConfig.setContactConfiguration();
            }catch (Exception e){
                PreferenceUtils.setDefaultConfig();
            }

        }
    }



        //标记业务异常
    protected void serviceException(){
        if(ObjectUtil.objectIsEmpty(httpErrorBean)){
            saveOrUpdateErrorPush();return;
        }
        if (ServiceErrorCode.isMatch(httpErrorBean.getErrCode()) || httpErrorBean.getStatus() <= StatusCode.NET_ERROR) {
            setHttpErrorBean(httpErrorBean);
            saveOrUpdateErrorPush();
        }
    }

    @Override
    public String getTaskId() {
        return CONFIGURATION_TASK;
    }

    @Override
    public String getReason() {
        return "获取联系人好友、群组数据配置出错";
    }
}
