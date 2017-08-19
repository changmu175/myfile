package com.xdja.data_mainframe.repository.datastore;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xdja.comm.bean.DataMigrationAccountBean;
import com.xdja.data_mainframe.di.CacheModule;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.frame.data.persistent.PreferencesUtil;

import java.util.ArrayList;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.data_mainframe.repository.datastore</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/14</p>
 * <p>Time:15:39</p>
 */
@SuppressWarnings({"ReturnOfNull"})
public class AccountDiskStore implements AccountStore.PreAccountStore, AccountStore.PostAccountStore {
    private final PreferencesUtil preferencesUtil;

    @Inject
    public AccountDiskStore(PreferencesUtil preferencesUtil) {
        this.preferencesUtil = preferencesUtil;
    }

    @Override
    public Observable<Response<MultiResult<Object>>> migrateOldAccount(@Nullable String account, @Nullable String passwd, @Nullable String deviceId) {
        return null;
    }

    @Override
    public Observable<DataMigrationAccountBean> isNewAccount() {
        return null;
    }

    @Override
    public Observable<Response<MultiResult<String>>> registAccount(@Nullable String nickName, @NonNull String password, @Nullable String avatarId, @Nullable String thumbnailId, @Nullable String deviceId) {
        return null;
    }

    @Override
    public Observable<Response<MultiResult<Object>>> reObtainAccount(@NonNull String oldAccount, @NonNull String innerAuthCode) {
        return null;
    }

    @Override
    public Observable<Response<Void>> customaAccount(@NonNull String account, @NonNull String innerAuthCode, @NonNull String customizeAccount) {
        return null;
    }

    @Override
    public Observable<Response<Void>> modifyAccount(@NonNull String oldAccount, @NonNull String newAccount, @NonNull String innerAuthCode) {
        return null;
    }

    @Override
    public Observable<Response<Map<String, String>>> obtainBindMobileAuthCode(@NonNull String account, @NonNull String mobile) {
        return null;
    }

    @Override
    public Observable<Response<MultiResult<String>>> bindMobile(@NonNull String account, @NonNull String authCode, @NonNull String innerAuthCode, @NonNull String mobile) {
        return null;
    }

    @Override
    public Observable<Response<Void>> forceBindMobile(@NonNull String account, @NonNull String innerAuthCode, @NonNull String mobile) {
        return null;
    }

    @SuppressWarnings("MethodWithTooManyParameters")
    @Override
    public Observable<Response<MultiResult<Object>>> accountPwdLogin(@NonNull String account, @NonNull String pwd, int clinetType, int loginType, @NonNull String deviceModel, @NonNull String osName, @NonNull String osVersion, @NonNull String clientVersion, @NonNull String clientResource, @NonNull String pnToken, @NonNull String imei) {
        return null;
    }

    @Override
    public Observable<Response<Map<String, String>>> obtainLoginAuthCode(@NonNull String mobile) {
        return null;
    }

    @Override
    public Observable<Response<Map<String, String>>> obtainResetAuthCode(@NonNull String mobile) {
        return null;
    }

    @SuppressWarnings("MethodWithTooManyParameters")
    @Override
    public Observable<Response<MultiResult<Object>>> mobileLogin(@NonNull String mobile, @NonNull String authCode, @NonNull String innerAuthCode, int clinetType, int loginType, @NonNull String deviceModel, @NonNull String osName, @NonNull String osVersion, @NonNull String clientVersion, @NonNull String clientResource, @NonNull String pnToken, @NonNull String imei) {
        return null;
    }

    /**
     * 保存上次登录使用的账号或手机号
     *
     */
    @Override
    public Observable<Boolean> savaLoginData(@NonNull final String accountOrMobile) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    subscriber.onNext(preferencesUtil.setPreferenceStringValue(CacheModule.KEY_PRE_LOGIN_DATA,accountOrMobile));
                    subscriber.onCompleted();
                }catch (Exception e){
                    subscriber.onError(e);
                }
            }
        });
    }


    @Override
    public Observable<Response<Map<String,Object>>> refreshTicket(@NonNull String oldTicket, @NonNull String
            pnToken, int clientType) {
        return null;
    }

    @Override
    public Observable<Response<Void>> obtainBindAuthCode(@NonNull String mobile) {
        return null;
    }

    @Override
    public Observable<Response<Void>> obtainModifyAuthCode(@NonNull String mobile) {
        return null;
    }

    @Override
    public Observable<Response<Void>> obtainModifyMobile(@NonNull String mobile, @NonNull String authCode) {
        return null;
    }

    @Override
    public Observable<Response<Void>> ticketBindMobile(@NonNull String authCode, @NonNull String mobile) {
        return null;
    }

    @Override
    public Observable<Response<Void>> ticketForceBindMobile(@NonNull String mobile) {
        return null;
    }

    @Override
    public Observable<Response<Void>> ticketCustomAccount(@NonNull String customizeAccount) {
        return null;
    }

    @Override
    public Observable<Response<Void>> unbindMobile(@NonNull String mobile) {
        return null;
    }

    /*[S]add by tangsha for third encrypt send secKey info*/
    @Override
    public Observable<Response<Void>> sendThirdEnPushInfo(@NonNull String topic, @NonNull ArrayList<String> destAccount, @NonNull String content) {
        return null;
    }
    /*[E]add by tangsha for third encrypt send secKey info*/
}
