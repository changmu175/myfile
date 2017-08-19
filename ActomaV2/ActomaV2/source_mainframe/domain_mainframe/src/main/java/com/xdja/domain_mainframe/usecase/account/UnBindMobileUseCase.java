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
 * <p>Summary:解绑手机号的用例</p>
 * <p>Description:fill方法参数为手机号</p>
 * <p>Package:com.xdja.domain_mainframe.usecase.account</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/21</p>
 * <p>Time:17:04</p>
 */
public class UnBindMobileUseCase extends Ext1UseCase<String,Void> {

    private AccountRepository.PostAccountRepository accountRepository;

    @Inject
    public UnBindMobileUseCase(ThreadExecutor threadExecutor,
                               PostExecutionThread postExecutionThread,
                               AccountRepository.PostAccountRepository accountRepository) {
        super(threadExecutor, postExecutionThread);
        this.accountRepository = accountRepository;
    }

    @Override
    public Observable<Void> buildUseCaseObservable() {
        if (TextUtils.isEmpty(this.p)) {
            return Observable.error(
                    new CheckException("mobile number cannot null")
            );
        }

        return this.accountRepository.unbindMobile(this.p);
    }
}
