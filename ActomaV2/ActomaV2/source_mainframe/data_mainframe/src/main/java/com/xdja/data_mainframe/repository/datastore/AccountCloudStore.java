package com.xdja.data_mainframe.repository.datastore;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.xdja.comm.bean.DataMigrationAccountBean;
import com.xdja.comm.cust.CustInfo;
import com.xdja.data_mainframe.rest.ApiFactory;
import com.xdja.dependence.annotations.ConnSecuritySpe;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.frame.data.net.ServiceGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Response;
import rx.Observable;

/**
 * <p>Summary:请求服务器接口获取原始的Http响应</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.data_mainframe.repository.datastore</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/14</p>
 * <p>Time:15:36</p>
 */
@SuppressWarnings({"EmptyClass"})
public class AccountCloudStore {

    public static class PreAccountCloudStore extends CloudStore implements AccountStore.PreAccountStore {

        @Inject
        public PreAccountCloudStore(@ConnSecuritySpe(DiConfig.CONN_HTTPS_DEF) ServiceGenerator serviceGenerator) {
            super(serviceGenerator);
        }

        @Override
        public Observable<Response<MultiResult<Object>>> migrateOldAccount(@Nullable String account, @Nullable String passwd, @Nullable String deviceId) {
            Map<String, String> body = new HashMap<>();
            body.put("account", account);
            body.put("passwd", passwd);
            body.put("deviceName", deviceId);
            return ApiFactory.getAccountRestApi(this.serviceGenerator).migrateOldAccount(body);
        }

        @Override
        public Observable<DataMigrationAccountBean> isNewAccount() {
            return ApiFactory.getAccountRestApi(this.serviceGenerator).isNewAccount();
        }

        @Override
        public Observable<Response<MultiResult<String>>> registAccount(@Nullable String nickName,
                                                                       @NonNull String password,
                                                                       @Nullable String avatarId,
                                                                       @Nullable String thumbnailId,
                                                                       @Nullable String deviceId) {
            Map<String, String> body = new HashMap<>();
            body.put("nickName", nickName);
            body.put("passwd", password);
            body.put("avatarId", avatarId);
            body.put("thumbnailId", thumbnailId);
            body.put("deviceName", deviceId);

            return ApiFactory.getAccountRestApi(this.serviceGenerator).registAccount(body);

        }

        @Override
        public Observable<Response<MultiResult<Object>>> reObtainAccount(@NonNull String oldAccount,
                                                                         @NonNull String innerAuthCode) {
            return ApiFactory.getAccountRestApi(this.serviceGenerator).reObtainAccount(oldAccount, innerAuthCode);
        }

        @Override
        public Observable<Response<Void>> customaAccount(@NonNull String account,
                                                   @NonNull String innerAuthCode,
                                                   @NonNull String customizeAccount) {
            Map<String, String> body = new HashMap<>();
            body.put("account", account);
            body.put("innerAuthCode", innerAuthCode);
            body.put("customizeAccount", customizeAccount);

            return ApiFactory.getAccountRestApi(this.serviceGenerator).customaAccount(body);
        }

        @Override
        public Observable<Response<Void>> modifyAccount(@NonNull String oldAccount,
                                                  @NonNull String newAccount,
                                                  @NonNull String innerAuthCode) {
            Map<String, String> body = new HashMap<>();
            body.put("oldAccount", oldAccount);
            body.put("newAccount", newAccount);
            body.put("innerAuthCode", innerAuthCode);

            return ApiFactory.getAccountRestApi(this.serviceGenerator).modifyAccount(body);
        }

        @Override
        public Observable<Response<Map<String, String>>> obtainBindMobileAuthCode(@NonNull String account,
                                                                                  @NonNull String mobile) {
            return ApiFactory.getAccountRestApi(this.serviceGenerator).obtainBindMobileAuthCode(account, mobile);
        }

