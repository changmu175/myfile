package com.xdja.presenter_mainframe;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.squareup.otto.Subscribe;
import com.xdja.CommonApplication;
import com.xdja.comm.blade.accountLifeCycle.AccountLifeCycle;
import com.xdja.comm.ckms.CkmsGpEnDecryptManager;
import com.xdja.comm.cust.CustInfo;
import com.xdja.comm.event.BusProvider;
import com.xdja.comm.event.CloseAppEvent;
import com.xdja.comm.event.MoveToBackEvent;
import com.xdja.comm.event.TicketAuthErrorEvent;
import com.xdja.data_mainframe.di.CacheModule;
import com.xdja.data_mainframe.di.PostRepositoryModule;
import com.xdja.data_mainframe.di.PostStoreModule;
import com.xdja.data_mainframe.di.PreRepositoryModule;
import com.xdja.data_mainframe.di.PreStoreModule;
import com.xdja.data_mainframe.di.UserCacheModule;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.domain_mainframe.di.PreUseCaseModule;
import com.xdja.domain_mainframe.model.Account;
import com.xdja.frame.ApplicationLifeCycle;
import com.xdja.frame.presenter.ActivityStack;
import com.xdja.frame.widget.XToast;
import com.xdja.imp.ImApplication;
import com.xdja.presenter_mainframe.autoupdate.UpdateManager;
import com.xdja.presenter_mainframe.di.components.post.UserComponent;
import com.xdja.presenter_mainframe.di.components.pre.AppComponent;
import com.xdja.presenter_mainframe.di.components.pre.DaggerAppComponent;
import com.xdja.presenter_mainframe.di.components.pre.PreUseCaseComponent;
import com.xdja.presenter_mainframe.di.modules.AppModule;
import com.xdja.presenter_mainframe.di.modules.PostModule;
import com.xdja.presenter_mainframe.global.GlobalLifeCycle;
import com.xdja.presenter_mainframe.presenter.LogoutHelper;
import com.xdja.presenter_mainframe.util.CrashHandler;
import com.xdja.presenter_mainframe.widget.SafeLockApplication;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

/**
 * Created by xdja-fanjiandong on 2016/3/17.
 */
public class ActomaApplication extends SafeLockApplication {
    private static ActomaApplication sInstance;
    private AppComponent appComponent;
//    public AllEncryptInfo encryptInfo = new AllEncryptInfo();//wangchao temp code

    private UserComponent userComponent;

    private GlobalLifeCycle appGlobalLife, userGlobalLife;
    private AccountLifeCycle accountLifeCycle;
    private Handler mHandler;
    private boolean mIsSilentRunning = false;
    private static final int TIME_DIFF = 2000;

    @Nullable
    public AppComponent getAppComponent() {
        return this.appComponent;
    }

    @Nullable
    public UserComponent getUserComponent() {
        return userComponent;
    }

    /**
     * 初始化登录成功后的注入容器
     *
     * @param account 帐号
     * @param ticket  Ticket
     */
    /*[S]modify by tangsha@20170222 for ckms fail to logout, but contact task start then ticket invalie*/
    public void createUserComponent(@NonNull Account account, @Nullable String ticket,boolean forLogout) {
        this.userComponent = this.appComponent.plus(
                new PostModule(),
                new UserCacheModule(account, ticket),
                new PostRepositoryModule(),
                new PostStoreModule()
        );
        this.userGlobalLife = this.userComponent.globalLifeCycle();
        this.userGlobalLife.create();
        //开始account生命周期
        accountLifeCycle = userComponent.accountLifeCycle();
        if(forLogout == false) {
            accountLifeCycle.login();
        }
        String preAccount = appComponent.sharedPreferencesUtil().gPrefStringValue(CacheModule.KEY_PRE_ACCOUNT_IN_AFTER_LOGIN);
        if (preAccount!=null&&!preAccount.isEmpty()&&!preAccount.equals(account.getAccount())){
            accountLifeCycle.accountChange();
        }
    }

    public void createUserComponent(@NonNull Account account, @Nullable String ticket) {
        createUserComponent(account, ticket, false);
    }
	/*[E]modify by tangsha@20170222 for ckms fail to logout, but contact task start then ticket invalie*/

    public void releaseAppComponent() {
        releaseApplicationComponent();

        if (this.appGlobalLife != null) {
            this.appGlobalLife.destroy();
            this.appGlobalLife = null;
        }
        this.appComponent = null;
    }

    public void releaseUserComponent() {
        LogUtil.getUtils().e("releaseUserComponent");
        if (this.userGlobalLife != null) {
            this.userGlobalLife.destroy();
            this.userGlobalLife = null;
        }
        if (accountLifeCycle!=null){
            accountLifeCycle.logout();
            accountLifeCycle = null;
        }
        this.userComponent = null;
    }

    public static Application getInstance(){
        return sInstance;
    }

