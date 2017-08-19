package com.xdja.contact.bean.dto;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.xdja.contact.database.columns.TableAccountAvatar;
import com.xdja.contact.database.columns.TableActomaAccount;
import com.xdja.contact.database.columns.TableDepartmentMember;
import com.xdja.contact.database.columns.TableFriend;
import com.xdja.contact.database.columns.TableGroupMember;
import com.xdja.comm.uitl.ObjectUtil;

/**
 * Created by wanghao on 2015/12/18.
 * 联系人显示名称临时对象
 * 群组名称显示规则 : remark > groupNickName > name > nickName > account
 * 群内成员显示规则 : remark > groupNickName > name > nickName > account
 * 好友列表显示规则 : remark > name > nickName > account
 *
 */
public class ContactNameDto implements Parcelable {

    /**
     * 大头像url
     */
    private String avatarUrl;
    /**
     * 小头像url
     */
    private String thumbnailUrl;

    private String account;

    private String nickName;

    private String name;

    private String remark;
    //add alias
    private String alias;

    private String groupMemberNickName;

    public ContactNameDto(){}


    public ContactNameDto(String groupId,Cursor cursor){
        setRemark(cursor.getString(cursor.getColumnIndex(TableFriend.REMARK)));
        setName(cursor.getString(cursor.getColumnIndex(TableDepartmentMember.NAME)));
        setNickName(cursor.getString(cursor.getColumnIndex(TableActomaAccount.NICKNAME)));
        setThumbnailUrl(cursor.getString(cursor.getColumnIndex(TableAccountAvatar.THUMBNAIL)));
        setAvatarUrl(cursor.getString(cursor.getColumnIndex(TableAccountAvatar.AVATAR)));
        setAccount(cursor.getString(cursor.getColumnIndex(TableActomaAccount.ACCOUNT)));
        //start:add by wal@xdja.com for 2575
        if(!ObjectUtil.stringIsEmpty(groupId)){
            setGroupMemberNickName(cursor.getString(cursor.getColumnIndex(TableGroupMember.MEMBER_NICKNAME)));
        }
        //end:add by wal@xdja.com for 2575
        setAlias(cursor.getString(cursor.getColumnIndex(TableActomaAccount.ALIAS)));
    }

    public String getDisplayName(){

        if(!ObjectUtil.stringIsEmpty(remark))
            return remark;
        if(!ObjectUtil.stringIsEmpty(groupMemberNickName))
            return groupMemberNickName;
        if(!ObjectUtil.stringIsEmpty(name))
            return name;
        if(!ObjectUtil.stringIsEmpty(nickName))
            return nickName;
        if(!ObjectUtil.stringIsEmpty(alias))
            return alias;
        if(!ObjectUtil.stringIsEmpty(account))
            return account;
        return account;
    }
    //add by lwl start
    public String getDisplayNameContact(){

        if(!ObjectUtil.stringIsEmpty(remark))
            return remark;
//        if(!ObjectUtil.stringIsEmpty(groupMemberNickName))
//            return groupMemberNickName;
        if(!ObjectUtil.stringIsEmpty(name))
            return name;
        if(!ObjectUtil.stringIsEmpty(nickName))
            return nickName;
        if(!ObjectUtil.stringIsEmpty(alias))
            return alias;
        if(!ObjectUtil.stringIsEmpty(account))
            return account;
        return account;
    }
    //add by lwl end

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setGroupMemberNickName(String groupMemberNickName) {
        this.groupMemberNickName = groupMemberNickName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public static Creator<ContactNameDto> getCREATOR() {
        return CREATOR;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(account);
        dest.writeString(nickName);
        dest.writeString(name);
        dest.writeString(remark);
        dest.writeString(groupMemberNickName);
        dest.writeString(alias);
        dest.writeString(avatarUrl);
        dest.writeString(thumbnailUrl);

    }

    public ContactNameDto(Parcel source){
        this.account = source.readString();
        this.nickName = source.readString();
        this.name = source.readString();
        this.remark = source.readString();
        this.groupMemberNickName= source.readString();
        this.alias= source.readString();
        this.avatarUrl=source.readString();
        this.thumbnailUrl=source.readString();
    }

    public static final Creator<ContactNameDto> CREATOR = new Creator<ContactNameDto>() {
        public ContactNameDto createFromParcel(Parcel source) {
            return new ContactNameDto(source);
        }

        public ContactNameDto[] newArray(int size) {
            return new ContactNameDto[size];
        }
    };
}
