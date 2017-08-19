package com.xdja.safeauth.request;

/**
 * Created by THZ on 2016/5/18.
 * 请求的数据结构体
 */
public class GetTicketBean {

    /**
     * 版本号
     */
    private String version;

    /**
     * 芯片id
     */
    private String cardId;

    /**
     * 证书sn
     */
    private String sn;

    /**
     * 时间
     */
    private String timestamp;

    /**
     * 挑战值索引
     */
    private String index;

    /**
     * 签名结果
     */
    private String signature;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
