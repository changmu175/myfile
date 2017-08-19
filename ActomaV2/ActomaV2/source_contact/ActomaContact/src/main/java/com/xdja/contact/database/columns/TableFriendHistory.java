package com.xdja.contact.database.columns;

/**
 * Created by wanghao on 2015/7/7.
 */
public class TableFriendHistory extends AbsTableOperate{

    /**
     * 好友请求表名
     */
    public static final String TABLE_NAME = "t_friend_request_history";
    /**
     * 自增id
     */
    public static final String ID = "_id";
    /**
     * 添加方账号
     */
    public static final String C_REQ_ACCOUNT = "c_req_account";
    /**
     * 被添加方账号
     */
    public static final String C_REC_ACCOUNT = "c_rec_account";
    /**
     * 显示在请求列表以及关联查询请求信息表时需要用
     */
    public static final String SHOW_ACCOUNT = "c_show_account";
    /**
     * 请求时间
     */
    public static final String CREATE_TIME = "c_time";
    /**
     * 1等待验证 2 ：已添加 4 接受
     */
    public static final String STATE = "c_state";

    /**
     * 增量更新标示
     */
    public static final String UPDATE_SERIAL = "c_update_serial";

    /**
     * 已读未读 0： 未读 1 :已读
     */
    public static final String IS_READ = "c_is_read";
    /**
     * 最近一次请求验证信息
     */
    public static final String LAST_REQUEST_INFO = "c_last_request_info";


    @Override
    protected Class getCls() {
        return TableFriendHistory.class;
    }
}
