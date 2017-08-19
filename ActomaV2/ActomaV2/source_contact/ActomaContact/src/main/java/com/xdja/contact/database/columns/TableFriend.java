package com.xdja.contact.database.columns;

/**
 * Created by wanghao on 2015/7/22.
 */
public class TableFriend extends AbsTableOperate{

    public static final String TABLE_NAME = "t_friend";

    public static final String ID = "_id";

    public static final String ACCOUNT = "c_account";

    public static final String REMARK = "c_remark";

    public static final String REMARK_PY = "c_remark_py";

    public static final String REMARK_FULL_PY = "c_remark_full_py";

    public static final String TYPE = "c_type";

    public static final String UPDATE_SERIAL = "c_update_serial";

    public static final String IS_SHOW = "c_is_show";

    public static final String INITIATIVE = "initiative";

    @Override
    protected Class getCls() {
        return TableFriend.class;
    }
}
