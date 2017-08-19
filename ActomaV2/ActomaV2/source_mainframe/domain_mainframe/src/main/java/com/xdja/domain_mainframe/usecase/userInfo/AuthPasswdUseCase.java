package com.xdja.domain_mainframe.usecase.userInfo;

import android.text.TextUtils;

import com.xdja.dependence.exeptions.CheckException;
import com.xdja.domain_mainframe.repository.UserInfoRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext1UseCase;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by ldy on 16/4/21.
 * 检测账号密码用例
 */
public class AuthPasswdUseCase extends Ext1UseCase<String,Void> {
    private final UserInfoRepository userInfoRepository;

    @Inject
    public AuthPasswdUseCase(ThreadExecutor threadExecutor,
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
                    new CheckException("passwd cannot null")
            );
        }
        return userInfoRepository.authPasswd(p);
    }
}
