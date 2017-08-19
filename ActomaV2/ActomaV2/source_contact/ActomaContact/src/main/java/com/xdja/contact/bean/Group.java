package com.xdja.contact.bean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.xdja.contact.convert.GroupConvert;
import com.xdja.contact.database.columns.TableGroup;
import com.xdja.contact.util.GroupUtils;
import com.xdja.comm.uitl.ObjectUtil;

/**
 * Created by XDJA_XA on 2015/7/8.
 * Modify history description:
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 */
public class Group implements BaseContact, Parcelable {

    public static final String ADD = "1";

    public static final String MODIFY = "2";

    public static final String DELETE = "3";

    /**
     * 主键、自增、唯一
     */
    private String id;
    /**
     * groupId
     */
    private String groupId;
    /**
     * 群组名
     */
    private String groupName;
    /**
     * 头像URL
     */
    private String avatar;
    /**
     * 头像原图URL
     */
    private String thumbnail;
    /**
     * 原图摘要
     */
    private String avatarHash;
    /**
     * 缩略图摘要
     */
    private String thumbnailHash;
    /**
     * 群主账号
     */
    private String groupOwner;
    /**
     * 群组创建时间
     */
    private String createTime;

    /**
     * 群组名首字母
     */
    private String namePY;
    /**
     * 群组名全拼
     */
    private String nameFullPY;
    /**
     * 增量更新标示
     */
    private String updateSerial;
    /**
     * 默认群组名称，如果群组未命名，显示此名称
     */
    //private String defaultGroupName;

    /**
     * 是否显示在群组列表里面
     */
    protected String isDeleted;

    //标示群组信息要执行的动作 -》状态 1：新增加、2：修改；3 删除
    /**
     *@see Group#ADD, Group#MODIFY, Group#DELETE
     */
    private String status;

    // Task 2632 [Begin]
    private String indexChar; //快速索引标示数据库不需要使用此字段
    private int viewType;//联系人列表显示索引和正常显示item专用
    public static final int ALPHA = 1;

    public String getIndexChar() {
        return indexChar;
    }

