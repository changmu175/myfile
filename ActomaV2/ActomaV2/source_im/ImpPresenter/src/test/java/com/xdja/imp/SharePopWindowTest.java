//package com.xdja.imp;
//
//import android.content.Context;
//import android.net.Uri;
//
//import com.xdja.comm.server.ActomaController;
//import com.xdja.imp.widget.SharePopWindow;
//
//import junit.framework.Assert;
//
//import org.junit.Test;
//
//import java.lang.reflect.Method;
//
///**
// * 项目名称：ActomaV2
// * 类描述：
// * 创建人：yuchangmu
// * 创建时间：2016/11/23.
// * 修改人：
// * 修改时间：
// * 修改备注：
// */
//public class SharePopWindowTest {
//
//    @Test
//    public void testShowSingleSharePopWindow() {
//
//    }
//
//    @Test
//    public void testShowSingleSharePopWindow1() {
//
//    }
//
//    @Test
//    public void testShowSingleSharePopWindow2() {
//
//    }
//
//    @Test
//    public void testSetPopWindowMessage() {
//
//    }
//
//    @Test
//    public void testSetSharePreviewBitmap() {
//
//    }
//
//    @Test
//    public void testSetSharePreviewText() {
//
//    }
//
//    @Test
//    public void testSetForwardPreviewBitmap() {
//
//    }
//
//    @Test
//    public void testInitShareDialog() {
//
//    }
//
//    @Test
//    public void testInitView() {
//
//    }
//
//    @Test
//    public void testSetCircleImageUrl() {
//
//    }
//
//    @Test
//    public void testSetSudokuAvater() {
//
//    }
//
//    @Test
//    public void testGetDefaultImageId() {
//
//    }
//
//    @Test
//    public void testGetImagePathFormUri() throws Exception {
//        SharePopWindow sharePopWindow = new SharePopWindow();
//        Uri uri = Uri.parse("content://media/external/images/media/3762");
//        String expect = "storage/emulated/0/UCDownloads/Screenshot/TMPSNAPSHOT1479795367257";
//        Context context = ActomaController.getApp().getBaseContext();
//        String method = "getImagePathFormUri";
////        SharePopWindow spw = PowerMockito.spy(new SharePopWindow());
////        PowerMockito.doReturn(expect).when(spw, method, context, uri);
//
//        Class cls = sharePopWindow.getClass();
//        Method getImagePathFormUri = cls.getDeclaredMethod("getImagePathFormUri", Context.class, Uri.class);
//        getImagePathFormUri.setAccessible(true);
//        String result = (String) getImagePathFormUri.invoke(sharePopWindow, context, uri);
//        Assert.assertEquals(expect, result);
//    }
//
//
//    @Test
//    public void testShowDialog() {
//
//    }
//
//    @Test
//    public void testDismissDialog() {
//
//    }
//
//    @Test
//    public void testSelectActionPopWindow() {
//
//    }
//
//    @Test
//    public void testInitSelectActionPop() {
//
//    }
//
//    @Test
//    public void testDealText() throws Exception {
//        SharePopWindow sharePopWindow = new SharePopWindow();
//        Class cls = sharePopWindow.getClass();
//        Method dealText = cls.getDeclaredMethod("dealText", String.class, String.class);
//        dealText.setAccessible(true);
//        String result = (String) dealText.invoke(sharePopWindow, "ycm", "hm");
//        Assert.assertEquals("ycm", result);
//    }
//}
