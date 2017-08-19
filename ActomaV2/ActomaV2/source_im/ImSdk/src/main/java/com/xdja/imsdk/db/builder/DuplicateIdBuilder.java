package com.xdja.imsdk.db.builder;

import com.xdja.imsdk.db.helper.SqlBuilder;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：重复消息id记录表                   <br>
 * 创建时间：2016/11/25 16:58                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class DuplicateIdBuilder {
    public static final String TABLE_NAME = "duplicate_id";

    public static final String ID = "_id";                  // 数据库id
    public static final String SEND_TIME = "SEND_TIME";     // 消息发送时间
    public static final String SERVER_ID = "SERVER_ID";     // 服务器id

    private static final String[] ALL_COLUMNS = { ID, SEND_TIME, SERVER_ID };

    public static final String SQL_CREATE_TABLE_DUPLICATE_ID =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + SEND_TIME + " TEXT,"
                    + SERVER_ID + " TEXT)";

    /**
     * 插入
     * INSERT OR IGNORE INTO session_entry (...) VALUES(...);
     * @return sql
     */
    public static String insertSql() {
        return SqlBuilder.insertSql("INSERT OR IGNORE INTO  ",
                TABLE_NAME, ALL_COLUMNS);
    }
}
