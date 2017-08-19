package com.xdja.report;


import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.xdja.comm.http.OkHttpsClient;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.server.PreferencesServer;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.report.bean.reportCloseAppMode;
import com.xdja.report.bean.reportCrashLog;
import com.xdja.report.bean.reportDataBean;
import com.xdja.report.bean.reportLoginCount;
import com.xdja.report.bean.reportMsgBean;
import com.xdja.report.bean.reportParams;
import com.xdja.report.bean.reportSkipBindMobile;
import com.xdja.report.bean.reportVoipAccept;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by gbc on 2016/12/1.
 */
@SuppressWarnings("unchecked")
public class reportClientMessage {
    private static final String PVER = "1.0";
    private static final int RMT_ACC = 100;
    private static final int RMT_FRAME = 200;
    private static final int RMT_S_LOGIN = 10001;
    private static final int RMT_S_SKIP = 10002;
    private static final int RMT_S_CRASH = 10001;
    private static final int RMT_S_ACCEPT = 30001;
    private static final int RMT_S_CLOSEAPP = 20002;

    private static final String JSONRPC = "2.0";
    private static final String R_METHOD = "rssendmsg";
    private static final String R_METHOD_QUERY_VERSION = "rsqueryversion";

    /**
     * 上报注册时候跳过绑定手机号码
     * @param ticket
     * @param account
     * @param cardId
     * @param responseCallback
     */
    public static void reportClientMessage_skipBindMobile(final String ticket, final String account, final String cardId,
                                                          final Callback responseCallback) {
        if (TextUtils.isEmpty(ticket) || TextUtils.isEmpty(account) || TextUtils.isEmpty(cardId)
                || responseCallback == null) {
            return;
        }
        String reportserver_url = PreferencesServer.getWrapper(ActomaController.getApp())
                .gPrefStringValue("reportUrl");
        if (TextUtils.isEmpty(reportserver_url)) {
            return;
        }
        //json::params::content::message
        reportSkipBindMobile rSkbmM = new reportSkipBindMobile();
        rSkbmM.setAccount(account);
        rSkbmM.setDeviceid(cardId);
        //json::params::content
        reportDataBean rData = new reportDataBean<>();
        rData.setMessage(rSkbmM);
        rData.setVersion(PVER);
        rData.setType(RMT_ACC);
        rData.setSubtype(RMT_S_SKIP);
        //json::params
        reportParams rParams = new reportParams();
        rParams.setAppname("AT+");
        rParams.setUser(account);
        rParams.setContent(rData);
        //json
        reportMsgBean rMessage = new reportMsgBean();
        rMessage.setParams(rParams);
        rMessage.setId("1");
        rMessage.setJsonrpc(JSONRPC);
        rMessage.setMethod(R_METHOD);

        Gson gson = new Gson();
        String json = gson.toJson(rMessage);
        LogUtil.getUtils().i("reportserver_url: " + reportserver_url);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);
        final Request request = new Request.Builder()
                .addHeader("ticket", ticket)
                .url(reportserver_url)
                .post(body)
                .build();
        reportRequest(request, responseCallback);
    }

    /**
     * 登录成功上报信息，统计日登录次数
     * @param ticket
     * @param account
     * @param cardId
     * @param responseCallback
     */
    public static void reportClientMessage_loginCount(final String ticket, final String account, final String cardId,
                                                          final Callback responseCallback) {
        if (TextUtils.isEmpty(ticket) || TextUtils.isEmpty(account) || TextUtils.isEmpty(cardId)
                || responseCallback == null) {
            return;
        }
        String reportserver_url = PreferencesServer.getWrapper(ActomaController.getApp())
                .gPrefStringValue("reportUrl");
        if (TextUtils.isEmpty(reportserver_url)) {
            return;
        }
        //json::params::content::message
        reportLoginCount rLoginCM = new reportLoginCount();
        rLoginCM.setAccount(account);
        rLoginCM.setDeviceid(cardId);
        rLoginCM.setTime(System.currentTimeMillis());

        //json::params::content
        reportDataBean rData = new reportDataBean<>();
        rData.setMessage(rLoginCM);
        rData.setVersion(PVER);
        rData.setType(RMT_ACC);
        rData.setSubtype(RMT_S_LOGIN);
        //json::params
        reportParams rParams = new reportParams();
        rParams.setAppname("AT+");
        rParams.setUser(account);
        rParams.setContent(rData);
        //json
        reportMsgBean rMessage = new reportMsgBean();
        rMessage.setParams(rParams);
        rMessage.setId("1");
        rMessage.setJsonrpc(JSONRPC);
        rMessage.setMethod(R_METHOD);

        Gson gson = new Gson();
        String json = gson.toJson(rMessage);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);
        final Request request = new Request.Builder()
                .addHeader("ticket", ticket)
                .url(reportserver_url)
                .post(body)
                .build();
        reportRequest(request, responseCallback);
    }

    /**
     * voip电话接通耗时统计
     * @param ticket
     * @param voipAccept
     * @param responseCallback
     */
    public static void reportClientMessage_voipAcceptPeriod(final String ticket, final String account,
                                                            final reportVoipAccept voipAccept,
                                                            final Callback responseCallback) {
        if (TextUtils.isEmpty(ticket) || null == account ||
                null == voipAccept || responseCallback == null) {
            return;
        }
        String reportserver_url = PreferencesServer.getWrapper(ActomaController.getApp())
                .gPrefStringValue("reportUrl");
        if (TextUtils.isEmpty(reportserver_url)) {
            return;
        }
        //json::params::content::message
        //voipAccept
        //json::params::content
        reportDataBean rData = new reportDataBean<>();
        rData.setMessage(voipAccept);
        rData.setVersion(PVER);
        rData.setType(RMT_ACC);
        rData.setSubtype(RMT_S_ACCEPT);

        //json::params
        reportParams rParams = new reportParams();
        rParams.setAppname("AT+");
        rParams.setUser(account);
        rParams.setContent(rData);
        //json
        reportMsgBean rMessage = new reportMsgBean();
        rMessage.setParams(rParams);
        rMessage.setId("1");
        rMessage.setJsonrpc(JSONRPC);
        rMessage.setMethod(R_METHOD);

        Gson gson = new Gson();
        String json = gson.toJson(rMessage);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);
        final Request request = new Request.Builder()
                .addHeader("ticket", ticket)
                .url(reportserver_url)
                .post(body)
                .build();
        reportRequest(request, responseCallback);
    }


    /**
     * 关闭安通+应用模式
     * @param ticket
     * @param account
     * @param mode 1 接收IM消息， 0 不接收IM消息
     * @param responseCallback
     */
    public static void reportClientMessage_closeAppMode(final String ticket, final String account,final String cardId,
                                                            final int mode, final Callback responseCallback) {
        if (TextUtils.isEmpty(ticket) || TextUtils.isEmpty(account) || TextUtils.isEmpty(cardId)
               ||  responseCallback == null) {
            return;
        }
        String reportserver_url = PreferencesServer.getWrapper(ActomaController.getApp())
                .gPrefStringValue("reportUrl");
        if (TextUtils.isEmpty(reportserver_url)) {
            return;
        }
        //json::params::content::message
        reportCloseAppMode rCloseMode = new reportCloseAppMode();
        rCloseMode.setAccount(account);
        rCloseMode.setDeviceid(cardId);
        rCloseMode.setImflag(mode);
        //json::params::content
        reportDataBean rData = new reportDataBean<>();
        rData.setMessage(rCloseMode);
        rData.setVersion(PVER);
        rData.setType(RMT_ACC);
        rData.setSubtype(RMT_S_CLOSEAPP);
        //json::params
        reportParams rParams = new reportParams();
        rParams.setAppname("AT+");
        rParams.setUser(account);
        rParams.setContent(rData);
        //json
        reportMsgBean rMessage = new reportMsgBean();
        rMessage.setParams(rParams);
        rMessage.setId("1");
        rMessage.setJsonrpc(JSONRPC);
        rMessage.setMethod(R_METHOD);

        Gson gson = new Gson();
        String json = gson.toJson(rMessage);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);
        final Request request = new Request.Builder()
                .addHeader("ticket", ticket)
                .url(reportserver_url)
                .post(body)
                .build();
        reportRequest(request, responseCallback);
    }

    /**
     * 客户端异常日志上报
     * @param ticket
     * @param account
     * @param cardId
     * @param responseCallback
     */
    public static void reportClientMessage_crashLogInfo(final String ticket, final String account, final String cardId,
                                                      final reportCrashLog rCrashLog, final Callback responseCallback) {
        if (TextUtils.isEmpty(ticket) || TextUtils.isEmpty(account) || TextUtils.isEmpty(cardId)
                || responseCallback == null || null == rCrashLog) {
            return;
        }
        String reportserver_url = PreferencesServer.getWrapper(ActomaController.getApp())
                .gPrefStringValue("reportUrl");
        if (TextUtils.isEmpty(reportserver_url)) {
            return;
        }
        //json::params::content::message
        //rCrashLog
        //json::params::content
        reportDataBean rData = new reportDataBean<>();
        rData.setMessage(rCrashLog);
        rData.setVersion(PVER);
        rData.setType(RMT_FRAME);
        rData.setSubtype(RMT_S_CRASH);

        //json::params
        reportParams rParams = new reportParams();
        rParams.setAppname("AT+");
        rParams.setUser(account);
        rParams.setContent(rData);
        //json
        reportMsgBean rMessage = new reportMsgBean();
        rMessage.setParams(rParams);
        rMessage.setId("1");
        rMessage.setJsonrpc(JSONRPC);
        rMessage.setMethod(R_METHOD);

        Gson gson = new Gson();
        String json = gson.toJson(rMessage);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);
        final Request request = new Request.Builder()
                .addHeader("ticket", ticket)
                .url(reportserver_url)
                .post(body)
                .build();
        reportRequest(request, responseCallback);
    }

    /**
     * 上报请求用户账号登录的app版本号
     *
     * @param ticket
     * @param account
     */
    public static Response reportClientMessage_queryAppVersion(final String ticket,
                                                                       final String account) {

        String reportserver_url = PreferencesServer.getWrapper(ActomaController.getApp())
                .gPrefStringValue("imUrl");

        //判空
        if (TextUtils.isEmpty(ticket) || TextUtils.isEmpty(account) ||
                TextUtils.isEmpty(reportserver_url)) {
            return null;
        }

        //json::params
        reportParams rParams = new reportParams();
        rParams.setAppname("AT+");
        rParams.setUser(account);
        //json
        reportMsgBean rMessage = new reportMsgBean();
        rMessage.setParams(rParams);
        rMessage.setId("1");
        rMessage.setJsonrpc(JSONRPC);
        rMessage.setMethod(R_METHOD_QUERY_VERSION);


        Gson gson = new Gson();
        String json = gson.toJson(rMessage);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);
        final Request request = new Request.Builder()
                .addHeader("ticket", ticket)
                .url(reportserver_url)
                .post(body)
                .build();

        //两种方式，回调、直接返回Respone
        //reportRequestSync(request, responseCallback);

        try {
            Call call = OkHttpsClient.getInstance(ActomaController.getApp())
                    .getOkHttpClient()
                    .newCall(request);
            return call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 异步请求
     * @param request
     * @param responseCallback
     */
    private static void reportRequest(final Request request, final Callback responseCallback) {
        if (null == request || null == responseCallback) {
            return;
        }
        new AsyncTask<Long,Long,Object>() {
            @Override
            protected Object doInBackground(Long[] params) {
                try {
                    OkHttpsClient.getInstance(ActomaController.getApp()).getOkHttpClient().newCall(request)
                            .enqueue(responseCallback);
                } catch (Exception e) {
                        LogUtil.getUtils().e(e);
                }
                return null;
            }
        }.execute();
    }

    /**
     * 同步请求
     *
     * @param request
     * @param responseCallback 
     */
    private static void reportRequestSync(Request request, final Callback responseCallback) {
        if (null == request) {
            return;
        }
        okhttp3.Call call = OkHttpsClient.getInstance(ActomaController.getApp())
                .getOkHttpClient()
                .newCall(request);
        try {
            Response response = call.execute();
            responseCallback.onResponse(call, response);
        } catch (IOException e) {
            e.printStackTrace();
            responseCallback.onFailure(call, e);
        }
    }
}
