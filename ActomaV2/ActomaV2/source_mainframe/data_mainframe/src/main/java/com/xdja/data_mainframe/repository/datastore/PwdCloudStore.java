package com.xdja.data_mainframe.repository.datastore;

import android.support.annotation.NonNull;

import com.xdja.data_mainframe.rest.ApiFactory;
import com.xdja.dependence.annotations.ConnSecuritySpe;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.frame.data.net.ServiceGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import retrofit2.Response;
import rx.Observable;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.data_mainframe.repository.datastore</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/21</p>
 * <p>Time:20:19</p>
 */
public class PwdCloudStore extends CloudStore implements PwdStore{

    private Map<String, Provider<String>> stringMap;
    private Map<String, Provider<Integer>> integerMap;

    @Inject
    public PwdCloudStore(@ConnSecuritySpe(DiConfig.CONN_HTTPS_DEF) ServiceGenerator serviceGenerator,
                         Map<String, Provider<String>> stringMap,
                         Map<String, Provider<Integer>> integerMap) {
        super(serviceGenerator);
        this.stringMap = stringMap;
        this.integerMap = integerMap;
    }

    @Override
    public Observable<Response<Map<String, String>>> checkRestPwdAuthCode(@NonNull String mobile,
                                                                          @NonNull String authCode,
                                                                          @NonNull String innerAuthCode) {
        Map<String,String> body = new HashMap<>();
        body.put("mobile",mobile);
        body.put("authCode",authCode);
        body.put("innerAuthCode",innerAuthCode);
        return ApiFactory.getPwdRestApi(this.serviceGenerator).checkRestPwdAuthCode(body);
    }

    @Override
    public Observable<Response<Void>> restPwdByAuthCode(@NonNull String mobile,
                                                  @NonNull String innerAuthCode,
                                                  @NonNull String passwd) {
        Map<String,String> body = new HashMap<>();
        body.put("mobile",mobile);
        body.put("innerAuthCode",innerAuthCode);
        body.put("passwd",passwd);
        return ApiFactory.getPwdRestApi(this.serviceGenerator).restPwdByAuthCode(body);
    }

    @Override
    public Observable<Response<Void>> restPwdByFriendMobiles(@NonNull String account,
                                                               @NonNull String innerAuthCode,
                                                               @NonNull String passwd ) {
        Map<String,String> body = new HashMap<>();
        body.put("account",account);
        body.put("innerAuthCode",innerAuthCode);
        body.put("passwd",passwd);
        return ApiFactory.getPwdRestApi(this.serviceGenerator).restPwdByFriendMobiles(body);
    }

    @Override
    public Observable<Response<MultiResult<Object>>> authFriendPhone(@NonNull String account, @NonNull List<String> mobiles) {
        Map<String,Object> body = new HashMap<>();
        body.put("account",account);
        body.put("mobiles",mobiles);
        return ApiFactory.getPwdRestApi(this.serviceGenerator).authFriendPhone(body);
    }
}
