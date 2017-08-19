package com.xdja.imsdk.manager.callback;

import com.xdja.imsdk.model.IMMessage;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：  闪信销毁回调                                   <br>
 * 创建时间：2016/11/27 下午6:51                           <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public interface BombCallback {
    /**
     * 闪信消息状态变为已销毁
     * @param message 已销毁的消息
     */
    void BombDestroy(IMMessage message);
}
