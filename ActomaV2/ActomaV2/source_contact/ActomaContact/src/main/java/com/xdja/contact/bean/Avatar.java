package com.xdja.contact.bean;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.xdja.contact.database.columns.TableAccountAvatar;

/**
 * Created by wanghao on 2015/7/23.
 */
public class Avatar implements BaseContact, Parcelable {
    /**
     * 关联的账号
     */
    private String account;
    /**
     * 缩略图文件名
     */
    private String thumbnail;
    /**
     * 头像文件名
     */
    private String avatar;

    public Avatar() {

    }


    public Avatar(Cursor cursor) {
        account = getStringFromCursor(cursor, TableAccountAvatar.ACCOUNT);
        thumbnail = getStringFromCursor(cursor, TableAccountAvatar.THUMBNAIL);
        avatar = getStringFromCursor(cursor, TableAccountAvatar.AVATAR);
    }

    private String getStringFromCursor(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndexOrThrow(columnName));

    }


    @Override
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TableAccountAvatar.ACCOUNT, getAccount());
        contentValues.put(TableAccountAvatar.AVATAR, getAvatar());
        contentValues.put(TableAccountAvatar.THUMBNAIL, getThumbnail());
        return contentValues;
    }


    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.account);
        dest.writeString(this.thumbnail);
        dest.writeString(this.avatar);
    }


    protected Avatar(Parcel in) {
        this.account = in.readString();
        this.thumbnail = in.readString();
        this.avatar = in.readString();
    }


    public static final Parcelable.Creator<Avatar> CREATOR = new Parcelable.Creator<Avatar>() {
        public Avatar createFromParcel(Parcel source) {
            return new Avatar(source);
        }

        public Avatar[] newArray(int size) {
            return new Avatar[size];
        }
    };
}
