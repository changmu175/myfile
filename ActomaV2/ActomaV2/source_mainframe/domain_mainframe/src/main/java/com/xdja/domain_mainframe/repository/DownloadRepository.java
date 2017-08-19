package com.xdja.domain_mainframe.repository;

import android.support.annotation.NonNull;

import com.xdja.domain_mainframe.usecase.settings.UploadFeedBackUseCase;

import rx.Observable;

/**
 * Created by wangchao on 2016/7/25.
 */
public interface DownloadRepository {
    Observable<Object> download(@NonNull String account, long range);
    Observable<UploadFeedBackUseCase.UploadFeedBackResponeBean> uploadFeedback(@NonNull UploadFeedBackUseCase.FeedBackRequestBean bean);
}
