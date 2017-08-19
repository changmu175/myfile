package com.xdja.imp.domain.interactor.mx;


import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.DeleteAllDraft;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.repository.UserOperateRepository;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by Administrator on 2016/5/31.
 */
public class DeleteAllDraftUseCase extends MxUseCase<Boolean> implements DeleteAllDraft{

    @Inject
    public DeleteAllDraftUseCase(ThreadExecutor threadExecutor,
                          PostExecutionThread postExecutionThread,
                          UserOperateRepository userOperateRepository){
        super(threadExecutor, postExecutionThread, userOperateRepository);
    }

    @Override
    public Interactor<Boolean> get() {
        return this;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable() {
        return userOperateRepository.deleteAllDraft();
    }

}
