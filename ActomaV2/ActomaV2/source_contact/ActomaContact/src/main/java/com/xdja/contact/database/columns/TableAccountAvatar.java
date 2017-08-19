package com.xdja.contact.database.columns;

/**
 * Created by wanghao on 2015/7/22.
 * <b>
 *     账户头像表
 * </b>
 */
public class TableAccountAvatar extends AbsTableOperate{


    public static final String TABLE_NAME = "t_account_avatar";

    public static final String ID = "_id";

    public static final String ACCOUNT = "c_account";

    public static final String AVATAR = "c_avatar";

    public static final String THUMBNAIL = "c_thumbnail";

    /*public static final String AVATAR_HASH = "c_avatar_hash";

    public static final String THUMBNAIL_HASH = "c_thumbnail_hash";

    public static final String AVATAR_URL = "c_avatar_url";

    public static final String THUMBNAIL_URL = "c_thumbnail_url";*/


    @Override
    protected Class getCls() {
        return TableAccountAvatar.class;
    }
}
