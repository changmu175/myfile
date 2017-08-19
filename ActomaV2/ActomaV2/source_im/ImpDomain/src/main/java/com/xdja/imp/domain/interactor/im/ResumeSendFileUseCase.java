package com.xdja.imp.domain.interactor.im;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.interactor.def.ResumeSendFile;
import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.repository.IMProxyRepository;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:恢复文件发送用例</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.im</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/3</p>
 * <p>Time:17:04</p>
 */
public class ResumeSendFileUseCase extends IMUseCase<Integer> implements ResumeSendFile {

    private FileInfo fileInfo;

    @Inject
    public ResumeSendFileUseCase(ThreadExecutor threadExecutor,
                                 PostExecutionThread postExecutionThread,
                                 IMProxyRepository imProxyRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
    }

    @Override
    public ResumeSendFile resume(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
        return this;
    }

    @Override
    public Observable<Integer> buildUseCaseObservable() {
        return imProxyRepository.resumeFileSend(this.fileInfo);
    }

    @Override
    public Interactor<Integer> get() {
        return this;
    }
}
