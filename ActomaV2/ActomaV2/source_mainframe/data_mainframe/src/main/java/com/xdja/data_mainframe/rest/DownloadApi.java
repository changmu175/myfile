package com.xdja.data_mainframe.rest;

import com.xdja.domain_mainframe.usecase.settings.UploadFeedBackUseCase;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import rx.Observable;

/**
 * Created by wangchao on 2016/7/25.
 */
public interface DownloadApi {
    @Streaming
    @GET("")
    Observable<Response> download(@Header("Range") String range);

    @POST("feedback/1?type=1")
    Observable<UploadFeedBackUseCase.UploadFeedBackResponeBean> uploadFeedBack(@Body UploadFeedBackUseCase.FeedBackRequestBean requestBean);
}
