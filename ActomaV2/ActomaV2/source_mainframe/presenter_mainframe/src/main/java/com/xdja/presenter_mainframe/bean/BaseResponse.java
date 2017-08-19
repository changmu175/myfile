package com.xdja.presenter_mainframe.bean;

/**
 * Created by geyao on 2015/7/21.
 * 应用市场-操作信息上传bean
 */
public class BaseResponse {
    /**
     * 操作是否成功
     */
    private boolean result;
    /**
     * 返回消息，如果操作失败则为失败信息，如果操作成功可为任意信息或者为“”。
     */
    private String msg;

    public BaseResponse() {
    }

    public BaseResponse(boolean result, String msg) {
        this.result = result;
        this.msg = msg;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "result=" + result +
                ", msg='" + msg + '\'' +
                '}';
    }
}
