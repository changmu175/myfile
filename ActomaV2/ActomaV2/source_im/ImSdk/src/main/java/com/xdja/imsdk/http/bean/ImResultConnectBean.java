package com.xdja.imsdk.http.bean;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：  连接IM服务器结果                               <br>
 * 创建时间：2016/11/27 下午6:58                           <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public class ImResultConnectBean {
    private boolean success;            // 连接成功，login接口使用
    private int code;                   // 连接异常，网络异常或返回结果异常

    public ImResultConnectBean(boolean success, int code) {
        this.success = success;
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean result) {
        this.success = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "ImResultConnectBean{" +
                "success=" + success +
                ", code=" + code +
                '}';
    }
}
