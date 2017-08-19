package com.xdja.imsdk.db.builder;

import com.xdja.imsdk.db.helper.SqlBuilder;

/**
 * 项目名称：ImSdk                                  <br>
 * 类描述  ：原始文件基本信息，图片原图，视频源文件       <br>
 * 创建时间：2016/11/25 16:30                       <br>
 * 修改记录：                                       <br>
 *
 * @author liming@xdja.com                         <br>
 * @version V1.1.7                                 <br>
 */
public class RawFileBuilder {
    public static final String TABLE_NAME = "raw_file";

    public static final String ID = "_id";                                  // 数据库id
    public static final String RAW_MSG_ID = "RAW_MSG_ID";                   // 所属的file_msg的数据库id
    public static final String RAW_FILE_PATH = "RAW_FILE_PATH";             // 本地(加密前)存储路径
    public static final String RAW_ENCRYPT_PATH = "RAW_ENCRYPT_PATH";       // 本地(加密后)存储路径
    public static final String RAW_FILE_NAME = "RAW_FILE_NAME";             // 文件名称
    public static final String RAW_FILE_SIZE = "RAW_FILE_SIZE";             // 文件大小
    public static final String RAW_ENCRYPT_SIZE = "RAW_ENCRYPT_SIZE";       // 加密后文件大小
    public static final String RAW_TRANSLATE_SIZE = "RAW_TRANSLATE_SIZE";   // 文件传输大小
    public static final String RAW_FID = "RAW_FID";                         // 文件在后台地址
    public static final String RAW_STATE = "RAW_STATE";                     // 文件状态

    private static final String[] ALL_COLUMNS = { ID, RAW_MSG_ID, RAW_FILE_PATH, RAW_ENCRYPT_PATH,
            RAW_FILE_NAME, RAW_FILE_SIZE, RAW_ENCRYPT_SIZE, RAW_TRANSLATE_SIZE, RAW_FID, RAW_STATE };

    public static final String RAW_JOIN_ID = "RAW_JOIN_ID";

    public static final String SQL_CREATE_TABLE_RAW_FILE =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + ID + " INTEGER PRIMARY KEY ASC AUTOINCREMENT,"
                    + RAW_MSG_ID + " INTEGER UNIQUE,"
                    + RAW_FILE_PATH + " TEXT,"
                    + RAW_ENCRYPT_PATH + " TEXT,"
                    + RAW_FILE_NAME + " TEXT,"
                    + RAW_FILE_SIZE + " INTEGER DEFAULT 0,"
                    + RAW_ENCRYPT_SIZE + " INTEGER DEFAULT 0,"
                    + RAW_TRANSLATE_SIZE + " INTEGER DEFAULT 0,"
                    + RAW_FID + " TEXT,"
                    + RAW_STATE + " INTEGER DEFAULT 0)";

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
