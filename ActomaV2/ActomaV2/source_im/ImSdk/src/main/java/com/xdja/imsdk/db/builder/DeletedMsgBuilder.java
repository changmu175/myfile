package com.xdja.imsdk.db.builder;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：删除的消息id记录表                 <br>
 * 创建时间：2016/11/25 15:58                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class DeletedMsgBuilder {
    public static final String TABLE_NAME = "deleted_msg";

    public static final String ID = "_id";                     // 数据库id
    public static final String MSG_ID = "MSG_ID";              // 删除的消息数据库id
    public static final String SERVER_ID = "SERVER_ID";        // 删除的消息服务器id

    public static final String SQL_CREATE_TABLE_DELETED_MSG =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + MSG_ID + " INTEGER DEFAULT 0,"
                    + SERVER_ID + " TEXT)";

    /**
     * SELECT SERVER_ID FROM deleted_msg WHERE SERVER_ID IN(serverIds);
     * @param where where
     * @return String
     */
    public static String queryIds(String where) {
        StringBuilder builder = new StringBuilder("SELECT ");
        builder.append(SERVER_ID).append(" FROM ").append(TABLE_NAME).append(where);
        return builder.toString();
    }

}
