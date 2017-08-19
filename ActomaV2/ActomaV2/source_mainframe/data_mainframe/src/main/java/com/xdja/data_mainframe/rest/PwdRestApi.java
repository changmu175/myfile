package com.xdja.data_mainframe.rest;

import com.xdja.domain_mainframe.model.MultiResult;

import java.util.Map;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import rx.Observable;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.data_mainframe.rest</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/21</p>
 * <p>Time:20:09</p>
 */
public interface PwdRestApi {

    @POST("passwd/auth/code")
    Observable<Response<Map<String,String>>> checkRestPwdAuthCode(@Body Map<String,String> body);

    @PUT("passwd/reset/auth/code")
    Observable<Response<Void>> restPwdByAuthCode(@Body Map<String,String> body);

    @PUT("passwd/reset/check/friend/mobile")
    Observable<Response<Void>> restPwdByFriendMobiles(@Body Map<String,String> body);

    @POST("passwd/check/friend/mobile")
    Observable<Response<MultiResult<Object>>> authFriendPhone(@Body Map<String,Object> body);

}
