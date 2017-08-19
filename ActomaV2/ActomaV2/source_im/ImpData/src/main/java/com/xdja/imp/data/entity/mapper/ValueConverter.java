package com.xdja.imp.data.entity.mapper;

import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.MsgType;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imsdk.constant.ChangeAction;
import com.xdja.imsdk.constant.IMSessionType;
import com.xdja.imsdk.constant.ImSdkFileConstant.FileType;
import com.xdja.imsdk.constant.ImSdkFileConstant.FileState;
import com.xdja.imsdk.constant.MsgPackType;
import com.xdja.imsdk.constant.MsgState;
import com.xdja.imsdk.model.IMMessage;
import com.xdja.imsdk.model.body.IMFileBody;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.entity.mapper</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/2</p>
 * <p>Time:9:12</p>
 */
public class ValueConverter {

    /**
     * 转换实体会话类型为业务会话类型
     *
     * @param sessionType 实体会话类型
     * @return 业务会话类型
     */
    @ConstDef.ChatType
    public static int sessionTypeConvert(int sessionType) {
        if (sessionType == IMSessionType.SESSION_SINGLE) {
            return ConstDef.CHAT_TYPE_P2P;
        }
        if (sessionType == IMSessionType.SESSION_GROUP){
            return ConstDef.CHAT_TYPE_P2G;
        }

        if (sessionType == IMSessionType.SESSION_CUSTOM) {
            return ConstDef.CHAT_TYPE_ACTOMA;
        }

        // TODO: 2016/11/21 liming 异常处理
        return ConstDef.CHAT_TYPE_P2P;
    }

    /**
     * 将业务会话类型转换为实体会话类型
     *
     * @param chatType 业务会话类型
     * @return 实体会话类型
     */
    public static int talkListTypeConvert(@ConstDef.ChatType int chatType) {
        switch (chatType){
            case ConstDef.CHAT_TYPE_P2G:
                return IMSessionType.SESSION_GROUP;
            case ConstDef.CHAT_TYPE_ACTOMA:
                return IMSessionType.SESSION_CUSTOM;
            default:
                return IMSessionType.SESSION_SINGLE;
        }
    }

    /**
     * 将实体消息类型转化为业务消息类型
     * @param imFileBody 文件类型消息内容
     * @return 消息类型
     */
    public static int imFileTypeConvert(IMFileBody imFileBody){

        if (imFileBody == null){
            return ConstDef.MSG_TYPE_VOICE;
        }

        int fileType = imFileBody.getType();
        LogUtil.getUtils().e("imMessageMapper2TalkMessage fileType : " + fileType);
        switch (fileType){
            case ConstDef.TYPE_VOICE: //音频
                return ConstDef.MSG_TYPE_VOICE;

            case ConstDef.TYPE_VIDEO: //视频
                return ConstDef.MSG_TYPE_VIDEO;
            case ConstDef.TYPE_WEB: //网页
                return ConstDef.MSG_TYPE_WEB;
            case ConstDef.TYPE_PHOTO: //图片
                return ConstDef.MSG_TYPE_PHOTO;

            case ConstDef.TYPE_APK:   //以下为已定义文件类型
            case ConstDef.TYPE_TXT:
            case ConstDef.TYPE_WORD:
            case ConstDef.TYPE_PPT:
            case ConstDef.TYPE_EXCEL:
            case ConstDef.TYPE_PDF:
            case ConstDef.TYPE_ZIP:
                return ConstDef.MSG_TYPE_FILE;
            default:
                return ConstDef.MSG_TYPE_FILE;
        }
    }

