package com.xdja.data_mainframe.repository.datastore;

import android.support.annotation.NonNull;

import com.xdja.data_mainframe.rest.ApiFactory;
import com.xdja.dependence.annotations.ConnSecuritySpe;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.domain_mainframe.usecase.settings.UploadFeedBackUseCase;
import com.xdja.frame.data.net.ServiceGenerator;
import com.xdja.frame.data.persistent.PreferencesUtil;

import javax.inject.Inject;

import retrofit2.Response;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by wangchao on 2016/7/25.
 */
public class DownloadCloudStore extends CloudStore implements DownloadStore {

    private String url;

    @Inject
    public DownloadCloudStore(@ConnSecuritySpe(DiConfig.CONN_HTTPS_TICKET) ServiceGenerator serviceGenerator, PreferencesUtil util) {
        super(serviceGenerator);
        url = util.gPrefStringValue("feedBackUrl");
    }

    @Override
    public Observable<Object> download(@NonNull String fileId, long range) {
        return ApiFactory.getDownloadApi(this.serviceGenerator, url).download(range + "")
                .flatMap(new Func1<Response, Observable<Object>>() {
                    @Override
                    public Observable<Object> call(Response response) {
                        return Observable.just((Object) response);
                    }
                });
    }

    @Override
    public Observable<UploadFeedBackUseCase.UploadFeedBackResponeBean> uploadFeedback(@NonNull UploadFeedBackUseCase.FeedBackRequestBean bean) {
        return ApiFactory.getDownloadApi(this.serviceGenerator, url).uploadFeedBack(bean);
    }
}
