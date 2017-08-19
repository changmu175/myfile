package com.xdja.contact.database.columns;

/**
 * Created by wanghao on 2015/7/7.
 * <b>
 * 账户信息表
 * </b>
 */
public class TableActomaAccount extends AbsTableOperate {

    /**
     * 安通账户表
     */
    public static final String TABLE_NAME = "t_actoma_account";

    /**
     * 自增id
     */
    public static final String ID = "_id";
    /**
     * 好友安通账号、唯一
     */
    public static final String ACCOUNT = "c_account";
    /**
     * 账号别名
     */
    public static final String ALIAS = "c_alias";
    /**
     * 昵称
     */
    public static final String NICKNAME = "c_nickname";
    /**
     * 电话 (绑定的电话)
     */
    public static final String BIND_PHONE = "c_phone";
    /**
     * 昵称全拼
     */
    public static final String NICKNAME_FULL_PY = "c_nickname_full_py";
    /**
     * 昵称简拼
     */
    public static final String NICKNAME_PY = "c_nickname_py";
    /**
     * 性别
     */
    public static final String GENDER = "c_gender";
    /**
     * 邮箱
     */
    public static final String EMAIL = "c_email";

    /**
     * 0:安通账号未使用 非0:已使用
     */
    public static final String FIRST_LOGIN_TIME = "c_first_login_time";
    /**
     * 激活状态；1-未激活，2-已激活
     */
    public static final String ACTIVATE_STATUS = "c_activate_status";

    //单个用户的更新序列
    public static final String IDENTIFY = "c_identify";

    @Override
    protected Class getCls() {
        return TableActomaAccount.class;
    }
}
