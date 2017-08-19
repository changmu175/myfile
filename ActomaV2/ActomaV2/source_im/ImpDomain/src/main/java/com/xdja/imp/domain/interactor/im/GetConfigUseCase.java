package com.xdja.imp.domain.interactor.im;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.GetConfig;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.repository.IMProxyRepository;
import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:获取配置信息的用例</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.im</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/20</p>
 * <p>Time:14:56</p>
 */
public class GetConfigUseCase extends IMUseCase<String> implements GetConfig {

    String key;

    @Inject
    public GetConfigUseCase(ThreadExecutor threadExecutor,
                            PostExecutionThread postExecutionThread,
                            IMProxyRepository imProxyRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
    }

    @Override
    public Observable<String> buildUseCaseObservable() {
        return imProxyRepository.getProxyConfig(key);
    }

    @Override
    public Interactor<String> get() {
        return this;
    }

    @Override
    public GetConfig get(String key) {
        this.key=key;
        return this;
    }
}
