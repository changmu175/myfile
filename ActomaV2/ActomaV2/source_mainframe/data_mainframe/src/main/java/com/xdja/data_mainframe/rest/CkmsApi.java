package com.xdja.data_mainframe.rest;

import java.util.Map;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by tangsha on 2016/7/5.
 */
public interface CkmsApi {
    @POST("ckms/signbase64")
    Observable<Response<Map<String, String>>> ckmsOperSign(@Body Map<String,String> body);
}
