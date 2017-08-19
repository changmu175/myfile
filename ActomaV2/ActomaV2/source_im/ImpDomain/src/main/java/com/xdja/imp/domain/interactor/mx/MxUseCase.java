package com.xdja.imp.domain.interactor.mx;


import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.UseCase;
import com.xdja.imp.domain.repository.UserOperateRepository;

/**
 * <p>Summary:用户设置通用用例</p>
 * <p>Description:</p>
 * <p>Package:com.imdo.interactor</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/2</p>
 * <p>Time:15:17</p>
 */
public abstract class MxUseCase<T> extends UseCase<T> {

    protected UserOperateRepository userOperateRepository;

    protected MxUseCase(ThreadExecutor threadExecutor,
                        PostExecutionThread postExecutionThread,
                        UserOperateRepository userOperateRepository) {
        super(threadExecutor, postExecutionThread);
        this.userOperateRepository = userOperateRepository;
    }


}
