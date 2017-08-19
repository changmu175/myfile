package com.xdja.data_mainframe.repository.datastore;

import android.support.annotation.NonNull;

import com.xdja.domain_mainframe.usecase.settings.UploadFeedBackUseCase;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by wangchao on 2016/7/25.
 */
public class DownloadDiskStore implements DownloadStore {
    @Inject
    public DownloadDiskStore() {

    }

    @SuppressWarnings("ReturnOfNull")
    @Override
    public Observable<Object> download(@NonNull String fileId, long range) {
        return null;
    }

    @SuppressWarnings("ReturnOfNull")
    @Override
    public Observable<UploadFeedBackUseCase.UploadFeedBackResponeBean> uploadFeedback(@NonNull UploadFeedBackUseCase
            .FeedBackRequestBean bean) {
        return null;
    }
}
