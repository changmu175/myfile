package com.xdja.domain_mainframe.usecase.account;

import android.text.TextUtils;

import com.xdja.dependence.exeptions.CheckException;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.domain_mainframe.repository.AccountRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext2UseCase;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:帐号和密码登录用例</p>
 * <p>Description:fill方法参数依次为：帐号、密码</p>
 * <p>Package:com.xdja.domain_mainframe.usecase</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/20</p>
 * <p>Time:14:11</p>
 */
public class AccountPwdLoginUseCase extends Ext2UseCase<String,String,MultiResult<Object>> {

    private AccountRepository.PreAccountRepository accountRepository;

    @Inject
    public AccountPwdLoginUseCase(ThreadExecutor threadExecutor,
                                  PostExecutionThread postExecutionThread,
                                  AccountRepository.PreAccountRepository accountRepository) {
        super(threadExecutor, postExecutionThread);
        this.accountRepository = accountRepository;
    }

    @Override
    public Observable<MultiResult<Object>> buildUseCaseObservable() {

        if (TextUtils.isEmpty(this.p)) {
            return Observable.error(
                    new CheckException("account cannot null")
            );
        }
        if (TextUtils.isEmpty(this.p1)) {
            return Observable.error(
                    new CheckException("password cannot null")
            );
        }

        return this.accountRepository.accountPwdLogin(this.p, this.p1);
    }
}
