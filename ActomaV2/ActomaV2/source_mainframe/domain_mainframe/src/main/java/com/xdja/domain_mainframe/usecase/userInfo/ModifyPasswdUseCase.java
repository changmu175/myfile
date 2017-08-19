package com.xdja.domain_mainframe.usecase.userInfo;

import android.text.TextUtils;

import com.xdja.dependence.exeptions.CheckException;
import com.xdja.domain_mainframe.repository.UserInfoRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext1UseCase;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by ldy on 16/4/20.
 * 修改密码用例
 */
public class ModifyPasswdUseCase extends Ext1UseCase<String,Void> {

    private final UserInfoRepository userInfoRepository;

    @Inject
    public ModifyPasswdUseCase(ThreadExecutor threadExecutor,
                               PostExecutionThread postExecutionThread,
                               UserInfoRepository userInfoRepository) {
        super(threadExecutor, postExecutionThread);
        this.userInfoRepository = userInfoRepository;
    }

    /**
     * 构建业务处理事件流
     *
     * @return 目标事件流
     */
    @Override
    public Observable<Void> buildUseCaseObservable() {
        if (TextUtils.isEmpty(this.p)) {
            return Observable.error(
                    new CheckException("passwd cannot null")
            );
        }
        return userInfoRepository.modifyPasswd(p);
    }
}
