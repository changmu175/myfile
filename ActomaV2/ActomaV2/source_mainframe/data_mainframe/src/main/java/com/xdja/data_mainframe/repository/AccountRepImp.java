package com.xdja.data_mainframe.repository;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.xdja.comm.bean.DataMigrationAccountBean;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.server.PreferencesServer;
import com.xdja.comm.uitl.CommonUtils;
import com.xdja.comm.uitl.DeviceUtil;
import com.xdja.data_mainframe.di.CacheModule;
import com.xdja.data_mainframe.di.PreRepositoryModule;
import com.xdja.data_mainframe.entities.AccountEntityDataMapper;
import com.xdja.data_mainframe.entities.cache.UserCache;
import com.xdja.data_mainframe.repository.datastore.AccountStore;
import com.xdja.data_mainframe.repository.datastore.UserInfoDiskStore;
import com.xdja.data_mainframe.repository.datastore.UserInfoStore;
import com.xdja.data_mainframe.util.LogError;
import com.xdja.dependence.annotations.ContextSpe;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.dependence.annotations.StoreSpe;
import com.xdja.dependence.exeptions.CheckException;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.domain_mainframe.model.Account;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.domain_mainframe.repository.AccountRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * <p>Summary::帐号相关操作实现</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.data_mainframe.repository</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/14</p>
 * <p>Time:15:33</p>
 */
@SuppressWarnings({"EmptyClass"})
public class AccountRepImp {

    public static final int HTTP_400_ERROR_CODE = 400;
    public static final int HTTP_401_ERROR_CODE = 401;
    public static final int HTTP_403_ERROR_CODE = 403;
    public static final int HTTP_404_ERROR_CODE = 404;
    public static final int HTTP_406_ERROR_CODE = 406;
    public static final int HTTP_409_ERROR_CODE = 409;
    public static final int HTTP_500_ERROR_CODE = 500;

    public static class PreAccountRepImp extends ExtRepositoryImp<AccountStore.PreAccountStore> implements AccountRepository.PreAccountRepository {

        private final AccountEntityDataMapper accountEntityDataMapper;
        private final Context context;
        private Map<String, Provider<String>> stringMap;
        private Map<String, Provider<Integer>> integerMap;
        private UserInfoStore.PreUserInfoStore userInfoStore;
        public static final int SUCCESS = 0;

        @SuppressWarnings("ConstructorWithTooManyParameters")
        @Inject
        public PreAccountRepImp(@StoreSpe(DiConfig.TYPE_DISK) AccountStore.PreAccountStore diskStore,
                                @StoreSpe(DiConfig.TYPE_CLOUD) AccountStore.PreAccountStore cloudStore,
                                @Named(PreRepositoryModule.NAMED_ERRORSTATUS) Set<Integer> errorStatus,
                                @StoreSpe(DiConfig.TYPE_DISK) UserInfoStore.PreUserInfoStore userInfoDiskStore,
                                @ContextSpe(DiConfig.CONTEXT_SCOPE_APP) Context context,
                                AccountEntityDataMapper accountEntityDataMapper,
                                Gson gson,
                                Map<String, Provider<String>> stringMap,
                                Map<String, Provider<Integer>> integerMap) {
            super(diskStore, cloudStore, errorStatus, gson);
            this.stringMap = stringMap;
            this.integerMap = integerMap;
            this.userInfoStore = userInfoDiskStore;
            this.accountEntityDataMapper = accountEntityDataMapper;
            this.context = context;
        }

        @Override
        public Observable<MultiResult<Object>> migrateOldAccount(String account, String passwd) {
            return cloudStore.migrateOldAccount(account , passwd , stringMap.get(CacheModule.KEY_DEVICE_MODEL).get()).map(
                    new ResponseFunc1<MultiResult<Object>>(errorStatus, gson)
                            .setCustomStatus(null));
        }

        @Override
        public Observable<DataMigrationAccountBean> isNewAccount() {
            return cloudStore.isNewAccount();
        }

