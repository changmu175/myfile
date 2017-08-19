package com.xdja.imp.data.net;

import com.xdja.imp.data.entity.NoDisturbSetter;
import com.xdja.imp.data.entity.RoamSetter;
import com.xdja.imp.data.entity.SessionTopSetter;
import com.xdja.imp.domain.model.NoDisturbConfig;
import com.xdja.imp.domain.model.RoamConfig;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

/**
 * <p>Summary:用户设置相关REST API</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.params</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/13</p>
 * <p>Time:14:20</p>
 */
public interface UserSettingApi {

    /**
     * 保存漫游设置
     * @param setter 漫游设置
     * @return  事件源
     */
    @PUT("account/roam/update")
    Observable<Object> saveRoamSettings(@Body RoamSetter setter);

    @GET("account/roam/{account}/{cardId}")
    Observable<RoamConfig> getRoamSettings(@Path("account") String account,
                                           @Path("cardId") String cardId);
    @PUT("account/nodisturb/update")
    Observable<Object> saveNoDisturbSettings(@Body NoDisturbSetter setter);

    @PUT("account/nodisturb/delete")
    Observable<Object> deleteNoDisturbSettings(@Body NoDisturbSetter setter);

    @GET("account/nodisturb/{account}")
    Observable<List<NoDisturbConfig>> getNoDisturbSettings(@Path("account") String account);

    @GET("account/top/{account}")
    Observable<List<String>> getSettingTopSettings(@Path("account") String account);

    @PUT("account/top/update")
    Observable<Object> saveSessionTopSettings(@Body SessionTopSetter setter);

    @PUT("account/top/delete")
    Observable<Object> deleteSessionTopSettings(@Body SessionTopSetter setter);
}
