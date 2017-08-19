package com.xdja.domain_mainframe.usecase.deviceauth;

import android.text.TextUtils;

import com.xdja.dependence.exeptions.CheckException;
import com.xdja.domain_mainframe.repository.DeviceAuthRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext1UseCase;

import java.util.Map;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:获取设备授信所需要的信息</p>
 * <p>Description:fill方法参数为授权ID</p>
 * <p>Package:com.xdja.domain_mainframe.usecase.deviceauth</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/22</p>
 * <p>Time:14:53</p>
 */
public class ObtainAuthInfoUseCase extends Ext1UseCase<String, Map<String,String>> {

    private DeviceAuthRepository.PostDeviceAuthRepository postDeviceAuthRepository;

    @Inject
    public ObtainAuthInfoUseCase(ThreadExecutor threadExecutor,
                                 PostExecutionThread postExecutionThread,
                                 DeviceAuthRepository.PostDeviceAuthRepository deviceAuthRepository) {
        super(threadExecutor, postExecutionThread);
        this.postDeviceAuthRepository = deviceAuthRepository;
    }

    @Override
    public Observable<Map<String,String>> buildUseCaseObservable() {

        if (TextUtils.isEmpty(this.p)) {
            return Observable.error(
                    new CheckException("auth ID cannot null")
            );
        }

        return this.postDeviceAuthRepository.obtainAuthInfo(this.p);
    }
}
