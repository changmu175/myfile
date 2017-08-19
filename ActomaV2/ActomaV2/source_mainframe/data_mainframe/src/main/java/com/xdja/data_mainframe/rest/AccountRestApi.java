package com.xdja.data_mainframe.rest;

import com.xdja.comm.bean.DataMigrationAccountBean;
import com.xdja.domain_mainframe.model.MultiResult;

import java.util.Map;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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
 * <p>Date:2016/4/14</p>
 * <p>Time:20:02</p>
 */
public interface AccountRestApi {

    @POST("account/judgecomplete")
    Observable<Response<MultiResult<Object>>> migrateOldAccount(@Body Map<String ,String> body);

    @GET("account/judge")
    Observable<DataMigrationAccountBean> isNewAccount();

    @POST("register")
    Observable<Response<MultiResult<String>>> registAccount(@Body Map<String,String> body);

    @GET("account/new/{oldAccount}/auth/{innerAuthCode}")
    Observable<Response<MultiResult<Object>>> reObtainAccount(@Path("oldAccount") String oldAccount,
                                                             @Path("innerAuthCode") String innerAuthCode);

    @PUT("account/customize/authPasswd")
    Observable<Response<Void>> customaAccount(@Body Map<String,String> body);

    @POST("account/modify")
    Observable<Response<Void>> modifyAccount(@Body Map<String,String> body);

    @GET("authCode/register/{account}/bindMobile/{mobile}")
    Observable<Response<Map<String,String>>> obtainBindMobileAuthCode(@Path("account") String account,
                                                                      @Path("mobile") String mobile);

    @POST("mobile/bind")
    Observable<Response<MultiResult<String>>> bindMobile(@Body Map<String,String> body);

    @POST("mobile/bind/force")
    Observable<Response<Void>> forceBindMobile(@Body Map<String,String> body);

    @POST("login/account_pwd")
    Observable<Response<MultiResult<Object>>> accountPwdLogin(@Body Map<String,Object> body);

    @POST("ticket/refresh")
    Observable<Response<Map<String, Object>>> refreshTicket(@Body Map<String,Object> body);

    @GET("authCode/login/{mobile}")
    Observable<Response<Map<String,String>>> obtainLoginAuthCode(@Path("mobile") String mobile);

    @GET("authCode/resetPasswd/{mobile}")
    Observable<Response<Map<String,String>>> obtainResetAuthCode(@Path("mobile") String mobile);

    @POST("login/mobile_authcode")
    Observable<Response<MultiResult<Object>>> mobileLogin(@Body Map<String,Object> body);

    @GET("authCode/bind/{mobile}")
    Observable<Response<Void>> obtainBindAuthCode(@Path("mobile") String mobile);

    @GET("authCode/modify/{mobile}")
    Observable<Response<Void>> obtainModifyAuthCode(@Path("mobile") String mobile);

    @POST("mobile/modify/ticket")
    Observable<Response<Void>> obtainModifyMobile(@Body Map<String,String> body);

    @POST("mobile/bind/ticket")
    Observable<Response<Void>> ticketBindMobile(@Body Map<String,String> body);

    @POST("mobile/bind/force/ticket")
    Observable<Response<Void>> ticketForceBindMobile(@Body Map<String,String>  body);

    @PUT("account/customize")
    Observable<Response<Void>> ticketCustomAccount(@Body Map<String,String> body);

    @DELETE("mobile/unBind/{mobile}")
    Observable<Response<Void>> unBindMobile(@Path("mobile") String mobile);

    /*[S]add by tangsha for third encrypt*/
    @POST("notice/push")
    Observable<Response<Void>> sendThirdEnPushInfo(@Body Map<String,Object> body);
     /*[E]add by tangsha for third encrypt*/
}
