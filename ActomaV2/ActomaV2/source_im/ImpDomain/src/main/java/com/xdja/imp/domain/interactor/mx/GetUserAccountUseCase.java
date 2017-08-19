package com.xdja.imp.domain.interactor.mx;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.interactor.def.QueryUserAccount;
import com.xdja.imp.domain.repository.UserOperateRepository;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by Administrator on 2016/3/12.
 * 功能描述
 */
public class GetUserAccountUseCase extends MxUseCase<String> implements QueryUserAccount {
    @Inject
    public GetUserAccountUseCase(ThreadExecutor threadExecutor,
                                 PostExecutionThread postExecutionThread,
                                 UserOperateRepository userOperateRepository) {
        super(threadExecutor, postExecutionThread, userOperateRepository);
    }

    @Override
    public Observable<String> buildUseCaseObservable() {
        return userOperateRepository.queryUserAccount();
    }


    @Override
    public QueryUserAccount getUseAccount() {
        return this;
    }

    @Override
    public Interactor<String> get() {
        return null;
    }
}
