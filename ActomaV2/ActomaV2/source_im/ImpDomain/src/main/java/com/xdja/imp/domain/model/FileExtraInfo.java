package com.xdja.imp.domain.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by xdjaxa on 2016/7/1.
 * Modify history description:
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 */
public class FileExtraInfo implements Parcelable, Serializable{

    /**
     * 文件消息ID
     */
    private long msgId;

    /**
     * 原始文件名称
     */
    private String rawFileName;

    /**
     * 原始文件URL
     */
    private String rawFileUrl;

    /**
     * 原始文件大小
     */
    private long rawFileSize;

    /**
     * 原始文件已传输大小
     */
    private long rawFileTranslateSize;

    /**
     * 缩略图文件名称
     */
    private String thumbFileName;

    /**
     * 缩略图文件路径
     */
    private String thumbFileUrl;

    /**
     * 缩略图文件大小
     */
    private long thumbFileSize;

    /**
     * 缩略图文件传输大小
     */
    private long thumbFileTranslateSize;

    public FileExtraInfo() {
    }

    protected FileExtraInfo(Parcel in) {
        msgId = in.readLong();
        rawFileName = in.readString();
        rawFileUrl = in.readString();
        rawFileSize = in.readLong();
        rawFileTranslateSize = in.readLong();
        thumbFileName = in.readString();
        thumbFileUrl = in.readString();
        thumbFileSize = in.readLong();
        thumbFileTranslateSize = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(msgId);
        dest.writeString(rawFileName);
        dest.writeString(rawFileUrl);
        dest.writeLong(rawFileSize);
        dest.writeLong(rawFileTranslateSize);
        dest.writeString(thumbFileName);
        dest.writeString(thumbFileUrl);
        dest.writeLong(thumbFileSize);
        dest.writeLong(thumbFileTranslateSize);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FileExtraInfo> CREATOR = new Creator<FileExtraInfo>() {
        @Override
        public FileExtraInfo createFromParcel(Parcel in) {
            return new FileExtraInfo(in);
        }

        @Override
        public FileExtraInfo[] newArray(int size) {
            return new FileExtraInfo[size];
        }
    };

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public String getRawFileName() {
        return rawFileName;
    }

    public void setRawFileName(String rawFileName) {
        this.rawFileName = rawFileName;
    }

    public String getRawFileUrl() {
        return rawFileUrl;
    }

    public void setRawFileUrl(String rawFileUrl) {
        this.rawFileUrl = rawFileUrl;
    }

    public long getRawFileSize() {
        return rawFileSize;
    }

    public void setRawFileSize(long rawFileSize) {
        this.rawFileSize = rawFileSize;
    }

    public long getRawFileTranslateSize() {
        return rawFileTranslateSize;
    }

    public void setRawFileTranslateSize(long rawFileTranslateSize) {
        this.rawFileTranslateSize = rawFileTranslateSize;
    }

    public String getThumbFileName() {
        return thumbFileName;
    }

    public void setThumbFileName(String thumbFileName) {
        this.thumbFileName = thumbFileName;
    }

    public String getThumbFileUrl() {
        return thumbFileUrl;
    }

    public void setThumbFileUrl(String thumbFileUrl) {
        this.thumbFileUrl = thumbFileUrl;
    }

    public long getThumbFileSize() {
        return thumbFileSize;
    }

    public void setThumbFileSize(long thumbFileSize) {
        this.thumbFileSize = thumbFileSize;
    }

    public long getThumbFileTranslateSize() {
        return thumbFileTranslateSize;
    }

    public void setThumbFileTranslateSize(long thumbFileTranslateSize) {
        this.thumbFileTranslateSize = thumbFileTranslateSize;
    }

    @Override
    public String toString() {
        return "IMFileExtraInfo{" +
                "msgId=" + msgId +
                ", rawFileName='" + rawFileName + '\'' +
                ", rawFileUrl='" + rawFileUrl + '\'' +
                ", rawFileSize=" + rawFileSize +
                ", rawFileTranslateSize=" + rawFileTranslateSize +
                ", thumbFileName='" + thumbFileName + '\'' +
                ", thumbFileUrl='" + thumbFileUrl + '\'' +
                ", thumbFileSize=" + thumbFileSize +
                ", thumbFileTranslateSize=" + thumbFileTranslateSize +
                '}';
    }
}
