package com.xdja.contact.database.columns;

/**
 * Created by wanghao on 2015/7/8.
 */
public class TableEncryptRecord extends AbsTableOperate {


    /**
     * 好友表名
     */
    public static final String TABLE_NAME = "t_encrypt_record";

    /**
     * 自增id
     */
    public static final String ID = "_id";

    /**
     * 账户
     */
    public static final String ACCOUNT = "c_account";

    /**
     * 最后建立安全连接的时间
     */
    public static final String LAST_CONNECTED_TIME = "c_last_connected_time";

    /**
     * 是否建立安全通信
     */
    public static final String OPEN_TRANSFER = "c_open_transfer";


    @Override
    protected Class getCls() {
        return TableEncryptRecord.class;
    }
}
