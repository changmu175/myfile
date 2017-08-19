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
public interface DeviceAuthRepository extends Repository {

    /**
     * 获取授信设备需要的验证码
     *
     * @param account 帐号
     * @param mobile  手机号
     * @return 返回结果
     */
    Observable<Map<String, String>> obtainDeviceAuthrizeAuthCode(@NonNull String account,
                                                                 @NonNull String mobile);

    /**
     * 通过好友手机号授信设备
     *
     * @param account       帐号
     * @param innerAuthCode 内部验证码
     * @param mobiles       好友的手机号
     * @return 返回结果
     */
    Observable<MultiResult<Object>> checkFriendMobiles(@NonNull String account,
                                                       @NonNull String innerAuthCode,
                                                       @NonNull List<String> mobiles);

    /**
     * 通过验证短信验证码授信设备
     *
     * @param account       帐号
     * @param mobile        手机号
     * @param authCode      短信验证码
     * @param innerAuthCode 内部验证码
     * @return 返回结果
     */
    Observable<Void> checkMobile(@NonNull String account,
                           @NonNull String mobile,
                           @NonNull String authCode,
                           @NonNull String innerAuthCode);


    /**
     * 重新获取设备授权需要的信息
     *
     * @param account       帐号
     * @param innerAuthCode 内部验证码
     * @param authorizeId   授权ID
     * @return 返回结果
     */
    Observable<Map<String, String>> reObtaionAuthInfo(@NonNull String account,
                                                      @NonNull String innerAuthCode,
                                                      @NonNull String authorizeId);

    interface PostDeviceAuthRepository {
        /**
         * 获取设备授信所需要的信息
         *
         * @param authorizeId 授权ID
         * @return 返回结果
         */
        Observable<Map<String, String>> obtainAuthInfo(@NonNull String authorizeId);


        /**
         * 授权设备
         *
         * @param authorizeId 授权ID
         * @return 返回结果
         */
        Observable<Void> authDevice(@NonNull String authorizeId,@NonNull String cardNo);

        /*[S]modify by xienana@2016/08/31 to fix bug 3363 [review by] tangsha*/
        Observable<Map<String,Object>> getAuthInfo(@NonNull String authorizeId);
	   /*[E]modify by xienana@2016/08/31 to fix bug 3363 [review by] tangsha*/
    }
}
