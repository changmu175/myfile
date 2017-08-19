package com.xdja.domain_mainframe.usecase.deviceauth;

import android.text.TextUtils;

import com.xdja.dependence.exeptions.CheckException;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.domain_mainframe.repository.DeviceAuthRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext3UseCase;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:通过验证好友手机号授权设备</p>
 * <p>Description:fill方法参数依次为：帐号、内部验证码、好友手机号</p>
 * <p>Package:com.xdja.domain_mainframe.usecase.deviceauth</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/22</p>
 * <p>Time:14:53</p>
 */
public class CheckFriendMobilesUseCase extends Ext3UseCase<String, String, List<String>, MultiResult<Object>> {

    private DeviceAuthRepository deviceAuthRepository;

    @Inject
    public CheckFriendMobilesUseCase(ThreadExecutor threadExecutor,
                                     PostExecutionThread postExecutionThread,
                                     DeviceAuthRepository deviceAuthRepository) {
        super(threadExecutor, postExecutionThread);
        this.deviceAuthRepository = deviceAuthRepository;
    }

    @Override
    public Observable<MultiResult<Object>> buildUseCaseObservable() {

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

        if (this.p2 == null || this.p2.isEmpty()) {
            return Observable.error(
                    new CheckException("friend mobile number cannot null")
            );
        }

        return this.deviceAuthRepository.checkFriendMobiles(this.p, this.p1, this.p2);
    }
}
