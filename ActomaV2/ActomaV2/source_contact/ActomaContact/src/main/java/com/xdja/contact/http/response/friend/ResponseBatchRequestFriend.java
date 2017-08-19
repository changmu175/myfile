package com.xdja.contact.http.response.friend;

import com.xdja.contact.bean.FriendRequestHistory;
import com.xdja.contact.bean.enumerate.FriendHistoryState;
import com.xdja.contact.util.ContactUtils;

/**
 * Created by wanghao on 2015/7/23.
 *
 * 批量获取请求好友请求接口
 */
public class ResponseBatchRequestFriend {

    //添加方账号
    private String reqAccount;

    //被添加方账号
    private String recAccount;
    /**
     * 验证信息
     */
    private String verification;
    /**
     * 请求时间
     */
    private String time;
    /**
     * 最后的更新标示
     */
    private String updateSerial;





    public FriendRequestHistory convert2FriendRequestHistory(){
        FriendRequestHistory history = new FriendRequestHistory();
        history.setReqAccount(getReqAccount());
        history.setRecAccount(getRecAccount());
        history.setTime(getTime());
        history.setUpdateSerial(getUpdateSerial());
        history.setAuthInfo(verification);
        history.setIsRead(history.UNREAD);
        if(ContactUtils.getCurrentAccount().equals(getReqAccount())){
            history.setRequestState(FriendHistoryState.WAIT_ACCEPT);
        }else{
            history.setRequestState(FriendHistoryState.ACCEPT);
        }
        return history;
    }


    public String getRecAccount() {
        return recAccount;
    }

    public void setRecAccount(String recAccount) {
        this.recAccount = recAccount;
    }

    public String getReqAccount() {
        return reqAccount;
    }

    public void setReqAccount(String reqAccount) {
        this.reqAccount = reqAccount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUpdateSerial() {
        return updateSerial;
    }

    public void setUpdateSerial(String updateSerial) {
        this.updateSerial = updateSerial;
    }

    public String getVerification() {
        return verification;
    }

    public void setVerification(String verification) {
        this.verification = verification;
    }
}