    /**
     * 将实体消息类型转化为业务消息类型
     *
     * 注意：此处不考虑是否为闪信，在应用处理时，要先判断是否为闪信和群组，然后再找对应的文件类型
     *
     * @return 业务消息类型
     */
    @ConstDef.MsgType
    public static int imMsgTypeConvert(IMMessage imMessage) {

        if ((imMessage.getType() & MsgType.MSG_TYPE_PRESENTATION) ==
                MsgType.MSG_TYPE_PRESENTATION){

            //自定义消息
            return ConstDef.MSG_TYPE_PRESENTATION;
        } else if (imMessage.isTextIMMessage()) {

            //文本消息
            return ConstDef.MSG_TYPE_TEXT;
        } else if (imMessage.isFileIMMessage()) {
			//[S]lll@xdja.com 2016-08-05 add. fix bug 2349. review by liming.
            //文件消息
            if (imMessage.getMessageBody() != null){
                return imFileTypeConvert((IMFileBody) imMessage.getMessageBody());
            } else {
                try {
                    //当闪信文件销毁后，content中存放文件的类型
                    // TODO: 2016/11/24 liming optimize
                    int fileType = Integer.parseInt(imMessage.getMessageBody().toString());
                    switch (fileType){
                        case ConstDef.MSG_TYPE_VOICE:
                            return ConstDef.MSG_TYPE_VOICE;
                        case ConstDef.MSG_TYPE_VIDEO:
                            return ConstDef.MSG_TYPE_VIDEO;
                        case ConstDef.MSG_TYPE_PHOTO:
                            return ConstDef.MSG_TYPE_PHOTO;
                        case ConstDef.MSG_TYPE_WEB:
                            return ConstDef.MSG_TYPE_WEB;
                        default:
                            return ConstDef.MSG_TYPE_FILE;
                    }
                } catch (NumberFormatException e) {
                    //e.printStackTrace();
                }
            }
			//[E]lll@xdja.com 2016-08-05 add. fix bug 2349. review by liming.
        } else if (imMessage.isWebIMMessage()) {
            return ConstDef.MSG_TYPE_WEB; // add by ycm for sharing web message
        }
        return ConstDef.MSG_TYPE_TEXT;
    }

    /**
     * 转化消息类型
     * @param messageBean
     * @return
     */
    public static int talkMsgTypeConvert(TalkMessageBean messageBean){
        int talkMsgType = messageBean.getMessageType();
        if(!messageBean.isGroupMsg()){
            if(messageBean.isBomb()){
                if(talkMsgType == ConstDef.MSG_TYPE_TEXT){
                    //闪信文本 0000 1001
                    return MsgPackType.BOMB_PP_TEXT;
                } else {
                    //闪信文件 0000 1010
                    return MsgPackType.BOMB_PP_FILE;
                }
            } else {
                if (talkMsgType == ConstDef.MSG_TYPE_PRESENTATION){
                    //单聊提示消息 1000 0001
                    return MsgPackType.NOTICE_PP_TEXT;
                } else if (talkMsgType == ConstDef.MSG_TYPE_TEXT){
                    //文本消息 0000 0001
                    return MsgPackType.NORMAL_PP_TEXT;
                } else {
                    //文件消息 0000 0010
                    return MsgPackType.NORMAL_PP_FILE;
                }
            }
        } else {
            if(messageBean.isBomb()){
                if(talkMsgType == ConstDef.MSG_TYPE_TEXT){
                    //群组闪信文本消息 0000 1101
                    return MsgPackType.BOMB_PG_TEXT;
                } else{
                    //群组闪信文件消息 0000 1110
                    return MsgPackType.BOMB_PG_FILE;
                }
            } else {
                if (talkMsgType == ConstDef.MSG_TYPE_PRESENTATION){
                    //群组提示消息 1000 0101
                    return MsgPackType.NOTICE_PG_TEXT;
                } else if (talkMsgType == ConstDef.MSG_TYPE_TEXT){
                    //群组文本消息 0000 0101
                    return MsgPackType.NORMAL_PG_TEXT;
                } else {
                    //群组文件消息 0000 0110
                    return MsgPackType.NORMAL_PG_FILE;
                }
            }
        }
    }

    /**
     * 文件类型转换(缩略图，高清缩略图，原始文件)
     * @param fileType 文件类型
     * @return ImSdkFileConstant
     */
    public static FileType fileTypeConvert(int fileType) {
        switch (fileType) {
            case ConstDef.FILE_IS_RAW:
                return FileType.IS_RAW;
            case ConstDef.FILE_IS_THUMB_HD:
                return FileType.IS_HD;
            default:
                return FileType.IS_SHOW;
        }
    }

