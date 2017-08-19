package com.xdja.domain_mainframe.usecase.pwd;

import android.text.TextUtils;

import com.xdja.dependence.exeptions.CheckException;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.domain_mainframe.repository.PwdRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext2UseCase;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by ldy on 16/5/11.
 * 验证好友手机号的用例
 * fill方法的参数分别是账号和好友手机号列表
 */
public class AuthFriendPhoneUseCase extends Ext2UseCase<String, List<String>, MultiResult<Object>> {
    private PwdRepository pwdRepository;

    @Inject
    public AuthFriendPhoneUseCase(ThreadExecutor threadExecutor,
                                  PostExecutionThread postExecutionThread,
                                  PwdRepository pwdRepository) {
        super(threadExecutor, postExecutionThread);
        this.pwdRepository = pwdRepository;
    }

    /**
     * 构建业务处理事件流
     *
     * @return 目标事件流
     */
    @Override
    public Observable<MultiResult<Object>> buildUseCaseObservable() {
        if (TextUtils.isEmpty(this.p)) {
            return Observable.error(
                    new CheckException("account cannot null")
            );
        }
        if (p1 == null || p1.isEmpty()) {
            return Observable.error(
                    new CheckException("friends mobile list  cannot null")
            );
        }
        return pwdRepository.authFriendPhone(p,p1);
    }
}
