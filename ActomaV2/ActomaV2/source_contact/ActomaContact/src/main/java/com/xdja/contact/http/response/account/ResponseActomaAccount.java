package com.xdja.contact.http.response.account;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.xdja.contact.bean.ActomaAccount;
import com.xdja.contact.bean.Avatar;

/**
 * 服务器返回安通账户信息Bean
 *
 * @author hkb.
 * @since 2015/8/9/0009.
 */
public class ResponseActomaAccount extends ActomaAccount implements Parcelable {


    //原图摘要
    private String avatarId;
    //缩略图摘要
    private String thumbnailId;

    public Avatar getAvatarBean() {
        Avatar avatar = new Avatar();
        avatar.setAccount(getAccount());
        avatar.setThumbnail(getThumbnailId());
        avatar.setAvatar(getAvatarId());
        return avatar;
    }



    @Override
    public ContentValues getContentValues() {
        return super.getContentValues();
    }


    public String getThumbnailId() {
        return thumbnailId;
    }

    public void setThumbnailId(String thumbnailId) {
        this.thumbnailId = thumbnailId;
    }


    public String getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(String avatarId) {
        this.avatarId = avatarId;
    }

    public ResponseActomaAccount() {

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.thumbnailId);
        dest.writeString(this.avatarId);
    }

    protected ResponseActomaAccount(Parcel in) {
        super(in);
        this.thumbnailId = in.readString();
        this.avatarId = in.readString();
    }

    public static final Creator<ResponseActomaAccount> CREATOR = new Creator<ResponseActomaAccount>() {
        public ResponseActomaAccount createFromParcel(Parcel source) {
            return new ResponseActomaAccount(source);
        }

        public ResponseActomaAccount[] newArray(int size) {
            return new ResponseActomaAccount[size];
        }
    };
}
