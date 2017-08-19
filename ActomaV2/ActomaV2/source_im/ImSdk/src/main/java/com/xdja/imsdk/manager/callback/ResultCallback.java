package com.xdja.imsdk.manager.callback;

import com.xdja.imsdk.http.bean.ImResultConnectBean;
import com.xdja.imsdk.http.bean.MsgBean;

import java.util.List;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：  收发消息处理结果回调                           <br>
 * 创建时间：2016/11/27 下午6:56                           <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public interface ResultCallback {
    /**
     * 处理拉取到的逆序消息完成后回调SDK消息处理任务的回调接口
     * @param msgList 收到的MsgBean的列表
     */
    void ReceiveMessage(List<MsgBean> msgList);

    /**
     * 通知发送状态消息回调接口
     */
    void SendState();

}
