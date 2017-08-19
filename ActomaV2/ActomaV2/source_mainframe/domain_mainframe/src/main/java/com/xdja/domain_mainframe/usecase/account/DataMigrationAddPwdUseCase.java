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
import rx.functions.Func1;

/**
 * Created by ALH on 2016/8/17.
 */
public class DataMigrationAddPwdUseCase extends Ext3UseCase<String ,String ,String ,MultiResult<Object>> {

    private AccountRepository.PreAccountRepository mAccountRepository;

    @Inject
    public DataMigrationAddPwdUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
                                AccountRepository.PreAccountRepository accountRepository) {
        super(threadExecutor, postExecutionThread);
        mAccountRepository = accountRepository;
    }

    @Override
    public Observable<MultiResult<Object>> buildUseCaseObservable() {
        if (TextUtils.isEmpty(this.p)) {
            return Observable.error(new CheckException("account  cannot null", CheckException.CODE_ACCOUNT_NONE)
            );
        }

        if (TextUtils.isEmpty(this.p1)) {
            return Observable.error(
                    new CheckException("password cannot null", CheckException.CODE_PASSWORD_NONE)
            );
        }

        if (TextUtils.isEmpty(this.p2)) {
            return Observable.error(
                    new CheckException("please verify password", CheckException.CODE_PASSWORD_NONE)
            );
        }

        if (p1.length() < 6 || p1.length() > 20) {
            return Observable.error(
                    new CheckException("password must be 6~20 strings(with capital and lower case)", CheckException.CODE_PASSWORD_FORMAT)
            );
        }
        if (!TextUtils.equals(p1 , p2)){
            return Observable.error(
                    new CheckException("password inconsistent", CheckException.CODE_PASSWORD_DISCORD)
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
                        new Func1<String, Observable<MultiResult<Object>>>() {
                            @Override
                            public Observable<MultiResult<Object>> call(String s) {
                                return mAccountRepository.migrateOldAccount(p, p1);
                            }
                        }
                );
    }
}
