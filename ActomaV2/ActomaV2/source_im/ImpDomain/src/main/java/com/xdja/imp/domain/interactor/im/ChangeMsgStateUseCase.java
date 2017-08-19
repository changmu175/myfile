package com.xdja.imp.domain.interactor.im;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.ChangeMsgState;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.domain.repository.IMProxyRepository;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:更改消息状态</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.im</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/23</p>
 * <p>Time:10:39</p>
 */
public class ChangeMsgStateUseCase extends IMUseCase<Integer> implements ChangeMsgState {

    private TalkMessageBean message;

    private @ConstDef.MsgState int state;

    @Inject
    public ChangeMsgStateUseCase(ThreadExecutor threadExecutor,
                                 PostExecutionThread postExecutionThread,
                                 IMProxyRepository imProxyRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
    }

    @Override
    public Observable<Integer> buildUseCaseObservable() {
        return imProxyRepository.changeMessageState(message, state);
    }

    @Override
    public ChangeMsgState change(TalkMessageBean message, @ConstDef.MsgState int state) {
        this.message = message;
        this.state = state;
        return this;
    }

    @Override
    public Interactor<Integer> get() {
        return this;
    }
}
