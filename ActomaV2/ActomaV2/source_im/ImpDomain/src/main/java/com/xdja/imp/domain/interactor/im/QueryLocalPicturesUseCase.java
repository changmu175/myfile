package com.xdja.imp.domain.interactor.im;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.interactor.def.QueryLocalPictures;
import com.xdja.imp.domain.model.LocalPictureInfo;
import com.xdja.imp.domain.repository.IMProxyRepository;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 *
 * Created by xdjaxa on 2016/6/17.
 */
public class QueryLocalPicturesUseCase extends IMUseCase<List<LocalPictureInfo>> implements QueryLocalPictures{

    @Inject
    public QueryLocalPicturesUseCase(ThreadExecutor threadExecutor,
                                     PostExecutionThread postExecutionThread,
                                     IMProxyRepository imProxyRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
    }

    @Override
    public QueryLocalPictures queryLocalPictures() {
        return this;
    }

    @Override
    public Observable<List<LocalPictureInfo>> buildUseCaseObservable() {
        return imProxyRepository.queryLocalPictures();
    }

    @Override
    public Interactor<List<LocalPictureInfo>> get() {
        return null;
    }
}
