package com.xdja.domain_mainframe.usecase.userInfo;

import com.xdja.dependence.exeptions.CheckException;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.domain_mainframe.repository.UserInfoRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext1UseCase;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by ldy on 16/4/21.
 * 批量查询用户信息用例
 */
public class QueryBatchAccountUseCase extends Ext1UseCase<List<String>, MultiResult<String>> {
    private final UserInfoRepository userInfoRepository;

    @Inject
    public QueryBatchAccountUseCase(ThreadExecutor threadExecutor,
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
    public Observable<MultiResult<String>> buildUseCaseObservable() {
        if (p != null && p.isEmpty()) {
            return Observable.error(
                    new CheckException("accounts cannot null")
            );
        }
        return userInfoRepository.queryBatchAccount(p);
    }
}
