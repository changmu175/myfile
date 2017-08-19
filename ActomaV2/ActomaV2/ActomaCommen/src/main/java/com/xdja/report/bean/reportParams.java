package com.xdja.report.bean;

/**
 * Created by gbc on 2016/12/5.
 */
public class reportParams {
    /**
     * app name
     */
    private String appname;
    /**
     * 账号
     */
    private String user;

    public reportDataBean getContent() {
        return content;
    }

    public void setContent(reportDataBean content) {
        this.content = content;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    /**
     * 参数内容

     */
    private reportDataBean content;

}
