package com.xdja.comm.encrypt;

/**
 * Created by geyao on 2015/11/16.
 * 应用策略更新请求参数对象
 */
public class NewStrategyRequestBean {
    /**
     * 协议版本号
     */
    private String version;
    /**
     * 设备芯片卡号
     */
    private String cardNo;
    /**
     * 手机型号
     */
    private String model;
    /**
     * 厂商信息
     */
    private String manufacturer;
    /**
     * 最后策略更新ID，第一次为0
     */
    private int lastStrategyId;
    /**
     * 批量条数
     */
    private int batchSize;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public int getLastStrategyId() {
        return lastStrategyId;
    }

    public void setLastStrategyId(int lastStrategyId) {
        this.lastStrategyId = lastStrategyId;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public String toString() {
        return "NewStrategyRequestBean{" +
                "batchSize=" + batchSize +
                ", version='" + version + '\'' +
                ", cardNo='" + cardNo + '\'' +
                ", model=" + model +
                ", manufacturer=" + manufacturer +
                ", lastStrategyId=" + lastStrategyId +
                '}';
    }
}
