package com.xdja.domain_mainframe.usecase.safeLock;

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
 * Created by licong on 2016/11/26.
 * 向服务器上保存安全锁设置数据，并返回保存结果
 */
public class SaveSafeLockSettingUseCase extends Ext2UseCase<Context, SettingBean[],Boolean[]> {
    /**
     * 上下文句柄
     */
    private Context context;

    /**
     * 需要保存的安全锁页内的对象
     */
    private SettingBean[] settingBeans;

    @Inject
    public SaveSafeLockSettingUseCase(ThreadExecutor threadExecutor,
                                        PostExecutionThread postExecutionThread
                                      ) {

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
                SettingServer.insertSafeLock(settingBeans);
                subscriber.onNext(list);
            }
        });
    }
}
