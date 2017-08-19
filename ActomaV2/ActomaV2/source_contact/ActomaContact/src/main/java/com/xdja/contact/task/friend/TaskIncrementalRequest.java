package com.xdja.contact.task.friend;

import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.xdja.comm.contacttask.TaskManager;
import com.xdja.comm.event.BusProvider;
import com.xdja.comm.event.UpdateContactTabTipsEvent;
import com.xdja.comm.https.ErrorCode.StatusCode;
import com.xdja.comm.https.HttpsRequest;
import com.xdja.comm.https.HttpsRequstResult;
import com.xdja.comm.https.Property.HttpResultSate;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.server.PreferencesServer;
import com.xdja.contact.R;
import com.xdja.contact.bean.ActomaAccount;
import com.xdja.contact.bean.AuthInfo;
import com.xdja.contact.bean.Avatar;
import com.xdja.contact.bean.FriendRequestHistory;
import com.xdja.contact.bean.enumerate.ServiceErrorCode;
import com.xdja.contact.exception.http.FriendHttpException;
import com.xdja.contact.http.FriendHttpServiceHelper;
import com.xdja.contact.http.response.account.ResponseActomaAccount;
import com.xdja.contact.http.response.friend.ResponseBatchRequestFriend;
import com.xdja.contact.service.AvaterService;
import com.xdja.contact.service.FriendRequestServiceWrap;
import com.xdja.contact.task.AbstractTaskContact;
import com.xdja.contact.util.BroadcastManager;
import com.xdja.contact.util.ContactUtils;
import com.xdja.contact.util.NotificationUtil;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.comm.uitl.RegisterActionUtil;
import com.xdja.dependence.uitls.LogUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wanghao on 2015/12/24.
 * 增量获取好友请求
 */
public class TaskIncrementalRequest extends AbstractTaskContact {

    //使用map过滤两个相同账号之间的请求
    private Map<String,FriendRequestHistory> historyMap = new HashMap<String,FriendRequestHistory>();

    private List<AuthInfo> authInfos = new ArrayList<>();

    private HttpsRequstResult responseResult;

    private boolean isLoginOut = false;

    private String ticket = "";

    private boolean isPushService = false;
    private ArrayList<String>  requestAccount=new ArrayList<>();

    private final String TASK_TAG_PRE = PUSH_REQUEST;
    /**
     * 增量好友请求数据
     */
    public TaskIncrementalRequest() {
        initData();
        setTaskTag(TASK_TAG_PRE);
    }

    public TaskIncrementalRequest(String taskTagSuf) {
        initData();
        setTaskTag(TASK_TAG_PRE+taskTagSuf);
    }

    private void initData(){
        context = ActomaController.getApp();
        isLoginOut = PreferencesServer.getWrapper(context).gPrefBooleanValue("logout", true);
        ticket = PreferencesServer.getWrapper(context).gPrefStringValue("ticket");
    }

    /***
     * 如果是推送分支处理的业务需要调用此构造
     * @param isPushService
     */
    public TaskIncrementalRequest(boolean isPushService) {
        this();
        this.isPushService = isPushService;
    }
    //modify by lwl start for refresh
    @Override
    public void template() {
        if(isPushService){
            if(isLoginOut && ObjectUtil.stringIsEmpty(ticket)){
                NotificationUtil.showTokenExpiredNotification();
            }
        }
        super.template();
    }
    @Override
    public void template(int refresh) {
        if(isPushService){
            if(isLoginOut && ObjectUtil.stringIsEmpty(ticket)){
                NotificationUtil.showTokenExpiredNotification();
            }
        }
        super.template(refresh);
    }
    //modify by lwl end for refresh
    @Override
    protected HttpsRequstResult doInBackground(Void... params) {
        try {
            responseResult = FriendHttpServiceHelper.incrementalRequest();
            HttpsRequest.checkTicketError(responseResult, !isCancelled(),getTaskId());
        } catch (FriendHttpException e) {
            LogUtil.getUtils().e("TaskIncrementalRequest doInBackground exception1="+e.getMessage());
        }
        if(ObjectUtil.objectIsEmpty(responseResult) == false) {
            Collection<String> accounts = extractAccounts();
            try {
                if (isCancelled() == false) {
                    responseResult = FriendHttpServiceHelper.bulkDownloadAccounts(accounts);
                    HttpsRequest.checkTicketError(responseResult, !isCancelled(), getTaskId());
                }
            } catch (FriendHttpException e) {
                LogUtil.getUtils().e("TaskIncrementalRequest doInBackground exception2=" + e.getMessage());
            }
            //[S]modify by tangsha@20161101 for 5748
            processResult(responseResult);
            //[E]modify by tangsha@20161101 for 5748
        }
        TaskManager.getInstance().removeTask(this);
        return responseResult;
    }

