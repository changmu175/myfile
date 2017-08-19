package com.xdja.contact.exception;

/**
 * Created by wanghao on 2016/3/9.
 * 好友模块处理组合业务出错
 */
public class FriendServiceWrapException extends AbsContactException {

    private static final String MESSAGE = "好友模块处理组合业务出错";

    public FriendServiceWrapException(){
        super(MESSAGE);
    }

    @Override
    protected String getClsName() {
        return this.getClass().getSimpleName();
    }
}