        @Override
        public Observable<Response<MultiResult<String>>> bindMobile(@NonNull String account,
                                                                    @NonNull String authCode,
                                                                    @NonNull String innerAuthCode,
                                                                    @NonNull String mobile) {

            Map<String, String> body = new HashMap<>();
            body.put("account", account);
            body.put("authCode", authCode);
            body.put("innerAuthCode", innerAuthCode);
            body.put("mobile", mobile);

            return ApiFactory.getAccountRestApi(this.serviceGenerator).bindMobile(body);
        }

        @Override
        public Observable<Response<Void>> forceBindMobile(@NonNull String account,
                                                    @NonNull String innerAuthCode,
                                                    @NonNull String mobile) {
            Map<String, String> body = new HashMap<>();
            body.put("account", account);
            body.put("innerAuthCode", innerAuthCode);
            body.put("mobile", mobile);

            return ApiFactory.getAccountRestApi(this.serviceGenerator).forceBindMobile(body);

        }

        @SuppressWarnings("MethodWithTooManyParameters")
        @Override
        public Observable<Response<MultiResult<Object>>> accountPwdLogin(@NonNull String account,
                                                                         @NonNull String pwd,
                                                                         int clinetType,
                                                                         int loginType,
                                                                         @NonNull String deviceModel,
                                                                         @NonNull String osName,
                                                                         @NonNull String osVersion,
                                                                         @NonNull String clientVersion,
                                                                         @NonNull String clientResource,
                                                                         @NonNull String pnToken,
                                                                         @NonNull String imei) {
            Map<String, Object> body = new HashMap<>();
            body.put("account", account);
            body.put("pwd", pwd);
            body.put("clientType", clinetType);
            body.put("loginType", loginType);
            body.put("clientModel", deviceModel);
            body.put("osName", osName);
            body.put("osVersion", osVersion);
            body.put("clientVersion", clientVersion);
            body.put("resource", clientResource);
            body.put("pnToken", pnToken);
            if (CustInfo.isTelcom()) body.put("imei", imei);
            return ApiFactory.getAccountRestApi(this.serviceGenerator).accountPwdLogin(body);
        }

        @Override
        public Observable<Response<Map<String, String>>> obtainLoginAuthCode(@NonNull String mobile) {
            return ApiFactory.getAccountRestApi(this.serviceGenerator).obtainLoginAuthCode(mobile);
        }
        @Override
        public Observable<Response<Map<String,String>>> obtainResetAuthCode(@NonNull String mobile){
            return ApiFactory.getAccountRestApi(this.serviceGenerator).obtainResetAuthCode(mobile);
        }


        @SuppressWarnings("MethodWithTooManyParameters")
        @Override
        public Observable<Response<MultiResult<Object>>> mobileLogin(@NonNull String mobile,
                                                                     @NonNull String authCode,
                                                                     @NonNull String innerAuthCode,
                                                                     int clinetType,
                                                                     int loginType,
                                                                     @NonNull String deviceModel,
                                                                     @NonNull String osName,
                                                                     @NonNull String osVersion,
                                                                     @NonNull String clientVersion,
                                                                     @NonNull String clientResource,
                                                                     @NonNull String pnToken,
                                                                     @NonNull String imei) {

            Map<String, Object> body = new HashMap<>();
            body.put("mobile", mobile);
            body.put("authCode", authCode);
            body.put("innerAuthCode", innerAuthCode);

            body.put("clientType", clinetType);
            body.put("loginType", loginType);
            body.put("clientModel", deviceModel);
            body.put("osName", osName);
            body.put("osVersion", osVersion);
            body.put("clientVersion", clientVersion);
            body.put("resource", clientResource);
            body.put("pnToken", pnToken);
            if (CustInfo.isTelcom()) body.put("imei", imei);//866240028008543
            return ApiFactory.getAccountRestApi(this.serviceGenerator).mobileLogin(body);
        }

