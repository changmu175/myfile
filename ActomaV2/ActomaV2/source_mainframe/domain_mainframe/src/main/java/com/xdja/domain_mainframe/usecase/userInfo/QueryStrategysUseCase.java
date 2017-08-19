package com.xdja.domain_mainframe.usecase.userInfo;

import com.xdja.comm.encrypt.EncryptAppBean;
import com.xdja.domain_mainframe.repository.UserInfoRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext0UseCase;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by ldy on 16/4/21.
 *  第三方加密应用策略更新用例
 */
public class QueryStrategysUseCase extends Ext0UseCase<List<EncryptAppBean>> {
    private final UserInfoRepository.PreUserInfoRepository userInfoRepository;

    @Inject
    public QueryStrategysUseCase(ThreadExecutor threadExecutor,
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
    public Observable<List<EncryptAppBean>> buildUseCaseObservable() {
        return userInfoRepository.queryStrategys();
    }
}
