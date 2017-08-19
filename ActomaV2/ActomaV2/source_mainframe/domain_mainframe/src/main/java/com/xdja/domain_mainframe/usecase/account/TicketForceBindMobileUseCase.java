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
 * <p>Summary:强制绑定手机号到账号（Ticket验证）</p>
 * <p>Description:fill方法的参数为手机号</p>
 * <p>Package:com.xdja.domain_mainframe.usecase</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/20</p>
 * <p>Time:19:15</p>
 */
public class TicketForceBindMobileUseCase extends Ext1UseCase<String, Void> {

    private AccountRepository.PostAccountRepository accountRepository;

    @Inject
    public TicketForceBindMobileUseCase(ThreadExecutor threadExecutor,
                                        PostExecutionThread postExecutionThread,
                                        AccountRepository.PostAccountRepository accountRepository) {
        super(threadExecutor, postExecutionThread);
        this.accountRepository = accountRepository;
    }

    @Override
    public Observable<Void> buildUseCaseObservable() {
        if (TextUtils.isEmpty(this.p)) {
            return Observable.error(
                    new CheckException("account cannot null")
            );
        }

        return accountRepository.ticketForceBindMobile(this.p);
    }
}
