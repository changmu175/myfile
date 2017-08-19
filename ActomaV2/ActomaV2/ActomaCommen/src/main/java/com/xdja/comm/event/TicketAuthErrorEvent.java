package com.xdja.comm.event;

/**
 * Created by gbc on 2016/10/21.
 */
public class TicketAuthErrorEvent {
    /**
     * 错误码
     */
    private int code;
    /**
     * 错误信息
     */
    private String message;

    /**
     * 错误级别
     */
    private int level;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
