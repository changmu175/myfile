package com.xdja.data_mainframe.repository;


import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.xdja.comm.encrypt.EncryptAppBean;
import com.xdja.comm.encrypt.IEncryptUtils;
import com.xdja.comm.encrypt.NewStrategyAppsBean;
import com.xdja.comm.encrypt.NewStrategyContentBean;
import com.xdja.comm.encrypt.NewStrategyResponseBean;
import com.xdja.data_mainframe.db.bean.AccountTable;
import com.xdja.data_mainframe.db.encrypt.EncryptAppsDao;
import com.xdja.data_mainframe.db.encrypt.EncryptHelper;
import com.xdja.data_mainframe.di.CacheModule;
import com.xdja.data_mainframe.di.PreRepositoryModule;
import com.xdja.data_mainframe.entities.AccountEntityDataMapper;
import com.xdja.data_mainframe.entities.AccountProperty;
import com.xdja.data_mainframe.entities.AccountUserCacheDataMapper;
import com.xdja.data_mainframe.entities.cache.UserCache;
import com.xdja.data_mainframe.repository.datastore.UserInfoCloudStore;
import com.xdja.data_mainframe.repository.datastore.UserInfoStore;
import com.xdja.data_mainframe.util.LogError;
import com.xdja.data_mainframe.util.Util;
import com.xdja.dependence.annotations.ContextSpe;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.dependence.annotations.StoreSpe;
import com.xdja.domain_mainframe.model.Account;
import com.xdja.domain_mainframe.model.ImgCompressResult;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.domain_mainframe.repository.UserInfoRepository;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by ldy on 16/4/20.
 * 用户登录后,与用户信息,设置相关的操作的实现
 */
public class UserInfoRepImp extends ExtRepositoryImp<UserInfoStore> implements UserInfoRepository {

    private final Context context;
    private final AccountEntityDataMapper accountEntityDataMapper;
    private final Map<String, Provider<String>> stringMap;
    private final Map<String,Provider<Integer>> integerMap;
    private final UserCache userCache;
    private final UserInfoStore.PreUserInfoStore preDiskStore;
    private final AccountUserCacheDataMapper accountUserCacheDataMapper;
    private static final int WIDTH = 150;
    private static final int HEIGHT= 150;

    @SuppressWarnings("ConstructorWithTooManyParameters")
    @Inject
    public UserInfoRepImp(@StoreSpe(DiConfig.TYPE_DISK) UserInfoStore diskStore,
                          @StoreSpe(DiConfig.TYPE_DISK) UserInfoStore.PreUserInfoStore preDiskStore,
                          @StoreSpe(DiConfig.TYPE_CLOUD) UserInfoStore cloudStore,
                          @Named(PreRepositoryModule.NAMED_ERRORSTATUS) Set<Integer> errorStatus,
                          Gson gson,
                          @ContextSpe(DiConfig.CONTEXT_SCOPE_APP) Context context,
                          AccountEntityDataMapper accountEntityDataMapper,
                          Map<String, Provider<String>> stringMap,
                          Map<String,Provider<Integer>> integerMap,
                          UserCache userCache,
                          AccountUserCacheDataMapper accountUserCacheDataMapper) {
        super(diskStore, cloudStore, errorStatus, gson);
        this.context = context;
        this.accountEntityDataMapper = accountEntityDataMapper;
        this.stringMap = stringMap;
        this.integerMap = integerMap;
        this.userCache = userCache;
        this.preDiskStore = preDiskStore;
        this.accountUserCacheDataMapper = accountUserCacheDataMapper;
    }

    /**
     * 修改昵称
     *
     * @param nickName 新昵称
     */
    @Override
    public Observable<Map<String, String>> modifyNickName(@NonNull final String nickName) {
        return this.cloudStore.modifyNickName(nickName)
                .map(new ResponseFunc1<Map<String, String>>(errorStatus, gson).setCustomStatus(null))
                .doOnNext(new Action1<Map<String, String>>() {
                    @Override
                    public void call(Map<String, String> stringStringMap) {
                        final String nickNamePinYin = stringStringMap.get(AccountProperty.NICK_NAME_PINYIN);
                        final String nickNamePy = stringStringMap.get(AccountProperty.NICK_NAME_PY);
                        preDiskStore.updateCurrentAccountTableNickName(nickName, nickNamePy, nickNamePinYin)
                                .subscribe(new LogError());
                        userCache.setNickName(nickName, nickNamePinYin, nickNamePy);
                    }
                })
                ;
    }

