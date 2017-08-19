package com.xdja.imp.data.entity.mapper;

import android.text.TextUtils;

import com.xdja.comm.server.ActomaController;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.data.cache.UserCache;
import com.xdja.imp.data.entity.SessionParam;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.FileExtraInfo;
import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.model.ImageFileInfo;
import com.xdja.imp.domain.model.SessionConfig;
import com.xdja.imp.domain.model.TalkListBean;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.domain.model.VideoFileInfo;
import com.xdja.imp.domain.model.WebPageInfo;
import com.xdja.imp.domain.model.VoiceFileInfo;
import com.xdja.imp_data.R;
import com.xdja.imsdk.constant.ImSdkFileConstant;
import com.xdja.imsdk.model.IMFileInfo;
import com.xdja.imsdk.model.IMMessage;
import com.xdja.imsdk.model.IMSession;
import com.xdja.imsdk.model.body.IMFileBody;
import com.xdja.imsdk.model.body.IMImageBody;
import com.xdja.imsdk.model.body.IMMessageBody;
import com.xdja.imsdk.model.body.IMTextBody;
import com.xdja.imsdk.model.body.IMVideoBody;
import com.xdja.imsdk.model.body.IMWebBody;
import com.xdja.imsdk.model.body.IMVoiceBody;
import com.xdja.imsdk.model.file.IMHDThumbFileInfo;
import com.xdja.imsdk.model.file.IMRawFileInfo;

import javax.inject.Inject;

/**
 * <p>Summary:数据转换器</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.entity.mapper</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/18</p>
 * <p>Time:15:40</p>
 */
public class DataMapper {

    private UserCache userCache;

    @Inject
    public DataMapper(UserCache userCache) {
        this.userCache = userCache;
    }

    /**
     * 将会话实体转化为会话业务对象
     * IMSession ==> TalkListBean
     * @param session 会话实体
     * @return 会话相关业务对象
     */
    public TalkListBean mapTalkBean(IMSession session) {
        if (session == null) {
            return null;
        }

        TalkListBean talkListBean = new TalkListBean();

        talkListBean.setTalkerAccount(session.getImPartner());
        talkListBean.setLastTime(session.getDisplayTime());
        talkListBean.setNotReadCount(session.getRemindCount());
        talkListBean.setTalkFlag(session.getSessionTag());
        talkListBean.setTalkType(ValueConverter.sessionTypeConvert(session.getSessionType()));

        IMMessage message = session.getLastMessage();
        if (message != null) {
            TalkMessageBean msg = mapMessage(message);

            talkListBean.setLastMsg(msg);
            talkListBean.setLastMsgAccount(msg.getFrom());
            talkListBean.setLastMsgType(msg.getMessageType());
            talkListBean.setContent(msg.getContent());
        }
        return talkListBean;
    }

    /**
     * 将会话业务对象转化为会话实体
     * TalkListBean ==> IMSession
     * @param talkListBean 会话业务对象
     * @return 会话实体
     */
    public IMSession mapSession(TalkListBean talkListBean) {
        if (talkListBean == null) {
            return null;
        }

        IMSession imSession = new IMSession();
        imSession.setSessionTag(talkListBean.getTalkFlag());
        imSession.setImPartner(talkListBean.getTalkerAccount());
        imSession.setDisplayTime(talkListBean.getDisplayTime());
        imSession.setSessionType(ValueConverter.talkListTypeConvert(talkListBean.getTalkType()));
        imSession.setRemindCount(talkListBean.getNotReadCount());

        TalkMessageBean lastMsg = talkListBean.getLastMsg();
        if (lastMsg != null) {
            IMMessage message = mapMessage(lastMsg);

            imSession.setLastMessage(message);
        }

        return imSession;
    }


