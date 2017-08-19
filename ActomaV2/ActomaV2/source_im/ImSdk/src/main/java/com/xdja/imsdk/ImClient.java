package com.xdja.imsdk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.xdja.imsdk.callback.IMFileInfoCallback;
import com.xdja.imsdk.callback.IMMessageCallback;
import com.xdja.imsdk.callback.IMSessionCallback;
import com.xdja.imsdk.constant.IMSessionType;
import com.xdja.imsdk.constant.ImSdkConfig;
import com.xdja.imsdk.constant.ImSdkConstant;
import com.xdja.imsdk.constant.ImSdkResult;
import com.xdja.imsdk.constant.MsgState;
import com.xdja.imsdk.constant.internal.MsgType;
import com.xdja.imsdk.constant.internal.Constant;
import com.xdja.imsdk.constant.internal.Constant.FileOptType;
import com.xdja.imsdk.exception.ImSdkException;
import com.xdja.imsdk.impl.ISdkClient;
import com.xdja.imsdk.logger.Logger;
import com.xdja.imsdk.manager.ImSdkCallbackManager;
import com.xdja.imsdk.manager.ImSdkConfigManager;
import com.xdja.imsdk.manager.ImSdkManager;
import com.xdja.imsdk.model.IMFileInfo;
import com.xdja.imsdk.model.IMMessage;
import com.xdja.imsdk.model.IMSession;
import com.xdja.imsdk.model.InitParam;
import com.xdja.imsdk.model.body.*;
import com.xdja.imsdk.util.ValidateUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 项目名称：ImSdk             <br>
 * 类描述  ：ImSdk接口实现      <br>
 * 创建时间：2016/11/16 15:42  <br>
 * 修改记录：                  <br>
 *
 * @author liming@xdja.com    <br>
 * @version V1.1.7            <br>
 */
public class ImClient implements ISdkClient {
    public static final String VER_CODE = "V1.1.8_0407_D";
    private static ImClient imClient;
    private static boolean isInit = false;
    private Context context;

    /**
     * @param context 上下文对象
     * @return ImClient实例
     */
    public static ImClient getInstance(Context context) {
        if(context != null){
            if (imClient == null) {
                imClient = Factory.getInstance(context);
            }
            return imClient;
        }
        return null;
    }

    /**
     * ImClient工厂类
     */
    private static class Factory {
        static ImClient getInstance(Context context) {
            return new ImClient(context);
        }
    }

    private ImClient(Context context) {
        this.context = context;
    }
    /**
     * ImSdk初始化
     *
     * @param initParam 初始化参数
     * @return 0: 调用成功<br>
     * 其他: 调用失败，原因可参考返回结果码
     * @see ImSdkResult
     */
    @Override
    public int Init(InitParam initParam) {
        if (!verifyInitParam(initParam)) {
            return ImSdkResult.RESULT_FAIL_PARA;
        }
        Logger.getLogger().i("ImSdk init version: [" + VER_CODE + "]...");
        Logger.getLogger().d("ImSdk init para: " + initParam);
        ImSdkCallbackManager.getInstance().registerCallbackFunction(initParam.getCallback());
        ImSdkCallbackManager.getInstance().registerSecurityCallback(initParam.getSecurityCallback());
        startService(initParam);
        isInit = true;
        return ImSdkResult.RESULT_OK;
    }

    /**
     * ImSdk释放
     *
     * @param flag 说明<br>
     *             flag = 1: 应用关闭，释放所有资源,不再接收消息 <br>
     *             flag = 0: 应用退出前台，ImSdkService后台运行，继续接收消息
     * @return 0: ImSdk释放成功<br>
     * 其他: 调用失败，原因可参考返回结果码
     * @see ImSdkResult
     */
    @Override
    public int Release(int flag) {
        Logger.getLogger().i("ImSdk " + (flag == ImSdkConstant.RELEASE_KEEP ?
                "cancel but keep receiving message..." : "cancel all..."));
        if (!isInit) {
            Logger.getLogger().d("ImSdk already released...");
            return ImSdkResult.RESULT_FAIL_SERVICE;
        }

        if(flag == ImSdkConstant.RELEASE_KEEP){
            isInit = false;
            return ImSdkResult.RESULT_OK;
        }

        if(flag == ImSdkConstant.RELEASE_QUIT){
            Intent intent = new Intent(context, ImSdkService.class);
            context.stopService(intent);
            isInit = false;
            return ImSdkResult.RESULT_OK;
        }

        return ImSdkResult.RESULT_FAIL_PARA;
    }

