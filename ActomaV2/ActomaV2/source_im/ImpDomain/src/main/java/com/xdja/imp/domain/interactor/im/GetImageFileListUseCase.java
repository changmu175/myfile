package com.xdja.imp.domain.interactor.im;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.GetImageFileList;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.model.LocalPictureInfo;
import com.xdja.imp.domain.repository.IMProxyRepository;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * 获取图片文件信息列表
 * Created by xdjaxa on 2016/8/5.
 */
public class GetImageFileListUseCase extends IMUseCase<List<FileInfo>> implements GetImageFileList {

    private List<LocalPictureInfo> mPictureList;

    @Inject
    public GetImageFileListUseCase(ThreadExecutor threadExecutor,
                                   PostExecutionThread postExecutionThread,
                                   IMProxyRepository imProxyRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
    }

    @Override
    public Observable<List<FileInfo>> buildUseCaseObservable() {
        return imProxyRepository.getImageFileList(mPictureList);
    }

    @Override
    public GetImageFileList getImageFileList(List<LocalPictureInfo> pictureList) {
        this.mPictureList = pictureList;
        return this;
    }

    @Override
    public Interactor<List<FileInfo>> get() {
        return this;
    }
}