        @Override
        public Observable<MultiResult<String>> registAccount(@Nullable String nickName,
                                                             @NonNull String password,
                                                             @Nullable String avatarId,
                                                             @Nullable String thumbnailId) {
            return this.cloudStore
                    .registAccount(
                            nickName,
                            password,
                            avatarId,
                            thumbnailId,
                            stringMap.get(CacheModule.KEY_DEVICE_MODEL).get())
                    .map(
                            new ResponseFunc1<MultiResult<String>>(errorStatus, gson)
                                    .setCustomStatus(null)
                    );
        }

        @Override
        public Observable<MultiResult<Object>> reObtainAccount(@NonNull String oldAccount,
                                                               @NonNull String innerAuthCode) {
            return this.cloudStore
                    .reObtainAccount(oldAccount, innerAuthCode)
                    .map(
                            new ResponseFunc1<MultiResult<Object>>(errorStatus, gson)
                                    .setCustomStatus(new HashSet<>(Arrays.asList(HTTP_406_ERROR_CODE, HTTP_401_ERROR_CODE, HTTP_409_ERROR_CODE)))
                    );
        }

        @Override
        public Observable<Void> customAccount(@NonNull String account,
                                              @NonNull String innerAuthCode,
                                              @NonNull String customizeAccount) {
            return this.cloudStore.customaAccount(account, innerAuthCode, customizeAccount)
                    .map(
                            new ResponseFunc1<Void>(errorStatus, gson)
                                    .setCustomStatus(new HashSet<>(Arrays.asList(HTTP_401_ERROR_CODE, HTTP_406_ERROR_CODE)))
                    );
        }

        @Override
        public Observable<Void> modifyAccount(@NonNull String oldAccount,
                                              @NonNull String newAccount,
                                              @NonNull String innerAuthCode) {
            return this.cloudStore.modifyAccount(oldAccount, newAccount, innerAuthCode)
                    .map(
                            new ResponseFunc1<Void>(errorStatus, gson)
                                    .setCustomStatus(new HashSet<>(Arrays.asList(HTTP_401_ERROR_CODE, HTTP_406_ERROR_CODE)))
                    );
        }

        @Override
        public Observable<Map<String, String>> obtainBindMobileAuthCode(@NonNull String account,
                                                                        @NonNull String mobile) {
            return this.cloudStore.obtainBindMobileAuthCode(account, mobile)
                    .map(
                            new ResponseFunc1<Map<String, String>>(errorStatus, gson)
                                    .setCustomStatus(new HashSet<>(Arrays.asList(HTTP_404_ERROR_CODE, HTTP_406_ERROR_CODE, HTTP_409_ERROR_CODE, HTTP_500_ERROR_CODE)))
                    );
        }

        @Override
        public Observable<MultiResult<String>> bindMobile(@NonNull String account,
                                                          @NonNull String authCode,
                                                          @NonNull String innerAuthCode,
                                                          @NonNull String mobile) {
            return this.cloudStore.bindMobile(account, authCode, innerAuthCode, mobile)
                    .map(
                            new ResponseFunc1<MultiResult<String>>(errorStatus, gson)
                                    .setCustomStatus(new HashSet<>(Arrays.asList(HTTP_401_ERROR_CODE, HTTP_406_ERROR_CODE)))
                    );
        }

        @Override
        public Observable<Void> forceBindMobile(@NonNull String account,
                                                @NonNull String innerAuthCode,
                                                @NonNull String mobile) {
            return this.cloudStore.forceBindMobile(account, innerAuthCode, mobile)
                    .map(
                            new ResponseFunc1<Void>(errorStatus, gson)
                                    .setCustomStatus(new HashSet<>(Arrays.asList(HTTP_401_ERROR_CODE, HTTP_406_ERROR_CODE)))
                    );
        }

