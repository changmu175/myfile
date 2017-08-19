package com.xdja.presenter_mainframe.presenter.activity.setting;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.xdja.comm.data.SettingBean;
import com.xdja.comm.server.SettingServer;
import com.xdja.comm.uitl.handler.SafeLockUtil;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.domain_mainframe.model.Account;
import com.xdja.frame.data.persistent.PreferencesUtil;
import com.xdja.frame.domain.usecase.Ext0Interactor;
import com.xdja.frame.domain.usecase.Ext2Interactor;
import com.xdja.imp.util.BitmapUtils;
import com.xdja.presenter_mainframe.ActomaApplication;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.OpenGestureCommand;
import com.xdja.presenter_mainframe.presenter.LogoutHelper;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.uiInterface.OpenGestureVu;
import com.xdja.presenter_mainframe.ui.uiInterface.ViewOpenGesture;
import com.xdja.presenter_mainframe.util.LockPatternUtils;
import com.xdja.presenter_mainframe.widget.LockPatternView;
import com.xdja.presenter_mainframe.widget.SafeLockApplication;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
import rx.functions.Action1;
import util.CustomDialog;

/**
 * Created by licong on 2016/11/25.
 * 安全锁开启界面
 */
@InjectOption.Options(InjectOption.OPTION_POSTCACHEDUSER)
public class OpenGesturePresenter extends PresenterActivity<OpenGestureCommand, OpenGestureVu> implements  OpenGestureCommand,LockPatternView.OnPatternListener {
    @Inject
    @InteractorSpe(value = DomainConfig.GET_CURRENT_ACCOUNT_INFO)
    Lazy<Ext0Interactor<Account>> getCurrentAccountInfoUseCase;

    @Inject
    LockPatternUtils lockPatternUtils;

    @Inject
    LogoutHelper logoutHelper;

    PreferencesUtil preferencesUtil;

    public static final String MODIFIED_GESTURE = "openGesture";

    public static final String CHECK_GESTURE = "checkGesture";

    private static final String IS_FORGET_PASSWORD = "isForgetPassword";

    private static final int MODIFIEF_GESTURE = 1;

    private static boolean haveLockActivity = false;

    private static final int HANDLE_POST_TIME = 2000;
    private static final float SMALL_SCALL = 0.75f;

    /**
     * 忘记密码所要存储的状态
     */
    private static final String IS_FORGET_PW = "-2";

    //[S] fix  bug 7972 by licong
    //解决最近任务栏问题。
    boolean isBackpressed;
    //[E] fix  bug 7972 by licong
    

    /**
     * 密码输入错误的次数
     */
    private int mFailedPatternCount;

    /**
     * 输入错误的dialog
     */
    private CustomDialog errorDialog;

    /**
     * 忘记密码的dialog
     */
    private CustomDialog customDialog;

    private SafeLockApplication application;

    public static boolean isTransmit;


    @NonNull
    @Override
    protected Class<? extends OpenGestureVu> getVuClass() {
        return ViewOpenGesture.class;
    }

    @NonNull
    @Override
    protected OpenGestureCommand getCommand() {
        return this;
    }

    @Inject
    @InteractorSpe(DomainConfig.SET_SAFELOCK_SETTINGS)
    Lazy<Ext2Interactor<Context, SettingBean[],Boolean[]>> saveSafeLockSettingUseCase;