    /**
     * 和Im server 同步消息
     *
     * @return 0: ImSdk释放成功<br>
     * 其他: 调用失败，原因可参考返回结果码
     * @see ImSdkResult
     */
    @Override
    public int SyncMessage() {
        if (!isInit) {
            Logger.getLogger().d("ImSdk sync message...");
            return ImSdkResult.RESULT_FAIL_SERVICE;
        }

        return ImSdkManager.getInstance().syncMessage();
    }

    /**
     * 配置ImSdk属性
     *
     * @param param 配置项属性键值表
     * @return 0: 配置成功<br>
     * 其他: 配置失败，原因可参考返回结果码
     * @see ImSdkConfig
     */
    @Override
    public int SetConfig(Map<String, String> param) {
        if (!isInit) {
            return ImSdkResult.RESULT_FAIL_SERVICE;
        }
        return ImSdkManager.getInstance().saveConfig(param);
    }


    /**
     * 获取ImSdk配置项属性
     *
     * @param key 属性的键
     * @return ImSdk配置项key对应的值
     * @throws ImSdkException
     * @see ImSdkResult
     * @see ImSdkConfig
     */
    @Override
    public String GetConfig(String key) throws ImSdkException {
        if (!isInit) {
            throw new ImSdkException(ImSdkResult.RESULT_FAIL_SERVICE);
        }
        return ImSdkManager.getInstance().getConfigByKey(key);
    }

    /**
     * 获取会话列表，结果按照时间降序排列返回<br>
     * 如果启用无会话模式，则此接口无数据返回
     *
     * @param begin 获取会话列表的起始位置的会话的会话标识，为空时表示从最新一个会话开始获取
     * @param size  从起始位置开始获取指定数量会话:List&lt;IMSession&gt;<br>
     *              <p>说明<br>
     *              size>0: 往时间小的方向取值，往过去的时间取，按时间降序排列<br>
     *              size<0: 往时间大的方向取值，往向前的时间取，按时间降序排列<br>
     *              size=0: 取当前这条
     * @return 符合条件的会话组成的列表，以会话生成的时间为序<br>
     * @throws ImSdkException
     * @see IMSession
     */
    @Override
    public List<IMSession> GetIMSessionList(String begin, int size) throws ImSdkException {
        if (!isInit) {
            throw new ImSdkException(ImSdkResult.RESULT_FAIL_SERVICE);
        }

        // 无会话模式
        if (!ImSdkConfigManager.getInstance().needSession()) {
            throw new ImSdkException(ImSdkResult.RESULT_FAIL_FORBID);
        }

        return ImSdkManager.getInstance().getIMSessionList(begin, size);
    }

    /**
     * 添加自定义会话
     *
     * @param session 用户自定义会话<br>
     *                <p>session的参数说明：<br>
     *                sessionType值为100，是必填项<br>
     *                imPartner是必填项
     * @return 带有会话所有信息的一个完整的会话
     * @throws ImSdkException
     * @see IMSession
     */
    @Override
    public IMSession IMSessionListAddCust(IMSession session) throws ImSdkException {
        if (!isInit) {
            throw new ImSdkException(ImSdkResult.RESULT_FAIL_SERVICE);
        }

        // 无会话模式
        if (!ImSdkConfigManager.getInstance().needSession()) {
            throw new ImSdkException(ImSdkResult.RESULT_FAIL_FORBID);
        }

        if (!verifySession(session)) {
            throw new ImSdkException(ImSdkResult.RESULT_FAIL_PARA);
        }

        return ImSdkManager.getInstance().addCustomSession(session);
    }