    /**
     * 消息实体对象转换为消息业务对象
     * IMMessage ==> TalkMessageBean
     * @param imMessage 消息实体对象
     * @return 消息业务对象
     */
    public TalkMessageBean mapMessage(IMMessage imMessage) {
        if (imMessage == null) {
            return null;
        }

        TalkMessageBean talkMessageBean = new TalkMessageBean();
        talkMessageBean.set_id(imMessage.getIMMessageId());
        talkMessageBean.setFrom(imMessage.getFrom());
        talkMessageBean.setSenderCardId(imMessage.getCardId());
        talkMessageBean.setTo(imMessage.getTo());
        talkMessageBean.setMessageType(ValueConverter.imMsgTypeConvert(imMessage));
        talkMessageBean.setIsBomb(imMessage.isBombIMMessage());
        talkMessageBean.setGroupMsg(imMessage.isGroupIMMessage());

        talkMessageBean.setMessageState(ValueConverter.imMsgStateConvert(imMessage.getState()));
        talkMessageBean.setLimitTime(imMessage.getTimeToLive());
        talkMessageBean.setMine(userCache.isMine(imMessage.getFrom()));
        talkMessageBean.setShowTime(imMessage.getIMMessageTime());
        talkMessageBean.setFailCode(imMessage.getFailCode());

        if (imMessage.isTextIMMessage()) {
            IMTextBody textBody = (IMTextBody) imMessage.getMessageBody();
            talkMessageBean.setContent(textBody.getContent());
            return talkMessageBean;
        }

        //add by ycm 2017/4/1 for web message[start]
        if (imMessage.isWebIMMessage()) {
            talkMessageBean.setFileInfo(mapWebFileInfo(imMessage));
        }
        //add by ycm 2017/4/1 for web message[end]

        //add by juyingang 20161226[start]
        //对不支持的消息类型转化成文字提醒消息
        if (!ConstDef.isSupportMsgType(talkMessageBean.getMessageType())){
            talkMessageBean.setContent(ActomaController.getApp().getString(R.string.unsupport_message_type));
            //消息类型
            talkMessageBean.setMessageType(ConstDef.MSG_TYPE_TEXT);
        }
        //add by juyingang 20161226[end]

        if(imMessage.isFileIMMessage()){
            talkMessageBean.setFileInfo(mapFileInfo(imMessage));
        }
        return talkMessageBean;
    }

    /**
     * 消息业务对象转换为消息实体对象
     * TalkMessageBean ==> IMMessage
     * @param talkMessageBean 消息业务对象
     * @return 消息实体对象
     */
    public IMMessage mapMessage(TalkMessageBean talkMessageBean) {
        if (talkMessageBean == null) {
            return null;
        }

        IMMessage imMessage = new IMMessage();
        imMessage.setIMMessageId(talkMessageBean.get_id());
        imMessage.setFrom(talkMessageBean.getFrom());
        imMessage.setCardId(talkMessageBean.getSenderCardId());
        imMessage.setTo(talkMessageBean.getTo());
        imMessage.setFailCode(talkMessageBean.getFailCode());
        imMessage.setType(ValueConverter.talkMsgTypeConvert(talkMessageBean));
        imMessage.setIMMessageTime(talkMessageBean.getSortTime());
        imMessage.setTimeToLive(talkMessageBean.getLimitTime());
        imMessage.setMessageBody(mapBody(talkMessageBean, imMessage.isFileIMMessage()));

        imMessage.setState(ValueConverter.talkMsgStateConvert(talkMessageBean.getMessageState()));
        return imMessage;
    }

    /**
     * 会话设置转换
     * SessionParam ==> SessionConfig
     * @param sessionParam sessionParam
     * @return SessionConfig
     */
    public SessionConfig sessionParamMap2SessionConfig(SessionParam sessionParam) {
        if (sessionParam == null) {
            return null;
        }

        SessionConfig config = new SessionConfig();
        config.setFlag(sessionParam.getFlag());
        config.setDraft(sessionParam.getDraft());
        config.setNoDisturb(sessionParam.getIsNoDisturb() == SessionParam.ISDISTURB_TRUE);
        config.setTop(sessionParam.getIsTop() == SessionParam.ISTOP_TRUE);
        config.setDraftTime(sessionParam.getDraftTime());
        config.setId(sessionParam.getId());
        return config;
    }


