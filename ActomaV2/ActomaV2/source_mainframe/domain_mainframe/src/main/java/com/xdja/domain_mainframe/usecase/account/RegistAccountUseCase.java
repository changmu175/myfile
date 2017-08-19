package com.xdja.domain_mainframe.usecase.account;

import android.text.TextUtils;

import com.xdja.dependence.exeptions.CheckException;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.domain_mainframe.repository.AccountRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext4UseCase;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Summary:帐号注册用例</p>
 * <p>Description:fill方法参数依次为：昵称、密码、头像文件Id、头像缩略图Id</p>
 * <p>Package:com.xdja.domain_mainframe.usecase</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/14</p>
 * <p>Time:14:12</p>
 */
public class RegistAccountUseCase extends Ext4UseCase<String,String,String,String,MultiResult<String>> {

    private AccountRepository.PreAccountRepository accountRepository;

    @Inject
    public RegistAccountUseCase(ThreadExecutor threadExecutor,
                                PostExecutionThread postExecutionThread,
                                AccountRepository.PreAccountRepository accountRepository) {
        super(threadExecutor, postExecutionThread);
        this.accountRepository = accountRepository;
    }

    @Override
    public Observable<MultiResult<String>> buildUseCaseObservable() {
        //对密码进行合法性校验
        if (TextUtils.isEmpty(p1)) {
            return Observable.error(
                    new CheckException(CheckException.CODE_PASSWORD_NONE)
            );
        }

        return Observable.just(p1)
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        //TODO:此处对密码进行如下操作：密码字符串的SM3摘要的16进制小写字符串
                        return s;
                    }
                })
                .flatMap(
                        new Func1<String, Observable<MultiResult<String>>>() {
                            @Override
                            public Observable<MultiResult<String>> call(String s) {
                                return accountRepository.registAccount(p, p1, p2, p3);
                            }
                        }
                );

    }
}
