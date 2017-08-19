package com.xdja.contact.database.columns.error;


import com.xdja.contact.database.columns.AbsTableOperate;

/**
 * Created by wanghao on 2015/7/7.
 */
public class TableErrorPush extends AbsTableOperate {


    /**
     * 推送错误表
     */
    public static final String TABLE_NAME = "t_error_push";
    /**
     * 自增id
     */
    public static final String ID = "_id";
    /**
     * 事务id
     */
    public static final String TRANS_ID = "c_trans_id";
    /**
     * 事务类型
     */
    public static final String TRANS_TYPE = "c_trans_type";
    /**
     * 出错原因
     */
    public static final String REASON = "c_reason";
    /**
     * 保存时间
     */
    public static final String CREATE_TIME = "c_create_time";
    /**
     *异常发生时间
     */
    public static final String UPDATE_TIME = "c_update_time";


    @Override
    protected Class getCls() {
        return TableErrorPush.class;
    }
}
