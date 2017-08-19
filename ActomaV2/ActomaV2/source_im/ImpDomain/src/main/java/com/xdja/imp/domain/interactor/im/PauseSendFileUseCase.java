package com.xdja.imp.domain.interactor.im;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.interactor.def.PauseSendFile;
import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.repository.IMProxyRepository;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:暂停文件上传业务用例</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.im</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/3</p>
 * <p>Time:18:32</p>
 */
public class PauseSendFileUseCase extends IMUseCase<Integer> implements PauseSendFile{

    private FileInfo fileInfo;
    
    @Inject
    public PauseSendFileUseCase(ThreadExecutor threadExecutor,
                                PostExecutionThread postExecutionThread,
                                IMProxyRepository imProxyRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
    }

    @Override
    public PauseSendFile pause(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
        return this;
    }

    @Override
    public Observable<Integer> buildUseCaseObservable() {
        return imProxyRepository.pauseFileSending(this.fileInfo);
    }

    @Override
    public Interactor<Integer> get() {
        return this;
    }
}
