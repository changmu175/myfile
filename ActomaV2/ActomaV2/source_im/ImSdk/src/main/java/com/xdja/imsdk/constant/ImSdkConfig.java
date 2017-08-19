package com.xdja.imsdk.constant;

import com.xdja.imsdk.util.FileUtils;


/**
 * 项目名称：ImSdk            <br>
 * 类描述  ：ImSdk配置         <br>
 * 创建时间：2016/11/16 15:29  <br>
 * 修改记录：                 <br>
 *
 * @author liming@xdja.com    <br>
 * @version V1.1.7            <br>
 */
public class ImSdkConfig {
    /***************************************
     * ********* ImSdk配置项key值 *******  *
     ***************************************/
    /**
     * Im Server地址
     */
    public static final String K_SERVER = "server";

    /**
     * 文件服务器IP
     */
    public static final String K_FILE_ADDR = "file_ad";

    /**
     * 文件服务器端口
     */
    public static final String K_FILE_PORT = "file_port";

    /**
     * 是否需要状态消息，默认开启
     */
    public static final String K_STATE = "state";

    /**
     * 会话展示的最后一条消息是否需要状态，默认关闭
     */
    public static final String K_CHANGE ="change";

    /**
     * 是否需要会话，默认开启
     */
    public static final String K_SESSION = "session";

    /**
     * ImSdk发送文件大小限制
     */
    public static final String K_SIZE = "size";

    /**
     * 是否需要ImSdk生成缩略图，默认关闭
     */
    public static final String K_THU = "thumb";

    /**
     * ImSdk生成的缩略图的宽度，默认为160                                                </br>
     */
    public static final String K_THU_W = "thu_w";


    /**
     * ImSdk生成的缩略图的高度，默认为120                                                </br>
     */
    public static final String K_THU_H = "thu_h";

    /**
     * ImSdk生成的高清缩略图的宽度，默认为480                                                </br>
     */
    public static final String K_HD_W = "hd_w";

    /**
     * ImSdk生成的高清缩略图的高度，默认为480
     */
    public static final String K_HD_H = "hd_h";

    /**
     * ImSdk生成的缩略图的保存路径，默认为T卡根目录
     */
    public static final String K_PATH = "path";

    /**
     * ImSdk接收到的解密后的文件存放路径
     */
    public static final String K_REC = "rec";

    /**
     * ImSdk文件传输进度更新百分比控制，默认为每百分之一更新
     */
    public static final String K_PER = "per";

    /**
     * ImSdk漫游消息周期设置，单位为秒，默认为259200秒   </br>
     * 为0即关闭漫游
     */
    public static final String K_ROAM = "roam";

    /**
     * ImSdk同步消息周期设置，单位为秒，默认为259200秒  </br>
     * 为0即关闭同步
     */
    public static final String K_SYNC = "sync";

    /**
     * 和后台同步后的时间差值
     */
    public static final String K_DIFF = "diff";

    /**
     * 是否与后台进行https链接，取值 true 或 false
     */
    public static final String K_HTTPS = "https";

    /**
     * 使用Https链接，需要的证书Id
     */
    public static final String K_STORE = "keystore";

    /**
     * 使用Https链接，使用证书的密码
     */
    public static final String K_CERT = "cert";

    /**
     * 上层是否需要进行加密，默认开启
     */
    public static final String K_ENCRYPT = "encrypt";

    /**
     * 是否需要闪信功能，默认开启
     */
    public static final String K_BOMB = "bomb";

    /**
     * 语音，图片缩略图是否需要预先下载，默认开启
     */
    public static final String K_PRELOAD = "preload";

    /**
     * 是否直接使用原图发送。
     * 值为0时直接使用原图，默认为200k，可配置，最大为1M.
     */
    public static final String K_ORIGIN = "origin";

    /***************************************
     * ********* ImSdk配置项key值 *******  *
     ***************************************/

    /**
     * Im Server 值
     */
    public static final String V_SERVER = "https://im.pyis.safecenter.com:8469/webrelay/api";

    public static final String V_FILE = "dfs.pyis.safecenter.com";

    public static final String V_FILE_PORT = "80";

    public static final String V_CONFIG_0 = "0";

    public static final String V_CONFIG_1 = "1";

    public static final String V_3_DAY = "259200";

    public static final String V_EMPTY = "";

    public static final String V_TRUE = "true";

    public static final String V_FALSE = "false";

    /**
     * ImSdk发送文件最大大小，包括图片，视频，文件
     */
    public static final String V_SIZE = "30";

    /**
     * ImSdk生成的缩略图的默认宽度
     */
    public static final String V_THU_W = "160";

    /**
     * ImSdk生成的缩略图的默认高度
     */
    public static final String V_THU_H = "120";

    /**
     * ImSdk生成的缩略图的默认宽度
     */
    public static final String V_HD_W = "480";

    /**
     * ImSdk生成的缩略图的默认高度
     */
    public static final String V_HD_H = "480";


    public static final String V_ORIGIN = "204800";

    /**
     * ImSdk保存文件的默认路径（包括缩略图和收到的文件等）
     * /XdjaIm/
     */
    public static final String FILE_PATH = FileUtils.getDefaultParent();

    /**
     * ImSdk保存接收到的文件默认路径
     * /sdcard/XdjaIm/FileRec/
     */
    public static final String FILE_REC = FileUtils.getDefaultRec();


}
