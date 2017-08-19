package com.xdja.imp.domain.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <p>Author: xdjaxa</p>
 * <p>Date: 2016/11/29 11:47</p>
 * <p>Package: com.xdja.imp.domain.model</p>
 * <p>Description: 本地文件信息</p>
 */
public class LocalFileInfo implements Parcelable{

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

    public LocalFileInfo() {
    }

    public LocalFileInfo(String fileName, String filePath, long fileSize, long modifiedDate, int fileType) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.modifiedDate = modifiedDate;
        this.fileType = fileType;
    }

    protected LocalFileInfo(Parcel in) {
        fileName = in.readString();
        filePath = in.readString();
        fileSize = in.readLong();
        fileType = in.readInt();
        modifiedDate = in.readLong();
        extraInfo = in.readString();
        isSelected = (int) in.readByte() != 0;
    }

    public static final Creator<LocalFileInfo> CREATOR = new Creator<LocalFileInfo>() {
        @Override
        public LocalFileInfo createFromParcel(Parcel in) {
            return new LocalFileInfo(in);
        }

        @Override
        public LocalFileInfo[] newArray(int size) {
            return new LocalFileInfo[size];
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

    @Override
    public String toString() {
        return "LocalFileInfo{" +
                "fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileSize=" + fileSize +
                ", fileType=" + fileType +
                ", modifiedDate=" + modifiedDate +
                ", extraInfo='" + extraInfo + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }
}
