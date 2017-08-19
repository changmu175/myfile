package com.xdja.domain_mainframe.usecase.account;

import android.text.TextUtils;

import com.xdja.dependence.exeptions.CheckException;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.domain_mainframe.repository.AccountRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext3UseCase;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:手机号和验证码登录的用例</p>
 * <p>Description:fill方法参数依次为：手机号、验证码和内部验证码</p>
 * <p>Package:com.xdja.domain_mainframe.usecase</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/20</p>
 * <p>Time:15:43</p>
 */
public class MobileLoginUseCase extends Ext3UseCase<String, String, String, MultiResult<Object>> {

    private AccountRepository.PreAccountRepository accountRepository;

    @Inject
    public MobileLoginUseCase(ThreadExecutor threadExecutor,
                              PostExecutionThread postExecutionThread,
                              AccountRepository.PreAccountRepository accountRepository) {
        super(threadExecutor, postExecutionThread);
        this.accountRepository = accountRepository;
    }

    @Override
    public Observable<MultiResult<Object>> buildUseCaseObservable() {
        if (TextUtils.isEmpty(this.p)) {
            return Observable.error(
                    new CheckException("mobile number cannot null")
            );
        }

        if (TextUtils.isEmpty(this.p1)) {
            return Observable.error(
                    new CheckException("verify cannot null")
            );
        }

        if (TextUtils.isEmpty(this.p2)) {
            return Observable.error(
                    new CheckException("inner verify cannot null")
            );
        }

        return this.accountRepository.mobileLogin(this.p, this.p1, this.p2);
    }
}