    /**
     * 添加自定义消息
     *
     * @param sessionTag 要添加的消息所在的会话标识，类型最后需要为100
     * @param message    需要插入的消息<br>
     *                   <p>message的参数说明：<br>
     *                   必须字段：消息接收方to, 消息类型type, 消息内容c
     * @return 带有消息所有信息的一个完整的消息
     * @throws ImSdkException
     * @see IMMessage
     * @see IMSession
     */
    @Override
    public IMMessage IMMessageListAddCust(String sessionTag, IMMessage message) throws ImSdkException {
        if (!isInit) {
            throw new ImSdkException(ImSdkResult.RESULT_FAIL_SERVICE);
        }

        // 无会话模式
        if (!ImSdkConfigManager.getInstance().needSession()) {
            throw new ImSdkException(ImSdkResult.RESULT_FAIL_FORBID);
        }

        if (!verifyMessage(message)) {
            throw new ImSdkException(ImSdkResult.RESULT_FAIL_PARA);
        }


        return ImSdkManager.getInstance().addCustomMessage(sessionTag, message);
    }

    /**
     * 获取指定会话的消息列表，按照时间升序排列
     *
     * @param sessionTag 消息列表所在的会话标识
     * @param begin      获取消息起始位置
     * @param size       从起始位置开始获取指定数量消息<br>
     *                   <p>说明<br>
     *                   size>0: 往时间小的方向取值，往过去的时间取，按时间升序排列<br>
     *                   size<0: 往时间大的方向取值，往向前的时间取，按时间升序排列<br>
     *                   size=0: 取当前这条
     * @return 符合条件的消息组成的列表
     * @throws ImSdkException
     * @see IMMessage
     */
    @Override
    public List<IMMessage> GetIMMessageList(String sessionTag, long begin, int size) throws ImSdkException {
        if (!isInit) {
            throw new ImSdkException(ImSdkResult.RESULT_FAIL_SERVICE);
        }

        if (TextUtils.isEmpty(sessionTag)) {
            throw new ImSdkException(ImSdkResult.RESULT_FAIL_PARA);
        }
        return ImSdkManager.getInstance().getIMMessageList(sessionTag, begin, size);
    }

    /**
     * 获取会话中的图片列表
     *
     * @param sessionTag 指定的会话标识
     * @param begin      获取图像起始位置
     * @param size       从起始位置开始获取指定数量图片<br>
     *                   <p>说明<br>
     *                   size>0: 往时间大的方向取值，往现在的时间取<br>
     *                   size<0: 往时间小的方向取值，往过去的时间取<br>
     *                   size=0: 取当前这条
     * @return 会话中出现过的图片信息，会话中消息时间为序
     * @throws ImSdkException
     * @see IMMessage
     */
    @Override
    public List<IMMessage> GetImageList(String sessionTag, int begin, int size) throws ImSdkException {
        if (!isInit) {
            throw new ImSdkException(ImSdkResult.RESULT_FAIL_SERVICE);
        }

        if (TextUtils.isEmpty(sessionTag)) {
            throw new ImSdkException(ImSdkResult.RESULT_FAIL_PARA);
        }
        return ImSdkManager.getInstance().getImageList(sessionTag, begin, size);
    }

    /**
     * 获取会话中的文件列表
     *
     * @param sessionTag 指定的会话标识。为空时取所有文件列表
     * @return 文件消息列表
     * @throws ImSdkException
     * @see IMMessage
     */
    @Override
    public List<IMMessage> GetFileList(String sessionTag) throws ImSdkException {
        if (!isInit) {
            throw new ImSdkException(ImSdkResult.RESULT_FAIL_SERVICE);
        }

        return ImSdkManager.getInstance().getFileList(sessionTag);
    }

    /**
     * 根据消息id获取消息
     *
     * @param id 消息id
     * @return 消息
     * @throws ImSdkException
     * @see IMMessage
     */
    @Override
    public IMMessage getIMMessageById(long id) throws ImSdkException {
        if (!isInit) {
            throw new ImSdkException(ImSdkResult.RESULT_FAIL_SERVICE);
        }
        return ImSdkManager.getInstance().getIMMessageById(id);
    }