    /**
     * 根据返回的好友历史请求数据提取账号
     * @return
     */
    private Collection<String> extractAccounts(){
        if(responseResult.result != HttpResultSate.SUCCESS)return null;
        List<ResponseBatchRequestFriend> responses = JSON.parseArray(responseResult.body, ResponseBatchRequestFriend.class);
        if(ObjectUtil.collectionIsEmpty(responses))return null;
        Map<String,String> accountMap = new HashMap<>();
        String currentAccount = ContactUtils.getCurrentAccount();
        for (ResponseBatchRequestFriend response : responses) {
            accountMap.put(response.getReqAccount(), response.getReqAccount());
            if(currentAccount.equals(response.getRecAccount())){
                accountMap.put(response.getReqAccount(), response.getReqAccount());
                authInfos.add(new AuthInfo(response.getReqAccount(), response.getTime(), response.getVerification()));
            }else{
                accountMap.put(response.getRecAccount(), response.getRecAccount());
                authInfos.add(new AuthInfo(response.getRecAccount(), response.getTime(), response.getVerification()));
            }
            //这里如果需要排序请注意修正
            //modify by lwl start 2571
            FriendRequestHistory friendHistory=response.convert2FriendRequestHistory();
            historyMap.put(response.getReqAccount()+response.getRecAccount(), friendHistory);
            if(friendHistory.getRecAccount().equals(currentAccount)){
                requestAccount.add(friendHistory.getReqAccount());
            }
            //modify by lwl end 2571

        }
        return accountMap.values();
    }


    @Override
    protected void onPost(HttpsRequstResult result) {
        if(postEvent){
            //start:add by wangalei for 996
            BusProvider.getMainProvider().post(new UpdateContactTabTipsEvent());
            //end:add by wangalei for 996
        }
        if(needNoti){
            NotificationUtil.showNotification(notiTitle, contactIcon);
        }
    }

