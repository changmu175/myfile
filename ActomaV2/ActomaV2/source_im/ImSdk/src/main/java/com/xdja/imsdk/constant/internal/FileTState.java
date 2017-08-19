package com.xdja.imsdk.constant.internal;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：文件传输状态                       <br>
 *          对应file_msg                     <br>
 *              hd_file,                    <br>
 *              raw_file的file_state         <br>
 * 创建时间：2016/12/28 14:07                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class FileTState {
    public static final int UP_NON = 10;                            //文件未开始上传
    public static final int ENCRYPT_FAIL = 11;                      //文件加密失败
    public static final int ENCRYPT_SUCCESS = 12;                   //文件加密成功
    public static final int UP_FID_FAIL = 13;                       //文件获取FID失败
    public static final int UP_FID = 14;                            //文件获取FID成功
    public static final int UP_PAUSE = 15;                          //文件上传暂停
    public static final int UP_LOADING = 16;                        //文件正在上传
    public static final int UP_FAIL = 17;                           //文件上传失败
    public static final int UP_DONE = 18;                           //文件上传完成

    public static final int DOWN_NON = 20;                          //文件未开始下载
    public static final int DOWN_PAUSE = 21;                        //文件下载暂停
    public static final int DOWN_LOADING = 22;                      //文件正在下载
    public static final int DOWN_FAIL = 23;                         //文件下载失败
    public static final int DOWN_DONE = 24;                         //文件下载完成，未开始解密
    public static final int DECRYPT_FAIL = 25;                      //文件下载完成，解密失败
    public static final int DECRYPT_SUCCESS = 26;                   //文件解密成功
}
