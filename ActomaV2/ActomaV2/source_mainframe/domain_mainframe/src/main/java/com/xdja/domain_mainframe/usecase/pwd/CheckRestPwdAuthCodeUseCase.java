package com.xdja.domain_mainframe.usecase.pwd;

import android.text.TextUtils;

import com.xdja.dependence.exeptions.CheckException;
import com.xdja.domain_mainframe.repository.PwdRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext3UseCase;

import java.util.Map;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:验证重置密码的酸辛验证码</p>
 * <p>Description:fill方法的参数依次为：手机号、短信验证码和内部验证码</p>
 * <p>Package:com.xdja.domain_mainframe.usecase.pwd</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/23</p>
 * <p>Time:9:33</p>
 */
public class CheckRestPwdAuthCodeUseCase extends Ext3UseCase<String,String,String,Map<String,String>> {

    private PwdRepository pwdRepository;

    @Inject
    public CheckRestPwdAuthCodeUseCase(ThreadExecutor threadExecutor,
                                       PostExecutionThread postExecutionThread,
                                       PwdRepository pwdRepository) {
        super(threadExecutor, postExecutionThread);
        this.pwdRepository = pwdRepository;
    }

    @Override
    public Observable<Map<String, String>> buildUseCaseObservable() {
        if (TextUtils.isEmpty(this.p)) {
            return Observable.error(
                    new CheckException("mobile number cannot null")
            );
        }
        if (TextUtils.isEmpty(this.p)) {
            return Observable.error(
                    new CheckException("verify cannot null")
            );
        }
        if (TextUtils.isEmpty(this.p)) {
            return Observable.error(
                    new CheckException("inner verify cannot null")
            );
        }

        return this.pwdRepository.checkRestPwdAuthCode(this.p, this.p1, this.p2);
    }
}
