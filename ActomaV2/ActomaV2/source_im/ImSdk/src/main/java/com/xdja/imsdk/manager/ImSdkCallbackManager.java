package com.xdja.imsdk.manager;

import android.content.Context;

import com.xdja.imsdk.callback.CallbackFunction;
import com.xdja.imsdk.callback.IMFileInfoCallback;
import com.xdja.imsdk.callback.IMMessageCallback;
import com.xdja.imsdk.callback.IMSecurityCallback;
import com.xdja.imsdk.callback.IMSessionCallback;
import com.xdja.imsdk.constant.ChangeAction;
import com.xdja.imsdk.constant.internal.Constant.FileCallType;
import com.xdja.imsdk.model.IMFileInfo;
import com.xdja.imsdk.model.IMMessage;
import com.xdja.imsdk.model.IMSession;
import com.xdja.imsdk.security.SecurityResult;
import com.xdja.imsdk.security.SecurityPara;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：  回调管理类                                    <br>
 * 创建时间：2016/11/27 下午6:18                           <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public class ImSdkCallbackManager {
    private static ImSdkCallbackManager instance;

    private Map<String, IMSessionCallback> sessionCallback =
            new HashMap<>();                                    //会话回调map表
    private Map<String, IMMessageCallback> messageCallback =
            new HashMap<>();                                    //消息回调map表
    private Map<String, IMFileInfoCallback> fileInfoCallback =
            new HashMap<>();                                    //文件信息回调map表

    private CallbackFunction callbackFunction;                  //ImSdk状态回调

    private IMSecurityCallback securityCallback;                //加解密回调

    public static ImSdkCallbackManager getInstance() {
        synchronized (ImSdkCallbackManager.class) {
            if (instance == null) {
                instance = Factory.getInstance();
            }
        }
        return instance;
    }

    private static class Factory {
        static ImSdkCallbackManager getInstance() {
            return new ImSdkCallbackManager();
        }
    }

    public void cancelAllCallback() {
        sessionCallback.clear();
        messageCallback.clear();
        fileInfoCallback.clear();
        callbackFunction = null;
        securityCallback = null;
        instance = null;
    }

    public void registerCallbackFunction(CallbackFunction callback) {
        this.callbackFunction = callback;
    }

    public void registerSecurityCallback(IMSecurityCallback callback) {
        this.securityCallback = callback;
    }

    public void registerSessionCallback(Context context, IMSessionCallback callback) {
        if (context == null || callback == null) {
            return;
        }
        sessionCallback.put(context.getClass().getName(), callback);
    }

    public void unregisterSessionCallback(Context context) {
        if (context == null) {
            return;
        }
        sessionCallback.remove(context.getClass().getName());
    }

    public void registerMessageCallback(Context context, IMMessageCallback callback) {
        if (context == null || callback == null) {
            return;
        }
        messageCallback.put(context.getClass().getName(), callback);
    }

    public void unregisterMessageCallback(Context context) {
        if (context == null) {
            return;
        }
        messageCallback.remove(context.getClass().getName());
    }

    public void registerFileCallback(Context context, IMFileInfoCallback callback) {
        if (context == null || callback == null) {
            return;
        }
        fileInfoCallback.put(context.getClass().getName(), callback);
    }

    public void unregisterFileCallback(Context context) {
        if (context == null) {
            return;
        }
        fileInfoCallback.remove(context.getClass().getName());
    }

    /**
     * ImSdk的状态的回调
     * @param code 状态码
     */
    public void callState(int code) {
        if (callbackFunction == null) {
            return;
        }
        callbackFunction.ImSdkStateChange(code);
    }

    /**
     * 新消息通知回调
     * @param session 新消息所在会话
     * @param messageList 新消息列表
     */
    public void callNew(IMSession session, List<IMMessage> messageList) {
        if (callbackFunction == null || messageList == null || messageList.isEmpty()) {
            return;
        }

        callbackFunction.NewIMMessageCome(session, messageList);
    }

    /**
     * 检测应用版本号
     * @return int
     */
    public int callVersion(String account) {
        return callbackFunction.CheckVersion(account);
    }

    /**
     *  回调消息的状态刷新，根据配置决定是否回调会话最后一条消息的状态刷新
     *  消息状态刷新只针对单条消息，所以消息列表中只有一条消息
     */
    public void callChange(IMSession session, IMMessage message) {
        if (session == null ||message == null) {
            return;
        }

        List<IMMessage> list = new ArrayList<>();
        list.add(message);
        callMessage(session, list, ChangeAction.ACT_SC);
        if (ImSdkConfigManager.getInstance().needChange() && session.getLastMessage() != null) {
            if (session.getLastMessage().getIMMessageId() == message.getIMMessageId()) {
                callSession(session, ChangeAction.ACT_SC);
            }
        }
    }

    /**
     * 会话的回调
     * @param session 要回调处理的会话
     * @param action 要处理的动作
     * @see com.xdja.imsdk.constant.ChangeAction
     */
    public void callSession(IMSession session, ChangeAction action) {
        Iterator it = sessionCallback.entrySet().iterator();
        if (it.hasNext()) {
            IMSessionCallback callback =(IMSessionCallback) ((Entry) it.next()).getValue();
            callback.IMSessionListChange(session, action);
        }
    }

    /**
     * 消息的回调
     * @param session 消息所在的会话
     * @param messageList 要回调处理的消息列表
     * @param action 要处理的动作
     * @see com.xdja.imsdk.constant.ChangeAction
     */
    public void callMessage(IMSession session, List<IMMessage> messageList, ChangeAction action) {
        Iterator it = messageCallback.entrySet().iterator();
        if (it.hasNext()) {
            IMMessageCallback callback =(IMMessageCallback) ((Entry) it.next()).getValue();
            callback.IMMessageListChange(session, messageList, action);
        }
    }

    /**
     * 加密文本
     * @param source source
     * @param para para
     * @return SecurityResult
     */
    public SecurityResult callEncryptText(String source, SecurityPara para) {
        if (securityCallback == null) {
            return null;
        }

        return securityCallback.EncryptText(source, para);
    }

    /**
     * 加密文件
     * @param source source
     * @param dest dest
     * @param para para
     * @return SecurityResult
     */
    public SecurityResult callEncryptFile(String source, String dest, SecurityPara para) {
        if (securityCallback == null) {
            return null;
        }

        return securityCallback.EncryptFile(source, dest, para);
    }

    /**
     * 解密文本
     * @param source source
     * @param para para
     * @return SecurityResult
     */
    public SecurityResult callDecryptText(String source, SecurityPara para) {
        if (securityCallback == null) {
            return null;
        }

        return securityCallback.DecryptText(source, para);
    }

    /**
     * 解密文件
     * @param source source
     * @param dest dest
     * @param para para
     * @return SecurityResult
     */
    public SecurityResult callDecryptFile(String source, String dest, SecurityPara para) {
        if (securityCallback == null) {
            return null;
        }

        return securityCallback.DecryptFile(source, dest, para);
    }

    /**
     * 回调文件状态，需要放到主线程
     * @param fileInfo fileInfo
     * @param type type
     */
    public void callFile(IMFileInfo fileInfo, FileCallType type) {
        Iterator it = fileInfoCallback.entrySet().iterator();
        if (it.hasNext()) {
            IMFileInfoCallback callback =(IMFileInfoCallback) ((Entry) it.next()).getValue();
            switch (type) {
                case UP_UPDATE:
                    callback.SendFileProgressUpdate(fileInfo);
                    break;
                case UP_FAIL:
                    callback.SendFileFail(fileInfo);
                    break;
                case UP_FINISH:
                    callback.SendFileFinish(fileInfo);
                    break;
                case DOWN_UPDATE:
                    callback.ReceiveFileProgressUpdate(fileInfo);
                    break;
                case DOWN_FAIL:
                    callback.ReceiveFileFail(fileInfo);
                    break;
                case DOWN_FINISH:
                    callback.ReceiveFileFinish(fileInfo);
                    break;
                case DOWN_PAUSE:
                    callback.ReceiveFilePaused(fileInfo);
                    break;
                default:
                    break;
            }
        }
    }

}
