package com.xdja.imp.domain.interactor.im;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.GetMsgList;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.domain.repository.IMProxyRepository;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:根据会话ID获取指定数量的消息集合</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.im</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/23</p>
 * <p>Time:11:51</p>
 */
public class GetMsgListUseCase extends IMUseCase<List<TalkMessageBean>> implements GetMsgList {

//    private String sessionFlag;

    private String talkId;

    private long begin;

    private int size;

    @Inject
    public GetMsgListUseCase(ThreadExecutor threadExecutor,
                             PostExecutionThread postExecutionThread,
                             IMProxyRepository imProxyRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
    }

    @Override
    public Observable<List<TalkMessageBean>> buildUseCaseObservable() {
        return imProxyRepository.getMessageList(this.talkId,this.begin,this.size);
    }

    @Override
    public GetMsgList get(String talkId, long begin, int size) {
        this.talkId = talkId;
        this.begin = begin;
        this.size = size;
        return this;
    }

    @Override
    public Interactor<List<TalkMessageBean>> get() {
        return this;
    }
}
