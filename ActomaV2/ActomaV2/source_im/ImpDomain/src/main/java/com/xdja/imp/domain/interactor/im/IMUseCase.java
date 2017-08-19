package com.xdja.imp.domain.interactor.im;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.UseCase;
import com.xdja.imp.domain.repository.IMProxyRepository;

/**
 * <p>Summary:IM业务用例基类</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.im</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/20</p>
 * <p>Time:9:15</p>
 */
public abstract class IMUseCase<T> extends UseCase<T> {

    protected IMProxyRepository imProxyRepository;

    protected IMUseCase(ThreadExecutor threadExecutor,
                        PostExecutionThread postExecutionThread,
                        IMProxyRepository imProxyRepository) {
        super(threadExecutor, postExecutionThread);
        this.imProxyRepository = imProxyRepository;
    }
}
