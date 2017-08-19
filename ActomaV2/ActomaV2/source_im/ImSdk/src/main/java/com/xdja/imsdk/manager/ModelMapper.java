package com.xdja.imsdk.manager;

import android.text.TextUtils;
import com.xdja.imsdk.constant.IMFailCode;
import com.xdja.imsdk.constant.IMSessionType;
import com.xdja.imsdk.constant.ImSdkFileConstant;
import com.xdja.imsdk.constant.ImSdkFileConstant.FileState;
import com.xdja.imsdk.constant.ImSdkFileConstant.FileType;
import com.xdja.imsdk.constant.MsgState;
import com.xdja.imsdk.constant.internal.Constant;
import com.xdja.imsdk.constant.internal.Constant.BodyType;
import com.xdja.imsdk.constant.internal.FileTState;
import com.xdja.imsdk.constant.internal.MsgType;
import com.xdja.imsdk.constant.internal.State;
import com.xdja.imsdk.db.bean.*;
import com.xdja.imsdk.db.wrapper.MessageWrapper;
import com.xdja.imsdk.db.wrapper.SessionWrapper;
import com.xdja.imsdk.http.bean.MsgBean;
import com.xdja.imsdk.http.bean.StateBean;
import com.xdja.imsdk.http.file.FileEntry;
import com.xdja.imsdk.logger.Logger;
import com.xdja.imsdk.model.IMFileInfo;
import com.xdja.imsdk.model.IMMessage;
import com.xdja.imsdk.model.IMSession;
import com.xdja.imsdk.model.body.*;
import com.xdja.imsdk.model.file.IMHDThumbFileInfo;
import com.xdja.imsdk.model.file.IMRawFileInfo;
import com.xdja.imsdk.model.internal.IMContent;
import com.xdja.imsdk.model.internal.IMRaw;
import com.xdja.imsdk.model.internal.IMState;
import com.xdja.imsdk.model.internal.extra.VideoExtraInfo;
import com.xdja.imsdk.model.internal.extra.VoiceExtraInfo;
import com.xdja.imsdk.model.internal.extra.WebExtraInfo;
import com.xdja.imsdk.model.internal.old.OldExtra;
import com.xdja.imsdk.model.internal.old.OldFile;
import com.xdja.imsdk.security.SecurityPara;
import com.xdja.imsdk.security.SecurityResult;
import com.xdja.imsdk.util.FileUtils;
import com.xdja.imsdk.util.JsonUtils;
import com.xdja.imsdk.util.RandomStringUtils;
import com.xdja.imsdk.util.ToolUtils;

