package com.xdja.imsdk.db.builder;

import com.xdja.imsdk.db.helper.SqlBuilder;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：配置项表                          <br>
 * 创建时间：2016/11/25 17:03                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class OptionsBuilder {
    public static final String TABLE_NAME = "options";

    public static final String ID = "_id";                  // 数据库id
    public static final String PROPERTY = "PROPERTY";       // 属性key
    public static final String VALUE = "VALUE";             // 属性value

    private static final String[] ALL_COLUMNS = { ID, PROPERTY, VALUE };

    public static final String SQL_CREATE_TABLE_OPTIONS =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + PROPERTY + " TEXT UNIQUE,"
                    + VALUE + " TEXT)";

    /**
     * 插入
     * INSERT OR REPLACE INTO session_entry (...) VALUES(...);
     * @return sql
     */
    public static String insertSql() {
        return SqlBuilder.insertSql("INSERT OR REPLACE INTO  ",
                TABLE_NAME, ALL_COLUMNS);
    }

    /**
     * 查询
     * SELECT options.* FROM options;
     * @return sql
     */
    public static String queryAll() {
        return SqlBuilder.selectAll(TABLE_NAME);
    }
}
