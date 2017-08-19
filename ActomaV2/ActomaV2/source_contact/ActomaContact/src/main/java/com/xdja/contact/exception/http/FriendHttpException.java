package com.xdja.contact.exception.http;

import com.xdja.contact.exception.AbsContactException;

/**
 * Created by wanghao on 2016/4/20.
 * @see com.xdja.contact.http.FriendHttpServiceHelper
 * 好友与服务器对接功能出现异常
 */
public class FriendHttpException extends AbsContactException {

    private static final String MESSAGE = "好友功能执行网络操作异常";

    public FriendHttpException(){
        super(MESSAGE);
    }

    @Override
    protected String getClsName() {
        return this.getClass().getSimpleName();
    }
}
