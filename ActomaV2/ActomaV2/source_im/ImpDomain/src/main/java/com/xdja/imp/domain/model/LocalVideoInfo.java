package com.xdja.imp.domain.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 项目名称：短视频             <br>
 * 类描述  ：本地短视频信息     <br>
 * 创建时间：2017/2/18        <br>
 * 修改记录：                 <br>
 *
 * @author jyg@xdja.com   <br>
 */
public class LocalVideoInfo implements Parcelable{

    /** 文件名称*/
    private String fileName;
    /** 文件路径*/
    private String filePath;
    /** 文件大小*/
    private long fileSize;
    /** 文件类型**/
    private int fileType;
    /** 文件最后修改日期**/
    private long modifiedDate;
    /** 扩展信息，可为空*/
    private String extraInfo;
    /** 文件状态*/
    private boolean isSelected;
    /** 原文件名称*/
    private String rawFileName;
    /** 原文件路径*/
    private String rawFilePath;
    /** 原文件大小*/
    private long rawFileSize;
    /** 短视频时长*/
    private int duration;

    public LocalVideoInfo() {
    }

    public LocalVideoInfo(String fileName, String filePath, long fileSize, long modifiedDate, int fileType) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.modifiedDate = modifiedDate;
        this.fileType = fileType;
    }

    private LocalVideoInfo(Parcel in) {
        fileName = in.readString();
        filePath = in.readString();
        fileSize = in.readLong();
        fileType = in.readInt();
        modifiedDate = in.readLong();
        extraInfo = in.readString();
        isSelected = in.readByte() != 0;
    }

    public static final Creator<LocalVideoInfo> CREATOR = new Creator<LocalVideoInfo>() {
        @Override
        public LocalVideoInfo createFromParcel(Parcel in) {
            return new LocalVideoInfo(in);
        }

        @Override
        public LocalVideoInfo[] newArray(int size) {
            return new LocalVideoInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fileName);
        dest.writeString(filePath);
        dest.writeLong(fileSize);
        dest.writeInt(fileType);
        dest.writeLong(modifiedDate);
        dest.writeString(extraInfo);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
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

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public long getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(long modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
    public String getRawFileName() {
        return rawFileName;
    }

    public void setRawFileName(String rawFileName) {
        this.rawFileName = rawFileName;
    }

    public String getRawFilePath() {
        return rawFilePath;
    }

    public void setRawFilePath(String rawFilePath) {
        this.rawFilePath = rawFilePath;
    }

    public long getRawFileSize() {
        return rawFileSize;
    }

    public void setRawFileSize(long rawFileSize) {
        this.rawFileSize = rawFileSize;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
    @Override
    public String toString() {
        return "LocalVideoInfo{" +
                "fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileSize=" + fileSize +
                ", fileType=" + fileType +
                ", modifiedDate=" + modifiedDate +
                ", extraInfo='" + extraInfo + '\'' +
                ", isSelected=" + isSelected +
                ", rawFileName=" + rawFileName +
                ", rawFilePath=" + rawFilePath +
                ", rawFilePath=" + rawFilePath +
                ", rawFileSize=" + rawFileSize +
                ", duration=" + duration +
                '}';
    }
}
