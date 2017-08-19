package com.xdja.data_mainframe.rest;

import com.xdja.comm.encrypt.NewStrategyResponseBean;
import com.xdja.domain_mainframe.model.MultiResult;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by ldy on 16/4/20.
 */
public interface UserInfoRestApi {
    @PUT("nickName/modify")
    Observable<Response<Map<String, String>>> modifyNickName(@Body Map<String, String> body);

    @PUT("avatar/modify")
    Observable<Response<Void>> modifyAvatar(@Body Map<String, String> body);

    @PUT("passwd/modify")
    Observable<Response<Void>> modifyPasswd(@Body Map<String, String> body);

    @POST("account/auth/passwd")
    Observable<Response<Void>> authPasswd(@Body Map<String, String> body);

    @GET("devices")
    Observable<Response<List<Map<String, String>>>> queryDevices();

    @PUT("device/name")
    Observable<Response<Void>> modifyDeviceName(@Body Map<String, String> body);

    @DELETE("device/relieve/{cardNo}")
    Observable<Response<Void>> relieveDevice(@Path("cardNo") String cardNo);

    @DELETE("logout")
    Observable<Response<Void>> logout();

    @GET("account/{accountOrMobile}")
    Observable<Response<MultiResult<String>>> queryAccountInfo(@Path("accountOrMobile") String accountOrMobile);

    @GET("accounts/increment")
    Observable<Response<MultiResult<String>>> queryIncrementAccounts(@Body Map<String, String> body);

    @POST("account/batch")
    Observable<Response<MultiResult<String>>> queryBatchAccount(@Body List<String> body);

    /*
    @POST("strategys")
    Observable<Response<NewStrategyResponseBean>> updateStrategys(@Body Map<String, String> body);
    */

    @POST("strategybymodel")
    Observable<Response<NewStrategyResponseBean>> queryStrategyByMobile(@Body Map<String, String> body);

    @GET("serverConfigs")
    Observable<Response<Map<String, String>>> queryServerConfigs();

    @GET("notice/online")
    Observable<Response<Map<String, String>>> queryOnlineNotice();

    @GET("notice/{account}/logout/force/{clientType}")
    Observable<Response<Map<String, String>>> queryForceLogoutNotice(@Path("account") String account,
                                                                     @Path("clientType") String clientType);

    @GET("notice/{account}/unBindDevice")
    Observable<Response<Map<String, String>>> queryUnBindDeviceNotice(@Path("account") String account);

    @Multipart
    @POST("upload")
    Observable<Response<Map<String, String>>> uploadFile(
            @Part("file; filename=\"111.jpg") RequestBody file
    );

}
