package com.xdja.domain_mainframe.repository;

import android.support.annotation.NonNull;

import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.frame.domain.repository.Repository;

import java.util.List;
import java.util.Map;

import rx.Observable;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.domain_mainframe.repository</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/22</p>
 * <p>Time:14:00</p>
 */
public interface PwdRepository extends Repository {

    /**
     * 校验重置密码验证码
     *
     * @param mobile        手机号
     * @param authCode      短信验证码
     * @param innerAuthCode 内部验证码
     * @return 服务器返回结果
     */
    Observable<Map<String, String>> checkRestPwdAuthCode(@NonNull String mobile,
                                                         @NonNull String authCode,
                                                         @NonNull String innerAuthCode);

    /**
     * 通过验证码重置密码
     *
     * @param mobile        手机号
     * @param innerAuthCode 内部验证码
     * @param passwd        密码
     * @return 服务器返回结果
     */
    Observable<Void> restPwdByAuthCode(@NonNull String mobile,
                                 @NonNull String innerAuthCode,
                                 @NonNull String passwd);


    /**
     * 通过好友手机号重置密码
     *
     * @param account       帐号
     * @param innerAuthCode
     * @param passwd        密码  @return 服务器返回结果
     */
    Observable<Void> restPwdByFriendMobiles(@NonNull String account, String innerAuthCode, String passwd);

    /**
     * 验证好友手机号
     *
     * @param account 帐号
     * @param mobiles 好友手机号列表
     * @return 服务器返回结果
     */
    Observable<MultiResult<Object>> authFriendPhone(@NonNull String account, List<String> mobiles);
}
