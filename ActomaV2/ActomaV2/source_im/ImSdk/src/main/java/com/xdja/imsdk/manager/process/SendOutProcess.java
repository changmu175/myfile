package com.xdja.imsdk.manager.process;

import com.xdja.imsdk.constant.ImSdkFileConstant.FileType;
import com.xdja.imsdk.constant.internal.Constant;
import com.xdja.imsdk.constant.internal.Constant.SentType;
import com.xdja.imsdk.constant.internal.FileTState;
import com.xdja.imsdk.constant.internal.State;
import com.xdja.imsdk.db.ImSdkDbUtils;
import com.xdja.imsdk.db.bean.FileMsgDb;
import com.xdja.imsdk.db.bean.HdThumbFileDb;
import com.xdja.imsdk.db.bean.MsgEntryDb;
import com.xdja.imsdk.db.bean.RawFileDb;
import com.xdja.imsdk.db.helper.OptHelper;
import com.xdja.imsdk.db.helper.OptType;
import com.xdja.imsdk.db.helper.UpdateArgs;
import com.xdja.imsdk.db.wrapper.MessageWrapper;
import com.xdja.imsdk.db.wrapper.SessionWrapper;
import com.xdja.imsdk.http.bean.MsgBean;
import com.xdja.imsdk.logger.Logger;
import com.xdja.imsdk.manager.ImMsgManager;
import com.xdja.imsdk.manager.ImSdkCallbackManager;
import com.xdja.imsdk.manager.ImSdkConfigManager;
import com.xdja.imsdk.manager.ImSdkFileManager;
import com.xdja.imsdk.manager.ModelMapper;
import com.xdja.imsdk.model.IMMessage;
import com.xdja.imsdk.model.IMSession;
import com.xdja.imsdk.model.internal.IMState;
import com.xdja.imsdk.security.SecurityResult;
import com.xdja.imsdk.security.SecurityPara;
import com.xdja.imsdk.util.FileSizeUtils;
import com.xdja.imsdk.util.FileUtils;
import com.xdja.imsdk.util.ToolUtils;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：  发送消息处理                                   <br>
 * 创建时间：2016/11/27 下午6:27                           <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public class SendOutProcess {
    private Thread sendOutThread;                                     // 发送消息线程
    private final LinkedBlockingQueue<SendOutNode> sendOutQueue = new LinkedBlockingQueue<>();
    private volatile boolean doing = true;
    private boolean state = true;
    private boolean encrypt = true;

    public SendOutProcess() {
        this.state = ImSdkConfigManager.getInstance().needState();
        this.encrypt = ImSdkConfigManager.getInstance().needEncrypt();

        init();
    }

    /**
     * 普通消息加入发送队列
     * @param wrapper 待发送消息
     */
    public void add(MessageWrapper wrapper) {
        SendOutNode node = new SendOutNode(SentType.NORMAL);
        node.setMessage(wrapper);
        sendOutQueue.offer(node);
        synchronized (sendOutQueue) {
            sendOutQueue.notifyAll();
        }
    }

    /**
     * 状态消息加入发送队列
     * @param state 状态消息
     */
    public void add(IMState state) {
        SendOutNode node = new SendOutNode(SentType.STATE);
        node.setState(state);
        sendOutQueue.offer(node);
        synchronized (sendOutQueue) {
            sendOutQueue.notifyAll();
        }
    }

    /**
     * 状态消息列表加入发送队列
     * @param states 状态消息
     */
    public void add(List<IMState> states) {
        if (!state) {
            return;
        }

        if (states == null || states.isEmpty()) {
            return;
        }

        for (IMState state : states) {
            SendOutNode node = new SendOutNode(SentType.STATE);
            node.setState(state);
            synchronized (sendOutQueue) {
                sendOutQueue.offer(node);
            }
        }

        synchronized (sendOutQueue) {
            if (sendOutQueue.size() > 0) {
                sendOutQueue.notifyAll();
            }
        }
    }

    /**
     * 停止发送消息
     */
    public void stop() {
        doing = false;
        synchronized (sendOutQueue) {
            sendOutQueue.notifyAll();
        }
        if (sendOutThread != null) {
            try {
                sendOutThread.interrupt();
            } catch (Exception e) {
//                e.printStackTrace();
                Logger.getLogger().d("Stop send message !!!");
            }
        }
    }


    /**
     * 初始化处理线程
     */
    private void init() {
        if (sendOutThread == null) {
            sendOutThread = new Thread(new SendOutRunnable());
            sendOutThread.setName("sendOutThread");
            sendOutThread.setDaemon(true);
            sendOutThread.start();
        } else if (!sendOutThread.isAlive()) {
            sendOutThread.start();
        }
    }

    /**
     * 普通消息发送
     * @param wrapper 消息
     */
    private void sendNormal(MessageWrapper wrapper) {
        // 发送图片，检测图片大小
        if (!checkFileSize(wrapper)) {
            return;
        }

        // 发送文件，检测应用版本号
        if (!checkVersion(wrapper)) {
            // 版本号检测失败，直接返回
            return;
        }

        // 加密
        wrapper = encryptMessage(wrapper);

        if (wrapper == null) {
            return;
        }

        if (wrapper.isText()) {
            sendText(wrapper);
        }

        if (wrapper.isFile()) {
            sendFile(wrapper);
        }

        if (wrapper.isWeb()) {
            if (wrapper.isExistFile()) {
                sendFile(wrapper);
            } else {
                sendText(wrapper);
            }
        }
    }

    /**
     * 状态消息发送
     * @param state 状态
     */
    private void sendState(IMState state) {
        if (state == null) {
            return;
        }

        ImMsgManager.getInstance().sendState(state.getContent(), state.getId());
    }

    /**
     * 发送文本消息
     * @param wrapper 文本
     */
    private void sendText(MessageWrapper wrapper) {
        if (wrapper == null) {
            return;
        }

        long sendId = ToolUtils.getLong(wrapper.getMsgEntryDb().getId());
        MsgBean msgBean = ModelMapper.getIns().mapTextBean(wrapper);
        if (msgBean == null) {
            return;
        }
        ImMsgManager.getInstance().sendText(msgBean, sendId);
    }

    /**
     * 发送文件文本消息
     * @param wrapper 文件文本
     */
    public void sendFileText(MessageWrapper wrapper) {
        if (wrapper == null || wrapper.getMsgEntryDb() == null) {
            return;
        }

        long sendId = ToolUtils.getLong(wrapper.getMsgEntryDb().getId());
        MsgBean msgBean = ModelMapper.getIns().mapFileBean(wrapper);
        if (msgBean == null) {
            return;
        }
        ImMsgManager.getInstance().sendText(msgBean, sendId);
    }

    /**
     * 发送文件。发送策略：
     * @param wrapper 文件
     */
    private void sendFile(MessageWrapper wrapper) {
        ImSdkFileManager.getInstance().uploadFile(wrapper);
    }

    /**
     * 校验文件大小
     * @param wrapper 消息
     * @return boolean
     */
    private boolean checkFileSize(MessageWrapper wrapper) {
        if (wrapper == null || wrapper.getMsgEntryDb() == null) {
            return false;
        }

        long id = wrapper.getMsgEntryDb().getId();

        if (wrapper.isFile()) {
            FileMsgDb fileMsgDb = wrapper.getFileMsgDb();
            long size = ImSdkConfigManager.getInstance().getSize();
            if (fileMsgDb != null && fileMsgDb.isNormal()) {
                if (fileMsgDb.getFile_size() > size) {
                    updateMState(id, State.FAIL_VALID);

                    wrapper.getMsgEntryDb().setState(State.FAIL_VALID);
                    callbackFail(wrapper);
                    return false;
                }
            }

            if (fileMsgDb != null && fileMsgDb.isImage()) {
                RawFileDb rawFileDb = wrapper.getRawFileDb();
                if (rawFileDb != null && rawFileDb.getRaw_file_size() > size) {
                    updateMState(id, State.FAIL_VALID);

                    wrapper.getMsgEntryDb().setState(State.FAIL_VALID);
                    callbackFail(wrapper);
                    return false;
                }
            }

            if (fileMsgDb != null && fileMsgDb.isVideo()) {
                // TODO: 2017/2/20 liming
            }

        }
        return true;
    }

    /**
     * 检测版本号
     * @param wrapper 消息
     * @return boolean
     */
    private boolean checkVersion(MessageWrapper wrapper) {
        if (wrapper == null || wrapper.getMsgEntryDb() == null) {
            return false;
        }
        long id = wrapper.getMsgEntryDb().getId();

        if (wrapper.isFile() || wrapper.isWeb()) {
            FileMsgDb fileMsgDb = wrapper.getFileMsgDb();
            if (fileMsgDb != null && (fileMsgDb.isNormal() || fileMsgDb.isVideo())) {
                if (Constant.CHECK_VERSION) {
                    String account = wrapper.getMsgEntryDb().getReceiver();
                    int code = ImSdkCallbackManager.getInstance().callVersion(account);
                    if (code == Constant.CHECK_SUCCESS) {
                        // 版本号检测通过，继续发送
                        // 更新消息状态，通知上层处理，更新界面，发送通知消息
                        wrapper.getMsgEntryDb().setState(State.DEFAULT);
                        return true;
                    }

                    if (code == Constant.CHECK_FAIL) {
                        // 版本号检测不通过，消息发送失败
                        // 更新消息状态，通知上层处理，更新界面，发送通知消息
                        updateMState(id, State.CHECK_FAIL);

                        wrapper.getMsgEntryDb().setState(State.CHECK_FAIL);
                        callbackFail(wrapper);
                        return false;
                    }

                    if (code == Constant.CHECK_ERROR) {
                        // 版本检测失败，更新消息状态失败，通知上层处理，更新界面，但是不发送消息
                        updateMState(id, State.CHECK_ERROR);

                        wrapper.getMsgEntryDb().setState(State.CHECK_ERROR);
                        callbackFail(wrapper);
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * 消息加密处理
     * @param wrapper wrapper
     * @return MessageWrapper
     */
    private MessageWrapper encryptMessage(MessageWrapper wrapper) {
        if (wrapper == null || wrapper.getMsgEntryDb() == null) {
            return null;
        }

        if (!encrypt) {
            updateFState(wrapper);
            return wrapper;
        }

        if (wrapper.getMsgEntryDb().isCustom()) {
            // 自定义通知消息，无需加密，直接发送
            updateFState(wrapper);
            return wrapper;
        }

        if (!wrapper.getMsgEntryDb().notEncrypt()) {
            // 已加密，直接发送
            updateFState(wrapper);
            return wrapper;
        }

        if (fileEncrypt(wrapper)) {
            // 文件已加密，直接发送
            updateFState(wrapper);
            return wrapper;
        }

        if (webEncrypt(wrapper)) { // TODO: 2017/3/29 ycm
            updateFState(wrapper);
            return wrapper;
        }

        boolean result = encrypt(wrapper);

        // 加密成功，更新数据库的state，content
        // 加密失败，更新数据库的state
        updateEncrypt(wrapper, result);

        if (!result) {
            //加密失败，回调刷新上层

            Logger.getLogger().d("encrypt fail!!!");

            wrapper.getMsgEntryDb().setState(State.ENCRYPT_FAIL);
            callbackFail(wrapper);
            return null;
        } else {
            wrapper = ImSdkDbUtils.queryMessage(
                    OptHelper.getIns().getAMQuery(wrapper.getMsgEntryDb().getId()), OptType.MQuery.ALL);
            return wrapper;
        }
    }

    /**
     * 回调上层进行加密
     * @param wrapper 待加密消息
     * @return 加密后的消息
     */
    private boolean encrypt(MessageWrapper wrapper) {
        MsgEntryDb msg = wrapper.getMsgEntryDb();
        SecurityPara para = ModelMapper.getIns().getSecurityPara(msg);
        if (para == null) {
            return false;
        }

        if (msg.isText()) {
            SecurityResult text = ImSdkCallbackManager.getInstance().callEncryptText(msg.getContent(), para);

            if (text == null || !text.success()) {
                return false;
            }

            msg.setContent(text.getResult());
            msg.setState(State.SENT_NON);
            return true;
        }

        // add by ycm 2017/4/1 for sharing web message [start]
        if (msg.isWeb()) {
            SecurityResult text = ImSdkCallbackManager.getInstance().callEncryptText(msg.getContent(), para);

            if (text == null || !text.success()) {
                return false;
            }

            msg.setContent(text.getResult());

            FileMsgDb file = wrapper.getFileMsgDb();
            if (file != null) {
                int type = file.getType();
                String name = file.getFile_name();
                String showPath = file.getFile_path();
                String showEncrypt;
                showEncrypt = FileUtils.getCachePath(type, name);

                SecurityResult show = ImSdkCallbackManager.getInstance().callEncryptFile(showPath, showEncrypt, para);

                if (show == null || !show.success()) {
                    return false;
                }

                file.setEncrypt_path(show.getResult());
                file.setEncrypt_size(FileSizeUtils.getFileSize(show.getResult()));
            }

            msg.setState(State.SENT_NON);
            return true;
        }
        // add by ycm 2017/4/1 for sharing web message [end]

        if (msg.isFile()) {
            FileMsgDb file = wrapper.getFileMsgDb();

            if (file == null) {
                return false;
            }
            int type = file.getType();
            String name = file.getFile_name();
            String showPath = file.getFile_path();
            String showEncrypt;

            showEncrypt = FileUtils.getCachePath(type, name);

            SecurityResult show = ImSdkCallbackManager.getInstance().callEncryptFile(showPath, showEncrypt, para);

            if (show == null || !show.success()) {
                return false;
            }

            file.setEncrypt_path(show.getResult());
            file.setEncrypt_size(FileSizeUtils.getFileSize(show.getResult()));

            HdThumbFileDb hdFile = wrapper.getHdThumbFileDb();
            if (hdFile != null) {
                String hdPath = hdFile.getHd_file_path();
                String hdName = hdFile.getHd_file_name();

                String hdEncrypt;

                hdEncrypt = FileUtils.getCachePath(type, hdName);
                SecurityResult hd = ImSdkCallbackManager.getInstance().callEncryptFile(hdPath, hdEncrypt, para);

                if (hd == null || !hd.success()) {
                    return false;
                }

                hdFile.setHd_encrypt_path(hd.getResult());
                hdFile.setHd_encrypt_size(FileSizeUtils.getFileSize(hd.getResult()));
            }

            RawFileDb rawFile = wrapper.getRawFileDb();
            if (rawFile != null) {
                String rawPath = rawFile.getRaw_file_path();
                String rawName = rawFile.getRaw_file_name();
                String rawEncrypt = FileUtils.getCachePath(type, rawName);
                SecurityResult raw = ImSdkCallbackManager.getInstance().callEncryptFile(rawPath, rawEncrypt, para);

                if (raw == null || !raw.success()) {
                    return false;
                }

                rawFile.setRaw_encrypt_path(raw.getResult());
                rawFile.setRaw_encrypt_size(FileSizeUtils.getFileSize(raw.getResult()));
            }

            msg.setState(State.SENT_NON);
            return true;
        }

        return false;
    }

    /**
     * 判断文件是否已加密
     * @param wrapper wrapper
     * @return boolean
     */
    private boolean fileEncrypt(MessageWrapper wrapper) {
        if (wrapper.getMsgEntryDb().isFile()) {
            FileMsgDb file = wrapper.getFileMsgDb();

            if (file == null) {
                return true;
            }
            if (file.getFile_state() > FileTState.ENCRYPT_FAIL) {
                return true;
            }
        }

        return false;
    }

    // add by ycm 2017/4/1 for sharing web message [start]
    /**
     * 判断网页是否已加密
     * @param wrapper wrapper
     * @return boolean
     */
    private boolean webEncrypt(MessageWrapper wrapper) {
        if (!wrapper.isWeb()) {
            return false;
        }
        // 先判断文件是否加密
        if (wrapper.getFileMsgDb() != null) {
            FileMsgDb file = wrapper.getFileMsgDb();
            if (file == null) {
                return wrapper.getMsgEntryDb().notEncrypt();
            }
            if (file.getFile_state() > FileTState.ENCRYPT_FAIL) {
                return true;
            }
        }
        return false;
    }
    // add by ycm 2017/4/1 for sharing web message [end]

    /**
     * 即将发送，需要对失败的消息状态更新
     * @param message message
     */
    private void updateFState(MessageWrapper message) {
        long id = message.getMsgEntryDb().getId();
        int state = message.getMsgEntryDb().getState();

        if (state > State.DEFAULT) {
            return;
        }

        if (State.NON_FRIENDS == state || state == State.UN_SUPPORT) {
            return;
        }

        updateMState(id, State.DEFAULT);
        message.getMsgEntryDb().setState(State.DEFAULT);
    }

    /**
     * 更新消息状态
     * @param id id
     * @param state state
     */
    private void updateMState(long id, int state) {
        UpdateArgs updateArgs = OptHelper.getIns().getMCUpdate(id, state);
        ImSdkDbUtils.update(updateArgs);
    }

    /**
     * 更新加密后的结果
     * @param message message
     */
    private void updateEncrypt(MessageWrapper message, boolean result) {
        if (message == null || message.getMsgEntryDb() == null) {
            return;
        }
        MsgEntryDb msg = message.getMsgEntryDb();
        long id = msg.getId();

        if (msg.isText()) {
            if (result) {
                // 文本加密成功 [msg state] = State.SENT_NON [content] = 密文
                UpdateArgs updateArgs = OptHelper.getIns().
                        getEUpdate(id, State.SENT_NON, msg.getContent());
                ImSdkDbUtils.update(updateArgs);
                return;
            } else {
                // 文本加密失败 [msg state] = State.ENCRYPT_FAIL
                updateMState(id, State.ENCRYPT_FAIL);
                return;
            }
        }

        //add by ycm 2017/4/1 for sharing web message [start]
        if (msg.isWeb()) {
            if (result) {
                // 加密成功 [msg state] = State.SENT_NON [content] = 密文
                UpdateArgs updateArgs = OptHelper.getIns().getEUpdate(id, State.SENT_NON, msg.getContent());
                ImSdkDbUtils.update(updateArgs);
                updateFile(message, State.SENT_NON, FileTState.ENCRYPT_SUCCESS);
                return;
            } else {
                // 加密失败 [msg state] = State.ENCRYPT_FAIL
                updateMState(id, State.ENCRYPT_FAIL);
                updateFile(message, State.ENCRYPT_FAIL, FileTState.ENCRYPT_FAIL);
                return;
            }
        }
        //add by ycm 2017/4/1 for sharing web message [end]

        if (msg.isFile()) {
            // 文件加密结果
            // [msg state] = State.SENT_NON
            // [file_msg.encrypt_path, file_msg.encrypt_size, file_msg.state]
            // [hd_file.encrypt_path, hd_file.encrypt_size, hd_file.state]
            // [raw_file.encrypt_path, raw_file.encrypt_size, raw_file.state]

            if (result) {
                updateFile(message, State.SENT_NON, FileTState.ENCRYPT_SUCCESS);
            } else {
                updateFile(message, State.ENCRYPT_FAIL, FileTState.ENCRYPT_FAIL);
            }
        }
    }

    /**
     * 更新文件加密结果
     * @param message message
     * @param msgState msgState
     * @param fileState fileState
     */
    private void updateFile(MessageWrapper message, int msgState, int fileState) {
        long id = message.getMsgEntryDb().getId();

        UpdateArgs msgUpdate = OptHelper.getIns().getMCUpdate(id, msgState);

        FileMsgDb file = message.getFileMsgDb();
        if (file == null) {
            ImSdkDbUtils.update(msgUpdate);
            return;
        }

        UpdateArgs fileUpdate = OptHelper.getIns().getEFUpdate(FileType.IS_SHOW,
                file.getEncrypt_path(), file.getEncrypt_size(), fileState, id);

        UpdateArgs hdUpdate = null;
        UpdateArgs rawUpdate = null;

        if (file.isImage()) {
            HdThumbFileDb hd = message.getHdThumbFileDb();
            if (hd != null) {
                hdUpdate = OptHelper.getIns().getEFUpdate(FileType.IS_HD, hd.getHd_encrypt_path(),
                                hd.getHd_encrypt_size(), fileState, id);
            }

            RawFileDb raw = message.getRawFileDb();
            if (raw != null) {
                rawUpdate = OptHelper.getIns().
                        getEFUpdate(FileType.IS_RAW, raw.getRaw_encrypt_path(),
                                raw.getRaw_encrypt_size(), fileState, id);
            }
        } else if (file.isVideo()) {
            RawFileDb raw = message.getRawFileDb();
            if (raw != null) {
                rawUpdate = OptHelper.getIns().
                        getEFUpdate(FileType.IS_RAW, raw.getRaw_encrypt_path(),
                                raw.getRaw_encrypt_size(), fileState, id);
            }
        }

        ImSdkDbUtils.updateEF(msgUpdate, fileUpdate, hdUpdate, rawUpdate);
    }

    /**
     * 回调消息状态刷新
     * @param wrapper wrapper
     */
    private void callbackFail(MessageWrapper wrapper) {
        String tag = wrapper.getMsgEntryDb().getSession_flag();
        SessionWrapper sWrapper = ImSdkDbUtils.querySession(OptHelper.getIns().getSMQuery(tag), OptType.SQuery.HAVE);
        IMSession session = ModelMapper.getIns().mapSession(sWrapper);
        IMMessage message = ModelMapper.getIns().mapMessage(wrapper);
        ImSdkCallbackManager.getInstance().callChange(session, message);
    }

    /**
     * 从队列中取消息
     * @return 消息
     */
    private SendOutNode getNode() {
        SendOutNode node;

        while ((node = sendOutQueue.poll()) == null) {
            try {
                synchronized (sendOutQueue) {
                    sendOutQueue.wait();
                }
            }
            catch (InterruptedException ie) {
                // Do nothing
            }
        }
        return node;
    }

    /**
     * 消息发送线程
     */
    protected class SendOutRunnable implements Runnable {
        @Override
        public void run() {
            while (doing) {
                SendOutNode node = getNode();
                if (node == null) {
                    return;
                }

                switch (node.getType()) {
                    case NORMAL:
                        MessageWrapper message = node.getMessage();
                        sendNormal(message);
                        break;
                    case STATE:
                        IMState state = node.getState();
                        sendState(state);
                        break;
                }
            }
        }
    }
}
