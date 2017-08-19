package com.xdja.imp.domain.interactor.im;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.ClearAllData;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.repository.IMProxyRepository;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by Administrator on 2016/3/23.
 * 功能描述
 */
public class ClearAllDataUseCase extends IMUseCase<Integer> implements ClearAllData {

    @Inject
    public ClearAllDataUseCase(ThreadExecutor threadExecutor,
                            PostExecutionThread postExecutionThread,
                            IMProxyRepository imProxyRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
    }

    @Override
    public ClearAllData deleteAllSession() {
        return this;
    }

    @Override
    public Observable<Integer> buildUseCaseObservable() {
        return imProxyRepository.clearAllData();
    }

    @Override
    public Interactor<Integer> get() {
        return this;
    }
}
