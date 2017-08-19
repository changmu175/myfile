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
 * <p>Summary:重新获取帐号的用例</p>
 * <p>Description:fill方法参数：旧帐号、内部验证码</p>
 * <p>Package:com.xdja.domain_mainframe.usecase</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/18</p>
 * <p>Time:20:13</p>
 */
public class ReObtainAccountUseCase extends Ext2UseCase<String,String,MultiResult<Object>> {


    private final String KEY_NEW_ACCOUNT = "newAccount";

    private AccountRepository.PreAccountRepository accountRepository;

    @Inject
    public ReObtainAccountUseCase(ThreadExecutor threadExecutor,
                                  PostExecutionThread postExecutionThread,
                                  AccountRepository.PreAccountRepository accountRepository) {
        super(threadExecutor, postExecutionThread);
        this.accountRepository = accountRepository;
    }

    @Override
    public Observable<MultiResult<Object>> buildUseCaseObservable() {

        if (TextUtils.isEmpty(this.p)) {
            return Observable.error(
                    new CheckException("old account cannot null",CheckException.CODE_PARAMES_NOTVALID)
            );
        }

        if (TextUtils.isEmpty(this.p1)) {
            return Observable.error(
                    new CheckException("inner verify cannot null",CheckException.CODE_PARAMES_NOTVALID)
            );
        }

        return this.accountRepository.reObtainAccount(this.p, this.p1)
//                .map(
//                        new Func1<Map<String, String>, String>() {
//                            @Override
//                            public String call(Map<String, String> stringMap) {
//                                if (stringMap == null || stringMap.isEmpty()) {
//                                    throw OnErrorThrowable.from(
//                                            new BusinessException("返回的新账号信息为空",
//                                                    BusinessException.CODE_EXEC_FAILD)
//                                    );
//                                }
//                                if (!stringMap.containsKey(KEY_NEW_ACCOUNT)) {
//                                    throw OnErrorThrowable.from(
//                                            new BusinessException("未查询到新账号信息",
//                                                    BusinessException.CODE_EXEC_FAILD)
//                                    );
//                                }
//                                return stringMap.get(KEY_NEW_ACCOUNT);
//                            }
//                        }
//                )
                ;
    }

}