    /**
     * 发送消息<br>
     *
     * @param message 需要发送的消息<br>
     *                说明：<br>
     *                IMMessage要求必填字段：消息接收方to, 消息类型type, 消息内容c
     * @return 带有消息所有信息的一个完整的消息
     * @throws ImSdkException
     * @see IMMessage
     */
    @Override
    public IMMessage SendIMMessage(IMMessage message) throws ImSdkException {
        if (!isInit) {
            throw new ImSdkException(ImSdkResult.RESULT_FAIL_SERVICE);
        }

        if (!verifyMessage(message)) {
            throw new ImSdkException(ImSdkResult.RESULT_FAIL_PARA);
        }
        return ImSdkManager.getInstance().sendIMMessage(message);
    }

    /**
     * 删除会话，维护会话列表使用<br>
     * 如果启用无会话模式，则此接口调用始终失败
     *
     * @param tags 需要删除的会话标识的列表，可删除多个会话
     * @return 0: 删除会话成功<br>
     * 其他: 删除操作失败，原因可参考常量错误码
     */
    @Override
    public int DeleteIMSession(List<String> tags) {
        if (!isInit) {
            return ImSdkResult.RESULT_FAIL_SERVICE;
        }

        // 无会话模式
        if (!ImSdkConfigManager.getInstance().needSession()) {
            return ImSdkResult.RESULT_FAIL_FORBID;
        }

        if (null == tags || tags.isEmpty()) {
            return ImSdkResult.RESULT_FAIL_PARA;
        }
        return ImSdkManager.getInstance().deleteIMSession(tags);
    }

    /**
     * 删除消息
     *
     * @param msgIds 需要删除的消息Id，列表形式可删除多条消息
     * @return 0：删除消息成功   <br>
     * 其他: 删除操作失败，原因可参考常量错误码
     */
    @Override
    public int DeleteIMMessage(List<Long> msgIds) {
        if (!isInit) {
            return ImSdkResult.RESULT_FAIL_SERVICE;
        }

        if (null == msgIds) {
            return ImSdkResult.RESULT_FAIL_PARA;
        }

        if (msgIds.isEmpty()) {
            return ImSdkResult.RESULT_FAIL_PARA;
        }

        for (Long id : msgIds) {
            if (!ValidateUtils.isValidateLong(id)) {
                return ImSdkResult.RESULT_FAIL_PARA;
            }
        }

        return ImSdkManager.getInstance().deleteIMMessage(msgIds);
    }

    /**
     * 获取会话中消息提醒数量
     *
     * @param sessionTag 指定的会话标识
     * @return 大于等于0：会话的消息数量   <br>
     * 其他：操作失败，原因可参考常量错误码
     */
    @Override
    public int GetRemindIMMessageCount(String sessionTag) {
        if (!isInit) {
            return ImSdkResult.RESULT_FAIL_SERVICE;
        }

        if (TextUtils.isEmpty(sessionTag)) {
            return ImSdkResult.RESULT_FAIL_PARA;
        }
        return ImSdkManager.getInstance().getRemindIMMessageCount(sessionTag);
    }

    /**
     * 获取所有会话消息提醒总数
     *
     * @return 大于等于0：所有消息的数量   <br>
     * 其他：操作失败，原因可参考常量错误码
     */
    @Override
    public int GetAllRemindIMMessageCount() {
        if (!isInit) {
            return ImSdkResult.RESULT_FAIL_SERVICE;
        }

        return ImSdkManager.getInstance().getAllRemindIMMessageCount();
    }

    /**
     * 清空指定会话的消息提醒数量
     *
     * @param sessionTag 指定的会话标识
     * @param type 操作类型。0：全部设为已读 1：设为未读
     * @return 0：操作成功 <br>
     * 其他: 操作失败，原因可参考常量错误码
     */
    @Override
    public int SetRemind(String sessionTag, int type) {
        if (!isInit) {
            return ImSdkResult.RESULT_FAIL_SERVICE;
        }

        if (TextUtils.isEmpty(sessionTag)) {
            return ImSdkResult.RESULT_FAIL_PARA;
        }
        return ImSdkManager.getInstance().setRemind(sessionTag, type);
    }

