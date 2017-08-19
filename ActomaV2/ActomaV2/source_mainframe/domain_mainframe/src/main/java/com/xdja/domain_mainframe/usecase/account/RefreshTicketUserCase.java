package com.xdja.domain_mainframe.usecase.account;

import com.xdja.domain_mainframe.repository.AccountRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext1UseCase;

import java.util.Map;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by ALH on 2016/12/14.
 */

public class RefreshTicketUserCase extends Ext1UseCase<String , Map<String,Object>>  {

    private AccountRepository.PostAccountRepository accountRepository;

    @Inject
    public RefreshTicketUserCase(ThreadExecutor threadExecutor,
                                  PostExecutionThread postExecutionThread,
                                  AccountRepository.PostAccountRepository accountRepository) {
        super(threadExecutor, postExecutionThread);
        this.accountRepository = accountRepository;
    }

    @Override
    public Observable<Map<String,Object>> buildUseCaseObservable() {
        return this.accountRepository.refreshTicket(this.p);
    }
}
