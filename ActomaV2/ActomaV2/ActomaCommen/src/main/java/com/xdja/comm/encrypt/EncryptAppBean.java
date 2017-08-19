package com.xdja.comm.encrypt;

/**
 * Created by Lixiaolong on 2016/08/16.
 *
 */
public class EncryptAppBean {
    /**
     * 应用名称
     */
    private String appName;
    /**
     * 应用包名
     */
    private String packageName;
    /**
     * 描述
     */
    private String description;
    /**
     * 支持消息类型
     */
    private String supportType;
    /**
     * 支持应用版本
     */
    private String supportVertion;
    /**
     * 是否开启
     */
    private boolean isOpen;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSupportType() {
        return supportType;
    }

    public void setSupportType(String supportType) {
        this.supportType = supportType;
    }

    public String getSupportVertion() {
        return supportVertion;
    }

    public void setSupportVertion(String supportVertion) {
        this.supportVertion = supportVertion;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    @Override
    public String toString() {
        return "NewStrategyAppsBean{" +
                "appName='" + appName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", description='" + description + '\'' +
                ", supportType='" + supportType + '\'' +
                ", supportVertion='" + supportVertion + '\'' +
                ", isOpen='" + isOpen + '\'' +
                '}';
    }
}
