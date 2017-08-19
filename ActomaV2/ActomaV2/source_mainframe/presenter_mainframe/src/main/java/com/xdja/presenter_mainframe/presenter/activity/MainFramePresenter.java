package com.xdja.presenter_mainframe.presenter.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.securevoipcommon.VoipFunction;
import com.squareup.otto.Subscribe;
import com.xdja.comm.circleimageview.CustomDialog;
import com.xdja.comm.cust.CustInfo;
import com.xdja.comm.data.SettingBean;
import com.xdja.comm.encrypt.IEncryptUtils;
import com.xdja.comm.event.ChangeTabIndexEvent;
import com.xdja.comm.event.FreshUpdateNewEvent;
import com.xdja.comm.event.TabTipsEvent;
import com.xdja.comm.event.UpdateContactTabTipsEvent;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.server.PreferencesServer;
import com.xdja.comm.server.SettingServer;
import com.xdja.comm.uitl.CommonUtils;
import com.xdja.comm.uitl.StateParams;
import com.xdja.comm.uitl.UniversalUtil;
import com.xdja.contact.ContactModuleService;
import com.xdja.contact.service.FriendRequestService;
import com.xdja.contact.util.BroadcastManager;
import com.xdja.contact.util.ContactUtils;
import com.xdja.contactcommon.ContactModuleProxy;
import com.xdja.dependence.event.BusProvider;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.frame.data.chip.TFCardManager;
import com.xdja.frame.data.persistent.PreferencesUtil;
import com.xdja.frame.domain.usecase.Ext1Interactor;
import com.xdja.frame.domain.usecase.Ext2Interactor;
import com.xdja.frame.domain.usecase.Ext4Interactor;
import com.xdja.frame.presenter.ActivityStack;
import com.xdja.frame.widget.XToast;
import com.xdja.presenter_mainframe.ActomaApplication;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.MainFrameCommand;
import com.xdja.presenter_mainframe.enc3rd.service.EncryptManager;
import com.xdja.presenter_mainframe.enc3rd.utils.StrategysUtils;
import com.xdja.presenter_mainframe.enc3rd.utils.ThirdEncAppProperty;
import com.xdja.presenter_mainframe.global.obs.UnBindDeviceObservable;
import com.xdja.presenter_mainframe.navigation.Navigator;
import com.xdja.presenter_mainframe.presenter.activity.setting.OpenGesturePresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.SettingSafeLockPresenter;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.LoadingDialogSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PerSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.presenter.fragement.AppStorePresenter;
import com.xdja.presenter_mainframe.receiver.ContactModulEncryptReceiver;
import com.xdja.presenter_mainframe.ui.ViewMainFrame;
import com.xdja.presenter_mainframe.ui.uiInterface.VuMainFrame;
import com.xdja.presenter_mainframe.util.LockPatternUtils;
import com.xdja.presenter_mainframe.util.SharePreferceUtil;
import com.xdja.presenter_mainframe.widget.SafeLockApplication;
import com.xdja.presenter_mainframe.widget.TabFragmentAdapter;
import com.xdja.proxy.imp.MxModuleProxyImp;
import com.xdja.report.reportClientMessage;
import com.xdja.safekeyjar.util.StringResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.actoma.presenter.activity</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/7/8</p>
 * <p>Time:17:01</p>
 */
