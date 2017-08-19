package com.xdja.imp.presenter.command;

import android.graphics.Bitmap;

import com.xdja.frame.presenter.mvp.Command;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.domain.model.VideoFileInfo;

import java.util.List;

/**
 * Created by jing on 2015/11/10.
 */
public interface IChatDetailCommand extends Command {

    List<TalkMessageBean> getMessageList();

    void setLimitFlagIsCheck(boolean isChecked);

    /**
     * 启动图库获取图片
     */
    void startToAlbum();

    /**
     * 相机拍照获取图片
     */
    void startToPhoto();

    /**
     * 文件管理器获取文件
     */
    void startToFileExplorer();
	
	/**
     * 摄像头获取小视频
     */
    void startToVideo();

    /**
     * 发送文本消息
     * @param message
     * @return
     */
    boolean sendTextMessage(String message);

    /**
     * 发送自定义文本消息
     * @param message
     * @return
     */
    boolean sendCustomTextMessage(String message, String to); // modified by ycm 20161229

    /**
     * 发送录音语音消息
     * @param path
     * @param seconds
     */
    void sendVoiceMessage(String path, int seconds);
	
	    /**
     * 发送小视频消息
     * @param videoFileInfo
     */
    void sendVideoMessage(VideoFileInfo videoFileInfo);

    /**
     * 发送图片文件消息
     * @param fileInfoList 文件信息
     */
    void sendImageMessage(List<FileInfo> fileInfoList);

    /**
     * 发送文件消息
     * @param fileInfos 文件信息
     * */
    void sendFileMessage(List<FileInfo> fileInfos);


    /**
     * 刷新文件下载列表
     */
    void downRefreshList();


    /**
     * 跳转到会话设置页面
     */
    void startSettingPage();

    /**
     * 跳转到群组会话设置界面
     */
    //TODO:gbc
    void startGroupSettingPage();

    /**
     * 获取显示名称
     *
     * @param contactId
     * @return
     */
    String getSenderShowName(String contactId);



    Bitmap getPhotoMiniMap(TalkMessageBean messageBean);


    CharSequence getVoiceLength(TalkMessageBean talkMessageBean);


    boolean getVoiceMessageIsPlaying(String messageId);


    /**
     * 获取当前的聊天类型
     */
    @ConstDef.ChatType
    int getSessionType();

    /**
     * 当前用户是否在群组中
     * @return
     */
    //TODO：gbc
    boolean getIsInGroup();

    /**
     * 拨打电话
     */
    void call();

    /**
     * 当前聊天对象是否为好友
     * @return
     */
    //TODO：kgg
    boolean getIsFriend();

    /**
     * 是否为单聊
     * @return
     */
    boolean getIsSingleChat();

    /**刷新消息列表*/
    void refreshMsgList();

    /**处理照相权限相关的问题*/
    void handleTakePhotoPermission(int code);

    String getTitlebarText();

}
