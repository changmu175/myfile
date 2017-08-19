package com.xdja.contact.exception;

/**
 * Created by wanghao on 2016/2/24.
 * 网络请求返回HttpResponse 异常
 */
public class ATHttpResponseException extends AbsContactException{


    private static final String MESSAGE = "服务器返回数据response出错";

    public ATHttpResponseException(){
        super(MESSAGE);
    }


    @Override
    protected String getClsName() {
        return this.getClass().getSimpleName();
    }
}
