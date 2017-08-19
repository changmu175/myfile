package com.xdja.domain_mainframe.usecase.account;

import android.text.TextUtils;

import com.xdja.comm_mainframe.error.BusinessException;
import com.xdja.dependence.exeptions.CheckException;
import com.xdja.domain_mainframe.repository.AccountRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext2UseCase;

import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Summary:获取绑定手机所需的验证码</p>
 * <p>Description:fill方法参数依次为：帐号、手机号</p>
 * <p>Package:com.xdja.domain_mainframe.usecase</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/19</p>
 * <p>Time:19:06</p>
 */
public class ObtainBindMobileAuthCodeUseCase extends Ext2UseCase<String, String, String> {

    private final String KEY_INNERAUTHCODE = "innerAuthCode";

    private AccountRepository.PreAccountRepository accountRepository;

    @Inject
    public ObtainBindMobileAuthCodeUseCase(ThreadExecutor threadExecutor,
                                           PostExecutionThread postExecutionThread,
                                           AccountRepository.PreAccountRepository accountRepository) {
        super(threadExecutor, postExecutionThread);
        this.accountRepository = accountRepository;
    }

    @Override
    public Observable<String> buildUseCaseObservable() {
        if (TextUtils.isEmpty(this.p)) {
            return Observable.error(
                    new CheckException("account cannot null")
            );
        }

        if (TextUtils.isEmpty(this.p1)) {
            return Observable.error(
                    new CheckException("mobile number cannot null")
            );
        }

        return this.accountRepository.obtainBindMobileAuthCode(this.p, this.p1)
                .map(
                        new Func1<Map<String, String>, String>() {
                            @Override
                            public String call(Map<String, String> stringMap) {
                                if (stringMap == null || stringMap.isEmpty()) {
                                    //[S]modify by xienana for Business Exception throw @2016/09/20 [reviewed by tangsha]
                                    throw  new BusinessException(
                                                    BusinessException.CODE_EXEC_FAILD);
                                }
                                if (!stringMap.containsKey(KEY_INNERAUTHCODE)) {
                                    throw new BusinessException(
                                                    BusinessException.CODE_EXEC_EMPTY_FAILD);
                                }//[E]modify by xienana for Business Exception throw @2016/09/20 [reviewed by tangsha]
                                return stringMap.get(KEY_INNERAUTHCODE);
                            }
                        }
                );
    }
}
