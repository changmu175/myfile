package com.xdja.imsdk.http.bean;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：  IM服务器请求参数                              <br>
 * 创建时间：2016/11/27 下午5:16                          <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public class ImRequestBean <T> {
    private T params;
    private String jsonrpc;
    private String id;
    private String method;
    public T getParams() {
        return params;
    }
    public void setParams(T params) {
        this.params = params;
    }
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

    @Override
    public String toString() {
        return "ImRequestBean{" +
                "params=" + params +
                ", jsonrpc='" + jsonrpc + '\'' +
                ", id='" + id + '\'' +
                ", method='" + method + '\'' +
                '}';
    }
}
