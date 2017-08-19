package com.xdja.data_mainframe.repository.datastore;

import android.support.annotation.NonNull;

import com.xdja.domain_mainframe.model.MultiResult;

import java.util.List;
import java.util.Map;

import retrofit2.Response;
import rx.Observable;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.data_mainframe.repository.datastore</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/21</p>
 * <p>Time:20:19</p>
 */
public interface PwdStore {

    /**
     * 校验重置密码验证码
     *
     * @param mobile        手机号
     * @param authCode      短信验证码
     * @param innerAuthCode 内部验证码
     * @return http响应
     */
    Observable<Response<Map<String, String>>> checkRestPwdAuthCode(@NonNull String mobile,
                                                                   @NonNull String authCode,
                                                                   @NonNull String innerAuthCode);

    /**
     * 通过验证码重置密码
     *
     * @param mobile        手机号
     * @param innerAuthCode 内部验证码
     * @param passwd        密码
     * @return http响应
     */
    Observable<Response<Void>> restPwdByAuthCode(@NonNull String mobile,
                                                   @NonNull String innerAuthCode,
                                                   @NonNull String passwd);

    /**
     * 通过好友手机号重置密码
     *
     * @param account       帐号
     * @param innerAuthCode 内部验证码
     * @param passwd        密码
     * @return http响应
     */
    Observable<Response<Void>> restPwdByFriendMobiles(@NonNull String account,
                                                        @NonNull String innerAuthCode,
                                                        @NonNull String passwd);

    Observable<Response<MultiResult<Object>>> authFriendPhone(@NonNull String account,
                                                                        @NonNull List<String> mobiles);
}
