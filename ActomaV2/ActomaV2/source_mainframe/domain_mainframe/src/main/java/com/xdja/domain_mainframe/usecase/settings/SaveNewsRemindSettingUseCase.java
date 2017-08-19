package com.xdja.domain_mainframe.usecase.settings;

import android.content.Context;

import com.xdja.comm.data.SettingBean;
import com.xdja.comm.server.SettingServer;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext2UseCase;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by geyao on 2015/7/15.
 * 保存新消息提醒信息
 */
public class SaveNewsRemindSettingUseCase extends Ext2UseCase<Context, SettingBean[], Boolean[]> {
    /**
     * 上下文句柄
     */
    private Context context;
    /**
     * 需要保存的新消息提醒页内的对象
     */
    private SettingBean[] settingBeans;

    @Inject
    public SaveNewsRemindSettingUseCase(ThreadExecutor threadExecutor,
                                        PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
    }

    @Override
    public Observable buildUseCaseObservable() {
        context = p;
        settingBeans = p1;
        return Observable.create(new Observable.OnSubscribe<Boolean[]>() {
            @Override
            public void call(Subscriber<? super Boolean[]> subscriber) {
                //循环逐条插入数据信息到设置表中
                Boolean[] list = new Boolean[settingBeans.length];
                for (int i = 0; i < settingBeans.length; i++) {
                    boolean result1 = SettingServer.insertSetting(settingBeans[i]);
                    list[i] = result1;
                }
                subscriber.onNext(list);
            }
        });
    }
}
