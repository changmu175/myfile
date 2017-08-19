package com.xdja.imp.domain.interactor.im;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.DeleteMsg;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.repository.IMProxyRepository;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:删除消息的用例定义</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.im</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/23</p>
 * <p>Time:11:52</p>
 */
public class DeleteMsgUseCase extends IMUseCase<Integer> implements DeleteMsg{

    private List<Long> ids;

    @Inject
    public DeleteMsgUseCase(ThreadExecutor threadExecutor,
                            PostExecutionThread postExecutionThread,
                            IMProxyRepository imProxyRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
    }

    @Override
    public Observable<Integer> buildUseCaseObservable() {
        return imProxyRepository.deleteMessages(ids);
    }

    @Override
    public DeleteMsg delete(List<Long> ids) {
        this.ids = ids;
        return this;
    }

    @Override
    public Interactor<Integer> get() {
        return this;
    }
}
