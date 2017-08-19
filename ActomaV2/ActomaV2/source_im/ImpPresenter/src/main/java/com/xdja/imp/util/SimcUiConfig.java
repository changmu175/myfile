package com.xdja.imp.util;

import android.os.Environment;

/**
 * Created by xrj on 2015/8/9.
 */
public class SimcUiConfig {
    /**
     * 工程的包名
     */
    public static final String packageName = "com.xdja.imp";// TODO: 2017/1/11 修改包名

    /**
     * SD卡路径
     */
    private static final String SDCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();

    /**
     * 缓存文件目录 /sdcard/Actoma+/XdjaIm/
     */
    public static final String cacheFileDir = SDCardPath + "/" + "Actoma+" + "/" + "XdjaIm" + "/";

    /**
     * 图片文件保存路径
     */
    public static final String savePicFileDir = "ActomaImage/";

    /**
     * 图片文件保存前缀
     */
    public static final String LOCAL_PIC_PREFIX = "imageexport";

    /**
     * 短视频文件保存路径
     */
    public static final String saveVideoFileDir = "ActomaVideo/";
    /**
     * 短视频文件保存前缀
     */
    public static final String LOCAL_VIDEO_PREFIX = "videoexport";

}
