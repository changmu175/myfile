package com.xdja.contact.task.account;

import android.os.SystemClock;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.xdja.comm.https.HttpsRequest;
import com.xdja.comm.https.HttpsRequstResult;
import com.xdja.comm.https.Property.HttpResultSate;
import com.xdja.contact.http.FriendHttpServiceHelper;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.http.response.account.ResponseActomaAccount;
import com.xdja.contact.http.wrap.HttpRequestWrap;
import com.xdja.contact.http.wrap.params.account.PullAccountInfoParam;
import com.xdja.contact.task.AbstractTaskContact;
import com.xdja.comm.contacttask.TaskManager;
import com.xdja.contact.util.BroadcastManager;
import com.xdja.contact.util.ContactUtils;
import com.xdja.comm.uitl.ObjectUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wanghao on 2016/5/20..
 * 进入联系人详情进行账户信息更新
 */
public class TaskPullAccountInfo extends AbstractTaskContact {

    /**
     * cacheAccountMap :
     * key-->帐号
     * value-->触发该账户信息更新时的时间
     */
    static final Map<String,Long> cacheAccountMap = new HashMap<String,Long>();

    private String account;

    private String identify;

    private String workID;//add by wal@xdja.com

    public TaskPullAccountInfo(String account,String identify){
        this.account = account;
        this.identify = identify;
    }
    //start:add by wal@xdja.com
    public TaskPullAccountInfo(String workID,String account,String identify){
        this.account = account;
        this.identify = identify;
        this.workID = workID;
    }
    //end:add by wal@xdja.com


    @Override
    public void template() {
        //super.template();
        if(filterExecute()) {
            setTaskTag(getClass().getSimpleName()+ SystemClock.elapsedRealtimeNanos());
            TaskManager.getInstance().putTask(this);
            execute();
        }
    }


    private boolean filterExecute(){
        if(ObjectUtil.mapIsEmpty(cacheAccountMap)){
            cacheAccountMap.put(account,System.currentTimeMillis());
            return true;
        }else{
            Long value = cacheAccountMap.get(account);
            if(ObjectUtil.objectIsEmpty(value)) {
                cacheAccountMap.put(account, System.currentTimeMillis());
                return true;
            }else{
                long now = System.currentTimeMillis();
                boolean bool = (now - value > 10*60*1000);
                //add by lwl start
                if(bool) {
                    cacheAccountMap.put(account,System.currentTimeMillis());
                }
                //add by lwl end
                return bool;
            }
        }
    }

    @Override
    protected HttpsRequstResult doInBackground(Void... params) {
        try {
            if(TextUtils.isEmpty(account) == false) {
                PullAccountInfoParam accountInfoParam = new PullAccountInfoParam(account, identify);
                HttpsRequstResult requstResult =  new HttpRequestWrap().synchronizedRequest(accountInfoParam);
                HttpsRequest.checkTicketError(requstResult, !isCancelled(),getTaskId());
                TaskManager.getInstance().removeTask(this);
                return requstResult;
            }
        }catch (Exception e){
            e.getStackTrace();
        }
        TaskManager.getInstance().removeTask(this);
        return null;
    }

    @Override
    protected void onPost(HttpsRequstResult result) {
        if(!ObjectUtil.objectIsEmpty(result) &&
                result.result == HttpResultSate.SUCCESS){
            try {
                if(!ObjectUtil.stringIsEmpty(result.body)){
                    ResponseActomaAccount responseActomaAccount = JSON.parseObject(result.body, ResponseActomaAccount.class);
                     /*[S]modify by tangsha@20161103 for 5661*/
                    ContactUtils.updateLocalAccountInfo(account,responseActomaAccount);
                    /*[E]modify by tangsha@20161103 for 5661*/
                    //start:modify for update actoma acount by wal@xdja.com
                    BroadcastManager.refreshCommonDetail(workID,account);//fix 1212 by wal@xdja.com
                    //end:modify for update actoma acount by wal@xdja.com
                }
            }catch (Exception e){
                LogUtil.getUtils().e("Actoma contact TaskPullAccountInfo onPost error");
            }
        }
        TaskManager.getInstance().removeTask(this);
    }

    @Override
    public String getTaskId() {
        return getTaskTag();
    }

    @Override
    public String getReason() {
        return null;
    }

}