        /**
         * 保存上次登录使用的账号或手机号
         *
         */
        @SuppressWarnings("ReturnOfNull")
        @Override
        public Observable<Boolean> savaLoginData(@NonNull String accountOrMobile) {
            return null;
        }
    }


    public static class PostAccountCloudStore extends CloudStore implements AccountStore.PostAccountStore {

        @Inject
        public PostAccountCloudStore(@ConnSecuritySpe(DiConfig.CONN_HTTPS_TICKET)
                                     ServiceGenerator serviceGenerator) {
            super(serviceGenerator);
        }

        @Override
        public Observable<Response<Map<String, Object>>> refreshTicket(@NonNull String oldTicket,
                                                                       @NonNull String pnToken,
                                                                                int clientType) {
            Map<String, Object> body = new HashMap<>();
            body.put("oldTicket", oldTicket);
            body.put("pnToken", pnToken);
            body.put("clientType", clientType);
            return ApiFactory.getAccountRestApi(this.serviceGenerator).refreshTicket(body);
        }

        @Override
        public Observable<Response<Void>> obtainBindAuthCode(@NonNull String mobile) {
            return ApiFactory.getAccountRestApi(this.serviceGenerator).obtainBindAuthCode(mobile);
        }

        @Override
        public Observable<Response<Void>> obtainModifyAuthCode(@NonNull String mobile) {
            return ApiFactory.getAccountRestApi(this.serviceGenerator).obtainModifyAuthCode(mobile);
        }

        @Override
        public Observable<Response<Void>> obtainModifyMobile(@NonNull String mobile, @NonNull String authCode) {
            Map<String,String> body = new HashMap<>();
            body.put("mobile",mobile);
            body.put("authCode",authCode);
            return ApiFactory.getAccountRestApi(this.serviceGenerator).obtainModifyMobile(body);
        }

        @Override
        public Observable<Response<Void>> ticketBindMobile(@NonNull String authCode, @NonNull String mobile) {

            Map<String, String> body = new HashMap<>();
            body.put("authCode", authCode);
            body.put("mobile", mobile);

            return ApiFactory.getAccountRestApi(this.serviceGenerator).ticketBindMobile(body);
        }

        @Override
        public Observable<Response<Void>> ticketForceBindMobile(@NonNull String mobile) {
            Map<String, String> body = new HashMap<>();
            body.put("mobile", mobile);

            return ApiFactory.getAccountRestApi(this.serviceGenerator).ticketForceBindMobile(body);
        }

        @Override
        public Observable<Response<Void>> ticketCustomAccount(@NonNull String customizeAccount) {
            Map<String, String> body = new HashMap<>();
            body.put("customizeAccount", customizeAccount);

            return ApiFactory.getAccountRestApi(this.serviceGenerator).ticketCustomAccount(body);
        }

        @Override
        public Observable<Response<Void>> unbindMobile(@NonNull String mobile) {
            return ApiFactory.getAccountRestApi(this.serviceGenerator).unBindMobile(mobile);
        }

        /*[S]add by tangsha for third encrypt send secKey info*/

        public static String SEND_THIRDEN_TOPIC = "topic";
        public static String SEND_THIRDEN_DEST = "dstAccount";
        public static String SEND_THIRDEN_CONTENT = "msg";
        @Override
        public Observable<Response<Void>> sendThirdEnPushInfo(@NonNull String topic, @NonNull ArrayList<String> destAccount, @NonNull String content) {
            Map<String, Object> body = new HashMap<>();
            body.put(SEND_THIRDEN_TOPIC, topic);
            body.put(SEND_THIRDEN_DEST,destAccount);
            body.put(SEND_THIRDEN_CONTENT,content);
            Log.d("AccoutCloudStore","sendThirdEnPushInfo "+body);
            return ApiFactory
                    .getAccountRestApi(this.serviceGenerator).sendThirdEnPushInfo(body);
        }
        /*[E]add by tangsha for third encrypt send secKey info*/
    }

}