    /**
     * 初始化完成之后
     */
    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        setFullScreen();
        if (getActivityPostUseCaseComponent() != null) {
            getActivityPostUseCaseComponent().inject(this);
        }
        preferencesUtil = new PreferencesUtil(this);
        haveLockActivity = true;
        getVu().getLockPatternView().setOnPatternListener(this);
        mFailedPatternCount = preferencesUtil.gPrefIntValue("retry");
        application = (SafeLockApplication)getApplication();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCurrentAccountInfo();
        //[S] fix bug 7504 by licong ,for safeLock
        if (preferencesUtil.gPrefIntValue("retry") >= LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT) {
            showErrorDialog();
        }
        if (getIntentExtra() == MODIFIEF_GESTURE) {
            getVu().setText(MODIFIED_GESTURE,LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT - preferencesUtil.gPrefIntValue("retry"));
        } else {
            getVu().setText(CHECK_GESTURE, LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT - preferencesUtil.gPrefIntValue("retry"));
        }
        //[E] fix bug 7504 by licong ,for safeLock
    }

    /**
     * 获取当前的账户信息
     */
    private void getCurrentAccountInfo() {
        getCurrentAccountInfoUseCase.get().fill().execute(new Action1<Account>() {
            @Override
            public void call(Account account) {
                if (account.getAvatarId() != null)
                    getVu().setImage(account.getAvatarId(), account.getThumbnailId());
            }
        });
    }

    @Override
    public void onPatternStart() {
        getVu().getLockPatternView().removeCallbacks(mClearPatternRunnable);
    }

    @Override
    public void onPatternCleared() {
        getVu().getLockPatternView().removeCallbacks(mClearPatternRunnable);
    }


    @Override
    public void onPatternDetected(List<LockPatternView.Cell> pattern) {
        if (pattern == null)
            return;
        if (lockPatternUtils.checkPattern(pattern)) {
            cancelFullScreen();
            getVu().getLockPatternView()
                    .setDisplayMode(LockPatternView.DisplayMode.Correct);
            if (getIntentExtra() == MODIFIEF_GESTURE) {
                Intent intent = new Intent(this, SettingGesturePresenter.class);
                startActivity(intent);
            }
            finish();
            synchronized (ActomaApplication.getObjLocker()) {
                ActomaApplication.setScreenLockerState(false);
            }
            haveLockActivity = false;
            preferencesUtil.setPreferenceIntValue("retry",0);
        } else {
            getVu().getLockPatternView()
                    .setDisplayMode(LockPatternView.DisplayMode.Wrong);
            if (pattern.size() >= LockPatternUtils.MIN_PATTERN_REGISTER_FAIL) {
                mFailedPatternCount++;
                int retry = LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT
                        - mFailedPatternCount;
                preferencesUtil.setPreferenceIntValue("retry",mFailedPatternCount);
                if (retry >= 0) {
                    if (retry == 0) {
                        showErrorDialog();
                    } else {
                        getVu().setText(MODIFIED_GESTURE, retry);
                    }
                }
            } else {
                Toast.makeText(this, getResources().getString(R.string.lockpattern_recording_incorrect_too_short), Toast.LENGTH_SHORT).show();
            }
            postClearRunnable();
        }
    }

    private Runnable mClearPatternRunnable = new Runnable() {
        public void run() {
            getVu().getLockPatternView().clearPattern();
        }
    };

    private void postClearRunnable(){
        getVu().getLockPatternView().removeCallbacks(mClearPatternRunnable);
        getVu().getLockPatternView().postDelayed(mClearPatternRunnable,HANDLE_POST_TIME);
    }

    @Override
    public void logOut(String isType) {
        haveLockActivity = false;
        logoutHelper.navigateToLoginWithExit();
        logoutHelper.logout(null);
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
        if (isType.equals(IS_FORGET_PASSWORD)) {
            preferencesUtil.setPreferenceBooleanValue(IS_FORGET_PASSWORD, true);
            setSafeLockState();
        }
    }

    @Override
    public void showDialog() {
        if (customDialog == null) {
            customDialog = new CustomDialog(this);
            customDialog.setTitle(getResources().getString(R.string.safe_hint_message))
                    .setMessage(BitmapUtils.formatAnTongSpanContent(getResources().getString(R.string.safe_login_close_gesture),
                            this, SMALL_SCALL, BitmapUtils.AN_TONG_DETAIL_PLUS))
                    .setPositiveButton(getResources().getString(R.string.safe_confirm), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getCommand().logOut(IS_FORGET_PASSWORD);
                            synchronized (ActomaApplication.getObjLocker()) {
                                ActomaApplication.setScreenLockerState(false);
                            }
                            preferencesUtil.setPreferenceIntValue("retry",0);
                            preferencesUtil.setPreferenceBooleanValue("dismiss",false);
                        }
                    }).setNegativeButton(getResources().getString(R.string.safe_cancel), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    customDialog.dismiss();
                }
            }).setCancelable(false);
        }
        if (!customDialog.isShowing()) {
            customDialog.show();
        }
    }

    public void showErrorDialog() {
        if (errorDialog == null) {
            errorDialog = new CustomDialog(this);
            errorDialog.setTitle(getResources().getString(R.string.safe_hint_message))
                    .setMessage(BitmapUtils.formatAnTongSpanContent(getResources().getString(R.string.safe_input_error_five_re_login),
                            this, SMALL_SCALL, BitmapUtils.AN_TONG_DETAIL_PLUS))
                    .setPositiveButton(getResources().getString(R.string.safe_re_login), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getCommand().logOut(CHECK_GESTURE);
                            synchronized (ActomaApplication.getObjLocker()) {
                                ActomaApplication.setScreenLockerState(false);
                            }
                            preferencesUtil.setPreferenceIntValue("retry",0);
                        }
                    }).setCancelable(false);
        }

        if (!errorDialog.isShowing()) {
            errorDialog.show();
        }
    }


    public int getIntentExtra() {
        Intent intent = getIntent();
        int intentExtra = intent.getIntExtra("modified_gesture", 0);
        return intentExtra;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        haveLockActivity = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        haveLockActivity = false;
        if (keyCode == KeyEvent.KEYCODE_BACK && getIntentExtra() != MODIFIEF_GESTURE) {
            isBackpressed = keyCode == KeyEvent.KEYCODE_BACK;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//如果intent不指定category，那么无论intent filter的内容是什么都应该是匹配的。
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setSafeLockState() {
        //实例化安全锁信息用于保存
        SettingBean bean1 = new SettingBean();
        bean1.setKey(SettingBean.SAFE_LOCK);
        bean1.setValue(IS_FORGET_PW);
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
                        //查询安全锁通知信息
                        SettingBean safeLock = SettingServer.querySetting(SettingBean.SAFE_LOCK);
                        //修改供其他模块调用的状态值
                        if (safeLock != null) {
                            SettingServer.safeLock = safeLock.getValue();
                        }
                        //查询安全锁锁屏锁定信息
                        SettingBean lockScreen = SettingServer.querySetting(SettingBean.LOCK_SCREEN);
                        //修改供其他模块调用的状态值
                        if (lockScreen != null) {
                            SettingServer.lockScreen = lockScreen.getValue();
                        }
                        //查询安全锁后台运行锁定信息
                        SettingBean backGroundLock = SettingServer.querySetting(SettingBean.LOCK_BACKGROUND);
                        //修改供其他模块调用的状态值
                        if (backGroundLock != null) {
                            SettingServer.backgroundLock = backGroundLock.getValue();
                        }
                    }
                });
    }

    public static boolean isHaveLockActivity() {
        return haveLockActivity;
    }

    //[S]fix bug 8719 by licong 在不是分享的情况下，解锁后 将安通+提到前台
    public void finish() {
        int goalTaskId = 0;
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = am.getRunningTasks(10);
        for(int i = 0; i <runningTaskInfos.size(); i++){
            ComponentName topComponentName =runningTaskInfos.get(i).topActivity;
            ComponentName baseComponentName = runningTaskInfos.get(i).baseActivity;
            String topPkgName = topComponentName.getPackageName();
            //当不是分享的情况下，解锁后将安通+应用提到前台
            if (topPkgName.equals(getPackageName())
                    && baseComponentName.getClassName().equals("com.xdja.presenter_mainframe.presenter.activity.MainFramePresenter")
                    && (!isTransmit || (isTransmit && SafeLockUtil.isForwardMessage()))) {
                goalTaskId = runningTaskInfos.get(i).id;
                break;
            }
        }
        am.moveTaskToFront(goalTaskId,0);
        super.finish();
    }
    //[E] fix bug 8719 by licong



    //[S] fix bug by licong for 8271
    // 全屏界面跳转到非全屏界面的屏幕闪烁问题
    /**
     * 设置全屏显示
     */
    private void setFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 退出全屏显示
     */
    private void cancelFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    }
    //[E] fix bug by licong for 8271
}
