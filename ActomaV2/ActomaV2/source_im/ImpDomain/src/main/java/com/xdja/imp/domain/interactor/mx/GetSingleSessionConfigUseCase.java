package com.xdja.imp.domain.interactor.mx;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.GetSingleSessionConfig;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.model.SessionConfig;
import com.xdja.imp.domain.repository.UserOperateRepository;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.mx</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/15</p>
 * <p>Time:16:21</p>
 */
public class GetSingleSessionConfigUseCase extends MxUseCase<SessionConfig>
        implements GetSingleSessionConfig {

    private String talkerId;

    @Inject
    public GetSingleSessionConfigUseCase(ThreadExecutor threadExecutor,
                                         PostExecutionThread postExecutionThread,
                                         UserOperateRepository userOperateRepository) {
        super(threadExecutor, postExecutionThread, userOperateRepository);
    }

    @Override
    public Observable<SessionConfig> buildUseCaseObservable() {
        return userOperateRepository.querySingleSessionSettingAtLocal(talkerId);
    }

    @Override
    public Interactor<SessionConfig> get() {
        return this;
    }

    @Override
    public GetSingleSessionConfig get(String talkerId) {
        this.talkerId = talkerId;
        return this;
    }
}
