package com.xdja.data_mainframe.rest;

import com.xdja.domain_mainframe.model.MultiResult;

import java.util.Map;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.data_mainframe.rest</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/21</p>
 * <p>Time:19:57</p>
 */
public interface DeviceAuthRestApi {

    @GET("authCode/{account}/bindDevice/{mobile}")
    Observable<Response<Map<String,String>>> obtainDeviceAuthrizeAuthCode(@Path("account") String account,
                                                                          @Path("mobile") String mobile);

    @POST("device/accredit/check/friend/mobile")
    Observable<Response<MultiResult<Object>>> checkFriendMobiles(@Body Map<String,Object> body);

    @POST("device/accredit/check/bind/mobile")
    Observable<Response<Void>> checkMobile(@Body Map<String,String> body);

    @GET("message/authorize/{authorizeId}")
    Observable<Response<Map<String,String>>> obtainAuthInfo(@Path("authorizeId") String authorizeId);

    @PUT("message/authorize/create")
    Observable<Response<Map<String,String>>> reObtaionAuthInfo(@Body Map<String,String> body);

    @POST("device/accredit")
    Observable<Response<Void>> authDevice(@Body Map<String,String> body);
}
