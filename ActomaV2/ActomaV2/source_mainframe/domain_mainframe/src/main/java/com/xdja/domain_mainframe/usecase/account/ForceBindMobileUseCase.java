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
 * <p>Summary:强制绑定手机号到账号的用例</p>
 * <p>Description:fill方法参数依次为：帐号、内部验证码、手机号</p>
 * <p>Package:com.xdja.domain_mainframe.usecase</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/19</p>
 * <p>Time:19:28</p>
 */
public class ForceBindMobileUseCase extends Ext3UseCase<String, String, String,Void> {

    private AccountRepository.PreAccountRepository accountRepository;

    @Inject
    public ForceBindMobileUseCase(ThreadExecutor threadExecutor,
                                  PostExecutionThread postExecutionThread,
                                  AccountRepository.PreAccountRepository accountRepository) {
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
        if (TextUtils.isEmpty(this.p1)) {
            return Observable.error(
                    new CheckException("inner verify cannot null")
            );
        }
        if (TextUtils.isEmpty(this.p2)) {
            return Observable.error(
                    new CheckException("mobile number cannot null")
            );
        }


        return this.accountRepository.forceBindMobile(this.p, this.p1, this.p2);
    }
}
