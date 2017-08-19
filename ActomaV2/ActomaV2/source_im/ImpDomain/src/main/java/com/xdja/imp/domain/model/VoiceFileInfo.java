package com.xdja.imp.domain.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/4/9.
 * 功能描述
 */
public class VoiceFileInfo extends FileInfo implements Parcelable{


    private int amountOfTime;

    public VoiceFileInfo(){

    }

    protected VoiceFileInfo(Parcel in) {
        super();
        amountOfTime = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(amountOfTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VoiceFileInfo> CREATOR = new Creator<VoiceFileInfo>() {
        @Override
        public VoiceFileInfo createFromParcel(Parcel in) {
            return new VoiceFileInfo(in);
        }

        @Override
        public VoiceFileInfo[] newArray(int size) {
            return new VoiceFileInfo[size];
        }
    };

    public int getAmountOfTime() {
        return amountOfTime;
    }

    public void setAmountOfTime(int amountOfTime) {
        this.amountOfTime = amountOfTime;
    }


    @Override
    public String toString() {
        super.toString();
        return "VoiceFileInfo{" +
                "_id=" + get_id() +
                ", isOpend=" + isOpend() +
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
