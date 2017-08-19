package com.xdja.presenter_mainframe.presenter.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.xdja.comm.bean.DataMigrationAccountBean;
import com.xdja.comm.circleimageview.CustomDialog;
import com.xdja.comm.ckms.CkmsGpEnDecryptManager;
import com.xdja.comm.data.AccountBean;
import com.xdja.comm.event.BusProvider;
import com.xdja.comm.event.LauncherUpdateEvent;
import com.xdja.comm.server.AccountServer;
import com.xdja.comm.uitl.DeviceUtil;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.comm.uitl.PermissionUtil;
import com.xdja.comm_mainframe.error.BusinessException;
import com.xdja.data_mainframe.di.CacheModule;
import com.xdja.data_mainframe.repository.datastore.UserInfoDiskStore;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.dependence.exeptions.OkException;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.dependence.uitls.NetworkUtil;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.domain_mainframe.model.Account;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.domain_mainframe.usecase.CkmsInitUseCase;
import com.xdja.domain_mainframe.usecase.DetectUseCase;
import com.xdja.frame.data.cache.ConfigCache;
import com.xdja.frame.data.chip.TFCardManager;
import com.xdja.frame.data.persistent.PreferencesUtil;
import com.xdja.frame.domain.usecase.Ext0Interactor;
import com.xdja.frame.domain.usecase.Ext1Interactor;
import com.xdja.frame.presenter.ActivityStack;
import com.xdja.frame.widget.XDialog;
import com.xdja.presenter_mainframe.ActomaApplication;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.autoupdate.AutoUpdate;
import com.xdja.presenter_mainframe.autoupdate.BackUpdateResultHandle;
import com.xdja.presenter_mainframe.autoupdate.UpdateManager;
import com.xdja.presenter_mainframe.cmd.LauncherCommand;
import com.xdja.presenter_mainframe.global.PushController;
import com.xdja.presenter_mainframe.navigation.Navigator;
import com.xdja.presenter_mainframe.presenter.LogoutHelper;
import com.xdja.presenter_mainframe.presenter.activity.login.DataMigrationAccountPresenter;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.LoadingDialogSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.LauncherView;
import com.xdja.presenter_mainframe.ui.uiInterface.VuLauncher;
import com.xdja.presenter_mainframe.util.SharePreferceUtil;
import com.xdja.safekeyservice.jarv2.SecuritySDKManager;
import com.xdja.safekeyservice.jarv2.bean.IVerifyPinResult;
import com.xdja.soc.certupload.CertUploadErrorCode;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import dagger.Lazy;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

@InjectOption.Options(InjectOption.OPTION_PRECACHEDUSER)
public class SplashPresenter extends PresenterActivity<LauncherCommand, VuLauncher> implements LauncherCommand {

    private static final int REQ_WIRTE_CODE = 0;

    public static final int  NAVIGATE_BTN_TYPE_ONE = 0;
    public static final int  NAVIGATE_BTN_TYPE_TWO = NAVIGATE_BTN_TYPE_ONE + 1;

    private String TAG = "SplashPresenter anTongCkms";
    @Inject
    Map<String, Provider<Integer>> integerMap;

    @Inject
    @InteractorSpe(DomainConfig.DETECT_INIT)
    Lazy<Ext0Interactor<MultiResult<Object>>> detectUseCase;

    //[S]tangsha@xdja.com 2016-08-17 add. for get pin before use pin. review by self.
    @Inject
    @InteractorSpe(DomainConfig.USER_INFO_INIT)
    Lazy<Ext0Interactor<MultiResult<Object>>> launcherGetUserInfoUseCase;
    //[E]tangsha@xdja.com 2016-08-17 add. for get pin before use pin. review by self.

    @Inject
    @InteractorSpe(DomainConfig.CKMS_INIT)
    Lazy<Ext1Interactor<Boolean,MultiResult<Object>>> ckmsInitUseCase;

    @Inject
    @InteractorSpe(DomainConfig.DATA_MIGRATION)
    Lazy<Ext0Interactor<DataMigrationAccountBean>> dataMigrationUserCase;

    @Inject
    PreferencesUtil preferencesUtil;

    @Inject
    LogoutHelper logoutHelper;

    @Inject
    UpdateManager updateManager;

    @Inject
    TFCardManager tfCardManager;

    @Inject
    PushController pushController;

    @Inject
    @Named(DiConfig.CONFIG_PROPERTIES_NAME)
    ConfigCache configCache;