    /**
     * 文件状态转换
     * @param state
     * @return
     */
    public static int fileStateConvert(FileState state) {
        return 0;// TODO: 2016/12/5 liming optimize
    }

    /**
     * 将实体消息状态转化为业务消息状态
     *
     * @param imMsgState 实体消息状态
     * @return 业务消息状态
     */
    @ConstDef.MsgState
    public static int imMsgStateConvert(int imMsgState) {

        @ConstDef.MsgState int state = ConstDef.STATE_SENDING;
        switch (imMsgState) {
            case MsgState.MSG_STATE_DEFAULT:
                state = ConstDef.STATE_SENDING;
                break;
            case MsgState.MSG_STATE_FAIL:
                state = ConstDef.STATE_SEND_FAILD;
                break;
            case MsgState.MSG_STATE_SEND:
                state = ConstDef.STATE_SEND_SUCCESS;
                break;
            case MsgState.MSG_STATE_REC:
                state = ConstDef.STATE_ARRIVE;
                break;
            case MsgState.MSG_STATE_READ:
                state = ConstDef.STATE_READED;
                break;
            case MsgState.MSG_STATE_BOMB:
                state = ConstDef.STATE_DESTROY;
                break;
            default:
                break;
        }
        return state;
    }

    /**
     * 将业务消息状态转化为实体消息状态
     *
     * @param talkMsgState 业务消息状态
     * @return 实体消息状态
     */
    public static int talkMsgStateConvert(@ConstDef.MsgState int talkMsgState) {
//        LogUtil.getUtils().d("转换   传入消息类型 talkMsgState = " + talkMsgState);
        int state = MsgState.MSG_STATE_DEFAULT;
        switch (talkMsgState) {
            case ConstDef.STATE_DEFAULT:// add by ycm for lint 2017/02/17
                break;
            case ConstDef.STATE_DESTROYING:
                break;
            case ConstDef.STATE_SENDING:
                state = MsgState.MSG_STATE_DEFAULT;
                break;
            case ConstDef.STATE_SEND_FAILD:
                state = MsgState.MSG_STATE_FAIL;
                break;
            case ConstDef.STATE_SEND_SUCCESS:
                state = MsgState.MSG_STATE_SEND;
                break;
            case ConstDef.STATE_ARRIVE:
                state = MsgState.MSG_STATE_REC;
                break;
            case ConstDef.STATE_READED:
                state = MsgState.MSG_STATE_READ;
                break;
            case ConstDef.STATE_DESTROY:
                state = MsgState.MSG_STATE_BOMB;
                break;
            default:
                break;
        }
        return state;
    }

    /**
     * 消息更改动作匹配
     *
     * @param changeAction 更改动作
     * @return 匹配后的消息动作代码
     */
    @ConstDef.ActionRefMsg
    public static int msgChangeActionConvert(ChangeAction changeAction) {

        @ConstDef.ActionRefMsg int act = ConstDef.ACTION_MODIFY;

        switch (changeAction) {
            case ACT_ADD:
                act = ConstDef.ACTION_ADD;
                break;
            case ACT_DEL:
                act = ConstDef.ACTION_DELETE;
                break;
            case ACT_RF:
                act = ConstDef.ACTION_MODIFY;
                break;
            case ACT_SC:
                act = ConstDef.ACTION_MODIFY;
                break;
            default:
                break;
        }

        return act;
    }
    /**
     * 会话更改动作匹配
     *
     * @param changeAction 更改动作
     * @return 匹配后的会话动作代码
     */
    @ConstDef.ActionRefCon
    public static int sessionChangeActionConvert(ChangeAction changeAction) {

        @ConstDef.ActionRefCon int act = ConstDef.ACTION_MODIFY;

        switch (changeAction) {
            case ACT_ADD:
                act = ConstDef.ACTION_ADD;
                break;
            case ACT_DEL:
                act = ConstDef.ACTION_DELETE;
                break;
            case ACT_RF:
                act = ConstDef.ACTION_REFRESH;
                break;
            case ACT_SC:
                act = ConstDef.ACTION_MODIFY;
                break;
            default:
                break;
        }

        return act;
    }
}
