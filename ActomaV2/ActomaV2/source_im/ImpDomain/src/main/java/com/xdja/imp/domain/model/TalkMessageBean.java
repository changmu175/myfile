package com.xdja.imp.domain.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 消息类定义.
 */
public class TalkMessageBean implements Parcelable {

    private long _id;
    /**
     * 消息内容
     */
    protected String content;
    /**
     * 消息类型（0：文本，1:图片，2：语音，3:文件）
     */
    @ConstDef.MsgType
    private int messageType = ConstDef.MSG_TYPE_TEXT;
    /**
     * 消息状态
     */
    @ConstDef.MsgState
    private int messageState = ConstDef.STATE_SEND_FAILD;
    /**
     * 是否为自己发送的消息
     */
    private boolean isMine = false;
    /**
     * 排序时间
     */
    private long sortTime;
    /**
     * 展示时间
     */
    private long showTime;

    /**
     * 闪信持续时间（非正数为非闪信）
     */
    private int limitTime = 0;
    /**
     * 消息发送发标识
     */
    private String from;
    /**
     * 消息接收方标识
     */
    private String to;
    /**
     * 是否为群发消息
     */
    private boolean isGroupMsg = false;
    /**
     * 发送方卡ID
     */
    private String senderCardId;

    /**
     * 失败原因
     */
    private int failCode;

    /**
     * 是否是闪信
     */
    private boolean isBomb = false;
    //[S]add by lixiaolong on 20160902. fix bug 3158. review by gbc.
    /**
     * 是否显示时间线
     */
    private boolean isShowTimeLine = false;
    //[E]add by lixiaolong on 20160902. fix bug 3158. review by gbc.
    private FileInfo fileInfo;


    //add by zya
    private boolean isSelect;//选择模式
    private String categoryId;
    private int progress;
    private boolean isCheck;//是否选择
    private int downloadState;

    public void setDownloadState(int downloadState){
        this.downloadState = downloadState;
    }

    public int getDownloadState(){
        return downloadState;
    }

    public boolean isCheck(){
        return isCheck;
    }

    public void setCheck(boolean isCheck){
        this.isCheck = isCheck;
    }

    public void setProgress(int progress){
        this.progress = progress;
    }

    public int getProgress(){
        return progress;
    }

    public String getCategoryId(){
        return categoryId;
    }

    public void setCategoryId(String categoryId){
        this.categoryId = categoryId;
    }

    public boolean isSelect(){
        return isSelect;
    }

    public void setSelect(boolean isSelect){
        this.isSelect = isSelect;
    }

    public FileInfo getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }


    public int getFailCode() {
        return failCode;
    }

    public void setFailCode(int failCode) {
        this.failCode = failCode;
    }


    public boolean isBomb() {
        return isBomb;
    }

    public void setIsBomb(boolean isBomb) {
        this.isBomb = isBomb;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @ConstDef.MsgType
    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(@ConstDef.MsgType int messageType) {
        this.messageType = messageType;
    }

    @ConstDef.MsgState
    public int getMessageState() {
        return messageState;
    }

    public void setMessageState(@ConstDef.MsgState int messageState) {
        this.messageState = messageState;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public long getSortTime() {
        return sortTime;
    }

    public void setSortTime(long sortTime) {
        this.sortTime = sortTime;
    }

    public long getShowTime() {
        return showTime;
    }

    public void setShowTime(long showTime) {
        this.showTime = showTime;
    }

    public int getLimitTime() {
        return limitTime;
    }

    public void setLimitTime(int limitTime) {
        this.limitTime = limitTime;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public boolean isGroupMsg() {
        return isGroupMsg;
    }

    public void setGroupMsg(boolean groupMsg) {
        isGroupMsg = groupMsg;
    }

    public String getSenderCardId() {
        return senderCardId;
    }

    public void setSenderCardId(String senderCardId) {
        this.senderCardId = senderCardId;
    }

    public boolean isShowTimeLine() {
        return isShowTimeLine;
    }

    public void setShowTimeLine(boolean showTimeLine) {
        isShowTimeLine = showTimeLine;
    }

    @Override
    public String toString() {
        return "TalkMessageBean{" +
                "_id=" + _id +
                ", content='" + content + '\'' +
                ", messageType=" + messageType +
                ", messageState=" + messageState +
                ", isMine=" + isMine +
                ", sortTime=" + sortTime +
                ", showTime=" + showTime +
                ", limitTime=" + limitTime +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", isGroupMsg=" + isGroupMsg +
                ", failCode=" + failCode +
                ", senderCardId='" + senderCardId + '\'' +
                ", fileInfo='" + fileInfo + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(_id);
        out.writeString(content);
        out.writeInt(messageType);
        out.writeInt(messageState);
        out.writeByte((byte) (isMine ? 1 : 0));
        out.writeLong(sortTime);
        out.writeLong(showTime);
        out.writeInt(limitTime);
        out.writeString(from);
        out.writeString(to);
        out.writeByte((byte) (isGroupMsg ? 1 : 0));
        out.writeInt(failCode);
        out.writeString(senderCardId);
        out.writeParcelable(fileInfo, flags);
    }

    public static final Parcelable.Creator<TalkMessageBean> CREATOR = new Parcelable.Creator<TalkMessageBean>()
    {
        @Override
        public TalkMessageBean createFromParcel(Parcel in)
        {
            return new TalkMessageBean(in);
        }

        @Override
        public TalkMessageBean[] newArray(int size)
        {
            return new TalkMessageBean[size];
        }
    };

    public TalkMessageBean(){

    }

    /**add by zya 20170111
     * COPY构造函数
     * @param bean
     */
    public TalkMessageBean(TalkMessageBean bean){
        sortTime = bean.getSortTime();
        isMine = bean.isMine();
        showTime = bean.getShowTime();
        content = bean.getContent();
        to = bean.getTo();
        failCode = bean.getFailCode();
        _id = bean.get_id();
        fileInfo = bean.getFileInfo() != null ?
                new FileInfo(bean.getFileInfo()) : null;
        from = bean.getFrom();
        isBomb = bean.isBomb();
        isGroupMsg = bean.isGroupMsg();
        senderCardId = bean.getSenderCardId();
        limitTime = bean.getLimitTime();
        messageType = bean.getMessageType();
    }

    private TalkMessageBean(Parcel in)
    {
        _id = in.readLong();
        content = in.readString();
        messageType = getType(in.readInt());
        messageState = getState(in.readInt());
        isMine = (int) in.readByte() != 0;
        sortTime = in.readLong();
        showTime = in.readLong();
        limitTime = in.readInt();
        from = in.readString();
        to = in.readString();
        isGroupMsg = (int) in.readByte() != 0;
        failCode = in.readInt();
        senderCardId = in.readString();
        fileInfo = in.readParcelable(FileInfo.class.getClassLoader());
    }

    @ConstDef.MsgType
    private static int getType(int type){
        return type;
    }

    @ConstDef.MsgState
    private static int getState(int state){
        return state;
    }

    //add by zya@xdja.com,fix bug NACTOMA-240._id作为唯一标识
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (_id ^ (_id >>> 32));
        return result;
    }

    //add by zya@xdja.com,fix bug NACTOMA-240. _id作为唯一标识
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TalkMessageBean other = (TalkMessageBean) obj;
        if (_id != other._id)
            return false;
        return true;
    }
}
