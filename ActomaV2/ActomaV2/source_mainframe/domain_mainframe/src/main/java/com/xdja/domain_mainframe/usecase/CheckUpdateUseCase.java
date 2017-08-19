package com.xdja.domain_mainframe.usecase;

import android.content.Context;
import android.text.TextUtils;

import com.xdja.comm.server.ActomaController;
import com.xdja.comm.server.PreferencesServer;
import com.xdja.comm.uitl.DeviceUtil;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext1UseCase;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by chenbing on 2015/7/31.
 */
public class CheckUpdateUseCase extends Ext1UseCase<Context, Boolean> {
    private Context context;

    @Inject
    public CheckUpdateUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
    }


    @Override
    public Observable<Boolean> buildUseCaseObservable() {
        context = this.p;
        return Observable.just(context).flatMap(new Func1<Context, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(Context context) {
                String currentVersion = DeviceUtil.getClientVersion(context);
                // [Start] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-11. for check update.
                String newVersion = PreferencesServer.getWrapper(ActomaController.getApp())
                        .gPrefStringValue("new_version");
                // [End] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-11. for check update.
                boolean result;
                //如果没有取到升级信息,就不显示new
                if (TextUtils.isEmpty(newVersion)){
                    result = false;
                }else {
                    if (currentVersion.equals(newVersion)){//如果版本号相同，就没有更新提示
                        result = false;
                    }else {
                        result = true;
                    }
                }
                return Observable.just(result);
            }
        });
    }
}
