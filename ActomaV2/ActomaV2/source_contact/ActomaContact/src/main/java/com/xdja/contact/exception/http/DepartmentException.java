package com.xdja.contact.exception.http;

import com.xdja.contact.exception.AbsContactException;

/**
 * Created by xienana on 2016/12/16.
 */
public class DepartmentException extends AbsContactException {

    private static final String MESSAGE = "department module get internet server error";

    public DepartmentException() {
        super(MESSAGE);
    }

    @Override
    protected String getClsName() {
        return this.getClass().getSimpleName();
    }
}
