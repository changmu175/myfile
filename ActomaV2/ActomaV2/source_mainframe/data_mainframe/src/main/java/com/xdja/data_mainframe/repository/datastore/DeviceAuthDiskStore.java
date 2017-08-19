package com.xdja.data_mainframe.repository.datastore;

import android.support.annotation.NonNull;

import com.xdja.domain_mainframe.model.MultiResult;

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
public class DeviceAuthDiskStore implements DeviceAuthStore, DeviceAuthStore.PostDeviceAuthStore {

    @Inject
    DeviceAuthDiskStore() {
    }

    @SuppressWarnings("ReturnOfNull")
    @Override
    public Observable<Response<Map<String, String>>> obtainDeviceAuthrizeAuthCode(@NonNull String account, @NonNull String mobile) {
        return null;
    }

    @SuppressWarnings("ReturnOfNull")
    @Override
    public Observable<Response<MultiResult<Object>>> checkFriendMobiles(@NonNull String account, @NonNull String innerAuthCode, @NonNull List<String> mobiles, @NonNull String deviceModel) {
        return null;
    }

    @SuppressWarnings("ReturnOfNull")
    @Override
    public Observable<Response<Void>> checkMobile(@NonNull String account, @NonNull String mobile, @NonNull String authCode, @NonNull String innerAuthCode, @NonNull String deviceModel) {
        return null;
    }

    @SuppressWarnings("ReturnOfNull")
    @Override
    public Observable<Response<Map<String, String>>> obtainAuthInfo(@NonNull String authorizeId) {
        return null;
    }

    @SuppressWarnings("ReturnOfNull")
    @Override
    public Observable<Response<Map<String, String>>> reObtaionAuthInfo(@NonNull String account,
                                                                       @NonNull String innerAuthCode,
                                                                       @NonNull String pnToken,
                                                                       @NonNull String deviceName) {
        return null;
    }

    @SuppressWarnings("ReturnOfNull")
    @Override
    public Observable<Response<Void>> authDevice(@NonNull String authorizeId, @NonNull String deviceId) {
        return null;
    }
}
