package com.xdja.report.bean;

/**
 * Created by gbc on 2016/12/1.
 */
public class reportMsgBean {


    private String jsonrpc;
    private String id;
    private String method;
    private reportParams params;

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public reportParams getParams() {
        return params;
    }

    public void setParams(reportParams params) {
        this.params = params;
    }
}
