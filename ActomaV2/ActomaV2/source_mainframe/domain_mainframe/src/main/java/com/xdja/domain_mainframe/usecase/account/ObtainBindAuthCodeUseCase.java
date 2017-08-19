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
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.domain_mainframe.usecase</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/20</p>
 * <p>Time:18:41</p>
 */
public class ObtainBindAuthCodeUseCase extends Ext1UseCase<String,Void> {

    private AccountRepository.PostAccountRepository accountRepository;

    @Inject
    public ObtainBindAuthCodeUseCase(ThreadExecutor threadExecutor,
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

        return this.accountRepository.obtainBindAuthCode(this.p);
    }
}
