package com.xdja.imsdk.impl;

import android.content.Context;

import com.xdja.imsdk.callback.IMFileInfoCallback;
import com.xdja.imsdk.callback.IMMessageCallback;
import com.xdja.imsdk.callback.IMSecurityCallback;
import com.xdja.imsdk.callback.IMSessionCallback;
import com.xdja.imsdk.constant.ImSdkConfig;
import com.xdja.imsdk.exception.ImSdkException;
import com.xdja.imsdk.model.IMFileInfo;
import com.xdja.imsdk.model.IMMessage;
import com.xdja.imsdk.model.IMSession;
import com.xdja.imsdk.model.InitParam;

import java.util.List;
import java.util.Map;

/**
 * 项目名称：ImSdk              <br>
 * 类描述  ：ImSdk接口定义       <br>
 * 创建时间：2016/11/16 15:06   <br>
 * 修改记录：                   <br>
 *
 * @author liming@xdja.com    <br>
 * @version V1.1.7            <br>
 */
public interface ISdkClient {
    /**
     * ImSdk初始化
     * @param initParam 初始化参数
     * @return 0: 调用成功<br>
     *       其他: 调用失败，原因可参考返回结果码
     * @see com.xdja.imsdk.constant.ImSdkResult
     */
    int Init(InitParam initParam);

    /**
     * ImSdk释放
     * @param flag 说明<br>
     *        flag = 1: 应用关闭，释放所有资源,不再接收消息 <br>
     * 		  flag = 0: 应用退出前天，ImSdkService后台运行，继续接收消息
     * @return 0: ImSdk释放成功<br>
     *       其他: 调用失败，原因可参考返回结果码
     * @see com.xdja.imsdk.constant.ImSdkConstant
     * @see com.xdja.imsdk.constant.ImSdkResult
     */
    int Release(int flag);

    /**
     * 和Im server 同步消息
     * @return 0: ImSdk释放成功<br>
     *       其他: 调用失败，原因可参考返回结果码
     * @see com.xdja.imsdk.constant.ImSdkResult
     */
    int SyncMessage();

    /**
     * 配置ImSdk属性
     * @param param 配置项属性键值表
     * @return 0: 配置成功<br>
     *       其他: 配置失败，原因可参考返回结果码
     * @see ImSdkConfig
     */
    int SetConfig(Map<String, String> param);

    /**
     * 获取ImSdk配置项属性
     * @param key 属性的键
     * @return ImSdk配置项key对应的值
     * @throws ImSdkException
     * @see com.xdja.imsdk.constant.ImSdkResult
     * @see ImSdkConfig
     */
    String GetConfig(String key) throws ImSdkException;

    /**
     * 获取会话列表，结果按照时间降序排列返回<br>
     * 如果启用无会话模式，则此接口无数据返回
     * @param begin 获取会话列表的起始位置的会话的会话标识，为空时表示从最新一个会话开始获取
     * @param size 从起始位置开始获取指定数量会话:List&lt;IMSession&gt;<br>
     *             <p>说明<br>
     *             size>0: 往时间小的方向取值，往过去的时间取，按时间降序排列<br>
     *             size<0: 往时间大的方向取值，往向前的时间取，按时间降序排列<br>
     *             size=0: 取当前这条
     *             begin = "", size = 0，表示取所有会话
     *             begin = "", size != 0， 表示从最新一个会话开始取size大小的会话
     *             begin != "", size =0, 表示取begin这一条
     *             begin != "", size != 0，表示取begin开始，size大小的会话
     * @return 符合条件的会话组成的列表，以会话生成的时间为序<br>
     * @throws ImSdkException
     * @see IMSession
     */
    List<IMSession> GetIMSessionList(String begin, int size) throws ImSdkException;

    /**
     * 删除会话，维护会话列表使用<br>
     * 如果启用无会话模式，则此接口调用始终失败
     * @param tags 需要删除的会话标识的列表，可删除多个会话
     * @return 0: 删除会话成功<br>
     *      其他: 删除操作失败，原因可参考常量错误码
     */
    int DeleteIMSession(List<String> tags);

