package com.xdja.imsdk.util;

import android.os.Environment;
import android.text.TextUtils;

import com.xdja.imsdk.constant.ImSdkFileConstant;
import com.xdja.imsdk.constant.internal.Constant;
import com.xdja.imsdk.manager.ImSdkConfigManager;


import java.io.File;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：                                 <br>
 * 创建时间：2017/1/2 16:44                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class FileUtils {
    private static int RANDOM = 2;
    /**
     * ImSdk本地缓存根目录，为默认路径。如果上层未配置应用根目录时，使用此目录
     * @return /sdcard/XdjaIm/
     */
    public static String getDefaultParent() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator +
                        ImSdkFileConstant.PARENT_FILE_PATH +
                        File.separator;
    }

    /**
     * ImSdk缓存接收到的文件默认目录。如果上层未配置应用根目录时，使用此目录
     * @return /sdcard/XdjaIm/FileRec/
     */
    public static String getDefaultRec() {
        return getDefaultParent() +
                ImSdkFileConstant.FILE_REC +
                File.separator;
    }

    /**
     * ImSdk语音缓存目录，上层保存发送的语音文件使用
     * @return /sdcard/AppName/XdjaIm/.MD5(account)/Voice/random1/random2/
     */
    public static String getVoicePath() {
        String path = ImSdkConfigManager.getInstance().getPath() +
                ImSdkFileConstant.VOICE_PATH +
                File.separator +
                RandomStringUtils.randomAlphanumeric(RANDOM) +
                File.separator +
                RandomStringUtils.randomAlphanumeric(RANDOM) +
                File.separator;

        File file = new File(path);
        if (!file.exists()) {
            if (file.mkdirs()) {
                return path;
            }
        } else {
            return path;
        }
        return getDefaultParent();
    }

    /**
     * ImSdk图片缓存目录，上层保存发送的图片使用，生成了缩略图和高清缩略图
     * @return /sdcard/AppName/XdjaIm/.MD5(account)/Image/random1/random2/
     */
    public static String getImagePath() {
        String path = ImSdkConfigManager.getInstance().getPath() +
                ImSdkFileConstant.IMAGE_PATH +
                File.separator +
                RandomStringUtils.randomAlphanumeric(RANDOM) +
                File.separator +
                RandomStringUtils.randomAlphanumeric(RANDOM) +
                File.separator;

        File file = new File(path);
        if (!file.exists()) {
            if (file.mkdirs()) {
                return path;
            }
        } else {
            return path;
        }
        return getDefaultParent();
    }

    /**
     * ImSdk短视频缓存目录，上层保存发送的视频文件使用
     * @return /sdcard/AppName/XdjaIm/.MD5(account)/Video/random1/random2/
     */
    public static String getVideoPath() {
        String path = ImSdkConfigManager.getInstance().getPath() +
                ImSdkFileConstant.VIDEO_PATH +
                File.separator +
                RandomStringUtils.randomAlphanumeric(RANDOM) +
                File.separator +
                RandomStringUtils.randomAlphanumeric(RANDOM) +
                File.separator;

        File file = new File(path);
        if (!file.exists()) {
            if (file.mkdirs()) {
                return path;
            }
        } else {
            return path;
        }
        return getDefaultParent();
    }

    /**
     * ImSdk网页图片保存目录
     * @return /sdcard/AppName/XdjaIm/.MD5(account)/Web
     */
    public static String getWebFilePath() {
        String path = ImSdkConfigManager.getInstance().getPath() +
                ImSdkFileConstant.WEB_PATH +
                File.separator +
                RandomStringUtils.randomAlphanumeric(RANDOM) +
                File.separator +
                RandomStringUtils.randomAlphanumeric(RANDOM) +
                File.separator;

        File file = new File(path);
        if (!file.exists()) {
            if (file.mkdirs()) {
                return path;
            }
        } else {
            return path;
        }
        return getDefaultParent();
    }
	
    /**
     * ImSdk短视频保存目录
     * @return /sdcard/AppName/XdjaIm/.MD5(account)/Video
     */
    public static String getVideoToPhonePath() {
        String path = ImSdkConfigManager.getInstance().getPath() +
                ImSdkFileConstant.VIDEO_PATH ;

        File file = new File(path);
        if (!file.exists()) {
            if (file.mkdirs()) {
                return path;
            }
        } else {
            return path;
        }
        return getDefaultParent();
    }
    /**
     * ImSdk未知文件缓存目录
     * @return /sdcard/AppName/XdjaIm/.MD5(account)/Unknown/
     */
    public static String getUnknownPath() {
        String path = ImSdkConfigManager.getInstance().getPath() +
                ImSdkFileConstant.UNKNOWN_PATH +
                File.separator;

        File file = new File(path);
        if (!file.exists()) {
            if (file.mkdirs()) {
                return path;
            }
        } else {
            return path;
        }
        return getDefaultParent();
    }

    /**
     * 根据文件类型和名称获取文件缓存路径
     * @param type 文件类型 (Voice, Image, Video, Normal)
     * @param name 文件原名称
     * @see ImSdkFileConstant
     * @return path
     * /sdcard/AppName/XdjaIm/.MD5(account)/type/random1/random2/
     */
    public static String getCachePath(int type, String name) {
        String path = ImSdkConfigManager.getInstance().getPath();

        switch (type) {
            case ImSdkFileConstant.FILE_VOICE:
                path = path + ImSdkFileConstant.VOICE_PATH;
                break;

            case ImSdkFileConstant.FILE_IMAGE:
                path = path + ImSdkFileConstant.IMAGE_PATH;
                break;

            case ImSdkFileConstant.FILE_NORMAL:
                path = path + ImSdkFileConstant.NORMAL_PATH;
                break;

            case ImSdkFileConstant.FILE_VIDEO:
                path = path + ImSdkFileConstant.VIDEO_PATH;
                break;
            case ImSdkFileConstant.FILE_WEB:
                path = path + ImSdkFileConstant.WEB_PATH;
                break;
            default:
                path = getUnknownPath();
        }

        path = path + File.separator +
                RandomStringUtils.randomAlphanumeric(RANDOM) +
                File.separator +
                RandomStringUtils.randomAlphanumeric(RANDOM) +
                File.separator;

        File file = new File(path);
        if (!file.exists()) {
            if (file.mkdirs()) {
                path = path + name;
            }
        } else {
            path = path + name;
        }

        return renameExist(path) + Constant.ENCRYPT_SUFFIX;
    }

    /**
     * 解密后文件路径
     * @param name name
     * @return path
     */
    public static String getFileRecPath(String name) {
        String path = ImSdkConfigManager.getInstance().getRecPath();

        File file = new File(path);
        if (!file.exists()) {
            if (file.mkdirs()) {
                return path + name;
            }
        } else {
            path = path + name;
        }
        return renameExist(path);
    }

    /**
     * 加上dat后缀
     * @param name name
     * @return String
     */
    public static String addDat(String name) {
        return name + Constant.ENCRYPT_SUFFIX;
    }

    /**
     * 去掉dat后缀
     * @param name name
     * @return String
     */
    public static String subDat(String name) {
        return ToolUtils.subString(name, Constant.ENCRYPT_SUFFIX_SUB);
    }

    /**
     * 处理已存在的文件
     * 即本地已有路径所指向的文件或者是文件加上.dat后也存在，则需要对文件进行重命名
     * @param path 文件路径
     * @return 重命名后的文件路径
     */
    public static String renameExist(String path) {
        String encryptPath = path + Constant.ENCRYPT_SUFFIX;
        File file = new File(path);
        File encryptFile = new File(encryptPath);

        String suffix = ToolUtils.getLastString(path, ".");
        String name = ToolUtils.subString(path, suffix);

        int count = 1;
        while (file.exists() || encryptFile.exists()) {
            if (TextUtils.isEmpty(suffix)) {
                path = name + "(" + count + ")";
                encryptPath = path + Constant.ENCRYPT_SUFFIX;
            } else {
                path = name + "(" + count + ")." + suffix;
                encryptPath = path + Constant.ENCRYPT_SUFFIX;
            }

            file = new File(path);
            encryptFile = new File(encryptPath);
            count = count + 1;
        }

        return path;
    }

}
