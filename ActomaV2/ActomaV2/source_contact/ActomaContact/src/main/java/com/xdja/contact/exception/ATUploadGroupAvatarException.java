package com.xdja.contact.exception;

/**
 * Created by wanghao on 2016/2/27.
 *
 */
public class ATUploadGroupAvatarException extends ContactServiceException {

    private static final String MESSAGE = "安通+上传头像包装对象出错";

    public ATUploadGroupAvatarException(){
        super(MESSAGE);
    }

    public ATUploadGroupAvatarException(Exception e){
        super(e);
    }

    @Override
    protected String getClsName() {
        return this.getClass().getSimpleName();
    }
}
