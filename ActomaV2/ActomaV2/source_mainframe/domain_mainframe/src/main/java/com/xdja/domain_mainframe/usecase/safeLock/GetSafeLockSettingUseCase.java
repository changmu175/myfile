package com.xdja.domain_mainframe.usecase.safeLock;

import android.content.Context;

import com.xdja.comm.data.SettingBean;
import com.xdja.comm.server.SettingServer;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext1UseCase;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by licong on 2016/11/26.
 * 从数据库查询手势密码的状态
 */
public class GetSafeLockSettingUseCase extends Ext1UseCase<Context, SettingBean[]> {
    /**
     * 上下文句柄
     */
    private Context context;

    @Inject
    public GetSafeLockSettingUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
    }

    @Override
    public Observable buildUseCaseObservable() {
        context = p;
        return Observable.create(new Observable.OnSubscribe<SettingBean[]>() {
            @Override
            public void call(Subscriber<? super SettingBean[]> subscriber) {
                //查找安全锁通知信息
                SettingBean bean1 = SettingServer.querySafeLock(SettingBean.SAFE_LOCK);
                //若数据库内无对应信息则实例化一个默认的安全锁通知设置对象
                if (bean1 == null) {
                    bean1 = new SettingBean();
                    bean1.setKey(SettingBean.SAFE_LOCK);
                    bean1.setValue("true");
                }
                //查找新消息提醒通知-声音信息
                SettingBean bean2 = SettingServer.querySafeLock(SettingBean.LOCK_SCREEN);
                //若数据库内无对应信息则实例化一个默认的锁屏锁定设置对象
                if (bean2 == null) {
                    bean2 = new SettingBean();
                    bean2.setKey(SettingBean.LOCK_SCREEN);
                    bean2.setValue("true");
                }
                //查找新消息提醒通知-振动信息
                SettingBean bean3 = SettingServer.querySafeLock(SettingBean.LOCK_BACKGROUND);
                //若数据库内无对应信息则实例化一个默认的后台运行锁定设置对象
                if (bean3 == null) {
                    bean3 = new SettingBean();
                    bean3.setKey(SettingBean.LOCK_BACKGROUND);
                    bean3.setValue("false");
                }
                subscriber.onNext(new SettingBean[]{bean1, bean2, bean3});
            }
        });
    }
}
