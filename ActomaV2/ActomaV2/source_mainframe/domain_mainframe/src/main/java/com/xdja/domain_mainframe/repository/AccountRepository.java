package com.xdja.domain_mainframe.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xdja.comm.bean.DataMigrationAccountBean;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.frame.domain.repository.Repository;

import java.util.ArrayList;
import java.util.Map;

import rx.Observable;

/**
 * <p>Summary:帐号相关操作接口仓库</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.domain_mainframe.repository</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/14</p>
 * <p>Time:15:17</p>
 */
public interface AccountRepository {

    interface PreAccountRepository extends Repository{
        /**
         * 完成数据迁移
         * @param account  账号
         * @param passwd   密码
         * @return
         */
        Observable<MultiResult<Object>> migrateOldAccount(String account, String passwd);

        /**
         * 判断是否新账号
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
         * @return 服务器返回结果
         */
        Observable<MultiResult<String>> registAccount(@Nullable String nickName,
                                                      @NonNull String password,
                                                      @Nullable String avatarId,
                                                      @Nullable String thumbnailId);

        /**
         * 重新获取账号
         *
         * @param oldAccount    老账号
         * @param innerAuthCode 内部验证码
         * @return 服务器返回结果
         */
        Observable<MultiResult<Object>> reObtainAccount(@NonNull String oldAccount,
                                                        @NonNull String innerAuthCode);

        /**
         * 自定义帐号
         *
         * @param account          原帐号
         * @param innerAuthCode    内部验证码
         * @param customizeAccount 自定义的帐号
         * @return 服务器返回结果
         */
        Observable<Void> customAccount(@NonNull String account,
                                 @NonNull String innerAuthCode,
                                 @NonNull String customizeAccount);

        /**
         * 用户切换帐号后确认新生成的帐号
         *
         * @param oldAccount    旧帐号
         * @param newAccount    新帐号
         * @param innerAuthCode 内部验证码
         * @return 服务器返回结果
         */
        Observable<Void> modifyAccount(@NonNull String oldAccount,
                                 @NonNull String newAccount,
                                 @NonNull String innerAuthCode);

        /**
         * 获取绑定手机所需的验证码
         *
         * @param account 帐号
         * @param mobile  手机号
         * @return 服务器返回结果（内部验证码）
         */
        Observable<Map<String, String>> obtainBindMobileAuthCode(@NonNull String account,
                                                                 @NonNull String mobile);

        /**
         * 绑定手机号到账号
         *
         * @param account       帐号
         * @param authCode      短信验证码
         * @param innerAuthCode 内部验证码
         * @param mobile        手机号
         * @return 服务器返回结果
         */
        Observable<MultiResult<String>> bindMobile(@NonNull String account,
                                                   @NonNull String authCode,
                                                   @NonNull String innerAuthCode,
                                                   @NonNull String mobile);

        /**
         * 强制绑定手机号到账号
         *
         * @param account       帐号
         * @param innerAuthCode 内部验证码
         * @param mobile        手机号
         * @return 服务器返回结果
         */
        Observable<Void> forceBindMobile(@NonNull String account,
                                   @NonNull String innerAuthCode,
                                   @NonNull String mobile);

        /**
         * 帐号和密码登录
         *
         * @param account 帐号
         * @param pwd     密码
         * @return 服务器返回结果
         */
        Observable<MultiResult<Object>> accountPwdLogin(@NonNull String account,
                                                        @NonNull String pwd);

        /**
         * 获取登录密码验证码
         *
         * @param mobile 手机号
         * @return 服务器返回的结果
         */
        Observable<Map<String, String>> obtainLoginAuthCode(@NonNull String mobile);

        /**
         * 获取重置密码验证码
         * @param mobile
         * @return 服务器返回结果
         */
        Observable<Map<String,String>> obtainResetAuthCode(@NonNull String mobile);

        /**
         * 手机号登录
         *
         * @param mobile        手机号
         * @param authCode      短信验证码
         * @param innerAuthCode 内部验证码
         * @return 服务器返回结果
         */
        Observable<MultiResult<Object>> mobileLogin(@NonNull String mobile,
                                                    @NonNull String authCode,
                                                    @NonNull String innerAuthCode);
    }

    interface  PostAccountRepository extends Repository{

        /**
         *设置手机号
         */
        Observable<Void> setMobile(@NonNull String mobile);

        /**
         * 刷新Ticket
         *
         * @param ticket 老Ticket
         * @return 服务器返回的结果
         */
        Observable<Map<String,Object>> refreshTicket(@NonNull String ticket);

        /**
         * 获取绑定或更换手机号所有的验证码（Ticket）
         *
         * @param mobile 手机号
         * @return 服务器返回的结果
         */
        Observable<Void> obtainBindAuthCode(@NonNull String mobile);

        /**
         * 获取更换手机号验证码
         * @param mobile
         * @return
         */
        Observable<Void> obtainModifyAuthCode(@NonNull String mobile);

        /**
         * 更换手机号
         * @param mobile 新手机号
         * @param authCode 手机验证码
         * @return
         */
        Observable<Void> obtainModifyMobile(@NonNull String mobile,@NonNull String authCode);

        /**
         * 绑定手机号到账号（Ticket）
         *
         * @param authCode 短信验证码
         * @param mobile   手机号
         * @return 服务器返回的结果
         */
        Observable<Void> ticketBindMobile(@NonNull String authCode, @NonNull String mobile);

        /**
         * 强制绑定手机号到账号（Ticket）
         *
         * @param mobile 手机号
         * @return 服务器返回的结果
         */
        Observable<Void> ticketForceBindMobile(@NonNull String mobile);

        /**
         * 自定义帐号（Ticket）
         *
         * @param customizeAccount 用户自定义的帐号
         * @return 服务器返回的结果
         */
        Observable<Void> ticketCustomAccount(@NonNull String customizeAccount);

        /**
         * 解绑手机号
         * @param mobile 手机号
         * @return 服务器返回的结果
         */
        Observable<Void> unbindMobile(@NonNull String mobile);

        /*[S]add by tangsha for third encrypt send secKey info*/
        Observable<Void> sendThirdEnPushInfo(String topic, ArrayList<String> destAccount, String content);
         /*[E]add by tangsha for third encrypt send secKey info*/
    }
}
