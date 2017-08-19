package com.xdja.domain_mainframe.usecase.userInfo;

import com.xdja.domain_mainframe.repository.UserInfoRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext0UseCase;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by ldy on 16/4/21.
 * 客户端退出用例,只是发起网络请求，不清除本地数据
 */
public class LogoutUseCase extends Ext0UseCase<Void> {
    private final UserInfoRepository userInfoRepository;

    @Inject
    public LogoutUseCase(ThreadExecutor threadExecutor,
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
        return userInfoRepository.logout();
    }
}
