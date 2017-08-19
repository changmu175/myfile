package com.xdja.contact.database.columns;

/**
 * Created by wanghao on 2015/7/7.
 */
public class TableGroup extends AbsTableOperate {

    /**
     * 群组表
     */
    public static final String TABLE_NAME = "t_group";
    /**
     * 自增id
     */
    public static final String ID = "_id";

    public static final String GROUP_ID = "c_group_id";
    /**
     * 群组名称
     */
    public static final String GROUP_NAME = "c_group_name";
    /**
     * 群组大头像
     */
    public static final String AVATAR = "c_avatar";
    /**
     * 群组小头像
     */
    public static final String THUMBNAIL = "c_thumbnail";
    /**
     * 群组大头像hash
     */
    public static final String AVATAR_HASH = "c_avatar_hash";
    /**
     * 群组小头像hash
     */
    public static final String THUMBNAIL_HASH = "c_thumbnail_hash";
    /**
     * 群主
     */
    public static final String OWNER = "c_owner";
    /**
     * 群组名称简拼
     */
    public static final String GROUP_NAME_PY = "c_group_name_py";
    /**
     * 群组名称全拼
     */
    public static final String GROUP_NAME_FULL_PY = "c_group_name_full_py";
    /**
     * 增量更新标示
     */
    public static final String UPDATE_SERIAL = "c_update_serial";
    /**
     * 是否已删除
     */
    public static final String IS_DELETED = "c_is_deleted";
    /**
     * 服务器创建群组的时间
     */
    public static final String SERVER_CREATE_TIME = "c_server_create_time";



    @Override
    protected Class getCls() {
        return TableGroup.class;
    }
}
