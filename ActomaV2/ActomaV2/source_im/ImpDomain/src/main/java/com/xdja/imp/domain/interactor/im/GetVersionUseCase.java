package com.xdja.imp.domain.interactor.im;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.GetVersion;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.repository.IMProxyRepository;

import javax.inject.Inject;

import rx.Observable;

/**
 * 项目名称：ActomaV2
 * 类描述：
 * 创建人：yuchangmu
 * 创建时间：2017/2/15.
 * 修改人：yuchangmu
 * 修改时间：2017/2/15.
 * 修改备注：modified by ycm for share and forward function
 */
public class GetVersionUseCase extends IMUseCase<Integer> implements GetVersion {
    String account;
    String ticket;
    @Inject
    public GetVersionUseCase(ThreadExecutor threadExecutor,
                             PostExecutionThread postExecutionThread,
                             IMProxyRepository imProxyRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
    }

    @Override
    public Observable<Integer> buildUseCaseObservable() {
        return imProxyRepository.getVersion(account, ticket);
    }

    @Override
    public Interactor<Integer> get() {
        return this;
    }

    @Override
    public GetVersion setParam(String account, String ticket) {
        this.account = account;
        this.ticket = ticket;
        return this;
    }
}
