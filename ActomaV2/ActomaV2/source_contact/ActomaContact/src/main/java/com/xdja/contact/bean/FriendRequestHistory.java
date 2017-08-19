package com.xdja.contact.bean;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.xdja.contact.bean.enumerate.FriendHistoryState;
import com.xdja.contact.database.columns.TableFriendHistory;
import com.xdja.contact.util.ContactUtils;

/**
 * <pre>
 * Created by wanghao on 2015/7/8.
 * 好友请求历史
 * </pre>
 * <b>
 *    <li>自己发出的请求</li>
 *    <li>自己收到的请求</li>
 *    <li>双发已经是好友</li>
 * </b>
 *
 */
public class FriendRequestHistory implements BaseContact, Parcelable {

    public static final String UNREAD = "0";

    public static final String READED = "1";

    private String currentAccount = ContactUtils.getCurrentAccount();

    private ActomaAccount actomaAccount;

    private Avatar avatar;

    //添加方账号
    private String reqAccount;

    //被添加方账号
    private String recAccount;
    //显示账号
    private String showAccount;
    /**
     * 最近一次的请求验证信息
     */
    private String authInfo;
    /**
     * 添加时间
     */
    private String time;
    /**
     * 增量标示
     */
    private String updateSerial;
    /**
     * 已读未读 0： 未读 1 :已读
     * 在好友列表页 如果存在未读数据 显示红色提醒
     */
    private String isRead;

    /**
     * 请求状态  添加 接受 等待验证
     * 1:等待验证; 2:已添加; 4:接受
     */
    private FriendHistoryState requestState;

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public FriendRequestHistory(){}

    public FriendRequestHistory(Cursor cursor){
        setReqAccount(cursor.getString(cursor.getColumnIndex(TableFriendHistory.C_REQ_ACCOUNT)));
        setRecAccount(cursor.getString(cursor.getColumnIndex(TableFriendHistory.C_REC_ACCOUNT)));
        setShowAccount(cursor.getString(cursor.getColumnIndex(TableFriendHistory.SHOW_ACCOUNT)));
        setTime(cursor.getString(cursor.getColumnIndex(TableFriendHistory.CREATE_TIME)));
        setUpdateSerial(cursor.getString(cursor.getColumnIndex(TableFriendHistory.UPDATE_SERIAL)));
        setRequestState(FriendHistoryState.getState(cursor.getInt(cursor.getColumnIndex(TableFriendHistory.STATE))));
        setIsRead(cursor.getString(cursor.getColumnIndex(TableFriendHistory.IS_READ)));
        setAuthInfo(cursor.getString(cursor.getColumnIndex(TableFriendHistory.LAST_REQUEST_INFO)));
    }


    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(TableFriendHistory.C_REQ_ACCOUNT,getReqAccount());
        values.put(TableFriendHistory.C_REC_ACCOUNT,getRecAccount());
        values.put(TableFriendHistory.SHOW_ACCOUNT,getShowAccount());
        values.put(TableFriendHistory.CREATE_TIME,getTime());
        values.put(TableFriendHistory.UPDATE_SERIAL,getUpdateSerial());
        values.put(TableFriendHistory.STATE, requestState.getKey());
        values.put(TableFriendHistory.IS_READ,getIsRead());
        values.put(TableFriendHistory.LAST_REQUEST_INFO, getAuthInfo());
        return values;
    }


    public String getShowAccount() {
        if(currentAccount.equals(getRecAccount())){
            return getReqAccount();
        }
        return getRecAccount();
    }

    public void setShowAccount(String showAccount) {
        this.showAccount = showAccount;
    }


    public static class Builder{

        private String reqAccount;

        public Builder(String reqAccount){
            this.reqAccount = reqAccount;
        }

        public FriendRequestHistory acceptFriendRequestHistory(String authinfo){
            FriendRequestHistory requestHistory = new FriendRequestHistory();
            requestHistory.setRequestState(FriendHistoryState.ALREADY_FRIEND);
            //requestHistory.setTime(String.valueOf(System.currentTimeMillis()));
            requestHistory.setRecAccount(ContactUtils.getCurrentAccount());
            requestHistory.setReqAccount(reqAccount);
            requestHistory.setIsRead(READED);
            requestHistory.setUpdateSerial("0");
            requestHistory.setAuthInfo(authinfo);//add by lwl
            return requestHistory;
        }
    }



    public String getAuthInfo() {
        return authInfo;
    }

    public void setAuthInfo(String authInfo) {
        this.authInfo = authInfo;
    }


    public FriendHistoryState getRequestState() {
        return requestState;
    }

    public void setRequestState(FriendHistoryState requestState) {
        this.requestState = requestState;
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

    public ActomaAccount getActomaAccount() {
        return actomaAccount;
    }

    public void setActomaAccount(ActomaAccount actomaAccount) {
        this.actomaAccount = actomaAccount;
    }


    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.actomaAccount, 0);
        dest.writeString(this.reqAccount);
        dest.writeString(this.recAccount);
        dest.writeString(this.time);
        dest.writeString(this.updateSerial);
        dest.writeString(this.isRead);
        dest.writeString(this.authInfo);
        dest.writeInt(this.requestState == null ? -1 : this.requestState.ordinal());
    }

    protected FriendRequestHistory(Parcel in) {
        this.actomaAccount = in.readParcelable(ActomaAccount.class.getClassLoader());
        this.reqAccount = in.readString();
        this.recAccount = in.readString();
        this.time = in.readString();
        this.updateSerial = in.readString();
        this.isRead = in.readString();
        this.authInfo = in.readString();
        int tmpRequestState = in.readInt();
        this.requestState = tmpRequestState == -1 ? null : FriendHistoryState.values()[tmpRequestState];
    }

    public static final Parcelable.Creator<FriendRequestHistory> CREATOR = new Parcelable.Creator<FriendRequestHistory>() {
        public FriendRequestHistory createFromParcel(Parcel source) {
            return new FriendRequestHistory(source);
        }

        public FriendRequestHistory[] newArray(int size) {
            return new FriendRequestHistory[size];
        }
    };

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
}
