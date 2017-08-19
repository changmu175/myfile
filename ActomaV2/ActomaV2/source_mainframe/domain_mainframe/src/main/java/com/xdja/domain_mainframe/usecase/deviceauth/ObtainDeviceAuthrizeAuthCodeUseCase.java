package com.xdja.domain_mainframe.usecase.deviceauth;

import android.text.TextUtils;

import com.xdja.dependence.exeptions.CheckException;
import com.xdja.domain_mainframe.repository.DeviceAuthRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext2UseCase;

import java.util.Map;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:获取授信设备需要的验证码</p>
 * <p>Description:fill方法参数依次为：帐号、手机号</p>
 * <p>Package:com.xdja.domain_mainframe.usecase.deviceauth</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/22</p>
 * <p>Time:14:53</p>
 */
public class ObtainDeviceAuthrizeAuthCodeUseCase extends Ext2UseCase<String, String, Map<String, String>> {

    private DeviceAuthRepository deviceAuthRepository;

    @Inject
    public ObtainDeviceAuthrizeAuthCodeUseCase(ThreadExecutor threadExecutor,
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
                    new CheckException("mobile number cannot null")
            );
        }

        return this.deviceAuthRepository.obtainDeviceAuthrizeAuthCode(this.p, this.p1);
    }
}
