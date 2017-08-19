package com.xdja.imp.data.callback;

import com.xdja.imp.ImpDataApplicationTestCase;
import com.xdja.imp.data.entity.NoDisturbSetter;
import com.xdja.imp.data.net.RestAdapterMe;
import com.xdja.imp.data.net.UserSettingApi;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.NoDisturbConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.params</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/29</p>
 * <p>Time:9:29</p>
 */
public class UserSettingApiTest extends ImpDataApplicationTestCase{
    RestAdapterMe restAdapter;

    @Before
    public void setUp() throws Exception {
        /*restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://11.12.110.225:8080/mxs-api/api/")
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("ticket", "945871831151c313d6de100c4bb523f6");
                    }
                })
                .build();*/
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testSaveRoamSettings() throws Exception {

    }

    @Test
    public void testGetRoamSettings() throws Exception {

    }

    //PASSED
    @Test
    public void testSaveNoDisturbSettings() throws Exception {
        NoDisturbSetter setter = new NoDisturbSetter();
        setter.setAccount("100000017");
        setter.setSessionId("100000017");
        setter.setSessionType(ConstDef.NODISTURB_SETTING_SESSION_TYPE_SINGLE);


        UserSettingApi userSettingApi = restAdapter.getRetrofit().create(UserSettingApi.class);
        Observable<Object> objectObservable = userSettingApi.saveNoDisturbSettings(setter);
        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        objectObservable.subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
    }
    //PASSED
    @Test
    public void testDeleteNoDisturbSettings() throws Exception {
        NoDisturbSetter setter = new NoDisturbSetter();
        setter.setAccount("100000017");
        setter.setSessionId("100000017");
        setter.setSessionType(ConstDef.NODISTURB_SETTING_SESSION_TYPE_SINGLE);

        UserSettingApi userSettingApi = restAdapter.getRetrofit().create(UserSettingApi.class);
        Observable<Object> objectObservable = userSettingApi.deleteNoDisturbSettings(setter);
        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        objectObservable.subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
    }

    //PASSED
    @Test
    public void testGetNoDisturbSettings() throws Exception {
        UserSettingApi userSettingApi = restAdapter.getRetrofit().create(UserSettingApi.class);
        Observable<List<NoDisturbConfig>> objectObservable = userSettingApi.getNoDisturbSettings("100000017");
        TestSubscriber<List<NoDisturbConfig>> testSubscriber = new TestSubscriber<>();
        objectObservable.subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
    }
}