    /**
     * 修改头像
     *
     * @param avatarId    头像id
     * @param thumbnailId 缩略图id
     */
    @Override
    public Observable<Void> modifyAvatar(@NonNull final String avatarId, @NonNull final String thumbnailId) {
        return this.cloudStore.modifyAvatar(avatarId, thumbnailId)
                .map(new ResponseFunc1<Void>(errorStatus, gson).setCustomStatus(null))
                .doOnNext(new Action1<Void>() {
                    @Override
                    public void call(Void o) {
                        preDiskStore.updateCurrentAccountTableAvatarId(avatarId, thumbnailId).subscribe(new LogError());
                        userCache.setAvatar(avatarId, thumbnailId);
                    }
                });
    }

    /**
     * 修改密码
     *
     * @param passwd 密码字符串的SM3摘要的16进制小写字符串
     */
    @Override
    public Observable<Void> modifyPasswd(@NonNull String passwd) {
        return cloudStore.modifyPasswd(passwd)
                .map(new ResponseFunc1<Void>(errorStatus, gson).setCustomStatus(null));
    }

    /**
     * 检测账号密码
     *
     * @param passwd 密码字符串的SM3摘要的16进制小写字符串
     */
    @Override
    public Observable<Void> authPasswd(@NonNull String passwd) {
        return cloudStore.authPasswd(passwd)
                .map(new ResponseFunc1<Void>(errorStatus, gson).setCustomStatus(new HashSet<>(Collections.singletonList(AccountRepImp.HTTP_401_ERROR_CODE))));
    }

    /**
     * 查询设备账号授信的设备列表
     *
     * @return <p>"cardNo":"78636a7982734923kj49873", //设备卡号</p>
     * <p>"deviceName":"ace", //设备名称</p>
     * <p>"bindTime":14221653215//绑定时间</p>
     */
    @Override
    public Observable<List<Map<String, String>>> queryDevices() {
        return cloudStore.queryDevices()
                .map(new ResponseFunc1<List<Map<String, String>>>(errorStatus, gson).setCustomStatus(null))
//                .map(new Func1<List<Map<String, String>>, List<Map<String, String>>>() {
//                    @Override
//                    public List<Map<String, String>> call(List<Map<String, String>> maps) {
//
//                        return null;
//                    }
//                })
                ;
    }


    /**
     * 修改授信设备名称
     *
     * @param cardNo     设备卡号
     * @param deviceName 设备名称
     */
    @Override
    public Observable<Void> modifyDeviceName(@NonNull String cardNo, @NonNull String deviceName) {
        return cloudStore.modifyDeviceName(cardNo, deviceName)
                .map(new ResponseFunc1<Void>(errorStatus, gson).setCustomStatus(new HashSet(Arrays.asList(AccountRepImp.HTTP_404_ERROR_CODE, AccountRepImp.HTTP_401_ERROR_CODE))));
    }

    /**
     * 解除授信设备与账号的关系
     *
     * @param cardNo 设备卡号
     */
    @Override
    public Observable<Void> relieveDevice(@NonNull String cardNo) {
        return cloudStore.relieveDevice(cardNo)
                .map(new ResponseFunc1<Void>(errorStatus, gson).setCustomStatus(new HashSet(Arrays.asList(AccountRepImp.HTTP_404_ERROR_CODE, AccountRepImp.HTTP_401_ERROR_CODE))));
    }

    /**
     * 客户端退出
     */
    @Override
    public Observable<Void> logout() {
        return cloudStore.logout()
                .map(new ResponseFunc1<Void>(errorStatus, gson)
                        .setCustomStatus(new HashSet(Arrays.asList(AccountRepImp.HTTP_401_ERROR_CODE))))
//                .doOnNext(new Action1() {
//                    @Override
//                    public void call(Object o) {
//                        diskStoreLogout().subscribe(new LogError());
//                    }
//                })
                ;
    }