        @Override
        public Observable<MultiResult<Object>> accountPwdLogin(@NonNull final String account, @NonNull String pwd) {
            String deviceId = stringMap.get(CacheModule.KEY_CLIENT_RESOURCE).get();
            if (TextUtils.isEmpty(deviceId)) {
                return Observable.error(new CheckException(CheckException.DEVICE_ID_NOT_PROVIDE));
            }
            String imei = DeviceUtil.getDeviceId(context);
            return this.cloudStore.accountPwdLogin(
                    account,
                    pwd,
                    integerMap.get(CacheModule.KEY_CLIENT_TYPE).get(),
                    integerMap.get(CacheModule.KEY_LOGIN_TYPE).get(),
                    stringMap.get(CacheModule.KEY_DEVICE_MODEL).get(),
                    stringMap.get(CacheModule.KEY_OS_NAME).get(),
                    stringMap.get(CacheModule.KEY_OS_VERSION).get(),
                    stringMap.get(CacheModule.KEY_CLIENT_VERSION).get(),
                    deviceId,
                    stringMap.get(CacheModule.KEY_PN_TOKEN).get() , imei)
                    .map(
                            new ResponseFunc1<MultiResult<Object>>(errorStatus, gson)
                                    .setCustomStatus(new HashSet<>(Arrays.asList(HTTP_401_ERROR_CODE, HTTP_404_ERROR_CODE , HTTP_403_ERROR_CODE)))
                    ).doOnNext(new Action1<MultiResult<Object>>() {
                        @Override
                        public void call(MultiResult<Object> objectMultiResult) {
                            storeLoginResultCache(objectMultiResult);
                            storeLoginAccount(diskStore,account);
                        }
                    });
        }

        static void storeLoginAccount(AccountStore.PreAccountStore store,String accountOrMobile) {
            store.savaLoginData(accountOrMobile).subscribe(new LogError());
        }

        @Override
        public Observable<Map<String, String>> obtainLoginAuthCode(@NonNull String mobile) {
            return this.cloudStore.obtainLoginAuthCode(mobile)
                    .map(
                            new ResponseFunc1<Map<String, String>>(errorStatus, gson)
                                    .setCustomStatus(new HashSet<>(Arrays.asList(HTTP_409_ERROR_CODE, HTTP_404_ERROR_CODE, HTTP_500_ERROR_CODE)))
                    );
        }

        @Override
        public Observable<Map<String, String>> obtainResetAuthCode(@NonNull String mobile) {
            return this.cloudStore.obtainResetAuthCode(mobile).map(
                    new ResponseFunc1<Map<String, String>>(errorStatus, gson)
                            .setCustomStatus(new HashSet<>(Arrays.asList(HTTP_404_ERROR_CODE, HTTP_409_ERROR_CODE, HTTP_500_ERROR_CODE)))
            );
        }

        @Override
        public Observable<MultiResult<Object>> mobileLogin(@NonNull final String mobile,
                                                           @NonNull String authCode,
                                                           @NonNull String innerAuthCode) {
            String deviceId = stringMap.get(CacheModule.KEY_CLIENT_RESOURCE).get();
            if (TextUtils.isEmpty(deviceId)) {
                return Observable.error(new CheckException(CheckException.DEVICE_ID_NOT_PROVIDE));
            }
            String imei = DeviceUtil.getDeviceId(context);
            return this.cloudStore.mobileLogin(
                    mobile,
                    authCode,
                    innerAuthCode,
                    integerMap.get(CacheModule.KEY_CLIENT_TYPE).get(),
                    integerMap.get(CacheModule.KEY_LOGIN_TYPE).get(),
                    stringMap.get(CacheModule.KEY_DEVICE_MODEL).get(),
                    stringMap.get(CacheModule.KEY_OS_NAME).get(),
                    stringMap.get(CacheModule.KEY_OS_VERSION).get(),
                    stringMap.get(CacheModule.KEY_CLIENT_VERSION).get(),
                    deviceId,
                    stringMap.get(CacheModule.KEY_PN_TOKEN).get(),
                    imei)
                    .map(
                            new ResponseFunc1<MultiResult<Object>>(errorStatus, gson)
                                    //alh@xdja.com<mailto://alh@xdja.com> 2016-11-11 add. fix bug 5901 . review by wangchao1. Start
                                    .setCustomStatus(new HashSet<>(Arrays.asList(HTTP_401_ERROR_CODE, HTTP_406_ERROR_CODE , HTTP_404_ERROR_CODE , HTTP_500_ERROR_CODE , HTTP_403_ERROR_CODE)))
                            //alh@xdja.com<mailto://alh@xdja.com> 2016-11-11 add. fix bug 5901 . review by wangchao1. End
                    ).doOnNext(new Action1<MultiResult<Object>>() {
                        @Override
                        public void call(MultiResult<Object> objectMultiResult) {
                            storeLoginResultCache(objectMultiResult);
                            storeLoginAccount(diskStore,mobile);
                        }
                    });
        }

