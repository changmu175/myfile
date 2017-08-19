package com.xdja.imp.domain.interactor.mx;


import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.interactor.def.LogOutMx;
import com.xdja.imp.domain.repository.UserOperateRepository;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:保存草稿用例</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.mx</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/19</p>
 * <p>Time:11:37</p>
 */
public class LogOutUseCase extends MxUseCase<Integer> implements LogOutMx {

    @Inject
    public LogOutUseCase(ThreadExecutor threadExecutor,
                         PostExecutionThread postExecutionThread,
                         UserOperateRepository userOperateRepository) {
        super(threadExecutor, postExecutionThread, userOperateRepository);
    }

    /**
     * 构建业务处理事件流
     *
     * @return 目标事件流
     */
    @Override
    public Observable<Integer> buildUseCaseObservable() {
        return userOperateRepository.releaseRepository();
    }

    /**
     * 退出迷信
     *
     * @return 用例对象
     */
    @Override
    public LogOutMx logoutMx() {
        return null;
    }

    @Override
    public Interactor<Integer> get() {
        return this;
    }
}
