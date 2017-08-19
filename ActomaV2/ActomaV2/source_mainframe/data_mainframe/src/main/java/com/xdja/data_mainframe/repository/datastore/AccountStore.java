package com.xdja.data_mainframe.repository.datastore;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xdja.comm.bean.DataMigrationAccountBean;
import com.xdja.domain_mainframe.model.MultiResult;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Response;
import rx.Observable;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.data_mainframe.repository.datastore</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/14</p>
 * <p>Time:15:35</p>
 */
public interface AccountStore {

    /**
     * 登录成功之前账户相关业务
     */
    interface PreAccountStore {
        /**
         * 判断新老账号
         * @return
         */
        Observable<Response<MultiResult<Object>>> migrateOldAccount(@Nullable String account, @Nullable String passwd, @Nullable String deviceId);

        /**
         * 判断新老账号
         * @return
         */
        Observable<DataMigrationAccountBean> isNewAccount();

        /**
         * 注册一个新帐号
         *
         * @param nickName    昵称
         * @param password    密码
         * @param avatarId    头像文件Id
         * @param thumbnailId 头像缩略图Id
         * @return Http响应
         */
        Observable<Response<MultiResult<String>>> registAccount(@Nullable String nickName,
                                                                @NonNull String password,
                                                                @Nullable String avatarId,
                                                                @Nullable String thumbnailId,
                                                                @Nullable String deviceId);

        /**
         * 重新获取帐号
         *
         * @param oldAccount    旧帐号
         * @param innerAuthCode 内部验证码
         * @return Http响应
         */
        Observable<Response<MultiResult<Object>>> reObtainAccount(@NonNull String oldAccount,
                                                                  @NonNull String innerAuthCode
        );

        /**
         * 自定义帐号
         *
         * @param account          原帐号
         * @param innerAuthCode    内部验证码
         * @param customizeAccount 自定义的帐号
         * @return Http响应
         */
        Observable<Response<Void>> customaAccount(@NonNull String account,
                                                  @NonNull String innerAuthCode,
                                                  @NonNull String customizeAccount);

        /**
         * 用户切换帐号后确认新生成的帐号
         *
         * @param oldAccount    旧帐号
         * @param newAccount    新帐号
         * @param innerAuthCode 内部验证码
         * @return Http响应
         */
        Observable<Response<Void>> modifyAccount(@NonNull String oldAccount,
                                                 @NonNull String newAccount,
                                                 @NonNull String innerAuthCode);

        /**
         * 获取绑定手机所需的验证码
         *
         * @param account 帐号
         * @param mobile  手机号
         * @return Http响应
         */
        Observable<Response<Map<String, String>>> obtainBindMobileAuthCode(@NonNull String account,
                                                                           @NonNull String mobile);


        /**
         * 绑定手机号到账号
         *
         * @param account       帐号
         * @param authCode      短信验证码
         * @param innerAuthCode 内部验证码
         * @param mobile        手机号
         * @return Http响应
         */
        Observable<Response<MultiResult<String>>> bindMobile(@NonNull String account,
                                                             @NonNull String authCode,
                                                             @NonNull String innerAuthCode,
                                                             @NonNull String mobile);

        /**
         * 强制绑定手机号到账号
         *
         * @param account       帐号
         * @param innerAuthCode 内部验证码
         * @param mobile        手机号
         * @return Http响应
         */
        Observable<Response<Void>> forceBindMobile(@NonNull String account,
                                                   @NonNull String innerAuthCode,
                                                   @NonNull String mobile);

        /**
         * 帐号和密码登录
         *
         * @param account 帐号
         * @param pwd     密码
         * @return http响应
         */
        @SuppressWarnings("MethodWithTooManyParameters")
        Observable<Response<MultiResult<Object>>> accountPwdLogin(@NonNull String account,
                                                                  @NonNull String pwd,
                                                                  int clinetType,
                                                                  int loginType,
                                                                  @NonNull String deviceModel,
                                                                  @NonNull String osName,
                                                                  @NonNull String osVersion,
                                                                  @NonNull String clientVersion,
                                                                  @NonNull String clientResource,
                                                                  @NonNull String pnToken,
                                                                  @NonNull String imei);

        /**
         * 获取登录密码验证码
         *
         * @param mobile 手机号
         * @return http响应
         */
        Observable<Response<Map<String, String>>> obtainLoginAuthCode(@NonNull String mobile);

        /**
         * 获取重置密码验证码
         *
         * @param mobile 手机号
         * @return http响应
         */
        Observable<Response<Map<String, String>>> obtainResetAuthCode(@NonNull String mobile);

        /**
         * 手机号登录
         *
         * @param mobile        手机号
         * @param authCode      短信验证码
         * @param innerAuthCode 内部验证码
         * @return http响应
         */
        @SuppressWarnings("MethodWithTooManyParameters")
        Observable<Response<MultiResult<Object>>> mobileLogin(@NonNull String mobile,
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
                                                              @NonNull String imei);

        /**
         * 保存上次登录使用的账号或手机号
         */
        Observable<Boolean> savaLoginData(@NonNull String accountOrMobile);
    }

    /**
     * 登录成功账户相关业务
     */
    interface PostAccountStore {

        /**
         * 刷新Ticket
         *
         * @param oldTicket 老Ticket
         * @return http响应
         */
        Observable<Response<Map<String, Object>>> refreshTicket(@NonNull String oldTicket,
                                                                @NonNull String pnToken,
                                                                         int clientType);

        /**
         * 获取绑定所有的验证码
         *
         * @param mobile 手机号
         * @return http响应
         */
        Observable<Response<Void>> obtainBindAuthCode(@NonNull String mobile);

        /**
         * 获取更换手机号验证码
         *
         * @param mobile 手机号
         * @return http响应
         */
        Observable<Response<Void>> obtainModifyAuthCode(@NonNull String mobile);

        /**
         * 更换手机号
         *
         * @param mobile   新手机号
         * @param authCode 手机验证码
         * @return
         */
        Observable<Response<Void>> obtainModifyMobile(@NonNull String mobile, @NonNull String authCode);

        /**
         * 绑定手机号到账号（Ticket）
         *
         * @param authCode 短信验证码
         * @param mobile   手机号
         * @return http响应
         */
        Observable<Response<Void>> ticketBindMobile(@NonNull String authCode, @NonNull String mobile);

        /**
         * 强制绑定手机号到账号（Ticket）
         *
         * @param mobile 手机号
         * @return http响应
         */
        Observable<Response<Void>> ticketForceBindMobile(@NonNull String mobile);

        /**
         * 自定义帐号（Ticket）
         *
         * @param customizeAccount 用户自定义的帐号
         * @return http响应
         */
        Observable<Response<Void>> ticketCustomAccount(@NonNull String customizeAccount);

        /**
         * 解绑手机号
         *
         * @param mobile 手机号
         * @return http响应
         */
        Observable<Response<Void>> unbindMobile(@NonNull String mobile);

        /*[S]add by tangsha for third encrypt send secKey info*/
        Observable<Response<Void>> sendThirdEnPushInfo(@NonNull String topic, @NonNull ArrayList<String> destAccount, @NonNull String content);
         /*[E]add by tangsha for third encrypt send secKey info*/
    }

}
