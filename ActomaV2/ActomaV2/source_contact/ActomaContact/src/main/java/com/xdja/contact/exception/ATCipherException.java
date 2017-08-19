package com.xdja.contact.exception;

/**
 * Created by wanghao on 2016/2/24.
 */
public class ATCipherException extends AbsContactException {

    private static final String MESSAGE = "调用密钥出错";

    public ATCipherException(){
        super(MESSAGE);
    }

    @Override
    protected String getClsName() {
        return this.getClass().getSimpleName();
    }
}
