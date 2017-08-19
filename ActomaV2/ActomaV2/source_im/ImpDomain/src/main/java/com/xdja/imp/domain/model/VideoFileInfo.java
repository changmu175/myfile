package com.xdja.imp.domain.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 项目名称：短视频             <br>
 * 类描述  ：短视频上层封装实体类     <br>
 * 创建时间：2016/12/16      <br>
 * 修改记录：                 <br>
 *
 * @author jyg@xdja.com   <br>
 */

public class VideoFileInfo extends FileInfo implements Parcelable {

    /**
     * 小视频时长
     */
    private int amountOfTime;

    /**
     * 小视频第一帧
     */
    private String firstFramePath;
    /**
     * 小视频大小
     */
    private long videoSize;
    /**
     * 类型：缩略图，原视频文件
     */
    private int type;
    /**
     * 详细短视频信息
     */
    private FileExtraInfo extraInfo;

    public VideoFileInfo(){}

    private VideoFileInfo(Parcel in) {
        super(in);
        amountOfTime = in.readInt();
        firstFramePath = in.readString();
        videoSize = in.readLong();
        type = in.readInt();
        extraInfo = in.readParcelable(FileExtraInfo.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(amountOfTime);
        dest.writeString(firstFramePath);
        dest.writeLong(videoSize);
        dest.writeInt(type);
        dest.writeParcelable(extraInfo, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VideoFileInfo> CREATOR = new Creator<VideoFileInfo>() {
        @Override
        public VideoFileInfo createFromParcel(Parcel in) {

            return new VideoFileInfo(in);
        }

        @Override
        public VideoFileInfo[] newArray(int size) {
            return new VideoFileInfo[size];
        }
    };

    public int getAmountOfTime() {
        return amountOfTime;
    }

    public void setAmountOfTime(int amountOfTime) {
        this.amountOfTime = amountOfTime;
    }

    public String getThumbPath() {
        return firstFramePath;
    }

    public void setThumbPath(String firstFramePath) {
        this.firstFramePath = firstFramePath;
    }

    public long getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(long videoSize) {
        this.videoSize = videoSize;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public FileExtraInfo getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(FileExtraInfo extraInfo) {
        this.extraInfo = extraInfo;
    }

    @Override
    public String toString() {
        return "VideoFileInfo{" +
                "_id=" + get_id() +
                ", isOpened=" + isOpend() +
                ", fileURL='" + getFilePath() + '\'' +
                ", fileName='" + getFileName() + '\'' +
                ", fileSize=" + getFileSize() +
                ", downloadedSize=" + getTranslateSize() +
                ", suffix='" + getSuffix() + '\'' +
                ", fileType=" + getFileType() +
                ", talkListTag = " + getTalkListTag() +
                ", talkMessageId = " + getTalkMessageId() +
                ", amountOfTime = " + amountOfTime +
                '}';
    }
}
