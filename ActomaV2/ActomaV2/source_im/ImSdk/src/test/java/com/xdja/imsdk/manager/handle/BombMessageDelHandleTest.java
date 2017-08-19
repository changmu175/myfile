//package com.xdja.imsdk.manager.handle;
//
//import com.xdja.imsdk.ImSdkTestCase;
//import com.xdja.imsdk.callback.CallbackFunction;
//import com.xdja.imsdk.constant.MsgType;
//import com.xdja.imsdk.manager.callback.BombMsgCallback;
//import com.xdja.imsdk.manager.model.IMMessage;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Captor;
//
///**
// * Created by yuchangmu on 2016/9/28.
// */
//public class BombMessageDelHandleTest extends ImSdkTestCase {
//    private BombMessageDelHandle bombMessageDelHandle;
//    private BombMsgCallback bombMsgCallback;
//
//    @Captor
//    private ArgumentCaptor<CallbackFunction> callbackFunctionArgumentCaptor;
//
//    @Before
//    public void setUp() throws Exception {
//    }
//
//    @Test
//    public void testBombMessageDelHandle() {
//
//    }
//
//    @Test
//    public void testAdd2NodesQueue() {
//        for (int i=0; i<10; i++) {
//            IMMessage imMessage = new IMMessage();
//            imMessage.setContent("TEST " + i);
//            imMessage.setTo("USER " + i);
//            imMessage.setType(MsgType.MSG_TYPE_TEXT);
//        }
//    }
//    @Test
//    public void testRemoveNodesInQueue() {
//
//    }
//
//    private void getIMMessage() {
//        for (int i=0; i<10; i++) {
//            IMMessage imMessage = new IMMessage();
//            imMessage.setContent("TEST " + i);
//            imMessage.setTo("USER " +i);
//            imMessage.setType(MsgType.MSG_TYPE_TEXT);
////            imMessage.setTo("111111");
////            imMessage.setCardId("1");
////            imMessage.setContent();
////            imMessage.setFailCode();
////            imMessage.setIMFileInfo();
////            imMessage.setIMMessageId();
////            imMessage.setIMMessageTime();
////            imMessage.setSortTime();
////            imMessage.setType();
////            imMessage.getIMMessageId();
////            imMessage.getState();
//
//        }
//    }
//}
