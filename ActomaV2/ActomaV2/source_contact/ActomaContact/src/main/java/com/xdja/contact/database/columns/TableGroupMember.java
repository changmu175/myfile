package com.xdja.contact.database.columns;

/**
 * Created by wanghao on 2015/7/7.
 */
public class TableGroupMember extends AbsTableOperate {

    /**
     * 群组成员表名
     */
    public static final String TABLE_NAME = "t_group_members";

    /**
     * 自增id
     */
    public static final String ID = "_id";
    /**
     * 群组id
     */
    public static final String GROUP_ID = "c_group_id";
    /**
     * 群成员账号
     */
    public static final String MEMBER_ACCOUNT = "c_member_account";
    /**
     * 群成员昵称
     */
    public static final String MEMBER_NICKNAME = "c_member_nickname";
    /**
     * 群成员昵称首字母
     */
    public static final String NICKNAME_PY = "c_nickname_py";
    /**
     * 群成员昵称全拼
     */
    public static final String NICKNAME_FULL_PY = "c_nickname_full_py";
    /**
     * 创建时间
     */
    public static final String SERVER_CREATE_TIME = "c_server_create_time";
    /**
     * 是否已删除
     */
    public static final String IS_DELETED = "c_is_deleted";
    /**
     * 增量标示
     */
    public static final String UPDATE_SERIAL = "c_update_serial";
    /**
     * 邀请人账号
     */
    public static final String INVITE_ACOCUNT = "c_invite_account";


    @Override
    protected Class getCls() {
        return TableGroupMember.class;
    }
}
