package com.xdja.data_mainframe.repository;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.xdja.data_mainframe.di.PreRepositoryModule;
import com.xdja.data_mainframe.repository.datastore.PwdStore;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.dependence.annotations.StoreSpe;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.domain_mainframe.repository.PwdRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.data_mainframe.repository</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/22</p>
 * <p>Time:14:35</p>
 */
public class PwdRepImp extends ExtRepositoryImp<PwdStore> implements PwdRepository {

    @Inject
    public PwdRepImp(@StoreSpe(DiConfig.TYPE_DISK) PwdStore diskStore,
                     @StoreSpe(DiConfig.TYPE_CLOUD) PwdStore cloudStore,
                     @Named(PreRepositoryModule.NAMED_ERRORSTATUS) Set<Integer> errorStatus,
                     Gson gson) {
        super(diskStore, cloudStore, errorStatus, gson);
    }

    @Override
    public Observable<Map<String, String>> checkRestPwdAuthCode(@NonNull String mobile,
                                                                @NonNull String authCode,
                                                                @NonNull String innerAuthCode) {
        return this.cloudStore.checkRestPwdAuthCode(mobile, authCode, innerAuthCode)
                .map(
                        new ResponseFunc1<Map<String, String>>(errorStatus, gson)
                                .setCustomStatus(new HashSet<>(Arrays.asList(AccountRepImp.HTTP_401_ERROR_CODE, AccountRepImp.HTTP_406_ERROR_CODE)))
                );
    }

    @Override
    public Observable<Void> restPwdByAuthCode(@NonNull String mobile,
                                        @NonNull String innerAuthCode,
                                        @NonNull String passwd) {
        return this.cloudStore.restPwdByAuthCode(mobile, innerAuthCode, passwd)
                .map(
                        new ResponseFunc1<Void>(errorStatus, gson)
                                .setCustomStatus(new HashSet<>(Arrays.asList(AccountRepImp.HTTP_401_ERROR_CODE, AccountRepImp.HTTP_406_ERROR_CODE)))
                );
    }

    @Override
    public Observable<Void> restPwdByFriendMobiles(@NonNull String account, String innerAuthCode, String passwd) {
        return this.cloudStore.restPwdByFriendMobiles(account, innerAuthCode, passwd)
                .map(
                        new ResponseFunc1<Void>(errorStatus, gson)
                                .setCustomStatus(new HashSet<>(Arrays.asList(AccountRepImp.HTTP_401_ERROR_CODE, AccountRepImp.HTTP_404_ERROR_CODE, AccountRepImp.HTTP_406_ERROR_CODE)))
                );
    }

    /**
     * 验证好友手机号
     *
     * @param account 帐号
     * @param mobiles 好友手机号列表
     * @return 服务器返回结果
     */
    @Override
    public Observable<MultiResult<Object>> authFriendPhone(@NonNull String account, List<String> mobiles) {
        return cloudStore.authFriendPhone(account,mobiles)
                .map(new ResponseFunc1<MultiResult<Object>>(errorStatus, gson)
                        .setCustomStatus(new HashSet<>(Arrays.asList(AccountRepImp.HTTP_401_ERROR_CODE, AccountRepImp.HTTP_404_ERROR_CODE, AccountRepImp.HTTP_406_ERROR_CODE))));
    }

}
