package com.xdja.safeauth.cip;

import android.text.TextUtils;
import android.util.Log;

import com.xdja.SafeKey.JNIAPI;
import com.xdja.safeauth.exception.CipException;
import com.xdja.safekeyjar.XDJASafeTF;
import com.xdja.safekeyjar.util.StringResult;

/**
 * Created by THZ on 2016/5/18.
 * 安全卡操作
 */
public class CipManager {

    /**
     * 日志标签
     */
    private static final String TAG = CipManager.class.getSimpleName();

    /**
     * 操作卡的对象
     */
    private static CipManager cipManager;



    /**证书所在容器*/
    public static final int SM2_CERT_CONTAINER_ID = 6; // 证书所在容器


    // TF卡角色

    public static final int ROLE_R = 0x11;

    /**
     * 证书类型-交换证书（加密证书）
     * */

    public static final int CERT_TYPE_DEC = 1;

    /**
     * 证书类型-签名证书
     * */
    public static final int CERT_TYPE_SIGN = 2;


    /**
     * 操作成功
     */
    public static final int OK_NRES = 0;

    /**
     * 97
     */
    public static final int INT_97 = 97;
    /**
     * 获取tf卡操作实例
     * @return
     */
    public static CipManager getInstance() {
        synchronized (CipManager.class) {
            if (cipManager == null) {
                cipManager = new CipManager();
            }
        }
        return cipManager;
    }




    /**
     * 获取sm2的签名证书的sn
     * @return
     */
    public String getSm2SignCertSn() throws CipException {
        StringResult stringResult = null;

        String result = null;
        XDJASafeTF xdjaSafeTF = XDJASafeTF.getInstance();
        if(xdjaSafeTF != null){
            stringResult = xdjaSafeTF.getSafeCardSn(SM2_CERT_CONTAINER_ID, CERT_TYPE_SIGN);
        }
        if (stringResult != null) {
            int nres = stringResult.getErrorCode();
            if (nres == OK_NRES) {
                result = stringResult.getResult();
            } else {
                Log.e(TAG, "getSm2EncCertSn error :" + XDJASafeTF.getInstance().getErrorInfo(nres) + " nres : " + nres);
                throw new CipException(nres, XDJASafeTF.getInstance().getErrorInfo(nres));
            }
        }
        return result;
    }


    /**
     * 获取TF卡硬件序列号
     *
     * @return StringResult对象中getErrorCode()方法为执行结果，0标识无错误，否则该值为错误代码；
     * getResult()方法得到TF卡硬件序列号
     */
    public String getCardId() throws CipException {
        StringResult stringResult = null;
        XDJASafeTF xdjaSafeTF = XDJASafeTF.getInstance();
        if (xdjaSafeTF != null) {
            stringResult = xdjaSafeTF.getSafeCardID();
            if (stringResult != null) {
                if (stringResult.getErrorCode() == OK_NRES) {
                    return stringResult.getResult();
                } else {
                    throw new CipException(stringResult.getErrorCode(), XDJASafeTF.getInstance().getErrorInfo(stringResult.getErrorCode()));
                }
            }
        }

        return "";
    }



    /**
     * sm2签名
     * @param pin 卡的pin码
     * @param dataIn 输入的源数据
     * @return 签名的数据
     */
    public byte[] sm2Sign(final String pin, byte[] dataIn) throws CipException  {
        if (dataIn == null || TextUtils.isEmpty(pin)) {
            Log.i(TAG, "sm2Sign error dataIn is null or  pin is null");
            return null;
        }
        int length = dataIn.length;
        int nres = 0;
        byte[] outData = new byte[length + INT_97];
        int[] outLen = new int[1];
        Log.i(TAG, "SM2Sign begin: pin = " + pin);
        nres = XDJASafeTF.getInstance().SM2Sign(ROLE_R, pin,
                SM2_CERT_CONTAINER_ID, CERT_TYPE_SIGN, JNIAPI.SIGN_NOHASH,
                dataIn, dataIn.length, outData, outLen);
        if (nres == 0) {
            int len = outLen[0];
            byte[] result = new byte[len];
            System.arraycopy(outData, 0, result, 0, len);
            return result;
        } else {
            Log.e(TAG, "sm2Sign error :"
                    + XDJASafeTF.getInstance().getErrorInfo(nres));
            throw new CipException(nres, XDJASafeTF.getInstance().getErrorInfo(nres));

        }
    }

}
