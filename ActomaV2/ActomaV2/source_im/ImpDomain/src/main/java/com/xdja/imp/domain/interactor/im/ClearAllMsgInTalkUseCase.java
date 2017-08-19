package com.xdja.imp.domain.interactor.im;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.ClearAllMsgInTalk;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.repository.IMProxyRepository;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.im</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/29</p>
 * <p>Time:20:28</p>
 */
public class ClearAllMsgInTalkUseCase extends IMUseCase<Integer> implements ClearAllMsgInTalk {

    private String talkId;

    @Inject
    public ClearAllMsgInTalkUseCase(ThreadExecutor threadExecutor,
                                    PostExecutionThread postExecutionThread,
                                    IMProxyRepository imProxyRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
    }

    @Override
    public ClearAllMsgInTalk clear(String talkId) {
        this.talkId = talkId;
        return this;
    }

    @Override
    public Observable<Integer> buildUseCaseObservable() {
        return imProxyRepository.clearAllMsgByTalkId(talkId);
    }

    @Override
    public Interactor<Integer> get() {
        return this;
    }
}
