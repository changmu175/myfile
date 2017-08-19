package com.xdja.imp.domain.model;

import android.os.Parcel;

/**
 * Created by guorong on 2016/7/7.
 * 会话详情界面图片预览使用的图片信息
 */
public class ChatDetailPicInfo extends FileInfo {
    //缩略图路径
    private String thumPath;

    private String thumName;

    private long thumSize;

    private long thumTranslateSize;

    //高清缩略图路径
    private String hdThumPath;

    private String hdThumName;

    private long hdThumSize;

    private long hdThumTranslateSize;

    //原图路径
    private String rawPath;

    private String rawName;

    private long rawTranslateSize;

    private long rawSize;

    private long msgId;

    private String talkId;

    private boolean isBoom;

    private boolean isMine;

    private long fileSize;

    private String suffix;

    public ChatDetailPicInfo() {

    }


    protected ChatDetailPicInfo(Parcel in) {
        thumPath = in.readString();
        thumName = in.readString();
        thumSize = in.readLong();
        thumTranslateSize = in.readLong();
        hdThumPath = in.readString();
        hdThumName = in.readString();
        hdThumSize = in.readLong();
        hdThumTranslateSize = in.readLong();
        rawPath = in.readString();
        rawName = in.readString();
        rawTranslateSize = in.readLong();
        rawSize = in.readLong();
        msgId = in.readLong();
        talkId = in.readString();
        isBoom = (int) in.readByte() != 0;
        isMine = (int) in.readByte() != 0;
        fileSize = in.readLong();
        suffix = in.readString();
    }

    public static final Creator<ChatDetailPicInfo> CREATOR = new Creator<ChatDetailPicInfo>() {
        @Override
        public ChatDetailPicInfo createFromParcel(Parcel in) {
            return new ChatDetailPicInfo(in);
        }

        @Override
        public ChatDetailPicInfo[] newArray(int size) {
            return new ChatDetailPicInfo[size];
        }
    };

    public String getThumPath() {
        return thumPath;
    }

    public void setThumPath(String thumPath) {
        this.thumPath = thumPath;
    }

    public String getThumName() {
        return thumName;
    }

    public void setThumName(String thumName) {
        this.thumName = thumName;
    }

    public long getThumSize() {
        return thumSize;
    }

    public void setThumSize(long thumSize) {
        this.thumSize = thumSize;
    }

    public String getHdThumPath() {
        return hdThumPath;
    }

    public void setHdThumPath(String hdThumPath) {
        this.hdThumPath = hdThumPath;
    }

    public String getHdThumName() {
        return hdThumName;
    }

    public void setHdThumName(String hdThumName) {
        this.hdThumName = hdThumName;
    }

    public long getHdThumSize() {
        return hdThumSize;
    }

    public void setHdThumSize(long hdThumSize) {
        this.hdThumSize = hdThumSize;
    }

    public String getRawPath() {
        return rawPath;
    }

    public void setRawPath(String rawPath) {
        this.rawPath = rawPath;
    }

    public String getRawName() {
        return rawName;
    }

    public void setRawName(String rawName) {
        this.rawName = rawName;
    }

    public long getRawSize() {
        return rawSize;
    }

    public void setRawSize(long rawSize) {
        this.rawSize = rawSize;
    }

    public String getTalkId() {
        return talkId;
    }

    public void setTalkId(String talkId) {
        this.talkId = talkId;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public boolean isBoom() {
        return isBoom;
    }

    public void setBoom(boolean boom) {
        isBoom = boom;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public long getHdThumTranslateSize() {
        return hdThumTranslateSize;
    }

    public void setHdThumTranslateSize(long hdThumTranslateSize) {
        this.hdThumTranslateSize = hdThumTranslateSize;
    }

    public long getRawTranslateSize() {
        return rawTranslateSize;
    }

    public void setRawTranslateSize(long rawTranslateSize) {
        this.rawTranslateSize = rawTranslateSize;
    }

    public long getThumTranslateSize() {
        return thumTranslateSize;
    }

    public void setThumTranslateSize(long thumTranslateSize) {
        this.thumTranslateSize = thumTranslateSize;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(thumPath);
        dest.writeString(thumName);
        dest.writeLong(thumSize);
        dest.writeLong(thumTranslateSize);
        dest.writeString(hdThumPath);
        dest.writeString(hdThumName);
        dest.writeLong(hdThumSize);
        dest.writeLong(hdThumTranslateSize);
        dest.writeString(rawPath);
        dest.writeString(rawName);
        dest.writeLong(rawTranslateSize);
        dest.writeLong(rawSize);
        dest.writeLong(msgId);
        dest.writeString(talkId);
        dest.writeByte((byte) (isBoom ? 1 : 0));
        dest.writeByte((byte) (isMine ? 1 : 0));
        dest.writeLong(fileSize);
        dest.writeString(suffix);
    }
}
