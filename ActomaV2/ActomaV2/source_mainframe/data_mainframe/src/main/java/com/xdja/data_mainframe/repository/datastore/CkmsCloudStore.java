package com.xdja.data_mainframe.repository.datastore;

import android.support.annotation.NonNull;

import com.xdja.data_mainframe.rest.ApiFactory;
import com.xdja.dependence.annotations.ConnSecuritySpe;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.frame.data.net.ServiceGenerator;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Response;
import rx.Observable;

/**
 * Created by tangsha on 2016/7/4.
 */
public class CkmsCloudStore extends CloudStore implements CkmsStore{
    private String TAG = "anTongCkmsCloudStore";
    public static String SEND_CKMS_OPER_CODE = "opCode";

    @Inject
    public CkmsCloudStore(@ConnSecuritySpe(DiConfig.CONN_HTTPS_DEF) ServiceGenerator serviceGenerator) {
        super(serviceGenerator);
    }

    @Override
    public Observable<Response<Map<String, String>>> ckmsOperSign(@NonNull String ckmsOperStr) {
       // Log.d(TAG,"ckmsOperSign  enter");
        Map<String, String> body = new HashMap<>();
        body.put(SEND_CKMS_OPER_CODE, ckmsOperStr);
        Observable<Response<Map<String, String>>> ret = ApiFactory
                                                         .getCkmsApi(this.serviceGenerator)
                                                         .ckmsOperSign(body);
        return ret;
    }
}
