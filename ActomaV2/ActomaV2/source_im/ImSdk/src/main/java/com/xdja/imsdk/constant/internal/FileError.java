package com.xdja.imsdk.constant.internal;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：文件收发错误码                     <br>
 * 创建时间：2016/12/5 11:46                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class FileError {
    /**
     * 文件收发获取Fid出错
     */
    public static final int FID_ERR = Constant.CODE_BEGIN + 30;

    /**
     * 文件收发IO出错
     */
    public static final int IO_ERR = Constant.CODE_BEGIN + 31;

    /**
     * 文件收发网络请求出错
     */
    public static final int HTTP_ERR = Constant.CODE_BEGIN + 32;

    /**
     * 参数出错
     */
    public static final int PARAM_ERR = Constant.CODE_BEGIN + 34;

    /**
     * 文件上传出错
     */
    public static final int UPLOAD_ERR = Constant.CODE_BEGIN + 35;

    /**
     * 文件下载出错
     */
    public static final int DOWNLOAD_ERR = Constant.CODE_BEGIN + 36;

    /**
     * 请求服务器返回错误
     */
    public static final int RES_ERR = Constant.CODE_BEGIN + 37;

    /**
     * 文件下载超时错误
     */
    public static final int TIMEOUT_ERR = Constant.CODE_BEGIN + 38;

}
