package com.xdja.data_mainframe.repository.datastore;

import android.support.annotation.NonNull;

import com.xdja.domain_mainframe.usecase.settings.UploadFeedBackUseCase;

import rx.Observable;

/**
 * Created by wangchao on 2016/7/25.
 */
public interface DownloadStore {
    Observable<Object> download(@NonNull String fileId, long range);
    Observable<UploadFeedBackUseCase.UploadFeedBackResponeBean> uploadFeedback(@NonNull UploadFeedBackUseCase.FeedBackRequestBean bean);
}
