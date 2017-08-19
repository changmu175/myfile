package com.xdja.imp.domain.interactor.mx;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.DeleteSingleSessionConfig;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.repository.UserOperateRepository;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by kgg on 2016/6/2.
 */
public class DeleteSingleSessionConfigUseCase extends MxUseCase<Boolean> implements
        DeleteSingleSessionConfig {

    String talkFlag;

    @Inject
    public DeleteSingleSessionConfigUseCase(ThreadExecutor threadExecutor,
                                            PostExecutionThread postExecutionThread,
                                            UserOperateRepository userOperateRepository) {
        super(threadExecutor, postExecutionThread, userOperateRepository);
    }

    @Override
    public DeleteSingleSessionConfig delete(String talkFlag) {
        this.talkFlag = talkFlag;
        return this;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable() {
        return userOperateRepository.deleteSingleSessionSettingAtLocal(talkFlag);
    }

    @Override
    public Interactor<Boolean> get() {
        return this;
    }
}
