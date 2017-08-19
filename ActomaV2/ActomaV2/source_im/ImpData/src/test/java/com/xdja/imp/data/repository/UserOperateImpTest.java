package com.xdja.imp.data.repository;

import com.google.gson.Gson;
import com.xdja.imp.ImpDataApplicationTestCase;
import com.xdja.imp.data.cache.CardCache;
import com.xdja.imp.data.cache.CardEntity;
import com.xdja.imp.data.cache.UserCache;
import com.xdja.imp.data.cache.UserEntity;
import com.xdja.imp.data.entity.NoDisturbSetter;
import com.xdja.imp.data.entity.RoamSetter;
import com.xdja.imp.data.entity.SessionParam;
import com.xdja.imp.data.entity.mapper.DataMapper;
import com.xdja.imp.data.repository.datasource.CloudDataStore;
import com.xdja.imp.data.repository.datasource.DiskDataStore;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.KeyValuePair;
import com.xdja.imp.domain.model.RoamConfig;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * <p>Summary:用户操作功能性模块单元测试用例</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.repository</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/13</p>
 * <p>Time:18:42</p>
 */
public class UserOperateImpTest extends ImpDataApplicationTestCase {

    private UserOperateImp userOperateImp;

    @Mock
    private CloudDataStore cloudDataStore;

    @Mock
    private DiskDataStore diskDataStore;

    @Mock
    private UserCache userCache;

    @Mock
    private CardCache cardCache;

    @Mock
    private UserEntity userEntity;

    @Mock
    private CardEntity cardEntity;

    @Mock
    private DataMapper dataMapper;

    //    @Spy
//    private Gson gson = new Gson();
    private Gson gson = new Gson();

    private final String DEFAULT_CARDID = "45545664abcd6566f";

    private final String DEFAULT_ACCOUNT = "testAccount";

    private final
    @ConstDef.RoamState
    int ROAM_STATE = ConstDef.ROAM_STATE_OPEN;

    private final int ROAM_TIME = 3;

