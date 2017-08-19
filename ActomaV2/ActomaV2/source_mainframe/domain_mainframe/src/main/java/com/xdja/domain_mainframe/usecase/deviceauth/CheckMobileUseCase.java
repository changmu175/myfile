package com.xdja.domain_mainframe.usecase.deviceauth;

import android.text.TextUtils;

import com.xdja.dependence.exeptions.CheckException;
import com.xdja.domain_mainframe.repository.DeviceAuthRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext4UseCase;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:通过验证手机验证码授权设备</p>
 * <p>Description:fill方法参数依次为：帐号、手机号、短信验证码、内部验证码</p>
 * <p>Package:com.xdja.domain_mainframe.usecase.deviceauth</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/22</p>
 * <p>Time:14:53</p>
 */
public class CheckMobileUseCase extends Ext4UseCase<String, String, String, String, Void> {

    private DeviceAuthRepository deviceAuthRepository;

    @Inject
    public CheckMobileUseCase(ThreadExecutor threadExecutor,
                              PostExecutionThread postExecutionThread,
                              DeviceAuthRepository deviceAuthRepository) {
        super(threadExecutor, postExecutionThread);
        this.deviceAuthRepository = deviceAuthRepository;
    }

    @Override
    public Observable<Void> buildUseCaseObservable() {

        if (TextUtils.isEmpty(this.p)) {
            return Observable.error(
                    new CheckException("account cannot null")
            );
        }
        if (TextUtils.isEmpty(this.p1)) {
            return Observable.error(
                    new CheckException("mobile number cannot null")
            );
        }

        if (this.p2 == null || this.p2.isEmpty()) {
            return Observable.error(
                    new CheckException("verify cannot null")
            );
        }

        if (this.p3 == null || this.p3.isEmpty()) {
            return Observable.error(
                    new CheckException("inner verify  cannot null")
            );
        }

        return this.deviceAuthRepository.checkMobile(this.p, this.p1, this.p2, this.p3);
    }
}
