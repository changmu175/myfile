package com.xdja.comm.data;

/**
 * Created by xrj on 2015-12-23 14:06:50
 * 日志上传请求参数对象
 */
public class LogInfoBean {
    /**
     * 模块名称
     */
    private String appModule = "";

    /**
     * 网络类型
     * 0 无网络
     * 1 WIFI
     * 2 2G网
     * 3 3G网
     * 4 4G网
     */
    private int netType ;

    /**
     * 日志级别
     */
    private int level;

    /**
     * 日志内容
     */
    private String content = "";

    /**
     * 日志代码
     */
    private int logCode;

    /**
     * 日志产生时间
     */
    private long crashTime ;

    public String getAppModule() {
        return appModule;
    }

    public void setAppModule(String appModule) {
        this.appModule = appModule;
    }

    public int getNetType() {
        return netType;
    }

    public void setNetType(int netType) {
        this.netType = netType;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLogCode() {
        return logCode;
    }

    public void setLogCode(int logCode) {
        this.logCode = logCode;
    }

    public long getCrashTime() {
        return crashTime;
    }

    public void setCrashTime(long crashTime) {
        this.crashTime = crashTime;
    }
}

