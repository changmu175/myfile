package com.xdja.presenter_mainframe.presenter.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.xdja.comm.data.SettingBean;
import com.xdja.comm.server.SettingServer;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.frame.domain.usecase.Ext1Interactor;
import com.xdja.frame.domain.usecase.Ext2Interactor;
import com.xdja.presenter_mainframe.cmd.OpenSafeLockCommand;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.uiInterface.OpenSafeLockVu;
import com.xdja.presenter_mainframe.ui.uiInterface.ViewOpenSafeLock;

import javax.inject.Inject;

import dagger.Lazy;
import rx.functions.Action1;

/**
 * Created by licong on 2016/11/24.
 * 安全锁设置开启界面
 */
@InjectOption.Options(InjectOption.OPTION_POSTCACHEDUSER)
public class OpenSafeLockPresenter  extends PresenterActivity<OpenSafeLockCommand, OpenSafeLockVu> implements OpenSafeLockCommand{

    /**
     * 安全锁
     */
    private static boolean safeLockisOn = true;
    /**
     * 锁屏锁定
     */
    private static boolean lockScreenisOn = true;
    /**
     * 后台运行锁定
     */
    private static boolean backgroundLockisOn = true;

    private static final int MODIFIEF_GESTURE = 1;



    @Inject
    @InteractorSpe(DomainConfig.GET_SAFELOCK_SETTINGS)
    Lazy<Ext1Interactor<Context,SettingBean[]>> getSafeLockSettingUseCase;

    @Inject
    @InteractorSpe(DomainConfig.SET_SAFELOCK_SETTINGS)
    Lazy<Ext2Interactor<Context, SettingBean[], Boolean[]>> saveSafeLockSettingUseCase;

    @NonNull
    @Override
    protected Class<? extends OpenSafeLockVu> getVuClass() {
        return ViewOpenSafeLock.class;
    }

    @NonNull
    @Override
    protected OpenSafeLockCommand getCommand() {
        return this;
    }


    /**
     * 安全锁通知是否打开
     * @param isOn 是否开启通知
     */
    @Override
    public void safeLock(boolean isOn) {
        safeLockisOn = isOn;
        SettingServer.safeLock = String.valueOf(isOn);
    }

    /**
     * 锁屏锁定
     * @param isOn
     */
    @Override
    public void lockSreen(boolean isOn) {
        lockScreenisOn = isOn;
        SettingServer.lockScreen = String.valueOf(isOn);
    }

    @Override
    public void backgroundLock(boolean isOn) {
        backgroundLockisOn = isOn;
        SettingServer.backgroundLock = String.valueOf(isOn);
    }

    @Override
    public void modifiedGesture() {
        Intent intent = new Intent(this,OpenGesturePresenter.class);
        intent.putExtra("modified_gesture",MODIFIEF_GESTURE);
        startActivity(intent);
    }

    /**
     * 初始化完成之后
     */
    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        if (getActivityPostUseCaseComponent() != null) {
            getActivityPostUseCaseComponent().inject(this);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取安全锁通知全部状态
        getSafeLockSettingUseCase.get().fill(this).execute(new Action1<SettingBean[]>() {
            @Override
            public void call(SettingBean[] settingBeans) {
                for (int i = 0; i < settingBeans.length; i++) {
                    //若key对应安全锁通知
                    if (settingBeans[i].getKey().equals(SettingBean.SAFE_LOCK)) {
                        getVu().setSafeLock(Boolean.valueOf(settingBeans[i].getValue()));
                        safeLockisOn = Boolean.valueOf(settingBeans[i].getValue());
                        SettingServer.safeLock = settingBeans[i].getValue();
                    }
                    //若key对应锁屏锁定
                    if (settingBeans[i].getKey().equals(SettingBean.LOCK_SCREEN)) {
                        getVu().setLockSreen(Boolean.valueOf(settingBeans[i].getValue()));
                        lockScreenisOn = Boolean.valueOf(settingBeans[i].getValue());
                        SettingServer.lockScreen = settingBeans[i].getValue();
                    }
                    //若key对应后台运行锁定
                    if (settingBeans[i].getKey().equals(SettingBean.LOCK_BACKGROUND)) {
                        getVu().setBackgroundLock(Boolean.valueOf(settingBeans[i].getValue()));
                        backgroundLockisOn = Boolean.valueOf(settingBeans[i].getValue());
                        SettingServer.backgroundLock = settingBeans[i].getValue();
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
            //实例化安全锁信息用于保存
            SettingBean bean1 = new SettingBean();
            bean1.setKey(SettingBean.SAFE_LOCK);
            bean1.setValue(String.valueOf(safeLockisOn));
            //实例化锁屏锁定信息用于保存
            SettingBean bean2 = new SettingBean();
            bean2.setKey(SettingBean.LOCK_SCREEN);
            bean2.setValue(String.valueOf(lockScreenisOn));
            //实例化后台运行锁定信息用于保存
            SettingBean bean3 = new SettingBean();
            bean3.setKey(SettingBean.LOCK_BACKGROUND);
            bean3.setValue(String.valueOf(backgroundLockisOn));

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

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            Intent intent = new Intent(this,AccountSafePresenter.class);
//            startActivity(intent);
//            finish();
//            overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
//            return true;
//        }
//        return false;
//    }

}
