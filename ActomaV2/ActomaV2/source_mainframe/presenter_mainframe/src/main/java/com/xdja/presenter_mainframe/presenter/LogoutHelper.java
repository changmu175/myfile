package com.xdja.presenter_mainframe.presenter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;

import com.xdja.comm.event.BusProvider;
import com.xdja.comm.event.ExitAppEvent;
import com.xdja.contact.util.BroadcastManager;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.domain_mainframe.di.PostUseCaseModule;
import com.xdja.domain_mainframe.di.PreUseCaseModule;
import com.xdja.frame.presenter.ActivityStack;
import com.xdja.presenter_mainframe.ActomaApplication;
import com.xdja.presenter_mainframe.di.components.post.PostUseCaseComponent;
import com.xdja.presenter_mainframe.di.components.post.UserComponent;
import com.xdja.presenter_mainframe.navigation.Navigator;

import javax.inject.Inject;

import rx.Subscriber;

/**
 * Created by ldy on 16/5/23.
 */
public class LogoutHelper {

    @Inject
    public LogoutHelper() {
    }

    public void diskLogout() {
        Activity topActivity = ActivityStack.getInstanse().getTopActivity();
        if (topActivity == null) {
            LogUtil.getUtils().w("topActivity为空,退出失败");
            return;
        }
        final ActomaApplication application = (ActomaApplication) topActivity.getApplication();
        if (application == null) {
            LogUtil.getUtils().w("application为空,退出失败");
            return;
        }
        UserComponent userComponent = application.getUserComponent();
        if (userComponent == null) {
            LogUtil.getUtils().w("userComponent为空,退出失败");
            return;
        }
        //[S]modify by lixiaolong on 20160909. fix bug 3855. review by wangchao1.
        BroadcastManager.sendBroadcastCloseTransfer();//关闭加密通道
        //[E]modify by lixiaolong on 20160909. fix bug 3855. review by wangchao1.
        userComponent.diskLogout().fill()
                .execute(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.getUtils().e(e);
                    }

                    @Override
                    public void onNext(Void aVoid) {
                        LogUtil.getUtils().d("diskLogout userComponent.onNext");
                        application.releaseUserComponent();
                    }
                });
    }

    //alh@xdja.com<mailto://alh@xdja.com> 2017-03-22 add. fix bug 9664 . review by wangchao1. Start
    public void forceLogout() {
        final ActomaApplication app = (ActomaApplication) ActomaApplication.getInstance();
        LogUtil.getUtils().i("forceLogout app : " + (app == null));
        if (app == null) return;
        final UserComponent userComponent = app.getUserComponent();
        LogUtil.getUtils().i("forceLogout user : " + (userComponent == null));
        if (userComponent == null) return;
        PostUseCaseComponent postUseCaseComponent = userComponent.plus(new PostUseCaseModule(), new PreUseCaseModule());
        LogUtil.getUtils().i("forceLogout start logout");
        postUseCaseComponent.logout().fill().execute(new Subscriber<Void>() {
            @Override
            public void onStart() {
                super.onStart();
                BusProvider.getMainProvider().post(new ExitAppEvent());
                //[S]modify by lixiaolong on 20160909. fix bug 3855. review by wangchao1.
                BroadcastManager.sendBroadcastCloseTransfer();//关闭加密通道
                //[E]modify by lixiaolong on 20160909. fix bug 3855. review by wangchao1.
                userComponent.diskLogout().fill().execute(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {

                    }
                    @Override
                    public void onError(Throwable e) {
                        LogUtil.getUtils().e(e);
                    }

                    @Override
                    public void onNext(Void aVoid) {
                        LogUtil.getUtils().d("diskLogout userComponent.onNext");
                        app.releaseUserComponent();
                    }
                });
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.getUtils().e(e);
            }

            @Override
            public void onNext(Void aVoid) {
            }
        });
    }
    //alh@xdja.com<mailto://alh@xdja.com> 2017-03-22 add. fix bug 9664 . review by wangchao1. End

    /**
     * 发起退出请求后直接清空数据库
     * @param logoutFinish  网络下线操作结束通知
     */
    public boolean logout(@Nullable final LogoutFinish logoutFinish) {
        Activity topActivity = ActivityStack.getInstanse().getTopActivity();
        if (topActivity == null) {
            LogUtil.getUtils().w("topActivity为空,退出失败");
            return false;
        }
        final ActomaApplication application = (ActomaApplication) topActivity.getApplication();
        if (application == null) {
            LogUtil.getUtils().w("application为空,退出失败");
            return false;
        }
		UserComponent userComponent = application.getUserComponent();

        if (userComponent == null) {
            LogUtil.getUtils().w("userComponent为空,退出失败");
            return false;
        }

        PostUseCaseComponent postUseCaseComponent = userComponent.plus(
                new PostUseCaseModule(), new PreUseCaseModule());
        postUseCaseComponent.logout().fill()
                .execute(new Subscriber<Void>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        //alh@xdja.com<mailto://alh@xdja.com> 2016-08-08 add (weixiaofan need this event). review by wangchao1. Start
                        BusProvider.getMainProvider().post(new ExitAppEvent());
                        //alh@xdja.com<mailto://alh@xdja.com> 2016-08-08 add (weixiaofan need this event). review by wangchao1. End
                        //2016-6-2 ldy 请求开始时就直接清除本地数据
                        diskLogout();
                    }

                    @Override
                    public void onCompleted() {
                        if (logoutFinish != null) {
                            logoutFinish.finish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.getUtils().e(e);
                        if (logoutFinish != null) {
                            logoutFinish.finish();
                        }
                    }

                    @Override
                    public void onNext(Void aVoid) {
                    }
                });
        return true;
    }

    //[s]modify by xnn for bug 9664 review by tangsha
    public void navigateToLoginWithExit(Context context) {
        Navigator.navigateToLoginWithExit(context);
    }

    public void navigateToLoginWithExit() {
        Navigator.navigateToLoginWithExit();
    }
    //[e]modify by xnn for bug 9664 review by tangsha

    public interface LogoutFinish {
        void finish();
    }
}
