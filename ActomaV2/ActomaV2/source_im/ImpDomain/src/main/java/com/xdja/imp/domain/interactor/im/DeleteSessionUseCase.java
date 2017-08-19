package com.xdja.imp.domain.interactor.im;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.DeleteSession;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.repository.IMProxyRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:删除指定的会话</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.im</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/23</p>
 * <p>Time:10:17</p>
 */
public class DeleteSessionUseCase extends IMUseCase<Integer> implements DeleteSession {

    private List<String> ids;

    @Inject
    public DeleteSessionUseCase(ThreadExecutor threadExecutor,
                                PostExecutionThread postExecutionThread,
                                IMProxyRepository imProxyRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
    }

    @Override
    public Observable<Integer> buildUseCaseObservable() {
        return imProxyRepository.deleteTalks(ids);
    }

    @Override
    public DeleteSession delete(List<String> ids) {
        this.ids = ids;
        return this;
    }

    @Override
    public DeleteSession delete(String talkerId, int type) {
        String sessionFlag = talkerId+"_"+type;
        ids = new ArrayList<>();
        ids.add(sessionFlag);
        return this;
    }

    @Override
    public Interactor<Integer> get() {
        return this;
    }
}
