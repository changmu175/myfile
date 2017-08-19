package com.xdja.imp.domain.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.Serializable;

/**
 * 文件信息描述类
 * Modify history description:
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 */
public class FileInfo implements Parcelable, Serializable{
    /**
     * 文件id
     */
    private long _id;
    /**
     * 文件是否被查看过
     */
    private boolean isOpend = false;
    /**
     * 文件路径
     */
    private String filePath;
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 文件总大小
     */
    private long fileSize;
    /**
     * 文件已传输大小
     */
    private long translateSize;
    /**
     * 文件后缀
     */
    private String suffix;
    /**
     * 文件类型
     */
    private
    @ConstDef.FileType
    int fileType;

    /**
     * 文件对应的会话列表的标签
     */
    private String talkListTag;

    /**
     * 文件对应的消息的id
     */
    private long talkMessageId;

    private int percent;

    private
    @ConstDef.FileState
    int fileState;

    public FileInfo() {

    }


    public FileInfo(FileInfo fInfo){
        _id = fInfo.get_id();
        isOpend = fInfo.isOpend();
        filePath = fInfo.getFilePath();
        fileName = fInfo.getFileName();
        fileSize = fInfo.getFileSize();
        translateSize = fInfo.getTranslateSize();
        suffix = fInfo.getSuffix();
        fileType = fInfo.getFileType();
        talkListTag = fInfo.getTalkListTag();
        talkMessageId = fInfo.getTalkMessageId();
        percent = fInfo.getPercent();
        fileState = fInfo.getFileState();
    }

    protected FileInfo(Parcel in) {
        _id = in.readLong();
        isOpend = (int) in.readByte() != 0;
        filePath = in.readString();
        fileName = in.readString();
        fileSize = in.readLong();
        translateSize = in.readLong();
        suffix = in.readString();
        fileType = in.readInt();
        talkListTag = in.readString();
        talkMessageId = in.readLong();
        percent = in.readInt();
        fileState = in.readInt();
    }

    public static final Creator<FileInfo> CREATOR = new Creator<FileInfo>() {
        @Override
        public FileInfo createFromParcel(Parcel in) {
            return new FileInfo(in);
        }

        @Override
        public FileInfo[] newArray(int size) {
            return new FileInfo[size];
        }
    };

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public boolean isOpend() {
        return isOpend;
    }

    public void setOpend(boolean opend) {
        isOpend = opend;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        if (TextUtils.isEmpty(filePath)) {
            return "";
        }
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getTranslateSize() {
        return translateSize;
    }

    public void setTranslateSize(long translateSize) {
        this.translateSize = translateSize;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    @ConstDef.FileType
    private static int getFileType(int fileType){
        return fileType;
    }

    public String getTalkListTag() {
        return talkListTag;
    }

    public void setTalkListTag(String talkListTag) {
        this.talkListTag = talkListTag;
    }

    public long getTalkMessageId() {
        return talkMessageId;
    }

    public void setTalkMessageId(long talkMessageId) {
        this.talkMessageId = talkMessageId;
    }


    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public int getFileState() {
        return fileState;
    }

    public void setFileState(int fileState) {
        this.fileState = fileState;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "_id=" + _id +
                ", isOpend=" + isOpend +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileSize=" + fileSize +
                ", translateSize=" + translateSize +
                ", suffix='" + suffix + '\'' +
                ", fileType=" + fileType +
                ", talkListTag = " + talkListTag +
                ", talkMessageId = " + talkMessageId +
                ", percent = " + percent +
                ", fileState = " + fileState +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(_id);
        dest.writeByte((byte) (isOpend ? 1 : 0));
        dest.writeString(filePath);
        dest.writeString(fileName);
        dest.writeLong(fileSize);
        dest.writeLong(translateSize);
        dest.writeString(suffix);
        dest.writeInt(fileType);
        dest.writeString(talkListTag);
        dest.writeLong(talkMessageId);
        dest.writeInt(percent);
        dest.writeInt(fileState);
    }
}
