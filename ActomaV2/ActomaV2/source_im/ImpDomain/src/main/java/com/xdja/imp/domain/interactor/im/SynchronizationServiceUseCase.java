package com.xdja.imp.domain.interactor.im;

import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.SynchronizationService;
import com.xdja.imp.domain.repository.IMProxyRepository;

import javax.inject.Inject;

import rx.Observable;


/**
 * <p>Summary:刷新网络模块</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.im</p>
 * <p>Author:licong</p>
 * <p>Date:2017/2/16</p>
 * <p>Time:11:26</p>
 */
public class SynchronizationServiceUseCase extends IMUseCase<Integer> implements SynchronizationService {

    @Inject
    public SynchronizationServiceUseCase(ThreadExecutor threadExecutor,
                                 PostExecutionThread postExecutionThread,
                                 IMProxyRepository imProxyRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
    }

    @Override
    public Observable<Integer> buildUseCaseObservable() {
        return imProxyRepository.synService();
    }

    @Override
    public Interactor<Integer> get() {
        return this;
    }

}
