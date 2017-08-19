package com.xdja.imp.domain.interactor.im;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.GetSessionImageList;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.domain.repository.IMProxyRepository;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by guorong on 2016/7/5.
 */
public class GetSessionImageListUseCase extends IMUseCase<List<TalkMessageBean>> implements GetSessionImageList{
    private String talkId;

    @Inject
    public GetSessionImageListUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, IMProxyRepository imProxyRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
    }

    @Override
    public GetSessionImageList get(String talkId) {
        this.talkId = talkId;
        return this;
    }

    @Override
    public Observable<List<TalkMessageBean>> buildUseCaseObservable() {
        return imProxyRepository.getImageList(talkId , 0 , 0);
    }


    @Override
    public Interactor<List<TalkMessageBean>> get() {
        return this;
    }
}