    /**
     * 添加自定义会话
     * @param session 用户自定义会话<br>
     *                <p>session的参数说明：<br>
     *                sessionType值为100，是必填项<br>
     *                imPartner是必填项
     * @return  带有会话所有信息的一个完整的会话
     * @throws ImSdkException
     * @see IMSession
     */
    IMSession IMSessionListAddCust(IMSession session) throws ImSdkException;

    /**
     * 添加自定义消息
     * @param sessionTag  要添加的消息所在的会话标识，类型最后需要为100
     * @param message  需要插入的消息<br>
     *                <p>message的参数说明：<br>
     *                必须字段：消息接收方to, 消息类型type, 消息内容c
     * @return 带有消息所有信息的一个完整的消息
     * @throws ImSdkException
     * @see IMMessage
     * @see IMSession
     */
    IMMessage IMMessageListAddCust(String sessionTag, IMMessage message) throws ImSdkException;

    /**
     * 获取指定会话的消息列表，按照时间升序排列
     * @param sessionTag 消息列表所在的会话标识
     * @param begin 获取消息起始位置
     * @param size 从起始位置开始获取指定数量消息<br>
     *        <p>说明<br>
     *        size>0: 往时间小的方向取值，往过去的时间取，按时间升序排列<br>
     *        size<0: 往时间大的方向取值，往向前的时间取，按时间升序排列<br>
     *        size=0: 取当前这条
     *
     * @return  符合条件的消息组成的列表
     * @throws ImSdkException
     * @see IMMessage
     */
    List<IMMessage> GetIMMessageList(String sessionTag, long begin, int size) throws ImSdkException;

    /**
     * 删除消息
     * @param msgIds 需要删除的消息Id，列表形式可删除多条消息
     * @return 0：删除消息成功   <br>
     *       其他: 删除操作失败，原因可参考常量错误码
     */
    int DeleteIMMessage(List<Long> msgIds);

    /**
     * 获取会话中消息提醒数量
     * @param sessionTag 指定的会话标识
     * @return 大于等于0：会话的消息数量   <br>
     *        其他：操作失败，原因可参考常量错误码
     */
    int GetRemindIMMessageCount(String sessionTag);

    /**
     * 获取所有会话消息提醒总数
     * @return 大于等于0：所有消息的数量   <br>
     *        其他：操作失败，原因可参考常量错误码
     */
    int GetAllRemindIMMessageCount();

    /**
     * 设置指定会话的消息提醒数量
     * @param sessionTag 指定的会话标识
     * @param type 操作类型。0：全部设为已读 1：设为未读
     * @return  0：操作成功 <br>
     *        其他: 操作失败，原因可参考常量错误码
     */
    int SetRemind(String sessionTag, int type);

    /**
     * 清空指定会话中的所有的消息，即删除指定会话中的所有消息，会话保留
     * @param sessionTag 指定的会话标识
     * @return  0：操作成功 <br>
     *        其他: 操作失败，原因可参考常量错误码
     */
    int ClearIMSessionAllIMMessage(String sessionTag);


    /**
     * 清空SDK中所有数据
     * @return 0：操作成功 <br>
     *       其他：操作失败，原因可参考常量错误码
     */
    int ClearAllLocalData();

    /**
     * 获取会话中的图片列表
     * @param sessionTag  指定的会话标识
     * @param begin  获取图像起始位置
     * @param size 从起始位置开始获取指定数量图片<br>
     *             <p>说明<br>
     *             size>0: 往时间大的方向取值，往现在的时间取<br>
     *             size<0: 往时间小的方向取值，往过去的时间取<br>
     *             size=0: 取当前这条
     * @return  会话中出现过的图片信息，会话中消息时间为序
     * @throws ImSdkException
     * @see IMMessage
     */
    List<IMMessage> GetImageList(String sessionTag, int begin, int size) throws ImSdkException;

