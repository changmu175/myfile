package com.xdja.data_mainframe.repository;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.xdja.data_mainframe.di.CacheModule;
import com.xdja.data_mainframe.repository.datastore.UserInfoStore;
import com.xdja.dependence.annotations.ContextSpe;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.dependence.annotations.StoreSpe;
import com.xdja.dependence.exeptions.SafeCardException;
import com.xdja.dependence.uitls.ApkDetector;
import com.xdja.domain_mainframe.repository.ChipRepository;
import com.xdja.domain_mainframe.usecase.DetectUseCase;
import com.xdja.frame.data.cache.ConfigCache;
import com.xdja.frame.data.chip.TFCardManager;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Summary:安全卡操作集合</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.data_mainframe.repository</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/5/3</p>
 * <p>Time:14:40</p>
 */
public class ChipRepImp implements ChipRepository {

    public static final String ACTIVE_URL = "activeChipUrl";

    public static final String PKG_NAME = "com.xdja.safekeyservice";

    public static final String CKMS_VERSION = "CKMSVersionCode";

    public static final String CKMS_APK_NAME = "Standard-CKMS.apk";

    public static final int CKMS_UPDATE_VERSION_CODE = 32132303;

    private final Context context;
    private final TFCardManager tfCardManager;
    private final ConfigCache configCache;
    private final Map<String, Provider<String>> stringProviderMap;

    @SuppressWarnings("UnusedParameters")
    @Inject
    public ChipRepImp(@ContextSpe(DiConfig.CONTEXT_SCOPE_APP)
                      Context context,
                      @StoreSpe(DiConfig.TYPE_DISK)
                      UserInfoStore.PreUserInfoStore diskStore,
                      @Named(DiConfig.CONFIG_PROPERTIES_NAME) ConfigCache configCache,
                      TFCardManager tfCardManager,
                      Map<String, Provider<String>> stringProviderMap) {
        this.context = context;
        this.tfCardManager = tfCardManager;
        this.configCache = configCache;
        this.stringProviderMap = stringProviderMap;
    }

    /*[S]modify by xienana @20160721 for security chip driver detection (rummager : tangsha)*/
    @Override
    public Observable<Integer> checkDriverExist() {
        return Observable
                .just(context)
                .flatMap(
                        new Func1<Context, Observable<Integer>>() {
                            @Override
                            public Observable<Integer> call(Context context) {
                                if (ApkDetector.judgeIsInstall(context)) {
                                    String newVersionStr = configCache.get().get(CKMS_VERSION);
                                    int newVersion = 0;
                                    if (!TextUtils.isEmpty(newVersionStr)) {
                                        newVersion = Integer.parseInt(newVersionStr);
                                    }
                                    int installedVersion = ApkDetector.getInstalledApkVersion(context, PKG_NAME);
                                    Log.d("ChipRepImp", "CKMS Version info, newVersionStr " + newVersionStr + " installedVersion " + installedVersion);
                                    if (installedVersion < CKMS_UPDATE_VERSION_CODE) {
                                        ApkDetector.copyArchive(context, CKMS_APK_NAME);
                                        boolean res = ApkDetector.installApk(context, CKMS_APK_NAME);
                                        if (!res) {
                                            return Observable.just(DetectUseCase.CKMS_INSTALL_FAIL);
                                        }
                                        return Observable.just(DetectUseCase.CKMS_UPDATE);
                                    } else if (installedVersion < newVersion) {
                                        ApkDetector.startUpdateActivity(context);
                                        return Observable.just(DetectUseCase.CKMS_UPDATE);
                                    } else if (installedVersion >= newVersion) {
                                        return Observable.just(DetectUseCase.CKMS_ALREADY_INSTALL);
                                    }
                                }
                                return Observable.just(DetectUseCase.CKMS_UNINSTALL);
                            }

                        }
                );
    }/*[E]modify by xienana @20160721 for security chip driver detection (rummager : tangsha)*/


    @Override
    public Observable<Boolean> checkChipExist() {
        return Observable.just(this.tfCardManager.detectSafeCard());
    }

    @Override
    public Observable<Boolean> isChipActived() {
        return Observable.just(this.tfCardManager.isSafeCardActivied());
    }

    @Override
    public Observable<Boolean> activeChip() {
	    //[S]modify by tangsha@20161118 for active chip  (6146)
        String activeUrl = configCache.get().get(ACTIVE_URL);
        return Observable.just(this.tfCardManager.activieSafeCard(activeUrl));
		//[E]modify by tangsha@20161118 for active chip  (6146)
    }

    @Override
    public Observable<Boolean> isChangedChip() {
        return  Observable.just(stringProviderMap.get(CacheModule.KEY_PRE_CHIPID).get())
                .flatMap(
                        new Func1<String, Observable<Boolean>>() {
                            @Override
                            public Observable<Boolean> call(String s) {
                                if (TextUtils.isEmpty(s)) {
                                    return Observable.just(Boolean.TRUE);
                                } else {
                                    String deviceId = tfCardManager.getDeviceId();
                                    if (TextUtils.isEmpty(deviceId)) {
                                        return Observable.error(
                                                new SafeCardException(
                                                        "获取安全芯片ID失败",
                                                        SafeCardException.ERROR_GETCARDID_FAILD
                                                )
                                        );
                                    }
                                    return Observable.just(!deviceId.equals(s));
                                }
                            }
                        }
                );
    }
}
