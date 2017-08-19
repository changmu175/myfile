package com.xdja.imp.data.callback;

import android.text.TextUtils;

import com.xdja.comm.event.BusProvider;
import com.xdja.comm.event.TicketAuthErrorEvent;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.data.cache.UserCache;
import com.xdja.imp.data.entity.mapper.DataMapper;
import com.xdja.imp.data.entity.mapper.ValueConverter;
import com.xdja.imp.data.repository.SecurityImp;
import com.xdja.imp.data.utils.AppVersionHelper;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.model.TalkListBean;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.domain.repository.IMProxyCallBack;
import com.xdja.imp.domain.repository.SecurityRepository;
import com.xdja.imsdk.callback.CallbackFunction;
import com.xdja.imsdk.callback.IMFileInfoCallback;
import com.xdja.imsdk.callback.IMMessageCallback;
import com.xdja.imsdk.callback.IMSecurityCallback;
import com.xdja.imsdk.callback.IMSessionCallback;
import com.xdja.imsdk.constant.ChangeAction;
import com.xdja.imsdk.constant.ImSdkFileConstant;
import com.xdja.imsdk.constant.StateCode;
import com.xdja.imsdk.model.IMFileInfo;
import com.xdja.imsdk.model.IMMessage;
import com.xdja.imsdk.model.IMSession;
import com.xdja.imsdk.security.SecurityResult;
import com.xdja.imsdk.security.SecurityPara;
import com.xdja.imsdk.model.body.IMFileBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * <p>Summary:IMSDK回调句柄</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.params</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/19</p>
 * <p>Time:14:54</p>
 */
