package com.xdja.imp.domain.interactor.im;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.ClearUnReadMsg;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.repository.IMProxyRepository;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:清空会话中的未读消息数量</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.im</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/3</p>
 * <p>Time:18:49</p>
 */
public class ClearUnReadMsgUseCase extends IMUseCase<Integer> implements ClearUnReadMsg {

    private String talkId;

    @Inject
    public ClearUnReadMsgUseCase(ThreadExecutor threadExecutor,
                                 PostExecutionThread postExecutionThread,
                                 IMProxyRepository imProxyRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
    }

    @Override
    public ClearUnReadMsg clear(String talkId) {
        this.talkId = talkId;
        return this;
    }

//    @Override
//    public ClearUnReadMsg clear(String talkId) {
//        this.talkId = talkId;
//        return this;
//    }

    @Override
    public Observable<Integer> buildUseCaseObservable() {
        return imProxyRepository.clearUnReadMsgCount(talkId);
    }

    @Override
    public Interactor<Integer> get() {
        return this;
    }
}
