package com.xdja.domain_mainframe.usecase.userInfo;

import com.xdja.domain_mainframe.repository.UserInfoRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext0UseCase;

import java.util.Map;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by ldy on 16/4/21.
 * 获取服务配置用例
 */
public class QueryServerConfigsUseCase extends Ext0UseCase<Map<String,String>> {
    private final UserInfoRepository.PreUserInfoRepository userInfoRepository;

    @Inject
    public QueryServerConfigsUseCase(ThreadExecutor threadExecutor,
                         PostExecutionThread postExecutionThread,
                         UserInfoRepository.PreUserInfoRepository userInfoRepository) {
        super(threadExecutor, postExecutionThread);
        this.userInfoRepository = userInfoRepository;
    }
    /**
     * 构建业务处理事件流
     *
     * @return 目标事件流
     */
    @Override
    public Observable<Map<String,String>> buildUseCaseObservable() {
        return userInfoRepository.queryServerConfigs();
    }
}
