package com.xdja.imp.data.repository;

import android.content.Context;

import com.google.gson.Gson;
import com.xdja.imp.ImpDataApplicationTestCase;
import com.xdja.imp.data.cache.CardCache;
import com.xdja.imp.data.cache.ConfigCache;
import com.xdja.imp.data.cache.UserCache;
import com.xdja.imp.data.entity.mapper.DataMapper;
import com.xdja.imp.data.repository.datasource.DiskDataStore;
import com.xdja.imp.data.repository.im.IMProxyImp;
import com.xdja.imsdk.ImClient;
import com.xdja.imsdk.callback.CallbackFunction;
import com.xdja.imsdk.callback.IMFileInfoCallback;
import com.xdja.imsdk.callback.IMMessageCallback;
import com.xdja.imsdk.callback.IMSecurityCallback;
import com.xdja.imsdk.callback.IMSessionCallback;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.repository</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/18</p>
 * <p>Time:15:52</p>
 */
public class IMProxyImpTest extends ImpDataApplicationTestCase {

    IMProxyImp proxyImp;

    private Context context;

    @Mock
    private ImClient imClient;
    @Mock
    private UserCache userCache;
    @Mock
    private CardCache cardCache;
    @Mock
    private ConfigCache configCache;
    @Mock
    private DataMapper mapper;
    @Mock
    private CallbackFunction callbackFunction;
    @Mock
    private IMSecurityCallback imSecurityCallback;
    @Mock
    private IMFileInfoCallback imFileInfoCallback;
    @Mock
    private IMMessageCallback imMessageCallback;
    @Mock
    private IMSessionCallback imSessionCallback;
    @Mock
    private DiskDataStore diskDataStore;
    @Mock
    private Gson gson;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        context = RuntimeEnvironment.application;
        proxyImp = new IMProxyImp(
                imClient,
                context,
                userCache,
                cardCache,
                configCache,
                mapper,
                callbackFunction,
                imFileInfoCallback,
                imMessageCallback,
                imSessionCallback,
                imSecurityCallback,
                diskDataStore,
                gson);
    }

    @Test
    public void testInitIMProxy() throws Exception {

    }

    @Test
    public void testReleaseIMProxy() throws Exception {

    }

    @Test
    public void testSetProxyConfig() throws Exception {

    }

    @Test
    public void testGetProxyConfig() throws Exception {

    }

    @Test
    public void testGetSessionList() throws Exception {

        /*List<SessionBean> sessionBeans = new ArrayList<>();
        Random random = new Random(1000);
        for (int i = 0; i < 20; i++) {
            SessionBean sessionBean = new SessionBean();
            sessionBean.setLastMsg(System.currentTimeMillis());
            sessionBean.setLastMsgTime(random.nextLong());
            if (i % 7 == 0) {
                sessionBeans.add(null);
            } else {
                sessionBeans.add(sessionBean);
            }
        }


        when(imClient.GetSessionList(anyInt(), anyInt())).thenReturn(sessionBeans);

        TestSubscriber<List<TalkListBean>> testSubscriber = new TestSubscriber<>();
        Observable<List<TalkListBean>> sessionList = proxyImp.getTalkListBeans(1, 10);
        sessionList.subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);
        List<List<TalkListBean>> onNextEvents = testSubscriber.getOnNextEvents();
        List<TalkListBean> talkListBeen = onNextEvents.get(0);
        for (TalkListBean talkListBean : talkListBeen) {
            LogUtil.getUtils().i(talkListBean.getTime());
        }*/

    }

    @Test
    public void testDeleteSession() throws Exception {

    }

    @Test
    public void testSessionListAddCust() throws Exception {

    }

    @Test
    public void testMsgListAddCust() throws Exception {

    }

    @Test
    public void testGetMsgList() throws Exception {

    }

    @Test
    public void testDeleteMsg() throws Exception {

    }

    @Test
    public void testGetMissedCount() throws Exception {

    }

    @Test
    public void testGetAllMissedCount() throws Exception {

    }

    @Test
    public void testGetImageList() throws Exception {

    }

    @Test
    public void testSendTextMsg() throws Exception {

    }

    @Test
    public void testSendFileMsg() throws Exception {

    }

    @Test
    public void testSendFilePause() throws Exception {

    }

    @Test
    public void testSendFileStart() throws Exception {

    }

    @Test
    public void testReciveFilePause() throws Exception {

    }

    @Test
    public void testReciveFileStart() throws Exception {

    }

    @Test
    public void testMsgStateChange() throws Exception {

    }

    @Test
    public void testResendMsg() throws Exception {

    }
}