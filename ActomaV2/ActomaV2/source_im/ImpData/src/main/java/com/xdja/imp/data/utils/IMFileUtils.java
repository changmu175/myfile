package com.xdja.imp.data.utils;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.xdja.comm.server.ActomaController;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.domain.model.*;
import com.xdja.imsdk.model.IMMessage;
import com.xdja.imsdk.model.body.IMFileBody;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * <p>Author: leiliangliang   </br>
 * <p>Date: 2016/12/13 14:53   </br>
 * <p>Package: com.xdja.imp.util</br>
 * <p>Description: 本地文件相关处理类</br>
 */
public class IMFileUtils {
    private final static String CONTACT_FILES_AUTHORITY = "com.android.contacts.files";//用于判断是否是联系人文件
    private final static String DOWNLOAD_FILES_AUTHORITY = "com.android.providers.downloads.documents";//用于判断是否是系统下载中的文件
    private final static String CONTACT_FILES_PATH = "/Android/data/com.android.contacts/cache/";//联系人文件路径
    //document use suffix
    public static final String[] mImageSuffix = new String[]{
            ".bmp",
            ".png",
            ".jpg",
            ".jpeg",
            ".gif"
    };

    //document use suffix
    public static final String[] mDocSuffix = new String[]{
            ".doc",
            ".docx",
            ".xls",
            ".xlsx",
            ".ppt",
            ".pptx",
            ".pdf",
            ".wps",
            ".pps",
            ".txt"
    };

    //zip use suffix
    public static final String[] mZipSuffix = new String[]{
            ".rar",
            ".tar",
            ".zip",
            ".gz"
    };

    //apk use suffix
    public static final String[] mApkSuffix = new String[]{
            ".apk"
    };

    //video use suffix add by ycm 20161222
    public static String[] mVideoSuffix = new String[]{
            ".asf", ". asx", ".avi",
            ".fvi", ".lsf", ".lsx",
            ".mng", ".mov", ".movie",
            ".mp4", ".mpe", ".mpeg",
            ".mpg", ".mpg4", ".pvx",
            ".qt", ".rv", ".vdo",
            ".viv", ".vivo", ".wm",
            ".wmx", ".wv", ".wvx"
    };

    //audio use suffix add by ycm 20161222
    //fix bug 7661 by zya 20170102
    public static String[] mVoiceSuffix = new String[]{
            ".aif", ".aifc", ".aiff", ".snd", ".smz", ".smd",
            ".als", ".au", ".awb", ".rm", ".ram", ".ra",
            ".es", ".esl", ".imy", ".qcp", ".pae", ".pac",
            ".it", ".xmz", ".xm", ".nsnd", ".mpga", ".mp3",
            ".wmv", ".wma", ".wax", ".mp2", ".mod", ".mio",
            ".wav", ".vql", ".vqf", ".midi", ".mid", ".mdz",
            ".vqe", ".vox", ".vib", ".ma5", ".ma3", ".ma2",
            ".ult", ".tsi", ".stm", ".ma1", ".m3url", ".m3u",
            ".s3z", ".s3m", ".rpm", ".m15", ".itz", ".rmf",
            ".rmvb", ".rmm",".aac",".3gpp",".amr",".flac",
            ".ota",".kar",".rtttl",".xmf",".mxmf",".m4a",
            ".sid",".gsm",".mka",".pls",".sd2"
    };//end by zya


    //image use mimetype
    public static final String[] mImageArgs = new String[]{
            "image/bmp",
            "image/png",
            "image/jpeg",
            "image/jpeg",
            "image/gif"
    };

    //document select args use mimetype
    public static final String[] mDocMimeArgs = new String[]{
            "application/msword",  //doc
            "application/vnd.ms-excel", //xls
            "application/mspowerpoint", //ppt
            "application/pdf",  //pdf
            "application/vnd.ms-works",
            "application/vnd.ms-powerpoint",
            "text/plain" //txt
    };

    //document select args use suffix
    public static final String[] mDocSuffixArgs = new String[]{
            ".docx",
            ".pptx",
            ".xlsx"
    };


    //zip use mimetype
    public static final String[] mZipMimeArgs = new String[]{
            "application/zip",
    };

    //zip use mimetype
    public static final String[] mZipSuffixArgs = new String[]{
            ".tar",
            ".rar",
            ".gz",
    };

    //im share use mimetype
    public static String[] mFilterType = new String[]{
            "audio/",
            "video/",
            "text/x-vcard",
            "application/",
            "web/",
            "*/*"
    };

    public static String[] mShareType = new String[]{
            "audio/",
            "text/x-vcard",
            "application/",
            "*/*"
    };

    private static final String[] mImageSelectionArgs = new String[mImageSuffix.length];
    private static final String[] mDocSelectionArgs = new String[mDocSuffix.length];
    private static final String[] mZipSelectionArgs = new String[mZipSuffix.length];
    private static final String[] mApkSelectionArgs = new String[mApkSuffix.length];

    /**
     * 文档查询条件
     *
     * @return 图片文件查询条件
     */
    private static String buildImageSelection() {
        StringBuilder selection = new StringBuilder();
        for (int i = 0; i < mImageArgs.length; i++) {
            if (i > 0) {
                selection.append(" OR ");
            }
            selection.append(MediaStore.Images.ImageColumns.MIME_TYPE + " =? ");
            mImageSelectionArgs[i] = mImageArgs[i];
        }
        return selection.toString();
    }

