package com.xdja.comm.uitl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cb on 2015/11/26.
 */
public class QRUtil {
    /**
     * 解锁usbkey二维码
     */
    private static final String USBKEYUNLOCK = "USBKeyUnlock";

    public enum QRType {
        Error,
        USBKeyUnlock
    }

    /**
     * 获取通用二维码业务标识
     * @param qrString 二维码信息
     * @return
     */
    public static QRType getQRBusinessIdentity(String qrString){
        try {
            JSONObject jsonObject = new JSONObject(qrString);
            String businessIndentity = jsonObject.getString("businessIdentity");
            if (businessIndentity.equals(USBKEYUNLOCK)){
                return QRType.USBKeyUnlock;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return QRType.Error;
    }

    public static final String AUTHORIZE_ID_TITLE = "authorizeIdTitle:";
    public static final String ACCOUNT_TITLE = "1#";

    @NonNull
    public static String AuthorizeId2QrString(@NonNull String authorizeId) {
        return AUTHORIZE_ID_TITLE + authorizeId;
    }

    /**
     * 二维码扫描值转换为有效的AuthorizeId
     * @param qrCode    二维码扫描值
     * @return  返回值可能为空，代表不符合规则。
     */
    @Nullable
    public static String qrString2AuthorizeId(@Nullable String qrCode) {

        if (qrCode == null) {// add by ycm for lint 2017/02/15
            return null;
        }

        try {
            String[] strings = qrCode.split(AUTHORIZE_ID_TITLE);
            return strings[1];
        } catch (Exception e) {
            return null;
        }
    }

    @NonNull
    public static String account2QrString(@NonNull String contact) {
        return AUTHORIZE_ID_TITLE + contact;
    }

    /**
     * 二维码扫描值转换为有效的contact
     * @param qrCode    二维码扫描值
     * @return  返回值可能为空，代表不符合规则。
     */
    @Nullable
    public static String qrString2Account(@Nullable String qrCode) {
        if (qrCode == null) {// add by ycm for lint 2017/02/15
            return null;
        }

        try {
            String[] strings = qrCode.split(ACCOUNT_TITLE);
            return strings[1];
        } catch (Exception e) {
            return null;
        }
    }
}