        /**
         * 将登陆数据放置到数据库中
         *
         * @param objectMultiResult 登录后的返回数据
         */
        private void storeLoginResultCache(MultiResult<Object> objectMultiResult) {
            if (objectMultiResult.getResultStatus() != SUCCESS || objectMultiResult == null) {
                return;
            }
            Map<String, Object> info = objectMultiResult.getInfo();
            if (info == null) {
                return;
            }
            Map<String, Object> stringObjectMap = (Map<String, Object>) info.get(Account.USER_INFO);
            final String ticket = (String) info.get(Account.TICKET);
            final long ticketCreateTime = info.get(Account.TICKET_CREATE_TIME) instanceof Double ? ((Double) info
                    .get(Account.TICKET_CREATE_TIME)).longValue() : (long) info.get(Account.TICKET_CREATE_TIME);
            final long ticketVaildExpireTime = info.get(Account.TICKET_VAILD_EXPIRE_TIME) instanceof Double ? (
                    (Double) info.get(Account.TICKET_VAILD_EXPIRE_TIME)).longValue() : (long) info.get(Account
                    .TICKET_VAILD_EXPIRE_TIME);

            if (stringObjectMap == null || TextUtils.isEmpty(ticket)) {
                return;
            }
            //[s]add by xnn for get company code from different two interface
            String curAccount =(String) stringObjectMap.get("account");
            String curCompanyCode = (String) stringObjectMap.get("companyCode");
            CommonUtils.checkCompanyCodeChanged(curAccount,curCompanyCode);
            //[e]add by xnn for get company code from different two interface

            final Account account = new Account(stringObjectMap);
            account.setOnLine(true);
            userInfoStore
                    .createOrUpdateCurrentAccountInfo(
                            accountEntityDataMapper.transform(account),true)//Note:if is new company code interface true should change false
                    .flatMap(
                            new Func1<Void, Observable<Boolean>>() {
                                @Override
                                public Observable<Boolean> call(Void aVoid) {
                                    LogUtil.getUtils().d("登录成功，缓存用户信息 : ");
                                    LogUtil.getUtils().d("Account : " + account.getAccount());
                                    LogUtil.getUtils().d("ticket : " + ticket);
                                    LogUtil.getUtils().d("ticketCreateTime : " + ticketCreateTime);
                                    LogUtil.getUtils().d("ticketVaildExpireTime : " + ticketVaildExpireTime);
                                    LogUtil.getUtils().d("chipId : " + stringMap.get(CacheModule.KEY_DEVICEID).get());
                                    return userInfoStore
                                            .saveLoginCache(
                                                    account.getAccount(),
                                                    ticket,
                                                    ticketCreateTime,
                                                    ticketVaildExpireTime,
                                                    stringMap.get(CacheModule.KEY_DEVICEID).get()
                                            );
                                }
                            }
                    )
                    .subscribe(new LogError());


        }

    }


