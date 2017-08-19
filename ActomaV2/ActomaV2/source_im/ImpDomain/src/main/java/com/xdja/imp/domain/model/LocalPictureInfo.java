package com.xdja.imp.domain.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 本地图片信息
 * Created by leill on 2016/6/17.
 */
public class LocalPictureInfo implements Parcelable {

    /**
     * 图片名称
     */
    private String picName;
    /**
     * 图片路径
     */
    private String localPath;
    /**
     * 图片大小
     */
    private long fileSize;
    /**
     * 图片状态
     */
    private int statue = Statue.STATUE_UNCHECKED;
    /**
     * 图片原图
     */
    private boolean bOriginalPic;
    /**
     * 图片类型
     */
    private int type;

    /**
     * 图片状态
     */
    public static class Statue {
        public static final int STATUE_UNCHECKED = 0; //图片未选中状态
        public static final int STATUE_SELECTED = 1; //图片选中状态
        public static final int STATUE_DELETE = 2; //图片删除
        public static final int STATUE_DESTROY = 3; //图片销毁
    }

    public LocalPictureInfo() {

    }

    public LocalPictureInfo(String picName, String localPath, long fileSize) {
        this.picName = picName;
        this.localPath = localPath;
        this.fileSize = fileSize;
        this.statue = Statue.STATUE_UNCHECKED;
    }

    public LocalPictureInfo(String picName, String localPath, long fileSize, int statue) {
        this.picName = picName;
        this.localPath = localPath;
        this.fileSize = fileSize;
        this.statue = statue;
    }

    protected LocalPictureInfo(Parcel in) {
        picName = in.readString();
        localPath = in.readString();
        fileSize = in.readLong();
        statue = in.readInt();
        bOriginalPic = in.readByte() != 0;
        type = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(picName);
        dest.writeString(localPath);
        dest.writeLong(fileSize);
        dest.writeInt(statue);
        dest.writeByte((byte) (bOriginalPic ? 1 : 0));
        dest.writeInt(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LocalPictureInfo> CREATOR = new Creator<LocalPictureInfo>() {
        @Override
        public LocalPictureInfo createFromParcel(Parcel in) {
            return new LocalPictureInfo(in);
        }

        @Override
        public LocalPictureInfo[] newArray(int size) {
            return new LocalPictureInfo[size];
        }
    };

    public String getPicName() {
        return picName;
    }

    public void setPicName(String picName) {
        this.picName = picName;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public int getStatue() {
        return statue;
    }

    public void setStatue(int statue) {
        this.statue = statue;
    }

    public boolean isOriginalPic() {
        return bOriginalPic;
    }

    public void setOriginalPic(boolean bOriginalPic) {
        this.bOriginalPic = bOriginalPic;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "LocalPictureInfo{" +
                "picName='" + picName + '\'' +
                ", localPath='" + localPath + '\'' +
                ", fileSize=" + fileSize +
                ", statue=" + statue +
                ", bOriginalPic=" + bOriginalPic +
                '}';
    }
}
