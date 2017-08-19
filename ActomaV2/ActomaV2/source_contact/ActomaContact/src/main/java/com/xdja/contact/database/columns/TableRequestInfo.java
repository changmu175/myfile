package com.xdja.contact.database.columns;

/**
 * Created by wanghao on 2015/7/7.
 */
public class TableRequestInfo extends AbsTableOperate {


    /**
     * 用户请求信息表名
     */
    public static final String TABLE_NAME = "t_request_info";

    /**
     * 自增id
     */
    public static final String ID = "_id";
    /**
     * 帐号
     */
    public static final String ACCOUNT = "c_account";

    /**
     * 保存时间
     */
    public static final String CREATE_TIME = "c_create_time";
    /**
     * 验证信息
     */
    public static final String VALIDATE_INFO = "c_validate_info";


    @Override
    protected Class getCls() {
        return TableRequestInfo.class;
    }
}
