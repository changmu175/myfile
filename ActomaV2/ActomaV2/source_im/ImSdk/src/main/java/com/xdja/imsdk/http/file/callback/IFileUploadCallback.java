package com.xdja.imsdk.http.file.callback;

import com.xdja.imsdk.http.file.FileEntry;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：                                               <br>
 * 创建时间：2016/11/27 下午5:42                              <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public interface IFileUploadCallback {
    /**
     * 文件上传进度，上传进度更新达到设置的最小百分比的时候回调该方法通知上层上传进度
     *
     * @param percent  文件上传进度百分比
     * @param entry 需要上传文件的信息
     */
    void uploadFileProgressUpdate(int percent, FileEntry entry);

    /**
     * 文件上传完成时回调上层
     *
     * @param entry 需要上传文件的信息
     */
    void uploadFileFinish(FileEntry entry);

    /**
     * 文件上传过程中出错时回调上层
     *
     * @param code     文件上传出错错误码
     * @param entry 需要上传文件的信息
     */
    void uploadFileError(int code, FileEntry entry);

    /**
     * 文件上传过程中，网络发生变化
     * @param code 状态码
     * @param entry 上传文件的信息
     */
    void uploadFileNetChanged(int code, FileEntry entry);

    /**
     * 文件上传暂停时回调上层
     *
     * @param entry 需要暂停上传文件的信息
     */
    void uploadFilePause(FileEntry entry);

    /**
     * 文件开始上传时回调上层
     *
     * @param entry 需要上传文件的信息
     */
    void uploadFileStart(FileEntry entry);
}
