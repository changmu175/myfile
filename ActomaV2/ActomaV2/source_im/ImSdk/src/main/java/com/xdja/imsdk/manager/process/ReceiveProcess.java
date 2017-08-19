package com.xdja.imsdk.manager.process;


import com.xdja.imsdk.constant.ChangeAction;
import com.xdja.imsdk.constant.ImSdkFileConstant.FileState;
import com.xdja.imsdk.constant.ImSdkFileConstant.FileType;
import com.xdja.imsdk.constant.internal.Constant;
import com.xdja.imsdk.constant.internal.Constant.ReceiveType;
import com.xdja.imsdk.constant.internal.FileTState;
import com.xdja.imsdk.constant.internal.HttpApiConstant;
import com.xdja.imsdk.constant.internal.State;
import com.xdja.imsdk.db.ImSdkDbUtils;
import com.xdja.imsdk.db.bean.DuplicateIdDb;
import com.xdja.imsdk.db.bean.FileMsgDb;
import com.xdja.imsdk.db.helper.OptHelper;
import com.xdja.imsdk.db.helper.OptType;
import com.xdja.imsdk.db.helper.UpdateArgs;
import com.xdja.imsdk.db.wrapper.MessageWrapper;
import com.xdja.imsdk.db.wrapper.SessionWrapper;
import com.xdja.imsdk.http.bean.MsgBean;
import com.xdja.imsdk.manager.ImSdkCallbackManager;
import com.xdja.imsdk.manager.ImSdkConfigManager;
import com.xdja.imsdk.manager.ImSdkFileManager;
import com.xdja.imsdk.manager.ModelMapper;
import com.xdja.imsdk.manager.callback.SendCallback;
import com.xdja.imsdk.model.IMFileInfo;
import com.xdja.imsdk.model.IMMessage;
import com.xdja.imsdk.model.IMSession;
import com.xdja.imsdk.model.internal.IMState;
import com.xdja.imsdk.security.SecurityPara;
import com.xdja.imsdk.security.SecurityResult;
import com.xdja.imsdk.util.FileSizeUtils;
import com.xdja.imsdk.util.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：  接收消息处理                                   <br>
 * 创建时间：2016/11/27 下午6:28                           <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public class ReceiveProcess {
    private SendCallback sendCallback;
    private Thread receivedThread;                                           // 处理接收的消息线程
    private String account;
    private final LinkedBlockingQueue<ReceiveNode> receivedQueue = new LinkedBlockingQueue<>();  //处理接收的消息队列
    private Map<String, List<MessageWrapper>> newMap = new HashMap<>();
    private volatile boolean doing = true;
    private boolean preLoad = false;                                                //语音，图片是否要开启预下载

    /**
     * 构造
     * @param account 账号
     * @param sendCallback 回调
     */
    public ReceiveProcess(String account, SendCallback sendCallback) {
        this.account = account;
        this.sendCallback = sendCallback;
        this.preLoad = ImSdkConfigManager.getInstance().needPreload();
        init();
    }

    /**
     * 添加消息到处理队列
     * @param list 消息
     */
    public void add(List<MsgBean> list) {
        ReceiveNode msg = new ReceiveNode(ReceiveType.RECEIVE);
        msg.setMessages(list);
        receivedQueue.offer(msg);

        synchronized (receivedQueue) {
            receivedQueue.notifyAll();
        }
    }

    /**
     * 添加待解密文件到处理队列
     * @param message 文件
     * @param type 类型
     */
    public void add(MessageWrapper message, FileType type) {
        ReceiveNode msg = new ReceiveNode(ReceiveType.DECRYPT);
        msg.setFile(message);
        msg.setFileType(type);
        receivedQueue.offer(msg);

        synchronized (receivedQueue) {
            receivedQueue.notifyAll();
        }
    }

    /**
     *
     */
    public void stop() {
        doing = false;
        synchronized (receivedQueue) {
            receivedQueue.notifyAll();
        }
        if (receivedThread != null) {
            receivedThread.interrupt();
        }
    }

    protected void init() {
        if (receivedThread == null) {
            receivedThread = new Thread(new ReceivedRunnable());
            receivedThread.setName("receivedThread");
            receivedThread.setDaemon(true);
            receivedThread.start();
        } else if (!receivedThread.isAlive()) {
            receivedThread.start();
        }
    }

    /**
     * 解密接收到的文件，回调上层刷新
     * @param message 文件
     * @param type 类型
     */
    private void processDecrypt(MessageWrapper message, FileType type) {
        if (message == null || message.getMsgEntryDb() == null) {
            return;
        }
        SecurityPara para = ModelMapper.getIns().getSecurityPara(message.getMsgEntryDb());
        if (para == null) {
            return;
        }

        String path;
        if (type == FileType.IS_SHOW) {
            if (message.getFileMsgDb() == null) {
                return;
            }
            path = message.getFileMsgDb().getEncrypt_path();
        } else if (type == FileType.IS_HD) {
            if (message.getHdThumbFileDb() == null) {
                return;
            }
            path = message.getHdThumbFileDb().getHd_encrypt_path();
        } else {
            if (message.getRawFileDb() == null) {
                return;
            }
            path = message.getRawFileDb().getRaw_encrypt_path();
        }

        String dest = "";

        if (message.getFileMsgDb() != null && message.getFileMsgDb().isNormal() && !message.isWeb()) {
            dest = FileUtils.getFileRecPath(message.getFileMsgDb().getFile_name());
        }

        SecurityResult result = ImSdkCallbackManager.getInstance().callDecryptFile(path, dest, para);

        if (result == null) {
            return;
        }

        updateDecrypt(result, message, type);

        long id = message.getMsgEntryDb().getId();

        callbackFinish(id, type, result.success());
    }

    /**
     * 解密完成后回调
     * @param id id
     * @param type type
     */
    private void callbackFinish(long id, FileType type, boolean decrypt) {
        MessageWrapper file = ImSdkDbUtils.queryMessage(OptHelper.getIns().getAMQuery(id), OptType.MQuery.ALL);

        if (file == null || file.getMsgEntryDb() == null) {
            return;
        }
        IMMessage msg = ModelMapper.getIns().mapMessage(file);
        IMFileInfo fileInfo = new IMFileInfo(msg);
        fileInfo.setTag(file.getMsgEntryDb().getSession_flag());
        fileInfo.setFileType(type);
        fileInfo.setPercent(100);
        if (decrypt) {
            fileInfo.setState(FileState.DONE);
        } else {
            fileInfo.setState(FileState.FAIL);
        }
        ImSdkFileManager.getInstance().downFinishCallback(fileInfo);
    }

    /**
     * 更新解密后的状态
     * @param result result
     * @param message message
     * @param type type
     */
    private void updateDecrypt(SecurityResult result, MessageWrapper message, FileType type) {
        long id = message.getMsgEntryDb().getId();

        // 解密失败，更新文件状态
        if (!result.success()) {
            ImSdkDbUtils.update(OptHelper.getIns().getFTUpdate(type, -1, FileTState.DECRYPT_FAIL, id));
            return;
        }

        // 解密成功，更新文件的路径，大小，状态
        if (type == FileType.IS_SHOW) {
            if (message.getFileMsgDb() == null) {
                return;
            }

            String filePath = result.getResult();
            long fileSize = FileSizeUtils.getFileSize(filePath);
            ImSdkDbUtils.update(OptHelper.getIns().getDFUpdate(type, filePath, fileSize, FileTState.DECRYPT_SUCCESS, id));
        } else if (type == FileType.IS_HD) {
            if (message.getHdThumbFileDb() == null) {
                return;
            }
            String hdPath = result.getResult();
            long hdSize = FileSizeUtils.getFileSize(hdPath);
            ImSdkDbUtils.update(OptHelper.getIns().getDFUpdate(type, hdPath, hdSize, FileTState.DECRYPT_SUCCESS, id));
        } else {
            if (message.getRawFileDb() == null) {
                return;
            }
            String rawPath = result.getResult();
            long rawSize = FileSizeUtils.getFileSize(rawPath);
            ImSdkDbUtils.update(OptHelper.getIns().getDFUpdate(type, rawPath, rawSize, FileTState.DECRYPT_SUCCESS, id));
        }
    }

    /**
     * 解析接收到到消息，第一次循环处理，分别得到普通消息和状态消息
     * @param list 消息列表
     */
    private void processReceived(List<MsgBean> list) {
        boolean empty = list == null || list.isEmpty();
        if (queueIsEmpty() || empty) {
            callbackAll();
        }

        if (empty) {
            return;
        }

        List<MsgBean> messages = new ArrayList<>();                  // 普通消息列表
        List<MsgBean> states = new ArrayList<>();                    // 状态消息列表
        List<Long> nIds = new ArrayList<>();
        List<Long> sIds = new ArrayList<>();

        List<Long> failFilter = ImSdkDbUtils.queryFst(OptHelper.getIns().getFailQuery(account)); // 发送失败消息创建时间过滤器

        // First filter:过滤与本地发送失败重复的消息，区分普通消息和状态消息
        for (MsgBean msg : list) {
            boolean isNormal = msg.isTextMsg() || msg.isFileMsg() || msg.isWebMsg();
            boolean isState = msg.isStateMsg();

            if (isNormal) {
                boolean failed = failFilter.contains(msg.getFst());
                if (!failed) {
                    //去掉与本地发送失败的重复的普通消息
                    messages.add(msg);
                    nIds.add(msg.getI());
                }
            }

            // 别人发过来的状态消息
            if (isState && !isSendOut(msg)) {
                states.add(msg);
                sIds.add(Long.valueOf(msg.getC()));
            }
        }

        processNormals(messages, nIds);         //
        processStates(states, sIds);            //

        saveProcessed(list);                    //
    }

    /**
     * 处理普通消息，策略如下：
     * @param normals 普通消息
     * @param ids server ids
     */
    private void processNormals(List<MsgBean> normals, List<Long> ids) {
        if (normals == null || normals.isEmpty()) {
            callbackAll();
            return;
        }

        List<MsgBean> results = new ArrayList<>();                     //去重复处理后的消息列表
        List<MsgBean> toSaveDup = new ArrayList<>();                   //重复消息列表，要保存到duplicate_msg表中
        List<MessageWrapper> dupStates = new ArrayList<>();            //重复的，需要发送状态消息的消息列表


        List<MsgBean> dupResult = new ArrayList<>();           //未保存，未删除的消息
        Map<Long, MsgBean> noDupResult = new HashMap<>();      //normals中fst都不相同的消息，既去掉了重复消息


        List<Long> savedFilter = ImSdkDbUtils.queryIds(OptHelper.getIns().getSaveQuery(ids));// server id 已保存消息服务器id过滤器
        List<Long> delFilter = ImSdkDbUtils.queryDelIds(OptHelper.getIns().getDelQuery(ids));  // server id 已删除消息服务器id过滤器

        // Second filter
        for (MsgBean msg : normals) {
            Long id = msg.getI();
            if (!savedFilter.contains(id) && !delFilter.contains(id)) {
                dupResult.add(msg);
                noDupResult.put(msg.getFst(), msg);
            }
        }

        if (dupResult.size() > noDupResult.size()) {
            // 说明存在重复消息，遍历得到重复消息的fst和server id
            for (MsgBean msg : dupResult) {
                MsgBean dup = noDupResult.get(msg.getFst());
                if (isSame(msg, dup)) {
                    toSaveDup.add(msg);
                }
            }
        }

        List<Long> noDupFst = new ArrayList<>();
        List<MsgBean> noDupList = new ArrayList<>();

        Set<Map.Entry<Long, MsgBean>> entrySet = noDupResult.entrySet();

        for (Map.Entry<Long, MsgBean> entry : entrySet) {
            noDupFst.add(entry.getKey());
            noDupList.add(entry.getValue());
        }

        // Third filter
        // 查询相同fst已保存消息[CREATE_TIME, SENDER, RECEIVER, TYPE, STATE]
        List<MessageWrapper> savedFstFilter = ImSdkDbUtils.queryDup(OptHelper.getIns().getDupQuery(noDupFst));

        if (savedFstFilter == null || savedFstFilter.isEmpty()) {
            results = noDupList;
        } else {
            for (MsgBean msg : noDupList) {
                boolean isDuplicate = false;

                for (MessageWrapper wrapper : savedFstFilter) {
                    if (isSame(msg, wrapper)) {
                        toSaveDup.add(msg);
                        isDuplicate = true;

                        // 已接收过的别人发过来的消息，状态大于“已接收”，即状态发生了变化，需要发送状态消息
                        if (wrapper.getMsgEntryDb().getState() > State.SENT) {
                            wrapper.getMsgEntryDb().setServer_id(msg.getI());
                            dupStates.add(wrapper);
                        }
                    }
                }

                if (!isDuplicate) {
                    results.add(msg);
                }
            }
        }


        List<String> savedTagIds = ImSdkDbUtils.querySessions(OptHelper.getIns().getTQuery());  //所有已持久的会话tags

        List<MessageWrapper> toSaveM = new ArrayList<>();                                  //需要持久化的消息列表
        Map<String, SessionWrapper> toSaveS = new HashMap<>();                             //生成的新会话
        Set<String> toRefreshS = new HashSet<>();                                          //callback 需要刷新的会话tags

        List<MessageWrapper> sendBombs = new ArrayList<>();                               //需要发送已销毁状态的消息列表
        List<MessageWrapper> recStates = new ArrayList<>();                               //需要生成“已接收”状态消息列表

        for (MsgBean msg : results) {
            MessageWrapper wrapper = ModelMapper.getIns().mapMWrapper(msg);

            if (msg.getStat() == State.SENT) {
                wrapper.getMsgEntryDb().setState(State.REC);
                msg.setStat(State.REC);

                recStates.add(wrapper);
            }

            if (isRecReadBomb(msg)) {
                // 接收到的“已阅读”的闪信
                sendBombs.add(wrapper);
                msg.setStat(State.BOMB); //状态销毁
            }

            if (isRecBombed(msg)) {
                msg.setC("");
            }

            toSaveM.add(wrapper);

            // 会话处理
            String tag = wrapper.getMsgEntryDb().getSession_flag();

            boolean existed = savedTagIds.contains(tag);
            boolean saving = toSaveS.containsKey(tag);


            if (existed) {
                toRefreshS.add(tag);
            } else {
                if (!saving) {
                    SessionWrapper newSession = ModelMapper.getIns().getSWrapper(msg);
                    toSaveS.put(tag, newSession);
                }
            }
        }

        List<SessionWrapper> sessions = ModelMapper.getIns().mapSessions(toSaveS);
        List<DuplicateIdDb> dupIds = ModelMapper.getIns().mapDup(toSaveDup);
        List<IMState> bombs = ModelMapper.getIns().getStates(sendBombs);
        List<IMState> dupes = ModelMapper.getIns().getStates(dupStates);
        List<IMState> recs = ModelMapper.getIns().getStates(recStates);

        save(sessions, toSaveM, dupIds, bombs, dupes, recs);
        sendState(bombs, dupes);
        callbackSM(toSaveS, toSaveM, toRefreshS);
    }

    /**
     * 状态消息处理
     * @param states 状态消息
     * @param ids server ids
     */
    private void processStates(List<MsgBean> states, List<Long> ids) {
        if (states == null || states.isEmpty()) {
            return;
        }
        Map<Long, Integer> savedState = ImSdkDbUtils.queryMStates(OptHelper.getIns().getStates(ids));
        List<MsgBean> toUpdate = new ArrayList<>();
        List<Long> toUpdateIds = new ArrayList<>();
        if (savedState.isEmpty()) {
            toUpdate = states;
            toUpdateIds = ids;
        } else {
            for (MsgBean state : states) {
                Long serverId = Long.parseLong(state.getC());
                boolean isExist = savedState.containsKey(serverId);
                if (isExist) {
                    boolean stateIsValid = state.getStat() > savedState.get(serverId);
                    if (stateIsValid) {
                        toUpdate.add(state);
                        toUpdateIds.add(serverId);
                    }
                }
            }
        }

        updateState(toUpdate);
        callbackSR(toUpdateIds);
    }

    /**
     * 回调当前所有消息
     */
    private void callbackAll() {
        if (newMap == null || newMap.isEmpty()) {
            return;
        }

        Set<String> tags = newMap.keySet();
        List<SessionWrapper> sessions = ImSdkDbUtils.
                querySessions(OptHelper.getIns().getSQuery(tags), OptType.SQuery.HAVE);

        if (sessions == null || sessions.isEmpty()) {
            return;
        }

        for (SessionWrapper session : sessions) {
            List<MessageWrapper> messages = newMap.get(session.getSessionEntryDb().getSession_flag());
            callbackNM(session, messages);
        }

        newMap.clear();
    }

    /**
     * 回调新消息
     * @param sWrapper sWrapper
     * @param mWrappers mWrappers
     */
    private void callbackNM(SessionWrapper sWrapper, List<MessageWrapper> mWrappers) {
        List<IMMessage> adds = new ArrayList<>();
        List<IMMessage> news = new ArrayList<>();

        IMSession session = ModelMapper.getIns().mapSession(sWrapper);

        for (MessageWrapper msg : mWrappers) {
            IMMessage message = ModelMapper.getIns().mapMessage(msg);

            adds.add(message);

            if (msg.getMsgEntryDb().isNew()) {
                news.add(message);
            }
        }

        ImSdkCallbackManager.getInstance().callNew(session, news);
        ImSdkCallbackManager.getInstance().callMessage(session, adds, ChangeAction.ACT_ADD);
        sendCallback.SendStates();
        loadFile(mWrappers);// TODO: 2016/12/20 liming 预下载的文件是否需要历史消息的判断，是否加入条数限制
    }

    /**
     * 回调刷新上层
     * @param toSaveS 新增的会话
     * @param toSaveM 新增的消息
     * @param toRefreshS 需要刷新的会话
     */
    private void callbackSM(Map<String, SessionWrapper> toSaveS, List<MessageWrapper> toSaveM, Set<String> toRefreshS) {
        callbackNewS(toSaveS);
        callbackRS(toRefreshS);
        prepareCallback(toSaveM);
    }

    /**
     * 回调新增会话
     * @param toSaveS toSaveS
     */
    private void callbackNewS(Map<String, SessionWrapper> toSaveS) {
        if (toSaveS == null || toSaveS.isEmpty()) {
            return;
        }
        Set<String> tags = toSaveS.keySet();
        List<SessionWrapper> newS = ImSdkDbUtils.
                querySessions(OptHelper.getIns().getSQuery(tags), OptType.SQuery.HAVE);
        if (newS == null || newS.isEmpty()) {
            return;
        }

        for (SessionWrapper entry:newS) {
            IMSession session = ModelMapper.getIns().mapSession(entry);
            ImSdkCallbackManager.getInstance().callSession(session, ChangeAction.ACT_ADD);
        }
    }

    /**
     * 回调刷新会话
     * @param toRefreshS toRefreshS
     */
    private void callbackRS(Set<String> toRefreshS) {
        if (toRefreshS == null || toRefreshS.isEmpty()) {
            return;
        }

        List<SessionWrapper> sessions = ImSdkDbUtils.
                querySessions(OptHelper.getIns().getSQuery(toRefreshS), OptType.SQuery.HAVE);
        if (sessions == null || sessions.isEmpty()) {
            return;
        }

        for (SessionWrapper wrapper : sessions) {
            IMSession session = ModelMapper.getIns().mapSession(wrapper);
            ImSdkCallbackManager.getInstance().callSession(session, ChangeAction.ACT_RF);
        }

    }

    /**
     * 回调新增消息准备，读取数据
     * @param toSaveM toSaveM
     */
    private void prepareCallback(List<MessageWrapper> toSaveM) {
        List<Long> serverIds = ModelMapper.getIns().getServerIds(toSaveM);
        if (serverIds.isEmpty()) {
            return;
        }
        List<MessageWrapper> messages = ImSdkDbUtils.
                queryMessages(OptHelper.getIns().getMSQuery(serverIds), OptType.MQuery.ALL);

        assertCallback(messages);
    }

    /**
     * 回调刷新消息状态改变
     * @param serverIds 状态消息
     */
    private void callbackSR(List<Long> serverIds) {
        if (serverIds == null || serverIds.isEmpty()) {
            return;
        }

        List<MessageWrapper> messages = ImSdkDbUtils.
                queryMessages(OptHelper.getIns().getMSQuery(serverIds), OptType.MQuery.ALL);

        if (messages == null || messages.isEmpty()) {
            return;
        }

        Map<String, List<MessageWrapper>> map = classifyMessage(messages);

        Set<String> tags = map.keySet();
        List<SessionWrapper> sWrappers = ImSdkDbUtils.
                querySessions(OptHelper.getIns().getSQuery(tags), OptType.SQuery.HAVE);

        if (sWrappers == null || sWrappers.isEmpty()) {
            return;
        }

        for (SessionWrapper sWrapper : sWrappers) {
            String tag = sWrapper.getSessionEntryDb().getSession_flag();
            List<MessageWrapper> list = map.get(tag);
            if (list == null || list.isEmpty()) {
                continue;
            }
            IMSession session = ModelMapper.getIns().mapSession(sWrapper);

            for (MessageWrapper mWrapper : list) {
                IMMessage message = ModelMapper.getIns().mapMessage(mWrapper);
                ImSdkCallbackManager.getInstance().callChange(session, message);
            }
        }
    }

    /**
     * 判断回调条数
     * @param messages messages
     */
    private void assertCallback(List<MessageWrapper> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }


        Map<String, List<MessageWrapper>> map = classifyMessage(messages);

        Set<String> tags = map.keySet();
        List<SessionWrapper> sessions = ImSdkDbUtils.
                querySessions(OptHelper.getIns().getSQuery(tags), OptType.SQuery.HAVE);

        if (sessions == null || sessions.isEmpty()) {
            return;
        }

        for (SessionWrapper session : sessions) {
            String tag = session.getSessionEntryDb().getSession_flag();
            List<MessageWrapper> list = map.get(tag);

            if (list == null || list.isEmpty()) {
                continue;
            }

            // 没有更多的消息
            if (queueIsEmpty()) {
                callbackNM(session, list);
                continue;
            }

            // 没有开启批量回调机制
            if (!Constant.CALLBACK_BATCH) {
                callbackNM(session, list);
                continue;
            }

            if (newMap.containsKey(tag)) {
                newMap.get(tag).addAll(list);
                if (newMap.get(tag).size() > Constant.CALLBACK_MAX) {
                    callbackNM(session, newMap.get(tag));
                    newMap.remove(tag);
                } else {
                    // do nothing
                }
            } else {
                if (list.size() > Constant.CALLBACK_MAX) {
                    callbackNM(session, list);
                } else {
                    newMap.put(tag, list);
                }
            }
        }
    }

    /**
     * 保存会话，保存消息，保存重复消息
     * @param sessions sessions
     * @param messages messages
     * @param dupIds dupIds
     * @param bombs bombs
     * @param dupes dupes
     */
    private void save(List<SessionWrapper> sessions, List<MessageWrapper> messages,
                      List<DuplicateIdDb> dupIds, List<IMState> bombs,
                      List<IMState> dupes, List<IMState> recs) {
        ImSdkDbUtils.saveReceived(sessions, messages, dupIds, bombs, dupes, recs);
    }

    /**
     * 持久化已处理的消息id
     * @param list 消息id
     */
    private void saveProcessed(List<MsgBean> list) {
        long id = list.get(list.size() - 1).getI();
        ImSdkDbUtils.update(OptHelper.getIns().getSyncUpdate(HttpApiConstant.PROCESS, id));
    }

    /**
     * 发送状态消息
     */
    private void sendState(List<IMState> bombs, List<IMState> dupes) {
        if ((bombs == null || bombs.isEmpty()) && (dupes == null || dupes.isEmpty())) {
            return;
        }

        sendCallback.SendStates();
    }

    /**
     * 更新状态消息对应的消息的状态
     */
    private void updateState(List<MsgBean> states) {
        if (states == null || states.isEmpty()) {
            return;
        }
        List<UpdateArgs> args = new ArrayList<>();
        for (MsgBean msg : states) {
            args.add(OptHelper.getIns().getSUpdate(Long.valueOf(msg.getC()), msg.getStat()));
        }
        ImSdkDbUtils.updateBatch(args);
    }

    /**
     * 开始预下载文件
     * 展示只预下载语音和缩略图
     * @param messages messages
     */
    private void loadFile(List<MessageWrapper> messages) {
        if (!preLoad) {
            return;
        }

        if (messages == null || messages.isEmpty()) {
            return;
        }

        List<MessageWrapper> files = new ArrayList<>();
        for (MessageWrapper message : messages) {
            if (message.getMsgEntryDb().isFile()) {
                FileMsgDb file = message.getFileMsgDb();
                if (file != null) {
                    if (file.isVoice() || file.isImage() || file.isVideo()) {
                        files.add(message);
                    }
                }
            }
        }

        ImSdkFileManager.getInstance().downloadPreStart(files);
    }

    /**
     * 判断是否是本账号发送出去的消息
     * @param msg 消息
     * @return boolean
     */
    private boolean isSendOut(MsgBean msg) {
        return msg.getF().equals(account);
    }

    /**
     * 判断是否是接收到的已阅读的闪信
     * @param msg 消息
     * @return boolean
     */
    private boolean isRecReadBomb(MsgBean msg) {
        boolean isRec = !isSendOut(msg);
        boolean isRead = msg.isReadMsg();
        boolean isBomb = msg.isBombMsg();

        return isRec && isRead && isBomb;
    }

    /**
     * 接收的已销毁的闪信
     * @param msg msg
     * @return boolean
     */
    private boolean isRecBombed(MsgBean msg) {
        boolean isRec = !isSendOut(msg);
        boolean bombed = msg.getStat() == State.BOMB;
        boolean isBomb = msg.isBombMsg();

        return isRec && bombed && isBomb;
    }

    /**
     * 判断消息是否相同
     * @param msg msg
     * @param dup dup
     * @return boolean
     */
    private boolean isSame(MsgBean msg, MsgBean dup) {
        if (msg == null || dup == null) {
            return false;
        }
        return msg.getFst() == dup.getFst() &&
                msg.getF().equals(dup.getF()) &&
                msg.getTo().equals(dup.getTo()) &&
                msg.getT() == dup.getT() &&
                msg.getI() != dup.getI();
    }

    /**
     * 判断消息是否相同
     * @param msg msg
     * @param wrapper wrapper
     * @return boolean
     */
    private boolean isSame(MsgBean msg, MessageWrapper wrapper) {
        if (msg == null || wrapper == null || wrapper.getMsgEntryDb() == null) {
            return false;
        }

        return msg.getFst() == wrapper.getMsgEntryDb().getCreate_time() &&
                msg.getF().equals(wrapper.getMsgEntryDb().getSender()) &&
                msg.getTo().equals(wrapper.getMsgEntryDb().getReceiver()) &&
                msg.getT() == wrapper.getMsgEntryDb().getType();
    }

    /**
     * 待处理队列是否为空
     * @return 返回结果
     */
    private boolean queueIsEmpty() {
        return receivedQueue.isEmpty();
    }

    /**
     * 消息按会话分类
     * @param messages messages
     * @return Map
     */
    private Map<String, List<MessageWrapper>> classifyMessage(List<MessageWrapper> messages) {
        Map<String, List<MessageWrapper>> map = new HashMap<>();
        for (MessageWrapper message : messages) {
            String tag = message.getMsgEntryDb().getSession_flag();

            if (map.containsKey(tag)) {
                map.get(tag).add(message);
            } else {
                List<MessageWrapper> list = new ArrayList<>();
                list.add(message);
                map.put(tag, list);
            }
        }
        return map;
    }

    /**
     * 从队列中读取消息
     * @return 待处理的消息
     */
    private ReceiveNode getNext() {
        ReceiveNode node;
        while ((node = receivedQueue.poll()) == null) {
            try {
                synchronized (receivedQueue) {
                    receivedQueue.wait();
                }
            }
            catch (InterruptedException ie) {
                // Do nothing
            }
        }
        return node;
    }


    protected class ReceivedRunnable implements Runnable {

        @Override
        public void run() {
            while (doing) {
                ReceiveNode node = getNext();
                if (node == null) {
                    return;
                }

                switch (node.getType()) {
                    case RECEIVE:
                        processReceived(node.getMessages());
                        break;
                    case DECRYPT:
                        processDecrypt(node.getFile(), node.getFileType());
                        break;
                }
            }
        }
    }
}
