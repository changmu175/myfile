package com.xdja.imsdk.callback;

import com.xdja.imsdk.constant.ChangeAction;
import com.xdja.imsdk.model.IMMessage;
import com.xdja.imsdk.model.IMSession;

import java.util.List;
/**
 * 项目名称：ImSdk            <br>
 * 类描述  ：消息列表变化接口    <br>
 * 创建时间：2016/11/16 15:07  <br>
 * 修改记录：                 <br>
 *
 * @author liming@xdja.com    <br>
 * @version V1.1.7            <br>
 */
public interface IMMessageCallback {

	/**
	 * 消息列表变化接口，即收到新消息或状态消息<br>
	 * @param session IM消息所要添加的会话，会话中没有最后一条消息的内容。无会话模式此参数为 null
	 * @param messageList 消息列表
	 * @param action 回调原因，说明：<br>
	 *               1、 新增消息， action = ACT_ADD 将message 列表增加到对应的会话中<br>
	 *               2、 状态消息， action = ACT_SC 列表中只有一条需要更新状态的消息，更新状态
	 * @return 1: 已处理<br>
	 *         0: 未处理
	 */
	int IMMessageListChange(IMSession session, List<IMMessage> messageList, ChangeAction action);
}
