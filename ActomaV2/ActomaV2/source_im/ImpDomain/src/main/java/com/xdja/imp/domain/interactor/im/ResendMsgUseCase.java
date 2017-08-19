package com.xdja.imp.domain.interactor.im;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.interactor.def.ResendMsg;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.domain.repository.IMProxyRepository;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:重发消息用例</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.im</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/23</p>
 * <p>Time:10:40</p>
 */
public class ResendMsgUseCase extends IMUseCase<Integer> implements ResendMsg{

    private TalkMessageBean talkMessageBean;

    @Inject
    public ResendMsgUseCase(ThreadExecutor threadExecutor,
                            PostExecutionThread postExecutionThread,
                            IMProxyRepository imProxyRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
    }

    @Override
    public Observable<Integer> buildUseCaseObservable() {
        return imProxyRepository.resendMsg(talkMessageBean);
    }

    @Override
    public ResendMsg setChatMsg(TalkMessageBean talkMessageBean) {
        this.talkMessageBean = talkMessageBean;
        return this;
    }

    @Override
    public Interactor<Integer> get() {
        return this;
    }
}