    /**
     * 文档查询条件
     *
     * @return 文档文件查询条件
     */
    private static String buildDocSelection() {
        StringBuilder selection = new StringBuilder();
        //按照mime_type查询
        for (int i = 0; i < mDocMimeArgs.length; i++) {
            if (i > 0) {
                selection.append(" OR ");
            }
            selection.append(MediaStore.Files.FileColumns.MIME_TYPE + " =?");
            mDocSelectionArgs[i] = mDocMimeArgs[i];
        }
        //按照后缀查
        int size = mDocMimeArgs.length;
        for (int i = 0; i < mDocSuffixArgs.length; i++) {
            selection.append(" OR ");
            selection.append(MediaStore.Files.FileColumns.DATA + " LIKE ? ");
            mDocSelectionArgs[size + i] = "%" + mDocSuffixArgs[i];
        }
        return selection.toString();
    }

    /**
     * 压缩包查询条件
     *
     * @return 压缩包文件查询条件
     */
    private static String buildZipSelection() {
        StringBuilder selection = new StringBuilder();
        for (int i = 0; i < mZipMimeArgs.length; i++) {
            if (i > 0) {
                selection.append(" OR ");
            }
            selection.append(MediaStore.Files.FileColumns.MIME_TYPE + " =? ");
            mZipSelectionArgs[i] = mZipMimeArgs[i];
        }
        int size = mZipMimeArgs.length;
        for (int i = 0; i < mZipSuffixArgs.length; i++) {
            selection.append(" OR ");
            selection.append(MediaStore.Files.FileColumns.DATA + " LIKE ? ");
            mZipSelectionArgs[size + i] = "%" + mZipSuffixArgs[i];
        }
        return selection.toString();
    }

    /**
     * apk查询条件
     *
     * @return apk应用程序查询条件
     */
    private static String buildApkSelection() {
        StringBuilder selection = new StringBuilder();
        for (int i = 0; i < mApkSuffix.length; i++) {
            if (i > 0) {
                selection.append(" OR ");
            }
            selection.append(MediaStore.Files.FileColumns.DATA + " LIKE ? ");
            mApkSelectionArgs[i] = "%" + mApkSuffix[i] ;
        }
        return selection.toString();
    }

    /**
     * 是否为图片类型
     *
     * @param path 图片文件绝对路径
     * @return true 图片支持格式 false不支持格式
     */
    private static boolean isImageType(String path) {
        /*String regularExpression = "(?i)^\\w+\\.(doc|docx|xls|xlsx|ppt|pptx|pdf|wps|pps|ppsx)$";
        return path.matches(regularExpression);*/
        return matchStringInArray(path, mImageSuffix);
    }

    /**
     * 是否为视频类型
     *
     * @param path
     * @return
     */
    private static boolean isVideoType(String path) {
        return matchStringInArray(path, mVideoSuffix);
    }

    /**
     * 是否为音频类型
     *
     * @param path
     * @return
     */
    private static boolean isAudioType(String path) {
        return matchStringInArray(path, mVoiceSuffix);
    }

    /**
     * 是否为文档类型
     *
     * @param path 文档文件绝对路径
     * @return
     */
    private static boolean isDocType(String path) {
        return matchStringInArray(path, mDocSuffix);
    }

    /**
     * 是否为压缩文件类型
     *
     * @param path
     * @return
     */
    private static boolean isZipType(String path) {
        return matchStringInArray(path, mZipSuffix);
    }

    /**
     * 是否为apk类型
     *
     * @param path
     * @return
     */
    private static boolean isApkType(String path) {
        return matchStringInArray(path, mApkSuffix);
    }

