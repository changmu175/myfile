package com.xdja.imp.domain.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 图片文件信息
 * Created by xdjaxa on 2016/6/23.
 */
public class ImageFileInfo extends FileInfo implements Parcelable{

    /**
     * 是否发送原图
     */
    private boolean bOriginal;

    /**
     * 下载图片的类型(缩略，高清，原图)
     * */
    private int type;

    private FileExtraInfo extraInfo;

    public ImageFileInfo() {
    }

    public boolean isOriginal() {
        return bOriginal;
    }

    public void setOriginal(boolean bOriginal) {
        this.bOriginal = bOriginal;
    }

    public FileExtraInfo getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(FileExtraInfo extraInfo) {
        this.extraInfo = extraInfo;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ImageFileInfo{" +
                "bOriginal=" + bOriginal +
                ", extraInfo=" + extraInfo +
                '}';
    }

    protected ImageFileInfo(Parcel in) {
        super(in);
        bOriginal = in.readInt() != 0;
        type = in.readInt();
        extraInfo = in.readParcelable(FileExtraInfo.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(bOriginal ? 1 : 0);
        out.writeInt(type);
        out.writeParcelable(extraInfo, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageFileInfo> CREATOR = new Creator<ImageFileInfo>() {
        @Override
        public ImageFileInfo createFromParcel(Parcel in) {
            return new ImageFileInfo(in);
        }

        @Override
        public ImageFileInfo[] newArray(int size) {
            return new ImageFileInfo[size];
        }
    };
}