    /**
     * SessionConfig ==> SessionParam
     * @param sessionParam sessionParam
     * @return sessionParam
     */
    public SessionParam sessionConfigMap2SessionParam(SessionConfig sessionParam) {
        if (sessionParam == null) {
            return null;
        }

        SessionParam config = new SessionParam();
        config.setFlag(sessionParam.getFlag());
        if (!TextUtils.isEmpty(sessionParam.getDraft())) {
            config.setDraft(sessionParam.getDraft());
        }
        config.setIsNoDisturb(sessionParam.isNoDisturb()
                ? SessionParam.ISDISTURB_TRUE : SessionParam.ISDISTURB_FALE);
        config.setIsTop(sessionParam.isTop() ? SessionParam.ISTOP_TRUE : SessionParam.ISTOP_FALE);
        if (sessionParam.getDraftTime() != 0) {
            config.setDraftTime(sessionParam.getDraftTime());
        }
        if (sessionParam.getId() != 0) {
            config.setId(sessionParam.getId());
        }
        return config;
    }

    /**
     * FileInfo ==> IMFileInfo
     * @param info info
     * @return IMFileInfo
     */
    public IMFileInfo mapIMFileInfo(FileInfo info) {
        IMFileInfo imFileInfo = new IMFileInfo();

        IMMessage message = new IMMessage();
        message.setIMMessageId(info.getTalkMessageId());

        if(info instanceof ImageFileInfo){
            ImageFileInfo image = (ImageFileInfo) info;

            if (image.getType() == ConstDef.FILE_IS_THUMB_HD) {
                imFileInfo.setFileType(ImSdkFileConstant.FileType.IS_HD);
            } else if (image.getType() == ConstDef.FILE_IS_RAW) {
                imFileInfo.setFileType(ImSdkFileConstant.FileType.IS_RAW);
            } else {
                imFileInfo.setFileType(ImSdkFileConstant.FileType.IS_SHOW);
            }
        }else if (info instanceof VideoFileInfo) {
		
            VideoFileInfo video = (VideoFileInfo) info;
            if (video.getType() == ConstDef.FILE_IS_RAW) {
                imFileInfo.setFileType(ImSdkFileConstant.FileType.IS_RAW);
            } else {
                imFileInfo.setFileType(ImSdkFileConstant.FileType.IS_SHOW);
            }
        } else {
            imFileInfo.setFileType(ImSdkFileConstant.FileType.IS_SHOW);
        }

        imFileInfo.setMessage(message);
        return imFileInfo;
    }

    // add by ycm for sharing web message [start]
    /**
     * FileInfo ==> IMFileInfo
     * @param imFile info
     * @return IMFileInfo
     */
    public FileInfo mapWebFileInfo(IMFileInfo imFile) {
        if (imFile == null) {
            return null;
        }
        IMMessage message = imFile.getMessage();
        FileInfo fileInfo = null;
        if (message.getMessageBody() instanceof IMWebBody) {
            IMWebBody imWebBody = (IMWebBody) message.getMessageBody();
            if (imWebBody == null) {
                return null;
            }
            fileInfo = new FileInfo();
            fileInfo.setFileType(ConstDef.TYPE_NORMAL);
            fileInfo.setTalkMessageId(message.getIMMessageId());
            fileInfo.setTalkListTag(imFile.getTag());
            fileInfo.setTranslateSize(imWebBody.getTranslateSize());
            fileInfo.setFilePath(imWebBody.getLocalPath());
            fileInfo.setFileName(imWebBody.getDisplayName());
            fileInfo.setFileState(mapFileState(imWebBody.getState()));
            fileInfo.setFileSize(imWebBody.getFileSize());
            fileInfo.setPercent(getPercent(imWebBody.getFileSize(), imWebBody.getTranslateSize()));
        }
        return fileInfo;
    }
    // add by ycm for sharing web message [end]

