package com.xdja.imsdk.http.bean;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：  请求IM服务器返回结果                           <br>
 * 创建时间：2016/11/27 下午5:14                           <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public class ImResultBean <T> {
    /**
     * Jsonrpc版本号，固定是2.0
     */
    private String jsonrpc;

    /**
     * 唯一表示符，默认都是1
     */
    private String id;

    /**
     * 结果bean
     */
    private T result;

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

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
