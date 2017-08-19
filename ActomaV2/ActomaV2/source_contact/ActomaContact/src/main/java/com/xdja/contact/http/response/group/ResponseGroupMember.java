package com.xdja.contact.http.response.group;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 群组成员响应
 * Created by yangpeng on 2015/9/25.
 */
public class ResponseGroupMember implements Parcelable {


    private String account;

    //groupId wanghao添加 2016-01-25 和周玉杰讨论之后认为这里需要解析服务返回的groupId
    private String groupId;

    private String nickname;
    private String nicknamePy;
    private String nicknamePinyin;
    private String createTime;
    private String inviteAccount;
    private String status;

    private String updateSerial;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNicknamePy() {
        return nicknamePy;
    }

    public void setNicknamePy(String nicknamePy) {
        this.nicknamePy = nicknamePy;
    }

    public String getNicknamePinyin() {
        return nicknamePinyin;
    }

    public void setNicknamePinyin(String nicknamePinyin) {
        this.nicknamePinyin = nicknamePinyin;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdateSerial() {
        return updateSerial;
    }

    public void setUpdateSerial(String updateSerial) {
        this.updateSerial = updateSerial;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getInviteAccount() {
        return inviteAccount;
    }

    public void setInviteAccount(String inviteAccount) {
        this.inviteAccount = inviteAccount;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.account);
        dest.writeString(this.groupId);
        dest.writeString(this.nickname);
        dest.writeString(this.nicknamePy);
        dest.writeString(this.nicknamePinyin);
        dest.writeString(this.status);
        dest.writeString(this.updateSerial);
        dest.writeString(this.createTime);
        dest.writeString(this.inviteAccount);
    }

    public ResponseGroupMember() {
    }

    protected ResponseGroupMember(Parcel in) {
        this.account = in.readString();
        this.groupId = in.readString();
        this.nickname = in.readString();
        this.nicknamePy = in.readString();
        this.nicknamePinyin = in.readString();
        this.status = in.readString();
        this.updateSerial = in.readString();
        this.createTime = in.readString();
        this.inviteAccount = in.readString();
    }

    public static final Parcelable.Creator<ResponseGroupMember> CREATOR = new Parcelable.Creator<ResponseGroupMember>() {
        public ResponseGroupMember createFromParcel(Parcel source) {
            return new ResponseGroupMember(source);
        }

        public ResponseGroupMember[] newArray(int size) {
            return new ResponseGroupMember[size];
        }
    };
}