    /**
     * 文件操作回调参数转换得到文件信息业务对象
     * IMFileInfo ==> FileInfo
     * @param imFile 文件操作回调参数
     * @return 文件信息业务对象
     */
    public FileInfo mapFileInfo(IMFileInfo imFile) {
        if (imFile == null) {
            return null;
        }

        IMMessage message = imFile.getMessage();

        FileInfo fileInfo;
        // modified by ycm for sharing web message [start]
        if (!(message.getMessageBody() instanceof IMFileBody)) {
            return null;
        }
        // modified by ycm for sharing web message [end]

        IMFileBody fileBody = (IMFileBody) message.getMessageBody();
        if (fileBody == null) {
            return null;
        }

        if(fileBody.isVoice()){
            fileInfo = new VoiceFileInfo();
            fileInfo.setFileType(ConstDef.TYPE_VOICE);
        } else if (fileBody.isImage()){
            ImageFileInfo imageFileInfo = new ImageFileInfo();
            if (imFile.getFileType() == ImSdkFileConstant.FileType.IS_RAW) {
                imageFileInfo.setType(ConstDef.FILE_IS_RAW);
            } else if (imFile.getFileType() == ImSdkFileConstant.FileType.IS_HD) {
                imageFileInfo.setType(ConstDef.FILE_IS_THUMB_HD);
            } else {
                imageFileInfo.setType(ConstDef.FILE_IS_THUMB);
            }
            imageFileInfo.setFileType(ConstDef.TYPE_PHOTO);
            fileInfo = imageFileInfo;
        } else if (fileBody.isVideo()) {
			//jyg add 2017/3/15 start fix bug 9261
            VideoFileInfo videoFileInfo = new VideoFileInfo();
            if (imFile.getFileType() == ImSdkFileConstant.FileType.IS_RAW) {
                videoFileInfo.setType(ConstDef.FILE_IS_RAW);
            } else {
                videoFileInfo.setType(ConstDef.FILE_IS_THUMB);
            }
            videoFileInfo.setFileType(ConstDef.TYPE_VIDEO);
            fileInfo = videoFileInfo;
			//jyg add 2017/3/15 end
        } else {
            fileInfo = new FileInfo();
            fileInfo.setFileType(ConstDef.TYPE_NORMAL);
        }
        fileInfo.setTalkMessageId(message.getIMMessageId());
        fileInfo.setTalkListTag(imFile.getTag());
        fileInfo.setTranslateSize(fileBody.getTranslateSize());
        fileInfo.setFilePath(fileBody.getLocalPath());
        fileInfo.setFileName(fileBody.getDisplayName());
        fileInfo.setFileState(mapFileState(fileBody.getState()));
        //fix bug 7663 by zya 20170102
        fileInfo.setFileSize(fileBody.getFileSize());
        fileInfo.setPercent(getPercent(fileBody.getFileSize(),fileBody.getTranslateSize()));
        //end by zya
        return fileInfo;
    }

    /**
     * 消息业务对象转换为ImSdk消息内容对象
     * @param talkMessageBean  消息业务对象
     * @param isFile 是否是文件类型
     * @return ImSdk消息内容对象
     */
    private IMMessageBody mapBody(TalkMessageBean talkMessageBean, boolean isFile) {
        if (!isFile) {
            return new IMTextBody(talkMessageBean.getContent());
        }
        if (talkMessageBean.getFileInfo() == null) {
            LogUtil.getUtils().w("fileInfo is null!!");
            return new IMFileBody(ImSdkFileConstant.FILE_UNKNOWN);
        }
        return mapFileBody(talkMessageBean.getFileInfo());
    }

    // add by ycm for sharing web message [start]
    /**
     * 网页消息业务对象转换为ImSdk消息内容对象
     * @param info 消息文件业务对象
     * @return ImSdk消息内容对象
     */
    public IMWebBody mapWebBody(WebPageInfo info) {
        IMWebBody imWebBody = new IMWebBody(
                info.getTitle(),
                info.getDescription(),
                info.getWebUri(),
                info.getSource());
        imWebBody.setLocalPath(info.getFilePath());
        imWebBody.setFileSize(info.getFileSize());
        imWebBody.setTranslateSize(info.getTranslateSize());
        imWebBody.setDisplayName(info.getFileName());
        imWebBody.setSuffix(info.getSuffix());
        return imWebBody;
    }
    // add by ycm for sharing web message [end]

