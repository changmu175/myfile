package com.xdja.imsdk.callback;

import com.xdja.imsdk.model.IMMessage;
import com.xdja.imsdk.model.IMSession;

import java.util.List;

/**
 * 项目名称：ImSdk            <br>
 * 类描述  ：                 <br>
 * 创建时间：2016/11/16 15:07  <br>
 * 修改记录：                 <br>
 *
 * @author liming@xdja.com    <br>
 * @version V1.1.7            <br>
 */
public interface CallbackFunction {
	
	/**
	* 收到新消息，要求上层处理                                        <br>
	*  此接口应该全局监听。新消息到来回调，用来振铃、响铃、刷新状态栏使用
	* @param session 收到的消息所属于的会话，会话中没有最后一条消息的内容
	* @param messageList 收到的消息体列表
	* @return  1： 处理<br>
    *          0： 未处理
	*/
	int NewIMMessageCome(IMSession session, List<IMMessage> messageList);
	
	/**
	* 此接口监听Sdk运行状态                                           <br>
	* @param code 状态码 ，表示Sdk运行状态码，有变化则回调。            <br>
	*            如：推送状态变化，Sdk初始化完成等
	* @return 1： 处理                                             <br>
    *        0： 未处理
	* @see com.xdja.imsdk.constant.StateCode
	*/
	int ImSdkStateChange(int code);

	/**
	 * 检测应用版本号
	 * @return int
     */
	int CheckVersion(String account);
}
