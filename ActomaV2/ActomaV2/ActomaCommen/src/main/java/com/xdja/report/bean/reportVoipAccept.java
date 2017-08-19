package com.xdja.report.bean;

/**
 * Created by gbc on 2016/12/1.
 */
public class reportVoipAccept {

    //帐户
    private String account;

    //设备号
    private String deviceid;

    //按下接听键的时间（毫秒）
    private long presstime;

    //通话接通的时间（毫秒）
    private long answertime;

    //通话的创建时间（毫秒）
    private long createtime;

    //caller
    private String caller;

    //callee
    private String callee;

    public String getCallee() {
        return callee;
    }

    public void setCallee(String callee) {
        this.callee = callee;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public long getAnswertime() {
        return answertime;
    }

    public void setAnswertime(long answertime) {
        this.answertime = answertime;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public long getPresstime() {
        return presstime;
    }

    public void setPresstime(long presstime) {
        this.presstime = presstime;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }
}