    /**
     * 获取会话中的文件列表
     * @param sessionTag 指定的会话标识。为空时取所有文件列表
     * @return 文件消息列表
     * @throws ImSdkException
     * @see IMMessage
     */
    List<IMMessage> GetFileList(String sessionTag) throws ImSdkException;

    /**
     * 根据消息id获取消息
     * @param id 消息id
     * @return 消息
     * @throws ImSdkException
     * @see IMMessage
     */
    IMMessage getIMMessageById(long id) throws ImSdkException;

    /**
     * 发送消息<br>
     * @param message 需要发送的消息<br>
     *        说明：<br>
     *        IMMessage要求必填字段：消息接收方to, 消息类型type, 消息内容c
     * @return 带有消息所有信息的一个完整的消息
     * @throws ImSdkException
     * @see IMMessage
     */
    IMMessage SendIMMessage(IMMessage message) throws ImSdkException;

    /**
     * 暂停文件上传
     * @param fileInfo 当前文件信息
     * @see IMFileInfo
     * @return  0：暂停文件上传调用成功    <br>
     *        其他：暂停失败
     */
    int SendFilePause(IMFileInfo fileInfo);

    /**
     * 重新开始文件上传
     * @param fileInfo 当前文件信息
     * @see IMFileInfo
     * @return  0：恢复文件上传调用成功     <br>
     *        其他：恢复文件上传处理失败
     */
    int SendFileResume(IMFileInfo fileInfo);

    /**
     * 开始文件下载
     * @param fileInfoList 当前文件信息列表
     * @see IMFileInfo
     * @return  0：开始文件下载调用成功   <br>
     *        其他：暂停处理失败
     */
    int ReceiveFileStart(List<IMFileInfo> fileInfoList);

    /**
     * 暂停文件下载
     * @param fileInfo 当前文件信息
     * @see IMFileInfo
     * @return  0：暂停文件下载调用成功   <br>
     *        其他：暂停处理失败
     */
    int ReceiveFilePause(IMFileInfo fileInfo);

    /**
     * 重新开始文件下载
     * @param fileInfo 当前文件信息
     * @see IMFileInfo
     * @return  0：恢复文件下载调用成功    <br>
     *        其他：恢复文件下载处理失败
     */
    int ReceiveFileResume(IMFileInfo fileInfo);

    /**
     * 更新消息状态
     * @param message 需要更新状态的消息
     * @param state  将要更新的状态
     * @return  0：调用成功
     *        其他：调用失败
     * @see IMMessage
     */
    int IMMessageStateChange(IMMessage message, int state);

    /**
     * 消息重发
     * @param messageId  需要重发的消息id
     * @return  0：调用成功
     *        其他：调用失败
     */
    int ResendIMMessage(long messageId);

    /**
     *注册会话变化监听
     *@param context 监听器上下文
     *@param callback 回调接口
     *@see IMSessionCallback
     */
    void RegisterIMSessionChangeListener(Context context, IMSessionCallback callback);

    /**
     *注销会话变化监听器
     *@param context 监听器上下文
     */
    void UnregisterIMSessionChangeListener(Context context);

    /**
     *注册消息变化监听
     *@param context 监听器上下文
     *@param callback 回调接口
     *@see IMMessageCallback
     */
    void RegisterIMMessageChangeListener(Context context, IMMessageCallback callback);

    /**
     *注销消息变化监听器
     *@param context 监听器上下文
     */
    void UnregisterIMMessageChangeListener(Context context);

    /**
     *注册文件操作变化监听
     *@param context 监听器上下文
     *@param callback 回调接口
     *@see IMFileInfoCallback
     */
    void RegisterIMFileInfoChangeListener(Context context, IMFileInfoCallback callback);

    /**
     *注销文件操作监听器
     *@param context 监听器上下文
     */
    void UnregisterIMFileInfoChangeListener(Context context);
}
