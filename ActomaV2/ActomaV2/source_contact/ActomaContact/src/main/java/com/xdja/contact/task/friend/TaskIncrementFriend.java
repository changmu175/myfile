package com.xdja.contact.task.friend;

import com.alibaba.fastjson.JSON;
import com.xdja.comm.contacttask.TaskManager;
import com.xdja.comm.https.ErrorCode.StatusCode;
import com.xdja.comm.https.HttpsRequest;
import com.xdja.comm.https.HttpsRequstResult;
import com.xdja.comm.https.Property.HttpResultSate;
import com.xdja.comm.server.ActomaController;
import com.xdja.contact.R;
import com.xdja.contact.bean.Friend;
import com.xdja.contact.bean.enumerate.ServiceErrorCode;
import com.xdja.contact.callback.IPullCallback;
import com.xdja.contact.convert.FriendConvert;
import com.xdja.contact.exception.ATJsonParseException;
import com.xdja.contact.exception.http.FriendHttpException;
import com.xdja.contact.http.FriendHttpServiceHelper;
import com.xdja.contact.http.response.friend.ResponseFriend;
import com.xdja.contact.service.FireEventUtils;
import com.xdja.contact.service.FriendService;
import com.xdja.contact.service.FriendServiceWrap;
import com.xdja.contact.task.AbstractTaskContact;
import com.xdja.contact.util.BroadcastManager;
import com.xdja.contact.util.EncryptManager;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.dependence.uitls.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wanghao on 2015/12/2.
 * 增量好友任务
 * 1 登录执行好友增量
 * 2 在好友列表下拉刷新好友增量
 * 3 收到推送好友增量
 */
public class TaskIncrementFriend extends AbstractTaskContact {

    private IPullCallback friendCallback;
    private boolean isNeedFriendEvent = false;
    private final String TASK_TAG_PRE = INCREMENT_FRIEND_TASK;

    public TaskIncrementFriend(){
        super();
        setTaskTag(TASK_TAG_PRE);
    }

    /***
     * 如果是推送分支处理的业务需要调用此构造
     * @param isNeedFriendEvent
     */
    public TaskIncrementFriend(boolean isNeedFriendEvent){
        super();
        this.isNeedFriendEvent=isNeedFriendEvent;
        setTaskTag(TASK_TAG_PRE);
    }

    public TaskIncrementFriend(IPullCallback friendCallback, String taskTagSuf){
        this();
        this.friendCallback = friendCallback;
        setTaskTag(TASK_TAG_PRE+taskTagSuf);
    }



    @Override
    protected HttpsRequstResult doInBackground(Void... params) {
	   //[S]modify by tangsha@20161101 for 5748
        HttpsRequstResult result = null;
        LogUtil.getUtils().d("TaskIncrementFriend doInBackground");
        try {
            result = FriendHttpServiceHelper.incrementalFriend();
            HttpsRequest.checkTicketError(result, !isCancelled(), getTaskId());
        } catch (FriendHttpException e) {
            LogUtil.getUtils().e("TaskIncrementFriend doInBackground exception:"+e.getMessage());
            result = null;
        }finally {
            processTaskResult(result);
            TaskManager.getInstance().removeTask(this);
            return result;
        }
	  //[E]modify by tangsha@20161101 for 5748
    }
    private boolean toFireEvent = false;
    List<String> newFriendAccountList=new ArrayList<String>();//modify by wal@xdja.com for 好友事件通知

    @Override
    protected void onPost(HttpsRequstResult result) {
        LogUtil.getUtils().d("TaskIncrementFriend onPost "+isCancelled());
        if(toFireEvent){
            FireEventUtils.pushFriendRequestedAcceptPush(newFriendAccountList);
        }
        if(!ObjectUtil.objectIsEmpty(friendCallback) && friendCallback.isSupportLoading()){
            friendCallback.stopRefreshLoading();
            //start:add by wal@xdja.com for 3991
            if(ObjectUtil.objectIsEmpty(result)) {
                friendCallback.onShowErrorToast(null);
            }else if (result.result != HttpResultSate.SUCCESS){
                if (ObjectUtil.objectIsEmpty(result.httpErrorBean)) {
                    friendCallback.onShowErrorToast(null);
                }else if (!ObjectUtil.objectIsEmpty(result.httpErrorBean) && result.httpErrorBean.getStatus() <= StatusCode.NET_ERROR){
                    friendCallback.onShowErrorToast(null);
                }
            }
            //end:add by wal@xdja.com for 3991
        }

    }

    private void processTaskResult(HttpsRequstResult result){
        if(ObjectUtil.objectIsEmpty(result) || isCancelled()){
            serviceException();
        }else if(result.result != HttpResultSate.SUCCESS){
            setHttpErrorBean(result.httpErrorBean);
            serviceException();
        }else{
            try {
                List<ResponseFriend> responseFriends = parseResponseBody(result.body);
                List<Friend> friendList = FriendConvert.extractFriend(responseFriends);
                if (!ObjectUtil.collectionIsEmpty(responseFriends)) {
                    EncryptManager.closeEncryptionChannel(FriendConvert.extractDeleteFriends(responseFriends));
                }
                //start:modify by wal@xdja.com for 好友事件通知
                if (isNeedFriendEvent){
                    Map<String,Friend> friendMap = new HashMap<String,Friend>();
                    List<Friend> localFriends = new FriendService().queryFriends();
                    if(!ObjectUtil.collectionIsEmpty(localFriends)) {
                        for (Friend localFriend : localFriends) {
                            friendMap.put(localFriend.getAccount(), localFriend);
                        }
                    }
                    if(ObjectUtil.mapIsEmpty(friendMap)){
                        for (Friend friend : friendList){
                            if (friend.getState().equals("0")){
                                newFriendAccountList.add(friend.getAccount());
                            }
                        }
                    }else{
                        for (Friend friend : friendList){
                            if (ObjectUtil.objectIsEmpty(friendMap.get(friend.getAccount()))&&friend.getState().equals("0")){
                                newFriendAccountList.add(friend.getAccount());
                            }
                        }
                    }
                    if (!newFriendAccountList.isEmpty()){
                        toFireEvent = true;
                    }
                }
                //end:modify by wal@xdja.com for 好友事件通知
                if(isCancelled() == false) {
                    FriendServiceWrap.updateLocalFriends(friendList);
                    FriendServiceWrap.updateLocalFriendHistory(friendList);
                    BroadcastManager.sendFriendsUpdateBroadcast(friendList);
                    BroadcastManager.refreshFriendList();
                    deleteErrorPush();
                }
            }catch (Exception e){
                serviceException();
            }
        }
    }


    /**
     * 解析服务端返回的好友增量数据
     * @param body
     * @return
     */
    private List<ResponseFriend> parseResponseBody(String body) throws ATJsonParseException{
        return JSON.parseArray(body, ResponseFriend.class);
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
        return getTaskTag();
    }

    @Override
    public String getReason() {
        if(ObjectUtil.objectIsEmpty(httpErrorBean))return getTaskId() + " : " + DEFAULT_REASON;
        return String.format(ActomaController.getApp().getString(R.string.increment_error_friend), httpErrorBean.getMessage(), httpErrorBean.getStatus());
    }
}
