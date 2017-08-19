package com.xdja.imsdk.manager.process;

import com.xdja.imsdk.constant.internal.Constant.SentType;
import com.xdja.imsdk.db.wrapper.MessageWrapper;
import com.xdja.imsdk.model.internal.IMState;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：  发送消息队列节点                               <br>
 * 创建时间：2016/11/27 下午6:46                           <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public class SendOutNode {
    private SentType type;                                     //消息类型

    private MessageWrapper message;                            //普通消息
    private IMState state;                                     //状态消息

    public SendOutNode(SentType type) {
        this.type = type;
    }

    public SentType getType() {
        return type;
    }

    public void setType(SentType type) {
        this.type = type;
    }

    public MessageWrapper getMessage() {
        return message;
    }

    public void setMessage(MessageWrapper message) {
        this.message = message;
    }

    public IMState getState() {
        return state;
    }

    public void setState(IMState state) {
        this.state = state;
    }
}