    @Override
    public void onCreate() {
        // [S] modify by LiXiaolong<mailTo:lxl@xdja.com> on 20160905. fix bug 3482.
        setLoggable(BuildConfig.DEBUG);// com.xdja.dependence.uitls.LogUtil
        // [E] modify by LiXiaolong<mailTo:lxl@xdja.com> on 20160905. fix bug 3482.
        //记录当前版本信息，所有模块共享
        CustInfo.setCurrentFlavor(BuildConfig.FLAVOR);
        super.onCreate();
        sInstance = this;
        //start: for multi_process by wangchao 20160820
        String process = getCurrentProcessName();
        //alh@xdja.com<mailto://alh@xdja.com> 2016-08-23 add. fix bug push lose . review by wangchao1. Start
        if (!TextUtils.isEmpty(process) && !getResources().getString(R.string.process_name).equals(process)) {
            LogUtil.getUtils().e("安通+独立进程（"+process+"）启动，不需要执行安通+主进程的流程");
            return;
        }
        //alh@xdja.com<mailto://alh@xdja.com> 2016-08-23 add. fix bug push lose . review by wangchao1. End

        LogUtil.getUtils().e("安通+主进程（"+process+"）启动");
        //end: wangchao for multi_process by wangchao 20160820

        //[S]add by lixiaolong on 20160830. for clean actoma+ V1 data. review by wangchao1.
        new UpdateManager().cleanActomaV1Data(this);
        //[E]add by lixiaolong on 20160830. for clean actoma+ V1 data. review by wangchao1.

        //tangsha@xdja.com 2016-08-03 add. for config open or close ckms flow . review by self. Start
        CkmsGpEnDecryptManager.setCkmsOpenTag(BuildConfig.OPEN_CKMS);
        CkmsGpEnDecryptManager.setCkmsContext(this);
        //tangsha@xdja.com 2016-08-03 add. for config open or close ckms flow . review by self. End

        mHandler = new Handler();
        this.appComponent = DaggerAppComponent
                .builder()
                .applicationComponent(getApplicationComponent())
                .appModule(new AppModule(this))
                .preRepositoryModule(new PreRepositoryModule())
                .preStoreModule(new PreStoreModule())
                .cacheModule(new CacheModule())
                .build();
        this.appGlobalLife = this.appComponent.globalLifeCycle();
        this.appGlobalLife.create();
        createApplication();
        BusProvider.getMainProvider().register(this);

        CrashHandler crashHandler = CrashHandler.getInstance();
        //注册crashHandler
        if (BuildConfig.LOGABLE) {
            crashHandler.setEnable(false);
        } else {
            crashHandler.setEnable(true);
        }
        crashHandler.init(this);

        Log.i("", "isDebugable: " +isApkDebugable(this));
    }

    //[S]modify by tangsha for bug 3343(if actoma or safe_key_service killed, binder maybe null) @2016/08/27 [review by] tangsha
    private boolean ckmsInitOk = false;

    public void setCkmsInitOk(boolean init){
        ckmsInitOk = init;
    }

    public boolean getCkmsInitOk(){
        return ckmsInitOk;
    }
    //[E]modify by tangsha for bug 3343(if actoma or safe_key_service killed, binder maybe null) @2016/08/27 [review by] tangsha

    //modify by alh@xdja.com to fix bug: Looper.prepare() 2016-07-26 start (rummager : guobinchang)
    public final static String TICKET_STATUS = "TICKET_STATUS";

    private long mGapTime = 0;

    @SuppressWarnings("UnusedParameters")
    @Subscribe
    public void onReceiveEvent(TicketAuthErrorEvent event) {
        if (getUserComponent() == null) return;
        long curr = System.currentTimeMillis();
        long diff = curr - mGapTime;
        if (diff > 0 && diff < TIME_DIFF){
            return;
        }
        LogUtil.getUtils().e("onReceiveEvent TicketAuthErrorEvent");
        mGapTime = curr;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                LogoutHelper logoutHelper = new LogoutHelper();
                logoutHelper.navigateToLoginWithExit();
                boolean isLogout = logoutHelper.logout(null);
                if (ActomaApplication.getInstance() != null && isLogout) {
                    XToast.show(ActomaApplication.getInstance(), getString(R.string.login_past_due));
                }
            }
        });
    }
    //modify by alh@xdja.com to fix bug: Looper.prepare() 2016-07-26 end (rummager : guobinchang)

    @SuppressWarnings("UnusedParameters")
    @Subscribe
    public void onReceiveMoveToBackEvent(MoveToBackEvent event){
        ActivityStack.getInstanse().moveToBackAllActivities();
    }

    @SuppressWarnings("UnusedParameters")
    @Subscribe
    public void onReceiveCloseEvent(CloseAppEvent event){
        this.appGlobalLife.destroy();
        //[S]tangsha@xdja.com 2016-08-12 add. for exit and not receive message should release ckms. review by self.
        PreUseCaseComponent preUseCaseComponent = getAppComponent().plus(new PreUseCaseModule());
        preUseCaseComponent.ckmsReleaseUseCase().fill().execute(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Integer result) {
               Log.d("ActomaApplication", "ckmsReleaseUseCase result "+result);
            }
        });
		//[E]tangsha@xdja.com 2016-08-12 add. for exit and not receive message should release ckms. review by self.
    }

    @Override
    public List<ApplicationLifeCycle> initApplicationLifeCycle() {
        List<ApplicationLifeCycle> applicationLifeCycles = new ArrayList<>();
        applicationLifeCycles.add(new ImApplication());
        applicationLifeCycles.add(new CommonApplication());
        return applicationLifeCycles;
    }

    /**
     * Set the base context for this ContextWrapper.  All calls will then be
     * delegated to the base context.  Throws
     * IllegalStateException if a base context has already been set.
     *
     * @param base The new base context for this wrapper.
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private String getCurrentProcessName() {
        int myPid = Process.myPid();
        ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : am.getRunningAppProcesses()) {
            if (appProcess.pid == myPid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    private boolean isApkDebugable(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {

        }
        return false;
    }

    //添加全局变量，在后台锁屏的时候添加状态判断
    private static boolean screenLockerState = false;
    private static Object objLocker = new Object();

    public static boolean isScreenLockerState() {
        return screenLockerState;
    }

    public static void setScreenLockerState(boolean state) {
        screenLockerState = state;
    }

    public static Object getObjLocker() {
        return objLocker;
    }

    public boolean isSilentRunning(){
        return mIsSilentRunning;
    }

    public void setSilentRunning(boolean silentRunning){
        mIsSilentRunning = silentRunning;
    }

}
