package com.xdja.imsdk.http.bean;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：  请求IM服务返回的错误                           <br>
 * 创建时间：2016/11/27 下午5:11                           <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public class ImResultErrorBean {
    /**
     * 返回的错误log
     */
    private String log;

    /**
     * 错误信息描述
     */
    private String message;

    /**
     * 服务器时间
     */
    private long sst;

    /**
     * 错误码
     */
    private int code;

    /**
     * 消息的标识，消息本地数据库id
     */
    private String flagid;

    /**
     * 请求id
     */
    private long requestid;

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getSst() {
        return sst;
    }

    public void setSst(long sst) {
        this.sst = sst;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getFlagid() {
        return flagid;
    }

    public void setFlagid(String flagid) {
        this.flagid = flagid;
    }

    public long getRequestid() {
        return requestid;
    }

    public void setRequestid(long requestid) {
        this.requestid = requestid;
    }
}
