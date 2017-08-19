package com.xdja.imsdk.manager.process;

import com.xdja.imsdk.constant.ImSdkFileConstant.FileType;
import com.xdja.imsdk.constant.internal.Constant.ReceiveType;
import com.xdja.imsdk.db.wrapper.MessageWrapper;
import com.xdja.imsdk.http.bean.MsgBean;

import java.util.List;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：                                 <br>
 * 创建时间：2016/12/20 14:04                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class ReceiveNode {
    private ReceiveType type;
    private List<MsgBean> messages;
    private MessageWrapper file;
    private FileType fileType;

    public ReceiveNode(ReceiveType type) {
        this.type = type;
    }

    public ReceiveType getType() {
        return type;
    }

    public void setType(ReceiveType type) {
        this.type = type;
    }

    public List<MsgBean> getMessages() {
        return messages;
    }

    public void setMessages(List<MsgBean> messages) {
        this.messages = messages;
    }

    public MessageWrapper getFile() {
        return file;
    }

    public void setFile(MessageWrapper file) {
        this.file = file;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }
}
