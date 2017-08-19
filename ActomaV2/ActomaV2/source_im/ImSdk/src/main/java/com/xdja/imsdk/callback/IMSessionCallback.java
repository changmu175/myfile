package com.xdja.imsdk.callback;

import com.xdja.imsdk.constant.ChangeAction;
import com.xdja.imsdk.model.IMSession;
/**
 * 项目名称：ImSdk            <br>
 * 类描述  ：会话列表变化接口   <br>
 * 创建时间：2016/11/16 15:07  <br>
 * 修改记录：                 <br>
 *
 * @author liming@xdja.com    <br>
 * @version V1.1.7            <br>
 */
public interface IMSessionCallback {
	
	/**
	 * 会话列表变化回调 <br/>
	 * @param ims 新增或更新的IMSession
	 * @param action 回调原因，说明：<br>
	 *               1、 新增会话，action = ACT_ADD 将ims增加到列表  <br>
	 *               2、 会话更新，action = ACT_RF 最后收到新消息，意味着最后一条消息变更  <br>
	 *               3、 最后一条消息状态变化，action = ACT_SC 通知到SessionList <br>
	 * @return 1：已处理<br>
	 *         0：未处理
	 * @see IMSession
	 */
	int IMSessionListChange(IMSession ims, ChangeAction action);
}
