package com.xdja.imsdk.manager;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.xdja.imsdk.constant.ChangeAction;
import com.xdja.imsdk.constant.ImSdkConstant;
import com.xdja.imsdk.constant.ImSdkFileConstant.FileType;
import com.xdja.imsdk.constant.ImSdkResult;
import com.xdja.imsdk.constant.MsgState;
import com.xdja.imsdk.constant.StateCode;
import com.xdja.imsdk.constant.internal.Constant;
import com.xdja.imsdk.constant.internal.Constant.FileOptType;
import com.xdja.imsdk.constant.internal.FileTState;
import com.xdja.imsdk.constant.internal.HttpApiConstant;
import com.xdja.imsdk.constant.internal.State;
import com.xdja.imsdk.db.DbHelper;
import com.xdja.imsdk.db.ImSdkDbUtils;
import com.xdja.imsdk.db.helper.OptType;
import com.xdja.imsdk.db.helper.OptType.MQuery;
import com.xdja.imsdk.db.helper.OptType.SQuery;
import com.xdja.imsdk.db.helper.UpdateArgs;
import com.xdja.imsdk.db.wrapper.MessageWrapper;
import com.xdja.imsdk.db.wrapper.SessionWrapper;
import com.xdja.imsdk.exception.ImSdkException;
import com.xdja.imsdk.http.HttpUtils;
import com.xdja.imsdk.http.bean.MsgBean;
import com.xdja.imsdk.logger.Logger;
import com.xdja.imsdk.manager.callback.BombCallback;
import com.xdja.imsdk.db.helper.OptHelper;
import com.xdja.imsdk.manager.callback.FileCallback;
import com.xdja.imsdk.manager.callback.NetCallback;
import com.xdja.imsdk.manager.callback.ResultCallback;
import com.xdja.imsdk.manager.callback.SendCallback;
import com.xdja.imsdk.manager.process.BombProcess;
import com.xdja.imsdk.manager.process.ReceiveProcess;
import com.xdja.imsdk.manager.process.SendOutProcess;
import com.xdja.imsdk.model.IMFileInfo;
import com.xdja.imsdk.model.IMMessage;
import com.xdja.imsdk.model.IMSession;
import com.xdja.imsdk.model.InitParam;
import com.xdja.imsdk.model.body.IMFileBody;
import com.xdja.imsdk.model.body.IMMessageBody;
import com.xdja.imsdk.model.internal.IMState;
import com.xdja.imsdk.util.ToolUtils;
import com.xdja.pushsdk.PushClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：                                               <br>
 * 创建时间：2016/11/27 下午6:49                           <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public class ImSdkManager {
    private static ImSdkManager imSdkManager;
    private static int netState = ImSdkConstant.IM_NETWORK_CONNECTED;          //网络状态

    private Context context;                                                   //应用上下文
    private String account;                                                    //登录账号
    private String cardId;                                                     //安全卡id
    private SendOutProcess sendOutProcess;                                     //发送消息处理
    private ReceiveProcess receiveProcess;                                     //接收消息处理
    private BombProcess bombProcess;                                           //闪信处理

    public static ImSdkManager getInstance(){
        synchronized(ImSdkManager.class) {
            if(imSdkManager == null){
                imSdkManager =  Factory.getInstance();
            }
        }
        return imSdkManager;
    }

    private static class Factory {
        static ImSdkManager getInstance() {
            return new ImSdkManager();
        }
    }

    /**
     * 初始化：
     * 1、生成需要的对象
     * 2、初始化各个单例
     * 3、重置本地处于异常状态的消息
     * @param context 上下文
     * @param initParam 初始化参数
     */
    public void init(Context context, InitParam initParam) {
        this.context = context;
        this.account = initParam.getAccount();
        this.cardId = initParam.getTfcardId();

        initImSdkDatabase(context);                                                 // 数据库
        ModelMapper.getIns().init(account);
        initConfig(account, initParam.getProperties());                                      // 配置项
        initCachePath();                                                            // 缓存路径
        this.sendOutProcess = new SendOutProcess();
        this.receiveProcess = new ReceiveProcess(account, new SendOut());
        this.bombProcess = new BombProcess(new BombDestroyed());
        HttpUtils.getInstance().init(context);                                      // 网络
        ImMsgManager.getInstance().init(account, cardId,
                initParam.getTicket(), new ResultProcess(), new NetWork());         // 消息同步管理
        ImSdkFileManager.getInstance().init(new FileText(), new NetWork());         // 文件管理
        subscribePushSdk();                                                         // 订阅Push
        resetAllAnomalousMsg();                                                     // 异常消息处理
        ImSdkCallbackManager.getInstance().callState(StateCode.SDK_SERVICE_OK);     // 回调ImSdk状态

        Logger.getLogger().d("ImSdk init done ...");
    }

    /**
     * ImSdk销毁，终止正在进行的所有业务
     * @return 操作结果
     */
    public int releaseAll() {
        releaseImAndPushSdk();                                                      // 释放Push

        HttpUtils.getInstance().cancelAll();                                        // 网络
        ImMsgManager.getInstance().cancelAll();                                     // 消息同步
        ImSdkCallbackManager.getInstance().cancelAllCallback();                     // 回调
        ImSdkConfigManager.getInstance().releaseAll();                              // 配置项
        DbHelper.getInstance().close();                                             // 数据库

        Logger.getLogger().i("ImSdk release done ...");
        return ImSdkResult.RESULT_OK;
    }

    /**
     * 同步消息，只有网络状态为连接不到服务器时才进行同步
     * @return 操作结果
     */
    public int syncMessage() {
        Logger.getLogger().d("netState = " + netState);
        if (netState != ImSdkConstant.IM_NETWORK_NO_SERVER) {
            return ImSdkResult.RESULT_FAIL_INVALID;
        }
        ImMsgManager.getInstance().syncIm();
        return ImSdkResult.RESULT_OK;
    }

    /**
     * 保存自定义配置项
     * @param param 配置项
     * @return 操作结果
     */
    public int saveConfig(Map<String, String> param) {
        return ImSdkConfigManager.getInstance().saveConfig(param);
    }

    /**
     * 读取指定配置项内容
     * @param key 键
     * @return 配置项
     */
    public String getConfigByKey(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }

        return ImSdkConfigManager.getInstance().getConfigByKey(key);
    }

    /**
     * 获取指定会话列表
     * @param begin 会话起始点
     * @param size 会话数量
     * @return 会话列表
     */
    public List<IMSession> getIMSessionList(String begin, int size) {
        List<SessionWrapper> wrappers = ImSdkDbUtils.querySessions(begin, size);
        return ModelMapper.getIns().mapSessions(wrappers);
    }

    /**
     * 获取指定会话消息列表
     * @param tag 指定会话
     * @param begin 消息起始点
     * @param size 消息数量
     * @return 消息列表
     */
    public List<IMMessage> getIMMessageList(String tag, long begin, int size) {
        List<MessageWrapper> wrappers = ImSdkDbUtils.
                queryMessages(OptHelper.getIns().getMQuery(tag, begin, size), MQuery.SHOW);
        return ModelMapper.getIns().mapMessages(wrappers);
    }

    /**
     * 获取指定图片列表
     * @param tag 指定会话
     * @param begin 图片起始点
     * @param size 图片数量
     * @return 图片列表
     */
    public List<IMMessage> getImageList(String tag, int begin, int size) {
        List<MessageWrapper> wrappers = ImSdkDbUtils.
                queryMessages(OptHelper.getIns().getIQuery(tag, begin, size), MQuery.ALL);
        return ModelMapper.getIns().mapMessages(wrappers);
    }

    /**
     * 获取指定文件列表
     * @param tag 指定会话
     * @return 文件列表
     */
    public List<IMMessage> getFileList(String tag) {
        List<MessageWrapper> wrappers;
        if (TextUtils.isEmpty(tag)) {
            // 查询所有最近文件，包括已下载的接收到的文件，发送成功的文件
            wrappers = ImSdkDbUtils.
                    queryMessages(OptHelper.getIns().getFQuery(), MQuery.RAW);
        } else {
            // 查询会话中的文件，包括所有接收到的文件，发送成功的文件
            wrappers = ImSdkDbUtils.
                    queryMessages(OptHelper.getIns().getFQuery(tag), MQuery.RAW);
        }
        return ModelMapper.getIns().mapMessages(wrappers);
    }

    /**
     * 根据消息id获取消息
     * @param id id
     * @return IMMessage
     */
    public IMMessage getIMMessageById(long id) {
        MessageWrapper wrapper = ImSdkDbUtils.
                queryMessage(OptHelper.getIns().getAMQuery(id), MQuery.ALL);
        return ModelMapper.getIns().mapMessage(wrapper);
    }

    /**
     * 获取指定新消息提醒数量
     * @param tag 会话标识
     * @return 新消息提醒数量
     */
    public int getRemindIMMessageCount(String tag) {
        return ImSdkDbUtils.queryRemindCount(OptHelper.getIns().getRQuery(tag));
    }

    /**
     * 获取所有新消息提醒数量
     * @return 新消息提醒数量
     */
    public int getAllRemindIMMessageCount() {
        return ImSdkDbUtils.queryRemindCount(OptHelper.getIns().getRQuery(""));
    }

    /**
     * 清除指定会话新消息数量
     * @param tag 指定会话
     * @return 操作结果
     */
    public int setRemind(String tag, int type) {
        int remind;
        if (type == ImSdkConstant.REMIND_CLEAR) {
            remind = 0;                             //会话设为已读
        } else {
            remind = 1;                             //会话设为未读
        }

        return ImSdkDbUtils.update(OptHelper.getIns().getRUpdate(tag, remind));
    }

    /**
     * 清除指定会话所有消息：
     * 1、终止此会话中所有正在进行的业务:
     *    正在发送的消息，
     *    移除闪信销毁队列中对应的消息；销毁闪信，并发送销毁状态消息
     * 2、清空数据
     * 3、TODO:清除T卡数据
     * 4、回调界面刷新
     * @param tag 指定会话
     * @return 操作结果
     */
    public int clearIMSessionAllIMMessage(String tag) {
        cancelSending(tag);                                    //正在发送中的消息停止发送
        processSReadBomb(tag);                                 //已阅读闪信 移除，销毁，发送
//        clearSDCardCache();
        int result = ImSdkDbUtils.delete(OptHelper.getIns().getMSDel(tag));      //删除会话中的所有消息
        callbackS(tag);                                       //回调会话刷新
        return result;
    }

    /**
     * 清除本地所有：
     * 1、终止所有正在进行的业务:
     *    正在发送的消息，
     *    移除闪信销毁队列中对应的消息；销毁闪信，并发送销毁状态消息
     * 2、清除本地消息，会话，文件等记录
     * 3、TODO:清除T卡数据
     * @return 操作结果
     */
    public int clearAllLocalData() {
        cancelSending();                                      //所有正在发送中的消息停止发送
        removeAllBomb();                                      //清空闪信队列
        processAllReadBomb();                                    //已阅读闪信 销毁，发送
        ImSdkDbUtils.delete(OptHelper.getIns().getSADel());    //删除会话，会通过触发器触发删除消息和文件
//        clearSDCardCache();                                //清除T卡缓存数据
        return ImSdkResult.RESULT_OK;
    }

    /**
     * 删除指定会话：
     * 1、终止会话中正在进行的业务:
     *    正在发送的消息，
     *    移除闪信销毁队列中对应的消息；销毁闪信，并发送销毁状态消息
     * 2、删除会话,触发器触发删除消息和文件
     * 3、TODO:删除会话在T卡缓存的文件，对照微信
     * @param tags 会话标识
     * @return 操作结果
     */
    public int deleteIMSession(List<String> tags) {
        cancelSending(tags);//所有正在发送中的消息停止发送
        processSReadBomb(tags);//已阅读闪信 移除，销毁，发送
        ImSdkDbUtils.delete(OptHelper.getIns().getSDel(tags));//删除会话，会通过触发器触发删除消息和文件
//        clearSDCardCache();                                //清除T卡缓存数据
        return ImSdkResult.RESULT_OK;
    }

    /**
     * 删除指定消息：
     * 1、终止消息中正在进行的业务:
     *    正在发送的消息，
     *    移除闪信销毁队列中对应的消息；销毁闪信，并发送销毁状态消息
     * 2、删除消息,触发器触发删除文件
     * 3、如果包含最后会话最后一条消息，则更新会话最后一条消息
     *    这里不用触发器原因：如果使用，在清空会话中所有消息时，
     *    每删除一条消息都会触发会话更新，速度较慢
     * 4、回调刷新
     * 5、TODO:删除消息在T卡缓存的文件，对照微信
     * @param ids 消息id
     * @return 操作结果
     */
    public int deleteIMMessage(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return ImSdkResult.RESULT_FAIL_PARA;
        }
        SessionWrapper wrapper = ImSdkDbUtils.
                querySession(OptHelper.getIns().getSMIQuery(ids.get(0)), SQuery.NON);
        if (wrapper == null || wrapper.getSessionEntryDb() == null) {
            return ImSdkResult.RESULT_FAIL_PARA;
        }
        cancelSendingRequest(ids);//所有正在发送中的消息停止发送
        processMReadBomb(ids);
        int result = ImSdkDbUtils.delete(OptHelper.getIns().getMDel(ids));
        if (result != ImSdkResult.RESULT_OK) {
            return result;
        }

        changeSession(ids, wrapper);//更新会话最后一条消息，回调刷新
        return result;
    }

    /**
     * 添加自定义会话：
     * @param session 自定义 会话
     * @return 操作结果
     */
    public IMSession addCustomSession(IMSession session) {
        SessionWrapper wrapper = ModelMapper.getIns().getSWrapper(session);
        long id = ImSdkDbUtils.saveSession(wrapper);
        wrapper.getSessionEntryDb().setId(id);
        return ModelMapper.getIns().mapSession(wrapper);
    }

    /**
     * 添加自定义消息:
     * 1、查询会话
     * 2、无会话，生成新会话，保存会话
     * 3、有会话，保存消息
     * 4、回调刷新消息，会话
     * @param tag 会话标识
     * @param message 自定义消息
     * @return 操作结果
     * @throws ImSdkException
     */
    public IMMessage addCustomMessage(String tag, IMMessage message) throws ImSdkException {
        return ModelMapper.getIns().mapMessage(saveIMMessage(tag, message, true));
    }

    /**
     * 发送消息：
     * 1、查询会话
     * 2、无会话，生成新会话，保存会话
     * 3、有会话，保存消息
     * 4、回调刷新消息，会话
     * 5、加入待发送队列
     * @param message 消息
     * @return 发送后的消息
     * @throws ImSdkException
     */
    public IMMessage sendIMMessage(IMMessage message) throws ImSdkException {
        if (sendOutProcess == null) {
            throw new ImSdkException(ImSdkResult.RESULT_FAIL_SERVICE);
        }

        message.setState(State.DEFAULT);
        String tag = ToolUtils.getSessionTag("", message);

        MessageWrapper wrapper = saveIMMessage(tag, message, false);
        IMMessage result = ModelMapper.getIns().mapMessageNoDecrypt(wrapper);
        sendOutProcess.add(wrapper);
        return result;
    }

    /**
     * 重新发送失败的消息
     * @param id 重发的消息id
     * @return 操作结果
     */
    public int resendIMMessage(long id) {
        if (sendOutProcess == null) {
            return ImSdkResult.RESULT_FAIL_SERVICE;
        }

        MessageWrapper wrapper = ImSdkDbUtils.queryMessage(OptHelper.getIns().getAMQuery(id), MQuery.ALL);
        if (wrapper == null || wrapper.getMsgEntryDb() == null) {
            return ImSdkResult.RESULT_FAIL_PARA;
        }

        sendOutProcess.add(wrapper);

        return ImSdkResult.RESULT_FAIL_PARA;
    }

    /**
     * 消息状态更新：
     * 1、更新数据库状态
     * 2、加入闪信队列
     * 3、回调刷新界面
     * 4、发送状态消息
     * @param message 要改变状态的消息
     * @param state 需要变化成的状态
     * @return 结果码 0，成功  1，失败
     */
    public int changeMessageState(IMMessage message, int state) {
        MessageWrapper msg = ImSdkDbUtils.queryMessage(OptHelper.getIns().
                getMIQuery(message.getIMMessageId()), MQuery.NO);

        if (msg == null || msg.getMsgEntryDb() == null) {
            return ImSdkResult.RESULT_FAIL_PARA;
        }
        msg.getMsgEntryDb().setState(state);
        message.setState(state);

        updateAndSave(msg, message);
        addBombQueue(message);
        callbackSM(message, msg.getMsgEntryDb().getSession_flag());
        sendStates();
        return ImSdkResult.RESULT_OK;
    }

    /**
     * 文件处理
     * @param fileInfo 文件信息
     * @param type 处理类型
     * @return 操作结果
     */
    public int processFile(IMFileInfo fileInfo, FileOptType type) {
        if (sendOutProcess == null || receiveProcess == null) {
            return ImSdkResult.RESULT_FAIL_SERVICE;
        }

        ImSdkFileManager.getInstance().filePauseResume(fileInfo, type);
        return ImSdkResult.RESULT_OK;
    }

    /**
     * 开始下载文件
     * @param files 文件列表
     * @return 操作结果
     */
    public int receiveFileStart(List<IMFileInfo> files) {
        if (sendOutProcess == null) {
            return ImSdkResult.RESULT_FAIL_SERVICE;
        }
        ImSdkFileManager.getInstance().downloadStart(files);
        return ImSdkResult.RESULT_OK;
    }

    /**
     * 取消所有正在发送的消息
     */
    private void cancelSending() {
        List<Long> sendingIds = ImSdkDbUtils.queryIds(OptHelper.getIns().getIngQuery(account, ""));
        cancelSendingRequest(sendingIds);
    }

    /**
     * 取消指定会话中正在发送的消息
     * @param tag 指定的会话
     */
    private void cancelSending(String tag) {
        List<String> tags = new ArrayList<>();
        tags.add(tag);

        cancelSending(tags);
    }

    /**
     * 取消指定的会话列表中正在发送的消息
     * @param tags 指定的会话列表
     */
    private void cancelSending(List<String> tags) {
        List<Long> sendingIds = ImSdkDbUtils.queryIds(OptHelper.getIns().getIngQuery(account, tags));
        cancelSendingRequest(sendingIds);
    }

    /**
     * 取消发送中的消息的请求
     * @param ids 发送中的消息
     */
    private void cancelSendingRequest(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            HttpUtils.getInstance().cancelRequest(ids);
        }
    }

    /**
     * 处理所有“已阅读”状态的闪信
     */
    private void processAllReadBomb() {
        List<MessageWrapper> wrappers = ImSdkDbUtils.queryMessages(OptHelper.getIns().getARBQuery(account, ""), MQuery.SHOW);
        destroyAndSend(wrappers);
    }

    /**
     * 处理指定会话中所有“已阅读”状态的闪信
     * 1、移除闪信销毁队列中对应的消息
     * 2、闪信销毁，并发送销毁状态消息
     * @param tag 指定会话
     */
    private void processSReadBomb(String tag) {
        List<MessageWrapper> wrappers = ImSdkDbUtils.queryMessages(OptHelper.getIns().getARBQuery(account, tag), MQuery.SHOW);
        removeBomb(ModelMapper.getIns().getIds(wrappers));
        destroyAndSend(wrappers);
    }

    /**
     * 处理指定会话中所有“已阅读”状态的闪信
     * 1、移除闪信销毁队列中对应的消息
     * 2、闪信销毁，并发送销毁状态消息
     * @param tags 指定会话
     */
    private void processSReadBomb(List<String> tags) {
        List<MessageWrapper> wrappers = ImSdkDbUtils.queryMessages(OptHelper.getIns().getARBQuery(account, tags), MQuery.SHOW);
        removeBomb(ModelMapper.getIns().getIds(wrappers));
        destroyAndSend(wrappers);
    }

    /**
     * 处理指定的消息列表中“已阅读”状态的闪信
     * 1、移除闪信销毁队列中对应的消息
     * 2、闪信销毁，并发送销毁状态消息
     * @param ids 指定的消息列表id
     */
    private void processMReadBomb(List<Long> ids) {
        List<MessageWrapper> wrappers = ImSdkDbUtils.queryMessages(OptHelper.getIns().getMRBQuery(account, ids), MQuery.SHOW);
        removeBomb(ModelMapper.getIns().getIds(wrappers));
        destroyAndSend(wrappers);
    }

    /**
     * 处理“已阅读”状态的闪信，处理如下：
     * 1、擦除消息内容，置消息状态为“已销毁”
     * 2、发送“已销毁”的状态消息
     */
    private void destroyAndSend(List<MessageWrapper> wrappers) {
        if (wrappers == null || wrappers.size() == 0) {
            return;
        }

        updateAndSave(wrappers);
        sendStates();
    }

    /**
     * 更改会话最后一条消息，回调刷新界面
     * @param msgIds 被删除的消息id
     * @param wrapper 会话
     */
    private void changeSession(List<Long> msgIds, SessionWrapper wrapper) {
        if (wrapper.getSessionEntryDb() == null ||
                wrapper.getSessionEntryDb().getLast_msg() == null) {
            return;
        }

        if (msgIds.contains(wrapper.getSessionEntryDb().getLast_msg())) {
            String tag = wrapper.getSessionEntryDb().getSession_flag();

            MessageWrapper msg = ImSdkDbUtils.queryMessage(OptHelper.getIns().getMQuery(tag), MQuery.NO);

            if (msg != null && msg.getMsgEntryDb() != null) {

                ImSdkDbUtils.update(OptHelper.getIns().
                        getLUpdate(tag, msg.getMsgEntryDb().getId(), msg.getMsgEntryDb().getSort_time()));
            }
            callbackS(tag);
        }
    }

    /**
     * 添加到闪信队列中，只有普通文本，图片。语音的销毁由上层控制
     * @param message 闪信消息
     */
    private void addBombQueue(IMMessage message) {
        if (message.isReadBomb()) {
            if (message.isTextIMMessage()) {
                // 文本闪信
                bombProcess.add(message);
            }

            if (message.isFileIMMessage()){
                IMMessageBody body = message.getMessageBody();
                if (body != null && body instanceof IMFileBody) {
                    IMFileBody fileBody = (IMFileBody) body;
                    if (fileBody.isImage()) {
                        // 图片闪信
                        bombProcess.add(message);
                    }
                }
            }
        }
    }

    /**
     * 移除闪信队列中指定的元素
     * @param list 要移除的元素
     */
    private void removeBomb(List<Long> list) {
        bombProcess.remove(list);
    }

    /**
     * 移除闪信队列中所有元素
     */
    private void removeAllBomb() {
        bombProcess.removeAll();
    }

    /**
     * 保存消息：
     * 1、查询会话
     * 2、无会话，生成新会话，保存会话
     * 3、有会话，保存消息
     * 4、回调刷新消息，会话
     * @param tag tag
     * @param message 消息
     * @param isCall 是否需要回调
     * @return 发送后的消息
     * @throws ImSdkException
     */
    private MessageWrapper saveIMMessage(String tag, IMMessage message, boolean isCall) throws ImSdkException {
        tag = ToolUtils.getSessionTag(tag, message);

        SessionWrapper sWrapper = ImSdkDbUtils.querySession(OptHelper.getIns().getSQuery(tag), SQuery.NON);

        boolean newSession = false;
        if (sWrapper == null) {
            sWrapper = ModelMapper.getIns().getSWrapper(tag, message);
            ImSdkDbUtils.saveSession(sWrapper);
            newSession = true;
        }

        message.setCardId(cardId);
        MessageWrapper mWrapper = ModelMapper.getIns().getMWrapper(tag, message);
        long id = ImSdkDbUtils.saveMessage(mWrapper);
        if (id < 0) {
            throw new ImSdkException(ImSdkResult.RESULT_FAIL_DATABASE);
        }

        if (mWrapper.getMsgEntryDb() != null) {
            mWrapper.getMsgEntryDb().setId(id); // TODO: 2016/12/5 liming 需要重新查询？
            IMSession session = callbackS(tag, newSession);
            if (isCall) {
                callbackM(id, session);                        //回调新增消息刷新
            }
        }
        return mWrapper;
    }

    /**
     * 更新消息状态，保存状态消息
     * @param wrapper 消息
     */
    private void updateAndSave(MessageWrapper wrapper, IMMessage message) {
        UpdateArgs update;

        if (message.isBomb()) {
            update = OptHelper.getIns().
                    getBombsUpdate(ModelMapper.getIns().mapBodyType(message),
                            message.getIMMessageId());
        } else {
            update = OptHelper.getIns().getMCUpdate(message.getIMMessageId(), message.getState());
        }

        List<MessageWrapper> wrappers = new ArrayList<>();
        wrappers.add(wrapper);

        List<IMState> states = ModelMapper.getIns().getStates(wrappers);

        if (!states.isEmpty()) {
            ImSdkDbUtils.updateAndSave(update, states.get(0));
        } else {
            ImSdkDbUtils.update(update);
        }
    }

    /**
     * 批量更新消息状态，保存状态消息
     * @param wrappers 消息
     */
    private void updateAndSave(List<MessageWrapper> wrappers) {
        List<UpdateArgs> bombs = new ArrayList<>();
        for (MessageWrapper wrapper : wrappers) {
            if (wrapper.getMsgEntryDb() != null) {
                UpdateArgs bomb = OptHelper.getIns().
                        getBombsUpdate(ModelMapper.getIns().mapBodyType(wrapper),
                                wrapper.getMsgEntryDb().getId());
                bombs.add(bomb);
                wrapper.getMsgEntryDb().setState(State.BOMB);
            }
        }

        List<IMState> states = ModelMapper.getIns().getStates(wrappers);

        ImSdkDbUtils.updateAndSaveBatch(bombs, states);
    }

    /**
     * 查询状态表，批量发送状态消息
     */
    private void sendStates() {
        List<IMState> states = ModelMapper.getIns().mapStates(ImSdkDbUtils.queryStates());
        sendOutProcess.add(states);
    }

    /**
     * 回调会话刷新
     * @param tag 会话标识
     */
    private void callbackS(String tag) {
        SessionWrapper wrapper = ImSdkDbUtils.querySession(OptHelper.getIns().getSMQuery(tag), SQuery.HAVE);
        IMSession session = ModelMapper.getIns().mapSession(wrapper);
        if (session == null) {
            return;
        }

        ImSdkCallbackManager.getInstance().callSession(session, ChangeAction.ACT_RF);
    }

    /**
     * 回调会话新增或刷新
     */
    private IMSession callbackS(String tag, boolean isNew) {
        SessionWrapper wrapper = ImSdkDbUtils.querySession(OptHelper.getIns().getSMQuery(tag), SQuery.HAVE);
        IMSession session = ModelMapper.getIns().mapSession(wrapper);
        if (isNew) {
            ImSdkCallbackManager.getInstance().callSession(session, ChangeAction.ACT_ADD);
        } else {
            ImSdkCallbackManager.getInstance().callSession(session, ChangeAction.ACT_RF);
        }
        return session;
    }

    /**
     * 回调消息新增
     * @param id 消息id
     * @param session 会话
     */
    private void callbackM(long id, IMSession session) {
        if (session == null) {
            return;
        }

        MessageWrapper wrapper = ImSdkDbUtils.queryMessage(OptHelper.getIns().getAMQuery(id), MQuery.SHOW);
        IMMessage message = ModelMapper.getIns().mapMessage(wrapper);

        if (message == null) {
            return;
        }

        List<IMMessage> messages = new ArrayList<>();
        messages.add(message);
        ImSdkCallbackManager.getInstance().callMessage(session, messages, ChangeAction.ACT_ADD);
    }

    /**
     * 回调会话刷新，消息状态更新
     */
    private void callbackSM(IMMessage message, String tag) {
        if (message.isBomb() || message.isRead()) {
            SessionWrapper wrapper = ImSdkDbUtils.querySession(OptHelper.getIns().
                    getSMQuery(tag), SQuery.HAVE);
            IMSession session = ModelMapper.getIns().mapSession(wrapper);
            if (session == null) {
                return;
            }
            ImSdkCallbackManager.getInstance().callChange(session, message);
            ImSdkCallbackManager.getInstance().callSession(session, ChangeAction.ACT_RF);
        }
    }

    /**
     * 生成push注册需要的client id
     * @return client id
     */
    private String getPushClientId() {
        return Constant.IM_ACCOUNT_PREFIX + cardId;
    }

    /**
     * 生成push注册需要的主题
     * @return 主题
     */
    private String getPushTopic() {
        return Constant.IM_ACCOUNT_PREFIX + cardId + Constant.IM_ACCOUNT_SUFFIX;
    }

    /**
     * 初始化数据库
     * @param context 上下文
     */
    private void initImSdkDatabase(Context context) {
        try {
            DbHelper.getInstance().initDatabase(context, account);
        } catch (Exception e) {
//			e.printStackTrace();
            Logger.getLogger().e("Database init error!!");// TODO: 2016/12/12 liming exception process
        }
    }

    /**
     * 初始化配置项
     * @param property property
     */
    private void initConfig(String account, Map<String, String> property) {
        ImSdkConfigManager.getInstance().init(account, property);
    }

    /**
     * 初始化缓存目录
     */
    private void initCachePath() {
        String path = ImSdkConfigManager.getInstance().getPath();
        if (TextUtils.isEmpty(path)) {
            return;
        }

        File dir = new File(path);
        boolean mkDir = false;
        if (!dir.exists()) {
            try {
                mkDir = dir.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (!mkDir) {
                    dir.mkdirs();
                }
            }
        }
    }

    /**
     * 初始化并订阅Push Sdk
     */
    private void subscribePushSdk() {
        PushClient.init(context, getPushClientId());
        PushClient.subTopic(context, getPushTopic(), 0);
    }

    /**
     * 重置初始化时所有状态不正常的消息，主要包括两个方面：
     * 1、消息状态为“发送中”，需要置为“发送失败”
     * 2、文件状态修改:[UP_LOADING ==> UP_FAIL], [DOWN_LOADING ==> DOWN_FAIL]
     * 3、接收到的“已阅读”闪信，需要进行处理。
     */
    private void resetAllAnomalousMsg() {
        // TODO: 2016/12/30 liming 使用事务，统一处理
        ImSdkDbUtils.update(OptHelper.getIns().getAAUpdate(State.DEFAULT, State.ENCRYPT_FAIL));
        ImSdkDbUtils.update(OptHelper.getIns().getAAUpdate(State.SENT_NON, State.SENT_FAIL));

        ImSdkDbUtils.update(OptHelper.getIns().getFAUpdate(FileTState.UP_LOADING, FileTState.UP_FAIL));
        ImSdkDbUtils.update(OptHelper.getIns().getFAUpdate(FileTState.DOWN_LOADING, FileTState.DOWN_FAIL));

        ImSdkDbUtils.update(OptHelper.getIns().getHAUpdate(FileTState.UP_LOADING, FileTState.UP_FAIL));
        ImSdkDbUtils.update(OptHelper.getIns().getHAUpdate(FileTState.DOWN_LOADING, FileTState.DOWN_FAIL));

        ImSdkDbUtils.update(OptHelper.getIns().getRAUpdate(FileTState.UP_LOADING, FileTState.UP_FAIL));
        ImSdkDbUtils.update(OptHelper.getIns().getRAUpdate(FileTState.DOWN_LOADING, FileTState.DOWN_FAIL));
        
        processAllReadBomb();
    }

    /**
     * 释放ImSdk, 反订阅并释放Push Sdk
     */
    private void releaseImAndPushSdk() {
        if (receiveProcess != null) {
            receiveProcess.stop();
        }

        if (sendOutProcess != null) {
            sendOutProcess.stop();
        }

        if (bombProcess != null) {
            bombProcess.removeAll();
        }
        PushClient.unsubscribe(context, getPushTopic(), "");
        PushClient.release(context);
    }

    /**
     * 发送网络状态广播
     * @param state 网络状态
     */
    private void sendNetBroadcast(String state) {
        if (!processNetState(state)) {
            return;
        }
        Intent stateBroadcast = new Intent();
        stateBroadcast.setAction(ImSdkConstant.IM_NETWORK_STATE_ACTION);
        stateBroadcast.putExtra(ImSdkConstant.IM_NETWORK_STATE, netState);
        context.sendBroadcast(stateBroadcast);
    }

    /**
     * 处理网络状态广播
     * @param state 网络状态
     * @return 是否需要发送广播
     */
    private boolean processNetState(String state) {
        // PUSH(10000) 建立长链接成功。
        // 直接上报正常状态
        if (Constant.PUSH_CONNECTED.equals(state)) {
            if (netState != ImSdkConstant.IM_NETWORK_CONNECTED) {
                netState = ImSdkConstant.IM_NETWORK_CONNECTED;
                return true;
            }
            return false;
        }

        // PUSH(10001) 长链接断开，系统断网，ping不通，后台踢人等。
        // 上报网络不可用状态
        if (Constant.PUSH_DISCONNECTED.equals(state)) {
            if (netState != ImSdkConstant.IM_NETWORK_DISABLED) {
                netState = ImSdkConstant.IM_NETWORK_DISABLED;
                return true;
            }

            return false;
        }

        // PUSH(10002) 网络可用，收到系统网络打开广播，每次尝试建立长链接时检测。
        // 之前状态为网络不可用状态，则上报正常状态
        if (Constant.PUSH_NET_OK.equals(state)) {
            if (netState == ImSdkConstant.IM_NETWORK_DISABLED) {
                netState = ImSdkConstant.IM_NETWORK_CONNECTED;
                return true;
            }
            return false;
        }

        // PUSH(10003) 无网络, 收到系统关闭网络广播。上报网络不可用状态
        if (Constant.PUSH_NET_DISMISS.equals(state)) {
            if (netState != ImSdkConstant.IM_NETWORK_DISABLED) {
                netState = ImSdkConstant.IM_NETWORK_DISABLED;
                return true;
            }
            return false;
        }

        // PUSH(10004) 建立长链接失败，即推送有网络时建立长链接失败。
        // 上报连接不到服务器状态
        if (Constant.PUSH_CONNECT_FAIL.equals(state)) {
            if (netState == ImSdkConstant.IM_NETWORK_NO_SERVER) {
                return true;
            } else {
                netState = ImSdkConstant.IM_NETWORK_NO_SERVER;
                return true;
            }
        }

        // HTTP(40000) 请求成功。上报正常状态
        if (String.valueOf(HttpApiConstant.HTTP_OK).equals(state)) {
            if (netState != ImSdkConstant.IM_NETWORK_CONNECTED) {
                netState = ImSdkConstant.IM_NETWORK_CONNECTED;
                return true;
            }
            return false;
        }

        // HTTP(40004) 请求无连接。上报网络不可用状态
        if (String.valueOf(HttpApiConstant.HTTP_NO).equals(state)) {
            if (netState != ImSdkConstant.IM_NETWORK_DISABLED) {
                netState = ImSdkConstant.IM_NETWORK_DISABLED;
                return true;
            }
            return false;
        }

        // HTTP(40007) 请求超时。上报连接不到服务器状态
        if (String.valueOf(HttpApiConstant.HTTP_TIMEOUT).equals(state)) {
            if (netState == ImSdkConstant.IM_NETWORK_CONNECTED) {
                netState = ImSdkConstant.IM_NETWORK_NO_SERVER;
                return true;
            }
            return false;
        }

        return false;
    }

    class BombDestroyed implements BombCallback {
        /**
         * 闪信消息状态变为已销毁
         *
         * @param message 已销毁的消息
         */
        @Override
        public void BombDestroy(IMMessage message) {
            changeMessageState(message, MsgState.MSG_STATE_BOMB);
        }
    }

    class SendOut implements SendCallback {

        /**
         * 发送状态消息
         */
        @Override
        public void SendStates() {
            sendStates();
        }
    }

    class ResultProcess implements ResultCallback {

        /**
         * 处理拉取到的逆序消息完成后回调SDK消息处理任务的回调接口
         *
         * @param msgList 收到的MsgBean的列表
         */
        @Override
        public void ReceiveMessage(List<MsgBean> msgList) {
            receiveProcess.add(msgList);
        }

        /**
         * 通知发送状态消息回调接口
         */
        @Override
        public void SendState() {
            sendStates();
        }
    }

    class FileText implements FileCallback {

        /**
         * 发送文件文本消息回调接口
         *
         * @param id 消息id
         */
        @Override
        public void SendFileText(long id) {
            MessageWrapper message = ImSdkDbUtils.queryMessage(OptHelper.getIns().getAMQuery(id), MQuery.ALL);
            sendOutProcess.sendFileText(message);
        }

        /**
         * 解密文件回调接口
         * @param message 文件
         * @param type 类型
         * @see FileType
         */
        @Override
        public void DecryptFile(MessageWrapper message, FileType type) {
            receiveProcess.add(message, type);
        }
    }

    class NetWork implements NetCallback {

        /**
         * 和IM SERVER 连接的网络状态变化回调接口
         *
         * @param state 变化状态
         */
        @Override
        public void NetChanged(String state) {
            sendNetBroadcast(state);
        }
    }
}
