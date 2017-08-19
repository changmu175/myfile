package com.xdja.contact.bean;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.xdja.contact.convert.GroupConvert;
import com.xdja.contact.database.columns.TableGroupMember;

/**
 * Created by XDJA_XA on 2015/7/9.
 */
public class GroupMember implements BaseContact {

    public static final String ADD = "1";

    public static final String MODIFY = "2";

    public static final String DELETE = "3";

    /**
     * 主键、自增、唯一
     */
    private String id;

    private String groupId;
    /**
     * 安通账号
     */
    private String account;
    /**
     * 群组中的昵称
     */
    private String nickName;
    /**
     * 入群时间
     */
    private String createTime;
    /**
     * 昵称首字母
     */
    private String nickNamePY;
    /**
     * 昵称全拼
     */
    private String nickNameFullPY;
    /**
     * 批量更新标示
     */
    private String updateSerial;
    /**
     * 是否已被移除出群组
     */
    private String isDeleted;
    /**
     * 邀请人账号
     */
    private String inviteAccount;

    private ActomaAccount actomaAccount;

    private Avatar avatar;


    //标示服务端返回数据的要对当前信息的操作
    /**
     *@see Group#status
     */
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public GroupMember(){}

    public GroupMember(boolean isNormal){
        setIsDeleted(isNormal ? GroupConvert.UN_DELETED : GroupConvert.DELETED);
    }

    public GroupMember(GroupMember m) {
        if (m != null) {
            setId(m.getId());
            setGroupId(m.getGroupId());
            setAccount(m.getAccount());
            setNickName(m.getNickName());
            setNickNamePY(m.getNickNamePY());
            setNickNameFullPY(m.getNickNameFullPY());
            setCreateTime(m.getCreateTime());
            setUpdateSerial(m.getUpdateSerial());
            setIsDeleted(m.getIsDeleted());
            setInviteAccount(m.getInviteAccount());
        }
    }

    public GroupMember(Cursor cursor){
        setId(cursor.getString(cursor.getColumnIndex(TableGroupMember.ID)));
        setGroupId(cursor.getString(cursor.getColumnIndex(TableGroupMember.GROUP_ID)));
        setAccount(cursor.getString(cursor.getColumnIndex(TableGroupMember.MEMBER_ACCOUNT)));
        setNickName(cursor.getString(cursor.getColumnIndex(TableGroupMember.MEMBER_NICKNAME)));
        setNickNamePY(cursor.getString(cursor.getColumnIndex(TableGroupMember.NICKNAME_PY)));
        setNickNameFullPY(cursor.getString(cursor.getColumnIndex(TableGroupMember.NICKNAME_FULL_PY)));
        setCreateTime(cursor.getString(cursor.getColumnIndex(TableGroupMember.SERVER_CREATE_TIME)));
        setUpdateSerial(cursor.getString(cursor.getColumnIndex(TableGroupMember.UPDATE_SERIAL)));
        setIsDeleted(cursor.getString(cursor.getColumnIndex(TableGroupMember.IS_DELETED)));
        setInviteAccount(cursor.getString(cursor.getColumnIndex(TableGroupMember.INVITE_ACOCUNT)));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String time) {
        this.createTime = time;
    }

    public String getNickNamePY() {
        return nickNamePY;
    }

    public void setNickNamePY(String nickNamePY) {
        this.nickNamePY = nickNamePY;
    }

    public String getNickNameFullPY() {
        return nickNameFullPY;
    }

    public void setNickNameFullPY(String nickNameFullPY) {
        this.nickNameFullPY = nickNameFullPY;
    }

    public String getUpdateSerial() {
        return updateSerial;
    }

    public void setUpdateSerial(String updateSerial) {
        this.updateSerial = updateSerial;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getInviteAccount() {
        return inviteAccount;
    }

    public void setInviteAccount(String inviteAccount) {
        this.inviteAccount = inviteAccount;
    }

    public ActomaAccount getActomaAccount() {
        return actomaAccount;
    }

    public void setActomaAccount(ActomaAccount actomaAccount) {
        this.actomaAccount = actomaAccount;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    /**
     * 获取群组成员的显示名称
     * 对于非当前用户： 群组中的昵称 > 安通账号昵称> 集团通讯录昵称 > 安通账号
     * 对于当前用户：群组中的昵称 > 账号昵称 > 安通账号
     * @return 显示名称
     */
    public String getDisplayName() {
        String showName;
        //对于当前用户，不含获取ActomaAccount，所以不会进入
        if (!TextUtils.isEmpty(getNickName())) {
            return getNickName();
        }
        if (getActomaAccount() != null) {
            showName = getActomaAccount().getNickname();
            if (!TextUtils.isEmpty(showName)) {
                return  showName;
            }
        }
        return account;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        //setContentValues(values, false);
        values.put(TableGroupMember.GROUP_ID, getGroupId());
        values.put(TableGroupMember.MEMBER_ACCOUNT, getAccount());
        values.put(TableGroupMember.MEMBER_NICKNAME, getNickName());
        values.put(TableGroupMember.NICKNAME_PY, getNickNamePY());
        values.put(TableGroupMember.NICKNAME_FULL_PY, getNickNameFullPY());
        values.put(TableGroupMember.UPDATE_SERIAL, getUpdateSerial());
        values.put(TableGroupMember.SERVER_CREATE_TIME, getCreateTime());
        values.put(TableGroupMember.IS_DELETED, getIsDeleted());
        values.put(TableGroupMember.INVITE_ACOCUNT, getInviteAccount());
        return values;
    }

    /*@Override
    public void setContentValues(ContentValues values, boolean isNeedId) {
        if(isNeedId){
            values.put(TableGroupMember.ID, getNotificationId());
        }
        values.put(TableGroupMember.GROUP_ID, getGroupId());
        values.put(TableGroupMember.MEMBER_ACCOUNT, getAccount());
        values.put(TableGroupMember.MEMBER_NICKNAME, getNickName());
        values.put(TableGroupMember.NICKNAME_PY, getNickNamePY());
        values.put(TableGroupMember.NICKNAME_FULL_PY, getNickNameFullPY());
        values.put(TableGroupMember.UPDATE_SERIAL, getUpdateSerial());
        values.put(TableGroupMember.SERVER_CREATE_TIME, getCreateTime());
        values.put(TableGroupMember.IS_DELETED, getIsDeleted());
        values.put(TableGroupMember.INVITE_ACOCUNT, getInviteAccount());
    }*/
}
