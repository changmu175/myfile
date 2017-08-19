package com.xdja.domain_mainframe.usecase.userInfo;

import android.text.TextUtils;

import com.xdja.dependence.exeptions.CheckException;
import com.xdja.domain_mainframe.repository.UserInfoRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext1UseCase;

import java.util.Map;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by ldy on 16/4/19.
 * 修改昵称用例,参数为昵称
 */
public class ModifyNikeNameUseCase extends Ext1UseCase<String, Map<String, String>> {


    private final UserInfoRepository userInfoRepository;

    @Inject
    public ModifyNikeNameUseCase(ThreadExecutor threadExecutor,
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
    public Observable<Map<String, String>> buildUseCaseObservable() {
        if (TextUtils.isEmpty(this.p)) {
            return Observable.error(
                    new CheckException("nickname cannot null")
            );
        }
        return userInfoRepository.modifyNickName(p);
    }
}
