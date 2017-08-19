package com.xdja.imp.domain.interactor.im;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.GetAllMissedCount;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.repository.IMProxyRepository;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:获取所有未读消息用例</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.im</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/23</p>
 * <p>Time:10:38</p>
 */
public class GetAllMissedCountUseCase extends IMUseCase<Integer> implements GetAllMissedCount{
    @Inject
    public GetAllMissedCountUseCase(ThreadExecutor threadExecutor,
                                    PostExecutionThread postExecutionThread,
                                    IMProxyRepository imProxyRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
    }

    @Override
    public Observable<Integer> buildUseCaseObservable() {
        return imProxyRepository.getAllUnReadMsgCount();
    }

    @Override
    public Interactor<Integer> get() {
        return this;
    }
}
