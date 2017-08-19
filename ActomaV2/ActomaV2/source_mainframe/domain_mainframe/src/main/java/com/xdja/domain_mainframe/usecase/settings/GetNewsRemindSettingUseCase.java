package com.xdja.domain_mainframe.usecase.settings;

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
 * Created by geyao on 2015/7/15.
 * 获取新消息提醒信息
 */
public class GetNewsRemindSettingUseCase extends Ext1UseCase<Context, SettingBean[]> {
    /**
     * 上下文句柄
     */
    private Context context;

    @Inject
    public GetNewsRemindSettingUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
    }

    @Override
    public Observable buildUseCaseObservable() {
        context = p;
        return Observable.create(new Observable.OnSubscribe<SettingBean[]>() {
            @Override
            public void call(Subscriber<? super SettingBean[]> subscriber) {
                //查找新消息提醒通知信息
                SettingBean bean1 = SettingServer.querySetting(SettingBean.NEWSREMIND);
                //若数据库内无对应信息则实例化一个默认的新消息提醒通知设置对象
                if (bean1 == null) {
                    bean1 = new SettingBean();
                    bean1.setKey(SettingBean.NEWSREMIND);
                    bean1.setValue("true");
                }
                //查找新消息提醒通知-声音信息
                SettingBean bean2 = SettingServer.querySetting(SettingBean.NEWSREMIND_RING);
                //若数据库内无对应信息则实例化一个默认的新消息提醒通知-声音设置对象
                if (bean2 == null) {
                    bean2 = new SettingBean();
                    bean2.setKey(SettingBean.NEWSREMIND_RING);
                    bean2.setValue("true");
                }
                //查找新消息提醒通知-振动信息
                SettingBean bean3 = SettingServer.querySetting(SettingBean.NEWSREMIND_SHAKE);
                //若数据库内无对应信息则实例化一个默认的新消息提醒通知-振动设置对象
                if (bean3 == null) {
                    bean3 = new SettingBean();
                    bean3.setKey(SettingBean.NEWSREMIND_SHAKE);
                    bean3.setValue("true");
                }
                subscriber.onNext(new SettingBean[]{bean1, bean2, bean3});
            }
        });
    }
}
