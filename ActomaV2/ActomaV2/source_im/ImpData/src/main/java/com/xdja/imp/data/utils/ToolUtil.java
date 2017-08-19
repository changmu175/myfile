package com.xdja.imp.data.utils;

import android.os.Environment;

import com.xdja.imsdk.constant.ImSdkFileConstant;
import com.xdja.imsdk.util.FileSizeUtils;
import com.xdja.imsdk.util.FileUtils;
import com.xdja.imsdk.util.ToolUtils;

import java.io.File;

/**
 * 项目名称：ActomaV2            <br>
 * 类描述  ：工具类               <br>
 * 创建时间：2016/11/16 16:57    <br>
 * 修改记录：                    <br>
 *
 * @author liming@xdja.com    <br>
 * @version                   <br>
 */
public class ToolUtil {
    /**
     * 获取会话标识
     * @param id 对方账号
     * @param type 会话类型
     * @return tag
     */
    public static String getSessionTag(String id, int type) {
        return ToolUtils.getSessionTag(id, type);
    }

    /**
     * 获取分隔符后的字符串
     * @param input input
     * @param separator separator
     * @return String
     */
    public static String getLastString(String input, String separator) {
        return ToolUtils.getLastString(input, separator);
    }

    /**
     * 读取指定路径文件大小
     * @param filePath filePath
     * @return long
     */
    public static long getFileSize(String filePath) {
        return FileSizeUtils.getFileSize(filePath);
    }

    /**
     * 获取应用T卡缓存根目录(/sdcard/Actoma+/XdjaIm/)
     * @return String
     */
    public static String getAppParent() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator +
                "Actoma+" +
                File.separator +
                ImSdkFileConstant.PARENT_FILE_PATH +
                File.separator;
    }

    /**
     * 语音存储目录
     * @return /sdcard/AppName/XdjaIm/.MD5(account)/Voice/random1/random2/
     */
    public static String getVoicePath() {
        return FileUtils.getVoicePath();
    }
	
	/**
     * 网页文件存储目录
     * @return /sdcard/AppName/XdjaIm/.MD5(account)/Web/random1/random2/
     */
    public static String getWebPath() {
        return FileUtils.getWebFilePath();
    }
	
    /**
     * 短视频存储目录
     * @return /sdcard/AppName/XdjaIm/.MD5(account)/Video/random1/random2/
     */
    public static String getVideoPath() {
        return FileUtils.getVideoPath();
    }

    /**
     * 短视频存储目录
     * @return /sdcard/AppName/XdjaIm/ActomaVideo/
     */
    public static String getVideoToPhonePath() {
        return FileUtils.getVideoToPhonePath();
    }


    //add by ycm for share function 20170207 [start]
    /**
     * 获取应用T卡缓存根目录(/sdcard/Actoma+/Share/)
     * @return String
     */
    public static String getShareDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator +
                "Actoma+" +
                File.separator +
                "Share" +
                File.separator;
    }
    //add by ycm for share function 20170207 [end]
}
