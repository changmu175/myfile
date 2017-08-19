package com.xdja.domain_mainframe.usecase;

import com.xdja.domain_mainframe.model.Account;
import com.xdja.domain_mainframe.repository.UserInfoRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext0UseCase;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by ldy on 16/5/23.
 * 查询在本地存储的上次登录账号
 */
public class QueryAccountAtLocalUseCase extends Ext0UseCase<Account> {
    private UserInfoRepository.PreUserInfoRepository userInfoRepository;

    @Inject
    public QueryAccountAtLocalUseCase(ThreadExecutor threadExecutor,
                         PostExecutionThread postExecutionThread,
                         UserInfoRepository.PreUserInfoRepository userInfoRepository) {
        super(threadExecutor, postExecutionThread);
        this.userInfoRepository = userInfoRepository;
    }

    /**
     * 构建业务处理事件流
     *
     * @return 目标事件流
     */
    @Override
    public Observable<Account> buildUseCaseObservable() {
        return userInfoRepository.queryAccountAtLocal();
    }
}
