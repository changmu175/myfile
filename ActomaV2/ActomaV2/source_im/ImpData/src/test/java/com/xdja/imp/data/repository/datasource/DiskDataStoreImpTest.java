package com.xdja.imp.data.repository.datasource;

import com.xdja.imp.ImpDataApplicationTestCase;
import com.xdja.imp.data.entity.SessionParam;
import com.xdja.imp.data.persistent.DbUtil;
import com.xdja.imp.data.persistent.PreferencesUtil;
import com.xdja.imp.domain.model.KeyValuePair;
import com.xdja.xutils.DbUtils;
import com.xdja.xutils.db.sqlite.WhereBuilder;
import com.xdja.xutils.exception.DbException;

import org.junit.Assert;
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
 * <p>Summary:数据硬盘存储单元测试用例</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.repository.datasource</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/14</p>
 * <p>Time:12:02</p>
 */
public class DiskDataStoreImpTest extends ImpDataApplicationTestCase {

    DiskDataStoreImp diskDataStoreImp;

    @Mock
    PreferencesUtil preferencesUtil;

    @Mock
    DbUtil dbUtil;

    @Mock
    DbUtils xDbUtils;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        diskDataStoreImp = new DiskDataStoreImp(preferencesUtil, dbUtil);
    }

    //PASSED
    @Test
    public void testGetLocalSingleSession() {
        final String mock_talkId = "12345";
        SessionParam sessionParam = new SessionParam();
        sessionParam.setIsTop(SessionParam.ISTOP_TRUE);
        sessionParam.setFlag(mock_talkId);
        sessionParam.setIsNoDisturb(SessionParam.ISDISTURB_TRUE);
        sessionParam.setDraft("draft");

        Exception ex = null;

        when(dbUtil.get()).thenReturn(xDbUtils);
        try {
            when(xDbUtils.findFirst(any(SessionParam.class.getClass()),any(WhereBuilder.class)))
                    .thenReturn(sessionParam);
        } catch (DbException e) {
            e.printStackTrace();
            ex = e;
        }

        Observable<SessionParam> localSingleSession = diskDataStoreImp.getLocalSingleSession(mock_talkId);
        TestSubscriber<SessionParam> testSubscriber = new TestSubscriber<>();
        localSingleSession.subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(sessionParam);
        Assert.assertNull(ex);

        try {
            verify(xDbUtils).findFirst(any(SessionParam.class.getClass()),any(WhereBuilder.class));
        } catch (DbException e) {
            e.printStackTrace();
            ex = e;
        }
        Assert.assertNull(ex);

    }

    //PASSED
    @Test
    public void testSaveOrUpdateLocalSingleSessionWithEmptyFlag() {

        SessionParam sessionParam = new SessionParam();
        sessionParam.setFlag("");

        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();

        Observable<Boolean> booleanObservable
                = diskDataStoreImp.saveOrUpdateLocalSingleSession(sessionParam);

        booleanObservable.subscribe(testSubscriber);

        testSubscriber.assertError(Throwable.class);

        verify(dbUtil,never()).get();


    }

    //PASSED
    @Test
    public void testSaveOrUpdateLocalSingleSession() {

        SessionParam sessionParam = new SessionParam();
        sessionParam.setFlag("12345");

        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();

        when(dbUtil.get()).thenReturn(xDbUtils);

        Observable<Boolean> booleanObservable
                = diskDataStoreImp.saveOrUpdateLocalSingleSession(sessionParam);

        booleanObservable.subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(dbUtil).get();
        try {
            verify(xDbUtils).saveOrUpdate(any(SessionParam.class));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    //PASSED
    @Test
    public void testSaveKeyValuePairsWithEmptyKey() throws Exception {
        KeyValuePair<String, String> testKVP = new KeyValuePair<>();
        testKVP.setKey("");

        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        Observable<Boolean> booleanObservable = diskDataStoreImp.saveKeyValuePairs(testKVP);
        booleanObservable.subscribe(testSubscriber);

        testSubscriber.assertError(Throwable.class);
        verify(preferencesUtil, never()).setPreferenceStringValue(anyString(), anyString());
    }
    //PASSED
    @Test
    public void testSaveKeyValuePairs() throws Exception {
        when(preferencesUtil.setPreferenceStringValue(anyString(), anyString()))
                .thenReturn(Boolean.TRUE);

        KeyValuePair<String, String> testKVP = new KeyValuePair<>();
        testKVP.setKey("testKey");
        testKVP.setValue("testValue");

        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        Observable<Boolean> booleanObservable = diskDataStoreImp.saveKeyValuePairs(testKVP);
        booleanObservable.subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        verify(preferencesUtil).setPreferenceStringValue(anyString(), anyString());
    }
    //PASSED
    @Test
    public void testQuerySharePrefWithEmptyKey() throws Exception {
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        Observable<String> testKey = diskDataStoreImp.queryStringSharePref("");
        testKey.subscribe(testSubscriber);

        testSubscriber.assertError(Throwable.class);
        verify(preferencesUtil, never()).gPrefStringValue(anyString());
    }
    //PASSED
    @Test
    public void testQuerySharePref() throws Exception {
        when(preferencesUtil.gPrefStringValue(anyString())).thenReturn("testValue");

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        Observable<String> testKey = diskDataStoreImp.queryStringSharePref("testKey");
        testKey.subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        verify(preferencesUtil).gPrefStringValue(anyString());
    }
}