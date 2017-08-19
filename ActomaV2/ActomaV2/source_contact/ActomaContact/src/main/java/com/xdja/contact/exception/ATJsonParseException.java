package com.xdja.contact.exception;

/**
 * Created by wanghao on 2016/2/24.
 */
public class ATJsonParseException extends AbsContactException {

    private static final String MESSAGE = "安通+调用alijson 出错";

    public ATJsonParseException(){
        super(MESSAGE);
    }

    public ATJsonParseException(Exception e){
        super(e);
    }

    public ATJsonParseException(String msg){
        super(msg);
    }

    @Override
    protected String getClsName() {
        return this.getClass().getSimpleName();
    }
}