public class IMSDKCallBack implements CallbackFunction,
        IMFileInfoCallback,
        IMSessionCallback,
        IMMessageCallback,
        IMSecurityCallback {

    private IMProxyCallBack callBack;
    private DataMapper mapper;
    private SecurityRepository repository;
    private UserCache userCache;

    @Inject
    public IMSDKCallBack(IMProxyCallBack callBack,
                         DataMapper mapper,
                         SecurityRepository repository,
                         UserCache userCache) {
        this.callBack = callBack;
        this.mapper = mapper;
        this.repository = repository;
        this.userCache = userCache;
    }

    @Override
    public int NewIMMessageCome(IMSession session, List<IMMessage> messageList) {

        if (messageList == null || messageList.size() < 0 || session == null) {
            return ConstDef.CALLBACK_NOT_HANDLED;
        }

        TalkListBean listBean = mapper.mapTalkBean(session);


        List<TalkMessageBean> talkMessageBeanList = new ArrayList<>();
        for(int i = 0; i < messageList.size(); i++){
            TalkMessageBean talkMessageBean = mapper.mapMessage(messageList.get(i));
            talkMessageBeanList.add(talkMessageBean);
        }
        LogUtil.getUtils().d("4966 IMSDKCallBack NewIMMessageCome " + listBean.getTalkType());
        return callBack.onRemainNewMessage(listBean, talkMessageBeanList);
    }

    @Override
    public int ImSdkStateChange(int code) {
        if (code == StateCode.SDK_SERVICE_OK || code == StateCode.SDK_CONNECTED_OK) {
            return callBack.onInitFinished();
        } else if (code == StateCode.SDK_TICKET_EXPIRE) {
            LogUtil.getUtils().d("-----------------Im ticket invalid------------------");
            BusProvider.getMainProvider().post(new TicketAuthErrorEvent());
        } else {
            // TODO: 2016/12/24 liming 其他异常，未处理
        }
        return 0;
    }

    /**
     * 检测应用版本号
     *
     * @return int
     */
    @Override
    public int CheckVersion(String account) {
        return AppVersionHelper.getHelper().requestAppVersion(account, userCache.get().getTicket());
    }

    @Override
    public int SendFileProgressUpdate(IMFileInfo file) {
        if(verifyFileCallback(file)){
            return ConstDef.CALLBACK_NOT_HANDLED;
        }

        IMMessage message = file.getMessage();
        //短视频原文件只更新进度
        IMFileBody fileBody = (IMFileBody) message.getMessageBody();
        if (fileBody.isVideo() && file.getFileType() != ImSdkFileConstant.FileType.IS_RAW) {
            return 0;
            }

        FileInfo fileInfo = mapper.mapFileInfo(file);
        if (fileInfo == null) {
            return 0;
        }

        return callBack.onSendFileProgressUpdate(
                file.getTag(),
                message.getIMMessageId(),
                fileInfo,
                file.getPercent()
        );
    }

    @Override
    public int SendFileFail(IMFileInfo file) {
        if(verifyFileCallback(file)){
            return ConstDef.CALLBACK_NOT_HANDLED;
        }

        IMMessage message = file.getMessage();
        FileInfo fileInfo = mapper.mapFileInfo(file);
        int code = ValueConverter.fileStateConvert(file.getState());
        //modified by ycm for sharing web message 2017/4/5 [start]
        if (fileInfo == null) {
            fileInfo = mapper.mapWebFileInfo(file);
            if (fileInfo == null) {
                return 0;
            }
        }
        //modified by ycm for sharing web message 2017/4/5 [end]

        return callBack.onSendFileFaild(
                file.getTag(),
                message.getIMMessageId(),
                fileInfo,
                code
        );
    }

    @Override
    public int SendFileFinish(IMFileInfo file) {
        if(verifyFileCallback(file)){
            return ConstDef.CALLBACK_NOT_HANDLED;
        }

        IMMessage message = file.getMessage();
        FileInfo fileInfo = mapper.mapFileInfo(file);
        if (fileInfo == null) {
            return 0;
        }

        return callBack.onSendFileFinished(
                file.getTag(),
                message.getIMMessageId(),
                fileInfo
        );
    }

    @Override
    public int ReceiveFileProgressUpdate(IMFileInfo file) {
        if(verifyFileCallback(file)){
            return ConstDef.CALLBACK_NOT_HANDLED;
        }

        IMMessage message = file.getMessage();

        //短视频原文件只更新进度
        IMFileBody fileBody = (IMFileBody) message.getMessageBody();
        if (fileBody.isVideo() && file.getFileType() != ImSdkFileConstant.FileType.IS_RAW) {
            return 0;
        }

        FileInfo fileInfo = mapper.mapFileInfo(file);
        if (fileInfo == null) {
            return 0;
        }

        return callBack.onReceiveFileProgressUpdate(
                file.getTag(),
                message.getIMMessageId(),
                fileInfo,
                file.getPercent()
        );
    }

    @Override
    public int ReceiveFileFail(IMFileInfo file) {
        if(verifyFileCallback(file)){
            return ConstDef.CALLBACK_NOT_HANDLED;
        }

        IMMessage message = file.getMessage();
        FileInfo fileInfo = mapper.mapFileInfo(file);
        int code = ValueConverter.fileStateConvert(file.getState());
        //modified by ycm for sharing web message 2017/4/5 [start]
        if (fileInfo == null) {
            fileInfo = mapper.mapWebFileInfo(file);
            if (fileInfo == null) {
                return 0;
            }
        }
        //modified by ycm for sharing web message 2017/4/5 [end]

        return callBack.onReceiveFileFaild(
                file.getTag(),
                message.getIMMessageId(),
                fileInfo,
                code
        );
    }

    @Override
    public int ReceiveFileFinish(IMFileInfo fileCallbackInfo) {
        if(verifyFileCallback(fileCallbackInfo)){
            return ConstDef.CALLBACK_NOT_HANDLED;
        }

        IMMessage message = fileCallbackInfo.getMessage();
        FileInfo fileInfo = mapper.mapFileInfo(fileCallbackInfo);
        //modified by ycm for sharing web message 2017/4/5 [start]
        if (fileInfo == null) {
            fileInfo = mapper.mapWebFileInfo(fileCallbackInfo);
            if (fileInfo == null) {
                return 0;
            }
        }
        //modified by ycm for sharing web message 2017/4/5 [end]

        return callBack.onReceiveFileFinished(
                fileCallbackInfo.getTag(),
                message.getIMMessageId(),
                fileInfo
        );
    }

    @Override
    public int ReceiveFilePaused(IMFileInfo fileCallbackInfo) {
        if(verifyFileCallback(fileCallbackInfo)){
            return ConstDef.CALLBACK_NOT_HANDLED;
        }

        IMMessage message = fileCallbackInfo.getMessage();
        FileInfo fileInfo = mapper.mapFileInfo(fileCallbackInfo);
        if (fileInfo == null) {
            return 0;
        }
        return callBack.onReceiveFilePaused(
                fileCallbackInfo.getTag(),
                message.getIMMessageId(),
                fileInfo
        );
    }

    @Override
    public int IMMessageListChange(IMSession session, List<IMMessage> message, ChangeAction action) {

        List<TalkMessageBean> msgBeanList = new ArrayList<>();
        for (int i = 0; i < message.size(); i++) {
            TalkMessageBean talkMessageBean = mapper.mapMessage(message.get(i));
            msgBeanList.add(talkMessageBean);
        }

        TalkMessageBean talkMessageBean = msgBeanList.get(0);
        TalkListBean talkListBean = mapper.mapTalkBean(session);
        switch (action){
            case ACT_ADD:
                return callBack.onReceiveNewMessage(talkListBean.getTalkerAccount(), msgBeanList);
            case ACT_DEL:
                return callBack.onDeleteMessage(talkListBean.getTalkerAccount(), talkMessageBean);
            case ACT_RF:
                return callBack.refreshMessageList();
            case ACT_SC:
                if(!talkMessageBean.isMine()
                        && talkMessageBean.isBomb()
                        && talkMessageBean.getMessageState() == ConstDef.STATE_DESTROY){
                    talkMessageBean.setMessageState(ConstDef.STATE_DESTROYING);
                }
                return callBack.onRefreshSingleMessage(talkListBean.getTalkerAccount(), talkMessageBean);
            default:
                return ConstDef.CALLBACK_NOT_HANDLED;
        }
    }

    /**
     * 加密回调接口
     *
     * @param source 待加密数据
     * @param para   加密参数
     * @return 加密结果
     */
    @Override
    public SecurityResult EncryptText(String source, SecurityPara para) {
        SecurityResult result = new SecurityResult(SecurityResult.SECURITY_NON);

        if (TextUtils.isEmpty(source)) {
            result.setResult(source);
            return result;
        }

        String to = para.getPerson();
        boolean isGroup = para.isGroup();

        Map<String, Object> text = repository.encryptText(source, to, isGroup);

        return getSecurityResult(text);
    }

    /**
     * 加密文件回调接口
     *
     * @param source 待加密文件
     * @param dest   加密后文件路径
     * @param para   加密参数
     * @return 加密结果
     */
    @Override
    public SecurityResult EncryptFile(String source, String dest, SecurityPara para) {
        SecurityResult result = new SecurityResult(SecurityResult.SECURITY_NON);

        if (TextUtils.isEmpty(source)) {
            result.setResult(source);
            return result;
        }

        String to = para.getPerson();
        boolean isGroup = para.isGroup();

        Map<String, Object> file = repository.encryptAsync(source, dest, to, isGroup);

        return getSecurityResult(file);
    }

    /**
     * 解密文本调接口
     *
     * @param source 待解密数据
     * @param para   解密参数
     * @return 解密结果
     */
    @Override
    public SecurityResult DecryptText(String source, SecurityPara para) {
        SecurityResult result = new SecurityResult(SecurityResult.SECURITY_NON);

        if (TextUtils.isEmpty(source)) {
            result.setResult(source);
            return result;
        }

        long msgId = para.getMsgId();
        String from = para.getUser();
        String to = para.getPerson();
        boolean isGroup = para.isGroup();

        Map<String, Object> text = repository.decryptText(source, msgId, from, to, isGroup);

        return getSecurityResult(text);
    }

    /**
     * 解密文件回调接口
     *
     * @param source 待解密文件
     * @param dest   解密后文件路径
     * @param para   解密参数
     * @return 解密结果
     */
    @Override
    public SecurityResult DecryptFile(String source, String dest, SecurityPara para) {
        SecurityResult result = new SecurityResult(SecurityResult.SECURITY_NON);

        if (TextUtils.isEmpty(source)) {
            result.setResult(source);
            return result;
        }

        String to = para.getPerson();
        boolean isGroup = para.isGroup();

        Map<String, Object> file = repository.decryptAsync(source, dest, to, isGroup);

        return getSecurityResult(file);
    }

    @Override
    public int IMSessionListChange(final IMSession ims, ChangeAction action) {
        TalkListBean talkListBean = mapper.mapTalkBean(ims);
        if(talkListBean == null){
            return ConstDef.CALLBACK_NOT_HANDLED;
        }
        LogUtil.getUtils().d(talkListBean.toString());
        switch (action){
            case ACT_ADD:
                return callBack.onCreateNewTalk(talkListBean);
            case ACT_DEL:
                return callBack.onDeleteTalk(talkListBean);
            case ACT_RF:
            case ACT_SC:
                return callBack.refreshSingleTalk(talkListBean);
            default:
                return ConstDef.CALLBACK_NOT_HANDLED;
        }
    }

    /**
     * 校验文件操作回调参数
     * @param fileCallbackInfo 文件回调参数
     * @return boolean
     */
    private boolean verifyFileCallback(IMFileInfo fileCallbackInfo) {// modified by ycm for lint 2017/02/16
        return fileCallbackInfo == null ||
                fileCallbackInfo.getMessage() == null ||
                fileCallbackInfo.getMessage().getMessageBody() == null;
    }

    /**
     * 解析加解密结果
     * @param security security
     * @return SecurityResult
     */
    private SecurityResult getSecurityResult(Map<String, Object> security) {
        SecurityResult securityResult = new SecurityResult(SecurityResult.SECURITY_NON);

        int code = (int) security.get(SecurityImp.SECURITY_CODE);
        String result = (String) security.get(SecurityImp.SECURITY_RESULT);

        if (code != SecurityImp.SECURITY_SUCCESS) {
            return getFailResult(code, result);
        }

        securityResult.setResult(result);
        securityResult.setCode(SecurityResult.SECURITY_SUCCESS);
        return securityResult;
    }

    /**
     * 生成失败结果
     * @param code code
     * @param source source
     * @return SecurityResult
     */
    private SecurityResult getFailResult(int code, String source) {
        SecurityResult result = new SecurityResult(SecurityResult.SECURITY_NON);

        if (code == SecurityImp.SECURITY_FAIL) {
            result.setCode(SecurityResult.SECURITY_FAIL);
        }

        if (code == SecurityImp.NO_DECRYPT) {
            result.setCode(SecurityResult.SECURITY_NON);
        }

        result.setResult(source);
        return result;
    }

}
