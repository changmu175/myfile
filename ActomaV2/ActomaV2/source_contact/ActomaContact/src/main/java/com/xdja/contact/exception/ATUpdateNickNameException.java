package com.xdja.contact.exception;

/**
 * Created by wanghao on 2016/2/27.
 *
 */
public class ATUpdateNickNameException extends ContactServiceException{

    private static final String MESSAGE = "安通+更新群昵称，群id为空";

    public ATUpdateNickNameException(){
        super(MESSAGE);
    }

    public ATUpdateNickNameException(Exception e){
        super(e);
    }

    @Override
    protected String getClsName() {
        return this.getClass().getSimpleName();
    }
}