    @NonNull
    @Override
    protected Class<? extends VuLauncher> getVuClass() {
        return LauncherView.class;
    }

    @NonNull
    @Override
    protected LauncherCommand getCommand() {
        return this;
    }

    //alh@xdja.com<mailto://alh@xdja.com> 2016-08-30 add. fix bug 3435 . review by wangchao1. Start
    private void smoothSwitchScreen() {
        ViewGroup rootView = (ViewGroup) this.findViewById(android.R.id.content);
        rootView.setBackgroundColor(Color.BLACK);
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        rootView.setPadding(0, statusBarHeight, 0, 0);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
    //alh@xdja.com<mailto://alh@xdja.com> 2016-08-30 add. fix bug 3435 . review by wangchao1. End

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        smoothSwitchScreen();
        // [Start] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-10. for check update.
        BusProvider.getMainProvider().register(this);
        // [End] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-10. for check update.
        getActivityPreUseCaseComponent().inject(this);
        /*[S]modify by xienana @20160721 for security chip detection install succeed(rummager : tangsha)*/
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addDataScheme("package");
        registerReceiver(ckmsReceiver,intentFilter);
        /*[E]modify by xienana @20160721 for security chip detection install succeed(rummager : tangsha)*/
        if (isMNC()) {
            ArrayList<String> permission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest
                    .permission.READ_EXTERNAL_STORAGE);
            if (permission != null && !permission.isEmpty()) {
                ActivityCompat.requestPermissions(this, permission.toArray(new String[]{}), REQ_WIRTE_CODE);
                return;
            }
        }
        init();

