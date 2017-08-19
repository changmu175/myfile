package com.xdja.domain_mainframe.usecase;

import com.xdja.domain_mainframe.repository.CkmsRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext0UseCase;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by tangsha on 2016/8/12.
 */
public class CkmsReleaseUseCase extends Ext0UseCase<Integer> {
    CkmsRepository ckmsRepository;

    @Inject
    public CkmsReleaseUseCase(ThreadExecutor threadExecutor,
                              PostExecutionThread postExecutionThread,
                              CkmsRepository ckmsRepository) {
        super(threadExecutor, postExecutionThread);
        this.ckmsRepository = ckmsRepository;
    }
    @Override
    public Observable<Integer> buildUseCaseObservable() {
        return ckmsRepository.ckmsRelease();
    }
}
