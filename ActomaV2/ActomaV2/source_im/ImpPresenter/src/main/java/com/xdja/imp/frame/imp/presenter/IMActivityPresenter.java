package com.xdja.imp.frame.imp.presenter;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import com.xdja.comm.uitl.UniversalUtil;
import com.xdja.frame.presenter.mvp.Command;
import com.xdja.frame.presenter.mvp.presenter.BasePresenterActivity;
import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.imp.IMAccountLifeCycle;
import com.xdja.imp.data.cache.UserCache;
import com.xdja.imp.data.error.OkException;
import com.xdja.imp.data.error.OkHandler;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.di.component.DaggerUseCaseComponent;
import com.xdja.imp.di.component.UseCaseComponent;
import com.xdja.imp.di.component.UserComponent;
import com.xdja.imp.domain.interactor.def.RemindNewMessage;
import com.xdja.imp.handler.OkHandlerImp;
import com.xdja.imp.util.MsgDisplay;
import com.xdja.imp.util.NotificationUtil;

/**
 * <p>Summary:IMPresenter</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.frame.imp</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/3</p>
 * <p>Time:19:08</p>
 */
public abstract class IMActivityPresenter<P extends Command, V extends ActivityVu>
        extends BasePresenterActivity<P, V> {

    protected UserComponent userComponent;

    protected UseCaseComponent useCaseComponent;

    protected RemindNewMessage remindNewMessage;

    protected UserCache userCache;

    protected OkHandler<OkException> okHandler;

    @Override
    protected void preBindView(Bundle savedInstanceState) {
        super.preBindView(savedInstanceState);
        /*[s]modify by tangsha@20161206 for multi language*/
        UniversalUtil.changeLanguageConfig(this);
        /*[E]modify by tangsha@20161206 for multi language*/
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        if (null == IMAccountLifeCycle.imAccountLifeCycle || null == IMAccountLifeCycle.imAccountLifeCycle.getComponent()) {
            //容错处理，回退到登陆界面
            LogUtil.getUtils().d("未查询到用户信息注入提供对象");
            Intent intent = Intent.makeMainActivity(
                    new ComponentName(getPackageName(), "com.xdja.presenter_mainframe.presenter.activity.LauncherPresenter"));//modify by xnn for inter version @20170224
            intent.putExtra("exit", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            UserComponent userComponent =
                    IMAccountLifeCycle.imAccountLifeCycle.getComponent();
            this.useCaseComponent = DaggerUseCaseComponent.builder()
                    .userComponent(userComponent)
                    .build();

            this.userCache = this.useCaseComponent.userCache();
            MsgDisplay msgDisplay = this.useCaseComponent.msgDisplay();
            this.okHandler = new OkHandlerImp<>(msgDisplay);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        NotificationUtil.getInstance(getApplicationContext()).clearPNNotification();
        if (userCache != null)
            userCache.setUserForeground(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        NotificationUtil.getInstance(getApplicationContext()).clearPNNotification();
        if (userCache != null)
            userCache.setUserForeground(false);
    }
}