    /**
     * 清空指定会话中的所有的消息，即删除指定会话中的所有消息，会话保留
     *
     * @param sessionTag 指定的会话标识
     * @return 0：操作成功 <br>
     * 其他: 操作失败，原因可参考常量错误码
     */
    @Override
    public int ClearIMSessionAllIMMessage(String sessionTag) {
        if (!isInit) {
            return ImSdkResult.RESULT_FAIL_SERVICE;
        }

        if (TextUtils.isEmpty(sessionTag)) {
            return ImSdkResult.RESULT_FAIL_PARA;
        }
        return ImSdkManager.getInstance().clearIMSessionAllIMMessage(sessionTag);
    }

    /**
     * 清空SDK中所有数据
     *
     * @return 0：操作成功 <br>
     * 其他：操作失败，原因可参考常量错误码
     */
    @Override
    public int ClearAllLocalData() {
        if (!isInit) {
            return ImSdkResult.RESULT_FAIL_SERVICE;
        }

        return ImSdkManager.getInstance().clearAllLocalData();
    }

    /**
     * 暂停文件上传
     *
     * @param fileInfo 当前文件信息
     * @return 0：暂停文件上传调用成功    <br>
     * 其他：暂停失败
     * @see IMFileInfo
     */
    @Override
    public int SendFilePause(IMFileInfo fileInfo) {
        if (!isInit) {
            return ImSdkResult.RESULT_FAIL_SERVICE;
        }

        if (!verifyFileInfo(fileInfo)) {
            return ImSdkResult.RESULT_FAIL_PARA;
        }
        return ImSdkManager.getInstance().processFile(fileInfo, FileOptType.UP_PAUSE);
    }

    /**
     * 重新开始文件上传
     *
     * @param fileInfo 当前文件信息
     * @return 0：恢复文件上传调用成功     <br>
     * 其他：恢复文件上传处理失败
     * @see IMFileInfo
     */
    @Override
    public int SendFileResume(IMFileInfo fileInfo) {
        if (!isInit) {
            return ImSdkResult.RESULT_FAIL_SERVICE;
        }

        if (!verifyFileInfo(fileInfo)) {
            return ImSdkResult.RESULT_FAIL_PARA;
        }
        return ImSdkManager.getInstance().processFile(fileInfo, FileOptType.UP_RESUME);
    }

    /**
     * 开始文件下载
     *
     * @param fileInfoList 当前文件信息列表
     * @return 0：开始文件下载调用成功   <br>
     * 其他：暂停处理失败
     * @see IMFileInfo
     */
    @Override
    public int ReceiveFileStart(List<IMFileInfo> fileInfoList) {
        if (!isInit) {
            return ImSdkResult.RESULT_FAIL_SERVICE;
        }

        if (fileInfoList == null || fileInfoList.isEmpty()) {
            return ImSdkResult.RESULT_FAIL_PARA;
        }

        for (IMFileInfo fileInfo : fileInfoList) {
            if (!verifyFileInfo(fileInfo)) {
                return ImSdkResult.RESULT_FAIL_PARA;
            }
        }
        return ImSdkManager.getInstance().receiveFileStart(fileInfoList);
    }

    /**
     * 暂停文件下载
     *
     * @param fileInfo 当前文件信息
     * @return 0：暂停文件下载调用成功   <br>
     * 其他：暂停处理失败
     * @see IMFileInfo
     */
    @Override
    public int ReceiveFilePause(IMFileInfo fileInfo) {
        if (!isInit) {
            return ImSdkResult.RESULT_FAIL_SERVICE;
        }

        if (!verifyFileInfo(fileInfo)) {
            return ImSdkResult.RESULT_FAIL_PARA;
        }
        return ImSdkManager.getInstance().processFile(fileInfo, FileOptType.DOWN_PAUSE);
    }

