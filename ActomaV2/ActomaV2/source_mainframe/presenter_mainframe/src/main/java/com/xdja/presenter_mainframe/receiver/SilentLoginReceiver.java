package com.xdja.presenter_mainframe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.xdja.comm.ckms.CkmsGpEnDecryptManager;
import com.xdja.comm.data.AccountBean;
import com.xdja.comm.server.AccountServer;
import com.xdja.comm.server.PreferencesServer;
import com.xdja.data_mainframe.repository.datastore.UserInfoDiskStore;
import com.xdja.domain_mainframe.di.PreUseCaseModule;
import com.xdja.domain_mainframe.model.Account;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.domain_mainframe.usecase.CkmsInitUseCase;
import com.xdja.domain_mainframe.usecase.DetectUseCase;
import com.xdja.frame.data.chip.TFCardManager;
import com.xdja.frame.data.persistent.PreferencesUtil;
import com.xdja.imp.IMAccountLifeCycle;
import com.xdja.presenter_mainframe.ActomaApplication;
import com.xdja.presenter_mainframe.WakeLockManager;
import com.xdja.presenter_mainframe.di.components.pre.PreUseCaseComponent;
import com.xdja.presenter_mainframe.global.PushController;
import com.xdja.presenter_mainframe.presenter.base.PerSubscriber;
import com.xdja.presenter_mainframe.util.DeviceUtil;

import java.lang.ref.WeakReference;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by ALH on 2017/3/9.
 */

public final class SilentLoginReceiver extends BroadcastReceiver {
    private static final String TAG = "SilentLoginReceiver";
    @Inject
    PreferencesUtil preferencesUtil;

    @Inject
    TFCardManager tfCardManager;

    private static final String PREVIOUS_VERSION = "previousVersion";

    private Map<String, Object> info = null;

    @Inject
    PushController pushController;

    private boolean isSilentRunning() {
        return ((ActomaApplication) ActomaApplication.getInstance()).isSilentRunning();
    }