    /**
     * 消息文件业务对象转换为ImSdk消息内容对象
     * @param info 消息文件业务对象
     * @return ImSdk消息内容对象
     */
    public IMFileBody mapFileBody(FileInfo info) {
        IMFileBody fileBody;
        if(info instanceof VoiceFileInfo) {
            VoiceFileInfo voiceFileInfo = ((VoiceFileInfo) info);
            fileBody = new IMVoiceBody(ImSdkFileConstant.FILE_VOICE, voiceFileInfo.getAmountOfTime());
        } else if (info instanceof ImageFileInfo){
            ImageFileInfo imageFileInfo = (ImageFileInfo) info;

            IMImageBody imageBody = new IMImageBody(ImSdkFileConstant.FILE_IMAGE);

            FileExtraInfo extra = imageFileInfo.getExtraInfo();
            if (extra != null) {
                imageBody.setHdTFileInfo(mapHd(extra));

                if (imageFileInfo.isOriginal()) {
                    imageBody.setRawFileInfo(mapRaw(extra));
                }
            }

            fileBody = imageBody;
        } else if (info instanceof VideoFileInfo){
		
            VideoFileInfo videoFileInfo = (VideoFileInfo) info;
            IMVideoBody videoBody = new IMVideoBody(ImSdkFileConstant.FILE_VIDEO, videoFileInfo.getAmountOfTime(),
                    videoFileInfo.getVideoSize());
            FileExtraInfo extra = videoFileInfo.getExtraInfo();
            if (extra != null) {
                videoBody.setRawFileInfo(mapRaw(extra));
            }

            fileBody = videoBody;
        } else {
            fileBody = new IMFileBody(ImSdkFileConstant.FILE_NORMAL);
        }

        fileBody.setLocalPath(info.getFilePath());
        fileBody.setFileSize(info.getFileSize());
        fileBody.setTranslateSize(info.getTranslateSize());
        fileBody.setDisplayName(info.getFileName());
        fileBody.setSuffix(info.getSuffix());
        return fileBody;
    }

    /**
     * 文件扩展信息业务对象转换为ImSdk高清缩略图文件信息
     * @param fileExtraInfo 文件扩展信息业务对象
     * @return ImSdk高清缩略图文件信息
     */
    private IMHDThumbFileInfo mapHd(FileExtraInfo fileExtraInfo) {
        IMHDThumbFileInfo hdThumbFileInfo = new IMHDThumbFileInfo();
        hdThumbFileInfo.setHdTDisplayName(fileExtraInfo.getThumbFileName());
        hdThumbFileInfo.setHdTLocalPath(fileExtraInfo.getThumbFileUrl());
        hdThumbFileInfo.setHdTFileSize(fileExtraInfo.getThumbFileSize());
        hdThumbFileInfo.setHdTFileTranslateSize(fileExtraInfo.getThumbFileTranslateSize());
        return hdThumbFileInfo;
    }

    /**
     * 文件扩展信息业务对象转换为ImSdk原始文件信息
     * @param fileExtraInfo 文件扩展信息业务对象
     * @return ImSdk高清缩略图文件信息
     */
    private IMRawFileInfo mapRaw(FileExtraInfo fileExtraInfo) {
        IMRawFileInfo rawFileInfo = new IMRawFileInfo();
        rawFileInfo.setRawDisplayName(fileExtraInfo.getRawFileName());
        rawFileInfo.setRawLocalPath(fileExtraInfo.getRawFileUrl());
        rawFileInfo.setRawFileSize(fileExtraInfo.getRawFileSize());
        rawFileInfo.setRawFileTranslateSize(fileExtraInfo.getRawFileTranslateSize());
        return rawFileInfo;
    }

