package com.xdja.imp.domain.interactor.im;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.CallBackUnRegist;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.repository.IMProxyRepository;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.im</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/12</p>
 * <p>Time:12:02</p>
 */
public class CallBackUnRegistUseCase extends IMUseCase<Integer> implements CallBackUnRegist {

    @Inject
    public CallBackUnRegistUseCase(ThreadExecutor threadExecutor,
                                   PostExecutionThread postExecutionThread,
                                   IMProxyRepository imProxyRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
    }

    @Override
    public Observable<Integer> buildUseCaseObservable() {
        return imProxyRepository.unRegistAllCallBack();
    }

    @Override
    public Interactor<Integer> get() {
        return this;
    }
}
