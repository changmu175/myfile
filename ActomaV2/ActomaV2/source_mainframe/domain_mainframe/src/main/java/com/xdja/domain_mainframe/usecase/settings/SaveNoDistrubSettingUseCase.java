package com.xdja.domain_mainframe.usecase.settings;

import android.content.Context;

import com.google.gson.Gson;
import com.xdja.comm.data.SettingBean;
import com.xdja.comm.server.SettingServer;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext2UseCase;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by geyao on 2015/7/15.
 * 保存新消息提醒信息
 */
public class SaveNoDistrubSettingUseCase extends Ext2UseCase<Context, GetNoDistrubSettingUseCase.NoDistrubBean, Boolean> {
    /**
     * 上下文句柄
     */
    private Context context;
    /**
     * 需要保存的新消息提醒页内的对象
     */
    private GetNoDistrubSettingUseCase.NoDistrubBean settingBeans;

    @Inject
    public SaveNoDistrubSettingUseCase(ThreadExecutor threadExecutor,
                                        PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
    }
    @Override
    public Observable buildUseCaseObservable() {
        context = p;
        settingBeans = p1;
       return Observable.just(context).flatMap(new Func1<Context, Observable<Boolean>>() {
           @Override
           public Observable<Boolean> call(Context context) {
               String tartget = new Gson().toJson(settingBeans);
               SettingBean settingBean = new SettingBean();
               settingBean.setKey(SettingBean.NODISTRUB);
               settingBean.setValue(tartget);
               boolean result = SettingServer.insertSetting(settingBean);
               return Observable.just(result);
           }
       });
    }
}