    //add by ycm 2017/4/1 for web message[start]
    /**
     * IMMessage ==> WebPageInfo
     * @param message message
     * @return WebPageInfo
     */
    private WebPageInfo mapWebFileInfo(IMMessage message) {
        if (message == null || message.getMessageBody() == null) {
            return null;
        }

        if (!message.isWebIMMessage()) {
            return null;
        }

        IMMessageBody body = message.getMessageBody();
        IMWebBody imWebFileBody = (IMWebBody) body;
        WebPageInfo webPageInfo = new WebPageInfo();
        webPageInfo.setFileState(mapFileState(imWebFileBody.getState()));
        webPageInfo.setTranslateSize(imWebFileBody.getTranslateSize());
        webPageInfo.setFileName(imWebFileBody.getDisplayName());
        webPageInfo.setFileSize(imWebFileBody.getFileSize());
        webPageInfo.setFilePath(imWebFileBody.getLocalPath());
        webPageInfo.setSuffix(imWebFileBody.getSuffix());
        webPageInfo.setTalkMessageId(message.getIMMessageId());
        webPageInfo.setTitle(imWebFileBody.getTitle());
        webPageInfo.setDescription(imWebFileBody.getDescription());
        webPageInfo.setWebUri(imWebFileBody.getUrl());
        webPageInfo.setSource(imWebFileBody.getSource());
        webPageInfo.setPercent(getPercent(imWebFileBody.getFileSize(), imWebFileBody.getTranslateSize()));
        return webPageInfo;
    }
    //add by ycm 2017/4/1 for web message[end]

    /**
     * IMMessage ==> FileInfo
     * @param message message
     * @return FileInfo
     */
    private FileInfo mapFileInfo(IMMessage message) {
        if (message == null || message.getMessageBody() == null) {
            return null;
        }

        if (!message.isFileIMMessage()) {
            return null;
        }

        try {
            IMMessageBody body = message.getMessageBody();
            IMFileBody fileBody = (IMFileBody) body;
            FileInfo fileInfo;

            if (fileBody.isVoice()){
                fileInfo =  new VoiceFileInfo();

                if (body instanceof IMVoiceBody) {
                    IMVoiceBody voiceBody = (IMVoiceBody) body;

                    if(voiceBody.getDuration() > 0){
                        ((VoiceFileInfo) fileInfo).setAmountOfTime(voiceBody.getDuration());
                    }
                }

                fileInfo.setFileType(ImSdkFileConstant.FILE_VOICE);

            } else if (fileBody.isVideo()) {
                //视频
	            fileInfo = new VideoFileInfo();
	            if (body instanceof IMVideoBody) {
                    IMVideoBody videoBody = (IMVideoBody) body;

                    ((VideoFileInfo) fileInfo).setAmountOfTime(videoBody.getDuration());
                    ((VideoFileInfo) fileInfo).setVideoSize(videoBody.getSize());

                    FileExtraInfo extraInfo = new FileExtraInfo();
                    extraInfo.setMsgId(message.getIMMessageId());

                    IMRawFileInfo raw = videoBody.getRawFileInfo();
                    if (raw != null) {

                        extraInfo.setRawFileName(raw.getRawDisplayName());
                        extraInfo.setRawFileSize(raw.getRawFileSize());
                        extraInfo.setRawFileTranslateSize(raw.getRawFileTranslateSize());
                        extraInfo.setRawFileUrl(raw.getRawLocalPath());
                    }

                    ((VideoFileInfo) fileInfo).setExtraInfo(extraInfo);

	            }
	            fileInfo.setFileType(ImSdkFileConstant.FILE_VIDEO);
            } else if (fileBody.isImage()) {
                fileInfo = new ImageFileInfo();

                if (body instanceof IMImageBody) {
                    IMImageBody imageBody = (IMImageBody) body;

                    FileExtraInfo extra = new FileExtraInfo();
                    extra.setMsgId(message.getIMMessageId());

                    IMHDThumbFileInfo hd = imageBody.getHdTFileInfo();
                    IMRawFileInfo raw = imageBody.getRawFileInfo();

                    if (hd != null) {
                        extra.setThumbFileName(hd.getHdTDisplayName());
                        extra.setThumbFileSize(hd.getHdTFileSize());
                        extra.setThumbFileTranslateSize(hd.getHdTFileSize());
                        extra.setThumbFileUrl(hd.getHdTLocalPath());
                    }

                    if (raw != null) {
                        ((ImageFileInfo) fileInfo).setOriginal(true);

                        extra.setRawFileName(raw.getRawDisplayName());
                        extra.setRawFileSize(raw.getRawFileSize());
                        extra.setRawFileTranslateSize(raw.getRawFileTranslateSize());
                        extra.setRawFileUrl(raw.getRawLocalPath());
                    } else {
                        ((ImageFileInfo) fileInfo).setOriginal(false);
                    }

                    ((ImageFileInfo) fileInfo).setExtraInfo(extra);
                }

                fileInfo.setFileType(ImSdkFileConstant.FILE_IMAGE);
            }else if(fileBody.isNormal()){
                //普通文件
                fileInfo = new FileInfo();
                fileInfo.setFileType(ConstDef.TYPE_NORMAL);
            } else {
                fileInfo = new FileInfo();
                fileInfo.setFileType(ConstDef.TYPE_OTHER);
            }

            fileInfo.setFileState(mapFileState(fileBody.getState()));
            fileInfo.setTranslateSize(fileBody.getTranslateSize());
            fileInfo.setFileName(fileBody.getDisplayName());
            fileInfo.setFileSize(fileBody.getFileSize());
            fileInfo.setFilePath(fileBody.getLocalPath());
            fileInfo.setSuffix(fileBody.getSuffix());
            fileInfo.setTalkMessageId(message.getIMMessageId());
			//add by zya fix bug 7669
            fileInfo.setPercent(getPercent(fileBody.getFileSize(),fileBody.getTranslateSize()));
            //end by zya
			return fileInfo;
        } catch (Exception e) {
            return null;
        }
    }
	//add by zya fix bug 7669
    private int getPercent(long size,long tSize){
        if(size == 0){
            return 0;
        }

        if(tSize >= size){
            return 100;
        }

        return (int) (tSize * 100/ size);

    } //end by zya

