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
 * 获取听筒模式开关
 */
public class GetReceiverModeSettingUseCase extends Ext1UseCase<Context, SettingBean[]> {
    /**
     * 上下文句柄
     */
    private Context context;

    @Inject
    public GetReceiverModeSettingUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
    }

    @Override
    public Observable buildUseCaseObservable() {
        context = p;
        return Observable.create(new Observable.OnSubscribe<SettingBean[]>() {
            @Override
            public void call(Subscriber<? super SettingBean[]> subscriber) {
                //查找听筒模式
                SettingBean bean = SettingServer.querySetting(SettingBean.RECEIVER_MODE);
                //若数据库内无对应信息则实例化一个默认的听筒模式设置对象
                if (bean == null) {
                    bean = new SettingBean();
                    bean.setKey(SettingBean.RECEIVER_MODE);
                    bean.setValue("false");
                }
                subscriber.onNext(new SettingBean[]{bean});
            }
        });
    }
}
