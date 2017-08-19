package com.xdja.data_mainframe.repository.datastore;

import android.support.annotation.NonNull;

import com.xdja.data_mainframe.rest.ApiFactory;
import com.xdja.dependence.annotations.ConnSecuritySpe;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.frame.data.net.ServiceGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

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
public class DeviceAuthCloudStore extends CloudStore implements DeviceAuthStore {

    @Inject
    public DeviceAuthCloudStore(@ConnSecuritySpe(DiConfig.CONN_HTTPS_DEF) ServiceGenerator serviceGenerator) {
        super(serviceGenerator);
    }


    @Override
    public Observable<Response<Map<String, String>>> obtainDeviceAuthrizeAuthCode(@NonNull String account,
                                                                                  @NonNull String mobile) {
        return ApiFactory
                .getDeviceAuthRestApi(this.serviceGenerator)
                .obtainDeviceAuthrizeAuthCode(account, mobile);
    }

    @Override
    public Observable<Response<MultiResult<Object>>> checkFriendMobiles(@NonNull String account,
                                                                        @NonNull String innerAuthCode,
                                                                        @NonNull List<String> mobiles,
                                                                        @NonNull String deviceModel) {

        Map<String, Object> body = new HashMap<>();
        body.put("account", account);
        body.put("innerAuthCode", innerAuthCode);
        body.put("mobiles", mobiles);
        body.put("deviceName", deviceModel);

        return ApiFactory
                .getDeviceAuthRestApi(this.serviceGenerator)
                .checkFriendMobiles(body);
    }

    @Override
    public Observable<Response<Void>> checkMobile(@NonNull String account,
                                            @NonNull String mobile,
                                            @NonNull String authCode,
                                            @NonNull String innerAuthCode,
                                            @NonNull String deviceModel) {
        Map<String, String> body = new HashMap<>();
        body.put("account", account);
        body.put("mobile", mobile);
        body.put("authCode", authCode);
        body.put("innerAuthCode", innerAuthCode);
        body.put("deviceName", deviceModel);

        return ApiFactory
                .getDeviceAuthRestApi(this.serviceGenerator)
                .checkMobile(body);
    }



    @Override
    public Observable<Response<Map<String, String>>> reObtaionAuthInfo(@NonNull String account,
                                                                       @NonNull String innerAuthCode,
                                                                       @NonNull String pnToken,
                                                                       @NonNull String deviceName) {
        Map<String, String> body = new HashMap<>();
        body.put("account", account);
        body.put("innerAuthCode", innerAuthCode);
        body.put("pnToken", pnToken);
        body.put("deviceName", deviceName);
        return ApiFactory
                .getDeviceAuthRestApi(this.serviceGenerator)
                .reObtaionAuthInfo(body);
    }

    public static class PostDeviceAuthCloudStore extends CloudStore implements DeviceAuthStore.PostDeviceAuthStore {

        @Inject
        public PostDeviceAuthCloudStore(@ConnSecuritySpe(DiConfig.CONN_HTTPS_TICKET)
                                        ServiceGenerator serviceGenerator) {
            super(serviceGenerator);
        }

        @Override
        public Observable<Response<Map<String, String>>> obtainAuthInfo(@NonNull String authorizeId) {
            return ApiFactory
                    .getDeviceAuthRestApi(this.serviceGenerator)
                    .obtainAuthInfo(authorizeId);
        }

        public Observable<Response<Void>> authDevice(@NonNull String authorizeId, @NonNull String deviceId) {

            Map<String, String> body = new HashMap<>();
            body.put("authorizeId", authorizeId);
            body.put("cardNo", deviceId);

            return ApiFactory
                    .getDeviceAuthRestApi(this.serviceGenerator)
                    .authDevice(body);
        }
    }

}
