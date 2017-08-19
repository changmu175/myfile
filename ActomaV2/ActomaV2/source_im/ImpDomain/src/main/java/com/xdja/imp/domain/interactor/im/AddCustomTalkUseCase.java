package com.xdja.imp.domain.interactor.im;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.AddCustomTalk;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.model.TalkListBean;
import com.xdja.imp.domain.repository.IMProxyRepository;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:添加自定义会话的用例</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.im</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/23</p>
 * <p>Time:10:18</p>
 */
public class AddCustomTalkUseCase extends IMUseCase<TalkListBean> implements AddCustomTalk {

    private TalkListBean talkListBean;

    @Inject
    public AddCustomTalkUseCase(ThreadExecutor threadExecutor,
                                PostExecutionThread postExecutionThread,
                                IMProxyRepository imProxyRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
    }

    @Override
    public Observable<TalkListBean> buildUseCaseObservable() {
        return imProxyRepository.addCustomTalk(this.talkListBean);
    }

    @Override
    public AddCustomTalk add(TalkListBean talkListBean) {
        this.talkListBean = talkListBean;
        return this;
    }

    @Override
    public Interactor<TalkListBean> get() {
        return this;
    }
}
