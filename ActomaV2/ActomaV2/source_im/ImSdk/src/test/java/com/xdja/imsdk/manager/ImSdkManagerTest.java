//package com.xdja.imsdk.manager;
//
//import android.content.Context;
//
//import com.xdja.google.gson.Gson;
//import com.xdja.google.gson.reflect.TypeToken;
//import com.xdja.imsdk.ImSdkTestCase;
//import com.xdja.imsdk.ImSdkTestConstant;
//import com.xdja.imsdk.callback.CallbackFunction;
//import com.xdja.imsdk.constant.ImSdkConfigKey;
//import com.xdja.imsdk.constant.ImSdkConstant;
//import com.xdja.imsdk.constant.MsgType;
//import com.xdja.imsdk.exception.ErrorCode;
//import com.xdja.imsdk.manager.model.IMMessage;
//import com.xdja.imsdk.manager.model.IMSession;
//import com.xdja.imsdk.manager.model.InitParam;
//import com.xdja.imsdk.util.JsonUtils;
//
//import org.json.JSONObject;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.mockito.invocation.InvocationOnMock;
//import org.mockito.stubbing.Answer;
//import org.robolectric.RuntimeEnvironment;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.Assert.*;
//
///**
// * 项目名称：IMPresenter
// * 类描述：
// * 创建人：liming
// * 创建时间：2016/7/4 17:09
// * 修改人：liming
// * 修改时间：2016/7/4 17:09
// * 修改备注：
// */
//public class ImSdkManagerTest extends ImSdkTestCase {
//    private Context context;
//    private InitParam initParam;
//    private ImSdkManager sdkManager;
//    private Gson gson;
//
//    @Before
//    public void setUp() throws Exception {
//        MockitoAnnotations.initMocks(this);
//        context = RuntimeEnvironment.application;
//        initParam = getInitParam();
//        sdkManager = new ImSdkManager();
//        gson = new Gson();
//    }
//
//    @Test
//    public void testInit() throws Exception {
//        InitParam initParam1 = new InitParam();
//        initParam1 = null;
////        initParam1.setAccount("111111");
////        initParam1.setTfcardId("111111111111");
////        initParam1.setCallback();
////        initParam1.setTicket("1111111111111");
////        initParam1.setdType();
////        initParam1.setProperties();
//        ImSdkManager imSdkManager = Mockito.mock(ImSdkManager.class);
//        Mockito.doAnswer(new Answer() {
//            @Override
//            public Object answer(InvocationOnMock invocation) throws Throwable {
//                Object[] arguments = invocation.getArguments();
//                InitParam initParam = (InitParam) arguments[1];
//                CallbackFunction callbackFunction = initParam.getCallback();
//                callbackFunction.ImSdkStateChange(ErrorCode.ECODE_SDK_SERVICE_OK);
//                return ErrorCode.ECODE_SDK_SERVICE_OK;
//            }
//        }).when(imSdkManager).init(context, initParam);
////        sdkManager.init(context, initParam);
//    }
//
//    @Test
//    public void testRelease() throws Exception {
//
//    }
//
//    @Test
//    public void testSetConfig() throws Exception {
////        sdkManager.init(context, initParam);
////        Map<String, String> param = new HashMap<>();
////        param.put("", "");
////        int result = sdkManager.saveConfig(param);
////        assertEquals("RESULT_FAIL_PARA", ErrorCode.ImSdkResult.RESULT_OK, result);
//
//
//    }
//
//    @Test
//    public void testGetConfig() throws Exception {
//
//    }
//
//    @Test
//    public void testGetIMSessionList() throws Exception {
////        beforeImpTest();
//        for (int i=0; i<10; i++) {
//            IMMessage msg = new IMMessage();
//            msg.setContent("TEST " + i);
//            msg.setTo("USER " +i);
//            msg.setType(MsgType.MSG_TYPE_TEXT);
//            sdkManager.sendIMMessage(msg);
//        }
//        List<IMSession> imSessionList = sdkManager.getIMSessionList("", 0);
////        int code = JsonUtils.getInt(sessionJson, ImSdkConstant.ImSdkJson.JSON_RESULT_CODE);
////        assertEquals("sdk should return OK", ErrorCode.ImSdkResult.RESULT_OK, code);
////        String allSessions = JsonUtils.getString(sessionJson, ImSdkConstant.ImSdkJson.JSON_RESULT);
////        List<IMSession> sessions = gson.fromJson(allSessions, new TypeToken<List<IMSession>>() {
////        }.getType());
////        assertEquals("session number sdk returned is wrong", num, sessions.size());
//    }
//
//    @Test
//    public void testGetIMMessageList() throws Exception {
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
//
//    }
//
//    @Test
//    public void testGetImageList() throws Exception {
//
//    }
//
//    @Test
//    public void testDeleteIMSession() throws Exception {
//
//    }
//
//    @Test
//    public void testIMSessionListAddCust() throws Exception {
//
//    }
//
//    @Test
//    public void testIMMessageListAddCust() throws Exception {
//
//    }
//
//    @Test
//    public void testDeleteIMMessage() throws Exception {
//
//    }
//
//    @Test
//    public void testSendIMMessage() throws Exception {
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
//    private void beforeImpTest() {
//        sdkManager.init(context, initParam);
////        assertEquals("init should be success", ErrorCode.ImSdkResult.RESULT_OK, result);
//        waitForCallback(5);
////
////        result = sdkManager.ClearAllLocalData();
////        assertEquals("sdk should be cleared", ErrorCode.ImSdkResult.RESULT_OK, result);
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
//        InitParam param = new InitParam();
//        param.setAccount(ImSdkTestConstant.LOGIN_ACCOUNT);
//        param.setTfcardId(ImSdkTestConstant.LOGIN_TFCARD);
//        param.setTicket(ImSdkTestConstant.LOGIN_TICKET);
//        HashMap<String, String> imProperty = new HashMap<String, String>();
//        imProperty.put(ImSdkConfigKey.IP_ADDRESS, ImSdkTestConstant.IP_ADDRESS);
//        imProperty.put(ImSdkConfigKey.IP_PORT, ImSdkTestConstant.IP_PORT);
//        param.setProperties(imProperty);
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
//        param.setCallback(sdkCallback);
//        return param;
//    }
//}