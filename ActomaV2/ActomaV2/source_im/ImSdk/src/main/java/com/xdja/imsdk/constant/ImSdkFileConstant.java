package com.xdja.imsdk.constant;

/**
 * 项目名称：ImSdk            <br>
 * 类描述  ：ImSdk文件处理配置  <br>
 * 创建时间：2016/11/16 17:03  <br>
 * 修改记录：                 <br>
 *
 * @author liming@xdja.com    <br>
 * @version V1.1.7            <br>
 */
public class ImSdkFileConstant {

    /***************************************
     * ******* ImSdk文件的类型 *****  *
     ***************************************/
    /**
     * 普通类型文件
     */
    public static final int FILE_NORMAL = 0;

    /**
     * 语音类型文件
     */
    public static final int FILE_VOICE = 1;

    /**
     * 视频类型文件
     */
    public static final int FILE_VIDEO = 2;

    /**
     * 图片类型文件
     */
    public static final int FILE_IMAGE = 3;

    /**
     * 未知文件类型
     */
    public static final int FILE_UNKNOWN = 4;


    public static final int FILE_WEB = 5;

    /***************************************
     * ******* ImSdk操作的文件类型 *****  *
     ***************************************/

    /**
     * ImSdk操作的文件类型
     * 操作的文件是消息列表展示的文件
     * 操作的文件是高清缩略图
     * 操作的文件是原始文件
     */
    public enum FileType {
        IS_SHOW,
        IS_HD,
        IS_RAW
    }

    /**
     * ImSdk操作的文件状态
     * 未开始
     * 完成
     * 进行中
     * 暂停
     * 失败
     */
    public enum FileState {
        INACTIVE,
        DONE,
        LOADING,
        PAUSE,
        FAIL
    }

    /***************************************
     * ******* ImSdk保存文件参数 *****  *
     ***************************************/
    /**
     *  ImSdk保存文件的根文件夹名
     */
    public static final String PARENT_FILE_PATH = "XdjaIm";

    public static final String VOICE_PATH = "Voice";
    public static final String IMAGE_PATH = "Image";
    public static final String NORMAL_PATH = "Normal";
    public static final String VIDEO_PATH = "Video";
    public static final String WEB_PATH = "Web";
    public static final String UNKNOWN_PATH = "Unknown";

    /**
     * 接收到的文件解密后保存缓存目录：/sdcard/AppName/FileRec/
     */
    public static final String FILE_REC = "FileRec";

    /**
     *  ImSdk生成的缩略图的文件前缀名
     */
    public static final String THUMBNAIL_FILE_PREFIX = "th_";

    /**
     *  ImSdk生成的缩略图的文件后缀名
     */
    public static final String THUMBNAIL_FILE_SUFFIX = ".jpeg";
}
