package com.xdja.imp.data.entity;

/**
 * <p>Summary:漫游信息设置请求对象</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.model</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/10</p>
 * <p>Time:18:32</p>
 */
public class RoamSetter {
//    account”:””,//账号
//            “cardId”:””,//硬件标示
//            “status”:””，//1-开启漫游，2-关闭漫游
//            “time”:””//漫游时长，当status=2时，该字段未0，否则为漫游时长，以天为单位，只能是整型
    /**
     * 账号
     */
    private String account;
    /**
     * 硬件标示
     */
    private String cardId;
    /**
     * 1-开启漫游，2-关闭漫游
     */
    private int status;
    /**
     * 漫游时长，当status=2时，该字段未0，否则为漫游时长，以天为单位，只能是整型
     */
    private int time;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "RoamSetter{" +
                "account='" + account + '\'' +
                ", cardId='" + cardId + '\'' +
                ", status=" + status +
                ", time=" + time +
                '}';
    }
}