@InjectOption.Options(InjectOption.OPTION_POSTCACHEDUSER)
public class MainFramePresenter extends PresenterActivity<MainFrameCommand, VuMainFrame>
        implements MainFrameCommand {
    private static final String TAG = "MainFramePresenter";
    private static int[] mTitle = {
            R.string.lable_tab_msg,
            R.string.lable_tab_phone,
            R.string.lable_tab_contact,
            R.string.lable_tab_more
    };
    private static int[] mIconSelect = {
            R.mipmap.af_tab_ic_im_press,
            R.mipmap.af_tab_ic_call_press,
            R.mipmap.af_tab_ic_contact_press,
            R.mipmap.af_tab_ic_more_press
    };
    private static int[] mIconNormal = {
            R.mipmap.af_tab_ic_im_normal,
            R.mipmap.af_tab_ic_call_normal,
            R.mipmap.af_tab_ic_contact_normal,
            R.mipmap.af_tab_ic_more_normal
    };

    @Inject
    BusProvider busProvider;

    @Inject
    @InteractorSpe(DomainConfig.CHECK_NEW_VERSION)
    Lazy<Ext1Interactor<Context, Boolean>> checkUpdateUseCase;


    @Inject
    PreferencesUtil preferencesUtil;

    @Inject
    LockPatternUtils lockPatternUtils;
    /**
     * 要跳转到的页面
     */
    public static final String ARG_PAGE_INDEX = "pageIndex";


    //重新设置手势密码的Dialog
    private CustomDialog customDialog;


    /**
     * 供外部调用的参数类
     */
    private StateParams stateParams = StateParams.getStateParams();

    /**
     * 第三方应用加密服务通知栏管理器
     */
    private EncryptManager manager;

    private PatternLockReceiver patternLockReceiver;//灭屏广播

    //private QRHandle qrHandle;

    /**
     * 获取支持的第三方应用的任务
     */
//    private GetSupportAppUseCase getSupportAppUseCase;

    /**
     * hook升级case对象
     */
    @Inject
    @InteractorSpe(DomainConfig.HOOK_UPDATE)
    Lazy<Ext1Interactor<Context, Boolean>> hookUpdateUseCase;

//    @Inject
//    @InteractorSpe(DomainConfig.REFRESH_TICKET)
//    Lazy<Ext1Interactor<String, MultiResult<Object>>> refreshTicket;

    @Inject
    @InteractorSpe(DomainConfig.UNBIND_DISK_MOBILE)
    Lazy<Ext1Interactor<String, Void>> unbindDiskMobile;

    @Inject
    @InteractorSpe(DomainConfig.SET_SAFELOCK_SETTINGS)
    Lazy<Ext2Interactor<Context, SettingBean[],Boolean[]>> saveSafeLockSettingUseCase;

    /**
     * 重启提示框
     */
    private CustomDialog mMaterialDialog;

    //是否是第一次或者清空数据或者忘记密码 进入安通+，安全锁的设置
    public static final String IS_FIRST_SAFELOCK = "-1";
    //忘记密码进来的安全锁设置
    private static final String IS_FORGET_PASSWORD = "-2";
    final int HANDLE_TIME_DELAY = 50;
    final int SYSTEM_GC_TIME = 1200;
    final int RED_MAX_COUNT = 1200;



    private TabFragmentAdapter buildAdapter(final List<Fragment> fragments){
        return new TabFragmentAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int[] onIconSelect(int position) {
                int icon[] = new int[2];
                icon[0] = mIconSelect[position];
                icon[1] = mIconNormal[position];
                return icon;
            }

            @Override
            public String onTextSelect(int position) {
                return getString(mTitle[position]);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        };
    }

    @Override
    protected void preBindView(Bundle savedInstanceState) {
        super.preBindView(savedInstanceState);
        ActivityStack.getInstanse().pop2TopActivity(true);
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        if (getActivityPostUseCaseComponent() != null) {
            getActivityPostUseCaseComponent().inject(this);
            busProvider.register(this);
        } else {
            LogUtil.getUtils().e("UseCaseComponent为空，重新进入LauncherPresenter！");
            return;
        }

        List<Fragment> fragments = new ArrayList<>();

        //fragments.add(buildFragment("THIS IS IM"));
        fragments.add(MxModuleProxyImp.getInstance().createChatListFragment());
        //fragments.add(buildFragment("THIS IS VOIP"));
        fragments.add(VoipFunction.getInstance().getVoipCallLogFragment());
//        fragments.add(buildFragment("THIS IS CONTACT"));
        fragments.add(ContactModuleProxy.createContactFragment());
//        fragments.add(buildFragment("THIS IS APPSTORE"));
        //私有化部署版本，隐藏“更多”标签页。gbc 2017-02-20
        if (!CustInfo.isCustom() && CommonUtils.isZH(this)) {
            fragments.add(new AppStorePresenter());
        }

        getVu().setFragmentAdapter(buildAdapter(fragments));

        changeInitFragment(getIntent());

        initParams();

        registPatternLockReceiver();

       // isSafeLockExist();

        /**
         * 弹出安全锁的Dialog框
         */
        Handler handler = new Handler();
        if (SettingServer.getSafeLock().equals(IS_FORGET_PASSWORD)) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    safeLockDialog();
                }
            };
            handler.postDelayed(runnable,HANDLE_TIME_DELAY);
        }


        if (UniversalUtil.isXposed()) {
            //GetAppStrategy.getStrategy(this, null);
            StrategysUtils.updateStrategys(this);
            startHookCheck();
            //注册系统设置广播
            // registRec();
        }

        //[S]add by lixiaolong on 20160827. for OOM. review by wangchao1.
        handler.postDelayed(new Runnable() {
            @SuppressWarnings("CallToSystemGC")
            @Override
            public void run() {
                System.gc();
            }
        }, SYSTEM_GC_TIME);
        //[E]add by lixiaolong on 20160827. for OOM. review by wangchao1.
        //add by gbc for statistics skip bind mobile. 2016-11-30 begin
        if (SharePreferceUtil.getPreferceUtil(getApplicationContext()).getIsSkipBindMobile()) {
            skipBindingNumber();
        }
        //add by gbc for statistics skip bind mobile. 2016-11-30 end
        //add by gbc for report last close actoma mode. 2016-12-02. begin
        if (SharePreferceUtil.getPreferceUtil(getApplicationContext()).getIsCloseActoma()) {
            closeAppMode(SharePreferceUtil.getPreferceUtil(getApplicationContext()).getCloseActomaMode());
        }
        //add by gbc for report last close actoma mode. 2016-12-02. end
        //test code
        //mHandler.sendEmptyMessageDelayed(0 , 10000);

        //[S]fix bug 7236 by licong,2016/12/27
        //当安通家不管在什么情况下设置为英文，如果第三方加密通道打开，则立即关掉
        if (UniversalUtil.getLanguageType(this) == UniversalUtil.LANGUAGE_EN) {
            BroadcastManager.sendBroadcastCloseTransfer();
            BroadcastManager.openFrameSafeSwitch("");
        }
        //[E]fix bug 7236 by licong,2016/12/27
    }

    /**
     * Hook升级检测
     */
    private void startHookCheck() {
        hookUpdateUseCase.get().fill(getApplicationContext()).execute(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {

            }

            // [Start] Modify by LiXiaolong<mailTo: lxl@xdja.com> on 2016-08-16. Review by WangChao1.
            @Override
            public void onError(Throwable e) {
                if (e != null && !TextUtils.isEmpty(e.getMessage())) {
                    LogUtil.getUtils().e("检测Hook升级失败，失败信息：\r\n" + e.getMessage());
                } else {
                    LogUtil.getUtils().e("检测Hook升级失败，失败信息：未知错误");
                }
            }

            @Override
            public void onNext(Boolean aBoolean) {
                LogUtil.getUtils().i("Hook升级结果为 ： " + aBoolean);
                //hook升级成功
                //alh@xdja.com<mailto://alh@xdja.com> 2016-12-30 add. fix bug 7705 . review by wangchao1. Start
                if (aBoolean && CommonUtils.isZH(MainFramePresenter.this)) {
                    alertRebootDialog();
                }
                //alh@xdja.com<mailto://alh@xdja.com> 2016-12-30 add. fix bug 7705 . review by wangchao1. End
            }
            // [End] Modify by LiXiaolong<mailTo: lxl@xdja.com> on 2016-08-16. Review by WangChao1.
        });
    }

    /**
     * 弹出重启提示框
     */
    private void alertRebootDialog() {
        if (mMaterialDialog == null) {
            mMaterialDialog = new CustomDialog(this);
        }

        if (!mMaterialDialog.isShowing()) {
            mMaterialDialog.setTitle(R.string.open_encrypt_sever_error_title)
                    .setMessage(R.string.open_encrypt_sever_error_message)
                    .setNegativeButton(
                            getString(R.string.certain), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mMaterialDialog.dismiss();
                                }
                            }
                    )
                    .setCanceledOnTouchOutside(true)
                    .show();
        }
    }

    /**
     * 初始化第三方加密所需参数
     */
    private void initParams() {
        LogUtil.getUtils().i("ckms initParams ************" + UniversalUtil.isXposed());
        //全局唯一一次赋值的地方
        //私有化版本，不需要第三方加密功能。gbc 2017-02-20
        stateParams.setIsSeverOpen(UniversalUtil.isXposed() && CommonUtils.isZH(this) && !CustInfo.isCustom());

        //设置提供给外部的快速开启应用设置列表
        setParams();
        //设置提供给外部的快速开启应用开启状态
        SettingBean thirdAppBean = SettingServer.querySetting(SettingBean.THIRDAPP);
        if (thirdAppBean != null) {
            stateParams.setIsQuickOpenThirdAppOpen(Boolean.parseBoolean(thirdAppBean.getValue()));
        } else {
            stateParams.setIsQuickOpenThirdAppOpen(true);
        }
        //实例化第三方应用加密服务通知栏管理器
        manager = new EncryptManager();

        //获取第三方应用加密服务信息
        SettingBean bean = SettingServer.querySetting(SettingBean.SEVER);
        if (bean != null) {
            //设置供外部获取的第三方应用加密服务开启状态
//            stateParams.setIsSeverOpen(Boolean.valueOf(bean.getValue()));
            if (stateParams.isSeverOpen()) {
                getVu().setSelectedFragment(TabTipsEvent.INDEX_CONTACT);
            }
            //设置加解密模块持久化第三方应用加密服务状态数据
            //wangchao_for_3rdenc
            //ActomaApp.getActomaApp().getAccountInfo().setIsEncryptSeverOpen(bean.getValue());
        } //else {
            //设置供外部获取的第三方应用加密服务开启状态
            //modify by thz 默认修改成开关为开 2016-2-24
//            stateParams.setIsSeverOpen(true);
            //设置加解密模块持久化第三方应用加密服务状态数据
            //wangchao_for_3rdenc
            //ActomaApp.getActomaApp().getAccountInfo().setIsEncryptSeverOpen(String.valueOf(true));
       // }
        //获取安全口令验证是否开启
        SettingBean vertifyBean = SettingServer.querySetting(SettingBean.VERTIFY);
        if (vertifyBean != null) {
            stateParams.setIsPassWordVertifyOpen(Boolean.parseBoolean(vertifyBean.getValue()));
        }
        //获取快速开启第三方应用是否开启
        SettingBean thirdApp = SettingServer.querySetting(SettingBean.THIRDAPP);
        if (thirdApp != null) {
            stateParams.setIsQuickOpenThirdAppOpen(Boolean.parseBoolean(thirdApp.getValue()));
        }
        //设置提供给外部的快速开启应用设置的开关状态
        SettingBean bean1 = SettingServer.querySetting(SettingBean.THIRDAPP);
        if (bean1 != null) {
            stateParams.setIsQuickOpenThirdAppOpen(Boolean.parseBoolean(bean1.getValue()));
        }
        //注册死亡通知
        IEncryptUtils.registerDeathNotification();
    }

    /**
     * 设置提供给外部的快速开启应用设置列表
     */
    private void setParams() {
        StrategysUtils.queryStrategys(MainFramePresenter.this);
//        List<QuickOpenAppBean> result = QuickOpenAppServer.queryAllData(getApplicationContext());
//        if (result == null || result.size() == 0) {
//            List<EncryptListBean> list = EncryptListServer.queryAllEncryptListData(getApplicationContext());
//            List<QuickOpenThirdAppListBean> appListBeans =
//                    ListUtil.initQuickOpenThirdAppListData(getApplicationContext(), list);
//            result = new ArrayList<>();
//            for (int i = 0; i < appListBeans.size(); i++) {
//                result.add(appListBeans.get(i).getQuickOpenAppBean());
//            }
//        }
//        stateParams.setQuickOpenAppBeanList(result);
    }


    private Fragment buildFragment(String content) {
        Fragment fragment = new MockFragment();
        Bundle bundle = new Bundle();
        bundle.putString("content", content);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void setting() {
        Navigator.navigateToSettingActivity();
    }

    @Override
    public void scan() {
        if (ContactModuleService.checkNetWork()) {
            Navigator.navigateToCaptureActivity();
        }
    }

    @Override
    public void search() {
        Navigator.navigateToSearchUser();
    }

    @Override
    public void addUser() {
        Navigator.navigateToAddUser();
    }

    @Override
    public void createGroup() {
        Navigator.navigateToPickPeople();
    }

    //tangsha@xdja.com 2016-08-09 add. for open third transfer. review by self. Start
    @Inject
    @InteractorSpe(DomainConfig.OPEN_THIRD_ENCRYPT_TRANSFER)
    Lazy<Ext4Interactor<String,String,String,String,Boolean>> openThirdEncryptUseCase;
    @Override
    public void openThirdTransfer() {
        final String friendAccount = StateParams.getStateParams().getEncryptAccount();
        final String currentAccount = ContactUtils.getCurrentAccount();
        final String pkgName =  StateParams.getStateParams().getPkgName();
        //tangsha@xdja.com 2016-08-11 add. for get third encrypt app name to show in notification . review by self. Start
        final String appName = StateParams.getStateParams().getAppName();
        //tangsha@xdja.com 2016-08-11 add. for get third encrypt app name to show in notification . review by self. End
        executeInteractorNoRepeat(openThirdEncryptUseCase.get()
                        .fill(currentAccount,friendAccount,pkgName,""),
                new LoadingDialogSubscriber<Boolean>(this,this){
                    boolean openRes = false;
                    @Override
                    public void onNext(Boolean aBoolean) {
                        super.onNext(aBoolean);
                        LogUtil.getUtils().d("MainFramePresenter openThirdTransfer onNext==> aBoolean "+aBoolean);
                        openRes = aBoolean;
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        LogUtil.getUtils().d("MainFramePresenter openThirdTransfer onCompleted==> ");
                        //告知联系人开启加密服务
                        getVu().closeCirCleMenu();
                        if(openRes) {
                            //[S] modify by lixiaolong on 20160824. fix bug 3079、3191、3272. review by wangchao1.
                            ContactModuleProxy.safeTransferOpened(MainFramePresenter.this, friendAccount, pkgName);
                            //打开通知栏
                            String showName = ContactModuleProxy.getContactInfo(friendAccount).getName();
                            //[E] modify by lixiaolong on 20160824. fix bug 3079、3191、3272. review by wangchao1.
                            showName = ContactModulEncryptReceiver.getCanShowNickName(showName);
                            new EncryptManager().changeNotificationContent(MainFramePresenter.this,
                                    showName,appName);
                            //打开第三方应用
                            startAppforPackageName(MainFramePresenter.this, pkgName);
                        }else{
                            getVu().showOpenEncryptErrorDialog("");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        getVu().closeCirCleMenu();
                        //[S]add by lixiaolong on 20161008. add error logs. review by myself.
                        if (e != null && !TextUtils.isEmpty(e.getMessage())) {
                            LogUtil.getUtils().e("MainFramePresenter openThirdTransfer onError: " + e.getMessage());
                            getVu().showOpenEncryptErrorDialog(e.getMessage());
                        } else {
                            getVu().showOpenEncryptErrorDialog("");
                            LogUtil.getUtils().e("MainFramePresenter openThirdTransfer onError: unknown error");
                        }
                        //[E]add by lixiaolong on 20161008. add error logs. review by myself.
                    }
                }.registerLoadingMsg(getString(R.string.open_encryption_channel)));
    }

    /**
     * 通过包名打开对应应用
     *
     * @param context     上下文句柄
     * @param packagename 要打开的应用包名
     */
    private void startAppforPackageName(Context context, String packagename) {
        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }
        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);
        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = context.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);
        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            //modify by 唐会增 2016-6-21 适配第三方自定义的launcher的界面
            if (ThirdEncAppProperty.mmsHash.containsKey(packageName)) {
                String configName = ThirdEncAppProperty.mmsHash.get(packageName);
                if (!TextUtils.isEmpty(configName)) {
                    className = configName;
                }
            }

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            context.startActivity(intent);
        }
    }
    //tangsha@xdja.com 2016-08-09 add. for open third transfer. review by self. End
    @NonNull
    @Override
    protected Class<? extends VuMainFrame> getVuClass() {
        return ViewMainFrame.class;
    }

    @NonNull
    @Override
    protected MainFrameCommand getCommand() {
        return this;
    }

    @Override
    protected boolean isMoveTaskToBack() {
        return true;
    }

    //alh@xdja.com<mailto://alh@xdja.com> 2016-11-03 add. fix bug 5645 . review by wangchao1. Start

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.v(TAG , "MainFramePresenter -> onKeyDown keyCode : " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK){
            mIsOnkeyDown = true;
            if (getVu().getArcView() != null && getVu().getArcView().isShown()){
                return getVu().getArcView().hide();
            }
           return moveTaskToBack(true);
        }
        return super.onKeyDown(keyCode, event);
    }

    //alh@xdja.com<mailto://alh@xdja.com> 2016-11-03 add. fix bug 5645 . review by wangchao1. End

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        changeInitFragment(intent);
    }

    /**
     * 统计未读消息  显示红点
     */
    private TabTipsEvent showRedItem2() {
        FriendRequestService service = new FriendRequestService();
        int count = service.countNewFriend();
        TabTipsEvent event = new TabTipsEvent();
        event.setIndex(TabTipsEvent.INDEX_CONTACT);
        if (count > RED_MAX_COUNT) {
            event.setContent("99");
            event.setIsShowPoint(true);
        } else if (count > 0) {
            event.setContent(String.valueOf(count));
            event.setIsShowPoint(true);
        } else if (count <= 0) {
            event.setIsShowPoint(false);
        }
        return event;
    }

    /**
     * 更改初始显示的Fragment
     *
     * @param intent
     */
    private void changeInitFragment(Intent intent) {
        if (intent != null) {
            @TabTipsEvent.POINT_DEF int index =
                    intent.getIntExtra(ARG_PAGE_INDEX, -1);
            if (index >= 0) {
                LogUtil.getUtils().i("==主页面初始化索引为 ： " + index + "==");
                getVu().setSelectedFragment(index);
            }
        }
    }
    @Subscribe
    public void changeCurrentTab(ChangeTabIndexEvent event) {
        if (event == null) {
            return;
        }
        getVu().setSelectedFragment(event.getIndex());
    }

    /**
     * 更新Tab上显示的小红点的内容
     *
     * @param event 事件对象
     */
    @Subscribe
    public void updateTabTips(TabTipsEvent event) {
        if (event == null) {
            return;
        }
        final boolean isClear = !event.isShowPoint();
        final CharSequence content = event.getContent();
        final int index = event.getIndex();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updatePoint(isClear, content, index);
            }
        });
    }

    private static final int START_ENCRYPT_GUIDE = 200;
    private Point point = null;

    @Subscribe
    public void setArcLayoutCenter(Point center) {
        point = center;
        LogUtil.getUtils().e("FabClick: MainFramePresenter setArcLayoutCenter==> ");
        if (SharePreferceUtil.getPreferceUtil(this).getIsFirstOpenSever()
                && UniversalUtil.isXposed()) {
            Intent intent = new Intent().setClass(this, EncryptGuidePresenter.class);
            startActivityForResult(intent, START_ENCRYPT_GUIDE);
        } else {
            getVu().getArcLayoutAnimation().setCenter(MainFramePresenter.this, center);
        }
//        boolean isOpen1 = getVu().getCirCleMenuIsOpen();
//        if (isOpen1) {
//        } else {
//        }
    }

    private void updatePoint(boolean isClear, CharSequence content, int index) {
        switch (index) {
            case TabTipsEvent.INDEX_CHAT:
                if (isClear) {
                    getVu().clearMsgTabTips();
                } else {
                    if (!TextUtils.isEmpty(content)) {
                        getVu().updateMsgTabTips(content);
                    }
                }
                break;
            case TabTipsEvent.INDEX_VOIP:
                if (isClear) {
                    getVu().clearPhoneTabTips();
                } else {
                    if (!TextUtils.isEmpty(content)) {
                        getVu().updatePhoneTabTips(content);
                    }
                }
                break;
            case TabTipsEvent.INDEX_CONTACT:
                if (isClear) {
                    getVu().clearContactTabTips();
                } else {
                    if (!TextUtils.isEmpty(content)) {
                        getVu().updateContactTabTips(content);
                    }
                }
                break;
            case TabTipsEvent.INDEX_APP:
                if (isClear) {
                    getVu().clearMoreTabTips();
                } else {
                    if (!TextUtils.isEmpty(content)) {
                        getVu().updateMoreTabTips(content);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTabTips(showRedItem2());
        //[S]modify by lixiaolong on 20160830. fix bug 3116. review by wangchao1.
        if (getActivityPostUseCaseComponent() != null) {
            //检查展示升级版本
            checkUpdateNew();
        }
        //[E]modify by lixiaolong on 20160830. fix bug 3116. review by wangchao1.

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (busProvider != null){
            busProvider.unregister(this);
        }

        if (patternLockReceiver != null) {
            unregisterReceiver(patternLockReceiver);
        }

    }

    public static class MockFragment extends Fragment {

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setLayoutParams(
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT)
            );
            TextView textView = new TextView(getContext());
            textView.setLayoutParams(
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            );
            textView.setText(getArguments().getString("content"));

            linearLayout.addView(textView);
            return linearLayout;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Navigator.SCAN_TAG) {
            if (resultCode == RESULT_OK) {
                if (Navigator.handleScanResultEvent(data) == Navigator.SCAN_ERROR_CODE_INVALID){
                    XToast.show(ActomaApplication.getInstance(), getString(R.string.scan_failed));
                }
            }
        }
    }


    /**
     * 检测是否有新版本
     */
    private void checkUpdateNew() {
        checkUpdateUseCase.get().fill(this).execute(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                // [Start] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-11. for check update.
//                boolean isShowNewView = SharePreferceUtil.getPreferceUtil(MainFramePresenter.this).
//                        getIsShowNewView();
//                if (isShowNewView) {
//                    getVu().freshUpdateNew(aBoolean);
//                }
                if (aBoolean != null) {
                    getVu().freshUpdateNew(aBoolean);
                }
                // [End] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-11. for check update.
            }
        });
    }

    //start:add by wangalei for 996
    @SuppressWarnings("UnusedParameters")
    @Subscribe
    public void updateContactTabTips(UpdateContactTabTipsEvent event) {
        updateTabTips(showRedItem2());
    }
    //end:add by wangalei for 996



    /**
     * 安全锁忘记密码使用的弹框
     */
    private void safeLockDialog() {
        customDialog = new CustomDialog(this);
        customDialog.setTitle(getResources().getString(R.string.safe_hint_message))
                .setMessage(getResources().getString(R.string.lockscreen_old_clear_reset))
                .setPositiveButton(getResources().getString(R.string.lockscreen_reset), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), SettingSafeLockPresenter.class);
                        startActivity(intent);
                    }
                }).setNegativeButton(getResources().getString(R.string.safe_cancel),new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //[S] fix bug 7691 by licong for safeLock
                preferencesUtil.setPreferenceBooleanValue("dismiss" ,true);
                //[E] fix bug 7691 by licong for safeLock
                customDialog.dismiss();
            }
        }).setCanceledOnTouchOutside(true);

        if (!customDialog.isShowing() && !preferencesUtil.gPrefBooleanValue("dismiss",false)){
            customDialog.show();
        }
    }

    //接收安全锁灭屏广播
    private void registPatternLockReceiver(){
        patternLockReceiver = new PatternLockReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SafeLockApplication.SCREEN_BROADCAST);
        registerReceiver(patternLockReceiver, filter);
    }

    /**
     * 收到灭屏广播之后，处理安全锁相应的业务
     */
    private class PatternLockReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent intentGesture = new Intent(context, OpenGesturePresenter.class);
            context.startActivity(intentGesture);
        }
    }


    /**
     * 刷新是有新更新的标识
     *
     * @param event
     * gbc 2016-08-08 检测到升级版本，主界面显示升级提示
     */
    @Subscribe
    public void freshUpdate(FreshUpdateNewEvent event) {
        // [Start] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-11. for check update.
//        boolean isShowNewView = SharePreferceUtil.getPreferceUtil(MainFramePresenter.this).
//                getIsShowNewView();
//        if (isShowNewView) {
//            getVu().freshUpdateNew(event.isHaveUpdate());
//        }
        if (event != null) {
            getVu().freshUpdateNew(event.isHaveUpdate());
        }
        // [End] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-11. for check update.
    }

    /**
     *上报跳过绑定手机号码动作行为
     */
    private void skipBindingNumber() {
        String ticket = PreferencesServer.getWrapper(ActomaController.getApp()).gPrefStringValue("ticket");
        StringResult tfCardIdResult = TFCardManager.getCardId();
        String cardId = "";
        if (tfCardIdResult != null && tfCardIdResult.getErrorCode() == 0) {
            cardId = tfCardIdResult.getResult();//cardId
        }
        reportClientMessage.reportClientMessage_skipBindMobile(ticket,
                ContactUtils.getCurrentAccount(),
                cardId,
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        LogUtil.getUtils().e(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            try {
                                String body = response.body().string();
                                JSONObject jsonObject = null;
                                jsonObject = new JSONObject(body);
                                if (null != jsonObject &&jsonObject.has("error")) {
                                    LogUtil.getUtils().e("report skip bind mobile number has error!");
                                    return;
                                }
                                //reset clear sharedpreference
                                SharePreferceUtil.getPreferceUtil(ActomaController.getApp()).setIsSkipBindMobile(false);
                                LogUtil.getUtils().e("report skip bind mobile success!");
                            } catch (JSONException e) {
                                //e.printStackTrace();
                                LogUtil.getUtils().e("report skip bind mobile JSON parse have error", e);
                            }
                        }
                    }
                });
    }


    /**
     * 关闭应用前上报关闭模式，是否选择接收IM消息
     * @param checked
     */
    private void closeAppMode(int checked) {
        String ticket = PreferencesServer.getWrapper(ActomaController.getApp()).gPrefStringValue("ticket");
        StringResult tfCardIdResult = TFCardManager.getCardId();
        String cardId = "";
        if (tfCardIdResult != null && tfCardIdResult.getErrorCode() == 0) {
            cardId = tfCardIdResult.getResult();//cardId
        }
        reportClientMessage.reportClientMessage_closeAppMode(ticket,
                ContactUtils.getCurrentAccount(),
                cardId,
                checked,
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        LogUtil.getUtils().e(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            try {
                                String body = response.body().string();
                                JSONObject jsonObject = new JSONObject(body);
                                if (null != jsonObject &&jsonObject.has("error")) {
                                    LogUtil.getUtils().e("report close app close mode has error!");
                                    return;
                                }
                                //reset clear sharedpreference
                                SharePreferceUtil.getPreferceUtil(getApplicationContext()).setIsCloseActoma(false);
                                LogUtil.getUtils().e("report close app close mode success!");
                            } catch (JSONException e) {
                                //e.printStackTrace();
                                LogUtil.getUtils().e("report closeAppMode JSON parse have error", e);
                            }
                        }
                    }
                });
    }

    /**
     * 当安全锁密码文件不存在的情况下，恢复未设置状态
     */
    public void isSafeLockExist() {
        if (!lockPatternUtils.isExistsFile()) {
            //实例化安全锁信息用于保存
            SettingBean bean1 = new SettingBean();
            bean1.setKey(SettingBean.SAFE_LOCK);
            bean1.setValue(IS_FIRST_SAFELOCK);
            //实例化锁屏锁定信息用于保存
            SettingBean bean2 = new SettingBean();
            bean2.setKey(SettingBean.LOCK_SCREEN);
            bean2.setValue(String.valueOf(false));
            //实例化后台运行锁定信息用于保存
            SettingBean bean3 = new SettingBean();
            bean3.setKey(SettingBean.LOCK_BACKGROUND);
            bean3.setValue(String.valueOf(false));

            //执行保存操作
            saveSafeLockSettingUseCase.get().fill(this, new SettingBean[]{bean1, bean2, bean3})
                    .execute(new Action1<Boolean[]>() {
                        @Override
                        public void call(Boolean[] booleen ) {
                        }
                    });
        }

    }

    @SuppressWarnings("UnusedParameters")
    @Subscribe
    public void onReceiveUnbindMobileEvent(UnBindDeviceObservable.UnBindMobileEvent event) {
        executeInteractorNoRepeat(unbindDiskMobile.get().fill(""), new PerSubscriber<Void>(MainFramePresenter.this) {
        });
    }

}

