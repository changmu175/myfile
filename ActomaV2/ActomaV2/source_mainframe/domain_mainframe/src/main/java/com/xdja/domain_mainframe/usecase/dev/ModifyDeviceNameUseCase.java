package com.xdja.domain_mainframe.usecase.dev;

import android.text.TextUtils;

import com.xdja.dependence.exeptions.CheckException;
import com.xdja.domain_mainframe.repository.UserInfoRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext2UseCase;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by ldy on 16/4/21.
 * 修改授信设备名称用例
 */
public class ModifyDeviceNameUseCase extends Ext2UseCase<String,String,Void> {
    private final UserInfoRepository userInfoRepository;

    @Inject
    public ModifyDeviceNameUseCase(ThreadExecutor threadExecutor,
                               PostExecutionThread postExecutionThread,
                               UserInfoRepository userInfoRepository) {
        super(threadExecutor, postExecutionThread);
        this.userInfoRepository = userInfoRepository;
    }
    /**
     * 构建业务处理事件流
     *
     * @return 目标事件流
     */
    @Override
    public Observable<Void> buildUseCaseObservable() {
        if (TextUtils.isEmpty(this.p)) {
            return Observable.error(
                    new CheckException("cardNo cannot null")
            );
        }
        if (TextUtils.isEmpty(this.p1)) {
            return Observable.error(
                    new CheckException("deviceName cannot null")
            );
        }
        return userInfoRepository.modifyDeviceName(p,p1);
    }
}