    /**
     * 重新开始文件下载
     *
     * @param fileInfo 当前文件信息
     * @return 0：恢复文件下载调用成功    <br>
     * 其他：恢复文件下载处理失败
     * @see IMFileInfo
     */
    @Override
    public int ReceiveFileResume(IMFileInfo fileInfo) {
        if (!isInit) {
            return ImSdkResult.RESULT_FAIL_SERVICE;
        }

        if (!verifyFileInfo(fileInfo)) {
            return ImSdkResult.RESULT_FAIL_PARA;
        }
        return ImSdkManager.getInstance().processFile(fileInfo, FileOptType.DOWN_RESUME);
    }

    /**
     * 更新消息状态
     *
     * @param message 需要更新状态的消息
     * @param state  将要更新的状态
     * @return 0：调用成功
     * 其他：调用失败
     * @see IMMessage
     */
    @Override
    public int IMMessageStateChange(IMMessage message, int state) {
        Logger.getLogger().d("ImSdk change to state = " + state);
        if (!isInit) {
            return ImSdkResult.RESULT_FAIL_SERVICE;
        }

        if (!verifyMessage(message)) {
            return ImSdkResult.RESULT_FAIL_PARA;
        }

        // 发送失败的消息只能重发或删除，不能直接修改消息状态
        if (message.getState() == MsgState.MSG_STATE_FAIL) {
            return ImSdkResult.RESULT_FAIL_PARA;
        }

        // 消息状态只能单调增加
        if (state == message.getState() || state < message.getState()) {
            return ImSdkResult.RESULT_FAIL_PARA;
        }
        return ImSdkManager.getInstance().changeMessageState(message, state);
    }

    /**
     * 消息重发
     *
     * @param messageId 需要重发的消息id
     * @return 0：调用成功
     * 其他：调用失败
     */
    @Override
    public int ResendIMMessage(long messageId) {
        Logger.getLogger().d("ImSdk resend message...");
        if (!isInit) {
            return ImSdkResult.RESULT_FAIL_SERVICE;
        }

        return ImSdkManager.getInstance().resendIMMessage(messageId);
    }

    /**
     * 注册会话变化监听
     *
     * @param context  监听器上下文
     * @param callback 回调接口
     * @see IMSessionCallback
     */
    @Override
    public void RegisterIMSessionChangeListener(Context context, IMSessionCallback callback) {
        ImSdkCallbackManager.getInstance().registerSessionCallback(context, callback);
    }

    /**
     * 注销会话变化监听器
     *
     * @param context 监听器上下文
     */
    @Override
    public void UnregisterIMSessionChangeListener(Context context) {
        ImSdkCallbackManager.getInstance().unregisterSessionCallback(context);
    }

    /**
     * 注册消息变化监听
     *
     * @param context  监听器上下文
     * @param callback 回调接口
     * @see IMMessageCallback
     */
    @Override
    public void RegisterIMMessageChangeListener(Context context, IMMessageCallback callback) {
        ImSdkCallbackManager.getInstance().registerMessageCallback(context, callback);
    }

    /**
     * 注销消息变化监听器
     *
     * @param context 监听器上下文
     */
    @Override
    public void UnregisterIMMessageChangeListener(Context context) {
        ImSdkCallbackManager.getInstance().unregisterMessageCallback(context);
    }

    /**
     * 注册文件操作变化监听
     *
     * @param context  监听器上下文
     * @param callback 回调接口
     * @see IMFileInfoCallback
     */
    @Override
    public void RegisterIMFileInfoChangeListener(Context context, IMFileInfoCallback callback) {
        ImSdkCallbackManager.getInstance().registerFileCallback(context, callback);
    }

    /**
     * 注销文件操作监听器
     *
     * @param context 监听器上下文
     */
    @Override
    public void UnregisterIMFileInfoChangeListener(Context context) {
        ImSdkCallbackManager.getInstance().unregisterFileCallback(context);
    }

