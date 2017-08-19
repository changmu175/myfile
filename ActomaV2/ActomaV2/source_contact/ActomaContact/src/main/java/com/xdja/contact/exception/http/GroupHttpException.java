package com.xdja.contact.exception.http;

import com.xdja.contact.exception.AbsContactException;

/**
 * Created by wanghao on 2016/4/20.
 * @see com.xdja.contact.http.GroupHttpServiceHelper
 * 群组与服务器对接功能出现异常
 */
public class GroupHttpException extends AbsContactException {

    private static final String MESSAGE = "群组功能执行网络操作异常";

    public GroupHttpException(){
        super(MESSAGE);
    }

    @Override
    protected String getClsName() {
        return this.getClass().getSimpleName();
    }
}