    @Override
    public Observable<Void> diskStoreLogout() {
        //下线当前账号，将数据库中当前账号是否登录值设为false
        return preDiskStore.logoutCurrentAccountTable()
                .doOnNext(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        preDiskStore.clearTicket();
                    }
                });
    }


    /**
     * 根据查询条件精确查询账户详情
     *
     * @param accountOrMobile 查询条件：账号或手机号
     */
    @Override
    public Observable<MultiResult<String>> queryAccountInfo(@NonNull String accountOrMobile) {
        return cloudStore.queryAccountInfo(accountOrMobile)
                .map(new ResponseFunc1<MultiResult<String>>(errorStatus, gson).setCustomStatus(generate404Error()));
    }

    /**
     * <p>分批次获取与发起账户更新请求的账号相关（是好友、在相同的群组、在同一个集团）的有变更的账户信息，以下条件代表更新完成：</p>
     * <p>(1)更新到的accounts列表为空</p>
     * <p>(2)更新到的数量小于batchSize</p>
     *
     * @param lastUpdateId 账户信息最后更新标识，首次更新由客户端置为0
     * @param batchSize    本批次更新的数量，默认为10
     */
    @Override
    public Observable<MultiResult<String>> queryIncrementAccounts(@NonNull int lastUpdateId, int batchSize) {
        return cloudStore.queryIncrementAccounts(lastUpdateId, batchSize)
                .map(new ResponseFunc1<MultiResult<String>>(errorStatus, gson).setCustomStatus(null));
    }

    /**
     * 批量查询用户信息
     *
     * @param accounts 账号信息列表
     */
    @Override
    public Observable<MultiResult<String>> queryBatchAccount(@NonNull List<String> accounts) {
        return cloudStore.queryBatchAccount(accounts)
                .map(new ResponseFunc1<MultiResult<String>>(errorStatus, gson).setCustomStatus(null));
    }


    /**
     * 多设备情况下，账号下一个设备登录后要通知其他类型在线设备，其他在线设备收到上线消息后，向后台拉取上线通知消息
     */
    @Override
    public Observable<Map<String, String>> queryOnlineNotice() {
        return cloudStore.queryOnlineNotice()
                .map(new ResponseFunc1<Map<String, String>>(errorStatus, gson).setCustomStatus(generate404Error()));
    }

    /**
     * 多设备情况下，账号下同类型设备登录会相互挤下线，被挤下线的设备会收到强制下线通知，收到通知后向后台拉取下线通知消息
     */
    @Override
    public Observable<Map<String, String>> queryForceLogoutNotice() {
        return cloudStore.queryForceLogoutNotice(userCache.getAccount(),
                String.valueOf(integerMap.get(CacheModule.KEY_CLIENT_TYPE).get()))
                .map(new ResponseFunc1<Map<String, String>>(errorStatus, gson).setCustomStatus(generate404Error()));
    }

    /**
     * 当用户解绑设备时，如果被解绑设备在线，会收到设备解绑通知，收到通知后将调用该接口获取显现提示内容
     */
    @Override
    public Observable<Map<String, String>> queryUnBindDeviceNotice() {


        return cloudStore.queryUnBindDeviceNotice(userCache.getAccount())
                .map(new ResponseFunc1<Map<String, String>>(errorStatus, gson).setCustomStatus(generate404Error()));
    }


    @Override
    public Observable<Account> getCurrentAccountInfo() {
        return Observable.just(accountUserCacheDataMapper.transform(userCache));
    }

    private static HashSet generate404Error() {
        return new HashSet(Collections.singletonList(AccountRepImp.HTTP_404_ERROR_CODE));
    }


    public static class PreUserInfoRepImp extends ExtRepositoryImp<UserInfoStore.PreUserInfoStore> implements UserInfoRepository.PreUserInfoRepository {

        private final Context context;
        private final AccountEntityDataMapper accountEntityDataMapper;
        private final Map<String, Provider<String>> stringMap;

        @SuppressWarnings("ConstructorWithTooManyParameters")
        @Inject
        public PreUserInfoRepImp(@StoreSpe(DiConfig.TYPE_DISK) UserInfoStore.PreUserInfoStore diskStore,
                                 @StoreSpe(DiConfig.TYPE_CLOUD) UserInfoStore.PreUserInfoStore cloudStore,
                                 @Named(PreRepositoryModule.NAMED_ERRORSTATUS) Set<Integer> errorStatus,
                                 Gson gson,
                                 @ContextSpe(DiConfig.CONTEXT_SCOPE_APP) Context context,
                                 Map<String, Provider<String>> stringMap,
                                 AccountEntityDataMapper accountEntityDataMapper) {
            super(diskStore, cloudStore, errorStatus, gson);
            this.context = context;
            this.accountEntityDataMapper = accountEntityDataMapper;
            this.stringMap = stringMap;
        }

        /**
         * 为安通+客户端提供第三方应用加密策略的增加量更新
         *
         * @param version        协议版本号
         * @param cardNo         设备芯片卡号
         * @param lastStrategyId 最后策略更新ID，第一次为0
         * @param batchSize      批量条数
         */
   /*     @Override
        public Observable<Integer> updateStrategys(@NonNull String version, @NonNull String cardNo, int lastStrategyId, int batchSize) {
//            return cloudStore.updateStrategys(version, cardNo, lastStrategyId, batchSize)
//                    .map(new ResponseFunc1<Map<String, Object>>(errorStatus, gson).setCustomStatus(null));

            return cloudStore.updateStrategys(version, cardNo, lastStrategyId, batchSize)
                    .map(new ResponseFunc1<NewStrategyResponseBean>(errorStatus, gson).setCustomStatus(null))
                    .flatMap(new Func1<NewStrategyResponseBean, Observable<Integer>>() {
                        @Override
                        public Observable<Integer> call(NewStrategyResponseBean res) {
                            if (res != null && res.getUpdate()) {
                                List<NewStrategyContentBean> content = res.getContent();
                                for (NewStrategyContentBean cb : content) {
                                    List<NewStrategyAppsBean> apps = cb.getApps();
                                    if (apps != null && apps.size() > 0) {
                                        for (NewStrategyAppsBean ab : apps) {
                                            // 操作类型 1-添加；2-修改；3-删除
                                            switch (ab.getAction()) {
                                                case 1:
                                                case 2:
                                                    EncryptAppBean app = new EncryptAppBean();
                                                    app.setAppName(ab.getAppName());
                                                    app.setPackageName(ab.getPackageName());
                                                    app.setDescription(ab.getDescription());
                                                    app.setSupportType(ab.getSupportType());
                                                    app.setSupportVertion(ab.getSupportVertion());
                                                    app.setOpen(true);
                                                    EncryptHelper.saveEncryptApp(app);
                                                    break;
                                                case 3:
                                                    EncryptHelper.deleteEncryptApp(EncryptAppsDao.FIELD_PACKAGENAME, ab.getPackageName());
                                                    break;
                                            }
                                        }
                                    }
                                }
                                int lastId = content.get(content.size() - 1).getStrategyId();
                                IEncryptUtils.setStrategys(String.valueOf(lastId), content);
                                if (res.getHasMore()) {
                                    return Observable.just(lastId);
                                }
                            }
                            return Observable.just(-1);
                        }
                    });

        }
*/
        /**
         * 为安通+客户端提供第三方应用加密策略的增加量更新
         *
         * @param version        协议版本号
         * @param cardNo         设备芯片卡号
         * @param lastStrategyId 最后策略更新ID，第一次为0
         * @param batchSize      批量条数
         */
        @SuppressWarnings("MethodWithTooManyParameters")
        @Override
        public Observable<Integer> queryStrategyByMobile(@NonNull String version, @NonNull String cardNo, @NonNull String model,
                                                   @NonNull String manufacturer, int lastStrategyId, int batchSize) {
//            return cloudStore.updateStrategys(version, cardNo, lastStrategyId, batchSize)
//                    .map(new ResponseFunc1<Map<String, Object>>(errorStatus, gson).setCustomStatus(null));

            return cloudStore.queryStrategyByMobile(version, cardNo, model,manufacturer,lastStrategyId, batchSize)
                    .map(new ResponseFunc1<NewStrategyResponseBean>(errorStatus, gson).setCustomStatus(null))
                    .flatMap(new Func1<NewStrategyResponseBean, Observable<Integer>>() {
                        @Override
                        public Observable<Integer> call(NewStrategyResponseBean res) {
                            if (res != null && res.getUpdate()) {
                                List<NewStrategyContentBean> content = res.getContent();
                                for (NewStrategyContentBean cb : content) {
                                    List<NewStrategyAppsBean> apps = cb.getApps();
                                    if (apps != null && !apps.isEmpty()) {
                                        for (NewStrategyAppsBean ab : apps) {
                                            // 操作类型 1-添加；2-修改；3-删除
                                            switch (ab.getAction()) {
                                                case 1:
                                                case 2:
                                                    EncryptAppBean app = new EncryptAppBean();
                                                    app.setAppName(ab.getAppName());
                                                    app.setPackageName(ab.getPackageName());
                                                    app.setDescription(ab.getDescription());
                                                    app.setSupportType(ab.getSupportType());
                                                    app.setSupportVertion(ab.getSupportVertion());
                                                    app.setOpen(true);
                                                    EncryptHelper.saveEncryptApp(app);
                                                    break;
                                                case 3:
                                                    EncryptHelper.deleteEncryptApp(EncryptAppsDao.FIELD_PACKAGENAME, ab.getPackageName());
                                                    break;
                                            }
                                        }
                                    }
                                }
                                int lastId = content.get(content.size() - 1).getStrategyId();
                                IEncryptUtils.setStrategys(String.valueOf(lastId), content);
                                if (res.getHasMore()) {
                                    return Observable.just(lastId);
                                }
                            }
                            return Observable.just(-1);
                        }
                    });

        }

        /**
         * 查询所有第三方加密策略
         * @return 加密策略集合
         */
        @Override
        public Observable<List<EncryptAppBean>> queryStrategys(){
            return diskStore.queryStrategys();
        }

        /**
         * 获取安通+相关后台服务地址信息
         */
        @Override
        public Observable<Map<String, String>> queryServerConfigs() {
            return cloudStore.queryServerConfigs()
                    .map(new ResponseFunc1<Map<String, String>>(errorStatus, gson).setCustomStatus(generate404Error()))
                    .doOnNext(
                            new Action1<Map<String, String>>() {
                                @Override
                                public void call(Map<String, String> configs) {
                                    if (!TextUtils.isEmpty(configs.get("accountUrl"))) {
                                        cloudStore.changeAccountBaseUrl(configs.get("accountUrl"));
                                    }
                                }
                            }
                    );
        }


        @Override
        public Observable<Boolean> queryServerConfigsAndSave() {

            return diskStore.queryServerConfigs()
                    .flatMap(
                            new Func1<Response<Map<String, String>>, Observable<Boolean>>() {
                                @Override
                                public Observable<Boolean> call(Response<Map<String, String>> mapResponse) {
                                    if (mapResponse != null) {
                                        return Observable.just(Boolean.TRUE);
                                    } else {
                                        return cloudStore.queryServerConfigs()
                                                .map(
                                                        new ResponseFunc1<Map<String, String>>(errorStatus, gson)
                                                                .setCustomStatus(generate404Error())
                                                )
                                                .flatMap(new Func1<Map<String, String>, Observable<Boolean>>() {
                                                    @Override
                                                    public Observable<Boolean> call(Map<String, String> result) {
                                                        if (result == null) {
                                                            return Observable.just(Boolean.FALSE);
                                                        }
                                                        return diskStore.saveConfig(result);
                                                    }
                                                });
                                    }
                                }
                            }
                    );
        }

        /**
         * 向fastdfs上传图像文件
         *
         * @param imgFile 图像文件
         * @return fastdfs存储的图像地址
         */
        @Override
        public Observable<String> uploadImg(@NonNull File imgFile) {
            return cloudStore.uploadImg(imgFile)
                    .map(new Func1<Response<Map<String, String>>, String>() {
                        @Override
                        public String call(Response<Map<String, String>> mapResponse) {
                            return mapResponse.body().get(UserInfoCloudStore.FILEID);
                        }
                    });
        }

        @Override
        public Observable<ImgCompressResult> compressBitmap2jpg(@NonNull final Bitmap bitmap) {
            return Observable.create(new Observable.OnSubscribe<ImgCompressResult>() {
                @Override
                public void call(Subscriber<? super ImgCompressResult> subscriber) {
                    Bitmap thumbnailBitmap = null;
                    try {
                        thumbnailBitmap = ThumbnailUtils.extractThumbnail(bitmap, WIDTH, HEIGHT);
                        subscriber.onNext(new ImgCompressResult(
                                Util.compressBitmap2jpg(context, bitmap, "image"),
                                Util.compressBitmap2jpg(context, thumbnailBitmap, "thumbnail_image")));
                        subscriber.onCompleted();
                    } catch (Exception e) {
                        subscriber.onError(e);
                    } finally {
                        //yangshaopeng for memory leak
                        if(thumbnailBitmap != null) {
                            thumbnailBitmap.recycle();
                        }
                    }
                }
            });
        }

        @Override
        public Observable<String> queryTicketAtLocal() {
            return Observable.just(stringMap.get(CacheModule.KEY_PRE_TICKET).get());
        }

        @Override
        public Observable<Account> queryAccountAtLocal() {
            return diskStore.getAccountTable(stringMap.get(CacheModule.KEY_PRE_ACCOUNT_IN_PRE_LOGIN).get())
                    .map(
                            new Func1<AccountTable, Account>() {
                                @SuppressWarnings("ReturnOfNull")
                                @Override
                                public Account call(AccountTable accountTable) {
                                    if (accountTable == null) {
                                        return null;
                                    }
                                    return accountEntityDataMapper.transform(accountTable);
                                }
                            }
                    );
        }
    }

}
