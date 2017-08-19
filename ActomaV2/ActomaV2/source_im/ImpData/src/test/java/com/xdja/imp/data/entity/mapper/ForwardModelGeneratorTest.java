//package com.xdja.imp.data.entity.mapper;
//
//import com.xdja.dependence.uitls.LogUtil;
//import com.xdja.imsdk.constant.ImSdkConfigValue;
//import com.xdja.imsdk.constant.ImSdkConstant;
//
//import junit.framework.Assert;
//
//import org.junit.Test;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
///**
// * 项目名称：ActomaV2
// * 类描述：
// * 创建人：yuchangmu
// * 创建时间：2016/12/13.
// * 修改人：
// * 修改时间：
// * 修改备注：
// */
//public class ForwardModelGeneratorTest {
//
////    @Test
////    public void testCreateThumbnail1() throws Exception {
////        String originalPath = "/storage/emulated/0/XdjaIm/Image/5029763/th_5dbef87e-000d-45e2-bf64-e8021b0e1e26";
////        String baseFileName = UUID.randomUUID().toString();
////        String thumbName = ImSdkConstant.ImSdkFile.THUMBNAIL_FILE_PREFIX + baseFileName;
////        String imageRootPath = getRootFilePath("5029763");
////        List<File> fileList = new ArrayList<>();
////        for (int i = 0; i < 9; i++) {
////            thumbName = thumbName + "_" + i;
////            File file = new File(imageRootPath, thumbName);
////            fileList.add(file);
////        }
////        ForwardModelGenerator.createThumbnail(originalPath, fileList);
////        for (int j = 0; j < fileList.size(); j++) {
////            Assert.assertFalse(fileList.get(j).exists());
////        }
////    }
//
//    @Test
//    public void testCreateThumbnail2() throws Exception {
//        String originalPath = "/storage/emulated/0/XdjaIm/Image/5029763/th_5dbef87e-000d-45e2-bf64-e8021b0e1e26";
//        String baseFileName = UUID.randomUUID().toString();
//        String thumbName = ImSdkConstant.ImSdkFile.THUMBNAIL_FILE_PREFIX + baseFileName;
////        File file = new File(originalPath);
//        String imageRootPath = getRootFilePath("5029763");
//        File file = new File(imageRootPath, thumbName);
//        ForwardModelGenerator.createThumbnail(originalPath, file);
//        boolean exists = file.exists();
//        Assert.assertEquals(true, exists);
//    }
//
//    public static String getRootFilePath(String account) {
//        //创建父目录
//        File imageCacheFile = new File(ImSdkConfigValue.IMAGE_CACHE_PATH);
//        if (!imageCacheFile.isDirectory()) {
//            imageCacheFile.deleteOnExit();
//        }
//        if (!imageCacheFile.exists()) {
//            imageCacheFile.mkdirs();
//        }
//        File imageRootPath = new File(imageCacheFile, account);
//        if (!imageRootPath.exists()) {
//            if (!imageRootPath.mkdirs()) {
//                LogUtil.getUtils().w("create image root file path failed.");
//            }
//        }
//        return imageRootPath.getAbsolutePath();
//    }
//}
