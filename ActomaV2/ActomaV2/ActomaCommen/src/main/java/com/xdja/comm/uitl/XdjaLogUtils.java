package com.xdja.comm.uitl;

import android.content.Context;
import android.support.annotation.Nullable;

import com.xdja.comm.data.AccountBean;
import com.xdja.comm.data.AppLogDao;
import com.xdja.comm.data.DeviceAndAppInfo;
import com.xdja.comm.data.DeviceAndAppInfoBean;
import com.xdja.comm.data.LogInfoBean;
import com.xdja.comm.data.UpLoadLogRequestBean;
import com.xdja.comm.server.AccountServer;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.server.PreferencesServer;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.frame.data.chip.TFCardManager;
import com.xdja.report.bean.reportCrashLog;
import com.xdja.report.reportClientMessage;
import com.xdja.safekeyjar.util.StringResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by XURJ on 2015/12/30.
 */
public class XdjaLogUtils {

    /**
     * 同步上传
     */
    public static final boolean SYNC_UP_LOAD = true;

    /**
     * 不同步上传
     */
    public static final boolean NO_SYNC_UP_LOAD = false;


    /**
     * 保存日志
     *
     * @param context      上下文句柄
     * @param logInfoBean  日志信息
     * @param isSyncUpLoad 是否需要同步上传至服务器
     */
    public static long saveLog(final Context context, @Nullable LogInfoBean logInfoBean, boolean isSyncUpLoad) {
        long insertResult = -1;

        UpLoadLogRequestBean upLoadLogRequestBean = new UpLoadLogRequestBean();

        DeviceAndAppInfoBean deviceAndAppInfoBean = DeviceAndAppInfo.getDeviceAndAppInfo(context);
        if (deviceAndAppInfoBean == null || logInfoBean == null) {
            return insertResult;
        }
        upLoadLogRequestBean.setDeviceAndAppInfoBean(deviceAndAppInfoBean);

        String account = "";

        // modified by ycm for lint 2017/02/15 [start]
        AccountBean accountBean = AccountServer.getAccount();
        if (accountBean != null) {
            try {
                account = accountBean.getAccount();
            } catch (Exception ignored) {
                LogUtil.getUtils("saveLog").e(ignored);// add by ycm for lint 2017/02/13
            }
        }
        // modified by ycm for lint 2017/02/15 [end]

        StringResult tfCardIdResult = TFCardManager.getCardId();
        if (tfCardIdResult != null && tfCardIdResult.getErrorCode() == 0) {
            upLoadLogRequestBean.setCardId(tfCardIdResult.getResult());//cardId
        }

        upLoadLogRequestBean.setAccount(account);
        upLoadLogRequestBean.addLogInfo(logInfoBean);

        insertResult = AppLogDao.instance().insert(upLoadLogRequestBean);
        if (insertResult > 0 && isSyncUpLoad) {
            String ticket = PreferencesServer.getWrapper(ActomaController.getApp()).gPrefStringValue("ticket");
            reportCrashLog rCrashLog = new reportCrashLog();
            rCrashLog.setAccount(upLoadLogRequestBean.getAccount());
            rCrashLog.setCardid(upLoadLogRequestBean.getCardId());
            rCrashLog.setDevicename(upLoadLogRequestBean.getDeviceName());
            rCrashLog.setImei(upLoadLogRequestBean.getImei());
            rCrashLog.setOsversion(upLoadLogRequestBean.getOsVersion());
            rCrashLog.setOstype(upLoadLogRequestBean.getOsType()+"");
            rCrashLog.setAppversion(upLoadLogRequestBean.getAppVersion());
            rCrashLog.setAppid(upLoadLogRequestBean.getAppId());
            rCrashLog.setContent(logInfoBean.getContent());
            rCrashLog.setCrashtime(logInfoBean.getCrashTime());
            final long finalInsertResult = insertResult;
            reportClientMessage.reportClientMessage_crashLogInfo(ticket, account, upLoadLogRequestBean.getCardId(),
                    rCrashLog,
                    new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            LogUtil.getUtils().e(e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                try {
                                    String body = response.body().string();
                                    JSONObject jsonObject;
                                    jsonObject = new JSONObject(body);
                                    if (null != jsonObject &&jsonObject.has("error")) { // TODO: 2017/2/15 若为空如何处理
                                        LogUtil.getUtils().e("report crash log has error!");
                                        return;
                                    }
                                    AppLogDao.instance().delete(finalInsertResult);
                                } catch (JSONException e) {
                                    //e.printStackTrace();
                                    LogUtil.getUtils().e("report crash log JSON parse has error!");
                                }
                            }
                        }
                    });
        }
        return insertResult;
    }
}
