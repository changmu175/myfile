package com.xdja.contact.database.columns.error;


import com.xdja.contact.database.columns.AbsTableOperate;

/**
 * Created by wanghao on 2015/7/7.
 */
public class TableErrorGroup extends AbsTableOperate {

    /**
     * 群组异常数据表名
     */
    public static final String TABLE_NAME = "t_error_groups";

    public static final String ID = "_id";
    /**
     * 群组ID
     */
    public static final String GROUP_ID = "c_group_id";
    /**
     * 事务类型
     */
    public static final String TYPE = "c_type";
    /**
     * 异常原因
     */
    public static final String REASON = "c_reason";
    /**
     * 事务产生时间
     */
    public static final String CREATE_TIME = "c_create_time";
    /**
     * 异常发生时间
     */
    public static final String UPDATE_TIME = "c_update_time";



    @Override
    protected Class getCls() {
        return TableErrorGroup.class;
    }
}