    public void setIndexChar(String indexChar) {
        this.indexChar = indexChar;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
    public String getStatus() {
        return status;
    }
    // Task 2632 [End]

    public void setStatus(String status) {
        this.status = status;
    }

    public Group(){}

    public Group(boolean isNormal) {
        setIsDeleted(isNormal ? GroupConvert.UN_DELETED : GroupConvert.DELETED);
    }

    public Group(Cursor cursor){
        setId(cursor.getString(cursor.getColumnIndex(TableGroup.ID)));
        setGroupId(cursor.getString(cursor.getColumnIndex(TableGroup.GROUP_ID)));
        setGroupName(cursor.getString(cursor.getColumnIndex(TableGroup.GROUP_NAME)));
        setNameFullPY(cursor.getString(cursor.getColumnIndex(TableGroup.GROUP_NAME_FULL_PY)));
        setNamePY(cursor.getString(cursor.getColumnIndex(TableGroup.GROUP_NAME_PY)));
        setGroupOwner(cursor.getString(cursor.getColumnIndex(TableGroup.OWNER)));
        setCreateTime(cursor.getString(cursor.getColumnIndex(TableGroup.SERVER_CREATE_TIME)));
        setAvatar(cursor.getString(cursor.getColumnIndex(TableGroup.AVATAR)));
        setAvatarHash(cursor.getString(cursor.getColumnIndex(TableGroup.AVATAR_HASH)));
        setThumbnail(cursor.getString(cursor.getColumnIndex(TableGroup.THUMBNAIL)));
        setThumbnailHash(cursor.getString(cursor.getColumnIndex(TableGroup.THUMBNAIL_HASH)));
        setUpdateSerial(cursor.getString(cursor.getColumnIndex(TableGroup.UPDATE_SERIAL)));
        setIsDeleted(cursor.getString(cursor.getColumnIndex(TableGroup.IS_DELETED)));
    }



    public String getComparatorColumn(){
        if(!ObjectUtil.stringIsEmpty(nameFullPY)){
            return nameFullPY;
        }
        return "";
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getNamePY() {
        return namePY;
    }

    public void setNamePY(String namePY) {
        this.namePY = namePY;
    }

    public String getNameFullPY() {
        return nameFullPY;
    }

    public void setNameFullPY(String nameFullPY) {
        this.nameFullPY = nameFullPY;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatarHash() {
        return avatarHash;
    }

    public void setAvatarHash(String avatarHash) {
        this.avatarHash = avatarHash;
    }

    public String getThumbnailHash() {
        return thumbnailHash;
    }

    public void setThumbnailHash(String thumbnailHash) {
        this.thumbnailHash = thumbnailHash;
    }

    public String getUpdateSerial() {
        return updateSerial;
    }

    public void setUpdateSerial(String updateSerial) {
        this.updateSerial = updateSerial;
    }

    public String getGroupOwner() {
        return groupOwner;
    }

    public void setGroupOwner(String groupOwner) {
        this.groupOwner = groupOwner;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    // Task 2632 [Begin]
    private boolean isChecked;
    public Boolean getIsChecked() {
        return isChecked;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(Boolean isChecked) {
        this.isChecked = isChecked;
    }
	// Task 2632 [End]

    /**
     * 返回群组的显示名称（如已命名，显示群组名字，否则显示群组成员昵称组合而成的名字）
     * @return
     */
    public String getDisplayName(final Context context) {
        if (!ObjectUtil.stringIsEmpty(getGroupName())) {
            return getGroupName();
        }else{
            return GroupUtils.genDefaultGroupName(context, getGroupId());
        }
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(TableGroup.GROUP_ID, getGroupId());
        values.put(TableGroup.GROUP_NAME, getGroupName());
        values.put(TableGroup.AVATAR, getAvatar());
        values.put(TableGroup.THUMBNAIL, getThumbnail());
        values.put(TableGroup.AVATAR_HASH, getAvatarHash());
        values.put(TableGroup.THUMBNAIL_HASH, getThumbnailHash());
        values.put(TableGroup.OWNER, getGroupOwner());
        values.put(TableGroup.GROUP_NAME_PY, getNamePY());
        values.put(TableGroup.GROUP_NAME_FULL_PY, getNameFullPY());
        values.put(TableGroup.UPDATE_SERIAL, getUpdateSerial());
        values.put(TableGroup.SERVER_CREATE_TIME, getCreateTime());
        values.put(TableGroup.IS_DELETED, getIsDeleted());
        return values;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.groupId);
        dest.writeString(this.groupName);
        dest.writeString(this.avatar);
        dest.writeString(this.thumbnail);
        dest.writeString(this.avatarHash);
        dest.writeString(this.thumbnailHash);
        dest.writeString(this.groupOwner);
        dest.writeString(this.createTime);
        dest.writeString(this.namePY);
        dest.writeString(this.nameFullPY);
        dest.writeString(this.updateSerial);
        //dest.writeString(this.defaultGroupName);
        dest.writeString(this.isDeleted);
    }

    protected Group(Parcel in) {
        this.id = in.readString();
        this.groupId = in.readString();
        this.groupName = in.readString();
        this.avatar = in.readString();
        this.thumbnail = in.readString();
        this.avatarHash = in.readString();
        this.thumbnailHash = in.readString();
        this.groupOwner = in.readString();
        this.createTime = in.readString();
        this.namePY = in.readString();
        this.nameFullPY = in.readString();
        this.updateSerial = in.readString();
        //this.defaultGroupName = in.readString();
        this.isDeleted = in.readString();
    }

    public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {
        public Group createFromParcel(Parcel source) {
            return new Group(source);
        }

        public Group[] newArray(int size) {
            return new Group[size];
        }
    };
}
