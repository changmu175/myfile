package com.xdja.imp.domain.interactor.im;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.CallBackRegist;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.repository.IMProxyRepository;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:注册回调的用例</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.im</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/12</p>
 * <p>Time:11:32</p>
 */
public class CallBackRegistUseCase extends IMUseCase<Integer> implements CallBackRegist{

    Observable<Integer> registObservable;

    @Inject
    public CallBackRegistUseCase(ThreadExecutor threadExecutor,
                                 PostExecutionThread postExecutionThread,
                                 IMProxyRepository imProxyRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
    }

    @Override
    public Observable<Integer> buildUseCaseObservable() {
        return this.registObservable;
    }

    @Override
    public Interactor<Integer> get() {
        return this;
    }

    @Override
    public CallBackRegist registMessageCallBack() {
        this.registObservable = imProxyRepository.registMessageCallBack();
        return this;
    }

    @Override
    public CallBackRegist registSessionCallBack() {
        this.registObservable = imProxyRepository.registSessionCallBack();
        return this;
    }

    @Override
    public CallBackRegist registFileCallBack() {
        this.registObservable = imProxyRepository.registFileCallBack();
        return this;
    }

    @Override
    public CallBackRegist registAllCallBack() {
        this.registObservable = imProxyRepository.registAllCallBack();
        return this;
    }
}
