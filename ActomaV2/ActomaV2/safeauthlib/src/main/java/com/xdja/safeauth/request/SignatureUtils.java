package com.xdja.safeauth.request;

import android.text.TextUtils;
import android.util.Base64;

import com.xdja.safeauth.cip.CipManager;
import com.xdja.safeauth.exception.CipException;
import com.xdja.safeauth.log.Log;

import java.io.UnsupportedEncodingException;

/**
 * Created by THZ on 2016/5/18.
 */
public class SignatureUtils {

    private static final String TAG = SignatureUtils.class.getSimpleName();

    private static final String VERSION = "0.1";
    /**
     * 获取ticket的方法
     * @param pin
     * @param random
     * @return
     * @throws CipException
     */
    public static GetTicketBean sm2SignData(final String pin, String random, String index) throws
            CipException {

        if (TextUtils.isEmpty(random)) {
            Log.e(TAG, "random is null");
            return null;
        }
        if (TextUtils.isEmpty(pin)) {
            Log.e(TAG, "pin is null");
            return null;
        }
        if (TextUtils.isEmpty(index)) {
            Log.e(TAG, "index is null");
            return null;
        }

        long currentTime = System.currentTimeMillis();
        byte[] datain = null;

        StringBuffer sb = new StringBuffer();
        sb.append(random + "|" + currentTime);

        Log.i(TAG, "sb : " + sb.toString());

        try {
            datain = sb.toString().getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte result[] = CipManager.getInstance().sm2Sign(pin, datain);
        String signature = Base64.encodeToString(result, Base64.NO_WRAP);

        Log.i(TAG, "signature : " + signature);
        Log.i(TAG, "signature byte length : " + Base64.decode(signature, Base64.NO_WRAP).length);

        String cardId = CipManager.getInstance().getCardId();
        Log.i(TAG, "cardId : " + cardId);
        String sn = CipManager.getInstance().getSm2SignCertSn();
        GetTicketBean getTicketBean = new GetTicketBean();
        getTicketBean.setVersion(VERSION);
        getTicketBean.setCardId(cardId);
        getTicketBean.setIndex(index);
        getTicketBean.setSignature(signature);
        getTicketBean.setTimestamp(currentTime + "");
        getTicketBean.setSn(sn);
        return getTicketBean;
    }
}
