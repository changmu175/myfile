package com.xdja.domain_mainframe.usecase;

import com.xdja.domain_mainframe.repository.DownloadRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext2UseCase;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:文件下载用例</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.actoma.domain</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/7/25</p>
 * <p>Time:15:25</p>
 */
public class DownloadUseCase extends Ext2UseCase<String, Long, Object> {
    DownloadRepository downloadRepository;

    @Inject
    public DownloadUseCase(ThreadExecutor threadExecutor,
                           PostExecutionThread postExecutionThread,
                           DownloadRepository downloadRepository) {
        super(threadExecutor, postExecutionThread);
        this.downloadRepository = downloadRepository;
    }

    @Override
    public Observable<Object> buildUseCaseObservable() {
        return downloadRepository.download(p, p1);
    }
}
