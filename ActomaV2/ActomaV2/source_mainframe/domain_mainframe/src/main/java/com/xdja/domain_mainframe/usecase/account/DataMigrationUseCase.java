package com.xdja.domain_mainframe.usecase.account;

import com.xdja.comm.bean.DataMigrationAccountBean;
import com.xdja.domain_mainframe.repository.AccountRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext0UseCase;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by ALH on 2016/8/15.
 */
public class DataMigrationUseCase extends Ext0UseCase<DataMigrationAccountBean> {
    public static final String KEY_ISOLD = "isOld";
    public static final String KEY_ACCOUNT = "account";
    private AccountRepository.PreAccountRepository mAccountRepository;

    @Inject
    public DataMigrationUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
                                AccountRepository.PreAccountRepository accountRepository) {
        super(threadExecutor, postExecutionThread);
        mAccountRepository = accountRepository;
    }

    @Override
    public Observable<DataMigrationAccountBean> buildUseCaseObservable() {
        return mAccountRepository.isNewAccount();
    }
}
