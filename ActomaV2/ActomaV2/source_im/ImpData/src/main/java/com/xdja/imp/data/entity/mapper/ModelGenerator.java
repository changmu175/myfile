package com.xdja.imp.data.entity.mapper;

import android.text.TextUtils;

import com.xdja.comm.server.ActomaController;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.data.utils.ToolUtil;
import com.xdja.imp.domain.model.FileExtraInfo;
import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.model.ImageFileInfo;
import com.xdja.imp.domain.model.LocalPictureInfo;
import com.xdja.imp_data.R;
import com.xdja.imsdk.constant.ImSdkFileConstant;
import com.xdja.imsdk.util.FileUtils;
import com.xdja.imsdk.util.ThumbnailUtils;

import java.io.File;
import java.util.UUID;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

/**
 * 数据实体类
 * Created by xdjaxa on 2016/8/8.
 */
public class ModelGenerator {

    /**
     * 根据本地图片信息，生成图片文件相关信息
     * @param pictureInfo 本地图片信息
     * @param account 用户账号
     * @return 图片文件信息实体类
     */
    public static Observable<FileInfo> createImageFileInfo(final LocalPictureInfo pictureInfo,
                                                                         final String account){
        if (pictureInfo == null || TextUtils.isEmpty(account)){
            return null;
        }

        //juyingang fix bug 4236 begin
        String imageFilePath = pictureInfo.getLocalPath();

        try {
            File file = new File(imageFilePath);
            if (!file.exists()){
                return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_picture_no_exist)));
            }
        } catch (Exception e) {
            LogUtil.getUtils().e(e);// add by ycm for lint 2017/02/16
        }
        //juyingang fix bug 4236 begin

        //缩略图文件保存父路径
        String imageRootPath = getRootFilePath(account);
        //文件名称
        final String baseFileName = UUID.randomUUID().toString();
        final String thumbName = ImSdkFileConstant.THUMBNAIL_FILE_PREFIX + baseFileName;
        final String hdThumbName = ImSdkFileConstant.THUMBNAIL_FILE_PREFIX + baseFileName  + "_hd";

        return Observable.zip(
                //高清缩略图
                createHDThumbnail(pictureInfo.getLocalPath(), new File(imageRootPath, hdThumbName)),
                //缩略图
                createThumbnail(pictureInfo.getLocalPath(), new File(imageRootPath, thumbName)),
                //原图
                createRawFilePath(pictureInfo.getLocalPath(), new File(imageRootPath, baseFileName), pictureInfo.isOriginalPic()),
                //生成实体类
                new Func3<String, String, String, FileInfo>() {

                    @Override
                    public ImageFileInfo call(String hdThumbFilePath,
                                              String thumbFilePath,
                                              String rawFilePath) {
                        if (TextUtils.isEmpty(hdThumbFilePath) ||
                                TextUtils.isEmpty(thumbFilePath) ||
                                TextUtils.isEmpty(rawFilePath)) {
                            return null;
                        }
                        ImageFileInfo imageFileInfo = new ImageFileInfo();
                        //缩略图信息
                        imageFileInfo.setFileName(thumbName);
                        imageFileInfo.setFilePath(thumbFilePath);
                        imageFileInfo.setFileSize(ToolUtil.getFileSize(thumbFilePath));
                        imageFileInfo.setTranslateSize(0);
                        imageFileInfo.setSuffix(ImSdkFileConstant.THUMBNAIL_FILE_SUFFIX.substring(1));

                        FileExtraInfo fileExtraInfo = new FileExtraInfo();
                        //高清缩略图信息
                        fileExtraInfo.setThumbFileName(hdThumbName);
                        fileExtraInfo.setThumbFileUrl(hdThumbFilePath);
                        fileExtraInfo.setThumbFileSize(ToolUtil.getFileSize(hdThumbFilePath));
                        fileExtraInfo.setThumbFileTranslateSize(0);

                        //原图信息
                        fileExtraInfo.setRawFileName(baseFileName);
                        fileExtraInfo.setRawFileUrl(rawFilePath);
                        if (pictureInfo.isOriginalPic()) {
                            fileExtraInfo.setRawFileSize(pictureInfo.getFileSize());
                        } else {
                            fileExtraInfo.setRawFileSize(0);
                        }
                        fileExtraInfo.setRawFileTranslateSize(0);

                        //是否需要发送原图
                        imageFileInfo.setOriginal(pictureInfo.isOriginalPic());
                        //数据填充
                        imageFileInfo.setExtraInfo(fileExtraInfo);
                        return imageFileInfo;
                    }
                }
        );
    }

    /**
     * 高清缩略图处理
     * @param originalPath
     * @param thumbFile
     * @return
     */
    public static Observable<String> createHDThumbnail(final String originalPath,
                                                       final File thumbFile){

        return Observable.just(originalPath)
                .subscribeOn(Schedulers.newThread())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String path) {
                        return ThumbnailUtils.createImageThumbnail(path,
                                ThumbnailUtils.KIND_HD, thumbFile);
                    }
                });
        /*return Observable.just(originalPath)
                .subscribeOn(Schedulers.computation())
                .map(new Func1<String, Bitmap>() {
                    @Override
                    public Bitmap call(String s) {
                        return ThumbUtils.getHDThumbFilePath(originalPath);
                    }
                })
                .subscribeOn(Schedulers.io())
                .map(new Func1<Bitmap, String>() {
                    @Override
                    public String call(Bitmap bitmap) {
                        if (bitmap != null) {
                            return ThumbUtils.saveBitmap(bitmap, 75, thumbFile);
                        } else {
                            return ThumbUtils.copyFile(originalPath, thumbFile);
                        }
                    }
                });*/
    }

    /**
     * 缩略图处理
     * @param originalPath
     * @param thumbFile
     */
    public static Observable<String> createThumbnail(final String originalPath,
                                                     final File thumbFile){
        return Observable.just(originalPath)
                .subscribeOn(Schedulers.newThread())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String path) {
                        return ThumbnailUtils.createImageThumbnail(path,
                                ThumbnailUtils.KIND_TH, thumbFile);
                    }
                });
        /*return Observable.just(originalPath)
                .subscribeOn(Schedulers.computation())
                .map(new Func1<String, Bitmap>() {
                    @Override
                    public Bitmap call(String s) {
                        //生成缩略图
                        return ThumbUtils.getThumbFilePath(originalPath);
                    }
                })
                .subscribeOn(Schedulers.io())
                .map(new Func1<Bitmap, String>() {
                    @Override
                    public String call(Bitmap bitmap) {
                        if (bitmap != null){
                            return ThumbUtils.saveBitmap(bitmap, 75, thumbFile);
                        } else {
                            return ThumbUtils.copyFile(originalPath, thumbFile);
                        }
                    }
                });*/
    }


    /**
     * 原图处理
     * @param originalPath
     * @param filePath
     * @param isOriginalPic
     * @return
     */
    public static Observable<String> createRawFilePath(final String originalPath,
                                                       final File filePath,
                                                       final boolean isOriginalPic){

        return Observable.just(originalPath)
                .subscribeOn(Schedulers.io())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String originalPath) {
                        //将原图拷贝至缓存目录下
                        //if (isOriginalPic) {
                        //    return ThumbUtils.copyFile(originalPath, filePath);
                        //} else {
                            return originalPath;
                        //}
                    }
                });
    }


    /**
     * 获取文件保存父路径
     * @return
     */
    public static String getRootFilePath(String account){
        //创建父目录
        File imageCacheFile = new File(FileUtils.getImagePath());
        if (!imageCacheFile.isDirectory()){
            imageCacheFile.deleteOnExit();
        }

        if (!imageCacheFile.exists()) {
            if (imageCacheFile.mkdirs()) {
                return imageCacheFile.getAbsolutePath();
            }

        } else {
            return imageCacheFile.getAbsolutePath();
        }

        return FileUtils.getImagePath();
//        if (!imageCacheFile.exists()) {
//            imageCacheFile.mkdirs();
//        }
//        File imageRootPath = new File(imageCacheFile, account);
//        if (!imageRootPath.exists()){
//            if (!imageRootPath.mkdirs()){
//                LogUtil.getUtils().w("create image root file path failed.");
//            }
//        }
//        return imageRootPath.getAbsolutePath();
    }
}