import java.util.*;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：数据映射                          <br>
 * 创建时间：2016/11/28 19:16                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class ModelMapper {
    private static ModelMapper modelMapper;

    private String account = "";

    public static ModelMapper getIns(){
        synchronized(ModelMapper.class) {
            if(modelMapper == null){
                modelMapper =  Factory.getInstance();
            }
        }
        return modelMapper;
    }

    private static class Factory {
        static ModelMapper getInstance() {
            return new ModelMapper();
        }
    }

    public void init(String account) {
        if (!TextUtils.isEmpty(account)) {
            this.account = account;
        }
    }

    /**
     * IMMessage ==> new SessionWrapper
     * @param message message
     * @return SessionWrapper
     */
    public SessionWrapper getSWrapper(String tag, IMMessage message) {
        SessionWrapper wrapper = new SessionWrapper();

        SessionEntryDb session = new SessionEntryDb();

        session.setSession_flag(tag);

        //from
        if (TextUtils.isEmpty(message.getFrom())) {
            message.setFrom(account);
        }

        //im partner
        session.setIm_partner(getImPartner(message));

        //type
        if (message.isGroupIMMessage()) {
            session.setSession_type(IMSessionType.SESSION_GROUP);
        } else {
            session.setSession_type(IMSessionType.SESSION_SINGLE);
        }
        session.setSession_flag(tag);
        session.setLast_msg(0L);
        session.setStart_time(ImMsgManager.getInstance().getCurrentM());
        session.setLast_time(0L);
        session.setReminded(0);

        wrapper.setSessionEntryDb(session);

        return wrapper;
    }

    /**
     * MessageWrapper ==> new IMState
     * @param wrappers wrappers
     * @return List
     */
    public List<IMState> getStates(List<MessageWrapper> wrappers) {
        List<StateBean> beans = new ArrayList<>();

        for (MessageWrapper msg : wrappers) {
            StateBean bean = new StateBean();
            MsgEntryDb entry = msg.getMsgEntryDb();
            if (entry != null) {
                bean.setF(account);
                bean.setC(String.valueOf(entry.getServer_id()));
                bean.setStat(entry.getState());

                if (entry.isGroup()) {
                    bean.setTo(entry.getReceiver());
                    bean.setT(MsgType.MSG_TYPE_GROUP + MsgType.MSG_TYPE_STATE);
                    if (entry.getState() < State.READ) {
                        beans.add(bean);
                    }
                } else {
                    bean.setTo(entry.getSender());
                    bean.setT(MsgType.MSG_TYPE_STATE);
                    beans.add(bean);
                }

            }
        }

        return subStates(beans);
    }

    /**
     * MsgBean ==> new SessionWrapper
     * @param msgBean msgBean
     * @return SessionWrapper
     */
    public SessionWrapper getSWrapper(MsgBean msgBean) {
        SessionWrapper wrapper = new SessionWrapper();
        SessionEntryDb session = new SessionEntryDb();

        //im partner
        session.setIm_partner(getImPartner(msgBean));

        //type
        if (msgBean.isGroupMsg()) {
            session.setSession_type(IMSessionType.SESSION_GROUP);//type
        } else {
            session.setSession_type(IMSessionType.SESSION_SINGLE);
        }
        session.setSession_flag(ToolUtils.getSessionTag(session.getIm_partner(), session.getSession_type()));
        session.setLast_msg(0L);
        session.setStart_time(ImMsgManager.getInstance().getCurrentM());
        session.setLast_time(0L);
        session.setReminded(0);

        wrapper.setSessionEntryDb(session);
        return wrapper;
    }

    /**
     * MsgEntryDb ==> SecurityPara
     * @param msg msg
     * @return SecurityPara
     */
    public SecurityPara getSecurityPara(MsgEntryDb msg) {
        SecurityPara para = new SecurityPara();
        if (msg == null) {
            return null;
        }

        para.setMsgId(msg.getId());
        para.setGroup(msg.isGroup());

        if (msg.isGroup()) {
            para.setUser(msg.getSender());
            para.setPerson(msg.getReceiver());
        } else {
            if (TextUtils.equals(msg.getSender(), account)) {
                para.setUser(msg.getSender());
                para.setPerson(msg.getReceiver());
            } else {
                para.setUser(msg.getReceiver());
                para.setPerson(msg.getSender());
            }
        }
        return para;
    }

    /***************************************
     * ************* 会话映射 ************* *
     ***************************************/


    /**
     * IMSession ==> SessionWrapper
     * @param session session
     * @return SessionWrapper
     */
    public SessionWrapper getSWrapper(IMSession session) {
        SessionWrapper wrapper = new SessionWrapper();
        SessionEntryDb entry = new SessionEntryDb();

        entry.setStart_time(ImMsgManager.getInstance().getCurrentM());
        entry.setReminded(session.getRemindCount());
        entry.setIm_partner(session.getImPartner());
        entry.setSession_type(session.getSessionType());
        entry.setLast_msg(0L);
        entry.setSession_flag(ToolUtils.getSessionTag(session.getImPartner(), session.getSessionType()));

        wrapper.setSessionEntryDb(entry);
        return wrapper;
    }

    /**
     * IMMessage ==> new MessageWrapper
     * 添加自定义消息
     * 发送消息
     * @param tag tag
     * @param message message
     * @return MessageWrapper
     */
    public MessageWrapper getMWrapper(String tag, IMMessage message) {
        MessageWrapper wrapper = new MessageWrapper();

        MsgEntryDb msg = new MsgEntryDb();
        msg.setCard_id(message.getCardId());
        msg.setReceiver(message.getTo());
        msg.setType(message.getType());
        msg.setState(message.getState());
        msg.setSession_flag(tag);
        msg.setCreate_time(ImMsgManager.getInstance().getCurrentN());
        msg.setServer_id(-msg.getCreate_time());
        msg.setSort_time(ImMsgManager.getInstance().getCurrentM());
        msg.setAttr(Constant.MSG_SENT_NEW);

        if (message.isBombIMMessage() && message.getTimeToLive() == 0) {
            msg.setLife_time(Constant.BOMB_TIME);
        } else {
            msg.setLife_time(message.getTimeToLive());
        }

        if (TextUtils.isEmpty(message.getFrom())) {
            msg.setSender(account);
        } else {
            msg.setSender(message.getFrom());
        }

        // add by ycm 2017/4/1 for sharing web message [start]
        if (message.isWebIMMessage()) {
            IMWebBody imWebBody = (IMWebBody) message.getMessageBody();
            WebExtraInfo webExtraInfo = new WebExtraInfo(
                    imWebBody.getTitle(),
                    imWebBody.getDescription(),
                    imWebBody.getUrl(),
                    imWebBody.getSource());
            msg.setContent(JsonUtils.getGson().toJson(webExtraInfo));
            FileMsgDb file = new FileMsgDb();
            if (!TextUtils.isEmpty(imWebBody.getLocalPath())) {
                file.setFile_path(imWebBody.getLocalPath());
                file.setFile_name(imWebBody.getDisplayName());
                file.setFile_size(imWebBody.getFileSize());
                file.setType(0);
                file.setFile_state(FileTState.UP_NON);
                file.setSuffix(imWebBody.getSuffix());
                file.setEncrypt_size(imWebBody.getFileSize());
                file.setTranslate_size(imWebBody.getTranslateSize());
                wrapper.setFileMsgDb(file);
            }
            wrapper.setMsgEntryDb(msg);
            return wrapper;
        }
        // add by ycm 2017/4/1 for sharing web message [end]

        if (message.isTextIMMessage()) {
            IMTextBody textBody = (IMTextBody) message.getMessageBody();
            msg.setContent(textBody.getContent());
            wrapper.setMsgEntryDb(msg);
            return wrapper;
        }  else if (message.isFileIMMessage()) {
            IMFileBody fileBody = (IMFileBody) message.getMessageBody();
            msg.setContent(String.valueOf(fileBody.getType()));

            FileMsgDb file = new FileMsgDb();
            file.setFile_path(fileBody.getLocalPath());
            file.setFile_name(fileBody.getDisplayName());
            file.setFile_size(fileBody.getFileSize());
            file.setType(fileBody.getType());
            file.setFile_state(FileTState.UP_NON);
            file.setSuffix(fileBody.getSuffix());
            file.setEncrypt_size(fileBody.getFileSize());
            file.setTranslate_size(fileBody.getTranslateSize());

            switch (fileBody.getType()) {
                case ImSdkFileConstant.FILE_NORMAL:
                    break;
                case ImSdkFileConstant.FILE_VOICE:
                    IMVoiceBody voiceBody = (IMVoiceBody) message.getMessageBody();
                    VoiceExtraInfo voiceExt = new VoiceExtraInfo(voiceBody.getDuration());
                    file.setExtra_info(JsonUtils.getGson().toJson(voiceExt));

                    break;
                case ImSdkFileConstant.FILE_IMAGE:
                    IMImageBody imageBody = (IMImageBody) message.getMessageBody();
                    IMHDThumbFileInfo hdInfo = imageBody.getHdTFileInfo();
                    if (hdInfo != null) {
                        wrapper.setHdThumbFileDb(mapHd(hdInfo));
                    }

                    IMRawFileInfo iRawInfo = imageBody.getRawFileInfo();
                    if (iRawInfo != null) {
                        wrapper.setRawFileDb(mapRaw(iRawInfo));
                    }
                    break;
                case ImSdkFileConstant.FILE_VIDEO:
                    IMVideoBody videoBody = (IMVideoBody) message.getMessageBody();

                    VideoExtraInfo videoExt = new VideoExtraInfo(videoBody.getDuration(), videoBody.getSize());
                    file.setExtra_info(JsonUtils.getGson().toJson(videoExt));

                    IMRawFileInfo vRawInfo = videoBody.getRawFileInfo();
                    if (vRawInfo != null) {
                        wrapper.setRawFileDb(mapRaw(vRawInfo));
                    }
                    break;
                case ImSdkFileConstant.FILE_UNKNOWN:
                    break;
                default:
                    break;
            }

            wrapper.setMsgEntryDb(msg);
            wrapper.setFileMsgDb(file);
        }
        return wrapper;
    }

    /**
     * new SyncIdDb
     * @param id id
     * @param key key
     * @return SyncIdDb
     */
    public SyncIdDb getSync(long id, String key) {
        SyncIdDb sync = new SyncIdDb();
        sync.setId_value(String.valueOf(id));
        sync.setId_type(key);
        return sync;
    }

    /**
     * SessionWrapper ==> IMSession
     * @param sWrapper sWrapper
     * @return IMSession
     */
    public IMSession mapSession(SessionWrapper sWrapper) {
        IMSession session = new IMSession();
        if (sWrapper == null || sWrapper.getSessionEntryDb() == null) {
            return session;
        }
        SessionEntryDb entry = sWrapper.getSessionEntryDb();

        session.setId(ToolUtils.getLong(entry.getId()));
        session.setImPartner(entry.getIm_partner());
        session.setRemindCount(entry.getReminded());
        session.setSessionTag(entry.getSession_flag());
        session.setSessionType(entry.getSession_type());

        if (sWrapper.getMsgEntryDb() == null) {
            session.setDisplayTime(entry.getStart_time());// TODO: 2016/12/16 liming 待产品确认
            session.setLastMessage(null);
        } else {
            session.setDisplayTime(entry.getLast_time());
            IMMessage message = mapMessage(sWrapper.getMsgEntryDb(), true);

            FileMsgDb file = sWrapper.getFileMsgDb();
            if (file != null && message.isFileIMMessage()) {
                mapFileMessage(message, file);
            }

            if (file != null && message.isWebIMMessage()) {
                mapWebFileMessage(message, file);
            }
            session.setLastMessage(message);
        }

        return session;
    }

    /**
     * SessionWrappers ==> IMSession
     * @param sWrappers sWrappers
     * @return List
     */
    public List<IMSession> mapSessions(List<SessionWrapper> sWrappers) {
        List<IMSession> sessions = new ArrayList<>();
        if (sWrappers == null || sWrappers.isEmpty()) {
            return sessions;
        }
        for (SessionWrapper wrapper : sWrappers) {
            sessions.add(mapSession(wrapper));
        }
        return sessions;
    }

    /**
     * MessageWrapper ==> IMMessage
     * @param mWrapper mWrapper
     * @return IMMessage
     */
    public IMMessage mapMessageNoDecrypt(MessageWrapper mWrapper) {
        IMMessage message = new IMMessage();
        MsgEntryDb msg = mWrapper.getMsgEntryDb();
        if (msg == null) {
            Logger.getLogger().e("wrong message!!!");
            return message;
        }

        message = mapMessage(msg, false);
        if (mWrapper.isFile()) {
            mapFileMessage(mWrapper, message, msg.getContent());
        } else if (mWrapper.isWeb()) {
            mapWebFileMessage(mWrapper, message);
        }
        return message;
    }

    /**
     * MessageWrapper ==> IMMessage
     * @param mWrapper mWrapper
     * @return IMMessage
     */
    public IMMessage mapMessage(MessageWrapper mWrapper) {
        IMMessage message = new IMMessage();
        MsgEntryDb msg = mWrapper.getMsgEntryDb();
        if (msg == null) {
            Logger.getLogger().e("wrong message!!!");
            return message;
        }

        message = mapMessage(msg, true);
        if (mWrapper.isFile()) {
            mapFileMessage(mWrapper, message, msg.getContent());
        }

        if (mWrapper.isWeb()) {
            mapWebFileMessage(mWrapper, message);
        }
        return message;
    }

    /**
     * MessageWrappers ==> IMMessages
     * @param mWrappers mWrappers
     * @return List
     */
    public List<IMMessage> mapMessages(List<MessageWrapper> mWrappers) {
        List<IMMessage> messages = new ArrayList<>();
        if (mWrappers == null || mWrappers.isEmpty()) {
            return messages;
        }

        for (MessageWrapper wrapper : mWrappers) {
            messages.add(mapMessage(wrapper));
        }
        return messages;
    }

    /**
     * MsgEntryDb ==> IMMessage
     * @param entry entry
     * @return IMMessage
     */
    private IMMessage mapMessage(MsgEntryDb entry, boolean decrypt) {
        IMMessage message = new IMMessage();
        message.setIMMessageId(ToolUtils.getLong(entry.getId()));
        message.setCardId(entry.getCard_id());
        message.setFrom(entry.getSender());
        message.setTo(entry.getReceiver());
        message.setType(entry.getType());
        message.setTimeToLive(entry.getLife_time());
        message.setIMMessageTime(entry.getSort_time());

        int state = entry.getState();
        if (state < State.DEFAULT) {
            message.setFailCode(mapFailState(state));
        } else {
            message.setFailCode(IMFailCode.NOT_FAIL);
        }

        message.setState(mapState(state));
        if (entry.isText()) {
            if (decrypt) {
                message.setMessageBody(new IMTextBody(decryptText(entry)));
            } else {
                message.setMessageBody(new IMTextBody(entry.getContent()));
            }
        }

        //add by ycm for sharing web message 2017/4/1 [start]
        if (entry.isWeb()) {
            WebExtraInfo webExtraInfo;
            if (decrypt) {
                webExtraInfo = JsonUtils.mapGson(decryptText(entry), WebExtraInfo.class);
            } else {
                webExtraInfo = JsonUtils.mapGson(entry.getContent(), WebExtraInfo.class);
            }
            if (webExtraInfo != null) {
                message.setMessageBody(new IMWebBody(
                        webExtraInfo.getTitle(),
                        webExtraInfo.getDescription(),
                        webExtraInfo.getUrl(),
                        webExtraInfo.getSource()));
            }
        }
        //add by ycm for sharing web message 2017/4/1 [end]

        if (entry.isFile()) {
            int fileType = Integer.valueOf(entry.getContent());

            switch (fileType) {
                case ImSdkFileConstant.FILE_NORMAL:
                    message.setMessageBody(new IMNormalFileBody(ImSdkFileConstant.FILE_NORMAL));
                    break;
                case ImSdkFileConstant.FILE_VOICE:
                    message.setMessageBody(new IMVoiceBody(ImSdkFileConstant.FILE_VOICE));
                    break;
                case ImSdkFileConstant.FILE_VIDEO:
                    message.setMessageBody(new IMVideoBody(ImSdkFileConstant.FILE_VIDEO));
                    break;
                case ImSdkFileConstant.FILE_IMAGE:
                    message.setMessageBody(new IMImageBody(ImSdkFileConstant.FILE_IMAGE));
                    break;
                case ImSdkFileConstant.FILE_UNKNOWN:
                    message.setMessageBody(new IMFileBody(ImSdkFileConstant.FILE_UNKNOWN));
                    break;
                default:
                    message.setMessageBody(new IMFileBody(ImSdkFileConstant.FILE_UNKNOWN));
                    break;
            }
        }
        return message;
    }

    /**
     * FileMsgDb ==> IMMessage
     * @param message message
     * @param file file
     */
    private void mapFileMessage(IMMessage message, FileMsgDb file) {
        if (file != null && !TextUtils.isEmpty(file.getFile_name())) {
            IMFileBody fileBody = (IMFileBody) message.getMessageBody();

            fileBody.setDisplayName(file.getFile_name());
        }
    }

	//add by ycm for sharing web message 2017/4/1 [start]
    /**
     * FileMsgDb ==> IMMessage
     * @param message message
     * @param file file
     */
    private void mapWebFileMessage(IMMessage message, FileMsgDb file) {
        if (file != null && !TextUtils.isEmpty(file.getFile_name())) {
            IMWebBody fileBody = (IMWebBody) message.getMessageBody();
            if (fileBody != null) {
                fileBody.setDisplayName(file.getFile_name());
            }
        }
    }

    /**
     * MessageWrapper ==> IMMessage
     * @param mWrapper mWrapper
     * @param message message
     */
    private void mapWebFileMessage(MessageWrapper mWrapper, IMMessage message) {
        if (!mWrapper.isWeb()) {
            return;
        }
        FileMsgDb file = mWrapper.getFileMsgDb();
        if (file != null && !TextUtils.isEmpty(file.getFile_path())) {
            IMWebBody fileBody = (IMWebBody) message.getMessageBody();
            if (fileBody != null) {
                fileBody.setLocalPath(file.getFile_path());
                fileBody.setDisplayName(file.getFile_name());
                fileBody.setSuffix(file.getSuffix());
                fileBody.setTranslateSize(file.getTranslate_size());
                fileBody.setFileSize(file.getFile_size());
                fileBody.setState(mapFileState(file.getFile_state()));
                message.setMessageBody(fileBody);
            }
        }
    }
	//add by ycm for sharing web message 2017/4/1 [end]

    /**
     * MessageWrapper ==> IMMessage
     * @param mWrapper mWrapper
     * @param message message
     * @param content content
     */
    private void mapFileMessage(MessageWrapper mWrapper, IMMessage message, String content) {
        if (!mWrapper.isFile()) {
            return;
        }
        FileMsgDb file = mWrapper.getFileMsgDb();
        if (file != null && !TextUtils.isEmpty(file.getFile_path())) {// TODO: 2016/12/26 liming
            IMFileBody fileBody = (IMFileBody) message.getMessageBody();
            fileBody = mapNormalBody(file, fileBody);

            int fileType = ToolUtils.getInt(content);
            String extraInfo = file.getExtra_info();

            switch (fileType) {
                case ImSdkFileConstant.FILE_NORMAL:
                    // TODO: 2016/12/16 liming 后续扩展
//                                NormalExtraInfo normalExtra =
//                                        JsonUtils.mapGson(extraInfo, NormalExtraInfo.class);
                    IMNormalFileBody normalFileBody = (IMNormalFileBody) fileBody;

                    message.setMessageBody(normalFileBody);
                    break;
                case ImSdkFileConstant.FILE_VOICE:
                    if (!TextUtils.isEmpty(extraInfo)) {
                        VoiceExtraInfo voiceExtra =
                                JsonUtils.mapGson(extraInfo, VoiceExtraInfo.class);
                        if (voiceExtra != null) {
                            IMVoiceBody voiceBody = (IMVoiceBody) fileBody;
                            voiceBody.setDuration(voiceExtra.getDuration());
                            message.setMessageBody(voiceBody);
                        }
                    }
                    break;
                case ImSdkFileConstant.FILE_VIDEO:
                    if (!TextUtils.isEmpty(extraInfo)) {
                        VideoExtraInfo videoExtra =
                                JsonUtils.mapGson(extraInfo, VideoExtraInfo.class);
                        if (videoExtra != null) {
                            IMVideoBody videoBody = (IMVideoBody) fileBody;
                            videoBody.setDuration(videoExtra.getDuration());
                            videoBody.setSize(videoExtra.getSize());
                            RawFileDb rawFile = mWrapper.getRawFileDb();
                            if (rawFile != null) {
                                videoBody.setRawFileInfo(mapRaw(rawFile));
                            }

                            message.setMessageBody(videoBody);
                        }
                    }
                    break;
                case ImSdkFileConstant.FILE_IMAGE:
                    IMImageBody imageBody = (IMImageBody) fileBody;

                    HdThumbFileDb hdFile = mWrapper.getHdThumbFileDb();
                    if (hdFile != null) {
                        imageBody.setHdTFileInfo(mapHd(hdFile));
                        message.setMessageBody(imageBody);
                    }

                    RawFileDb rawFile = mWrapper.getRawFileDb();
                    if (rawFile != null) {
                        imageBody.setRawFileInfo(mapRaw(rawFile));
                        message.setMessageBody(imageBody);
                    }
                    break;
                case ImSdkFileConstant.FILE_UNKNOWN:
                    break;
                default:
                    break;
            }

        }
    }

    /**
     * FileMsgDb ==> IMFileBody
     * @param file file
     * @param fileBody fileBody
     * @return IMFileBody
     */
    private IMFileBody mapNormalBody(FileMsgDb file, IMFileBody fileBody) {
        fileBody.setLocalPath(file.getFile_path());
        fileBody.setDisplayName(file.getFile_name());
        fileBody.setSuffix(file.getSuffix());
        fileBody.setTranslateSize(file.getTranslate_size());
        fileBody.setFileSize(file.getFile_size());
        fileBody.setState(mapFileState(file.getFile_state()));
        fileBody.setType(file.getType());
        return fileBody;
    }

    /**
     * IMRawFileInfo ==> RawFileDb
     * @param rawInfo rawInfo
     * @return RawFileDb
     */
    private RawFileDb mapRaw(IMRawFileInfo rawInfo) {
        RawFileDb raw = new RawFileDb();
        raw.setRaw_file_name(rawInfo.getRawDisplayName());
        raw.setRaw_file_path(rawInfo.getRawLocalPath());
        raw.setRaw_file_size(rawInfo.getRawFileSize());
        raw.setRaw_encrypt_size(rawInfo.getRawFileSize());
        raw.setRaw_translate_size(rawInfo.getRawFileTranslateSize());
        raw.setRaw_state(FileTState.UP_NON);
        return raw;
    }

    /**
     * RawFileDb ==> IMRawFileInfo
     * @param raw raw
     * @return IMRawFileInfo
     */
    private IMRawFileInfo mapRaw(RawFileDb raw) {
        IMRawFileInfo rawFile = new IMRawFileInfo();
        rawFile.setRawLocalPath(raw.getRaw_file_path());
        rawFile.setRawDisplayName(raw.getRaw_file_name());
        rawFile.setRawFileSize(raw.getRaw_file_size());
        rawFile.setRawFileTranslateSize(raw.getRaw_translate_size());
        rawFile.setRawState(mapFileState(raw.getRaw_state()));
        return rawFile;
    }

    /**
     * IMHDThumbFileInfo ==> HdThumbFileDb
     * @param hdInfo hdInfo
     * @return HdThumbFileDb
     */
    private HdThumbFileDb mapHd(IMHDThumbFileInfo hdInfo) {
        HdThumbFileDb hd = new HdThumbFileDb();
        hd.setHd_file_name(hdInfo.getHdTDisplayName());
        hd.setHd_file_path(hdInfo.getHdTLocalPath());
        hd.setHd_file_size(hdInfo.getHdTFileSize());
        hd.setHd_encrypt_size(hdInfo.getHdTFileSize());
        hd.setHd_translate_size(hdInfo.getHdTFileTranslateSize());
        hd.setHd_state(FileTState.UP_NON);
        return hd;
    }

    /**
     * HdThumbFileDb ==> IMHDThumbFileInfo
     * @param hd hd
     * @return IMHDThumbFileInfo
     */
    private IMHDThumbFileInfo mapHd(HdThumbFileDb hd) {
        IMHDThumbFileInfo hdFile = new IMHDThumbFileInfo();
        hdFile.setHdTLocalPath(hd.getHd_file_path());
        hdFile.setHdTDisplayName(hd.getHd_file_name());
        hdFile.setHdTFileSize(hd.getHd_file_size());
        hdFile.setHdTFileTranslateSize(hd.getHd_translate_size());
        hdFile.setHdState(mapFileState(hd.getHd_state()));
        return hdFile;
    }

    /**
     * MsgBean ==> MessageWrapper
     * @param msgBean msgBean
     * @return MessageWrapper
     */
    public MessageWrapper mapMWrapper(MsgBean msgBean) {
        MessageWrapper wrapper = new MessageWrapper();

        MsgEntryDb msg = new MsgEntryDb();
        msg.setServer_id(msgBean.getI());
        msg.setCard_id(msgBean.getFi());
        msg.setSession_flag(getSessionFlag(msgBean));
        msg.setAttr(getAttr(msgBean));
        msg.setSort_time(getSortTime(msgBean));
        msg.setState(msgBean.getStat());
        msg.setType(msgBean.getT());
        msg.setReceiver(msgBean.getTo());
        msg.setSender(msgBean.getF());
        msg.setCreate_time(msgBean.getFst());
        msg.setSent_time(msgBean.getSst());
        msg.setLife_time(msgBean.getLc());

        if (msgBean.isTextMsg()) {
            msg.setContent(msgBean.getC());
            wrapper.setMsgEntryDb(msg);
        }

        if (msgBean.isWebMsg()) {
            wrapper = mapWebContent(wrapper, msg, msgBean.getC());
        }

        if (msgBean.isFileMsg()) {
            wrapper = mapFileContent(wrapper, msg, msgBean.getC());
        }

        return wrapper;
    }

    /**
     * MessageWrapper ==> MsgBean
     * 文本消息转换
     * @param wrapper wrapper
     * @return MsgBean
     */
    public MsgBean mapTextBean(MessageWrapper wrapper) {
        MsgEntryDb msg = wrapper.getMsgEntryDb();
        if (msg == null) {
            return null;
        }

        MsgBean msgBean = new MsgBean();
        msgBean.setFi(msg.getCard_id());
        msgBean.setF(msg.getSender());
        msgBean.setTo(msg.getReceiver());
        msgBean.setI(Constant.DEFAULT_SERVER_ID);
        msgBean.setFst(msg.getCreate_time());
        msgBean.setStat(State.DEFAULT);
        msgBean.setT(msg.getType());
        msgBean.setLc(msg.getLife_time());
        msgBean.setC(msg.getContent());
        return msgBean;
    }

    /**
     * MessageWrapper ==> MsgBean
     * 文件文本消息转换
     * @param wrapper wrapper
     * @return MsgBean
     */
    public MsgBean mapFileBean(MessageWrapper wrapper) {
        MsgEntryDb msg = wrapper.getMsgEntryDb();
        if (msg == null) {
            return null;
        }

        MsgBean msgBean = new MsgBean();
        msgBean.setFi(msg.getCard_id());
        msgBean.setF(msg.getSender());
        msgBean.setTo(msg.getReceiver());
        msgBean.setI(Constant.DEFAULT_SERVER_ID);
        msgBean.setFst(msg.getCreate_time());
        msgBean.setStat(State.DEFAULT);
        msgBean.setT(msg.getType());
        msgBean.setLc(msg.getLife_time());
        if (wrapper.isFile()) {
            FileMsgDb file = wrapper.getFileMsgDb();
            if (file.isImage() || file.isVoice()) {
                OldFile oldFile = mapOldFile(wrapper);
                msgBean.setC(JsonUtils.getGson().toJson(oldFile));
            }

            if (file.isNormal()) {
                IMContent content = mapIMContent(wrapper);
                msgBean.setC(JsonUtils.getGson().toJson(content));
            }

            if (file.isVideo()) {
				IMContent content = mapIMContent(wrapper);
                msgBean.setC(JsonUtils.getGson().toJson(content));
            }

        } else if (wrapper.isWeb()) {
            IMContent content = mapIMContent(wrapper);
            msgBean.setC(JsonUtils.getGson().toJson(content));
        }
        return msgBean;
    }

    /**
     * [MessageWrapper， FileType] ==> FileEntry
     * @param wrapper wrapper
     * @param type type
     * @return FileEntry
     */
    public FileEntry getFileEntry(MessageWrapper wrapper, FileType type) {
        FileEntry entry = new FileEntry();
        if (wrapper.getMsgEntryDb() == null || !(wrapper.isFile() || wrapper.isWeb())) {
            return entry;
        }
        entry.setId(ToolUtils.getLong(wrapper.getMsgEntryDb().getId()));
        entry.setType(type);
        entry.setTag(wrapper.getMsgEntryDb().getSession_flag());

        FileMsgDb file = wrapper.getFileMsgDb();
        if (file != null) {
            entry.setMsgType(file.getType());
        }

        switch (type) {
            case IS_SHOW:
                if (file != null) {
                    entry.setSize(file.getFile_size());
                    entry.settSize(file.getTranslate_size());
                    entry.setFid(file.getFid());
                    entry.setPath(file.getFile_path());
                    entry.setEncryptPath(file.getEncrypt_path());
                    entry.setEncryptSize(file.getEncrypt_size());
                    entry.setName(file.getFile_name());
                    entry.setState(file.getFile_state());
                }
                break;
            case IS_HD:
                HdThumbFileDb hd = wrapper.getHdThumbFileDb();
                if (hd != null) {
                    entry.setSize(hd.getHd_file_size());
                    entry.settSize(hd.getHd_translate_size());
                    entry.setFid(hd.getHd_fid());
                    entry.setName(hd.getHd_file_name());
                    entry.setPath(hd.getHd_file_path());
                    entry.setEncryptPath(hd.getHd_encrypt_path());
                    entry.setEncryptSize(hd.getHd_encrypt_size());
                    entry.setState(hd.getHd_state());
                }
                break;
            case IS_RAW:
                RawFileDb raw = wrapper.getRawFileDb();
                if (raw != null) {
                    entry.setSize(raw.getRaw_file_size());
                    entry.settSize(raw.getRaw_translate_size());
                    entry.setFid(raw.getRaw_fid());
                    entry.setName(raw.getRaw_file_name());
                    entry.setPath(raw.getRaw_file_path());
                    entry.setEncryptPath(raw.getRaw_encrypt_path());
                    entry.setEncryptSize(raw.getRaw_encrypt_size());
                    entry.setState(raw.getRaw_state());
                }
                break;
            default:
                break;
        }
        return entry;
    }

    /**
     * FileEntry ==> IMFileInfo
     * @param entry entry
     * @return IMFileInfo
     */
    public IMFileInfo mapFileInfo(FileEntry entry) {
        IMFileInfo info = new IMFileInfo();
        info.setFileType(entry.getType());
        info.setState(mapFileState(entry.getState()));
        info.setTag(entry.getTag());
        info.setPercent(entry.getPercent());

        IMMessage message = new IMMessage();
        message.setIMMessageId(entry.getId());

        if (entry.isNormal()) {
            IMNormalFileBody normal = new IMNormalFileBody(entry.getMsgType());
            normal.setState(mapFileState(entry.getState()));
            normal.setLocalPath(entry.getPath());
            normal.setDisplayName(entry.getName());
            normal.setFileSize(entry.getSize());
            normal.setTranslateSize(entry.gettSize());
            normal.setType(entry.getMsgType());

            message.setMessageBody(normal);
        }

        if(entry.isImage()) {
            IMImageBody image = new IMImageBody(entry.getMsgType());
            image.setType(entry.getMsgType());
            if (entry.isShow()) {
                image.setState(mapFileState(entry.getState()));
                image.setLocalPath(entry.getPath());
                image.setDisplayName(entry.getName());
                image.setFileSize(entry.getSize());
                image.setTranslateSize(entry.gettSize());
            }

            if (entry.isHd()) {
                IMHDThumbFileInfo hd = new IMHDThumbFileInfo();
                hd.setHdState(mapFileState(entry.getState()));
                hd.setHdTFileTranslateSize(entry.gettSize());
                hd.setHdTFileSize(entry.getSize());
                hd.setHdTDisplayName(entry.getName());
                hd.setHdTLocalPath(entry.getPath());
                image.setHdTFileInfo(hd);
            }

            if (entry.isRaw()) {
                IMRawFileInfo raw = new IMRawFileInfo();
                raw.setRawState(mapFileState(entry.getState()));
                raw.setRawFileSize(entry.getSize());
                raw.setRawFileTranslateSize(entry.gettSize());
                raw.setRawDisplayName(entry.getName());
                raw.setRawLocalPath(entry.getPath());
                image.setRawFileInfo(raw);
            }

            message.setMessageBody(image);
        }

        if (entry.isVideo()) {
            IMVideoBody video = new IMVideoBody(entry.getMsgType());
            video.setType(entry.getMsgType());
            if (entry.isShow()) {
                video.setState(mapFileState(entry.getState()));
                video.setLocalPath(entry.getPath());
                video.setDisplayName(entry.getName());
                video.setFileSize(entry.getSize());
                video.setTranslateSize(entry.gettSize());
            }

            if(entry.isRaw()) {
                IMRawFileInfo raw = new IMRawFileInfo();
                raw.setRawState(mapFileState(entry.getState()));
                raw.setRawFileSize(entry.getSize());
                raw.setRawFileTranslateSize(entry.gettSize());
                raw.setRawDisplayName(entry.getName());
                raw.setRawLocalPath(entry.getPath());
                video.setRawFileInfo(raw);
            }

            message.setMessageBody(video);
        }

        if (entry.isVoice()) {
            IMVoiceBody voice = new IMVoiceBody(entry.getMsgType());
            voice.setType(entry.getMsgType());
            if (entry.isShow()) {
                voice.setState(mapFileState(entry.getState()));
                voice.setLocalPath(entry.getPath());
                voice.setDisplayName(entry.getName());
                voice.setFileSize(entry.getSize());
                voice.setTranslateSize(entry.gettSize());
            }

            message.setMessageBody(voice);
        }

        info.setMessage(message);
        return info;
    }

    /**
     * MessageWrapper ==> BodyType
     * @param wrapper wrapper
     * @return BodyType
     */
    public BodyType mapBodyType(MessageWrapper wrapper) {
        if (wrapper.isText()) {
            return BodyType.TEXT;
        }

        BodyType bodyType = BodyType.UNKNOWN;
        FileMsgDb file = wrapper.getFileMsgDb();
        if (wrapper.isFile() && file != null) {
            int type = file.getType();
            return mapBodyType(type);
        }
        return bodyType;
    }

    /**
     * IMMessage ==> BodyType
     * @param message message
     * @return BodyType
     */
    public BodyType mapBodyType(IMMessage message) {
        if (message.isTextIMMessage()) {
            return BodyType.TEXT;
        }
        BodyType bodyType = BodyType.UNKNOWN;

        if (message.isFileIMMessage()) {
            IMFileBody fileBody = (IMFileBody) message.getMessageBody();
            int type = fileBody.getType();
            return mapBodyType(type);
        }
        return bodyType;
    }

    /**
     * type ==> BodyType
     * @param type type
     * @return BodyType
     * @see ImSdkFileConstant
     */
    private BodyType mapBodyType(int type) {
        BodyType bodyType = BodyType.UNKNOWN;
        switch (type) {
            case ImSdkFileConstant.FILE_NORMAL:
                bodyType = BodyType.NORMAL;
                break;
            case ImSdkFileConstant.FILE_VOICE:
                bodyType = BodyType.VOICE;
                break;
            case ImSdkFileConstant.FILE_IMAGE:
                bodyType = BodyType.IMAGE;
                break;
            case ImSdkFileConstant.FILE_VIDEO:
                bodyType = BodyType.VIDEO;
                break;
            case ImSdkFileConstant.FILE_UNKNOWN:
                bodyType = BodyType.UNKNOWN;
                break;
            default:
                break;
        }
        return bodyType;
    }

    /**
     * SessionWrapper ==> SessionEntryDb
     * @param wrappers 会话
     * @return List<SessionEntryDb>
     */
    public List<SessionEntryDb> mapSession(List<SessionWrapper> wrappers) {
        List<SessionEntryDb> dbs = new ArrayList<>();
        for (SessionWrapper wrapper : wrappers) {
            dbs.add(wrapper.getSessionEntryDb());
        }
        return dbs;
    }

    /**
     * Maps String, SessionWrapper ==> List SessionWrapper
     * @param map map
     * @return List
     */
    public List<SessionWrapper> mapSessions(Map<String, SessionWrapper> map) {
        List<SessionWrapper> wrappers = new ArrayList<>();
        Set<Map.Entry<String, SessionWrapper>> entrySet = map.entrySet();

        for (Map.Entry<String, SessionWrapper> entry : entrySet) {
            wrappers.add(entry.getValue());
        }
        return wrappers;
    }

    /**
     * MessageWrappers ==> msg db ids
     * @param wrappers wrappers
     * @return List
     */
    public List<Long> getIds(List<MessageWrapper> wrappers) {
        List<Long> ids = new ArrayList<>();
        for (MessageWrapper wrapper : wrappers) {
            if (wrapper.getMsgEntryDb() != null) {
                ids.add(ToolUtils.getLong(wrapper.getMsgEntryDb().getId()));
            }
        }
        return ids;
    }

    /**
     * MessageWrappers ==> server ids
     * @param wrappers wrappers
     * @return List
     */
    public List<Long> getServerIds(List<MessageWrapper> wrappers) {
        List<Long> ids = new ArrayList<>();
        for (MessageWrapper wrapper : wrappers) {
            if (wrapper.getMsgEntryDb() != null) {
                ids.add(wrapper.getMsgEntryDb().getServer_id());
            }
        }
        return ids;
    }

    /**
     * Map ==> List
     * @param options 配置项键值对
     * @return optionsDb
     */
    public List<OptionsDb> mapOption(Map<String, String> options) {
        List<OptionsDb> dbList = new ArrayList<>();
        if (options != null && !options.isEmpty()) {
            Set<Map.Entry<String, String>> entrySet = options.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                OptionsDb db = new OptionsDb(null, entry.getKey(), entry.getValue());
                dbList.add(db);
            }
        }
        return dbList;
    }

    /**
     * List ==> Map
     * @param list list
     * @return Map
     */
    public Map<String, String> mapOption(List<OptionsDb> list) {
        Map<String, String> map = new HashMap<>();
        for (OptionsDb db : list) {
            map.put(db.getProperty(), db.getValue());
        }
        return map;
    }

    /**
     * IMState ==> LocalStateMsgDb
     * @param state 状态消息
     * @return stateDb
     */
    public LocalStateMsgDb mapState(IMState state) {
        LocalStateMsgDb stateDb = new LocalStateMsgDb();
        stateDb.setContent(state.getContent());
        stateDb.setSendTime(state.getSendTime());
        return stateDb;
    }

    /**
     * LocalStateMsgDb ==> IMState
     * @param list list
     * @return List
     */
    public List<IMState> mapStates(List<LocalStateMsgDb> list) {
        List<IMState> states = new ArrayList<>();
        for (LocalStateMsgDb msg : list) {
            IMState state = new IMState();
            state.setContent(msg.getContent());
            state.setSendTime(msg.getSendTime());
            state.setId(msg.getId());
            states.add(state);
        }
        return states;
    }

    /**
     * IMState ==> LocalStateMsgDb
     * @param list list
     * @return List
     */
    public List<LocalStateMsgDb> mapLStates(List<IMState> list) {
        List<LocalStateMsgDb> states = new ArrayList<>();
        for (IMState entry : list) {
            LocalStateMsgDb state = new LocalStateMsgDb();
            state.setContent(entry.getContent());
            state.setSendTime(entry.getSendTime());
            states.add(state);
        }
        return states;
    }

    /**
     * MsgBean ==> DuplicateIdDb
     * @param list list
     * @return List
     */
    public List<DuplicateIdDb> mapDup(List<MsgBean> list) {
        List<DuplicateIdDb> ids = new ArrayList<>();
        for (MsgBean msg : list) {
            DuplicateIdDb id = new DuplicateIdDb();
            id.setSend_time(String.valueOf(msg.getFst()));
            id.setServer_id(String.valueOf(msg.getI()));

            ids.add(id);
        }
        return ids;
    }

    /**
     * StateBean ==> IMState
     * @param beans beans
     * @return List
     */
    private List<IMState> subStates(List<StateBean> beans) {
        List<IMState> states = new ArrayList<>();
        int size = beans.size();
        int subSize = size % Constant.MAX_STATE == 0 ?
                size/Constant.MAX_STATE : size/Constant.MAX_STATE + 1;
        for (int i=0; i<subSize; i++) {
            List<StateBean> sub = new ArrayList<>();
            for (int j=i*Constant.MAX_STATE; j<Constant.MAX_STATE*(i+1); j++) {
                if (j < size) {
                    sub.add(beans.get(j));
                }
            }

            IMState state = new IMState();
            state.setContent(JsonUtils.getGson().toJson(sub));
            state.setSendTime(ImMsgManager.getInstance().getCurrentM());

            states.add(state);
        }
        return states;
    }

    /**
     * MsgBean C ==> MessageWrapper
     * @param message message
     * @param msg msg
     * @param c c
     * @return MessageWrapper
     */
    private MessageWrapper mapFileContent(MessageWrapper message, MsgEntryDb msg, String c) {
        IMContent content = JsonUtils.mapGson(c, IMContent.class);

        if (content != null && !TextUtils.isEmpty(content.getFid())) {
            return mapIMContent(message, msg, content);
        }

        OldFile oldFile = JsonUtils.mapGson(c, OldFile.class);

        if (oldFile != null) {
            return mapOldFile(message, msg, oldFile);
        }

        return message;
    }

    //add by ycm 2017/4/1 for sharing web message [start]
    private MessageWrapper mapWebContent(MessageWrapper message, MsgEntryDb msg, String c) {
        IMContent content = JsonUtils.mapGson(c, IMContent.class);
        if (content != null && !TextUtils.isEmpty(content.getFid())) {
            FileMsgDb file = new FileMsgDb();
            file.setFid(content.getFid());
            file.setFile_size(content.getSize());
            file.setEncrypt_size(content.getEncryptSize());
            file.setType(content.getType());
            file.setSuffix(content.getSuffix());
            file.setFile_state(FileTState.DOWN_NON);
            String name = content.getName();
            if (TextUtils.isEmpty(name)) {
                name = RandomStringUtils.randomAlphanumeric(2);
            }
            String encryptPath = getEncryptPath(ImSdkFileConstant.FILE_WEB, name);
            file.setFile_name(name);

            file.setFile_path(FileUtils.subDat(encryptPath));
            file.setEncrypt_path(encryptPath);
            message.setFileMsgDb(file);
            msg.setContent(content.getExtraInfo());
        } else {
            msg.setContent(c);
        }
        message.setMsgEntryDb(msg);
        return message;
    }
    //add by ycm 2017/4/1 for sharing web message [end]

    /**
     * 兼容老版本，只需要处理语音，图片
     * @param message message
     * @param msg msg
     * @param oldFile oldFile
     * @return MessageWrapper
     */
    public MessageWrapper mapOldFile(MessageWrapper message, MsgEntryDb msg, OldFile oldFile) {
        FileMsgDb file = new FileMsgDb();

        file.setFid(oldFile.getFileUrl());
        file.setFile_size(oldFile.getSize());
        file.setEncrypt_size(oldFile.getSize());
        file.setType(oldFile.getType());
        file.setSuffix(oldFile.getSuffix());
        file.setFile_state(FileTState.DOWN_NON);

        msg.setContent(String.valueOf(oldFile.getType()));

        message.setMsgEntryDb(msg);

        if (oldFile.isImage()) {

            String name = FileUtils.subDat(oldFile.getName());
            String encryptFilePath = getEncryptPath(ImSdkFileConstant.FILE_IMAGE, name);
            String filePath = FileUtils.subDat(encryptFilePath);

            file.setFile_name(name);
            file.setFile_path(filePath);
            file.setEncrypt_path(encryptFilePath);

            String extraInfo = oldFile.getExtraInfo();
            message.setHdThumbFileDb(mapHdExtraInfo(extraInfo, msg));
            message.setRawFileDb(mapRawExtraInfo(extraInfo, msg));

        } else if (oldFile.isVoice()) {
            String name = FileUtils.subDat(oldFile.getName());
            String encryptVoicePath = getEncryptPath(ImSdkFileConstant.FILE_VOICE, name);
            String filePath = FileUtils.subDat(encryptVoicePath);

            file.setFile_name(name);
            file.setFile_path(filePath);
            file.setEncrypt_path(encryptVoicePath);
            file.setExtra_info(oldFile.getExtraInfo());
        } else {
            file.setExtra_info(oldFile.getExtraInfo());
        }

        message.setFileMsgDb(file);

        return message;
    }

    /**
     * 解析高清图信息
     * @param oldExtra oldExtra
     * @param msg msg
     * @return HdThumbFileDb
     */
    public HdThumbFileDb mapHdExtraInfo(String oldExtra, MsgEntryDb msg) {
        OldExtra image = JsonUtils.mapGson(oldExtra, OldExtra.class);
        if (image != null && !TextUtils.isEmpty(image.getThumbFid())) {
            HdThumbFileDb hd = new HdThumbFileDb();
            hd.setHd_fid(image.getThumbFid());
            hd.setHd_file_size(image.getThumbFileSize());
            hd.setHd_encrypt_size(image.getThumbFileSize());
            hd.setHd_translate_size(image.getThumbFileTranslateSize());
            hd.setHd_state(FileTState.DOWN_NON);

            String thuName = FileUtils.subDat(image.getThumbFileName());
            String encryptThuPath = getEncryptPath(ImSdkFileConstant.FILE_IMAGE, thuName);
            String thuPath = FileUtils.subDat(encryptThuPath);

            hd.setHd_file_name(thuName);
            hd.setHd_file_path(thuPath);
            hd.setHd_encrypt_path(encryptThuPath);

            return hd;
        }
        return null;
    }

    /**
     * 解析原图信息
     * @param oldExtra oldExtra
     * @param msg msg
     * @return RawFileDb
     */
    public RawFileDb mapRawExtraInfo(String oldExtra, MsgEntryDb msg) {
        OldExtra image = JsonUtils.mapGson(oldExtra, OldExtra.class);
        if (image != null && !TextUtils.isEmpty(image.getRawFid())) {
            RawFileDb raw = new RawFileDb();
            raw.setRaw_fid(image.getRawFid());
            raw.setRaw_file_size(image.getRawFileSize());
            raw.setRaw_encrypt_size(image.getRawFileSize());
            raw.setRaw_translate_size(image.getRawFileTranslateSize());
            raw.setRaw_state(FileTState.DOWN_NON);

            String rawName = FileUtils.subDat(image.getRawFileName());
            String encryptRawPath = getEncryptPath(ImSdkFileConstant.FILE_IMAGE, rawName);
            String rawPath = FileUtils.subDat(encryptRawPath);

            raw.setRaw_file_name(rawName);
            raw.setRaw_file_path(rawPath);
            raw.setRaw_encrypt_path(encryptRawPath);

            return raw;
        }
        return null;
    }

    /**
     * MsgBean content ==> MessageWrapper
     * @param message message
     * @param msg msg
     * @param content content
     * @return MessageWrapper
     */
    private MessageWrapper mapIMContent(MessageWrapper message, MsgEntryDb msg, IMContent content) {
        FileMsgDb file = new FileMsgDb();
        file.setFid(content.getFid());
        file.setFile_size(content.getSize());
        file.setEncrypt_size(content.getEncryptSize());
        file.setType(content.getType());
        file.setSuffix(content.getSuffix());
        file.setFile_state(FileTState.DOWN_NON);
        file.setExtra_info(content.getExtraInfo());

        msg.setContent(String.valueOf(content.getType()));

        message.setMsgEntryDb(msg);


        if (content.isNormal()) {
            String name = content.getName();
            if (TextUtils.isEmpty(name)) {
                name = RandomStringUtils.randomAlphanumeric(2);
            }

            String encryptPath = getEncryptPath(ImSdkFileConstant.FILE_NORMAL, name);

            file.setFile_name(name);
            file.setFile_path(getDecryptPath(name));
            file.setEncrypt_path(encryptPath);

            IMRaw raw = JsonUtils.mapGson(content.getRaw(), IMRaw.class);

            if (raw != null && !TextUtils.isEmpty(raw.getFid())) {
                RawFileDb rawFile = new RawFileDb();
                rawFile.setRaw_fid(raw.getFid());
                rawFile.setRaw_file_size(raw.getSize());
                rawFile.setRaw_encrypt_size(raw.getEncryptSize());
                rawFile.setRaw_state(FileTState.DOWN_NON);

                String rawName = raw.getName();
                if (TextUtils.isEmpty(rawName)) {
                    rawName = RandomStringUtils.randomAlphanumeric(2);
                }
                String encryptRawPath = getEncryptPath(ImSdkFileConstant.FILE_NORMAL, rawName);
                rawFile.setRaw_file_name(rawName);
                rawFile.setRaw_encrypt_path(encryptRawPath);
                rawFile.setRaw_file_path(getDecryptPath(name));

                message.setRawFileDb(rawFile);
            }

            message.setFileMsgDb(file);
        }

        // 暂时不用
//        if (content.isImage()) {
//
//            IMHd hd = JsonUtils.mapGson(content.getHd(), IMHd.class);
//            if (hd != null) {
//                HdThumbFileDb hdFile = new HdThumbFileDb();
//                hdFile.setHd_encrypt_size(hd.getEncryptSize());
//                hdFile.setHd_file_size(hd.getSize());
//                hdFile.setHd_file_name(hd.getName());
//                hdFile.setHd_file_path(path);// TODO: 2016/12/16
//                hdFile.setHd_fid(hd.getFid());
//                hdFile.setHd_state(Constant.DOWN_NON);
//
//                message.setHdThumbFileDb(hdFile);
//            }
//            message.setFileMsgDb(file);
//        }

        // 暂时不用
//        if (content.isImage() || content.isVideo()) {
//            IMRaw raw = JsonUtils.mapGson(content.getRaw(), IMRaw.class);
//
//            if (raw != null) {
//                RawFileDb rawFile = new RawFileDb();
//                rawFile.setRaw_fid(raw.getFid());
//                rawFile.setRaw_file_name(raw.getName());
//                rawFile.setRaw_file_path(path);
//                rawFile.setRaw_file_size(raw.getSize());
//                rawFile.setRaw_encrypt_size(raw.getEncryptSize());
//                rawFile.setRaw_state(Constant.DOWN_NON);
//
//                message.setRawFileDb(rawFile);
//            }
//
//            message.setFileMsgDb(file);
//        }

        if (content.isVideo()) {
            String name = content.getName();
            if (TextUtils.isEmpty(name)) {
                name = RandomStringUtils.randomAlphanumeric(2);
            }

            String encryptPath = getEncryptPath(ImSdkFileConstant.FILE_VIDEO, name);

            file.setFile_name(name);
            file.setFile_path(FileUtils.subDat(encryptPath));
            file.setEncrypt_path(encryptPath);

            IMRaw raw = JsonUtils.mapGson(content.getRaw(), IMRaw.class);

            if (raw != null && !TextUtils.isEmpty(raw.getFid())) {
                RawFileDb rawFile = new RawFileDb();
                rawFile.setRaw_fid(raw.getFid());
                rawFile.setRaw_file_size(raw.getSize());
                rawFile.setRaw_encrypt_size(raw.getEncryptSize());
                rawFile.setRaw_state(FileTState.DOWN_NON);

                String rawName = raw.getName();
                if (TextUtils.isEmpty(rawName)) {
                    rawName = RandomStringUtils.randomAlphanumeric(2);
                }
                String encryptRawPath = getEncryptPath(ImSdkFileConstant.FILE_VIDEO, rawName);
                rawFile.setRaw_file_name(rawName);
                rawFile.setRaw_encrypt_path(encryptRawPath);
                rawFile.setRaw_file_path(FileUtils.subDat(encryptRawPath));

                message.setRawFileDb(rawFile);
            }

            message.setFileMsgDb(file);
        }
        return message;
    }

    /**
     * 获取文件路径
     * @param type 文件类型
     * @param name 文件名
     * @return path
     */
    private String getEncryptPath(int type, String name) {
        return FileUtils.getCachePath(type, name);
    }

    /**
     * 获取接收文件解密后的路径
     * @param name 文件名称
     * @return path
     */
    private String getDecryptPath(String name) {
        return FileUtils.getFileRecPath(name);
    }


    /**
     * MessageWrapper ==> OldFile ==> MsgBean
     * @param wrapper wrapper
     * @return OldFile
     */
    private OldFile mapOldFile(MessageWrapper wrapper) {
        OldFile oldFile = new OldFile();
        FileMsgDb file = wrapper.getFileMsgDb();

        oldFile.setName(FileUtils.addDat(file.getFile_name()));
        oldFile.setFilePath(FileUtils.addDat(file.getFile_path()));
        oldFile.setFileUrl(file.getFid());
        oldFile.setSize(file.getFile_size());
        oldFile.setSuffix(file.getSuffix());
        oldFile.setType(file.getType());

        if (file.isVoice()) {
            oldFile.setExtraInfo(file.getExtra_info());
        }

        if (file.isImage()) {
            OldExtra oldExt = new OldExtra();

            HdThumbFileDb hd = wrapper.getHdThumbFileDb();
            if (hd != null) {
                oldExt.setThumbFid(hd.getHd_fid());
                oldExt.setThumbFileName(FileUtils.addDat(hd.getHd_file_name()));
                oldExt.setThumbFileUrl(FileUtils.addDat(hd.getHd_file_path()));
                oldExt.setThumbFileSize(hd.getHd_file_size());
                oldExt.setThumbFileTranslateSize(hd.getHd_translate_size());
            }

            RawFileDb raw = wrapper.getRawFileDb();
            if (raw != null) {
                oldExt.setRawFid(raw.getRaw_fid());
                oldExt.setRawFileName(FileUtils.addDat(raw.getRaw_file_name()));
                oldExt.setRawFileUrl(FileUtils.addDat(raw.getRaw_file_path()));
                oldExt.setRawFileSize(raw.getRaw_file_size());
                oldExt.setRawFileTranslateSize(raw.getRaw_translate_size());
            }

            MsgEntryDb msg = wrapper.getMsgEntryDb();
            if (msg != null) {
                oldExt.setMsgId(ToolUtils.getLong(msg.getId()));
                oldExt.setBoom(msg.boomed());
            }

            oldFile.setExtraInfo(JsonUtils.getGson().toJson(oldExt));
        }
        return oldFile;
    }

    /**
     * MessageWrapper ==> IMContent ==> MsgBean
     * @param wrapper wrapper
     * @return IMContent
     */
    private IMContent mapIMContent(MessageWrapper wrapper) {
        IMContent content = new IMContent();

        FileMsgDb file = wrapper.getFileMsgDb();
        
        content.setFid(file.getFid());
        content.setType(file.getType());
        content.setName(file.getFile_name());
        content.setSize(file.getFile_size());
        content.setEncryptSize(file.getEncrypt_size());
        content.setSuffix(file.getSuffix());
        if (wrapper.isWeb()) {
            MsgEntryDb msgEntryDb = wrapper.getMsgEntryDb();
            content.setExtraInfo(msgEntryDb.getContent());
        }

        if (file.isNormal()) {
            // TODO: 2016/12/21 liming
        }
		
		if (file.isVideo()) {

            content.setExtraInfo(file.getExtra_info());
            RawFileDb raw = wrapper.getRawFileDb();
            IMRaw imRaw = new IMRaw();
            imRaw.setEncryptSize(raw.getRaw_encrypt_size());
            imRaw.setFid(raw.getRaw_fid());
            imRaw.setName(raw.getRaw_file_name());
            imRaw.setSize(raw.getRaw_file_size());

            content.setRaw(JsonUtils.getGson().toJson(imRaw));
        }

        return content;
    }

    /**
     * 获取聊天对象
     * @param msgBean msgBean
     * @return String
     */
    private String getImPartner(MsgBean msgBean) {
        //im partner
        if (account.equals(msgBean.getF())) {
            return msgBean.getTo();
        } else {
            if (msgBean.isGroupMsg()) {
                return msgBean.getTo();
            } else {
                return msgBean.getF();
            }
        }
    }

    /**
     * 获取session tag
     * @param msgBean msgBean
     * @return String
     */
    private String getSessionFlag(MsgBean msgBean) {
        String im = getImPartner(msgBean);

        if (msgBean.isGroupMsg()) {
            return ToolUtils.getSessionTag(im, IMSessionType.SESSION_GROUP);
        } else {
            return ToolUtils.getSessionTag(im, IMSessionType.SESSION_SINGLE);
        }
    }

    /**
     * 获取接收到的消息的attr
     * @param msgBean msgBean
     * @return int
     */
    private int getAttr(MsgBean msgBean) {
        if (account.equals(msgBean.getF())) {
            return Constant.MSG_SENT_OLD;
        } else {
            if (msgBean.getStat() == State.SENT) {
                return Constant.MSG_REC_NEW;
            } else {
                return Constant.MSG_REC_OLD;
            }
        }
    }

    /**
     * 获取排序显示时间
     * @param msgBean msgBean
     * @return long
     */
    private long getSortTime(MsgBean msgBean) {
        if (account.equals(msgBean.getF())) {
            return msgBean.getFst();
        } else {
            return msgBean.getSst();
        }
    }

    /**
     * 获取聊天对象
     * @param message message
     * @return String
     */
    private String getImPartner(IMMessage message) {
        //im partner
        if (account.equals(message.getFrom())) {
            return message.getTo();
        } else {
            if (message.isGroupIMMessage()) {
                return message.getTo();
            } else {
                return message.getFrom();
            }
        }
    }

    /**
     * 文本解密
     * @param msg msg
     * @return String
     */
    private String decryptText(MsgEntryDb msg) {
        if (!ImSdkConfigManager.getInstance().needEncrypt()) {
            return msg.getContent();
        }
        String content = msg.getContent();
        SecurityPara para = getSecurityPara(msg);
        SecurityResult result = ImSdkCallbackManager.getInstance().callDecryptText(content, para);
        if (result == null) {
            return msg.getContent();
        }
        return result.getResult();
    }

    /**
     * state ==> FileState
     * @param state state
     * @return FileState
     * @see Constant
     */
    private FileState mapFileState(int state) {
        FileState file = FileState.FAIL;
        switch (state) {
            case FileTState.DOWN_NON:
            case FileTState.UP_NON:
                file = FileState.INACTIVE;
                break;
            case FileTState.DOWN_LOADING:
            case FileTState.DOWN_DONE:
            case FileTState.UP_LOADING:
            case FileTState.ENCRYPT_SUCCESS:
            case FileTState.UP_FID:
                file = FileState.LOADING;
                break;
            case FileTState.DECRYPT_SUCCESS:
            case FileTState.UP_DONE:
                file = FileState.DONE;
                break;
            case FileTState.DOWN_PAUSE:
            case FileTState.UP_PAUSE:
                file = FileState.PAUSE;
                break;
            case FileTState.ENCRYPT_FAIL:
            case FileTState.UP_FID_FAIL:
            case FileTState.UP_FAIL:
            case FileTState.DOWN_FAIL:
            case FileTState.DECRYPT_FAIL:
                file = FileState.FAIL;
                break;
            default:
                break;
        }
        return file;
    }

    /**
     * ImSdk State ==> Data State
     * @param state state
     * @return int
     */
    private int mapState(int state) {
        if (state > State.DEFAULT) {
            return state;
        }

        if (state == State.DEFAULT ||
                state == State.SENT_NON) {
            return MsgState.MSG_STATE_DEFAULT;
        } else {
            return MsgState.MSG_STATE_FAIL;
        }
    }

    /**
     * State ==> IMFailCode
     * @param state state
     * @return int
     * @see State
     * @see IMFailCode
     */
    private int mapFailState(int state) {
        int fail = IMFailCode.FAIL_DEFAULT;
        switch (state) {
            case State.CHECK_FAIL:
                fail = IMFailCode.CHECK_FAIL;
                break;
            case State.ENCRYPT_FAIL:
                fail = IMFailCode.ENCRYPT_FAIL;
                break;
            case State.SENT_NON:
            case State.SENT_FAIL:
                fail = IMFailCode.SENT_FAIL;
                break;
            case State.NON_FRIENDS:
                fail = IMFailCode.NON_FRIENDS;
                break;
            case State.DOWN_FAIL:
            case State.UP_FAIL:
                fail = IMFailCode.FILE_FAIL;
                break;
            case State.UN_SUPPORT:
                fail = IMFailCode.UN_SUPPORT;
                break;
            default:
                break;
        }
        return fail;
    }
}
