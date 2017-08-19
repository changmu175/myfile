package com.xdja.imsdk.manager.callback;

import com.xdja.imsdk.constant.ImSdkFileConstant.FileType;
import com.xdja.imsdk.db.wrapper.MessageWrapper;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：                                 <br>
 * 创建时间：2016/12/12 18:49                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public interface FileCallback {
    /**
     * 发送文件文本消息回调接口
     * @param id 消息id
     */
    void SendFileText(long id);

    /**
     * 解密文件回调接口
     * @param message 文件
     * @param type 类型
     * @see FileType
     */
    void DecryptFile(MessageWrapper message, FileType type);
}
