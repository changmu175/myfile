package com.xdja.domain_mainframe.usecase.account;

import com.xdja.domain_mainframe.repository.AccountRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext1UseCase;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by ALH on 2017/1/5.
 */

public class UnbindMobileDiskUseCase extends Ext1UseCase<String, Void> {

    private AccountRepository.PostAccountRepository accountRepository;

    @Inject
    public UnbindMobileDiskUseCase(ThreadExecutor threadExecutor,
                               PostExecutionThread postExecutionThread,
                               AccountRepository.PostAccountRepository accountRepository) {
        super(threadExecutor, postExecutionThread);
        this.accountRepository = accountRepository;
    }

    @Override
    public Observable<Void> buildUseCaseObservable() {
        return accountRepository.setMobile(this.p);
    }
}
