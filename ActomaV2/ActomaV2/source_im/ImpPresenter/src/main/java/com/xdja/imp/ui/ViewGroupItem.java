package com.xdja.imp.ui;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xdja.contactopproxy.Bean.ContactInfo;
import com.xdja.imp.ImApplication;
import com.xdja.imp.R;
import com.xdja.imp.data.cache.UserCache;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.MsgType;
import com.xdja.imp.domain.model.TalkListBean;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.util.BitmapUtils;
import com.xdja.imp.util.Functions;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.presenter.refctor.ui</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/24</p>
 * <p>Time:16:23</p>
 */

public class ViewGroupItem extends ViewChatItem {

    private final int MSG_BOM = 0x08;

    //end fix bug 5000 by licong, reView by zya,2016/10/14
    //    @InjectView(R.id.groupName)
    private TextView userName;

    //    @InjectView(R.id.message_state)
    private ImageView messageState;

    //    @InjectView(R.id.content)
    private TextView content;

    //    @InjectView(R.id.senderName)
    private TextView senderName;

    private TextView senderNameSeparate;


    @Override
    protected int getLayoutRes() {
        return R.layout.chatlist_item_groupchat;
    }

    @Override
    protected void injectView() {
        super.injectView();

        View view = getView();
        if (view != null) {
            userName = (TextView) view.findViewById(R.id.groupName);
            messageState = (ImageView) view.findViewById(R.id.message_state);
            content = (TextView) view.findViewById(R.id.content);
            senderName = (TextView) view.findViewById(R.id.senderName);
            senderNameSeparate = (TextView) view.findViewById(R.id.senderNameSeparate);
        }
    }


    @Override
    public void bindDataSource(int position, @NonNull TalkListBean dataSource) {
        super.bindDataSource(position, dataSource);
        userName.setText(showChatName());
        senderName.setText(getSenderName());
        if (dataSource.isHasDraft()) {
            content.setText(BitmapUtils.formatSpanContent(
                    Functions.formatDraft(dataSource.getDraft()), getContext(), 0.7f));
        } else if (!TextUtils.isEmpty(getContent())) {
            content.setText(new SpannableString(BitmapUtils.formatSpanContent(getContent()
                    , getContext(), 0.7f)));
        } else {
            content.setText(getContent());
        }

        messageState.setVisibility(View.GONE);
        if(!dataSource.isHasDraft()) {//没有草稿的情况下才会显示消息状态图标
            if (getMessageState() == ConstDef.STATE_SEND_FAILD &&
                    dataSource.getLastMsg().getMessageType() != ConstDef.MSG_TYPE_PRESENTATION) {
                messageState.setImageResource(R.drawable.ic_message_fail);
                messageState.setVisibility(View.VISIBLE);
            }
            if (getMessageState() == ConstDef.STATE_SENDING &&
                    dataSource.getLastMsg().getMessageType() != ConstDef.MSG_TYPE_PRESENTATION) {
                messageState.setImageResource(R.drawable.ic_message_send);
                messageState.setVisibility(View.VISIBLE);
            }
        }

        ContactInfo info = getCommand().getGroupInfo(dataSource.getTalkerAccount());
        setCircleImageUrl(info.getAvatarUrl(), getDefaultImageId());
        showSenderNameSeparate();
    }

