package com.xdja.contact.exception;

/**
 * Created by wanghao on 2016/2/27.
 *
 */
public class ATUpdateGroupNameException extends ContactServiceException{

    private static final String MESSAGE = "安通+更新群名称，群id或者群名称为空";

    public ATUpdateGroupNameException(){
        super(MESSAGE);
    }

    public ATUpdateGroupNameException(Exception e){
        super(e);
    }

    @Override
    protected String getClsName() {
        return this.getClass().getSimpleName();
    }
}
