package com.xdja.imsdk.db.builder;

import com.xdja.imsdk.db.helper.SqlBuilder;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：和后台同步消息时使用的id记录         <br>
 * 创建时间：2016/11/25 16:47                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class SyncIdBuilder {
    public static final String TABLE_NAME = "sync_id";

    public static final String ID = "_id";                  // 数据库id
    public static final String ID_TYPE = "ID_TYPE";         // id类型
    public static final String ID_VALUE = "ID_VALUE";       // 服务器id值

    private static final String[] ALL_COLUMNS = { ID, ID_TYPE, ID_VALUE };

    public static final String SQL_CREATE_TABLE_SYNC_ID =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + ID_TYPE + " TEXT UNIQUE,"
                    + ID_VALUE + " TEXT)";

    /**
     * 插入
     * INSERT OR REPLACE INTO sync_id (...) VALUES(...);
     * @return sql
     */
    public static String insertSql() {
        return SqlBuilder.insertSql("INSERT OR REPLACE INTO  ",
                TABLE_NAME, ALL_COLUMNS);
    }

    /**
     * 更新操作
     * UPDATE sync_id SET ID_VALUE = ? WHERE ID_TYPE = ?;
     * @return  sql
     */
    public static String updateValueSql() {
        return "UPDATE " + TABLE_NAME + " SET " + ID_VALUE + " = ? WHERE " +
                ID_TYPE + " =  ?";
    }

    /**
     * 查询
     * SELECT sync_id.* FROM sync_id WHERE ID_TYPE = key;
     * @param key 键
     * @return sql
     */
    public static String queryValue(String key) {
        StringBuilder builder = new StringBuilder("SELECT ");
        builder.append(TABLE_NAME).append(".* FROM ");
        builder.append(TABLE_NAME).append(" WHERE ").append(ID_TYPE);
        builder.append(" = \'").append(key).append("\'");
        return builder.toString();
    }

    /**
     * 查询
     * SELECT sync_id._id FROM sync_id;
     * @return sql
     */
    public static String queryCount() {
        StringBuilder builder = new StringBuilder("SELECT ");
        builder.append(TABLE_NAME).append("._id FROM ");
        builder.append(TABLE_NAME);
        return builder.toString();
    }
}
