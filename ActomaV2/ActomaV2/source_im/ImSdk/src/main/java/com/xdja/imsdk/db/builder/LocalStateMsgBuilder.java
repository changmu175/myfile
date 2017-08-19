package com.xdja.imsdk.db.builder;

import com.xdja.imsdk.db.helper.SqlBuilder;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：本地产生的状态消息                  <br>
 *          发送成功即删除，失败则下次继续发送
 * 创建时间：2016/11/25 16:01                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class LocalStateMsgBuilder {
    public static final String TABLE_NAME = "local_state_msg";

    public static final String ID = "_id";                     // 数据库id
    public static final String SEND_TIME = "SEND_TIME";        // 发送时间
    public static final String CONTENT = "CONTENT";            // 发送的内容


    private static final String[] ALL_COLUMNS = { ID, SEND_TIME, CONTENT };

    public static final String SQL_CREATE_TABLE_LOCAL_STATE_MSG =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + SEND_TIME + " INTEGER,"
                    + CONTENT + " TEXT)";

    /**
     * 插入
     * INSERT OR IGNORE INTO session_entry (...) VALUES(...);
     * @return sql
     */
    public static String insertSql() {
        return SqlBuilder.insertSql("INSERT OR IGNORE INTO  ",
                TABLE_NAME, ALL_COLUMNS);
    }

    /**
     * 删除状态消息
     * @param tag tag
     * @return String
     */
    public static String delSql(String tag) {
        return SqlBuilder.deleteSql(LocalStateMsgBuilder.TABLE_NAME,
                LocalStateMsgBuilder.ID,
                tag);
    }

    /**
     * 查询
     * SELECT local_state_msg.* FROM local_state_msg;
     * @return sql
     */
    public static String queryAll() {
        return SqlBuilder.selectAll(TABLE_NAME);
    }
}
