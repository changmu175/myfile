package com.xdja.frame.data.chip;

import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.NonNull;

import com.xdja.dependence.annotations.ContextSpe;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.dependence.exeptions.SafeCardException;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.safekeyjar.XDJASafeTF;
import com.xdja.safekeyjar.util.ByteArrayResult;
import com.xdja.safekeyjar.util.StringResult;
import com.xdja.unitepin.UnitePinManager;

import java.io.UnsupportedEncodingException;

import javax.inject.Inject;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.frame.data.chip</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/5/4</p>
 * <p>Time:11:18</p>
 */
public class TFCardManager {

    public static final int RESULT_OK = 0;
    public static final int STATE_NEGATIVE_ACTIVE = -99;
    // TF卡角色
    public static final int ROLE_R = 0x11;

    private Context context;

    @Inject
    public TFCardManager(@ContextSpe(DiConfig.CONTEXT_SCOPE_APP) Context context) {
        this.context = context;
    }

    public boolean initTFCardManager() {
        int initResult = XDJASafeTF.getInstance().init(this.context);
        if (initResult != RESULT_OK) {
            LogUtil.getUtils().e(XDJASafeTF.getInstance().getErrorInfo(initResult));
        }
        return initResult == RESULT_OK;
    }

    public boolean initUnitePinManager() {
        int initResult = UnitePinManager.getInstance().init(this.context);
        if (initResult != RESULT_OK) {
            LogUtil.getUtils().e(XDJASafeTF.getInstance().getErrorInfo(initResult));
        }
        return initResult == RESULT_OK;
    }

    /**
     * 检测安全卡是否存在
     *
     * @return 检测结果（true为存在，false为不存在）
     */
    public static boolean detectSafeCard() {
        int result = XDJASafeTF.getInstance().detectSafeCard();
        if (result != RESULT_OK) {
            LogUtil.getUtils().e(XDJASafeTF.getInstance().getErrorInfo(result));
        }
        return result == RESULT_OK;
    }

    /**
     * 检测安全卡是否激活
     *
     * @return 检测结果（true为激活；false为未激活）
     */
    public boolean isSafeCardActivied() {
        int result = XDJASafeTF.getInstance().GetActivateState();
        return result != STATE_NEGATIVE_ACTIVE;
    }

    /**
     * 激活安全卡
     *
     * @param url 激活地址
     * @return 激活结果（true为激活成功，false为激活失败）
     */
    public boolean activieSafeCard(@NonNull String url) {
	    //[S]modify by tangsha@20161118 for active chip (6146)
        int result = XDJASafeTF.getInstance().ActivateCardByURL(url);
        LogUtil.getUtils().e("TFCardManager activieSafeCard url is "+url+" result "+result);
        return result == RESULT_OK;
		//[E]modify by tangsha@20161118 for active chip (6146)
    }

    /**
     * 获取设备ID
     *
     * @return 设备ID
     * @throws SafeCardException
     */
    public static String getDeviceId() throws SafeCardException {
        ByteArrayResult cardIDBAR = XDJASafeTF.getInstance().getCardID();
        if (cardIDBAR != null) {
            int errorCode = cardIDBAR.getErrorCode();
            if (errorCode == RESULT_OK) {
                try {
                    return new String(cardIDBAR.getResult(), "UTF-8").toLowerCase();
                } catch (UnsupportedEncodingException e) {
                    LogUtil.getUtils().e(e.getMessage());
                    throw new SafeCardException(e.getMessage(), SafeCardException.ERROR_UNKNOWN);
                }
            }
            throw new SafeCardException(XDJASafeTF.getInstance().getErrorInfo(errorCode),
                    SafeCardException.ERROR_GETCARDID_FAILD);
        }
        throw new SafeCardException(SafeCardException.ERROR_UNKNOWN);
    }

    //[s]modify by xienana for bug 6039 @20161118 review by tangsha
    /**
     * 获取pin码
     *
     * @return
     */
    public static String getPin() {
        String[] pin = new String[1];
        int result = UnitePinManager.getInstance().uniteGetPin(ROLE_R, pin);

        if (result == 0) {
            return pin[0];
        }
        return "";
    }
    //[e]modify by xienana for bug 6039 @20161118 review by tangsha

    /**
     * 获取TF卡硬件序列号
     *
     * @return StringResult对象中getErrorCode()方法为执行结果，0标识无错误，否则该值为错误代码；
     * getResult()方法得到TF卡硬件序列号
     */
    public static StringResult getCardId() {

        StringResult stringResult = null;

        XDJASafeTF xdjaSafeTF = XDJASafeTF.getInstance();
        if (xdjaSafeTF != null) {
            stringResult = xdjaSafeTF.getSafeCardID();
        }

        return stringResult;
    }

    /**
     * 证书类型-交换证书（加密证书）
     */
    public static final int CERT_TYPE_DEC = 1;
    /**
     * 证书所在容器
     */
    public static final int SM2_CERT_CONTAINER_ID = 6; // 证书所在容器
    /**
     * 获取sm2的加密证书的sn
     *
     * @return StringResult对象中getErrorCode()方法为执行结果，0标识无错误，否则该值为错误代码；
     * getResult()方法得到证书sn字符串
     */
    public static String getSm2EncCertSnString() {
        StringResult stringResult = null;

        String result = null;
        XDJASafeTF xdjaSafeTF = XDJASafeTF.getInstance();
        if (xdjaSafeTF != null) {
            stringResult = xdjaSafeTF.getSafeCardSn(SM2_CERT_CONTAINER_ID, CERT_TYPE_DEC);
        }
        if (stringResult != null) {
            int nres = stringResult.getErrorCode();
            if (nres == 0) {
                result = stringResult.getResult();
            } else {
//                Log.i(TAG, "getSm2EncCertSn error :" + nres);
            }
        }
        return result;
    }


    /**
     * 获取卡id
     *
     * @return
     */
    public static String getTfCardId() {
        try {
            ByteArrayResult result = XDJASafeTF.getInstance().getCardID();
            if (result != null) {
                return new String(result.getResult(), "utf-8");
            } else {
                return "";
            }
        } catch (Exception e) {
            LogUtil.getUtils().i("获取卡id======" + e.getMessage());

            SystemClock.sleep(10);

            try {
                ByteArrayResult result = XDJASafeTF.getInstance().getCardID();
                if (result != null) {
                    return new String(result.getResult(), "utf-8");
                } else {
                    return "";
                }
            } catch (Exception e1) {
                LogUtil.getUtils().i("获取卡id======" + e1.getMessage());
            }
        }
        return "";
    }

}
