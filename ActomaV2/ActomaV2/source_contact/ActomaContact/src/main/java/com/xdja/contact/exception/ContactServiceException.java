package com.xdja.contact.exception;

/**
 * Created by wanghao on 2016/3/9.
 * 业务层处理异常
 */
public class ContactServiceException extends AbsContactException {

    public ContactServiceException(Exception exception) {
        super(exception);
    }

    public ContactServiceException(String message){
        super(message);
    }


    @Override
    protected String getClsName() {
        return this.getClass().getSimpleName();
    }
}
