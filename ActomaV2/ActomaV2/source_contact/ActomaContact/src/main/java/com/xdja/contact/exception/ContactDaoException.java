package com.xdja.contact.exception;

/**
 * Created by wanghao on 2015/12/9.
 * 数据库操作层异常处理
 */
public class ContactDaoException extends AbsContactException {

    public ContactDaoException(Exception e){
        super(e);
    }

    @Override
    protected String getClsName() {
        return this.getClass().getSimpleName();
    }

}
