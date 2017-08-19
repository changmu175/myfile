package com.xdja.data_mainframe.repository;

import android.support.annotation.NonNull;

import com.xdja.data_mainframe.repository.datastore.DownloadStore;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.dependence.annotations.StoreSpe;
import com.xdja.domain_mainframe.repository.DownloadRepository;
import com.xdja.domain_mainframe.usecase.settings.UploadFeedBackUseCase;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by wangchao on 2016/7/28.
 */
public class DownloadRepImp implements DownloadRepository {
    DownloadStore cloudStore;

    @Inject
    public DownloadRepImp(@StoreSpe(DiConfig.TYPE_CLOUD) DownloadStore cloudStore) {
        this.cloudStore = cloudStore;
    }

    @Override
    public Observable<Object> download(@NonNull String account, long range) {
        return cloudStore.download(account, range);
    }

    @Override
    public Observable<UploadFeedBackUseCase.UploadFeedBackResponeBean> uploadFeedback(@NonNull UploadFeedBackUseCase.FeedBackRequestBean bean) {
        return cloudStore.uploadFeedback(bean);
    }
}
