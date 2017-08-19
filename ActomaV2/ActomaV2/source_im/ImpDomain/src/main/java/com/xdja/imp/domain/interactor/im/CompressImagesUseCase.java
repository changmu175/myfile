package com.xdja.imp.domain.interactor.im;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.CompressImages;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.model.WebPageInfo;
import com.xdja.imp.domain.repository.IMProxyRepository;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * 项目名称：ActomaV2
 * 类描述：压缩网页中的图片
 * 创建人：yuchangmu
 * 创建时间：2017/3/10.
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class CompressImagesUseCase extends IMUseCase<List<WebPageInfo>> implements CompressImages {
    private List<WebPageInfo> fileInfoList;

    @Inject
    public CompressImagesUseCase(ThreadExecutor threadExecutor,
                                 PostExecutionThread postExecutionThread,
                                 IMProxyRepository imProxyRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
    }

    @Override
    public CompressImages compressFile(List<WebPageInfo> fileInfoList) {
        this.fileInfoList = fileInfoList;
        return this;
    }

    @Override
    public Observable<List<WebPageInfo>> buildUseCaseObservable() {
        return imProxyRepository.getCompressFileList(this.fileInfoList);
    }

    @Override
    public Interactor<List<WebPageInfo>> get() {
        return this;
    }


}
