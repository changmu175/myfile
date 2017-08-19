package com.xdja.imp.domain.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * 会话信息类定义
 */
public class TalkListBean implements Comparable<TalkListBean>, Parcelable {
    /**
     * 会话数据库id，用于唯一标识一个会话
     */
//    private long talkId; 暂时不用

    /**
     * 会话标识，用于唯一标识一个会话
     */
    private String talkFlag;

    /**
     * 会话对象ID 单聊：聊天对象账号   群组：群号
     */
    private String talkerAccount;

    /**
     * ImSdk返回的会话时间
     * 1、有消息时，为最后一条消息时间，
     * 2、新会话时，会话创建时间，
     * 3、消息被清空时，为最后一条消息时间
     */
    private long lastTime;

    /**
     * 会话中最后一条聊天消息
     */
    private TalkMessageBean lastMsg;

    /**
     * 会话中最后一条消息的发送方账号
     */
    private String lastMsgAccount;

    /**
     * 最后一条消息类型
     */
    @ConstDef.MsgType
    private int lastMsgType;

    /**
     * 新消息数量
     */
    private int notReadCount;

    /**
     * 会话类型（群聊/单人聊天）
     */
    @ConstDef.ChatType
    private int talkType = ConstDef.CHAT_TYPE_DEFAULT;

    /**
     * 会话是否置顶显示
     */
    private boolean isShowOnTop;

    /**
     * 新消息是否提醒
     */
    private boolean newMessageIsNotify = true;

    /**
     * 草稿创建时间
     */
    private long draftTime;

    /**
     * 是否有草稿
     */
    private boolean isHasDraft;

    /**
     * 草稿
     */
    private String draft;

    /**
     * 最后一条消息的显示内容
     */
    private CharSequence content;

    /**
     * 置顶或取消置顶的时间
     */
    private long showOnTopTime;

    public TalkListBean() {
    }

    protected TalkListBean(Parcel in) {
        talkFlag = in.readString();
        talkerAccount = in.readString();
        lastTime = in.readLong();
        draftTime = in.readLong();
        lastMsg = in.readParcelable(TalkMessageBean.class.getClassLoader());
        lastMsgAccount = in.readString();
        lastMsgType = in.readInt();
        notReadCount = in.readInt();
        talkType = in.readInt();
        isShowOnTop = (int) in.readByte() != 0;
        newMessageIsNotify = (int) in.readByte() != 0;
        isHasDraft = (int) in.readByte() != 0;
        draft = in.readString();
        showOnTopTime = in.readLong();
    }

    public static final Creator<TalkListBean> CREATOR = new Creator<TalkListBean>() {
        @Override
        public TalkListBean createFromParcel(Parcel in) {
            return new TalkListBean(in);
        }

        @Override
        public TalkListBean[] newArray(int size) {
            return new TalkListBean[size];
        }
    };

    public String getTalkFlag() {
        return talkFlag;
    }

    public void setTalkFlag(String talkFlag) {
        this.talkFlag = talkFlag;
    }

    public String getLastMsgAccount() {
        return lastMsgAccount;
    }

    public void setLastMsgAccount(String lastMsgAccount) {
        this.lastMsgAccount = lastMsgAccount;
    }

    public String getTalkerAccount() {
        return talkerAccount;
    }

    public void setTalkerAccount(String talkerAccount) {
        this.talkerAccount = talkerAccount;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public long getDraftTime() {
        return draftTime;
    }

    public void setDraftTime(long draftTime) {
        this.draftTime = draftTime;
    }

    public TalkMessageBean getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(TalkMessageBean lastMsg) {
        this.lastMsg = lastMsg;
    }

    public int getLastMsgType() {
        return lastMsgType;
    }

    public void setLastMsgType(int lastMsgType) {
        this.lastMsgType = lastMsgType;
    }

    public int getNotReadCount() {
        return notReadCount;
    }

    public void setNotReadCount(int notReadCount) {
        this.notReadCount = notReadCount;
    }

    @ConstDef.ChatType
    public int getTalkType() {
        return talkType;
    }

    public void setTalkType(int talkType) {
        this.talkType = talkType;
    }

    public boolean isShowOnTop() {
        return isShowOnTop;
    }

    public void setShowOnTop(boolean showOnTop) {
        isShowOnTop = showOnTop;
    }

    public boolean isNewMessageIsNotify() {
        return newMessageIsNotify;
    }

    public void setNewMessageIsNotify(boolean newMessageIsNotify) {
        this.newMessageIsNotify = newMessageIsNotify;
    }

    public boolean isHasDraft() {
        return isHasDraft;
    }

    public void setHasDraft(boolean hasDraft) {
        isHasDraft = hasDraft;
    }


    public CharSequence getContent() {
        if (content == null && lastMsg != null) {
            content = lastMsg.getContent();
        }
        return content;
    }



    public String getDraft() {
        return draft;
    }

    public void setDraft(String draft) {
        this.draft = draft;
    }


    public void setContent(CharSequence content) {
        this.content = content;
    }

    public long getShowOnTopTime() {
        return showOnTopTime;
    }

    public void setShowOnTopTime(long showOnTopTime) {
        this.showOnTopTime = showOnTopTime;
    }

    /**
     * 获取会话显示时间：ImSdk返回的会话时间和草稿时间中取其中的较大值
     * @return long
     */
    public long getDisplayTime() {
        if (this.lastTime >= this.draftTime) {
            return this.lastTime;
        }
        return this.draftTime;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TalkListBean{");
        sb.append(", talkerAccount='").append(talkerAccount).append('\'');
        sb.append(", lastTime=").append(lastTime);
        sb.append(", lastMsg=").append(lastMsg);
        sb.append(", notReadCount=").append(notReadCount);
        sb.append(", isShowOnTop=").append(isShowOnTop);
        sb.append(", newMessageIsNotify=").append(newMessageIsNotify);
        sb.append(", isHasDraft=").append(isHasDraft);
        sb.append(", draft='").append(draft).append('\'');
        sb.append(", content=").append(content);
        sb.append(", lastMsgAccount=").append(lastMsgAccount);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int compareTo(@NonNull TalkListBean another) {
        if (!this.isShowOnTop() && !another.isShowOnTop()) {
            if (this.getDisplayTime() == another.getDisplayTime()) {
                return 0;
            }
            return this.getDisplayTime() > another.getDisplayTime() ? -1 : 1;
        } else if (this.isShowOnTop() && another.isShowOnTop()) {
            if (this.getShowOnTopTime() == another.getShowOnTopTime()) {
                return 0;
            }
            return this.getShowOnTopTime() > another.getShowOnTopTime() ? -1 : 1;
        } else {
            return this.isShowOnTop() ? -1 : 1;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(talkFlag);
        dest.writeString(talkerAccount);
        dest.writeLong(lastTime);
        dest.writeLong(draftTime);
        dest.writeParcelable(lastMsg, flags);
        dest.writeString(lastMsgAccount);
        dest.writeInt(lastMsgType);
        dest.writeInt(notReadCount);
        dest.writeInt(talkType);
        dest.writeByte((byte) (isShowOnTop ? 1 : 0));
        dest.writeByte((byte) (newMessageIsNotify ? 1 : 0));
        dest.writeByte((byte) (isHasDraft ? 1 : 0));
        dest.writeString(draft);
        dest.writeLong(showOnTopTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TalkListBean that = (TalkListBean) o;

        return that.getTalkFlag().equals(this.getTalkFlag());
    }

    @Override
    public int hashCode() {
        return talkFlag.hashCode();
    }
}
