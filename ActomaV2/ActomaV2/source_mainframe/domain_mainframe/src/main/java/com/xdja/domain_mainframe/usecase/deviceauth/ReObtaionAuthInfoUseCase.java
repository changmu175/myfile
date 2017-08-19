package com.xdja.domain_mainframe.usecase.deviceauth;

import android.text.TextUtils;

import com.xdja.dependence.exeptions.CheckException;
import com.xdja.domain_mainframe.repository.DeviceAuthRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext3UseCase;

import java.util.Map;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:重新获取认证所需的信息</p>
 * <p>Description:fill方法的参数依次为：帐号、内部验证码和授权码</p>
 * <p>Package:com.xdja.domain_mainframe.usecase.deviceauth</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/23</p>
 * <p>Time:9:01</p>
 */
public class ReObtaionAuthInfoUseCase extends Ext3UseCase<String,String,String,Map<String,String>> {
    private DeviceAuthRepository deviceAuthRepository;

    @Inject
    public ReObtaionAuthInfoUseCase(ThreadExecutor threadExecutor,
                                    PostExecutionThread postExecutionThread,
                                    DeviceAuthRepository deviceAuthRepository) {
        super(threadExecutor, postExecutionThread);
        this.deviceAuthRepository = deviceAuthRepository;
    }

    @Override
    public Observable<Map<String, String>> buildUseCaseObservable() {
        if (TextUtils.isEmpty(this.p)) {
            return Observable.error(
                    new CheckException("account cannot null")
            );
        }
        if (TextUtils.isEmpty(this.p1)) {
            return Observable.error(
                    new CheckException("inner verify cannot null")
            );
        }

        if (TextUtils.isEmpty(this.p2)) {
            return Observable.error(
                    new CheckException("auth code cannot null")
            );
        }
        return this.deviceAuthRepository.reObtaionAuthInfo(this.p, this.p1, this.p2);
    }
}
