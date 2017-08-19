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
 * 查询强制下线通知用例
 */
public class QueryForceLogoutNoticeUseCase extends Ext0UseCase<Map<String,String>> {
    private final UserInfoRepository userInfoRepository;

    @Inject
    public QueryForceLogoutNoticeUseCase(ThreadExecutor threadExecutor,
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
        return userInfoRepository.queryForceLogoutNotice();
    }
}