    public static class PostAccountRepImp extends ExtRepositoryImp<AccountStore.PostAccountStore>
            implements AccountRepository.PostAccountRepository {

        private final UserCache userCache;
        private final UserInfoStore.PreUserInfoStore userInfoPreDiskStore;
        private final AccountStore.PreAccountStore accountPreDiskStore;
        private Map<String, Provider<String>> stringMap;
        private Map<String, Provider<Integer>> integerMap;

        @SuppressWarnings("ConstructorWithTooManyParameters")
        @Inject
        public PostAccountRepImp(@StoreSpe(DiConfig.TYPE_DISK) AccountStore.PostAccountStore diskStore,
                                 @StoreSpe(DiConfig.TYPE_CLOUD) AccountStore.PostAccountStore cloudStore,
                                 @Named(PreRepositoryModule.NAMED_ERRORSTATUS) Set<Integer> errorStatus,
                                 UserCache userCache,
                                 @StoreSpe(DiConfig.TYPE_DISK) UserInfoStore.PreUserInfoStore userInfoPreDiskStore,
                                 @StoreSpe(DiConfig.TYPE_DISK) AccountStore.PreAccountStore accountPreDiskStore,
                                 Gson gson,
                                 Map<String, Provider<String>> stringMap,
                                 Map<String, Provider<Integer>> integerMap) {
            super(diskStore, cloudStore, errorStatus, gson);
            this.userCache = userCache;
            this.userInfoPreDiskStore = userInfoPreDiskStore;
            this.accountPreDiskStore = accountPreDiskStore;
            this.stringMap = stringMap;
            this.integerMap = integerMap;
        }

        @Override
        public Observable<Map<String,Object>> refreshTicket(@NonNull String ticket) {
            return this.cloudStore.refreshTicket(ticket , stringMap.get(CacheModule.KEY_PN_TOKEN).get() , integerMap.get(CacheModule.KEY_CLIENT_TYPE).get())
                    .map(
                            new ResponseFunc1<Map<String,Object>>(errorStatus, gson)
                                    .setCustomStatus(new HashSet<>(Arrays.asList(HTTP_401_ERROR_CODE, HTTP_404_ERROR_CODE)))
                    ).doOnNext(new Action1<Map<String, Object>>() {
                        @Override
                        public void call(Map<String, Object> info) {
                            if (info == null) return;
                            LogUtil.getUtils().e("refreshTicket doOnNext : " + info);
                            if (info == null) return;
                            PreferencesServer.getWrapper(ActomaController.getApp()).setPreferenceStringValue(UserInfoDiskStore.KEY_PRE_TICKET, (String) info.get("newTicket"));
                            PreferencesServer.getWrapper(ActomaController.getApp()).setPreferenceLongValue(Account.TICKET_CREATE_TIME, info.get("createTime") instanceof
                                    Double ? ((Double) info.get("createTime")).longValue() : (long) info.get
                                    ("createTime"));
                            PreferencesServer.getWrapper(ActomaController.getApp()).setPreferenceLongValue(Account.TICKET_VAILD_EXPIRE_TIME, info.get("vaildExpireTime")
                                    instanceof Double ? ((Double) info.get("vaildExpireTime")).longValue() : (long)
                                    info.get("vaildExpireTime"));
                            String newTicket = (String) info.get("newTicket");
                            PreferencesServer.getWrapper(ActomaController.getApp()).setPreferenceStringValue(Account.TICKET, newTicket);
                            userCache.setTicket(newTicket);
                        }
                    });
        }

        @Override
        public Observable<Void> setMobile(@NonNull String mobile) {
            saveMobile(mobile);
           return Observable.create(new Observable.OnSubscribe<Void>() {
               @Override
               public void call(Subscriber<? super Void> subscriber) {

               }
           });
        }

        @Override
        public Observable<Void> obtainBindAuthCode(@NonNull String mobile) {
            return this.cloudStore.obtainBindAuthCode(mobile)
                    .map(
                            new ResponseFunc1<Void>(errorStatus, gson)
                                    .setCustomStatus(new HashSet<>(Arrays.asList(HTTP_409_ERROR_CODE, HTTP_500_ERROR_CODE, HTTP_406_ERROR_CODE)))
                    );
        }

        @Override
        public Observable<Void> obtainModifyAuthCode(@NonNull String mobile) {
            return this.cloudStore.obtainModifyAuthCode(mobile).map(
                    new ResponseFunc1<Void>(errorStatus, gson)
                            .setCustomStatus(new HashSet<>(Arrays.asList(HTTP_500_ERROR_CODE, HTTP_409_ERROR_CODE, HTTP_406_ERROR_CODE))));
        }

        @Override
        public Observable<Void> obtainModifyMobile(@NonNull final String mobile, @NonNull String authCode) {
            return this.cloudStore.obtainModifyMobile(mobile, authCode).map(
                    new ResponseFunc1<Void>(errorStatus, gson)
                            .setCustomStatus(new HashSet<>(Arrays.asList(HTTP_409_ERROR_CODE, HTTP_401_ERROR_CODE, HTTP_406_ERROR_CODE)))

            ).doOnNext(new Action1<Void>() {
                @Override
                public void call(Void aVoid) {
                    saveMobile(mobile);
                }
            });
        }

        @Override
        public Observable<Void> ticketBindMobile(@NonNull String authCode, @NonNull final String mobile) {
            return this.cloudStore.ticketBindMobile(authCode, mobile)
                    .map(
                            new ResponseFunc1<Void>(errorStatus, gson)
                                    .setCustomStatus(new HashSet<>(Arrays.asList(HTTP_401_ERROR_CODE, HTTP_409_ERROR_CODE, HTTP_406_ERROR_CODE)))
                    ).doOnNext(new Action1<Void>() {
                        @Override
                        public void call(Void o) {
                            saveMobile(mobile);
                        }
                    });
        }

        private void saveMobile(@NonNull String mobile) {
            List<String> mobiles = Collections.singletonList(mobile);
            userInfoPreDiskStore.updateCurrentAccountTableMobile(mobiles).subscribe(new LogError());
            userCache.setMobiles(mobiles);
        }

        @Override
        public Observable<Void> ticketForceBindMobile(@NonNull final String mobile) {
            return this.cloudStore.ticketForceBindMobile(mobile)
                    .map(
                            new ResponseFunc1<Void>(errorStatus, gson)
                                    .setCustomStatus(null)
                    ).doOnNext(new Action1<Void>() {
                        @Override
                        public void call(Void o) {
                            saveMobile(mobile);
                        }
                    });
        }


        @Override
        public Observable<Void> ticketCustomAccount(@NonNull final String customizeAccount) {
            return this.cloudStore.ticketCustomAccount(customizeAccount)
                    .map(
                            new ResponseFunc1<Void>(errorStatus, gson)
                                    .setCustomStatus(new HashSet<>(Arrays.asList(HTTP_401_ERROR_CODE)))

                    ).doOnNext(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            userCache.setAlias(customizeAccount);
                            userInfoPreDiskStore.updateCurrentAccountTableAlias(customizeAccount).subscribe(new LogError());
                            PreAccountRepImp.storeLoginAccount(accountPreDiskStore,customizeAccount);
                        }
                    });
        }

        @Override
        public Observable<Void> unbindMobile(@NonNull String mobile) {
            return this.cloudStore.unbindMobile(mobile)
                    .map(
                            new ResponseFunc1<Void>(errorStatus, gson)
                                    .setCustomStatus(new HashSet<>(Arrays.asList(HTTP_401_ERROR_CODE)))
                    ).doOnNext(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            saveMobile("");
                        }
                    });
        }

        /*[S]add by tangsha for third encrypt send secKey info*/
        @Override
        public Observable<Void> sendThirdEnPushInfo(String topic, ArrayList<String> destAccount, String content) {
            return cloudStore.sendThirdEnPushInfo(topic, destAccount, content)
                    .map( new ResponseFunc1<Void>(errorStatus, gson)
                            .setCustomStatus(new HashSet<>(Arrays.asList(HTTP_404_ERROR_CODE))));
        }
    /*[E]add by tangsha for third encrypt send secKey info*/
    }
}
