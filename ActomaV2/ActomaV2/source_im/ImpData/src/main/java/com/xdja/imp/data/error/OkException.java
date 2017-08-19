package com.xdja.imp.data.error;

/**
 * <p>Summary:通用的错误处理类</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.error</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/1/7</p>
 * <p>Time:13:57</p>
 */
public class OkException extends Exception {
    /**
     * 错误码
     */
    private String okCode;
    /**
     * 错误信息
     */
    private String okMessage;
    /**
     * 错误匹配接口
     */
    private OkMatcher matcher;

    /**
     * @return {@link #matcher}
     */
    public OkMatcher getMatcher() {
        return matcher;
    }

    /**
     * @param matcher {@link #matcher}
     */
    public void setMatcher(OkMatcher matcher) {
        this.matcher = matcher;
    }

    /**
     * @return {@link #okMessage}
     */
    public String getOkMessage() {
        return okMessage;
    }

    /**
     * @param okMessage {@link #okMessage}
     */
    public void setOkMessage(String okMessage) {
        this.okMessage = okMessage;
    }

    /**
     * @return {@link #okCode}
     */
    public String getOkCode() {
        return okCode;
    }

    /**
     * @param okCode {@link #okCode}
     */
    public void setOkCode(String okCode) {
        this.okCode = okCode;
    }

    public OkException() {
        super();
    }

    public OkException(OkMatcher matcher) {
        super();
        this.matcher = matcher;
    }

    public OkException(OkMatcher matcher, String okCode, String okMessage) {
        super(okMessage);
        this.matcher = matcher;
        this.okCode = okCode;
        this.okMessage = okMessage;
    }

    public OkException(String okCode, String okMessage){
        super(okMessage);
        this.okCode = okCode;
        this.okMessage = okMessage;
    }

    /**
     * 匹配相应的用户错误信息
     *
     * @return 相应的用户错误信息
     */
    public String match() {
        if (matcher != null) {
            return matcher.match(this);
        }
        return null;
    }

    /**
     * 错误处理
     *
     * @param handler 错误处理句柄
     */
    public void handle(OkHandler handler) {
        if (handler != null) {
            handler.handle(this);
        }
    }
}
