package com.xdja.data_mainframe.repository;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.xdja.data_mainframe.di.CacheModule;
import com.xdja.data_mainframe.di.PreRepositoryModule;
import com.xdja.data_mainframe.repository.datastore.DeviceAuthStore;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.dependence.annotations.StoreSpe;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.domain_mainframe.repository.DeviceAuthRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import rx.Observable;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.data_mainframe.repository.datastore</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/22</p>
 * <p>Time:14:25</p>
 */
public class DeviceAuthRepImp extends ExtRepositoryImp<DeviceAuthStore> implements DeviceAuthRepository {

    private Map<String, Provider<String>> stringMap;
    private Map<String, Provider<Integer>> integerMap;

    @SuppressWarnings("ConstructorWithTooManyParameters")
    @Inject
    public DeviceAuthRepImp(@StoreSpe(DiConfig.TYPE_DISK) DeviceAuthStore diskStore,
                            @StoreSpe(DiConfig.TYPE_CLOUD) DeviceAuthStore cloudStore,
                            @Named(PreRepositoryModule.NAMED_ERRORSTATUS) Set<Integer> errorStatus,
                            Gson gson,
                            Map<String, Provider<String>> stringMap,
                            Map<String, Provider<Integer>> integerMap) {
        super(diskStore, cloudStore, errorStatus, gson);
        this.stringMap = stringMap;
        this.integerMap = integerMap;
    }

    @Override
    public Observable<Map<String, String>> obtainDeviceAuthrizeAuthCode(@NonNull String account,
                                                                        @NonNull String mobile) {
        return this.cloudStore.obtainDeviceAuthrizeAuthCode(account, mobile)
                .map(
                        new ResponseFunc1<Map<String, String>>(errorStatus, gson)
                                .setCustomStatus(new HashSet<>(Arrays.asList(404, 406, 409)))
                );
    }

    @Override
    public Observable<MultiResult<Object>> checkFriendMobiles(@NonNull String account,
                                                              @NonNull String innerAuthCode,
                                                              @NonNull List<String> mobiles) {
        return this.cloudStore.checkFriendMobiles(
                account,
                innerAuthCode,
                mobiles,
                stringMap.get(CacheModule.KEY_DEVICE_MODEL).get())
                .map(
                        new ResponseFunc1<MultiResult<Object>>(errorStatus, gson)
                                .setCustomStatus(new HashSet<>(Arrays.asList(401, 406)))
                );
    }

    @Override
    public Observable<Void> checkMobile(@NonNull String account,
                                  @NonNull String mobile,
                                  @NonNull String authCode,
                                  @NonNull String innerAuthCode) {
        return this.cloudStore.checkMobile(account,
                mobile,
                authCode,
                innerAuthCode,
                stringMap.get(CacheModule.KEY_DEVICE_MODEL).get())
                .map(
                        new ResponseFunc1<Void>(errorStatus, gson)
                                .setCustomStatus(new HashSet<>(Arrays.asList(401, 406)))
                );
    }


    @Override
    public Observable<Map<String, String>> reObtaionAuthInfo(@NonNull String account,
                                                             @NonNull String innerAuthCode,
                                                             @NonNull String authorizeId) {
        //ldy 2016-6-1 网络请求参数变化，删除authorizeId，增加pnToken，deviceName
        return this.cloudStore.reObtaionAuthInfo(account, innerAuthCode,
                stringMap.get(CacheModule.KEY_PN_TOKEN).get(),
                stringMap.get(CacheModule.KEY_DEVICE_MODEL).get()
                )
                .map(
                        new ResponseFunc1<Map<String, String>>(errorStatus, gson)
                                .setCustomStatus(new HashSet<>(Arrays.asList(401, 406, 404)))
                );
    }

    public static class PostDeviceAuthRepImp extends ExtRepositoryImp<DeviceAuthStore.PostDeviceAuthStore> implements DeviceAuthRepository.PostDeviceAuthRepository {

        private Map<String, Provider<String>> stringMap;
        private Map<String, Provider<Integer>> integerMap;

        @SuppressWarnings({"UnusedParameters", "ConstructorWithTooManyParameters"})
        @Inject
        public PostDeviceAuthRepImp(@StoreSpe(DiConfig.TYPE_DISK) DeviceAuthStore.PostDeviceAuthStore diskStore,
                                    @StoreSpe(DiConfig.TYPE_CLOUD) DeviceAuthStore.PostDeviceAuthStore cloudStore,
                                    @Named(PreRepositoryModule.NAMED_ERRORSTATUS) Set<Integer> errorStatus,
                                    Gson gson,
                                    Map<String, Provider<String>> stringMap,
                                    Map<String, Provider<Integer>> integerMap) {
            super(diskStore, cloudStore, errorStatus, gson);
        }

        @Override
        public Observable<Map<String, String>> obtainAuthInfo(@NonNull String authorizeId) {
            return this.cloudStore.obtainAuthInfo(authorizeId)
                    .map(
                            new ResponseFunc1<Map<String, String>>(errorStatus, gson)
                                    .setCustomStatus(new HashSet<>(Arrays.asList(401, 406)))
                    );
        }

        /**
         * 先获取授信信息,再为设备授信
         * @param authorizeId 授权ID
         */
        @Override
        public Observable<Void> authDevice(@NonNull final String authorizeId,@NonNull String cardNo) {
            return this.cloudStore.authDevice(authorizeId, cardNo)
                    .map(
                            new ResponseFunc1(errorStatus, gson)
                                    .setCustomStatus(new HashSet<>(Arrays.asList(401, 406, 409)))
                    );

        }

        /*[S]modify by xienana@2016/08/31 to fix bug 3363 [review by] tangsha*/
        @Override
        public Observable<Map<String,Object>>getAuthInfo(@NonNull String authorizeId) {
            return this.cloudStore.obtainAuthInfo(authorizeId)
                    .map(new ResponseFunc1(errorStatus, gson)
                            .setCustomStatus(new HashSet<>(Arrays.asList(401, 406))));
		/*[E]modify by xienana@2016/08/31 to fix bug 3363 [review by] tangsha*/
        }
    }

}
