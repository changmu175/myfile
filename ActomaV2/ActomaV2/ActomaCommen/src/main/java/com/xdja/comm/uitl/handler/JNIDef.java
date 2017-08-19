package com.xdja.comm.uitl.handler;

import com.xdja.SafeKey.JNIAPI;

/**
 * Created by xdja-fjd on 2016/2/24.
 * JNIAPI错误处理定义
 */
public class JNIDef {
//    * 在错误码中的错误标识定义如下：<br>
//    * 111:重试即可正常调用的<br>
//    * 222:重启安全芯片APK<br>
//    * 333:重启应用APK<br>
//    * 444:重启手机即可正常调用的；<br>
//    * 555:程序版本不一致，需要更新程序才可正常调用的；<br>
//    * 666:需要返厂修卡、修COS、写证书；<br>
//    * 777:未知原因，重试、重启、返厂、客服<br>
//    * 888:未遇到过、暂无解决方案的，可以运营收集信息<br>
//    * 999:调用其它接口进行处理后，可正常调用的；<br>
//    * aaa:不存在<br>
//    * qqq:待讨论<br>
//    * 001:进入解锁流程<br>
//    * 002:用户主动识别<br>

    /**
     * 成功
     */
    public static final int HANDLER_SUCESS = 0;

    /**
     * 未知的处理方式
     */
    public static final int HANDLER_UNKNOW = -10000;

    /**
     * 重试即可正常调用(111)
     */
    public static final int HANDLER_RETRY = -10001;
    /**
     * 重启安全芯片APK(222)
     */
    public static final int HANDLER_RELOAD_CARDAPK = -10002;
    /**
     * 重启应用APK(333)
     */
    public static final int HANDLER_RELOAD_APK = -10003;
    /**
     * 重启手机即可正常调用(444)
     */
    public static final int HANDLER_REBOOT = -10004;
    /**
     * 程序版本不一致，需要更新程序才可正常调用(555)
     */
    public static final int HANDLER_UPDATE_APK = -10005;
    /**
     * 需要返厂修卡、修COS、写证书(666)
     */
    public static final int HANDLER_FIX = -10006;
    /**
     * 未知原因，重试、重启、返厂、客服(777)
     */
    public static final int HANDLER_UNKNOW_RESON = -100007;
    /**
     * 未遇到过、暂无解决方案的，可以运营收集信息(888)
     */
    public static final int HANDLER_UNKNOW_NEW = -100008;
    /**
     * 调用其它接口进行处理后，可正常调用(999)
     */
    public static final int HANDLER_CALL_FUNCTION = -100009;
    /**
     * 不存在(aaa)
     */
    public static final int HANDLER_NOT_EXIST = -100010;
    /**
     * 待讨论(qqq)
     */
    public static final int HANDLER_DISCUSS = -100011;
    /**
     * 进入解锁流程(001)
     */
    public static final int HANDLER_UNLOCK = -100012;
    /**
     * 用户主动识别(002)
     */
    public static final int HANDLER_USER = -100013;

    /**
     * {@link #HANDLER_RETRY} 和 {@link #HANDLER_RELOAD_APK} 和 {@link #HANDLER_REBOOT}
     * 111 333 444
     */
    public static final int HANDLER_UNIN_1 = -100014;
    /**
     * {@link #HANDLER_REBOOT} 和 {@link #HANDLER_FIX}
     * 444 666
     */
    public static final int HANDLER_UNIN_2 = -100015;
    /**
     * {@link #HANDLER_FIX} 和 {@link #HANDLER_CALL_FUNCTION}
     * 666 999
     */
    public static final int HANDLER_UNIN_3 = -100016;
    /**
     * {@link #HANDLER_DISCUSS} 和 {@link #HANDLER_UNKNOW_RESON}
     * qqq 777
     */
    public static final int HANDLER_UNIN_4 = -100017;
    /**
     * {@link #HANDLER_RETRY} 和 {@link #HANDLER_UPDATE_APK}
     * 111 555
     */
    public static final int HANDLER_UNIN_5 = -100018;
    /**
     * {@link #HANDLER_USER} 和 {@link #HANDLER_REBOOT}和 {@link #HANDLER_UPDATE_APK}
     * 002 444 555
     */
    public static final int HANDLER_UNIN_6 = -100019;

