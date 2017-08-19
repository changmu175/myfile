package com.xdja.domain_mainframe.usecase.account;

import android.text.TextUtils;

import com.xdja.dependence.exeptions.CheckException;
import com.xdja.domain_mainframe.repository.AccountRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext3UseCase;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:用户生成新帐号后，确认新帐号</p>
 * <p>Description:fill方法依次为：旧帐号，新帐号，内部验证码</p>
 * <p>Package:com.xdja.domain_mainframe.usecase</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/19</p>
 * <p>Time:14:50</p>
 */
public class ModifyAccountUseCase extends Ext3UseCase<String, String, String, Void> {

    private AccountRepository.PreAccountRepository accountRepository;

    @Inject
    public ModifyAccountUseCase(ThreadExecutor threadExecutor,
                                PostExecutionThread postExecutionThread,
                                AccountRepository.PreAccountRepository accountRepository) {
        super(threadExecutor, postExecutionThread);
        this.accountRepository = accountRepository;
    }

    @Override
    public Observable<Void> buildUseCaseObservable() {
        if (TextUtils.isEmpty(this.p)) {
            return Observable.error(
                    new CheckException("old account cannot null")
            );
        }

        if (TextUtils.isEmpty(this.p)) {
            return Observable.error(
                    new CheckException("new account cannot null")
            );
        }

        if (TextUtils.isEmpty(this.p)) {
            return Observable.error(
                    new CheckException("inner verify cannot null")
            );
        }

        return this.accountRepository.modifyAccount(this.p, this.p1, this.p2);

    }
}