        //如果是退出登录进入到的登录界面，要把堆栈中的其他activity清空
        boolean isExit = getIntent().getBooleanExtra(Navigator.EXIT, false);
        if (isExit) {
            ActivityStack.getInstanse().pop2TopActivity(true);
        }
    }

    private CustomDialog noPermissionCustomDialo;
    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        if (isMNC()) {
            if (requestCode == REQ_WIRTE_CODE) {
                if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    init();
                } else {
                    final CustomDialog customDialog = new CustomDialog(this);
                    customDialog.setTitle(getString(R.string.none_sdcard_permission)).setMessage(getString(R.string
                            .none_sdcard_permission_hint)).setNegativeButton(getString(com.xdja.imp.R.string.confirm)
                            , new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            customDialog.dismiss();
                            ActivityStack.getInstanse().exitApp();
                        }
                    }).show();
                }
            }else if(requestCode == PermissionUtil.READ_PHONE_PERMISSION_REQUEST_CODE){
                if (grantResults != null && grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showCkmsUploadFailPrompt();
                } else {
                    noReadPhonePermissionPrompt();
                }
            }
        }
    }

    private void noReadPhonePermissionPrompt(){
        dismissPreDialog();
        noPermissionCustomDialo = new CustomDialog(this);
        noPermissionCustomDialo.setTitle(getString(R.string.none_read_phone_permission))
                .setMessage(getString(R.string.none_read_phone_permission_hint))
                .setNegativeButton(getString(com.xdja.imp.R.string.confirm)
                        , new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                noPermissionCustomDialo.dismiss();
                            }
                        }).show();
    }

   @Override
    protected boolean isNeedCheckPermission() {
        return false;
    }

    /*[S]modify by xienana @20160721 for security chip detection install succeed(rummager : tangsha)*/
    private BroadcastReceiver ckmsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String chipName = "com.xdja.safekeyservice";
            if(intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)){
                String packageName = intent.getData().getSchemeSpecificPart();
                if(packageName.equals(chipName)){
                    getVu().hideDownloadDialog();
                    getVu().setAgainBtnVisible(false);
                    dismissPreDialog();
                    init();
                }
            }else{
                getVu().setAgainBtnVisible(true);
                getVu().setNavigateBtnVisibilety(View.INVISIBLE, NAVIGATE_BTN_TYPE_TWO);
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // [Start] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-10. for check update.
        BusProvider.getMainProvider().unregister(this);
        // [End] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-10. for check update.
        unregisterReceiver(ckmsReceiver);
    }
    /*[E]modify by xienana @20160721 for security chip detection install succeed*/


    boolean isFirstLogIn = false;
    private void init() {
        AccountBean accountBean = AccountServer.getAccount();
        if (accountBean == null || ObjectUtil.objectIsEmpty(accountBean)
                || integerMap.get(CacheModule.KEY_LOGIN_STATE).get() != CacheModule.LOGIN_STATE_POS) {
            if (preferencesUtil.gPrefBooleanValue(ProductIntroductionAnimPresenter.IS_FINISH_INTRODUCE,false)){
                isFirstLogIn = true;
                loginDetect();
            }else {
                Navigator.navigateToProductIntroductionAnim(false);
            }
        } else {
            LogUtil.getUtils(TAG).d("notFirstLoginDetect：accountBean="+accountBean);
            loginDetect();
        }
    }

    private void loginDetect() {
        //alh@xdja.com<mailto://alh@xdja.com> 2016-09-02 add. fix do not show progress dialog . review by wangchao1. Start
        executeInteractorNoRepeat(detectUseCase.get().fill(),
                new LoadingDialogSubscriber<MultiResult<Object>>(this,this,true , 1) {
                    @Override
                    public void onNext(MultiResult<Object> result) {
                        super.onNext(result);
                        LogUtil.getUtils(TAG).i("detectUseCase LoadingDialogSubscriber result "+result);
						/*[S]modify by tangsha@20160913 for input pin use safekey interface, review by self*/
                        if (result != null && result.getResultStatus() == DetectUseCase.RESULT_PASSED) {
                            String pinCode = TFCardManager.getPin();
                            boolean pinEmpy = TextUtils.isEmpty(pinCode);
                            if(pinEmpy){
                                setDismissDialogWhenCompleleted(true);
                            }
                            initPin(pinEmpy);
							/*[E]modify by tangsha@20160913 for input pin use safekey interface, review by self*/
                        }else{
                            setDismissDialogWhenCompleleted(true);
                            getVu().setAgainBtnVisible(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if(e != null && e instanceof BusinessException){
                            String okCode = ((BusinessException)e).getOkCode();
                            if(TextUtils.isEmpty(okCode) == false && okCode.startsWith(BusinessException.ERROR_CKMS_UPLOAD_INFO_FAIL)) {
                                LogUtil.getUtils(TAG).i("detectUseCase LoadingDialogSubscriber okCode ERROR_CKMS_UPLOAD_INFO_FAIL "+okCode);
                                processCkmsUploadException(okCode);
                            }
                        }
                        setDismissDialogWhenCompleleted(true);
                        getVu().setAgainBtnVisible(true);
                    }
                });
    }

    private void processCkmsUploadException(String okCode){
        int sepIndex = okCode.indexOf(DetectUseCase.ERROR_CODE_SEP);
        int errorCodeInt = -1;
        if(sepIndex != -1 && sepIndex < okCode.length()){
            errorCodeInt = Integer.parseInt(okCode.substring(sepIndex+1));
        }
        switch (errorCodeInt){
            case CertUploadErrorCode.EXCEPTION_CODE_IO_ERR:
            {
                int toastStrId = R.string.feedback_network_error;
                if(NetworkUtil.isNetworkConnect(SplashPresenter.this)){
                    toastStrId = R.string.ckms_upload_failed;
                }
                showToast(toastStrId);
            }
            break;
            case CertUploadErrorCode.EXCEPTION_CODE_NO_POWER_REPORT:
            {
                int permissionRes = getDeviceImeiPermission();
                if (permissionRes == PermissionUtil.ALL_PERMISSION_OBTAINED) {
                    showCkmsUploadFailPrompt();
                }
            }
            break;
            case CertUploadErrorCode.EXCEPTION_CODE_GET_IMEI_NULL:
            case CertUploadErrorCode.EXCEPTION_CODE_NO_READPHONESATE_PERMISSION:
                noReadPhonePermissionPrompt();
                break;
            case CertUploadErrorCode.EXCEPTION_CODE_CHIPNO_SN_CERT_ACCORD:
            case CertUploadErrorCode.EXCEPTION_CODE_ILLEGAL_CERT:
                showToast(R.string.ckms_upload_card_exception);
                break;
            case CertUploadErrorCode.EXCEPTION_CODE_CON_CERT_NOT_YET_VALID:
            case CertUploadErrorCode.EXCEPTION_CODE_CON_CERT_EXPIRED:
                showToast(R.string.ckms_upload_time_exception);
                break;
            default:
                showToast(R.string.ckms_upload_failed);
                break;

        }
    }

    private void showToast(int strId){
        Toast.makeText(SplashPresenter.this,getString(strId),Toast.LENGTH_SHORT).show();
    }
    //alh@xdja.com<mailto://alh@xdja.com> 2016-09-02 add. fix do not show progress dialog . review by wangchao1. End

    private int getDeviceImeiPermission(){
        int i;
        if (Build.VERSION.SDK_INT < 23) {
            i = PermissionUtil.ALL_PERMISSION_OBTAINED;
        } else {
            i = PermissionUtil.requestPermissions(this, PermissionUtil.READ_PHONE_PERMISSION_REQUEST_CODE, Manifest.permission.READ_PHONE_STATE);
        }
        LogUtil.getUtils().d(TAG+" getDeviceImeiPermission i "+i);
        return i;
    }

    private XDialog xCkmsUploadDialog;
    private void showCkmsUploadFailPrompt(){
        dismissPreDialog();
        xCkmsUploadDialog = new XDialog(SplashPresenter.this);
        xCkmsUploadDialog.setCanceledOnTouchOutside(false);
        xCkmsUploadDialog.setCancelable(false);
        xCkmsUploadDialog.setTitle(R.string.no_permission_title)
                .setMessage(ActomaApplication.getInstance().getString(R.string.device_no_permission, DeviceUtil.getDeviceId(SplashPresenter.this)))
                .setPositiveButton(ActomaApplication.getInstance().getString(R.string.know),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                xCkmsUploadDialog.dismiss();
                            }
                        }).show();
    }

    private void dismissPreDialog(){
        if(noPermissionCustomDialo != null && noPermissionCustomDialo.isShowing()){
            noPermissionCustomDialo.dismiss();
        }
        if(xCkmsUploadDialog != null && xCkmsUploadDialog.isShowing()){
            xCkmsUploadDialog.dismiss();
        }
    }

    //alh@xdja.com<mailto://alh@xdja.com> 2016-08-31 add. fix bug 3456 . review by wangchao1. Start
    private void startPush(){
        LogUtil.getUtils(TAG).i("startPush");
        tfCardManager.initTFCardManager();
        tfCardManager.initUnitePinManager();

        boolean result = pushController.startPush();
        LogUtil.getUtils(TAG).i("启动推送结果为 ： " + result);
    }
    //alh@xdja.com<mailto://alh@xdja.com> 2016-08-31 add. fix bug 3456 . review by wangchao1. End

    /*[S]modify by tangsha@20160913 for input pin use safekey interface, review by self*/
    private void initPin(boolean pinEmpty){
          /*[S]modify by tangsha @20160816 for safe card lock because use default pin init ticket*/
        if(pinEmpty) {
            JSONObject json = SecuritySDKManager.getInstance().startVerifyPinActivity(this, new IVerifyPinResult() {
                @Override
                public void onResult(int i, String s) {
                    LogUtil.getUtils(TAG).i("startVerifyPinActivity onResult "+i+" s "+s);
                    if(i == 0){
                        startPush();
                        getUserConfigInfo();
                    }else{
                        SplashPresenter.this.finish();
                    }
                }
            });
            LogUtil.getUtils(TAG).i("startVerifyPinActivity return json "+json);
           /*[E]modify by tangsha@20160913 for input pin use safekey interface, review by self*/
        }else{
            startPush();
            getUserConfigInfo();
        }
        /*[E]modify by tangsha @20160816 for safe card lock because use default pin init ticket*/
    }
    //[S]tangsha@xdja.com 2016-08-17 add. for get pin before use pin. review by self.
    Map<String, Object> info = null;
    private void getUserConfigInfo(){
        //alh@xdja.com 2016-12-05 add review by gbc. Start
        String prevVersion = SharePreferceUtil.getPreferceUtil(this).getPreviousVersion();
        if (!TextUtils.isEmpty(prevVersion)) {
            String currVersion = DeviceUtil.getClientVersion(this);
            if (!TextUtils.isEmpty(currVersion) && !currVersion.equals(prevVersion)) {
                LogUtil.getUtils(TAG).e("!currVersion.equals(prevVersion) clear serverConfig");
                preferencesUtil.setPreferenceStringValue(UserInfoDiskStore.KEY_SERVERCONFIG, null);
            }
        }
        //alh@xdja.com 2016-12-05 add review by gbc. End

        executeInteractorNoRepeat(launcherGetUserInfoUseCase.get().fill(),
                new LoadingDialogSubscriber<MultiResult<Object>>(this,this , 1) {
                    @Override
                    public void onNext(MultiResult<Object> result) {
                        super.onNext(result);
                        LogUtil.getUtils(TAG).e("launcherGetUserInfoUseCase LoadingDialogSubscriber result "+result);
                        if (result != null && result.getResultStatus() == DetectUseCase.RESULT_PASSED) {
                            if(isFirstLogIn == false) {
                                info = result.getInfo();
                            }
                            // [Start] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-10. for check update.
                            // ckmsInit();
                            //开始后台检测升级
                            new AutoUpdate(SplashPresenter.this, new BackUpdateResultHandle(SplashPresenter.this)).updateStart();
                            // [End] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-10. for check update.
                        }else{
                            getVu().setAgainBtnVisible(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        getVu().setAgainBtnVisible(true);
                    }
                });
    }
    //[E]tangsha@xdja.com 2016-08-17 add. for get pin before use pin. review by self.

    // [S] modify by lixiaolong on 20160827.  for check whether need logout when upgraded.  review by wangchao1.
    private void checkWhetherNeedLogout(){
        updateManager.checkWhetherNeedLogout(this)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        // [S]add by LiXiaolong on 20160830. save current version name. review by myself.
                        updateManager.saveCurrentVersion(SplashPresenter.this);
                        // [E]add by LiXiaolong on 20160830. save current version name. review by myself.
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            logoutHelper.logout(null);
                            Navigator.navigateToLogin();
                        } else {
                            Navigator.navigateToMainFrame();
                        }
                        //alh@xdja.com 2016-09-05 modify. fix bug 3627 . review by wangchao1. Start
                        finish();
                        //alh@xdja.com 2016-09-05 modify. fix bug 3627 . review by wangchao1. End
                    }
                });
    }
    // [S] modify by lixiaolong on 20160827.  for check whether need logout when upgraded.  review by wangchao1.


    // [Start] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-10. for check update.
    @Subscribe
    public void checkUpdateResult (LauncherUpdateEvent event){
        if (event != null && event.isForceUpdate()) {
            return;
        }
        ckmsInit();
    }
    // [End] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-10. for check update.
    public static final String KEY_CHECK_ACCOUNT_STATUS = "check_account_status";
    private void ckmsInit() {
        executeInteractorNoRepeat(ckmsInitUseCase.get().fill(false),
                new LoadingDialogSubscriber<MultiResult<Object>>(this,this,true , 1) {
                    @Override
                    public void onNext(MultiResult<Object> result) {
                        super.onNext(result);
                        //tangsha@xdja.com 2016-08-02 modify. for ckms init fail can login . review by self. Start
                        int status = result.getResultStatus();
                        Map<String,Object> ckmsInfo = result.getInfo();
                        LogUtil.getUtils(TAG).i("ckmsInit onNext "+result.getResultStatus()+" ckmsInfo is "+ckmsInfo);
                        if(status == CkmsInitUseCase.INIT_OK) {
                            //tangsha@xdja.com 2016-08-05 modify. for ckms refresh . review by self. Start
                            //[S]modify by tangsha for bug 3343(if actoma or safe_key_service killed, binder maybe null) @2016/08/27 [review by] tangsha
                            ((ActomaApplication)ActomaApplication.getInstance()).setCkmsInitOk(true);
                            //[E]modify by tangsha for bug 3343(if actoma or safe_key_service killed, binder maybe null) @2016/08/27 [review by] tangsha
                            if(CkmsGpEnDecryptManager.getCkmsIsOpen() && ckmsInfo != null && (Boolean)ckmsInfo.get(CkmsInitUseCase.CKMS_HAS_INIT) == false) {
                                int validTime = (int) ckmsInfo.get(CkmsInitUseCase.VALID_HOUR);
                                CkmsGpEnDecryptManager.setCkmsValidTime(validTime);
                                CkmsGpEnDecryptManager.ckmsRefreshTask(SplashPresenter.this);
                            }
                            //tangsha@xdja.com 2016-08-05 modify. for ckms refresh . review by self. End
                            if (!PreferenceManager.getDefaultSharedPreferences(SplashPresenter.this).getBoolean(KEY_CHECK_ACCOUNT_STATUS , false)){
                                checkoutAccountStatus();
                                return;
                            }
                            dismissDialog();
                            navigateByInfo();
                        }else{
                            dismissDialog();
                            getVu().setAgainBtnVisible(true);
                        }
                        //tangsha@xdja.com 2016-08-02 modify. for ckms init fail can login . review by self. Start
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        setDismissDialogWhenCompleleted(true);
                        LogUtil.getUtils(TAG).i("ckmsInit onError "+e);
                        getVu().setAgainBtnVisible(true);
                    }
                });
    }

    /*[S]modify by xienana @20160721 for security chip detection initialize(rummager : tangsha)*/
    @Override
    public boolean handleOkException(@Nullable String okCode, @Nullable String userMsg, @Nullable OkException ex, @Nullable String mark) {
        if (okCode == null) {
            return true;
        }
        //[S]modify by xienana for ckms download @2016/10/08 [reviewed by tangsha]
        if (okCode.equals(BusinessException.ERROR_DRIVER_NOT_EXIST)) {
            getVu().showDownloadDialog();
            return false;
        }
        if(okCode.equals(BusinessException.ERROR_DRIVER_NEED_UPDATE)){
            return false;
        }
        if(okCode.equals(BusinessException.ERROR_DRIVER_INSTALL_FAIL)){
            getVu().showCkmsInstallFailToast();
            return false;
        }
        //[E]modify by xienana for ckms download @2016/10/08 [reviewed by tangsha]
        return true;
    }
    /*[E]modify by xienana @20160721 for security chip detection initialize(rummager : tangsha)*/

    //[S]modify by xienana for ckms download @2016/10/08 [reviewed by tangsha]
    @Override
    public void downloadCkms() {
        String url = configCache.get().get("downloadCkmsUrl");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        startActivity(intent);
    }
    //[E]modify by xienana for ckms download @2016/10/08 [reviewed by tangsha]

    @Override
    public void register() {
        Navigator.navigateToWriteRegistrationInfo();
    }

    @Override
    public void login() {
        Navigator.navigateToLogin();
    }

    @Override
    public void again() {
        getVu().setAgainBtnVisible(false);
        init();
    }

    private int mNavBtnType = NAVIGATE_BTN_TYPE_TWO;

    @Override
    public void setNavBtnType(int type) {
        mNavBtnType = type;
    }

    @Override
    public int getNavBtnType() {
        return mNavBtnType;
    }

    @Override
    public void oldLogin() {
        try {
            if (mResultBean == null) {
                return;
            }
            Intent intent = new Intent(SplashPresenter.this, DataMigrationAccountPresenter.class);
            intent.putExtra(Account.ACCOUNT, mResultBean.getAccount());
            startActivity(intent);
        } finally {
            finish();
        }
    }

    private void navigateByInfo(){
        boolean isFinish = true;
        try {
            if (info != null) {
                String ticket = (String) info.get("ticket");
                Account account = (Account) info.get("account");
                LogUtil.getUtils(TAG).i("离线登录获取本地数据为：ticket：" + ticket + ", account：" + account);

                if (!TextUtils.isEmpty(ticket) && account != null) {
                    ((ActomaApplication) getApplicationContext())
                            .createUserComponent(account, ticket);
                    // [Start] moidify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-11.  fix bug #2477.
                    // Navigator.navigateToMainFrame();
                    isFinish = false;
                    checkWhetherNeedLogout();
                    // [End] moidify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-11.  fix bug #2477.
                    return;
                }
            }

            // [S]add by LiXiaolong on 20160830. save current version name. review by myself.
            updateManager.saveCurrentVersion(this);
            // [E]add by LiXiaolong on 20160830. save current version name. review by myself.

            if(isFirstLogIn){
                isFinish = false;
                getVu().setNavigateBtnVisibilety(View.VISIBLE , NAVIGATE_BTN_TYPE_TWO);
            }else{
                Navigator.navigateToLogin();
            }
        } finally {
            if(isFinish) {
                finish();
            }
        }
    }

    private DataMigrationAccountBean mResultBean;

    private void checkoutAccountStatus(){
        executeInteractorNoRepeat(dataMigrationUserCase.get().fill() , new LoadingDialogSubscriber<DataMigrationAccountBean>(this,this , 1){

            @Override
            public void onNext(DataMigrationAccountBean bean) {
                super.onNext(bean);
                mResultBean = bean;
                if (bean.isOld()) {
                    getVu().setNavigateBtnVisibilety(View.VISIBLE, NAVIGATE_BTN_TYPE_ONE);
                    return;
                }
                navigateByInfo();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                getVu().setAgainBtnVisible(true);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Navigator.REQUEST_INTRODUCTION){
            isFirstLogIn = true;
            loginDetect();
        }
    }

    //[s]modify by xienana for bug 6039 @20161118 review by tangsha
    @Override
    public void initSafePin() {}
    //[e]modify by xienana for bug 6039 @20161118 review by tangsha

    //[s]add by ysp@xdja.com
    @Override
    public void detectUninstallSafekey() {
    }
    //[e]add by ysp@xdja.com
    @Override
    public void detectSafeKey() {
    }
}
