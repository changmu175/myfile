package com.xdja.comm.uitl.handler;

/**
 * Created by xdja-yogapro3 on 2016/2/25.
 */
public class UnitWrapper {
    /**
     * 错误码
     */
    private int errorCode;
    /**
     * 错误信息
     */
    private String errorMsg;
    /**
     * 处理方式
     */
    private int hanlerType;

    public int getHanlerType() {
        return hanlerType;
    }

    public void setHanlerType(int hanlerType) {
        this.hanlerType = hanlerType;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