    /**
     * @return
     */
    public static int getHandleType(int code) {
        int handleType = HANDLER_UNKNOW;
        switch (code) {
            case JNIAPI.XKR_OK:
                handleType = HANDLER_SUCESS;
                break;
            case JNIAPI.XKR_NO_HANDLE:
            case JNIAPI.XKR_IO_FAILED:
                handleType = HANDLER_UNIN_1;
                break;
            case JNIAPI.XKR_BACK_LENGTH:
            case JNIAPI.XKR_BACK_DATA:
            case JNIAPI.XKR_NO_ROLE:
            case JNIAPI.XKR_DATAIN_SIZE:
            case JNIAPI.XKR_OUTBUF_SIZE:
            case JNIAPI.XKR_INVALID_PARA:
            case JNIAPI.XKR_PARAMETER:
            case JNIAPI.XKR_NO_THIS_CMD:
            case JNIAPI.XKR_WRONG_KEY_TYPE:
            case JNIAPI.XKR_RSAPUBLIC_FAILED:
            case JNIAPI.XKR_NOT_SUPPORT:
                handleType = HANDLER_UPDATE_APK;
                break;
            case JNIAPI.XKR_RESET_FAILED:
            case JNIAPI.XKR_APP_LOCKED:
            case JNIAPI.XKR_CARD_LOCKED:
            case JNIAPI.XKR_HASH_FAILED:
            case JNIAPI.XKR_SIGN_CONFIRM:
            case JNIAPI.XKR_SIGN_CANCEL:
            case JNIAPI.XKR_CONDITION:
            case JNIAPI.XKR_DECRYPT_FAIL:
            case JNIAPI.XKR_NOT_FIND_DATA:
            case JNIAPI.XKR_DGI_NOT_SUPPORT:
            case JNIAPI.XKR_DATA_NOCORRENT:
            case JNIAPI.XKR_EXAUTH_FAIL:
            case JNIAPI.XKR_RSA_NOT_FIND:
            case JNIAPI.XKR_GETMOUNTPATH_FAILD:
                handleType = HANDLER_NOT_EXIST;
                break;
            case JNIAPI.XKR_PASSWORD:
            case JNIAPI.XKR_KEY_LOCKED:
                handleType = HANDLER_UNLOCK;
                break;
            case JNIAPI.XKR_EEPROM_WRITE:
                handleType = HANDLER_UNIN_2;
                break;
            case JNIAPI.XKR_CMD_NOTMATCH_LINE:
                handleType = HANDLER_UNKNOW_RESON;
                break;
            case JNIAPI.XKR_CMD_NOTMATCH_FAT:
            case JNIAPI.XKR_DATA_PARAMETER:
            case JNIAPI.XKR_FILE_NOT_EXIST:
            case JNIAPI.XKR_KEYFILE_NOT_EXIST:
            case JNIAPI.XKR_KEY_NOT_EXIST:
                handleType = HANDLER_FIX;
                break;
            case JNIAPI.XKR_NO_POWER:
                handleType = HANDLER_CALL_FUNCTION;
                break;
            //文件无足够空间 应用自己写入的业务文件时无足够空间 正常业务返回 <br>
            case JNIAPI.XKR_NO_FILE_SPACE:
                //文件已存在  正常业务返回
            case JNIAPI.XKR_FILE_EXIST:
                //签名验证失败 RSA签名验证的业务接口，签名是错误的 业务处理
            case JNIAPI.XKR_SIGN_VERIFY:
                //-99:卡未激活,需先激活才能正常使用
            case JNIAPI.XKR_NOT_ACTIVATED:
                //未知错误  重试或客服
            case JNIAPI.XKR_UNKNOWN:
                break;
            case JNIAPI.XKR_NOT_GET_RANDOM:
            case JNIAPI.XKR_BAD_PRIKEY:
            case JNIAPI.XKR_TLOCK_TIMEOUT:
                handleType = HANDLER_RETRY;
                break;
//            case JNIAPI.XKR_FILE_CONTFENT:
            case JNIAPI.XKR_BAD_PUBKEY:
            case JNIAPI.XKR_BAD_CERT:
                handleType = HANDLER_UNIN_3;
                break;
            case JNIAPI.XKR_WRONG_STATE:
                handleType = HANDLER_REBOOT;
                break;
            case JNIAPI.XKR_WRONG_LE:
            case JNIAPI.XKR_INVALID_DATA:
                handleType = HANDLER_UNIN_4;
                break;
            case JNIAPI.XKR_WRONG_MAC:
                handleType = HANDLER_UNIN_5;
                break;
            case JNIAPI.XKR_TLOCK_FAILD:
                handleType = HANDLER_UNKNOW_NEW;
                break;
            case JNIAPI.XKR_MALLOC_FALID:
                handleType = HANDLER_RELOAD_CARDAPK;
                break;
            case JNIAPI.XKR_NO_KEY:
                handleType = HANDLER_UNIN_6;
                break;
            default:
                break;
        }
        return handleType;
    }
}