    /**
     * 校验初始化参数
     * @param initParam 初始化参数
     * @return 校验结果
     */
    private boolean verifyInitParam(InitParam initParam) {
        if (initParam == null
                || initParam.getCallback() == null
                || TextUtils.isEmpty(initParam.getAccount())
                || TextUtils.isEmpty(initParam.getTfcardId())
                || TextUtils.isEmpty(initParam.getTicket())) {
            return false;
        }

        if (initParam.getCallback() == null) {
            return false;
        }

        if (initParam.getProperties() != null) {
            Map<String, String> property = initParam.getProperties();
            Set<Map.Entry<String, String>> entrySet = property.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                if (!ValidateUtils.verifyCustomConfig(entry.getKey(), entry.getValue())) {
                    // TODO: 2016/11/28 liming optimize
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 校验会话
     * @param session 会话
     * @return 校验结果
     */
    private boolean verifySession(IMSession session) {
        if (session == null) {
            return false;
        }

        if (TextUtils.isEmpty(session.getImPartner())) {
            return false;
        }

        if (session.getSessionType() != IMSessionType.SESSION_CUSTOM &&
                session.getSessionType() != IMSessionType.SESSION_GROUP &&
                session.getSessionType() != IMSessionType.SESSION_SINGLE) {
            return false;
        }

        return true;
    }

    /**
     * 校验消息体
     * @param message 消息体
     * @return 校验结果
     */
    private boolean verifyMessage(IMMessage message) {
        if (message == null) {
            return false;
        }

        if (TextUtils.isEmpty(message.getTo())) {
            return false;
        }

        if (message.getType() < 0) {
            return false;
        }

        if (message.isTextIMMessage() && message.isFileIMMessage()) {
            return false;
        }

        if ((message.getType() & MsgType.MSG_TYPE_TEXT) != MsgType.MSG_TYPE_TEXT &&
                (message.getType() & MsgType.MSG_TYPE_FILE) != MsgType.MSG_TYPE_FILE &&
                (message.getType() & MsgType.MSG_TYPE_WEB) != MsgType.MSG_TYPE_WEB){
            return false;
        }

        if (message.isTextIMMessage() &&
                (message.getMessageBody() == null ||
                TextUtils.isEmpty(message.getMessageBody().toString()))) {
            return false;
        }

        if (message.isFileIMMessage()) {
            if (!verifyFileBody(message.getMessageBody())) {
                return false;
            }
        }
        return true;
    }

    // TODO: 2016/12/5 liming optimize
    private boolean verifyFileInfo(IMFileInfo fileInfo) {
        if (fileInfo == null) {
            return false;
        }
        return true;
    }

    /**
     * 校验文件信息有效性
     * @param body 文件信息
     * @return boolean
     */
    private boolean verifyFileBody(IMMessageBody body) {
        if (body == null) {
            return false;
        }

        if (!(body instanceof IMFileBody)) {
            return false;
        }

        IMFileBody fileBody = (IMFileBody) body;

        if (TextUtils.isEmpty(fileBody.getLocalPath())) {
            return false;
        }

        if (fileBody instanceof IMVoiceBody) {
            IMVoiceBody voiceFile = (IMVoiceBody) fileBody;
            if (voiceFile.getDuration() <= 0) {
                return false;
            }
        } else if (fileBody instanceof IMVideoBody) {
            IMVideoBody videoFile = (IMVideoBody) fileBody;
            if (videoFile.getDuration() <= 0) {
                return false;
            }
			//TODO JYG
//            if (!ImSdkConfigManager.getInstance().needThu()) {
//                return ValidateUtils.isValidatePath(videoFile.getLocalPath());
//            }
        } else if (fileBody instanceof IMImageBody) {
            IMImageBody imageFile = (IMImageBody) fileBody;
            if (TextUtils.isEmpty(imageFile.getLocalPath())){
                return false;
            }
        }
        return true;
    }

    /**
     * 启动ImSdk service
     * @param initParam 初始化参数
     */
    private void startService(InitParam initParam) {
        Intent intent = new Intent(context, ImSdkService.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constant.IM_INIT_PARAM, initParam);
        intent.putExtras(bundle);
        intent.setAction(Constant.IM_ACTION_INIT);
        context.startService(intent);
    }
}
