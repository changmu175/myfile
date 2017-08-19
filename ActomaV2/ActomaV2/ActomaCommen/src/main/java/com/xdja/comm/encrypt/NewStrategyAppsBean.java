package com.xdja.comm.encrypt;

/**
 * Created by geyao on 2015/11/16.
 * 新策略-apps
 */
public class NewStrategyAppsBean {
    /**
     * 应用名称
     */
    private String appName;
    /**
     * 应用包名
     */
    private String packageName;
    /**
     * 操作类型 1-添加；2-修改；3-删除
     */
    private int action;
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
     * 应用增量策略信息
     */
    private String strategy;

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

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
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

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    @Override
    public String toString() {
        return "NewStrategyAppsBean{" +
                "appName='" + appName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", action='" + action + '\'' +
                ", description='" + description + '\'' +
                ", supportType='" + supportType + '\'' +
                ", supportVertion='" + supportVertion + '\'' +
                ", strategy='" + strategy + '\'' +
                '}';
    }
}
