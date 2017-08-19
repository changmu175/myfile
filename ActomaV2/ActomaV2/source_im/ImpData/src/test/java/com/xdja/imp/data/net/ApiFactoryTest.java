package com.xdja.imp.data.net;

import com.xdja.imp.ImpDataApplicationTestCase;
import com.xdja.imp.data.cache.ConfigCache;
import com.xdja.imp.data.cache.UserCache;
import com.xdja.imp.data.entity.NoDisturbSetter;
import com.xdja.imp.data.entity.RoamSetter;
import com.xdja.imp.data.entity.SessionTopSetter;
import com.xdja.imp.domain.model.NoDisturbConfig;
import com.xdja.imp.domain.model.RoamConfig;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.Path;
import rx.Observable;

/**
 * <p>Summary:Rest服务适配器单元测试用例</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.params</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/14</p>
 * <p>Time:10:48</p>
 */
public class ApiFactoryTest extends ImpDataApplicationTestCase {

    ApiFactoryMe apiFactory;

    @Mock private Retrofit retrofit;

    @Mock private ConfigCache configCache;

    @Mock private UserCache userCache;

    @Mock private UserSettingApi userSettingApi;

    @Mock private RestAdapterMe restAdapter;


    private final String DEFAULT_MXENDPOint = "http://11.12.112.249:8080/mxs-api/api/";

    private final String DEFAULT_TICKET = "testTicket";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        apiFactory = new ApiFactoryMe(restAdapter,configCache,userCache);
    }

    @Test
    public void testGetUserSettingApiWithNullObj() throws Exception {
        /*when(configCache.get()).thenReturn(configEntity);
        when(configEntity.getMxsEndpoint()).thenReturn(DEFAULT_MXENDPOint);

        when(userCache.get()).thenReturn(userEntity);
        when(userEntity.getTicket()).thenReturn(DEFAULT_TICKET);

        when(restsAdapterBuilder.setEndpoint(anyString())).thenReturn(restsAdapterBuilder);
        when(restsAdapterBuilder.setRequestInterceptor(any(RequestInterceptor.class))).thenReturn(restsAdapterBuilder);
        when(restsAdapterBuilder.build()).thenReturn(restAdapter);*/

        apiFactory.getUserSettingApi();

        //verify(restAdapter).create(UserSettingApi.class);
    }

    @Test
    public void testGetUserSettingApiWithNotNullObj() throws Exception {

        Field field = apiFactory.getClass().getDeclaredField("userSettingApi");
        field.setAccessible(true);
        field.set(apiFactory, new UserSettingApi() {
            @Override
            public Observable<Object> saveRoamSettings(@Body RoamSetter setter) {
                return null;
            }

            @Override
            public Observable<RoamConfig> getRoamSettings(@Path("account") String account, @Path("cardId") String cardId) {
                return null;
            }

            @Override
            public Observable<Object> saveNoDisturbSettings(@Body NoDisturbSetter setter) {
                return null;
            }

            @Override
            public Observable<Object> deleteNoDisturbSettings(@Body NoDisturbSetter setter) {
                return null;
            }

            @Override
            public Observable<List<NoDisturbConfig>> getNoDisturbSettings(@Path("account") String account) {
                return null;
            }

            @Override
            public Observable<List<String>> getSettingTopSettings(@Path("account") String account) {
                return null;
            }

            @Override
            public Observable<Object> saveSessionTopSettings(@Body SessionTopSetter setter) {
                return null;
            }

            @Override
            public Observable<Object> deleteSessionTopSettings(@Body SessionTopSetter setter) {
                return null;
            }
        });

        /*when(configCache.get()).thenReturn(configEntity);
        when(configEntity.getMxsEndpoint()).thenReturn(DEFAULT_MXENDPOint);

        when(userCache.get()).thenReturn(userEntity);
        when(userEntity.getTicket()).thenReturn(DEFAULT_TICKET);

        when(restsAdapterBuilder.setEndpoint(anyString())).thenReturn(restsAdapterBuilder);
        when(restsAdapterBuilder.setRequestInterceptor(any(RequestInterceptor.class))).thenReturn(restsAdapterBuilder);
        when(restsAdapterBuilder.build()).thenReturn(restAdapter);*/

        apiFactory.getUserSettingApi();

        //verify(restAdapter,never()).create(UserSettingApi.class);
    }
}