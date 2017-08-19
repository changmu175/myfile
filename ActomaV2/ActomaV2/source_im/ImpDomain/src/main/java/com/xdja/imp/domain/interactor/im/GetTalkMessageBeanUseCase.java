package com.xdja.imp.domain.interactor.im;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.GetTalkMessageBean;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.domain.repository.IMProxyRepository;

import javax.inject.Inject;

import rx.Observable;


/**
 * 项目名称：短视频             <br>
 * 类描述  ：从数据库获取短视频信息     <br>
 * 创建时间：2016/12/26     <br>
 * 修改记录：                 <br>
 *
 * @author jyg@xdja.com   <br>
 */

public class GetTalkMessageBeanUseCase extends IMUseCase<TalkMessageBean> implements GetTalkMessageBean {
    private String msgId;

    @Inject
    public GetTalkMessageBeanUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, IMProxyRepository imProxyRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
    }

    @Override
    public GetTalkMessageBean get(String msgId) {
        this.msgId = msgId;
        return this;
    }

    @Override
    public Observable<TalkMessageBean> buildUseCaseObservable() {
        return imProxyRepository.getMessageById(msgId);
    }
    @Override
    public Interactor<TalkMessageBean> get() {
        return this;
    }
}
