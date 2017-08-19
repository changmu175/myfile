package com.xdja.contact.exception;

/**
 * Created by wanghao on 2016/2/24.
 */
public class ATJsonBodyNullException extends AbsContactException {

    private static final String MESSAGE = "服务器返回数据body为空";

    public ATJsonBodyNullException(){
        super(MESSAGE);
    }

    @Override
    protected String getClsName() {
        return this.getClass().getSimpleName();
    }
}
