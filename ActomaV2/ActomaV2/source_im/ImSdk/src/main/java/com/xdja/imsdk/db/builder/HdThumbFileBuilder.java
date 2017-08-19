package com.xdja.imsdk.db.builder;

import com.xdja.imsdk.db.helper.SqlBuilder;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：高清缩略图文件信息表，图片预览用      <br>
 * 创建时间：2016/11/25 16:31                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class HdThumbFileBuilder {
    public static final String TABLE_NAME = "hd_thumb_file";

    public static final String ID = "_id";                                 // 数据库id
    public static final String HD_MSG_ID = "HD_MSG_ID";                    // 所属的file_msg的数据库id
    public static final String HD_FILE_PATH = "HD_FILE_PATH";              // 文件(加密前)本地路径
    public static final String HD_ENCRYPT_PATH = "HD_ENCRYPT_PATH";        // 文件(加密后)本地路径
    public static final String HD_FILE_NAME = "HD_FILE_NAME";              // 文件名称
    public static final String HD_FILE_SIZE = "HD_FILE_SIZE";              // 文件大小
    public static final String HD_ENCRYPT_SIZE = "HD_ENCRYPT_SIZE";        // 加密后文件大小
    public static final String HD_TRANSLATE_SIZE = "HD_TRANSLATE_SIZE";    // 文件传输大小
    public static final String HD_FID = "HD_FID";                          // 文件在服务器地址
    public static final String HD_STATE = "HD_STATE";                      // 文件状态

    private static final String[] ALL_COLUMNS = { ID, HD_MSG_ID, HD_FILE_PATH, HD_ENCRYPT_PATH,
            HD_FILE_NAME, HD_FILE_SIZE, HD_ENCRYPT_SIZE, HD_TRANSLATE_SIZE, HD_FID, HD_STATE };

    public static final String HD_JOIN_ID = "HD_JOIN_ID";

    public static final String SQL_CREATE_TABLE_HD_THUMB =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + ID + " INTEGER PRIMARY KEY ASC AUTOINCREMENT,"
                    + HD_MSG_ID + " INTEGER UNIQUE,"
                    + HD_FILE_PATH + " TEXT,"
                    + HD_ENCRYPT_PATH + " TEXT,"
                    + HD_FILE_NAME + " TEXT,"
                    + HD_FILE_SIZE + " INTEGER DEFAULT 0,"
                    + HD_ENCRYPT_SIZE + " INTEGER DEFAULT 0,"
                    + HD_TRANSLATE_SIZE + " INTEGER DEFAULT 0,"
                    + HD_FID + " TEXT,"
                    + HD_STATE + " INTEGER DEFAULT 0)";

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
