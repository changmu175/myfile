package com.xdja.imp.domain.interactor.im;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.AddCustomMsg;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.domain.repository.IMProxyRepository;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:增加自定义消息用例</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.im</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/23</p>
 * <p>Time:11:51</p>
 */
public class AddCustomMsgUseCase extends IMUseCase<TalkMessageBean> implements AddCustomMsg{

    private TalkMessageBean talkMessageBean;

    @Inject
    public AddCustomMsgUseCase(ThreadExecutor threadExecutor,
                               PostExecutionThread postExecutionThread,
                               IMProxyRepository imProxyRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
    }

    @Override
    public Observable<TalkMessageBean> buildUseCaseObservable() {
        return imProxyRepository.addCustomMessage(talkMessageBean);
    }

    @Override
    public AddCustomMsg add(TalkMessageBean talkMessageBean) {
        this.talkMessageBean = talkMessageBean;
        return this;
    }

    @Override
    public Interactor<TalkMessageBean> get() {
        return this;
    }
}
