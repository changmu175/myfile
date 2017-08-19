package com.xdja.imsdk.db.builder;

import com.xdja.imsdk.db.helper.SqlBuilder;

/**
 * 项目名称：ImSdk                                                  <br>
 * 类描述  ：ImSdk文件类型消息文件基本信息表file_msg                    <br>
 *          本表中保存的是消息列表中展示的内容，说明如下                  <br>
 *          语音即为语音原始文件信息                                   <br>
 *          图片为图片缩略小图文件信息                                 <br>
 *          视频为视频缩略图文件信息                                   <br>
 *          其他文件为原始文件                                        <br>
 * 创建时间：2016/11/25 16:14                                        <br>
 * 修改记录：                                                        <br>
 *
 * @author liming@xdja.com                                         <br>
 * @version V1.1.7                                                 <br>
 */
public class FileMsgBuilder {
    public static final String TABLE_NAME = "file_msg";

    public static final String ID = "_id";                           // 数据库id
    public static final String FILE_PATH = "FILE_PATH";              // 文件(加密前)在本地路径
    public static final String ENCRYPT_PATH = "ENCRYPT_PATH";        // 文件(加密后)在本地路径
    public static final String FILE_NAME = "FILE_NAME";              // 文件名称
    public static final String FILE_SIZE = "FILE_SIZE";              // 文件大小
    public static final String ENCRYPT_SIZE = "ENCRYPT_SIZE";        // 加密后文件大小
    public static final String TRANSLATE_SIZE = "TRANSLATE_SIZE";    // 文件传输大小
    public static final String SUFFIX = "SUFFIX";                    // 文件后缀名
    public static final String FID = "FID";                          // 文件在服务器地址
    public static final String FILE_STATE = "FILE_STATE";            // 文件的状态
    public static final String MSG_ID = "MSG_ID";                    // 文件所属消息数据库id
    public static final String FILE_TYPE = "FILE_TYPE";              // 文件类型(语音/图片/视频/其他...)
    public static final String EXTRA_INFO = "EXTRA_INFO";            // 文件扩展信息(语音时长/视频时长...)

    private static final String[] ALL_COLUMNS = { ID, FILE_PATH, ENCRYPT_PATH, FILE_NAME,
            FILE_SIZE, ENCRYPT_SIZE, TRANSLATE_SIZE, SUFFIX, FID, FILE_STATE,
            MSG_ID, FILE_TYPE, EXTRA_INFO };

    public static final String F_JOIN_ID = "F_JOIN_ID";

    public static final String SQL_CREATE_TABLE_FILE_MSG =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + ID + " INTEGER PRIMARY KEY ASC AUTOINCREMENT,"
                    + FILE_PATH + " TEXT,"
                    + ENCRYPT_PATH + " TEXT,"
                    + FILE_NAME + " TEXT,"
                    + FILE_SIZE + " INTEGER DEFAULT 0,"
                    + ENCRYPT_SIZE + " INTEGER DEFAULT 0,"
                    + TRANSLATE_SIZE + " INTEGER DEFAULT 0,"
                    + SUFFIX + " TEXT,"
                    + FID + " TEXT,"
                    + FILE_STATE + " INTEGER DEFAULT 0,"
                    + MSG_ID + " INTEGER DEFAULT 0 UNIQUE,"
                    + FILE_TYPE + " INTEGER DEFAULT -1,"
                    + EXTRA_INFO + " TEXT)";

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
