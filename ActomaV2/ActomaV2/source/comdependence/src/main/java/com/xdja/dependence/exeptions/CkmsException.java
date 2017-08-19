package com.xdja.dependence.exeptions;

/**
 * Created by ALH on 2016/8/3.
 */
//alh@xdja.com<mailto://alh@xdja.com> 2016-08-03 add. fix bug 2399 . review by wangchao1. Start
public class CkmsException extends OkException{
    public static final String CODE_CKMS_INIT_NET_ERROR ="100";
    public static final String CODE_CKMS_SERVER_ERROR = "101";
    public static final String CODE_CKMS_AUTH_DEVICE_ERROR = "102";
    public static final String CODE_CKMS_VERSION_ERROR ="103";
    public static final String CODE_CKMS_AUTH_INFO_INVALID = "104";
	/*[S]modify by tangsha@20161110 for 5836*/
    public static final String CODE_CKMS_INIT_TIME_ERROR = "105";
    public static final String CODE_EXIST_NOT_AUTH_DEVICE = "106";
	/*[E]modify by tangsha@20161110 for 5836*/

    public CkmsException(String okCode) {
        super(okCode);
    }

    public CkmsException(String detailMessage, String okCode) {
        super(detailMessage, okCode);
    }

    public CkmsException(String detailMessage, Throwable throwable, String okCode) {
        super(detailMessage, throwable, okCode);
    }

    public CkmsException(Throwable throwable, String okCode) {
        super(throwable, okCode);
    }
    //alh@xdja.com<mailto://alh@xdja.com> 2016-08-03 add. fix bug 2399 . review by wangchao1. End
}