    private void setSilentRunning(boolean isSilentRunning) {
        ((ActomaApplication) ActomaApplication.getInstance()).setSilentRunning(isSilentRunning);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || context == null || isSilentRunning()) return;
        Log.v(TAG ,"onReceive : " + intent.getAction() + " , " + IMAccountLifeCycle.imAccountLifeCycle);
        if (null == IMAccountLifeCycle.imAccountLifeCycle || null == IMAccountLifeCycle.imAccountLifeCycle
                .getComponent()) {
            setSilentRunning(true);
            startSilentLoginTask(context);
        }
    }

    private final void startSilentLoginTask(Context context) {
        new SilentLoginTask(context, this).execute(0);
    }

    private final class SilentLoginTask extends AsyncTask<Integer, Integer, Boolean> {
        private WeakReference<SilentLoginReceiver> mWeakReference = null;
        private Context mContext;

        public SilentLoginTask(Context context, SilentLoginReceiver weak) {
            if (weak != null) mWeakReference = new WeakReference<>(weak);
            if (preferencesUtil == null) preferencesUtil = new PreferencesUtil(context);
            if (tfCardManager == null) tfCardManager = new TFCardManager(context);
            mContext = context;
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            Log.v(TAG ,"SilentLogin start doInBackground");
            if (mWeakReference == null || mWeakReference.get() == null){ return false;}
            WakeLockManager.getInstance().acquire();
            AccountBean accountBean = AccountServer.getAccount();
            Log.v(TAG ,"SilentLogin accountBean : " + accountBean);
            if (accountBean == null) {
                WakeLockManager.getInstance().release();
                return false;
            }
            final PreUseCaseComponent preUseCaseComponent = ((ActomaApplication) ActomaApplication.getInstance())
                    .getAppComponent().plus(new PreUseCaseModule());
            Log.v(TAG ,"SilentLogin preUseCaseComponent : " + preUseCaseComponent);
            if (preUseCaseComponent == null) {
                WakeLockManager.getInstance().release();
                return false;
            }
            preUseCaseComponent.detectUseCase().fill().execute(new PerSubscriber<MultiResult<Object>>(null) {
                @Override
                public void onError(Throwable e) {
                    WakeLockManager.getInstance().release();
                }

                @Override
                public void onNext(MultiResult<Object> result) {
                    Log.v(TAG ,"SilentLogin detectUseCase : " + result);
                    if (result == null || result.getResultStatus() != DetectUseCase.RESULT_PASSED) {
                        WakeLockManager.getInstance().release();
                        return;
                    }
                    String pinCode = TFCardManager.getPin();
                    Log.v(TAG ,"SilentLogin pinCode : " + pinCode);
                    if (TextUtils.isEmpty(pinCode)) {
                        WakeLockManager.getInstance().release();
                        Log.v(TAG ,"SilentLoginService pinCode == null return");
                        return;
                    }
                    tfCardManager.initTFCardManager();
                    tfCardManager.initUnitePinManager();
                    Log.v(TAG ,"SilentLogin pushController : " + pushController);
                    //boolean pushResult = pushController.startPush();
                    //LogUtil.getUtils().e("SilentLoginService startPush : " + pushResult);

                    String prevVersion = PreferencesServer.getWrapper(mContext).gPrefStringValue(PREVIOUS_VERSION);
                    if (!TextUtils.isEmpty(prevVersion)) {
                        String currVersion = DeviceUtil.getClientVersion(mContext);
                        if (!TextUtils.isEmpty(currVersion) && !currVersion.equals(prevVersion)) {
                            Log.v(TAG ,"SilentLogin !currVersion.equals(prevVersion) clear serverConfig");
                            preferencesUtil.setPreferenceStringValue(UserInfoDiskStore.KEY_SERVERCONFIG, null);
                        }
                    }

                    preUseCaseComponent.launcherGetUserInfoUseCase().fill().execute(new PerSubscriber<MultiResult<Object>>(null) {
                        @Override
                        public void onError(Throwable e) {
                            WakeLockManager.getInstance().release();
                        }

                        @Override
                        public void onNext(MultiResult<Object> result) {
                            Log.v(TAG ,"SilentLogin launcherGetUserInfoUseCase LoadingDialogSubscriber " +
                                    "result " + result);
                            if (result == null || result.getResultStatus() != DetectUseCase.RESULT_PASSED) {
                                WakeLockManager.getInstance().release();
                                return;
                            }
                            info = result.getInfo();
                            Log.v(TAG ,"SilentLogin preUseCaseComponent.ckmsInitUseCase()");
                            preUseCaseComponent.ckmsInitUseCase().fill(false).execute(new PerSubscriber<MultiResult<Object>>(null) {

                                @Override
                                public void onError(Throwable e) {
                                    Log.v(TAG ,"SilentLogin ckmsInit onError : " + e);
                                    WakeLockManager.getInstance().release();
                                }

                                @Override
                                public void onNext(MultiResult<Object> result) {
                                    int status = result.getResultStatus();
                                    Map<String, Object> ckmsInfo = result.getInfo();
                                    Log.v(TAG ,"SilentLogin ckmsInit onNext " + result.getResultStatus() +
                                            " ckmsInfo is " + ckmsInfo);
                                    if (status != CkmsInitUseCase.INIT_OK) {
                                        WakeLockManager.getInstance().release();
                                        return;
                                    }
                                    ((ActomaApplication) ActomaApplication.getInstance()).setCkmsInitOk(true);
                                    if (CkmsGpEnDecryptManager.getCkmsIsOpen() && ckmsInfo != null &&
                                            (Boolean) ckmsInfo.get(CkmsInitUseCase.CKMS_HAS_INIT) == false) {
                                        int validTime = (int) ckmsInfo.get(CkmsInitUseCase.VALID_HOUR);
                                        CkmsGpEnDecryptManager.setCkmsValidTime(validTime);
                                        CkmsGpEnDecryptManager.ckmsRefreshTask(mContext);
                                    }
                                    if (info != null) {
                                        String ticket = (String) info.get("ticket");
                                        Account account = (Account) info.get("account");
                                        Log.v(TAG ,"SilentLogin 离线登录获取本地数据为：ticket：" + ticket +
                                                ", " +
                                                "account：" + account);
                                        if (TextUtils.isEmpty(ticket) || account == null) {
                                            WakeLockManager.getInstance().release();
                                            return;
                                        }
                                        ((ActomaApplication) ActomaApplication.getInstance()).createUserComponent
                                                (account, ticket);
                                        WakeLockManager.getInstance().release();
                                    }
                                }
                            });
                        }
                    });
                }
            });
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            Log.v(TAG , "SilentLogin onPostExecute : " + aBoolean);
            setSilentRunning(false);
        }
    }
}
