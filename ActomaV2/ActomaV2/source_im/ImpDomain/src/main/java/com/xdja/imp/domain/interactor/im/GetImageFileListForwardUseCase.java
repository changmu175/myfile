package com.xdja.imp.domain.interactor.im;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.GetImageFileListForward;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.model.ChatDetailPicInfo;
import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.repository.IMProxyRepository;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * 项目名称：ActomaV2
 * 类描述：获取图片文件信息列表
 * 创建人：yuchangmu
 * 创建时间：2016/11/10.
 * 修改人：
 * 修改时间：
 * 修改备注：
 * 1)Task 2632, modify for share and forward function by ycm at 20161110.
 * 2)Task 2632, modify for share and forward function by ycm at 20161130.
 */
public class GetImageFileListForwardUseCase extends IMUseCase<List<FileInfo>> implements GetImageFileListForward {

        private List<FileInfo> mPictureList;
        private boolean isOriginal;
        @Inject
        public GetImageFileListForwardUseCase(ThreadExecutor threadExecutor,
                                       PostExecutionThread postExecutionThread,
                                       IMProxyRepository imProxyRepository) {
            super(threadExecutor, postExecutionThread, imProxyRepository);
        }

        @Override
        public Observable<List<FileInfo>> buildUseCaseObservable() {
            return imProxyRepository.getImageFileListForForward(mPictureList, isOriginal);
        }

        @Override
        public GetImageFileListForward getImageFileList(List<FileInfo> pictureList, boolean isOriginal) {
            this.mPictureList = pictureList;
            this.isOriginal = isOriginal;
            return this;
        }

        @Override
        public Interactor<List<FileInfo>> get() {
            return this;
        }
}
