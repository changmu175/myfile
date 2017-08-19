package com.xdja.imp.domain.interactor.im;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.GetSessionList;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.model.TalkListBean;
import com.xdja.imp.domain.repository.IMProxyRepository;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:获取会话信息集合</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.im</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/20</p>
 * <p>Time:15:43</p>
 */
public class GetSessionListUseCase extends IMUseCase<List<TalkListBean>> implements GetSessionList{

    /**
     * 起始位置
     */
    private String begin;
    /**
     * 目标集合大小
     */
    private int size;

    @Inject
    public GetSessionListUseCase(ThreadExecutor threadExecutor,
                                 PostExecutionThread postExecutionThread,
                                 IMProxyRepository imProxyRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
    }

    @Override
    public Observable<List<TalkListBean>> buildUseCaseObservable() {
        return imProxyRepository.getTalkListBeans(begin,size);
    }

    @Override
    public Interactor<List<TalkListBean>> get() {
        return this;
    }

    @Override
    public GetSessionList setBegin(String begin) {
        this.begin = begin;
        return this;
    }

    @Override
    public GetSessionList setSize(int size) {
        this.size = size;
        return this;
    }

    @Override
    public GetSessionList setParam(String begin, int size) {
        this.begin = begin;
        this.size = size;
        return this;
    }
}
