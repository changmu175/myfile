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
public interface DeviceAuthStore {

    /**
     * 获取授信设备需要的验证码
     *
     * @param account 帐号
     * @param mobile  手机号
     * @return http响应
     */
    Observable<Response<Map<String, String>>> obtainDeviceAuthrizeAuthCode(@NonNull String account,
                                                                           @NonNull String mobile);

    /**
     * 通过好友手机号授信设备
     *
     * @param account       帐号
     * @param innerAuthCode 内部验证码
     * @param mobiles       好友的手机号
     * @return http响应
     */
    Observable<Response<MultiResult<Object>>> checkFriendMobiles(@NonNull String account,
                                                                 @NonNull String innerAuthCode,
                                                                 @NonNull List<String> mobiles,
                                                                 @NonNull String deviceModel);

    /**
     * 通过验证短信验证码授信设备
     *
     * @param account       帐号
     * @param mobile        手机号
     * @param authCode      短信验证码
     * @param innerAuthCode 内部验证码
     * @return http响应
     */
    Observable<Response<Void>> checkMobile(@NonNull String account,
                                           @NonNull String mobile,
                                           @NonNull String authCode,
                                           @NonNull String innerAuthCode,
                                           @NonNull String deviceModel);


    /**
     * 重新获取设备授权需要的信息
     *
     * @param account       帐号
     * @param innerAuthCode 内部验证码
     * @param pnToken       PN标识
     * @return http响应
     */
    Observable<Response<Map<String, String>>> reObtaionAuthInfo(@NonNull String account,
                                                                @NonNull String innerAuthCode,
                                                                @NonNull String pnToken,
                                                                @NonNull String deviceName);

    interface PostDeviceAuthStore {
        /**
         * 获取设备授信所需要的信息
         *
         * @param authorizeId 授权ID
         * @return http响应
         */
        Observable<Response<Map<String, String>>> obtainAuthInfo(@NonNull String authorizeId);

        /**
         * 授权设备
         *
         * @param authorizeId 授权ID
         * @return http响应
         */
        Observable<Response<Void>> authDevice(@NonNull String authorizeId, @NonNull String deviceId);

    }
}
