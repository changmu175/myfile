package com.xdja.comm.data;

/**
 * Created by geyao on 2015/7/25.
 * 应用市场-应用详情对象
 */
public class AppInfoBean {
    /**
     * 服务器端的appId
     */
    private String AppId;
    /**
     * 下载地址
     */
    private String downloadUrl;
    /**
     * 应用包名
     */
    private String packageName;
    /**
     * 版本名称
     */
    private String versionName;
    /**
     * 版本号
     */
    private String versionCode;
    /**
     * 状态(应用已安装 true,应用未安装 false)
     */
    private String state;
    /**
     * 是否有应用安装包(有 true,无 false)
     */
    private String isHaveApk;
    /**
     * 应用是否在下载当中(正在下载 1,下载完成 2,暂停下载 3,等待下载 4,已经安装 5)
     */
    private String isDownNow;
    /**
     * 若应用在下载当中 下载的进度 已下载多少字节
     */
    private String downSize;
    /**
     * 应用安装包名
     */
    private String fileName;
    /**
     * 应用总大小
     */
    private String appSize;
    /**
     * 已下载百分比
     */
    private String percentage;

    public AppInfoBean() {
    }

    public AppInfoBean(String appId, String downloadUrl, String packageName, String versionName, String versionCode, String state, String isHaveApk, String isDownNow, String downSize, String fileName, String appSize, String percentage) {
        AppId = appId;
        this.downloadUrl = downloadUrl;
        this.packageName = packageName;
        this.versionName = versionName;
        this.versionCode = versionCode;
        this.state = state;
        this.isHaveApk = isHaveApk;
        this.isDownNow = isDownNow;
        this.downSize = downSize;
        this.fileName = fileName;
        this.appSize = appSize;
        this.percentage = percentage;
    }

    public String getAppId() {
        return AppId;
    }

    public void setAppId(String appId) {
        AppId = appId;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getIsHaveApk() {
        return isHaveApk;
    }

    public void setIsHaveApk(String isHaveApk) {
        this.isHaveApk = isHaveApk;
    }

    public String getIsDownNow() {
        return isDownNow;
    }

    public void setIsDownNow(String isDownNow) {
        this.isDownNow = isDownNow;
    }

    public String getDownSize() {
        return downSize;
    }

    public void setDownSize(String downSize) {
        this.downSize = downSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getAppSize() {
        return appSize;
    }

    public void setAppSize(String appSize) {
        this.appSize = appSize;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    @Override
    public String toString() {
        return "AppInfoBean{" +
                "percentage='" + percentage + '\'' +
                ", appSize='" + appSize + '\'' +
                ", fileName='" + fileName + '\'' +
                ", downSize='" + downSize + '\'' +
                ", isDownNow='" + isDownNow + '\'' +
                ", isHaveApk='" + isHaveApk + '\'' +
                ", state='" + state + '\'' +
                ", versionCode='" + versionCode + '\'' +
                ", versionName='" + versionName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", AppId='" + AppId + '\'' +
                '}';
    }
}
