package com.xdja.presenter_mainframe.presenter.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.xdja.comm.data.SettingBean;
import com.xdja.comm.server.SettingServer;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.frame.data.persistent.PreferencesUtil;
import com.xdja.frame.domain.usecase.Ext2Interactor;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.SettingGestureCommand;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.uiInterface.SettingGestureVu;
import com.xdja.presenter_mainframe.ui.uiInterface.ViewSettingGesture;
import com.xdja.presenter_mainframe.util.LockPatternUtils;
import com.xdja.presenter_mainframe.widget.LockPatternView;
import com.xdja.presenter_mainframe.widget.LockPatternView.DisplayMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
import rx.functions.Action1;

/**
 * Created by licong on 2016/11/25.
 * 安全锁手势设置界面
 */
@InjectOption.Options(InjectOption.OPTION_POSTCACHEDUSER)
public class SettingGesturePresenter  extends PresenterActivity<SettingGestureCommand, SettingGestureVu> implements SettingGestureCommand,LockPatternView.OnPatternListener{
    private int gestureCount = 0;

    //是否是第一次或者清空数据进入安通+，安全锁的设置
    public final static String IS_SAFE_LOCK = "isSafeLock";

    private List<LockPatternView.Cell> pattern;

    @Inject
    @InteractorSpe(DomainConfig.SET_SAFELOCK_SETTINGS)
    Lazy<Ext2Interactor<Context, SettingBean[], Boolean[]>> saveSafeLockSettingUseCase;

    @Inject
    PreferencesUtil preferencesUtil;

    @Inject
    LockPatternUtils lockPatternUtils;

    private final int POST_DELAY = 2000;

    @NonNull
    @Override
    protected Class<? extends SettingGestureVu> getVuClass() {
        return ViewSettingGesture.class;
    }

    @NonNull
    @Override
    protected SettingGestureCommand getCommand() {
        return this;
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        if (getActivityPostUseCaseComponent() != null) {
            getActivityPostUseCaseComponent().inject(this);
        }
        getVu().getLockPatternView().setOnPatternListener(this);

    }

    @Override
    public void onPatternStart() {
        gestureCount ++;
        getVu().getLockPatternView().removeCallbacks(mClearPatternRunnable);
    }

    @Override
    public void onPatternCleared() {
        getVu().getLockPatternView().removeCallbacks(mClearPatternRunnable);
    }

    private void postClearRunnable(){
        getVu().getLockPatternView().removeCallbacks(mClearPatternRunnable);
        getVu().getLockPatternView().postDelayed(mClearPatternRunnable,POST_DELAY);
    }

    @Override
    public void onPatternDetected(List<LockPatternView.Cell> newPattern) {
        if (newPattern == null) {
            return;
        }
        if (newPattern.size() < LockPatternUtils.MIN_LOCK_PATTERN_SIZE) {
            getVu().getLockPatternView().setDisplayMode(DisplayMode.Wrong);
            postClearRunnable();
            //[S]add by licong for bug 9103
            gestureCount = (gestureCount == 1 )? 0 : gestureCount;
            //[E]add by licong for bug 9103
            Toast.makeText(this,getResources().getString(R.string.lockpattern_recording_incorrect_too_short),Toast.LENGTH_SHORT).show();

        } else {
            if (gestureCount == 1) {
                pattern = new ArrayList<>(newPattern);
                gestureCount ++;
                getVu().setText();
                getVu().getLockPatternView().clearPattern();
            } else if (gestureCount > 1) {
                if (newPattern.equals(pattern)) {
                    getVu().setText();
                    Toast.makeText(this,getResources().getString(R.string.safe_setting_success),Toast.LENGTH_SHORT).show();
                    lockPatternUtils.saveLockPattern(newPattern);

                    //add by licong for safeLock
                    Intent intent1 = getIntent();
                    int intExtra = intent1.getIntExtra("isSettingSafe",0);
                    if (intExtra == SettingSafeLockPresenter.IS_SETTING_SAFE) {
                        Intent intent = new Intent(this, OpenSafeLockPresenter.class);
                        startActivity(intent);
                    }
                    //add by licong for safeLock

                    gestureCount = 0;
                    saveSafeLock();
                    finish();
                } else {
                    getVu().getLockPatternView().setDisplayMode(DisplayMode.Wrong);
                    postClearRunnable();
                    Toast.makeText(this,getResources().getString(R.string.safe_second_unlock_wrong),Toast.LENGTH_SHORT).show();
                }
            }

        }

    }

    private Runnable mClearPatternRunnable = new Runnable() {
        public void run() {
            getVu().getLockPatternView().clearPattern();
        }
    };

    @Override
    public int getGestureCount() {
        return gestureCount;
    }

    @Override
    protected void onResume() {
        super.onResume();
        preferencesUtil.setPreferenceBooleanValue(IS_SAFE_LOCK,false);
    }

    private void saveSafeLock() {
        Intent intent = getIntent();
        int intExtra = intent.getIntExtra("isSettingSafe",0);
        if (intExtra == SettingSafeLockPresenter.IS_SETTING_SAFE) {
            //实例化安全锁信息用于保存
            SettingBean bean1 = new SettingBean();
            bean1.setKey(SettingBean.SAFE_LOCK);
            bean1.setValue(String.valueOf(true));
            //实例化锁屏锁定信息用于保存
            SettingBean bean2 = new SettingBean();
            bean2.setKey(SettingBean.LOCK_SCREEN);
            bean2.setValue(String.valueOf(true));
            //实例化后台运行锁定信息用于保存
            SettingBean bean3 = new SettingBean();
            bean3.setKey(SettingBean.LOCK_BACKGROUND);
            bean3.setValue(String.valueOf(false));

            //执行保存操作
            saveSafeLockSettingUseCase.get().fill(this, new SettingBean[]{bean1, bean2, bean3})
                    .execute(new Action1<Boolean[]>() {
                        @Override
                        public void call(Boolean[] booleen) {
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
    }

    //[S] add by licong for safeLock
    //安全锁向服务器保存手势密码
    /*private void exeSaveGesturePwdUseCase(final List<LockPatternView.Cell> pattern){
        final String patternPwd = LockPatternUtils.patternToString(pattern);
        Map<String,String> map = new HashMap<>();
       // map.put("gesturePwd", Base64.encodeToString(patternPwd.getBytes(),Base64.DEFAULT));
        map.put("gesturePwd",patternPwd);
        final Intent intent1 = new Intent(this,OpenSafeLockPresenter.class);
        saveGesturePwdUseCase.get().fill(this,map).execute(new PerSubscriber<String>(null) {
            @Override
            public void onNext(String s) {
                if (s.equals("error")) {
                    LogUtil.getUtils().d("SettingGesturePresenter : exeSaveGesturePwdUseCase 服务器保存密码失败");
                } else {
                    LockPatternUtils.saveLockPattern(pattern);
                    saveSafeLock();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                startActivity(intent1);
                finish();
                LogUtil.getUtils().d("onError" + e.getMessage());

            }
        });
    }*/
    //[E] add by licong for safeLock
}