    /**
     * 文件类型匹配
     *
     * @param path
     * @param arry
     * @return
     */
    private static boolean matchStringInArray(String path, String[] arry) {
        String pathLower = path.toLowerCase(Locale.getDefault());
        for (String suffix : arry) {
            if (pathLower.endsWith(suffix) && (!pathLower.equals(suffix))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据文件路径获取文件后缀名
     *
     * @param filepath
     * @return
     */
    public static String getSuffixFromFilepath(String filepath) {
        int pos = filepath.lastIndexOf('.');
        if (pos != -1) {
            return filepath.substring(pos + 1, filepath.length());
        }
        return null;
    }

    /**
     * 获取文档类型后缀名
     *
     * @return
     */
    private static String getDocFileTypeFromFilepath(String filepath) {
        String docFileType = null;
        String suffix = getSuffixFromFilepath(filepath);
        if (suffix != null) {
            suffix = suffix.toLowerCase(Locale.getDefault());
            if ("docx".equals(suffix) || "doc".equals(suffix)) {
                docFileType = "WORD";
            } else if ("xlsx".equals(suffix) || "xls".equals(suffix)) {
                docFileType = "EXCEL";
            } else if ("ppsx".equals(suffix) || "pps".equals(suffix)) {
                docFileType = "PPS";
            } else if ("pptx".equals(suffix) || "ppt".equals(suffix)) {
                docFileType = "PPT";
            } else if ("pdf".equals(suffix) || "wps".equals(suffix)) {
                docFileType = "PDF";
            } else {
                docFileType = suffix.toUpperCase(Locale.getDefault());
            }
        }
        return docFileType;
    }

    /**
     * 根据文件路径名获取文档类型
     *
     * @param filepath
     * @return
     */
    private static int getDocFileType(String filepath) {
        int fileType = 0;
        String suffix = getSuffixFromFilepath(filepath);
        if (suffix != null) {
            suffix = suffix.toLowerCase(Locale.getDefault());
            if ("docx".equals(suffix) || "doc".equals(suffix)) {
                fileType = ConstDef.TYPE_WORD;
            } else if ("xlsx".equals(suffix) || "xls".equals(suffix)) {
                fileType = ConstDef.TYPE_EXCEL;
            } else if ("ppsx".equals(suffix) || "pps".equals(suffix) ||
                    "pdf".equals(suffix) || "wps".equals(suffix)) {
                fileType = ConstDef.TYPE_PDF;
            } else if ("pptx".equals(suffix) || "ppt".equals(suffix)) {
                fileType = ConstDef.TYPE_PPT;
            } else if ("txt".equals(suffix)) {
                fileType = ConstDef.TYPE_TXT;
            } else {
                fileType = ConstDef.TYPE_OTHER;
            }
        }
        return fileType;
    }

    /**
     * 根据文件绝对路径，获取文件类型
     *
     * @param filePath
     * @return
     */
    public static int getFileTypeFromFilepath(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return 0;
        }
        if (isImageType(filePath)) {
            return ConstDef.TYPE_PHOTO;
        } else if (isVideoType(filePath)) {
            return ConstDef.TYPE_VIDEO;
        } else if (isAudioType(filePath)) {
            return ConstDef.TYPE_VOICE;
        } else if (isDocType(filePath)) {
            return getDocFileType(filePath);
        } else if (isApkType(filePath)) {
            return ConstDef.TYPE_APK;
        } else if (isZipType(filePath)) {
            return ConstDef.TYPE_ZIP;
        }
        return 0;
    }

    /**
     * 查询本地图片信息列表
     *
     * @return 图片列表
     */
    public static List<LocalPictureInfo> queryLocalPictures() {

        //查询内容 fixed bug 5675 by lll. end
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = ActomaController.getApp().getContentResolver();
        //查询内容
        String[] projection = {MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT};
        //排序
        String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " desc";
        //检索
        Cursor cursor = mContentResolver.query(mImageUri, projection, null, null, sortOrder);
        List<LocalPictureInfo> localPicInfoList = new ArrayList<>();
        if (cursor != null) { // modified by ycm for lint 2017/02/16
            while (cursor.moveToNext()) {
                // 获取图片大小
                int width = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
                int height = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                int size = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                if (width > 0 && height > 0) {
                    LocalPictureInfo info = new LocalPictureInfo(name, path, size);
                    localPicInfoList.add(info);
                } else {
                    //[S]lll@xdja.com 2016-10-026 add. fix bug 5311. review by liming.
                    //如果图库读取大小为0，则实地加载图片（该方法比较耗时）
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(path, options);
                    if (options.outWidth > 0 && options.outHeight > 0) {
                        LocalPictureInfo info = new LocalPictureInfo(name, path, size);
                        localPicInfoList.add(info);
                    }
                    //[E]lll@xdja.com 2016-10-026 add. fix bug 5311. review by liming.
                }
            }
            cursor.close();
        }

        return localPicInfoList;
    }

    /**
     * 查询本地媒体库中图片文件, 按照图片类型进行分类
     *
     * @return 图片文件列表
     */
    public static Map<String, List<LocalFileInfo>> queryLocalImages() {
        Map<String, List<LocalFileInfo>> imageMap = new HashMap<>();

        // 扫描外部设备中的照片
        String projection[] = {
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.SIZE,
                MediaStore.Images.ImageColumns.DATE_MODIFIED,
                MediaStore.Images.ImageColumns.MIME_TYPE};
        String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " desc";
        Cursor cursor = ActomaController.getApp().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, buildImageSelection(),
                mImageSelectionArgs, sortOrder);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                long fileSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.SIZE));
                long dateTime = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED)) * 1000;
                if (fileSize <= 0 || TextUtils.isEmpty(filePath)) {
                    continue;
                }

                // 获取该文件的父路径名
                File parentFile = new File(filePath).getParentFile();
                if (parentFile == null) {
                    continue;
                }
                //按照图片文件夹分类
                String parentName = parentFile.getName();
                if (TextUtils.isEmpty(parentName)) {
                    continue;
                }
                LocalFileInfo localFileInfo = new LocalFileInfo(getNameFromFilepath(filePath),
                        filePath, fileSize, dateTime, ConstDef.TYPE_PHOTO);
                if (imageMap.containsKey(parentName)) {
                    imageMap.get(parentName).add(localFileInfo);
                } else {
                    List<LocalFileInfo> fileInfoList = new ArrayList<>();
                    fileInfoList.add(localFileInfo);
                    imageMap.put(parentName, fileInfoList);
                }
            }
            cursor.close();
        }
        return imageMap;
    }

    /**
     * 查询媒体库中所有音频文件, 按照文件夹来分类
     *
     * @return 音频文件列表
     */
    public static Map<String, List<LocalFileInfo>> queryLocalAudios() {
        Map<String, List<LocalFileInfo>> audioMap = new HashMap<>();
        // 扫描外部设备中的音频
        String str[] = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DATE_MODIFIED};
        Cursor cursor = ActomaController.getApp().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, str, null,
                null, MediaStore.Audio.Media.DATE_MODIFIED + " desc");
        if (cursor != null) {
            while (cursor.moveToNext()) {

                String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                long fileSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                long dateTime = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)) * 1000;
                if (fileSize <= 0 || TextUtils.isEmpty(filePath)) {
                    continue;
                }
                LocalFileInfo localFileInfo = new LocalFileInfo(getNameFromFilepath(filePath),
                        filePath, fileSize, dateTime, ConstDef.TYPE_VOICE);

                //音频文件路径
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                // 获取该文件的父路径名
                File parentFile = new File(path).getParentFile();
                if (parentFile == null) {
                    continue;
                }
                String parentName = parentFile.getName();
                if (TextUtils.isEmpty(parentName)) {
                    continue;
                }
                if (audioMap.containsKey(parentName)) {
                    audioMap.get(parentName).add(localFileInfo);
                } else {
                    List<LocalFileInfo> fileInfoList = new ArrayList<>();
                    fileInfoList.add(localFileInfo);
                    audioMap.put(parentName, fileInfoList);
                }
            }
            cursor.close();
        }
        return audioMap;
    }

    /**
     * 查询媒体库中所有视频文件,按照文件夹进行分裂
     *
     * @return 视频文件列表
     */
    public static Map<String, List<LocalFileInfo>> queryLocalVideos() {
        Map<String, List<LocalFileInfo>> videoMap = new HashMap<>();
        // 扫描外部设备中的视频
        String str[] = {MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_MODIFIED,
                MediaStore.Video.Media.DESCRIPTION};
        Cursor cursor = ActomaController.getApp().getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, str, null,
                null, MediaStore.Video.Media.DATE_MODIFIED + " desc");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                long fileSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                long dateTime = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED)) * 1000;
                String desc = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DESCRIPTION));
                if (fileSize <= 0 || TextUtils.isEmpty(filePath)) {
                    continue;
                }
                // 获取该文件的父路径名
                File parentFile = new File(filePath).getParentFile();
                if (parentFile == null) {
                    continue;
                }
                LocalFileInfo localFileInfo = new LocalFileInfo(getNameFromFilepath(filePath),
                        filePath, fileSize, dateTime, ConstDef.TYPE_VIDEO);
                localFileInfo.setExtraInfo(desc);
                String parentName = parentFile.getName();
                if (videoMap.containsKey(parentName)) {
                    videoMap.get(parentName).add(localFileInfo);
                } else {
                    List<LocalFileInfo> fileInfoList = new ArrayList<>();
                    fileInfoList.add(localFileInfo);
                    videoMap.put(parentName, fileInfoList);
                }
            }
            cursor.close();
        }
        return videoMap;
    }

    /**
     * 查询本地所有应用列表，包括安装和未安装
     *
     * @return 应用列表
     */
    public static Map<String, List<LocalFileInfo>> queryLocalDocuments() {

        Map<String, List<LocalFileInfo>> documentMap = new HashMap<>();
        // 扫描外部设备中的文档
        String projection[] = {
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.DATE_MODIFIED,
                MediaStore.Files.FileColumns.MIME_TYPE};
        //排序
        String sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED + " desc";
        Cursor cursor = ActomaController.getApp().getContentResolver().query(
                MediaStore.Files.getContentUri("external"), projection, buildDocSelection(),
                mDocSelectionArgs, sortOrder);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                long fileSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                long dateTime = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED)) * 1000;
                if (fileSize <= 0 || TextUtils.isEmpty(filePath)) {
                    continue;
                }
                //按照类型来进行分类
                String fileType = getDocFileTypeFromFilepath(filePath);
                if (TextUtils.isEmpty(fileType)) {
                    continue;
                }
                LocalFileInfo localFileInfo = new LocalFileInfo(getNameFromFilepath(filePath),
                        filePath, fileSize, dateTime, getDocFileType(filePath));
                if (documentMap.containsKey(fileType)) {
                    documentMap.get(fileType).add(localFileInfo);
                } else {
                    List<LocalFileInfo> fileInfoList = new ArrayList<>();
                    fileInfoList.add(localFileInfo);
                    documentMap.put(fileType, fileInfoList);
                }
            }
            cursor.close();
        }
        return documentMap;
    }


    /**
     * 查询本地所有应用列表，包括安装和未安装
     *
     * @return 应用列表
     */
    public static Map<String, List<LocalFileInfo>> queryLocalApplication() {

        Map<String, List<LocalFileInfo>> applicationMap = new HashMap<>();
        // 扫描外部设备中的
        String str[] = {
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.DATE_MODIFIED};

        Cursor cursor = ActomaController.getApp().getContentResolver().query(
                MediaStore.Files.getContentUri("external"), str, buildApkSelection(),
                mApkSelectionArgs, MediaStore.Files.FileColumns.DATE_MODIFIED + " desc");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                //文件名称
                String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                long fileSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                long dateTime = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED)) * 1000;
                if (fileSize <= 0 || TextUtils.isEmpty(filePath)) {
                    continue;
                }
                // 获取该文件的父路径名
                File parentFile = new File(filePath).getParentFile();
                if (parentFile == null) {
                    continue;
                }
                String fileType = getSuffixFromFilepath(filePath);
                if (TextUtils.isEmpty(fileType)) {
                    continue;
                }
                //获取文件父目录名称
                String parentName = parentFile.getName();
                //apk按照已安装和未安装来区分
                LocalFileInfo localFileInfo = new LocalFileInfo(getNameFromFilepath(filePath),
                        filePath, fileSize, dateTime, ConstDef.TYPE_APK);
                if (applicationMap.containsKey(parentName)) {
                    applicationMap.get(parentName).add(localFileInfo);
                } else {
                    List<LocalFileInfo> fileInfoList = new ArrayList<>();
                    fileInfoList.add(localFileInfo);
                    applicationMap.put(parentName, fileInfoList);
                }
            }
            cursor.close();
        }
        return applicationMap;
    }

    /**
     * 查询本地所有应用列表，包括安装和未安装
     *
     * @return 应用列表
     */
    public static Map<String, List<LocalFileInfo>> queryOtherFiles() {
        Map<String, List<LocalFileInfo>> othersMap = new HashMap<>();
        // 扫描外部设备中的视频
        String projection[] = {
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.DATE_MODIFIED,
                MediaStore.Files.FileColumns.MIME_TYPE};
        //排序
        String sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED + " desc ";
        Cursor cursor = ActomaController.getApp().getContentResolver().query(
                MediaStore.Files.getContentUri("external"), projection, buildZipSelection(),
                mZipSelectionArgs, sortOrder);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                //文件名称
                String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                long fileSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                long dateTime = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED)) * 1000;
                if (fileSize <= 0 || TextUtils.isEmpty(filePath)) {
                    continue;
                }
                //按照类型来进行分类
                String fileType = getSuffixFromFilepath(filePath);
                if (TextUtils.isEmpty(fileType)) {
                    continue;
                } else {
                    fileType = fileType.toUpperCase(Locale.getDefault());
                }
                LocalFileInfo localFileInfo = new LocalFileInfo(getNameFromFilepath(filePath),
                        filePath, fileSize, dateTime, ConstDef.TYPE_ZIP);
                if (othersMap.containsKey(fileType)) {
                    othersMap.get(fileType).add(localFileInfo);
                } else {
                    List<LocalFileInfo> fileInfoList = new ArrayList<>();
                    fileInfoList.add(localFileInfo);
                    othersMap.put(fileType, fileInfoList);
                }
            }
            cursor.close();
        }
        return othersMap;
    }

    /**
     * 根据IM消息，获取最近聊天文件
     *
     * @param imMessageList
     * @return
     */
    public static Map<String, List<LocalFileInfo>> getLastFileList(List<IMMessage> imMessageList) {

        Map<String, List<LocalFileInfo>> lastFileMap = new HashMap<>();
        for (IMMessage file : imMessageList) {
            if (file != null && file.isFileIMMessage()) {
                //文件排序时间闲标识
                String mapKey = String.valueOf(IMFileUtils.getTimeLineType(file.getIMMessageTime()));
                IMFileBody fileBody = (IMFileBody) file.getMessageBody();
                LocalFileInfo localFileInfo = new LocalFileInfo(fileBody.getDisplayName(),
                        fileBody.getLocalPath(),
                        fileBody.getFileSize(),
                        file.getIMMessageTime(),
                        fileBody.getType());
                if (lastFileMap.containsKey(mapKey)) {
                    lastFileMap.get(mapKey).add(localFileInfo);
                } else {
                    List<LocalFileInfo> localFileList = new ArrayList<>();
                    localFileList.add(localFileInfo);
                    lastFileMap.put(mapKey, localFileList);
                }
            }
        }
        return lastFileMap;

    }

    /**
     * 根据文件路径获取文件名称
     *
     * @param filepath
     * @return
     */
    private static String getNameFromFilepath(String filepath) {
        int pos = filepath.lastIndexOf(File.separator);
        if (pos != -1) {
            return filepath.substring(pos + 1);
        }
        return "";
    }

    //*****************************************************************
    //*           以下为最新文件相关信息
    //*****************************************************************

    /**
     * 一天24小时时间毫秒数
     */
    private static final long ONE_DAY_TIME_MILLIS = 24 * 60 * 60 * 1000;

    public static final int TIME_WITHIN_TODAY = 0;  //今天内
    public static final int TIME_YESTERDAY = 1;     //昨天
    public static final int TIME_WITHIN_WEEK = 2;   //一周内
    public static final int TIME_WITHIN_MONTH = 3;  //一个月内
    public static final int TIME_WITHIN_MONTH_AGO = 4;  //一个月前

    /**
     * 根据时间，获取不同的时间线
     *
     * @param millis 时间毫秒
     * @return 时间线类型
     */
    public static int getTimeLineType(long millis) {
        int timeLineType;
        if (millis > getTimeToMorning()) {
            //一天内，今天0点到现在
            timeLineType = TIME_WITHIN_TODAY;

        } else if (millis > getTimeToYesterdayMorning()) {
            //昨天0点到现在时间
            timeLineType = TIME_YESTERDAY;

        } else if (millis > getTimeToWeekMorning()) {
            //一周内，本周一0点到现在
            timeLineType = TIME_WITHIN_WEEK;

        } else if (millis > getTimeToMonthMorning()) {
            //一个月内，本月一号0点到现在
            timeLineType = TIME_WITHIN_MONTH;
        } else {
            //上个月以及以前
            timeLineType = TIME_WITHIN_MONTH_AGO;
        }
        return timeLineType;
    }

    /**
     * 获得当天0点时间
     *
     * @return 时间毫秒
     */
    private static long getTimeToMorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 获得当天24点时间
     *
     * @return 时间毫秒
     */
    private static long getTimeToNight() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 获取两天内时间，到昨天0点时间
     *
     * @return 时间毫秒
     */
    private static long getTimeToYesterdayMorning() {
        return getTimeToMorning() - ONE_DAY_TIME_MILLIS;
    }

    /**
     * 获得本周一0点时间
     *
     * @return 时间毫秒
     */
    private static long getTimeToWeekMorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTimeInMillis();
    }

    /**
     * 获得本周日24点时间
     *
     * @return 时间毫秒
     */
    public static long getTimeTOWeekNight() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime().getTime() + 7 * ONE_DAY_TIME_MILLIS;
    }

    /**
     * 获得本月第一天0点时间
     *
     * @return 时间毫秒
     */
    private static long getTimeToMonthMorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTimeInMillis();
    }

    /**
     * 获得本月最后一天24点时间
     *
     * @return 时间毫秒
     */
    private static long getTimeToMonthNight() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 24);
        return cal.getTimeInMillis();
    }

    /**
     * 获得本月第一天0点时间
     *
     * @return 时间毫秒
     */
    private static long getTimeToTwoMonthMorning() {
        return getTimeToMonthMorning() - 30 * ONE_DAY_TIME_MILLIS;
    }

    // add by ycm for share file 20161222 [start]
    /**
     * 根据uri查找文件
     *
     * @param context 上下文
     * @param fileUri 文件Uri
     * @return LocalFileInfo 文件信息
     */
    public static LocalFileInfo queryLocalFiles(final Context context, Uri fileUri) {
        if (fileUri == null) {
            return null;
        }

        final String scheme = fileUri.getScheme();
        if (scheme == null) {
            return null;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT
                && DocumentsContract.isDocumentUri(context, fileUri)) {

            if (isExternalStorageDocument(fileUri)) {
                String docId = DocumentsContract.getDocumentId(fileUri);
                String[] split = docId.split(":");
                if (split.length < 2) {
                    return null;
                }
                String type = split[0];
                if (ConstDef.PRIMARY.equalsIgnoreCase(type)) {
                    return getLocalFileInfo(Environment.getExternalStorageDirectory() + File.separator + split[1]);
                }
            }

            if (isDownloadsDocument(fileUri)) {
                String id = DocumentsContract.getDocumentId(fileUri);
                String downloadUri = "content://downloads/public_downloads";
                Uri contentUri = ContentUris.withAppendedId(Uri.parse(downloadUri), Long.valueOf(id));
                return getLocalFileInfoByUri(context, contentUri, null, null);
            }

            if (isMediaDocument(fileUri)) {
                String docId = DocumentsContract.getDocumentId(fileUri);
                String[] split = docId.split(":");
                if (split.length < 2) {
                    return null;
                }
                String type = split[0];
                Uri contentUri = null;
                switch (type) {
                    case ConstDef.IMAGE:
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        break;
                    case ConstDef.VIDEO:
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        break;
                    case ConstDef.AUDIO:
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        break;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getLocalFileInfoByUri(context, contentUri, selection, selectionArgs);
            }
            return null;
        }

        if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            if (isVcardUri(fileUri)) {
                return getLocalFileInfo(getVCardFilePath(context, fileUri));
            }
            return getLocalFileInfoByUri(context, fileUri, null, null);
        }

        if (ConstDef.FILE.equalsIgnoreCase(fileUri.getScheme())) {
            if (isGooglePhotosUri(fileUri)) {
                return getLocalFileInfo(fileUri.getLastPathSegment());
            }
            return getLocalFileInfo(fileUri.getPath());
        }

        return null;
    }

    /**
     * 获取小视频信息
     * @param context 上下文
     * @param fileUri 视频uri
     * @return 视频信息
     */
    public static VideoFileInfo getVideoInfo(final Context context, Uri fileUri) {

        if (fileUri == null) {
            return null;
        }
        String scheme = fileUri.getScheme();
        if (TextUtils.isEmpty(scheme)) {
            return null;
        }
        switch (scheme) {
            case ContentResolver.SCHEME_FILE:
                String filePath = fileUri.getPath();
                return getVideoInfoByPath(context, filePath);
            case ContentResolver.SCHEME_CONTENT:
                return getVideoInfoByContentUri(context, fileUri);
            default:
                return null;
        }
    }

    /**
     * 保存短视频第一帧缩略图
     *
     * @param path 短视频地址
     */
    private static File saveFirstFrame(String path) {
        MediaMetadataRetriever media = null;
        File firstFrameFile;
        try {
            media = new MediaMetadataRetriever();
            media.setDataSource(path);
        } catch (IllegalArgumentException e) {
            LogUtil.getUtils().d("保存短视频第一帧失败，短视频路径错误");
        }
        if (media == null) {
            return null;
        }
        Bitmap bitmap = media.getFrameAtTime(0);
        String fileName = UUID.randomUUID().toString();
        BufferedOutputStream bos = null;
        firstFrameFile = new File(ToolUtil.getVideoPath(), fileName);

        try {
            bos = new BufferedOutputStream(new FileOutputStream(firstFrameFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, bos);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return firstFrameFile;
    }

    /**
     * 根据content uri
     * @param context 上下文
     * @param fileUri 文件uri
     * @return 小视频文件信息
     */
    private static VideoFileInfo getVideoInfoByContentUri(final Context context, Uri fileUri) {
            Cursor cursor = null;
            VideoFileInfo videoFileInfo = null;
            try {
                cursor = context.getContentResolver().query(fileUri,
                        new String[]{
                                MediaStore.Video.Media.DATA,
                                MediaStore.Video.Media.SIZE,
                                MediaStore.Video.Media.DATE_MODIFIED,
                                MediaStore.Video.Media.DURATION,
                                MediaStore.Video.Media.DISPLAY_NAME}, null, null, null);
                if (cursor == null) {
                    return null;
                }
                if (cursor.moveToFirst()) {
                    String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                    File firstFile = saveFirstFrame(filePath);
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                    long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                    int time = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))/1000;
                    videoFileInfo = new VideoFileInfo();
                    if (firstFile != null && firstFile.exists()) {
                        videoFileInfo.setFilePath(firstFile.getPath());
                        videoFileInfo.setFileName(firstFile.getName());
                        videoFileInfo.setFileSize(firstFile.length());
                    } else {
                        return null;
                    }
                    videoFileInfo.setAmountOfTime(time);
                    videoFileInfo.setSuffix(ConstDef.VIDEO_SUFFIX);
                    videoFileInfo.setFileType(ConstDef.TYPE_VIDEO);
                    videoFileInfo.setVideoSize(size);
                    FileExtraInfo extraInfo = new FileExtraInfo();
                    extraInfo.setRawFileUrl(filePath);
                    extraInfo.setRawFileName(name);
                    extraInfo.setRawFileSize(size);
                    videoFileInfo.setExtraInfo(extraInfo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            return videoFileInfo;
    }

    /**
     * 根据路径获取小视频信息
     * @param context 上下文
     * @param filePath 文件路径
     * @return 小视频信息
     */
    private static VideoFileInfo getVideoInfoByPath(final Context context, String filePath) {
        Uri baseUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = null;
        VideoFileInfo videoFileInfo = null;
        try {
            cursor = context.getContentResolver().query(baseUri,
                    new String[]{
                            MediaStore.Video.Media.SIZE,
                            MediaStore.Video.Media.DATE_MODIFIED,
                            MediaStore.Video.Media.DURATION,
                            MediaStore.Video.Media.DISPLAY_NAME},
                    MediaStore.Video.Media.DATA + "=?",
                    new String[]{filePath}, null);
            if (cursor == null) {
                return null;
            }
            if (cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                int time = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))/1000;
                videoFileInfo = new VideoFileInfo();
                File firstFile = saveFirstFrame(filePath);
                if (firstFile != null && firstFile.exists()) {
                    videoFileInfo.setFilePath(firstFile.getPath());
                    videoFileInfo.setFileName(firstFile.getName());
                    videoFileInfo.setFileSize(firstFile.length());
                } else {
                    return null;
                }
                videoFileInfo.setAmountOfTime(time);
                videoFileInfo.setSuffix(ConstDef.VIDEO_SUFFIX);
                videoFileInfo.setFileType(ConstDef.TYPE_VIDEO);
                videoFileInfo.setVideoSize(size);
                FileExtraInfo extraInfo = new FileExtraInfo();
                extraInfo.setRawFileUrl(filePath);
                extraInfo.setRawFileName(name);
                extraInfo.setRawFileSize(size);
                videoFileInfo.setExtraInfo(extraInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return videoFileInfo;
    }

    /**
     * 根据uri查找文件
     * @param context 上下文
     * @param fileUri 文件Uri
     * @return LocalFileInfo 文件信息
     */
    public static LocalFileInfo getLocalFileInfoByUri(final Context context,
                                                      Uri fileUri,
                                                      String selection,
                                                      String[] selectionArgs) {
        String path = null;
        long size = 0;
        long date = 0;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(fileUri, null, selection, selectionArgs, null);
            if (cursor == null) {
                return null;
            }

            if (!cursor.moveToFirst()) {
                return null;
            }

            int pathIndex = cursor.getColumnIndex(ConstDef.FILE_DATA_FIELD);
            int sizeIndex = cursor.getColumnIndex(ConstDef.FILE_SIZE_FIELD);
            int dateIndex = cursor.getColumnIndex(ConstDef.FILE_MODIFIED_FIELD);
            path = pathIndex == -1 ? null : cursor.getString(pathIndex);
            size = sizeIndex == -1 ? 0L : cursor.getLong(sizeIndex);
            date = dateIndex == -1 ? 0L : cursor.getLong(dateIndex);
            //add by ycm :ACE Android系统中的下载文件中分享出来的Uri，查询不到“_data”的数据，故使用特殊情况对待2017/02/09[start]
            if (TextUtils.isEmpty(path)) {// 如果获取的路径为空，则判断是否属于下面的情况
                if (!fileUri.toString().contains("android.providers.downloads")) {
                    return null;
                }
                int displayIndex = cursor.getColumnIndex(ConstDef.FILE_DISPLAY_FIELD);
                String displayName = displayIndex == -1 ? null : cursor.getString(displayIndex);
                if (TextUtils.isEmpty(displayName)) {
                    return null;
                }
                path = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + File.separator + Environment.DIRECTORY_DOWNLOADS
                        + File.separator + displayName;
                return getLocalFileInfo(path);
                //add by ycm :ACE Android系统中的下载文件中分享出来的Uri，查询不到“_data”的数据，故使用特殊情况对待2017/02/09[end]
            }

        } catch (Exception | NoSuchFieldError e) {
            LogUtil.getUtils("share file").e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        if (TextUtils.isEmpty(path) || size == 0) {
            return null;
        }
        return initLocalFileInfo(path, size, date);
    }

    /**
     * 根据路径获取文件信息
     * @param path 路径
     * @return 文件信息
     */
    public static LocalFileInfo getLocalFileInfo(String path) {
        long size;
        long date;
        if (TextUtils.isEmpty(path)) {
            return null;
        }

        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        size = file.length();
        date = file.lastModified();
        return initLocalFileInfo(path, size, date);
    }

    /**
     * 初始化LocalFileInfo
     * @param path 路径
     * @param size 大小
     * @param date 修改日期
     * @return LocalFileInfo
     */
    private static LocalFileInfo initLocalFileInfo(@NonNull String path, long size, long date) {
        int fileType = ConstDef.TYPE_OTHER;
        if (isImageType(path)) {
            fileType = ConstDef.TYPE_PHOTO;
        } else if (isDocType(path)) {
            fileType = IMFileUtils.getDocFileType(path);
        } else if (isZipType(path)) {
            fileType = ConstDef.TYPE_ZIP;
        } else if (isApkType(path)) {
            fileType = ConstDef.TYPE_APK;
        } else if (isVideoFileType(path)) {
            fileType = ConstDef.TYPE_VIDEO;
        } else if (isAudioFileType(path)) {
            fileType = ConstDef.TYPE_VOICE;
        }
        return new LocalFileInfo(getNameFromFilepath(path), path, size, date, fileType);
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    private static boolean isVcardUri(Uri uri) {
        String path = ContactsContract.Contacts.CONTENT_VCARD_URI.getAuthority();
        return TextUtils.equals(path, uri.getAuthority());
    }

    /**
     * 判断是否是video
     *
     * @param path
     * @return
     */
    public static boolean isVideoFileType(String path) {// modified by ycm for lint 2017/02/16
        return !(path == null || TextUtils.isEmpty(path)) && matchStringInArray(path, mVideoSuffix);
    }

    /**
     * 判断是否是audio
     *
     * @param path
     * @return
     */
    public static boolean isAudioFileType(String path) {// modified by ycm for lint 2017/02/16
        return !(path == null || TextUtils.isEmpty(path)) && matchStringInArray(path, mVoiceSuffix);
    }

    /**
     * 分享类型为支持的类型时统一为文件类型
     * @param intent 内容
     * @return 类型
     */
    public static String unifyFileType(Intent intent) {
        String type = filterTextFile(intent);
        for (String aMShareType : mShareType) { // modified by ycm for lint 2017/02/16
            if (type != null && type.startsWith(aMShareType)) {
                type = ConstDef.FILE_SHARE_TYPE;
                break;
            }
        }
        return type;
    }

    /**
     * 过滤文件类型
     * @param intent 含分享内容的Intent
     * @return 文件类型
     */
    public static String filterFileType(Intent intent) {// add by ycm for bug 8194 20170117
        String type = filterTextFile(intent);
        if (!TextUtils.isEmpty(type)) {
            for (String aMShareType : mFilterType) {// modified by ycm for lint 2017/02/16
                if (type.startsWith(aMShareType)) {
                    type = ConstDef.FILE_SHARE_TYPE;
                    break;
                }
            }
        }
        //过滤网页类型的分享
        return filterWebType(type, intent);
    }

    /**
     * 过滤文本文件类型
     * @param intent 分享内容
     * @return 文件类型
     */
    private static String filterTextFile(Intent intent) {
        String type = intent.getType();
        if (type.startsWith(ConstDef.TEXT_SHARE_TYPE)) {
            Uri textUri = intent.getData();
            Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (isObjectNotEmpty(textUri) || isObjectNotEmpty(uri)) {
                type = ConstDef.FILE_SHARE_TYPE;
            } else {
                ClipData shareClip = intent.getClipData();
                if (shareClip != null) {
                    ClipData.Item item = shareClip.getItemAt(0);
                    if (item != null) {
                        Uri fileuri = item.getUri();
                        if (fileuri != null) {
                            type = ConstDef.FILE_SHARE_TYPE;
                        }
                    }
                }
            }
        }

        return type;
    }

    /**
     * 过滤网页类型消息
     * @param type 类型
     * @param intent 含分享内容
     * @return 类型
     */
    private static String filterWebType(String type, Intent intent) {
        ShareInfo shareInfo = ShareUtils.getShareInfo(intent);
        if (shareInfo.getShareType() == ShareInfo.SHARE_WEB) {
            return ConstDef.FILE_SHARE_TYPE;
        }
        return type;
    }

    private static boolean isObjectNotEmpty(Object o) {

       return o != null && !o.toString().isEmpty();
    }

    /**
     * 导出Vcard文件并获取路径
     * @param context 上下文
     * @param contactUri 联系人uri
     * @return Vcard路径
     */
    private static String getVCardFilePath(Context context, Uri contactUri) {
        if (contactUri == null) {
            return null;
        }

        ContentResolver cr = context.getContentResolver();
        Cursor cursor = null;
        try {
            cursor = cr.query(contactUri, null, null, null, null);
            if (cursor == null || !cursor.moveToFirst()) {
                return null;
            }

            int disnameIndex = cursor.getColumnIndex(ConstDef.FILE_DISPLAY_FIELD);
            String disname = disnameIndex != -1 ? cursor.getString(disnameIndex) : null;

            if (TextUtils.isEmpty(disname)) {
                return null;
            }

            String shareDirectory = ToolUtil.getShareDirectory();
            File parentFile = new File(shareDirectory);
            if (!parentFile.exists()) {
                parentFile.mkdir();
            }
            String path = shareDirectory + disname;
            createVCard(context, path, contactUri);
            return path;
        } catch (Exception e) {
            LogUtil.getUtils("createVCard").e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 生成VCard文件
     * @param context 上下文
     * @param path 路径
     * @param contactUri 联系人uri
     */
    private static void createVCard(Context context, String path, Uri contactUri) {
        try {
            FileOutputStream fos  = new FileOutputStream(path);
            byte[] data = new byte[1024];
            AssetFileDescriptor afd = context.getContentResolver().openAssetFileDescriptor(contactUri, "r");
            if (afd != null) {
                FileInputStream fs = afd.createInputStream();
                int len;
                while ((len = fs.read(data)) != -1) {
                    fos.write(data, 0, len);
                }
                fos.close();
                fs.close();
            }
        } catch (IOException e ) {
            e.printStackTrace();
        }
    }
    // add by ycm for share file 20161222 [start]
    /**
     * 通过uri获取图片路径
     * @param context 上下文
     * @param uri     图片uri
     * @return 路径
     */
    public static String getImagePathFormUri(final Context context, Uri uri) {
        String path = null;
        if (uri == null) {
            return null;
        }
        final String scheme = uri.getScheme();
        if (scheme == null) {
            return null;
        }
        if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, null, null, null, null);
                if (cursor == null || !cursor.moveToFirst()) {
                    return null;
                }
                int pathIndex = cursor.getColumnIndex(ConstDef.FILE_DATA_FIELD);
                path = pathIndex == -1 ? null : cursor.getString(pathIndex);
                if (TextUtils.isEmpty(path)) {
                    int displayIndex = cursor.getColumnIndex(ConstDef.FILE_DISPLAY_FIELD);
                    String displayName = displayIndex == -1 ? null : cursor.getString(displayIndex);
                    return getSpecialImagePath(uri, displayName);//modified by ycm for bug 8429 2017/03/10
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else if (scheme.equals(ContentResolver.SCHEME_FILE)) {
            path = uri.toString().replace("file://", "");
        }

        return path;
    }

    /**
     * 获取特殊的图片路径，比如系统下载中的图片，华为mate9联系人二维码
     * @param imageUri 图片路径
     * @param displayName 图片名称
     * @return 图片路径
     */
    private static String getSpecialImagePath(Uri imageUri, String displayName) {
        String authority = imageUri.getAuthority();
        switch (authority) {
            case CONTACT_FILES_AUTHORITY:
                return getContactFilesPath(displayName);
            case DOWNLOAD_FILES_AUTHORITY:
                return getDownLoadImagePath(displayName);
            default:
                return null;
        }
    }

    /**
     * 获取华为手机联系人分享二维码的路径
     * @param displayName 二维码图名称
     * @return 路径
     */
    private static String getContactFilesPath(String displayName) {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + CONTACT_FILES_PATH + displayName;
    }

    /**
     * 获取download apk中的图片分享路径
     * @param displayName 图片名称
     * @return 图片路径
     */
    private static String getDownLoadImagePath(String displayName) {
        if (TextUtils.isEmpty(displayName)) {
            return null;
        }
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                + Environment.DIRECTORY_DOWNLOADS + File.separator + displayName;
    }
}