    /**
     * FileState ==> int
     * @param state state
     * @return int
     */
    private int mapFileState(ImSdkFileConstant.FileState state) {
        int result = ConstDef.INACTIVE;
        if(state == ImSdkFileConstant.FileState.DONE){
            result = ConstDef.DONE;
        }else if(state == ImSdkFileConstant.FileState.FAIL){
            result = ConstDef.FAIL;
        }else if(state == ImSdkFileConstant.FileState.INACTIVE){
            result = ConstDef.INACTIVE;
        }else if(state == ImSdkFileConstant.FileState.LOADING){
            result = ConstDef.LOADING;
        }else if(state == ImSdkFileConstant.FileState.PAUSE){
            result = ConstDef.PAUSE;
        }
        return result;
    }

    /**
     * IMMessage ==> VideoFileInfo
     * @param message message
     * @return VideoFileInfo
     */
    public VideoFileInfo mapVdeInfo(IMMessage message) {

        if (message == null ||
                message.getMessageBody() == null ||
                !(message.getMessageBody() instanceof IMVideoBody)) {
            return null;
        }

        VideoFileInfo videoFileInfo = new VideoFileInfo();
        IMVideoBody videoBody = (IMVideoBody) message.getMessageBody();
        videoFileInfo.setTalkMessageId(message.getIMMessageId());
        videoFileInfo.setAmountOfTime(videoBody.getDuration());
        videoFileInfo.setFilePath(videoBody.getLocalPath());
        videoFileInfo.setFileState(mapFileState(videoBody.getState()));

        IMRawFileInfo rawInfo = videoBody.getRawFileInfo();
        if (rawInfo != null) {
            videoFileInfo.setFileName(videoBody.getDisplayName());
            videoFileInfo.setSuffix("mp4");
            videoFileInfo.setFileType(ConstDef.TYPE_VIDEO);
            videoFileInfo.setFileSize(videoBody.getFileSize());
            videoFileInfo.setVideoSize(rawInfo.getRawFileSize());

            FileExtraInfo extraInfo = new FileExtraInfo();
            extraInfo.setRawFileUrl(rawInfo.getRawLocalPath());
            extraInfo.setRawFileName(rawInfo.getRawDisplayName());
            extraInfo.setRawFileSize(rawInfo.getRawFileSize());
            videoFileInfo.setExtraInfo(extraInfo);
        }
        return videoFileInfo;
    }

}
