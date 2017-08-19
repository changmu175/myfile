package com.xdja.imp.presenter.command;


import android.text.SpannableString;
import android.view.View;

import com.xdja.contactopproxy.Bean.ContactInfo;
import com.xdja.frame.presenter.mvp.Command;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.domain.model.VideoFileInfo;

/**
 * Created by jing on 2015/12/25.
 * 功能描述
 * 修改备注
 * 1)Task 2632, modify for hyperlink click by ycm at 20161104.
 */
public interface ChatDetailAdapterCommand extends Command {

    /**
     * 根据id获取TalkMessageBean
     */
    TalkMessageBean getTalkMsgBean(int position);

    /**
     * 设置普通文本消息字体颜色
     *
     * @return
     */
    int getNormalTextColor();

    /**
     * 设置文本闪信的字体颜色
     *
     * @return
     */
    int getLimitTextColor();


    SpannableString getShowContentFromString(TalkMessageBean talkMessageBean);

    boolean getActivityIsShowing();

    //fix bug 2705 by licong, reView zya, 2016/08/17
    boolean getActivityIsDestroy();

    void sendReadReceipt(TalkMessageBean talkMessageBean);

    void sendDestroyedReceipt(TalkMessageBean talkMessageBean);

    void postDestroyAnimate(TalkMessageBean talkMessageBean);


    void reSendMessage(TalkMessageBean messageBean);

    /**
     * 长按消息
     * @param bean
     * @param view
     */
    void longClickMessage(TalkMessageBean bean, View view);


    /**
     * 根据不同文件类型获取相应的显示图标id
     * */
    int getFileLogoId(TalkMessageBean talkMessageBean);

    /**
     * 删除单条消息
     *
     * @param talkMessageBean 消息对象
     */
    void deleteSingleMessage(TalkMessageBean talkMessageBean);

    /**
     * 获取账号
     * @param account
     * @return
     */
    ContactInfo getContactInfo(String account);

    /**
     * 获取群组成员信息
     * @param groupId 群组ID
     * @param account 成员账号
     * @return
     */
    ContactInfo getGroupMemberInfo(String groupId, String account);

    /**
     * 拉起联系人界面
     * @param account
     */
    void startContactDetailActivity(String account);

    /**
     * 获取语音时长显示字符串
     * @param talkMessageBean
     * @return
     */
    CharSequence getVoiceLength(TalkMessageBean talkMessageBean);

    /**
     * 获取语音消息是否正在播放，必须使用文件绝对路径和消息Id同时进行约束。
     * 比如： 1）不同文件夹下语音文件名称相同
     *       2）收到重复的语音消息
     *       在这以上两种情况下，如果在同一回话中，则会播放错误
     * @return
     */
    boolean getVoiceMessageIsPlaying(String filePath, long messageId);


    /**
     * 点击语音消息
     * @param talkMessageBean
     */
    void clickVoiceMessage(TalkMessageBean talkMessageBean);

    /**
     * 点击小视频消息
     * @param talkMessageBean
     */
    void clickVideoMessage(TalkMessageBean talkMessageBean);

    /**
     * 下载小视频消息
     * @param videoInfo
     */
    void downVideoMessage(VideoFileInfo videoInfo);

    /**
     * 获取最后一条显示时间轴的消息位置
     */
    int getLastTimeLineIsShowPosition();

    /**
     * 点击图片消息
     * @param talkMessageBean
     */
    void clickImageMessage(TalkMessageBean talkMessageBean);

    /**
     * 加载网络图片资源
     * @param talkMessageBean
     */
    void loadImage(TalkMessageBean talkMessageBean);


    /**发送闪信销毁事件*/
    void postMsgDestory(TalkMessageBean bean);
	
	/**点击网页消息*/
    void clickWebMessage(TalkMessageBean talkMessageBean);
}