    private String notiTitle = "";
    private Avatar contactIcon = null;
    private boolean needNoti = false;
    private boolean postEvent = false;
    private void processResult(HttpsRequstResult result){
        if(ObjectUtil.objectIsEmpty(result) || isCancelled()){
            serviceException();
        }else if(result.result != HttpResultSate.SUCCESS){
            setHttpErrorBean(result.httpErrorBean);
            serviceException();
        }else{
            try {
                List<ResponseActomaAccount> responseAccounts = JSON.parseArray(responseResult.body,ResponseActomaAccount.class);
                if(ObjectUtil.collectionIsEmpty(responseAccounts) || isCancelled())return ;
                FriendRequestServiceWrap.updateAccounts(responseAccounts);
                FriendRequestServiceWrap.updateLocalAutoInfos(authInfos);
                FriendRequestServiceWrap.updateLocalRequestHistory(new ArrayList<FriendRequestHistory>(historyMap.values()));
                postEvent = true;
                if(isPushService){
                    notiTitle = extractNotificationTitle(responseAccounts);
                    //Start:add by wal@xdja.com for 4219
//                    ActomaAccount actomaAccount = responseAccounts.get(responseAccounts.size() - 1);
                    ActomaAccount actomaAccount = extractNotificationIcon(responseAccounts);
                    if (!ObjectUtil.objectIsEmpty(actomaAccount)){
                        AvaterService avaterService = new AvaterService();
                        contactIcon= avaterService.queryByAccount(actomaAccount.getAccount());
                    }
                    //End:add by wal@xdja.com for 4219
                    needNoti = true;
                    BroadcastManager.refreshFriendRequestList();
                }
                ActomaController.getApp().sendBroadcast(new Intent().setAction(RegisterActionUtil.ACTION_REQUEST_DOWNLOAD_SUCCESS));
                deleteErrorPush();
            }catch (Exception e){
                serviceException();
            }
        }
    }
    //start:add by wangalei for 996
     //public static class  updateContactTabTipsEvent{}
    //end:add by wangalei for 996
    //标记业务异常
    protected void serviceException(){
        if(ObjectUtil.objectIsEmpty(httpErrorBean)){
            saveOrUpdateErrorPush();
            return;
        }
        if(isPushService
                && httpErrorBean.getErrCode().equals(ServiceErrorCode.NOT_AUTHORIZED.getCode())
                && !isLoginOut
                && !ObjectUtil.stringIsEmpty(ticket)){
            NotificationUtil.showTokenExpiredNotification();
            return;
        }
        if (ServiceErrorCode.isMatch(httpErrorBean.getErrCode()) || httpErrorBean.getStatus() <= StatusCode.NET_ERROR) {
            setHttpErrorBean(httpErrorBean);
            saveOrUpdateErrorPush();
        }
    }

    //Start:add by wal@xdja.com for 4219
    private ActomaAccount extractNotificationIcon(List<ResponseActomaAccount> actomaAccounts){
        int length = actomaAccounts.size();
        for (int i = 0; i < length; i++) {
            ActomaAccount actomaAccount = actomaAccounts.get(i);
            if (!requestAccount.contains(actomaAccount.getAccount())) {
                continue;
            }
            return actomaAccount;
        }
        return null;
    }
    //End:add by wal@xdja.com for 4219

    private String extractNotificationTitle(List<ResponseActomaAccount> actomaAccounts){
        StringBuffer title = new StringBuffer();
        String currentAccount = ContactUtils.getCurrentAccount();//add by wal@xdja.com for 985
        int length = actomaAccounts.size();
        for (int i = 0; i < length; i++) {
            ActomaAccount actomaAccount = actomaAccounts.get(i);
            //modify by lwl start 2571
            //start:add by wal@xdja.com for 985
//            if(currentAccount.equals(actomaAccount.getAccount())){
//                continue;
//            }
            //end:add by wal@xdja.com for 985
            if(!requestAccount.contains(actomaAccount.getAccount())){
                continue ;
            }
            //modify by lwl end 2571
            String nickName = actomaAccount.getNickname();
            if (ObjectUtil.objectIsEmpty(nickName)) {
                String alias = actomaAccount.getAlias();
                if(ObjectUtil.objectIsEmpty(alias)){
                    title.append(actomaAccount.getAccount());
                }else{
                    title.append(alias);
                }
            } else {
                title.append(nickName);
            }
            if (i == 2) {
                break;
            }
            title.append(",");
        }
        return title.toString().substring(0, title.length() - 1);
    }


    @Override
    public String getTaskId() {
        return getTaskTag();
    }

    @Override
    public String getReason() {
        if(ObjectUtil.objectIsEmpty(httpErrorBean))return getTaskId() + " : " + DEFAULT_REASON;
        return String.format(ActomaController.getApp().getString(R.string.increment_error_request_friend), httpErrorBean.getMessage(), httpErrorBean.getStatus());
    }

}
