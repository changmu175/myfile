package com.xdja.domain_mainframe.usecase.account;

import android.text.TextUtils;

import com.xdja.dependence.exeptions.CheckException;
import com.xdja.domain_mainframe.repository.AccountRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext1UseCase;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:自定义帐号（Ticket）</p>
 * <p>Description:fill方法的参数为自定义的帐号</p>
 * <p>Package:com.xdja.domain_mainframe.usecase.account</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/21</p>
 * <p>Time:16:33</p>
 */
public class TicketCustomAccountUseCase extends Ext1UseCase<String, Void> {

    private AccountRepository.PostAccountRepository accountRepository;

    @Inject
    public TicketCustomAccountUseCase(ThreadExecutor threadExecutor,
                                      PostExecutionThread postExecutionThread,
                                      AccountRepository.PostAccountRepository accountRepository) {
        super(threadExecutor, postExecutionThread);
        this.accountRepository = accountRepository;
    }

    @Override
    public Observable<Void> buildUseCaseObservable() {
        if (TextUtils.isEmpty(this.p)) {
            return Observable.error(
                    new CheckException("account cannot null by yourself")
            );
        }
        return this.accountRepository.ticketCustomAccount(this.p);
    }
}
