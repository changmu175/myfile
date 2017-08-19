package com.xdja.imsdk.http.file.callback;

import com.xdja.imsdk.http.file.FileEntry;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：                                               <br>
 * 创建时间：2016/11/27 下午5:34                              <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public interface IFileDownloadCallback {
    /**
     * 文件下载进度回调
     *
     * @param percent  下载进度百分比
     * @param entry 下载文件的信息
     */
    void downloadFileProgressUpdate(int percent, FileEntry entry);

    /**
     * 文件下载完成回调
     *
     * @param entry 下载文件的信息
     */
    void downloadFileFinish(FileEntry entry);

    /**
     * 文件下载错误回调
     *
     * @param code     错误码
     * @param entry 下载文件的信息
     */
    void downloadFileError(int code, FileEntry entry);

    /**
     * 文件下载网络发生变化
     * @param code 状态码
     * @param entry 下载的文件信息
     */
    void downloadNetChanged(int code, FileEntry entry);

    /**
     * 文件下载暂停回调
     *
     * @param entry 暂停下载的文件信息
     */
    void downloadFilePause(FileEntry entry);
}
