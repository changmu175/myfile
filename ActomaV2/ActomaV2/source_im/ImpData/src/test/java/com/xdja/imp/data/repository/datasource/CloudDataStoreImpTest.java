package com.xdja.imp.data.repository.datasource;

import com.xdja.imp.ImpDataApplicationTestCase;
import com.xdja.imp.data.entity.NoDisturbSetter;
import com.xdja.imp.data.entity.RoamSetter;
import com.xdja.imp.data.net.ApiFactoryMe;
import com.xdja.imp.data.net.UserSettingApi;
import com.xdja.imp.domain.model.RoamConfig;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * <p>Summary:数据网络存储单元测试用例</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.repository.datasource</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/14</p>
 * <p>Time:12:02</p>
 */
public class CloudDataStoreImpTest extends ImpDataApplicationTestCase {

    CloudDataStoreImp cloudDataStoreImp;

    @Mock private ApiFactoryMe apiFactory;

    @Mock private UserSettingApi userSettingApi;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        cloudDataStoreImp = new CloudDataStoreImp(apiFactory);
        when(apiFactory.getUserSettingApi()).thenReturn(userSettingApi);
    }
    //PASSED
    @Test
    public void testSaveRoamSetting2CloudNullObj() throws Exception {
        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        Observable<Boolean> booleanObservable = cloudDataStoreImp.saveRoamSetting2Cloud(null);
        booleanObservable.subscribe(testSubscriber);
        testSubscriber.assertError(Throwable.class);

        verify(apiFactory,never()).getUserSettingApi();
        verify(userSettingApi, never()).saveRoamSettings(any(RoamSetter.class));
    }
    //PASSED
    @Test
    public void testSaveRoamSetting2Cloud() throws Exception {
        Object object = new Object();
        when(userSettingApi.saveRoamSettings(any(RoamSetter.class)))
                .thenReturn(Observable.just(object));

        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        Observable<Boolean> booleanObservable = cloudDataStoreImp.saveRoamSetting2Cloud(new RoamSetter());
        booleanObservable.subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(Boolean.TRUE);

        verify(apiFactory).getUserSettingApi();
        verify(userSettingApi).saveRoamSettings(any(RoamSetter.class));
    }


    //PASSED
    @Test
    public void testGetRoamSettingAtCloud() throws Exception {
        when(userSettingApi.getRoamSettings(anyString(), anyString()))
                .thenReturn(Observable.just(new RoamConfig()));

        TestSubscriber<RoamConfig> testSubscriber = new TestSubscriber<>();
        Observable<RoamConfig> roamSetting4Cloud = cloudDataStoreImp.getRoamSettingAtCloud("testAccount", "testCardId");
        roamSetting4Cloud.subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);
        RoamConfig roamConfig = testSubscriber.getOnNextEvents().get(0);
        Assert.assertNotNull(roamConfig);

        verify(apiFactory).getUserSettingApi();
        verify(userSettingApi).getRoamSettings(anyString(), anyString());
    }

    //PASSED
    @Test
     public void testAddNoDisturb2Cloud(){
        Object object = new Object();
        when(userSettingApi.saveNoDisturbSettings(any(NoDisturbSetter.class)))
                .thenReturn(Observable.just(object));

        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        Observable<Boolean> noDisturb2Cloud
                = cloudDataStoreImp.addNoDisturb2Cloud(new NoDisturbSetter());
        noDisturb2Cloud.subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(Boolean.TRUE);

        verify(apiFactory).getUserSettingApi();
        verify(userSettingApi).saveNoDisturbSettings(any(NoDisturbSetter.class));

    }

    //PASSED
    @Test
    public void testDeleteNoDisturb4Cloud(){
        Object object = new Object();
        when(userSettingApi.deleteNoDisturbSettings(any(NoDisturbSetter.class)))
                .thenReturn(Observable.just(object));

        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        Observable<Boolean> noDisturb2Cloud = cloudDataStoreImp
                .deleteNoDisturbAtCloud(new NoDisturbSetter());
        noDisturb2Cloud.subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(Boolean.TRUE);

        verify(apiFactory).getUserSettingApi();
        verify(userSettingApi).deleteNoDisturbSettings(any(NoDisturbSetter.class));
    }

    @Test
    public void testGetNoDisturbSettings4Cloud(){
//        when(userSettingApi.getNoDisturbSettings(anyString()))
//                .thenReturn(Observable.<List<SessionConfig>>empty());
//
//        cloudDataStoreImp.getNoDisturbSettingsAtCloud("testAccount");
//        verify(userSettingApi).getNoDisturbSettings(anyString());
    }
}