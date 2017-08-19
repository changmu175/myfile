//package com.xdja.imsdk;
//
//import android.content.Context;
//import android.text.TextUtils;
//
//import com.xdja.comm.server.ActomaController;
//import com.xdja.comm.server.PreferencesServer;
//import com.xdja.google.gson.Gson;
//import com.xdja.google.gson.reflect.TypeToken;
//import com.xdja.imsdk.callback.CallbackFunction;
//import com.xdja.imsdk.constant.ImSdkConfigKey;
//import com.xdja.imsdk.constant.ImSdkConfigValue;
//import com.xdja.imsdk.constant.ImSdkConstant;
//import com.xdja.imsdk.constant.MsgType;
//import com.xdja.imsdk.exception.ErrorCode;
//import com.xdja.imsdk.manager.ImSdkConfigManager;
//import com.xdja.imsdk.manager.ImSdkManager;
//import com.xdja.imsdk.manager.model.IMMessage;
//import com.xdja.imsdk.manager.model.IMSession;
//import com.xdja.imsdk.manager.model.InitParam;
//import com.xdja.imsdk.util.JsonUtils;
//
//import org.json.JSONObject;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Captor;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.mockito.invocation.InvocationOnMock;
//import org.mockito.stubbing.Answer;
//import org.robolectric.RuntimeEnvironment;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.Assert.*;
//import static org.mockito.Matchers.any;
//
///**
// * 项目名称：IMPresenter
// * 类描述：
// * 创建人：liming
// * 创建时间：2016/6/30 11:42
// * 修改人：liming
// * 修改时间：2016/6/30 11:42
// * 修改备注：
// */
//public class ImClientTest extends ImSdkTestCase{
//    private Context context;
//    private ImClient imClient;
//    private InitParam initParam;
//    private Gson gson;
//    private ImSdkManager imSdkManager;
//
//    @Captor
//    private ArgumentCaptor<CallbackFunction> callbackFunctionArgumentCaptor;
//
//    @Before
//    public void setUp() throws Exception {
//        MockitoAnnotations.initMocks(this);
//        initParam = getInitParam();
//        context = RuntimeEnvironment.application;
//        imClient = ImClient.getInstance(RuntimeEnvironment.application);
//        gson = new Gson();
//    }
//
//    @Test
//    public void testInit() throws Exception {
//        // validate para start
//        int result = imClient.Init(initParam);
//        assertEquals("context is null", ErrorCode.ImSdkResult.RESULT_OK, result);
//
//        result = imClient.Init(null);
//        assertEquals("InitParam is null", ErrorCode.ImSdkResult.RESULT_FAIL_PARA, result);
//
//        InitParam para1 = getInitParam();
//        para1.setAccount(null);
//        result = imClient.Init(para1);
//        assertEquals("account is null", ErrorCode.ImSdkResult.RESULT_FAIL_PARA, result);
//
//        InitParam para2 = getInitParam();
//        para2.setAccount("");
//        result = imClient.Init(para2);
//        assertEquals("account is empty", ErrorCode.ImSdkResult.RESULT_FAIL_PARA, result);
//
//        InitParam para3 = getInitParam();
//        para3.setTfcardId(null);
//        result = imClient.Init(para3);
//        assertEquals("tf card is null", ErrorCode.ImSdkResult.RESULT_FAIL_PARA, result);
//
//        InitParam para4 = getInitParam();
//        para4.setTfcardId("");
//        result = imClient.Init(para4);
//        assertEquals("tf card is empty", ErrorCode.ImSdkResult.RESULT_FAIL_PARA, result);
//
//        InitParam para5 = getInitParam();
//        para5.setTicket(null);
//        result = imClient.Init(para5);
//        assertEquals("ticket is null", ErrorCode.ImSdkResult.RESULT_FAIL_PARA, result);
//
//        InitParam para6 = getInitParam();
//        para6.setTicket("");
//        result = imClient.Init(para6);
//        assertEquals("ticket is empty", ErrorCode.ImSdkResult.RESULT_FAIL_PARA, result);
//
//        InitParam para7 = getInitParam();
//        para7.setCallback(null);
//        result = imClient.Init(para7);
//        assertEquals("callback is null", ErrorCode.ImSdkResult.RESULT_FAIL_PARA, result);
//        // validate para end
//
//        // forbidden para start
//
//        // forbidden para end
//
////        beforeImpTest();
//
//        ImClient client = Mockito.mock(ImClient.class);
//        Mockito.doAnswer(new Answer() {
//            @Override
//            public Object answer(InvocationOnMock invocation) throws Throwable {
//                //这里可以获得传给performLogin的参数
//                Object[] arguments = invocation.getArguments();
//
//                InitParam init = (InitParam) arguments[0];
//                CallbackFunction callback = init.getCallback();
//                callback.ImSdkStateChange(ErrorCode.ECODE_SDK_SERVICE_OK);
//                return ErrorCode.ECODE_SDK_SERVICE_OK;
//            }
//        }).when(client).Init(any(InitParam.class));
//
//
//        InitParam initForMock = getInitParam();
//        initForMock.setCallback(Mockito.mock(CallbackFunction.class));
//        client.Init(initForMock);
//
////        InitParam para8 = getInitParam();
////        para8.setCallback(callbackFunctionArgumentCaptor.capture());
////        client.Init(para8);
////        callbackFunctionArgumentCaptor.getValue().ImSdkStateChange(ErrorCode.ECODE_SDK_SERVICE_OK);
//
//        result = imClient.Init(initParam);
//        assertEquals("应该返回调用成功", 0, result);
//    }
//
//    @Test
//    public void testSetConfig() throws Exception {
//        int result = imClient.Init(initParam);
//        assertEquals("init should be success", ErrorCode.ImSdkResult.RESULT_OK, result);
//        waitForCallback(5);
//
//        Map<String, String> config = new HashMap<>();
//        int resukt = imClient.SetConfig(config);
//    }
//
//    @Test
//    public void testGetConfig() throws Exception {
//
//    }
//
//    @Test
//    public void testGetIMSessionList() throws Exception {
//        imClient.Init(initParam);
//        ImSdkConfigManager imSdkConfigManager = ImSdkConfigManager.getInstance();
//        HashMap<String, String> imProperty = setProperty();
//        imSdkConfigManager.init(imProperty);
////        imClient.ClearAllLocalData();
////        beforeImpTest();
//        int num = 10;
//        for (int i=0; i<10; i++) {
//            IMMessage msg = new IMMessage();
//            msg.setContent("TEST " + i);
//            msg.setTo("USER " +i);
//            msg.setType(MsgType.MSG_TYPE_TEXT);
//            imClient.SendIMMessage(msg);
//        }
//        JSONObject sessionJson = imClient.GetIMSessionList("", 10);
//        int code = JsonUtils.getInt(sessionJson, ImSdkConstant.ImSdkJson.JSON_RESULT_CODE);
//        assertEquals("sdk should return OK", ErrorCode.ImSdkResult.RESULT_OK, code);
//
//        String allSessions = JsonUtils.getString(sessionJson, ImSdkConstant.ImSdkJson.JSON_RESULT);
//        List<IMSession> sessions = gson.fromJson(allSessions, new TypeToken<List<IMSession>>() {
//        }.getType());
//        assertEquals("session number sdk returned is wrong", num, sessions.size());
//    }
//
//    @Test
//    public void testDeleteIMSession() throws Exception {
//        List<String> list1 = new ArrayList<String>();
//        list1.add("111111_1");
//        int result1 = imClient.DeleteIMSession(list1);
//        assertEquals("not init", result1, ErrorCode.ImSdkResult.RESULT_FAIL_SERVICE);
//
//        imClient.Init(initParam);
//        int result2 = imClient.DeleteIMSession(list1);
//        assertEquals("not init", result2, ErrorCode.ImSdkResult.RESULT_FAIL_FORBID);
//
//        imClient.Init(initParam);
//        ImSdkConfigManager imSdkConfigManager = ImSdkConfigManager.getInstance();
//        HashMap<String, String> imProperty = setProperty();
//        imSdkConfigManager.init(imProperty);
//        List<String> list2 = new ArrayList<String>();
//        list2.add("111111_1");
//        int result3 = imClient.DeleteIMSession(list2);
//        assertEquals("delete success", result3, ErrorCode.ImSdkResult.RESULT_OK);
//
//
//    }
//
//    @Test
//    public void testIMSessionListAddCust() throws Exception {
//        IMSession session = new IMSession();
//        session.setImPartner("111111");
//        session.setLastMessageType(1);
//        session.setSessionTag("111111_1");
//        session.setSessionType(1);
//        JSONObject jsonObject = imClient.IMSessionListAddCust(session);
//        int code = JsonUtils.getInt(jsonObject, ImSdkConstant.ImSdkJson.JSON_RESULT_CODE);
//        assertEquals("not init", code, ErrorCode.ImSdkResult.RESULT_FAIL_SERVICE);
//
//        imClient.Init(initParam);
//        JSONObject jsonObject1 = imClient.IMSessionListAddCust(session);
//        int code1 = JsonUtils.getInt(jsonObject1, ImSdkConstant.ImSdkJson.JSON_RESULT_CODE);
//        assertEquals(" no session", code1, ErrorCode.ImSdkResult.RESULT_FAIL_FORBID);
//
//        imClient.Init(initParam);
//        ImSdkConfigManager imSdkConfigManager = ImSdkConfigManager.getInstance();
//        imSdkConfigManager.init(setProperty());
//        IMSession session1 = null;
//        JSONObject jsonObject2 = imClient.IMSessionListAddCust(session1);
//        int code2 = JsonUtils.getInt(jsonObject2, ImSdkConstant.ImSdkJson.JSON_RESULT_CODE);
//        assertEquals("session is null", code2, ErrorCode.ImSdkResult.RESULT_FAIL_PARA);
//
//
//        imClient.Init(initParam);
//        ImSdkConfigManager imSdkConfigManager1 = ImSdkConfigManager.getInstance();
//        imSdkConfigManager1.init(setProperty());
//        IMSession session2 = new IMSession();
//        session2.setImPartner("");
//        JSONObject jsonObject3 = imClient.IMSessionListAddCust(session2);
//        int code3 = JsonUtils.getInt(jsonObject3, ImSdkConstant.ImSdkJson.JSON_RESULT_CODE);
//        assertEquals("partner is empty!", code3, ErrorCode.ImSdkResult.RESULT_FAIL_PARA);
//
//        imClient.Init(initParam);
//        ImSdkConfigManager imSdkConfigManager2 = ImSdkConfigManager.getInstance();
//        imSdkConfigManager2.init(setProperty());
//        IMSession session3 = new IMSession();
//        session3.setImPartner("111111");
//        session3.setSessionType(0);
//        JSONObject jsonObject4 = imClient.IMSessionListAddCust(session3);
//        int code4 = JsonUtils.getInt(jsonObject4, ImSdkConstant.ImSdkJson.JSON_RESULT_CODE);
//        assertEquals("partner is empty!", code4, ErrorCode.ImSdkResult.RESULT_FAIL_PARA);
//
////        imClient.Init(initParam);
////        ImSdkConfigManager imSdkConfigManager3 = ImSdkConfigManager.getInstance();
////        imSdkConfigManager3.init(setProperty());
////        IMSession session4 = new IMSession();
////        session4.setImPartner("111111");
////        session4.setSessionType(1);
////        JSONObject jsonObject5 = new JSONObject();
////        jsonObject5 = imClient.IMSessionListAddCust(session4);
////
////        String code5 = JsonUtils.getString(jsonObject5, ImSdkConstant.ImSdkJson.JSON_RESULT);
////        assertEquals("partner is empty!", code5, ErrorCode.ImSdkResult.RESULT_FAIL_PARA);
//
//    }
//
//    @Test
//    public void testIMMessageListAddCust() throws Exception {
//        IMMessage imMessage = new IMMessage();
//        String sessionTag = "111111_1";
//        JSONObject jsonObject =  imClient.IMMessageListAddCust(sessionTag, imMessage);
//        int code = JsonUtils.getInt(jsonObject, ImSdkConstant.ImSdkJson.JSON_RESULT_CODE);
//        assertEquals("imclient not init", code, ErrorCode.ImSdkResult.RESULT_FAIL_SERVICE);
//
//        imClient.Init(initParam);
//        IMMessage imMessage1 = new IMMessage();
//        String sessionTag1 = "111111_1";
//        JSONObject jsonObject1 =  imClient.IMMessageListAddCust(sessionTag1, imMessage1);
//        int code1 = JsonUtils.getInt(jsonObject1, ImSdkConstant.ImSdkJson.JSON_RESULT_CODE);
//        assertEquals("config not init", code1, ErrorCode.ImSdkResult.RESULT_FAIL_FORBID);
//
//
//        imClient.Init(initParam);
//        ImSdkConfigManager sdkConfigManager = ImSdkConfigManager.getInstance();
//        sdkConfigManager.init(setProperty());
//        IMMessage imMessage3 = null;
//        String sessionTag3 = "111111_1";
//        JSONObject jsonObject3 =  imClient.IMMessageListAddCust(sessionTag3, imMessage3);
//        int code3 = JsonUtils.getInt(jsonObject3, ImSdkConstant.ImSdkJson.JSON_RESULT_CODE);
//        assertEquals("immessage is null ", code3, ErrorCode.ImSdkResult.RESULT_FAIL_PARA);
//
//    }
//
//    @Test
//    public void testGetIMMessageList() throws Exception {
//        JSONObject jsonObject = imClient.GetIMMessageList(" ", 0L, 0);
//        int code = JsonUtils.getInt(jsonObject, ImSdkConstant.ImSdkJson.JSON_RESULT_CODE);
//        assertEquals("imclient not init", code, ErrorCode.ImSdkResult.RESULT_FAIL_SERVICE);
//
//        imClient.Init(initParam);
//        JSONObject jsonObject1 = imClient.GetIMMessageList("", 0L, 0);
//        int code1 = JsonUtils.getInt(jsonObject1, ImSdkConstant.ImSdkJson.JSON_RESULT_CODE);
//        assertEquals("imclient not init", code1, ErrorCode.ImSdkResult.RESULT_FAIL_PARA);
//
//    }
//
//    @Test
//    public void testDeleteIMMessage() throws Exception {
//
//    }
//
//    @Test
//    public void testGetIMMessageById() throws Exception {
//
//    }
//
//    @Test
//    public void testGetIMSessionIdByAccount() throws Exception {
//
//    }
//
//    @Test
//    public void testGetRemindIMMessageCount() throws Exception {
//
//    }
//
//    @Test
//    public void testGetAllRemindIMMessageCount() throws Exception {
//
//    }
//
//    @Test
//    public void testClearRemindIMMessage() throws Exception {
//
//    }
//
//    @Test
//    public void testClearIMSessionAllIMMessage() throws Exception {
//
//    }
//
//    @Test
//    public void testClearAllLocalData() throws Exception {
//        int result = imClient.Init(initParam);
//        assertEquals("init success", ErrorCode.ImSdkResult.RESULT_OK, result);
//        int result1 = imClient.ClearAllLocalData();
//        assertEquals("clear success" ,ErrorCode.ImSdkResult.RESULT_OK, result1);
//    }
//
//    @Test
//    public void testGetImageList() throws Exception {
//
//    }
//
//    @Test
//    public void testSendFilePause() throws Exception {
//
//    }
//
//    @Test
//    public void testSendFileResume() throws Exception {
//
//    }
//
//    @Test
//    public void testReceiveFileStart() throws Exception {
//
//    }
//
//    @Test
//    public void testReceiveFilePause() throws Exception {
//
//    }
//
//    @Test
//    public void testReceiveFileResume() throws Exception {
//
//    }
//
//    @Test
//    public void testIMMessageStateChange() throws Exception {
//
//    }
//
//    @Test
//    public void testResendIMMessage() throws Exception {
//
//    }
//
//    @Test
//    public void testSendIMMessage() throws Exception {
//
//    }
//
//    @Test
//    public void testRegisterIMSessionChangeListener() throws Exception {
//
//    }
//
//    @Test
//    public void testUnregisterIMSessionChangeListener() throws Exception {
//
//    }
//
//    @Test
//    public void testRegisterIMMessageChangeListener() throws Exception {
//
//    }
//
//    @Test
//    public void testUnregisterIMMessageChangeListener() throws Exception {
//
//    }
//
//    @Test
//    public void testRegisterIMFileInfoChangeListener() throws Exception {
//
//    }
//
//    @Test
//    public void testUnregisterIMFileInfoChangeListener() throws Exception {
//
//    }
//
//    @Test
//    public void testRelease() throws Exception {
//
//    }
//
//    private void initSdkService() {
//        imClient.Release(ImSdkConstant.RELEASE_QUIT);
//    }
//
//    private void beforeImpTest() {
//        int result = imClient.Init(initParam);
//        assertEquals("init should be success", ErrorCode.ImSdkResult.RESULT_OK, result);
//        waitForCallback(5);
//
//        result = imClient.ClearAllLocalData();
//        assertEquals("sdk should be cleared", ErrorCode.ImSdkResult.RESULT_OK, result);
//    }
//
//    private void waitForCallback(long time) {
//        try {
//            Thread.sleep(time * 1000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private InitParam getInitParam() {
//        InitParam para = new InitParam();
//        para.setAccount(ImSdkTestConstant.LOGIN_ACCOUNT);
//        para.setTfcardId(ImSdkTestConstant.LOGIN_TFCARD);
//        para.setTicket(ImSdkTestConstant.LOGIN_TICKET);
//
//        HashMap<String, String> imProperty = new HashMap<>();
//        imProperty.put(ImSdkConfigKey.IP_ADDRESS, ImSdkTestConstant.IP_ADDRESS);
//        imProperty.put(ImSdkConfigKey.IP_PORT, ImSdkTestConstant.IP_PORT);
//        para.setProperties(imProperty);
//
//        CallbackFunction sdkCallback = new CallbackFunction() {
//            @Override
//            public int NewIMMessageCome(IMSession session, List<IMMessage> messageList) {
//                return 0;
//            }
//
//            @Override
//            public int ImSdkStateChange(int code) {
//                return 0;
//            }
//        };
//        para.setCallback(sdkCallback);
//        return para;
//    }
//
//    private HashMap<String, String> setProperty(){
//        HashMap<String, String> imProperty = new HashMap<>();
//        String url = PreferencesServer.getWrapper(ActomaController.getApp())
//                .gPrefStringValue("imUrl");
//        imProperty.put(ImSdkConfigKey.IM_SERVER, url);
//        String fdfs = PreferencesServer.getWrapper(ActomaController.getApp())
//                .gPrefStringValue("fastDfs");
//        if (!TextUtils.isEmpty(fdfs)) {
//            if (fdfs.contains("http://") || fdfs.contains("https://")) {
//                String[] infos = fdfs.split("/");
//                String host_port = infos[2];
//                if (null != host_port) {
//                    int pos = host_port.lastIndexOf(":");
//                    String address = host_port.substring(0, pos == -1 ? 0 : pos);
//                    String port = pos == -1 ? "" : host_port.substring(pos + 1);
//                    imProperty.put(ImSdkConfigKey.FILE_HTTP, address);
//                    imProperty.put(ImSdkConfigKey.FILE_PORT, port);
//                }
//            }
//        }
//        imProperty.put(ImSdkConfigKey.SLAST_MSG_SC, ImSdkConfigValue.CONFIG_VALUE_YES);
//        imProperty.put(ImSdkConfigKey.KEYSTORE_ID, String.valueOf(R.raw.truststore));
//        return imProperty;
//    }
//}