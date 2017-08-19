package com.xdja.imp.domain.interactor.mx;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.AddUserAccount;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.repository.UserOperateRepository;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by Administrator on 2016/3/12.
 * 功能描述
 */
public class AddUserAccountUseCase extends MxUseCase<Boolean> implements AddUserAccount {
    private String str;
    @Inject
    public AddUserAccountUseCase(ThreadExecutor threadExecutor,
                                      PostExecutionThread postExecutionThread,
                                      UserOperateRepository userOperateRepository) {
        super(threadExecutor, postExecutionThread, userOperateRepository);
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable() {
        return userOperateRepository.saveUserAccount(this.str);
    }

    @Override
    public AddUserAccount add(String string) {
        this.str = string;
        return this;
    }

    @Override
    public Interactor<Boolean> get() {
        return this;
    }
}
