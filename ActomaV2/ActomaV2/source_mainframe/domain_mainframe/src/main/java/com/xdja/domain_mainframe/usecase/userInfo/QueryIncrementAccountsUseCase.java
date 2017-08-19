package com.xdja.domain_mainframe.usecase.userInfo;

import com.xdja.dependence.exeptions.CheckException;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.domain_mainframe.repository.UserInfoRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext2UseCase;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by ldy on 16/4/21.
 * 获取用户相关账号信息用例
 */
public class QueryIncrementAccountsUseCase extends Ext2UseCase<Integer, Integer, MultiResult<String>> {
    private final UserInfoRepository userInfoRepository;

    @Inject
    public QueryIncrementAccountsUseCase(ThreadExecutor threadExecutor,
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
        if (p == null) {
            return Observable.error(
                    new CheckException("lastUpdateId cannot null")
            );
        }
        if (p1 == null) {
            return Observable.error(
                    new CheckException("batchSize cannot null")
            );
        }
        return userInfoRepository.queryIncrementAccounts(p, p1);
    }
}