    //自定義消息，隱藏分隔符
    private void showSenderNameSeparate() {
        boolean isShow;

        if(dataSource != null) {
            //modify by zya ,20170309
            TalkMessageBean bean = dataSource.getLastMsg();
            if (bean != null && bean.get_id() != 0) {
                //end by zya
                isShow = !bean.isMine() ;
            } else {
                String lastMsgAccount = dataSource.getLastMsgAccount();

                UserCache userCache = ImApplication.androidApplication.getComponent().userCache();
                String currentAccount = userCache == null ? "" : userCache.get().getAccount();

                //modify by zya@xdja.com.20161013
                isShow = !currentAccount.equals(lastMsgAccount);

                //add by zya ,fix bug 8305 ,20170204
                int lastMessageType = 13;
                if (TextUtils.isEmpty(dataSource.getContent()) && dataSource.getLastMsgType() != lastMessageType) {
                    isShow = false;
                }//end by zya
            }
            if (!TextUtils.isEmpty(dataSource.getDraft())) {
                isShow = false;
            }
        } else {
            isShow = false;
        }

        senderName.setVisibility(isShow ? View.VISIBLE : View.GONE);
        senderNameSeparate.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    private String showChatName() {
        if (dataSource != null && dataSource.getTalkerAccount() != null) {
            String name = getCommand().getGroupInfo(dataSource.getTalkerAccount()).getName();
            if (TextUtils.isEmpty(name)) {
                return getStringRes(R.string.group_name_default);
            }
            return name;
        }
        return "";
    }

    //[S]lll@xdja.com 2016-08-08 add. fix bug 1684 . review by liming.
    @SuppressLint("SwitchIntDef")
    private CharSequence getContent() {
        TalkMessageBean talkMessageBean = dataSource.getLastMsg();
        //进入应用或者从聊天详情返回到会话列表，回话列表拉取消息时，不为空
        if (talkMessageBean != null){
            //获取消息类型
            @ConstDef.MsgType
            int messageType = talkMessageBean.getMessageType();
            if (!talkMessageBean.isMine() &&
                    talkMessageBean.isBomb() &&//闪信并且已销毁
                    (talkMessageBean.getMessageState() == ConstDef.STATE_DESTROY)){
                return getContext().getResources().getString(R.string.boom_message);
            } else {//普通消息
                switch (messageType) {
                    case ConstDef.MSG_TYPE_TEXT:
                        return dataSource.getContent();
                    case ConstDef.MSG_TYPE_VOICE:
                        return getContext().getResources().getString(R.string.voice_message);
//                    case ConstDef.MSG_TYPE_VIDEO:
//                        return getContext().getResources().getString(R.string.video_message);
                    case ConstDef.MSG_TYPE_PHOTO:
                        return getContext().getResources().getString(R.string.photo_message);
                    case ConstDef.MSG_TYPE_FILE:
                        return getContext().getResources().getString(R.string.file_message);
                    default:
                        return dataSource.getContent();
                }
            }
        } else {//会话界面收到消息
            int lastMsgType = dataSource.getLastMsgType();
            //闪信消息
            if ((lastMsgType & MsgType.MSG_TYPE_BOMB) == MsgType.MSG_TYPE_BOMB){
                //文本销毁后，content内容为空；文件销毁后，content字段填写为该文件的类型
                if (TextUtils.isEmpty(dataSource.getContent())){ //文本闪信已经销毁
                    return getContext().getResources().getString(R.string.boom_message);
                } else {
                    //文本或者是文件（已销毁或者未销毁）
                    if ((lastMsgType & MsgType.MSG_TYPE_TEXT) == MsgType.MSG_TYPE_TEXT){
                        return dataSource.getContent();
                    } else {
                        try {
                            //文件已销毁(通过校验是否为数字，来判断文件是否已经销毁)
                            Integer.parseInt(dataSource.getContent().toString());
                            return getContext().getResources().getString(R.string.boom_message);
                        } catch (NumberFormatException e) {
                            //文件未销毁
                            if (ConstDef.FILE_NAME_VOICE.equals(dataSource.getContent())) {
                                return getContext().getResources().getString(R.string.voice_message);
                            }
//                            else if (ConstDef.FILE_NAME_VIDEO.equals(dataSource.getContent())) {
//                                return getContext().getResources().getString(R.string.video_message);
//                            }
                            else if (ConstDef.FILE_NAME_IMAGE.equals(dataSource.getContent())) {
                                return getContext().getResources().getString(R.string.photo_message);
                            } else {
                                return dataSource.getContent();
                            }
                        }
                    }
                }
            }
            //普通消息
            else {
                if (ConstDef.FILE_NAME_VOICE.equals(dataSource.getContent())) {
                    return getContext().getResources().getString(R.string.voice_message);
                } 
//				else if (ConstDef.FILE_NAME_VIDEO.equals(dataSource.getContent())) {
//                return getContext().getResources().getString(R.string.video_message);
//                } 
				else if (ConstDef.FILE_NAME_IMAGE.equals(dataSource.getContent())) {
                    return getContext().getResources().getString(R.string.photo_message);
                } else {
                    return dataSource.getContent();
                }
            }
        }
    }
    //[E]lll@xdja.com 2016-08-08 add. fix bug 1684 . review by liming.

    /**
     * 获取消息发送状态
     * @return
     */
    private String getSenderName() {
        TalkMessageBean lastMsg = dataSource.getLastMsg();

        String account = "";
        String groupId = dataSource.getTalkerAccount();
        if (lastMsg != null) {
            account = lastMsg.getFrom();

        }
        //判断如果是自定義消息，返回空
        if (dataSource.getLastMsgType() == ConstDef.MSG_TYPE_PRESENTATION) {
            return "";
        }

        if (TextUtils.isEmpty(account)) {
            account = dataSource.getLastMsgAccount();
        }

        if (!TextUtils.isEmpty(account)) {
            return getCommand().getGroupMemberInfo(groupId, account).getName();
        }
        return "";
    }


    @Override
    protected int getDefaultImageId() {
        return R.drawable.group_avatar_40;
    }

    private int getMessageState() {
        if (dataSource.getLastMsg() == null) {
            return ConstDef.STATE_DEFAULT;
        } else if (dataSource.getLastMsg().isMine() &&
                dataSource.getLastMsg().getMessageState() == ConstDef.STATE_SEND_FAILD) {
            return ConstDef.STATE_SEND_FAILD;
        } else if (dataSource.getLastMsg().isMine() &&
                dataSource.getLastMsg().getMessageState() == ConstDef.STATE_SENDING) {
            return ConstDef.STATE_SENDING;
        }
        return ConstDef.STATE_DEFAULT;
    }
}
