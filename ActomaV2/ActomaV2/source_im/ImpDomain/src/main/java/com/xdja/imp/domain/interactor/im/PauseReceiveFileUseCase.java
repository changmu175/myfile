package com.xdja.imp.domain.interactor.im;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.interactor.def.PauseReceiveFile;
import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.repository.IMProxyRepository;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:暂停文件接收业务用例</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.im</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/3</p>
 * <p>Time:18:44</p>
 */
public class PauseReceiveFileUseCase extends IMUseCase<Integer> implements PauseReceiveFile {

    private FileInfo fileInfo;

    @Inject
    public PauseReceiveFileUseCase(ThreadExecutor threadExecutor,
                                   PostExecutionThread postExecutionThread,
                                   IMProxyRepository imProxyRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
    }

    @Override
    public PauseReceiveFile pause( FileInfo fileInfo) {
        this.fileInfo = fileInfo;
        return this;
    }

    @Override
    public Observable<Integer> buildUseCaseObservable() {
        return imProxyRepository.pauseFileReceiving(fileInfo);
    }

    @Override
    public Interactor<Integer> get() {
        return this;
    }
}
