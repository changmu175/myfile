package com.xdja.imp.domain.interactor.im;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.interactor.def.QueryLastFiles;
import com.xdja.imp.domain.model.LocalFileInfo;
import com.xdja.imp.domain.repository.IMProxyRepository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Author: leiliangliang   </br>
 * <p>Date: 2016/12/15 14:34   </br>
 * <p>Package: com.xdja.imp.domain.interactor.im</br>
 * <p>Description:            </br>
 */
public class QueryLastFilesUseCase extends IMUseCase<Map<String, List<LocalFileInfo>>> implements QueryLastFiles {

    @Inject
    public QueryLastFilesUseCase(ThreadExecutor threadExecutor,
                                 PostExecutionThread postExecutionThread,
                                 IMProxyRepository imProxyRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
    }

    @Override
    public Observable<Map<String, List<LocalFileInfo>>> buildUseCaseObservable() {
        return imProxyRepository.queryLastFiles();
    }

    @Override
    public QueryLastFiles queryLastFiles() {
        return this;
    }

    @Override
    public Interactor<Map<String, List<LocalFileInfo>>> get() {
        return this;
    }
}
