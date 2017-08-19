package com.securevoip.presenter.adapter.calllog;

/**
 * Created by chenbing on 2015/5/21.
 */
public class CallLogDetail {
    private String number;
    private String name;
    private long date;
    private int accountId;
    private int status_code;
    private String status_text;
    private long duration;
    private int type;//电话呼入呼出类型
    /**
     * zjc 20150906
     * 新增一个字段，号码类型。和type不同的是，这个只有呼入和呼出
     * 通过SipCallSession的getRole()方法获得
     */
    private int numberType;
    private boolean isNew;
    private String nickName;
    private String nickNamePy;
    private String nickNamePyFull;
    private String avatarUri;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus_text() {
        return status_text;
    }

    public void setStatus_text(String status_text) {
        this.status_text = status_text;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setNewF(boolean flag) {
        isNew = flag;
    }

    public boolean getNewF() {
        return isNew;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNickNamePy() {
        return nickNamePy;
    }

    public void setNickNamePy(String nickNamePy) {
        this.nickNamePy = nickNamePy;
    }

    public String getNickNamePyFull() {
        return nickNamePyFull;
    }

    public void setNickNamePyFull(String nickNamePyFull) {
        this.nickNamePyFull = nickNamePyFull;
    }

    public String getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
    }

    public int getNumberType() {
        return numberType;
    }

    public void setNumberType(int numberType) {
        this.numberType = numberType;
    }

}
