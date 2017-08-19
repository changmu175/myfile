package com.xdja.imp.domain.interactor.im;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.interactor.def.SendMessage;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.domain.repository.IMProxyRepository;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:发送消息业务用例</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.im</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/3</p>
 * <p>Time:19:18</p>
 */
public class SendMessageUseCase extends IMUseCase<TalkMessageBean> implements SendMessage {

    private TalkMessageBean talkMessageBean;

    @Inject
    public SendMessageUseCase(ThreadExecutor threadExecutor,
                              PostExecutionThread postExecutionThread,
                              IMProxyRepository imProxyRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
    }

    @Override
    public SendMessage send(TalkMessageBean talkMessageBean) {

        this.talkMessageBean = talkMessageBean;

        return this;
    }

    @Override
    public Observable<TalkMessageBean> buildUseCaseObservable() {
        return imProxyRepository.sendMessage(talkMessageBean);
    }

    @Override
    public Interactor<TalkMessageBean> get() {
        return this;
    }
}
