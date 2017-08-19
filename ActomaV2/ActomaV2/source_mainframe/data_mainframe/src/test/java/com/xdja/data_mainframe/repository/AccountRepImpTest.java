package com.xdja.data_mainframe.repository;

import android.content.Context;

import com.google.gson.Gson;
import com.xdja.data_mainframe.DataApplicationTestCase;
import com.xdja.data_mainframe.di.CacheModule;
import com.xdja.data_mainframe.entities.AccountEntityDataMapper;
import com.xdja.data_mainframe.repository.datastore.AccountStore;
import com.xdja.data_mainframe.repository.datastore.UserInfoStore;
import com.xdja.domain_mainframe.model.MultiResult;

import junit.framework.Assert;

import org.hamcrest.core.IsEqual;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Provider;

import retrofit2.Response;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.data_mainframe.repository</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/5/9</p>
 * <p>Time:13:45</p>
 */
public class AccountRepImpTest extends DataApplicationTestCase {

    AccountRepImp.PreAccountRepImp preAccountRepImp;

    @Mock
    AccountStore.PreAccountStore preAccountCloudStore;
    @Mock
    AccountStore.PreAccountStore preAccountDiskStore;

    Set<Integer> errorStatus;

    @Mock
    UserInfoStore.PreUserInfoStore preUserInfoStore;

    Context context;
    @Mock
    AccountEntityDataMapper accountEntityDataMapper;

    Gson gson;

    @Mock
    Map<String, Provider<String>> stringMap;

    @Mock
    Map<String, Provider<Integer>> integerMap;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        this.context = RuntimeEnvironment.application;
        this.gson = new Gson();
        this.errorStatus = new HashSet<>(Arrays.asList(400, 401, 500));


        this.preAccountRepImp = new AccountRepImp.PreAccountRepImp(
                preAccountDiskStore,
                preAccountCloudStore,
                errorStatus,
                preUserInfoStore,
                context,
                accountEntityDataMapper,
                gson,
                stringMap,
                integerMap
        );
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testRegistAccount() throws Exception {

        when(stringMap.get(CacheModule.KEY_DEVICEID))
                .thenReturn(
                        new Provider<String>() {
                            @Override
                            public String get() {
                                return "key_deviceId";
                            }
                        }
                );

        MultiResult<String> multiResult = new MultiResult<>();
        multiResult.setResultStatus(0);
        Map<String, String> info = new HashMap<>();
        info.put("1", "1");
        info.put("2", "2");
        info.put("3", "3");
        multiResult.setInfo(info);

        when(preAccountCloudStore.registAccount(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString()))
                .thenReturn(
                        Observable.just(
                                Response.success(multiResult)
                        )
                );


        TestSubscriber<MultiResult<String>> testSubscriber = new TestSubscriber<>();
        preAccountRepImp
                .registAccount("nickName", "pwd", "avatarId", "thumbnaild")
                .subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        List<MultiResult<String>> onNextEvents = testSubscriber.getOnNextEvents();


        verify(preAccountCloudStore).registAccount(anyString(), anyString(), anyString(), anyString(), anyString());

        assertThat(onNextEvents.size(), new IsEqual(1));

        Assert.assertNotNull(onNextEvents);
        Assert.assertTrue(onNextEvents.size() == 1);
        MultiResult<String> multiResult1 = onNextEvents.get(0);
        Assert.assertEquals(multiResult1.toString(), multiResult.toString());

    }

    @Test
    public void testReObtainAccount() throws Exception {

    }

    @Test
    public void testCustomAccount() throws Exception {

    }

    @Test
    public void testModifyAccount() throws Exception {

    }

    @Test
    public void testObtainBindMobileAuthCode() throws Exception {

    }

    @Test
    public void testBindMobile() throws Exception {

    }

    @Test
    public void testForceBindMobile() throws Exception {

    }

    @Test
    public void testAccountPwdLogin() throws Exception {

    }

    @Test
    public void testObtainLogin_ResetAuthCode() throws Exception {

    }

    @Test
    public void testMobileLogin() throws Exception {

    }
}