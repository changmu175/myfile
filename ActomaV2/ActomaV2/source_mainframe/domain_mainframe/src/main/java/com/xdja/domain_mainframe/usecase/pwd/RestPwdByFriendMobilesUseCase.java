package com.xdja.domain_mainframe.usecase.pwd;

import android.text.TextUtils;

import com.xdja.dependence.exeptions.CheckException;
import com.xdja.domain_mainframe.repository.PwdRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext3UseCase;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:通过好友手机号重置密码</p>
 * <p>Description:fill方法的参数依次为：帐号、内部验证码和密码</p>
 * <p>Package:com.xdja.domain_mainframe.usecase.pwd</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/23</p>
 * <p>Time:9:37</p>
 */
public class RestPwdByFriendMobilesUseCase extends Ext3UseCase<String, String, String, Void> {

    private PwdRepository pwdRepository;

    @Inject
    public RestPwdByFriendMobilesUseCase(ThreadExecutor threadExecutor,
                                         PostExecutionThread postExecutionThread,
                                         PwdRepository pwdRepository) {
        super(threadExecutor, postExecutionThread);
        this.pwdRepository = pwdRepository;
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
                    new CheckException("password cannot null")
            );
        }
        return this.pwdRepository.restPwdByFriendMobiles(this.p, this.p1, this.p2);
    }
}
