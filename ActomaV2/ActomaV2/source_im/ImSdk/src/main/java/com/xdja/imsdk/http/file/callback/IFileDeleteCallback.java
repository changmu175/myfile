package com.xdja.imsdk.http.file.callback;

import com.xdja.imsdk.http.file.FileEntry;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：                                               <br>
 * 创建时间：2016/11/27 下午5:33                              <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public interface IFileDeleteCallback {
    /**
     * 文件删除成功
     */
    void deleteSucess();
    /**
     * 文件删除失败
     */
    void deleteFailed(int code , FileEntry entry);
}