    private final String DEFAULT_TALKER = "123456";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        userOperateImp = new UserOperateImp(
                cloudDataStore,
                diskDataStore,
                userCache,
                cardCache,
                dataMapper,
                gson);
    }

    @After
    public void tearDown() throws Exception {
        gson = null;
        userOperateImp = null;
    }

    //PASSED
    @Test
    public void testSaveSessionTopSetting2Local(){

        final String mock_talkId = "12345";

        SessionParam sessionParam = new SessionParam();
        sessionParam.setIsTop(SessionParam.ISTOP_TRUE);
        sessionParam.setFlag(mock_talkId);
        sessionParam.setIsNoDisturb(SessionParam.ISDISTURB_TRUE);
        sessionParam.setDraft("draft");

        when(diskDataStore.getLocalSingleSession(anyString()))
                            .thenReturn(Observable.just(sessionParam));

        when(diskDataStore.saveOrUpdateLocalSingleSession(any(SessionParam.class)))
                .thenReturn(Observable.just(Boolean.TRUE));

        Observable<Boolean> booleanObservable
                = userOperateImp.saveSessionTopSetting2Local(mock_talkId, false);
        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        booleanObservable.subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(Boolean.TRUE);

        int isTop = sessionParam.getIsTop();
        Assert.assertEquals(SessionParam.ISTOP_FALE,isTop);

        verify(diskDataStore).getLocalSingleSession(anyString());

        verify(diskDataStore).saveOrUpdateLocalSingleSession(any(SessionParam.class));
    }

    //PASSED
    @Test
    public void testSaveSessionTopSetting2LocalWithNoneDBQuery(){

        final String mock_talkId = "12345";

        SessionParam sessionParam = null;

        when(diskDataStore.getLocalSingleSession(anyString()))
                .thenReturn(Observable.just(sessionParam));

        when(diskDataStore.saveOrUpdateLocalSingleSession(any(SessionParam.class)))
                .thenReturn(Observable.just(Boolean.TRUE));

        Observable<Boolean> booleanObservable
                = userOperateImp.saveSessionTopSetting2Local(mock_talkId, false);
        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        booleanObservable.subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(Boolean.TRUE);

        verify(diskDataStore).getLocalSingleSession(anyString());

        verify(diskDataStore).saveOrUpdateLocalSingleSession(any(SessionParam.class));
    }

    //PASSED
    @Test
    public void testAddNoDisturb2CloudWithEmptyAccount() {
        when(userCache.get()).thenReturn(userEntity);
        when(userEntity.getAccount()).thenReturn("");

        when(cardCache.get()).thenReturn(cardEntity);
        when(cardEntity.getCardId()).thenReturn(DEFAULT_CARDID);

        Observable<Boolean> booleanObservable =
                userOperateImp.addNoDisturb2Cloud(DEFAULT_TALKER,
                        ConstDef.NODISTURB_SETTING_SESSION_TYPE_SINGLE);
        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        booleanObservable.subscribe(testSubscriber);
        testSubscriber.assertError(Throwable.class);

        verify(cloudDataStore, never()).addNoDisturb2Cloud(any(NoDisturbSetter.class));
    }

    //PASSED
    @Test
    public void testAddNoDisturb2CloudWithEmptyCardId() {
        when(userCache.get()).thenReturn(userEntity);
        when(userEntity.getAccount()).thenReturn(DEFAULT_ACCOUNT);

        when(cardCache.get()).thenReturn(cardEntity);
        when(cardEntity.getCardId()).thenReturn("");

        Observable<Boolean> booleanObservable =
                userOperateImp.addNoDisturb2Cloud(DEFAULT_TALKER,
                        ConstDef.NODISTURB_SETTING_SESSION_TYPE_SINGLE);
        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        booleanObservable.subscribe(testSubscriber);
        testSubscriber.assertError(Throwable.class);

        verify(cloudDataStore, never()).addNoDisturb2Cloud(any(NoDisturbSetter.class));
    }

    //PASSED
    @Test
    public void testAddNoDisturb2Cloud() {
        when(userCache.get()).thenReturn(userEntity);
        when(userEntity.getAccount()).thenReturn(DEFAULT_ACCOUNT);

        when(cardCache.get()).thenReturn(cardEntity);
        when(cardEntity.getCardId()).thenReturn(DEFAULT_CARDID);

        when(cloudDataStore.addNoDisturb2Cloud(any(NoDisturbSetter.class)))
                .thenReturn(Observable.just(Boolean.TRUE));

        Observable<Boolean> booleanObservable =
                userOperateImp.addNoDisturb2Cloud(DEFAULT_TALKER,
                        ConstDef.NODISTURB_SETTING_SESSION_TYPE_SINGLE);
        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        booleanObservable.subscribe(testSubscriber);
        testSubscriber.assertNoErrors();

        verify(cloudDataStore).addNoDisturb2Cloud(any(NoDisturbSetter.class));
    }

    //PASSED
    @Test
    public void testAddNoDisturb2LocalWithEmptyAccount() {
        when(userCache.get()).thenReturn(userEntity);
        when(userEntity.getAccount()).thenReturn("");

        when(cardCache.get()).thenReturn(cardEntity);
        when(cardEntity.getCardId()).thenReturn(DEFAULT_CARDID);

        Observable<Boolean> booleanObservable =
                userOperateImp.addNoDisturb2Local(DEFAULT_TALKER);
        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        booleanObservable.subscribe(testSubscriber);
        testSubscriber.assertError(Throwable.class);

        verify(diskDataStore, never()).getLocalSingleSession(anyString());
    }

    //PASSED
    @Test
    public void testAddNoDisturb2LocalWithEmptyCardId() {
        when(userCache.get()).thenReturn(userEntity);
        when(userEntity.getAccount()).thenReturn(DEFAULT_ACCOUNT);

        when(cardCache.get()).thenReturn(cardEntity);
        when(cardEntity.getCardId()).thenReturn("");

        Observable<Boolean> booleanObservable =
                userOperateImp.addNoDisturb2Local(DEFAULT_TALKER);
        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        booleanObservable.subscribe(testSubscriber);
        testSubscriber.assertError(Throwable.class);

        verify(diskDataStore, never()).getLocalSingleSession(anyString());
    }

    //PASSED
    @Test
    public void testAddNoDisturb2Local() {

        SessionParam sessionParam = new SessionParam();

        when(userCache.get()).thenReturn(userEntity);
        when(userEntity.getAccount()).thenReturn(DEFAULT_ACCOUNT);

        when(cardCache.get()).thenReturn(cardEntity);
        when(cardEntity.getCardId()).thenReturn(DEFAULT_CARDID);

        when(diskDataStore.getLocalSingleSession(anyString()))
                .thenReturn(Observable.just(sessionParam));

        when(diskDataStore.saveOrUpdateLocalSingleSession(any(SessionParam.class)))
                .thenReturn(Observable.just(Boolean.TRUE));

        Observable<Boolean> booleanObservable =
                userOperateImp.addNoDisturb2Local(DEFAULT_TALKER);
        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        booleanObservable.subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(Boolean.TRUE);

        verify(diskDataStore).getLocalSingleSession(anyString());
        verify(diskDataStore).saveOrUpdateLocalSingleSession(any(SessionParam.class));
    }

    //PASSED
    @Test
    public void testDeleteNoDisturbAtCloudWithEmptyAccount() {
        when(userCache.get()).thenReturn(userEntity);
        when(userEntity.getAccount()).thenReturn("");

        when(cardCache.get()).thenReturn(cardEntity);
        when(cardEntity.getCardId()).thenReturn(DEFAULT_CARDID);

        Observable<Boolean> booleanObservable =
                userOperateImp.deleteNoDisturbAtCloud(DEFAULT_TALKER,
                        ConstDef.NODISTURB_SETTING_SESSION_TYPE_SINGLE);
        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        booleanObservable.subscribe(testSubscriber);
        testSubscriber.assertError(Throwable.class);

        verify(cloudDataStore, never()).deleteNoDisturbAtCloud(any(NoDisturbSetter.class));
    }

    //PASSED
    @Test
    public void testDeleteNoDisturbAtCloudWithEmptyCardId() {
        when(userCache.get()).thenReturn(userEntity);
        when(userEntity.getAccount()).thenReturn(DEFAULT_ACCOUNT);

        when(cardCache.get()).thenReturn(cardEntity);
        when(cardEntity.getCardId()).thenReturn("");

        Observable<Boolean> booleanObservable =
                userOperateImp.deleteNoDisturbAtCloud(DEFAULT_TALKER,
                        ConstDef.NODISTURB_SETTING_SESSION_TYPE_SINGLE);
        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        booleanObservable.subscribe(testSubscriber);
        testSubscriber.assertError(Throwable.class);

        verify(cloudDataStore, never()).deleteNoDisturbAtCloud(any(NoDisturbSetter.class));
    }

    //PASSED
    @Test
    public void testDeleteNoDisturbAtCloud() {
        when(userCache.get()).thenReturn(userEntity);
        when(userEntity.getAccount()).thenReturn(DEFAULT_ACCOUNT);

        when(cardCache.get()).thenReturn(cardEntity);
        when(cardEntity.getCardId()).thenReturn(DEFAULT_CARDID);

        when(cloudDataStore.deleteNoDisturbAtCloud(any(NoDisturbSetter.class)))
                .thenReturn(Observable.just(Boolean.TRUE));

        Observable<Boolean> booleanObservable =
                userOperateImp.deleteNoDisturbAtCloud(DEFAULT_TALKER,
                        ConstDef.NODISTURB_SETTING_SESSION_TYPE_SINGLE);
        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        booleanObservable.subscribe(testSubscriber);
        testSubscriber.assertNoErrors();

        verify(cloudDataStore).deleteNoDisturbAtCloud(any(NoDisturbSetter.class));
    }

    //PASSED
    @Test
    public void testDeleteNoDisturbAtLocalWithEmptyAccount() {
        when(userCache.get()).thenReturn(userEntity);
        when(userEntity.getAccount()).thenReturn("");

        when(cardCache.get()).thenReturn(cardEntity);
        when(cardEntity.getCardId()).thenReturn(DEFAULT_CARDID);

        Observable<Boolean> booleanObservable =
                userOperateImp.deleteNoDisturbAtLocal(DEFAULT_TALKER);
        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        booleanObservable.subscribe(testSubscriber);
        testSubscriber.assertError(Throwable.class);

        verify(diskDataStore, never()).getLocalSingleSession(anyString());
    }

    //PASSED
    @Test
    public void testDeleteNoDisturbAtLocalWithEmptyCardId() {
        when(userCache.get()).thenReturn(userEntity);
        when(userEntity.getAccount()).thenReturn(DEFAULT_ACCOUNT);

        when(cardCache.get()).thenReturn(cardEntity);
        when(cardEntity.getCardId()).thenReturn("");

        Observable<Boolean> booleanObservable =
                userOperateImp.deleteNoDisturbAtLocal(DEFAULT_TALKER);
        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        booleanObservable.subscribe(testSubscriber);
        testSubscriber.assertError(Throwable.class);

        verify(diskDataStore, never()).getLocalSingleSession(anyString());
    }

    //PASSED
    @Test
    public void testDeleteNoDisturbAtLocal() {
        SessionParam param = new SessionParam();

        when(userCache.get()).thenReturn(userEntity);
        when(userEntity.getAccount()).thenReturn(DEFAULT_ACCOUNT);

        when(cardCache.get()).thenReturn(cardEntity);
        when(cardEntity.getCardId()).thenReturn(DEFAULT_CARDID);

        when(diskDataStore.getLocalSingleSession(anyString()))
                .thenReturn(Observable.just(param));

        when(diskDataStore.saveOrUpdateLocalSingleSession(any(SessionParam.class)))
                .thenReturn(Observable.just(Boolean.TRUE));

        Observable<Boolean> booleanObservable =
                userOperateImp.deleteNoDisturbAtLocal(DEFAULT_TALKER);
        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        booleanObservable.subscribe(testSubscriber);
        testSubscriber.assertNoErrors();

        verify(diskDataStore).getLocalSingleSession(anyString());
        verify(diskDataStore).saveOrUpdateLocalSingleSession(any(SessionParam.class));
    }


    //PASSED
    @Test
    public void testSaveDraft2Local(){

        SessionParam param = new SessionParam();

        when(diskDataStore.getLocalSingleSession(anyString()))
                .thenReturn(Observable.just(param));

        when(diskDataStore.saveOrUpdateLocalSingleSession(any(SessionParam.class)))
                .thenReturn(Observable.just(Boolean.TRUE));

        Observable<Boolean> booleanObservable = userOperateImp.saveDraft2Local("123", null,0);

        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        booleanObservable.subscribe(testSubscriber);
        testSubscriber.assertNoErrors();

        verify(diskDataStore).getLocalSingleSession(anyString());
        verify(diskDataStore).saveOrUpdateLocalSingleSession(any(SessionParam.class));

    }

    //PASSED
    @Test
    public void testQueryRoamSettingAtLocal() throws Exception {
        RoamConfig roamConfig = new RoamConfig();
        roamConfig.setStatus(ROAM_STATE);
        roamConfig.setTime(ROAM_TIME);

        String gsonStr = gson.toJson(roamConfig);


        TestSubscriber<RoamConfig> testSubscriber = new TestSubscriber<>();
        when(diskDataStore.queryStringSharePref(anyString())).thenReturn(Observable.just(gsonStr));
        Observable<RoamConfig> roamConfigObservable = userOperateImp.queryRoamSettingAtLocal();
        roamConfigObservable.subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);
        RoamConfig roamConfig1 = testSubscriber.getOnNextEvents().get(0);
        Assert.assertEquals(roamConfig.toString(), roamConfig1.toString());

        verify(diskDataStore).queryStringSharePref(anyString());
    }

    //PASSED
    @Test
    public void testQueryRoamSettingAtLocalWithErrorJson() throws Exception {
        RoamConfig roamConfig = new RoamConfig();
        roamConfig.setStatus(ROAM_STATE);
        roamConfig.setTime(ROAM_TIME);

        String gsonStr = gson.toJson(roamConfig) + "111";


        TestSubscriber<RoamConfig> testSubscriber = new TestSubscriber<>();
        when(diskDataStore.queryStringSharePref(anyString())).thenReturn(Observable.just(gsonStr));
        Observable<RoamConfig> roamConfigObservable = userOperateImp.queryRoamSettingAtLocal();
        roamConfigObservable.subscribe(testSubscriber);

        testSubscriber.assertError(Throwable.class);
        List<Throwable> onErrorEvents = testSubscriber.getOnErrorEvents();
        Throwable throwable = onErrorEvents.get(0);
        Assert.assertNotNull(throwable);

        verify(diskDataStore).queryStringSharePref(anyString());
    }

    //PASSED
    @Test
    public void testQueryRoamSettingAtLocalWithNoneQuery() throws Exception {
        RoamConfig roamConfig = new RoamConfig();
        roamConfig.setStatus(ROAM_STATE);
        roamConfig.setTime(ROAM_TIME);

        String gsonStr = null;


        TestSubscriber<RoamConfig> testSubscriber = new TestSubscriber<>();
        when(diskDataStore.queryStringSharePref(anyString())).thenReturn(Observable.just(gsonStr));
        Observable<RoamConfig> roamConfigObservable = userOperateImp.queryRoamSettingAtLocal();
        roamConfigObservable.subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(null);

        verify(diskDataStore).queryStringSharePref(anyString());
    }

    //PASSED
    @Test
    public void testGetRoamSetttingAtCloudWithEmptyAccount() throws Exception {
        when(userCache.get()).thenReturn(userEntity);
        when(userEntity.getAccount()).thenReturn("");

        when(cardCache.get()).thenReturn(cardEntity);
        when(cardEntity.getCardId()).thenReturn(DEFAULT_CARDID);

        TestSubscriber<RoamConfig> testSubscriber = new TestSubscriber<>();
        Observable<RoamConfig> roamConfigObservable = userOperateImp.getRoamSetttingAtCloud();
        roamConfigObservable.subscribe(testSubscriber);

        verify(cloudDataStore, never()).getRoamSettingAtCloud(anyString(), anyString());
        testSubscriber.assertError(Throwable.class);
    }

    //PASSED
    @Test
    public void testGetRoamSetttingAtloudWithEmptyCardId() throws Exception {
        when(userCache.get()).thenReturn(userEntity);
        when(userEntity.getAccount()).thenReturn(DEFAULT_ACCOUNT);

        when(cardCache.get()).thenReturn(cardEntity);
        when(cardEntity.getCardId()).thenReturn("");

        TestSubscriber<RoamConfig> testSubscriber = new TestSubscriber<>();
        Observable<RoamConfig> roamConfigObservable = userOperateImp.getRoamSetttingAtCloud();
        roamConfigObservable.subscribe(testSubscriber);

        verify(cloudDataStore, never()).getRoamSettingAtCloud(anyString(), anyString());
        testSubscriber.assertError(Throwable.class);
    }

    //PASSED
    @Test
    public void testGetRoamSetttingAtCloud() throws Exception {
        when(userCache.get()).thenReturn(userEntity);
        when(userEntity.getAccount()).thenReturn(DEFAULT_ACCOUNT);

        when(cardCache.get()).thenReturn(cardEntity);
        when(cardEntity.getCardId()).thenReturn(DEFAULT_CARDID);

        RoamConfig roamConfig = new RoamConfig();
        roamConfig.setStatus(ROAM_STATE);
        roamConfig.setTime(ROAM_TIME);

        when(cloudDataStore.getRoamSettingAtCloud(anyString(), anyString()))
                .thenReturn(Observable.just(roamConfig));

        TestSubscriber<RoamConfig> testSubscriber = new TestSubscriber<>();
        Observable<RoamConfig> roamConfigObservable = userOperateImp.getRoamSetttingAtCloud();
        roamConfigObservable.subscribe(testSubscriber);

        verify(cloudDataStore).getRoamSettingAtCloud(anyString(), anyString());

        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertValue(roamConfig);
    }

    //PASSED
    @Test
    public void testSaveRoamSetting2Local() throws Exception {
        when(diskDataStore.saveKeyValuePairs(any(KeyValuePair.class)))
                .thenReturn(Observable.just(Boolean.TRUE));

        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        Observable<Boolean> booleanObservable =
                userOperateImp.saveRoamSetting2Local(ROAM_STATE, ROAM_TIME);
        booleanObservable.subscribe(testSubscriber);

        verify(diskDataStore).saveKeyValuePairs(any(KeyValuePair.class));

        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(Boolean.TRUE);
    }
    //PASSED
    @Test
    public void testSaveRoamSetting2CloudWithEmptyAccount() throws Exception {
        when(userCache.get()).thenReturn(userEntity);
        when(userEntity.getAccount()).thenReturn("");

        when(cardCache.get()).thenReturn(cardEntity);
        when(cardEntity.getCardId()).thenReturn(DEFAULT_CARDID);

        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        Observable<Boolean> booleanObservable =
                userOperateImp.saveRoamSetting2Cloud(ConstDef.ROAM_STATE_OPEN, 3);
        booleanObservable.subscribe(testSubscriber);

        verify(cloudDataStore, never()).saveRoamSetting2Cloud(any(RoamSetter.class));
        testSubscriber.assertError(Throwable.class);
    }
    //PASSED
    @Test
    public void testSaveRoamSetting2CloudWithEmptyCardId() throws Exception {
        when(userCache.get()).thenReturn(userEntity);
        when(userEntity.getAccount()).thenReturn(DEFAULT_ACCOUNT);

        when(cardCache.get()).thenReturn(cardEntity);
        when(cardEntity.getCardId()).thenReturn("");

        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        Observable<Boolean> booleanObservable =
                userOperateImp.saveRoamSetting2Cloud(ConstDef.ROAM_STATE_OPEN, 3);
        booleanObservable.subscribe(testSubscriber);

        verify(cloudDataStore, never()).saveRoamSetting2Cloud(any(RoamSetter.class));
        testSubscriber.assertError(Throwable.class);
    }
    //PASSED
    @Test
    public void testSyncRoamSetting2Cloud() throws Exception {

        when(userCache.get()).thenReturn(userEntity);
        when(userEntity.getAccount()).thenReturn(DEFAULT_ACCOUNT);

        when(cardCache.get()).thenReturn(cardEntity);
        when(cardEntity.getCardId()).thenReturn(DEFAULT_CARDID);

        when(cloudDataStore.saveRoamSetting2Cloud(any(RoamSetter.class)))
                .thenReturn(Observable.just(Boolean.TRUE));

        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        Observable<Boolean> booleanObservable =
                userOperateImp.saveRoamSetting2Cloud(ConstDef.ROAM_STATE_OPEN, 3);
        booleanObservable.subscribe(testSubscriber);

        verify(cloudDataStore).saveRoamSetting2Cloud(any(RoamSetter.class));

        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(Boolean.TRUE);
    }






    @Test
    public void testQueryNoDisturbSetting4CloudWithEmptyAccount() {
//        when(userCache.get()).thenReturn(userEntity);
//        when(userEntity.getAccount()).thenReturn("");
//
//        TestSubscriber<List<SessionConfig>> testSubscriber = new TestSubscriber<>();
//        Observable<List<SessionConfig>> listObservable = userOperateImp.queryNoDisturbSetting4Cloud();
//        listObservable.subscribe(testSubscriber);
//        testSubscriber.assertError(Throwable.class);
//        verify(cloudDataStore, never()).getNoDisturbSettingsAtCloud(anyString());
    }

    @Test
    public void testQueryNoDisturbSetting4Cloud() {
//        when(userCache.get()).thenReturn(userEntity);
//        when(userEntity.getAccount()).thenReturn(DEFAULT_ACCOUNT);
//
//        userOperateImp.queryNoDisturbSetting4Cloud();
//        verify(cloudDataStore).getNoDisturbSettingsAtCloud(anyString());
    }

    @Test
    public void testQueryNoDisturbSetting4LocalWithEmptyAccount() {
//        when(userCache.get()).thenReturn(userEntity);
//        when(userEntity.getAccount()).thenReturn("");
//
//        TestSubscriber<List<SessionConfig>> testSubscriber = new TestSubscriber<>();
//        Observable<List<SessionConfig>> listObservable = userOperateImp.querySessionSettingsAtLocal();
//        listObservable.subscribe(testSubscriber);
//        testSubscriber.assertError(Throwable.class);
//        verify(diskDataStore, never()).getNoDisturbSettings4Local(anyString());
    }

    @Test
    public void testQueryNoDisturbSetting4Local() {
//        when(userCache.get()).thenReturn(userEntity);
//        when(userEntity.getAccount()).thenReturn(DEFAULT_ACCOUNT);
//
//        userOperateImp.querySessionSettingsAtLocal();
//        verify(diskDataStore).getNoDisturbSettings4Local(anyString());
    }
}