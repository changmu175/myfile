package com.xdja.imsdk.db.builder;

import com.xdja.imsdk.constant.ImSdkFileConstant;
import com.xdja.imsdk.constant.ImSdkFileConstant.FileType;
import com.xdja.imsdk.constant.MsgState;
import com.xdja.imsdk.constant.internal.FileTState;
import com.xdja.imsdk.constant.internal.MsgType;
import com.xdja.imsdk.constant.internal.Constant;
import com.xdja.imsdk.constant.internal.State;
import com.xdja.imsdk.db.helper.SqlBuilder;

import java.util.List;


/**
 * 项目名称：ImSdk                         <br>
 * 类描述  ：普通消息记录表                  <br>
 * 创建时间：2016/11/18 16:19              <br>
 * 修改记录：                              <br>
 *
 * @author liming@xdja.com               <br>
 * @version V1.1.7                       <br>
 */
public class MsgEntryBuilder {
    public static final String TABLE_NAME = "msg_entry";

    public static final String ID = "_id";                     // 数据库id
    public static final String SERVER_ID = "SERVER_ID";        // 服务器id，默认为生成纳秒时间取负
    public static final String SENDER = "SENDER";              // 消息发送方账号
    public static final String RECEIVER = "RECEIVER";          // 消息接收方账号
    public static final String CARD_ID = "CARD_ID";            // 消息发送方安全卡id
    public static final String TYPE = "TYPE";                  // 消息类型(文本/文件/群/闪...)
    public static final String CONTENT = "CONTENT";            // 消息内容
    public static final String STATE = "STATE";                // 消息状态(发送中/已发送/已接收...)
    public static final String SESSION_FLAG = "SESSION_FLAG";  // 消息所属会话标识
    public static final String ATTR = "ATTR";                  // 消息在客户端的属性
    public static final String LIFE_TIME = "LIFE_TIME";        // 消息生存周期
    public static final String CREATE_TIME = "CREATE_TIME";    // 发送方生成消息时间
    public static final String SENT_TIME = "SENT_TIME";        // 消息到达服务器时间
    public static final String SORT_TIME = "SORT_TIME";        // 消息排序时间

    private static final String[] ALL_COLUMNS = {ID, SERVER_ID,
            SENDER, RECEIVER, CARD_ID, TYPE, CONTENT, STATE, SESSION_FLAG,
            ATTR, LIFE_TIME, CREATE_TIME, SENT_TIME, SORT_TIME};

    public static final String JOIN_ID = "JOIN_ID";

    public static final String M_TAG = "M_TAG";

    public static final String SQL_CREATE_TABLE_MSG =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + SERVER_ID + " INTEGER UNIQUE,"
                    + SENDER + " TEXT,"
                    + RECEIVER + " TEXT,"
                    + CARD_ID + " TEXT,"
                    + TYPE + " INTEGER DEFAULT 0,"
                    + CONTENT + " TEXT,"
                    + STATE + " INTEGER DEFAULT 0,"
                    + SESSION_FLAG + " TEXT,"
                    + ATTR + " INTEGER DEFAULT 0,"
                    + LIFE_TIME + " INTEGER DEFAULT 0,"
                    + CREATE_TIME + " INTEGER DEFAULT 0,"
                    + SENT_TIME + " INTEGER DEFAULT 0,"
                    + SORT_TIME + " INTEGER DEFAULT 0)";

    /**
     * session_entry update: 保存消息更新会话触发器                                                                  <br>
     * 保存消息到msg_entry后，更新session_entry的LAST_TIME, LAST_MSG, REMINDED                 <br>
     * CREATE TRIGGER session_update_on_insert_trigger                                      <br>
     * AFTER INSERT ON msg_entry                                                            <br>
     * BEGIN                                                                                <br>
     *                                                                                      <br>
     * UPDATE session_entry SET                                                             <br>
     * session_entry.LAST_TIME = (CASE                                                      <br>
     * WHEN session_entry.LAST_TIME < new.SORT_TIME                                         <br>
     * THEN new.SORT_TIME                                                                   <br>
     * ELSE session_entry.LAST_TIME END),                                                   <br>
     *                                                                                      <br>
     * session_entry.LAST_MSG = (CASE                                                       <br>
     * WHEN session_entry.LAST_TIME < new.SORT_TIME                                         <br>
     * THEN new._id                                                                         <br>
     * ELSE session_entry.LAST_MSG END),                                                    <br>
     *                                                                                      <br>
     * session_entry.REMIND = (CASE                                                         <br>
     * WHEN new.STATE = 2 AND new.ATTR = 1                                                 <br>
     * THEN session_entry.REMIND + 1                                                        <br>
     * ELSE session_entry.REMIND END),                                                      <br>
     *                                                                                      <br>
     * WHERE (new.TYPE & 8) <> 8 AND session_entry.SESSION_FLAG = new.SESSION_FLAG;         <br>
     * END;                                                                                 <br>
     */
    public static final String SESSION_UPDATE_ON_INSERT_TRIGGER =
            "CREATE TRIGGER session_update_on_insert_trigger "+
                    " AFTER INSERT ON " + TABLE_NAME +

                    " BEGIN " +

                    " UPDATE " + SessionEntryBuilder.TABLE_NAME +" SET " +

                    SessionEntryBuilder.LAST_TIME +" = (CASE" +
                    " WHEN " + SessionEntryBuilder.LAST_TIME +" < new." + SORT_TIME +
                    " THEN new." + SORT_TIME +
                    " ELSE " +SessionEntryBuilder.LAST_TIME + " END), " +

                    SessionEntryBuilder.LAST_MSG +" = (CASE" +
                    " WHEN " + SessionEntryBuilder.LAST_TIME +" < new." + SORT_TIME +
                    " THEN new." + ID +
                    " ELSE " +SessionEntryBuilder.LAST_MSG + " END), " +

                    SessionEntryBuilder.REMINDED + " = (CASE" +
                    " WHEN new." + STATE + " = " + State.REC +
                    " AND new." + ATTR + " = " + Constant.MSG_REC_NEW +
                    " THEN " + SessionEntryBuilder.REMINDED + " + 1" +
                    " ELSE "  + SessionEntryBuilder.REMINDED + " END)" +

                    " WHERE (new." + TYPE + " & " + MsgType.MSG_TYPE_STATE + ") <> " + MsgType.MSG_TYPE_STATE +
                    " AND "+ SessionEntryBuilder.SESSION_FLAG +" = new." + SESSION_FLAG + ";" +

                    " END;";

    /**
     * deleted_msg insert: 删除消息保存消息id到消息删除表触发器                                  <br>
     * 删除的消息的数据库id和服务器id要保持到消息删除表deleted_msg表中                             <br>
     * CREATE TRIGGER msg_insert_deleted_on_delete_trigger                                  <br>
     * BEFORE DELETE ON msg_entry WHEN (old.TYPE & 16) != 16                                <br>
     * AND old.SERVER_ID > 0                                                                <br>
     * BEGIN                                                                                <br>
     * INSERT INTO deleted_msg (deleted_msg.MSG_ID, deleted_msg.SERVER_ID)                  <br>
     * VALUES(old._id, old.SERVER_ID);                                                      <br>
     * END;                                                                                 <br>
     */
    public static final String MSG_INSERT_DELETED_ON_DELETE_TRIGGER =
            "CREATE TRIGGER msg_insert_deleted_on_delete_trigger "+
            " BEFORE DELETE ON " + TABLE_NAME  +
            " WHEN (old." + TYPE + " & " + MsgType.MSG_TYPE_STATE + ") <> " + MsgType.MSG_TYPE_STATE +
            " AND old." + SERVER_ID + " > 0 " +

            " BEGIN " +

            " INSERT INTO " + DeletedMsgBuilder.TABLE_NAME +
            " (" + DeletedMsgBuilder.MSG_ID  + ", " + DeletedMsgBuilder.SERVER_ID + ")" +
            " VALUES(old." + ID + ", " + "old." + SERVER_ID + ");" +

            " END; ";

    /**
     * file_msg, hd_thumb, raw_file delete: 删除消息同步删除文件表触发器                        <br>
     * 删除消息时，如果是文件类型消息，同步删除各个关联表中的数据
     */
    public static final String FILE_MSG_DELETE_ON_DELETE_TRIGGER =
            "CREATE TRIGGER file_msg_delete_on_delete_trigger " +
                    " BEFORE DELETE ON " + TABLE_NAME +

                    " BEGIN " +
                    " DELETE FROM " + FileMsgBuilder.TABLE_NAME +
                    " WHERE " + FileMsgBuilder.MSG_ID +
                    " = old." + ID + ";" +

                    " DELETE FROM " + HdThumbFileBuilder.TABLE_NAME+
                    " WHERE " + HdThumbFileBuilder.HD_MSG_ID +
                    " = old." + ID + ";" +

                    " DELETE FROM " + RawFileBuilder.TABLE_NAME+
                    " WHERE " + RawFileBuilder.RAW_MSG_ID +
                    " = old." + ID + ";" +

                    " END; ";

    /**
     * file_msg, hd_thumb, raw_file delete: 更新闪信消息同步删除文件表触发器                    <br>
     * 消息是接收到的文件闪信，在状态为销毁时，同步删除文件表中数据
     */
    public static final String FILE_MSG_DELETE_ON_UPDATE_TRIGGER =
            "CREATE TRIGGER file_msg_delete_on_update_trigger " +
                    " AFTER UPDATE ON " + TABLE_NAME + " WHEN (new." +
                    TYPE + " & " + MsgType.MSG_TYPE_FILE + ") = " +
                    MsgType.MSG_TYPE_FILE + " AND new." + STATE + " = " +
                    State.BOMB + " AND new." + ATTR + " & " +
                    Constant.MSG_DIRECTION + " = " + Constant.MSG_DIRECTION +

                    " BEGIN " +
                    " DELETE FROM " + FileMsgBuilder.TABLE_NAME +
                    " WHERE " + FileMsgBuilder.MSG_ID +
                    " = new." + ID + ";" +

                    " DELETE FROM " + HdThumbFileBuilder.TABLE_NAME+
                    " WHERE " + HdThumbFileBuilder.HD_MSG_ID +
                    " = old." + ID + ";" +

                    " DELETE FROM " + RawFileBuilder.TABLE_NAME+
                    " WHERE " + RawFileBuilder.RAW_MSG_ID +
                    " = old." + ID + ";" +

                    " END; ";

    /**
     * 插入
     * INSERT OR IGNORE INTO msg_entry (...) VALUES(...);
     * @return sql
     */
    public static String insertSql() {
        return SqlBuilder.insertSql("INSERT OR IGNORE INTO  ", TABLE_NAME, ALL_COLUMNS);
    }

    /**
     * 删除指定消息
     * @param ids ids
     * @return String
     */
    public static String delSql(List<Long> ids) {
        return SqlBuilder.deleteSql(MsgEntryBuilder.TABLE_NAME,
                MsgEntryBuilder.ID,
                ids);
    }

    /**
     * 删除会话消息
     * @param tag tag
     * @return String
     */
    public static String delSql(String tag) {
        return SqlBuilder.deleteSql(MsgEntryBuilder.TABLE_NAME,
                MsgEntryBuilder.SESSION_FLAG,
                tag);
    }

    /**
     * 更新，根据状态消息更新状态
     * @return sql
     */
    public static String updateStateSql() {
        return "UPDATE " + TABLE_NAME + " SET " + STATE + " = ? WHERE " +
                SERVER_ID + " =  ? AND " + STATE + " < ?";
    }

    /**
     * 更新，更新状态和内容
     * @return sql
     */
    public static String updateMsgStateSql() {
        return "UPDATE " + TABLE_NAME + " SET " + STATE + " = ?, " +
                CONTENT + " = ? WHERE " + ID + " =  ?";
    }

    /**
     * name._id AS as
     * @param name name
     * @param as as
     * @return String
     */
    private static String getAsId(String name ,String as) {
        StringBuilder builder = new StringBuilder(name);
        builder.append(".").append(ID).append(" AS ").append(as);

        return builder.toString();
    }

    /**
     * 联合查询 [msg_entry, file_msg, hd_thumb_file, raw_file]
     * SELECT msg_entry._id AS JOIN_ID, file_msg._id AS F_JOIN_ID,
     * hd_thumb_file._id AS HD_JOIN_ID, raw_file._id AS RAW_JOIN_ID,
     * msg_entry.*, file_msg.*, hd_thumb_file.*, raw_file.*
     * FROM msg_entry
     * LEFT JOIN file_msg ON msg_entry._id = file_msg.MSG_ID
     * LEFT JOIN hd_thumb_file ON msg_entry._id = hd_thumb_file.HD_MSG_ID
     * LEFT JOIN raw_file ON msg_entry._id = raw_file.RAW_MSG_ID
     * @return StringBuilder
     */
    private static StringBuilder getAll() {
        StringBuilder builder = new StringBuilder("SELECT ");
        builder.append(getAsId(TABLE_NAME, JOIN_ID)).append(", ");
        builder.append(getAsId(FileMsgBuilder.TABLE_NAME, FileMsgBuilder.F_JOIN_ID)).append(", ");
        builder.append(getAsId(HdThumbFileBuilder.TABLE_NAME, HdThumbFileBuilder.HD_JOIN_ID)).append(", ");
        builder.append(getAsId(RawFileBuilder.TABLE_NAME, RawFileBuilder.RAW_JOIN_ID)).append(", ");
        builder.append(TABLE_NAME).append(".*, ").append(FileMsgBuilder.TABLE_NAME);
        builder.append(".*, ").append(HdThumbFileBuilder.TABLE_NAME);
        builder.append(".*, ").append(RawFileBuilder.TABLE_NAME);
        builder.append(".* FROM ").append(TABLE_NAME).append(" LEFT JOIN ");
        builder.append(FileMsgBuilder.TABLE_NAME).append(" ON ").append(TABLE_NAME);
        builder.append(".").append(ID).append(" = ").append(FileMsgBuilder.TABLE_NAME);
        builder.append(".").append(FileMsgBuilder.MSG_ID).append(" LEFT JOIN ");
        builder.append(HdThumbFileBuilder.TABLE_NAME).append(" ON ").append(TABLE_NAME);
        builder.append(".").append(ID).append(" = ").append(HdThumbFileBuilder.TABLE_NAME);
        builder.append(".").append(HdThumbFileBuilder.HD_MSG_ID).append(" LEFT JOIN ");
        builder.append(RawFileBuilder.TABLE_NAME).append(" ON ").append(TABLE_NAME);
        builder.append(".").append(ID).append(" = ").append(RawFileBuilder.TABLE_NAME);
        builder.append(".").append(RawFileBuilder.RAW_MSG_ID);
        return builder;
    }

    /**
     * 只查询msg [msg_entry]
     * SELECT msg_entry.* FROM msg_entry
     * @return StringBuilder
     */
    private static StringBuilder getNon() {
        StringBuilder builder = new StringBuilder("SELECT ");
        builder.append(TABLE_NAME).append(".*");
        builder.append(" FROM ").append(TABLE_NAME);
        return builder;
    }

    /**
     * 联合查询 [msg_entry, file_msg]
     * SELECT msg_entry.*, file_msg.*
     * FROM msg_entry
     * LEFT JOIN file_msg
     * ON msg_entry._id = file_msg.MSG_ID;
     * @return StringBuilder
     */
    private static StringBuilder getShow() {
        StringBuilder builder = new StringBuilder("SELECT ");
        builder.append(getAsId(TABLE_NAME, JOIN_ID)).append(", ");
        builder.append(getAsId(FileMsgBuilder.TABLE_NAME, FileMsgBuilder.F_JOIN_ID)).append(", ");
        builder.append(TABLE_NAME).append(".*, ").append(FileMsgBuilder.TABLE_NAME);
        builder.append(".* FROM ").append(TABLE_NAME).append(" LEFT JOIN ");
        builder.append(FileMsgBuilder.TABLE_NAME).append(" ON ").append(TABLE_NAME);
        builder.append(".").append(ID).append(" = ").append(FileMsgBuilder.TABLE_NAME);
        builder.append(".").append(FileMsgBuilder.MSG_ID);
        return builder;
    }

    /**
     * 联合查询 [msg_entry, file_msg, raw_file]
     * SELECT msg_entry.*, file_msg.*, raw_file.*
     * FROM msg_entry
     * LEFT JOIN file_msg ON msg_entry._id = file_msg.MSG_ID
     * LEFT JOIN raw_file ON msg_entry._id = raw_file.RAW_MSG_ID
     * @return StringBuilder
     */
    private static StringBuilder getRaw() {
        StringBuilder builder = new StringBuilder("SELECT ");
        builder.append(getAsId(TABLE_NAME, JOIN_ID)).append(", ");
        builder.append(getAsId(FileMsgBuilder.TABLE_NAME, FileMsgBuilder.F_JOIN_ID)).append(", ");
        builder.append(getAsId(RawFileBuilder.TABLE_NAME, RawFileBuilder.RAW_JOIN_ID)).append(", ");

        builder.append(TABLE_NAME).append(".*, ").append(FileMsgBuilder.TABLE_NAME);
        builder.append(".*, ").append(RawFileBuilder.TABLE_NAME);
        builder.append(".* FROM ").append(TABLE_NAME).append(" LEFT JOIN ");
        builder.append(FileMsgBuilder.TABLE_NAME).append(" ON ").append(TABLE_NAME);
        builder.append(".").append(ID).append(" = ").append(FileMsgBuilder.TABLE_NAME);
        builder.append(".").append(FileMsgBuilder.MSG_ID).append(" LEFT JOIN ");
        builder.append(RawFileBuilder.TABLE_NAME).append(" ON ").append(TABLE_NAME);
        builder.append(".").append(ID).append(" = ").append(RawFileBuilder.TABLE_NAME);
        builder.append(".").append(RawFileBuilder.RAW_MSG_ID);
        return builder;
    }

    /**
     * [msg_entry, file_msg] 查询指定会话的size消息
     * SELECT msg_entry.*, file_msg.* FROM msg_entry
     * LEFT JOIN file_msg ON msg_entry._id = file_msg.MSG_ID
     * WHERE ... ORDER BY msg_entry.SORT_TIME DESC LIMIT size;
     * @param tag tag
     * @param begin begin
     * @param size size
     * @return String
     */
    public static String queryShow(String tag, long begin, int size) {
        StringBuilder builder = getShow();

        String whereAll = " WHERE " + TABLE_NAME + "." + SESSION_FLAG + " = \'" + tag + "\'";
        if (begin == 0) {
            builder.append(whereAll)
                    .append(SqlBuilder.getOrder(SORT_TIME));
            if (size > 0) {
                builder.append(SqlBuilder.getLimit(Math.abs(size)));
            }
        } else {
            if (size == 0) {
                builder.append(" WHERE ").append(TABLE_NAME).append(".").append(ID).append(" = ").append(begin);
            } else {
                String whereCompare = " AND " + TABLE_NAME + "." + SORT_TIME + (size > 0 ? " < " : " > ") +
                        "(SELECT " + TABLE_NAME + "." + SORT_TIME + " FROM " + TABLE_NAME +
                        " WHERE " + TABLE_NAME + "." + ID + " = " + begin + ")";
                builder.append(whereAll)
                        .append(whereCompare)
                        .append(SqlBuilder.getOrder(SORT_TIME))
                        .append(SqlBuilder.getLimit(Math.abs(size)));
            }
        }
        return builder.toString();
    }

    /**
     * 查询指定会话的size图片
     * SELECT msg_entry.*, file_msg.*, hd_thumb_file.*, raw_file.*
     * FROM msg_entry
     * LEFT JOIN file_msg ON msg_entry._id = file_msg.MSG_ID
     * LEFT JOIN hd_thumb_file ON msg_entry._id = hd_thumb_file.HD_MSG_ID
     * LEFT JOIN raw_file ON msg_entry._id = raw_file.RAW_MSG_ID
     * WHERE ... ORDER BY msg_entry.SORT_TIME DESC LIMIT size;
     * @param tag tag
     * @param begin begin
     * @param size size
     * @return String
     */
    public static String queryImage(String tag, long begin, int size) {
        StringBuilder builder = getAll();

        String whereAll = " WHERE " + TABLE_NAME + "." + SESSION_FLAG + " = \'" + tag + "\' AND (" +
                FileMsgBuilder.TABLE_NAME + "." + FileMsgBuilder.FILE_TYPE +
                " = " + ImSdkFileConstant.FILE_IMAGE +
                " OR " + FileMsgBuilder.TABLE_NAME + "." + FileMsgBuilder.FILE_TYPE +
                " = " + ImSdkFileConstant.FILE_VIDEO + ") ";
        String whereSingle = " WHERE " + TABLE_NAME + "." + ID + " = ";

        if (begin == 0) {
            builder.append(whereAll).append(SqlBuilder.getOrderDefault(SORT_TIME));
            if (size > 0) {
                builder.append(SqlBuilder.getLimit(Math.abs(size)));
            }
        } else {
            if (size == 0) {
                builder.append(whereSingle).append(begin);
            } else {
                String stime = "(SELECT " + TABLE_NAME + "." + SORT_TIME + " FROM " + TABLE_NAME +
                        " WHERE " + TABLE_NAME + "." + ID + " = " + begin + ")";
                String whereCompare = " AND " + TABLE_NAME + "." + SORT_TIME + (size > 0 ? " < " : " > ") + stime;

                builder.append(whereAll).
                        append(whereCompare).
                        append(SqlBuilder.getOrderDefault(SORT_TIME)).
                        append(SqlBuilder.getLimit(Math.abs(size)));
            }
        }
        return builder.toString();
    }

    /**
     * 查询指定会话的文件
     * SELECT msg_entry.*, file_msg.*, raw_file.*
     * FROM msg_entry
     * LEFT JOIN file_msg ON msg_entry._id = file_msg.MSG_ID
     * LEFT JOIN raw_file ON msg_entry._id = raw_file.RAW_MSG_ID
     * WHERE ...
     * @return String
     */
    public static String queryFile() {
        StringBuilder builder = getRaw();
        builder.append(" WHERE ").append(FileMsgBuilder.TABLE_NAME).append(".").append(FileMsgBuilder.FILE_TYPE);
        builder.append(" = ").append(ImSdkFileConstant.FILE_NORMAL).append(" AND ((");
        builder.append(TABLE_NAME).append(".").append(ATTR).append(" & ").append(Constant.MSG_DIRECTION);
        builder.append(" = 0 AND ").append(FileMsgBuilder.TABLE_NAME).append(".").append(FileMsgBuilder.FILE_STATE);
        builder.append(" = ").append(FileTState.UP_DONE).append(") OR (").append(TABLE_NAME).append(".");
        builder.append(ATTR).append(" & ").append(Constant.MSG_DIRECTION).append(" = ").append(Constant.MSG_DIRECTION);
        builder.append(" AND ").append(FileMsgBuilder.TABLE_NAME).append(".").append(FileMsgBuilder.FILE_STATE);
        builder.append(" = ").append(FileTState.DECRYPT_SUCCESS).append("))");

        builder.append(SqlBuilder.getOrder(SORT_TIME));

        return builder.toString();
    }

    /**
     * 查询指定会话的文件
     * SELECT msg_entry.*, file_msg.*, raw_file.*
     * FROM msg_entry
     * LEFT JOIN file_msg ON msg_entry._id = file_msg.MSG_ID
     * LEFT JOIN raw_file ON msg_entry._id = raw_file.RAW_MSG_ID
     * WHERE ...
     * @param tag tag
     * @return String
     */
    public static String queryFile(String tag) {
        StringBuilder builder = getRaw();

        builder.append(" WHERE ").append(TABLE_NAME).append(".").append(SESSION_FLAG);
        builder.append(" = \'").append(tag).append("\' AND ");
        builder.append(FileMsgBuilder.TABLE_NAME).append(".").append(FileMsgBuilder.FILE_TYPE);
        builder.append(" = ").append(ImSdkFileConstant.FILE_NORMAL).append(" AND ((");
        builder.append(TABLE_NAME).append(".").append(ATTR).append(" & ").append(Constant.MSG_DIRECTION);
        builder.append(" = 0 AND ").append(FileMsgBuilder.TABLE_NAME).append(".").append(FileMsgBuilder.FILE_STATE);
        builder.append(" = ").append(FileTState.UP_DONE).append(") OR (").append(TABLE_NAME).append(".");
        builder.append(ATTR).append(" & ").append(Constant.MSG_DIRECTION).append(" = ");
        builder.append(Constant.MSG_DIRECTION).append("))");

        builder.append(SqlBuilder.getOrder(SORT_TIME));
        return builder.toString();
    }

    /**
     * 查询指定id的消息
     * SELECT msg_entry.*, file_msg.*, hd_thumb_file.*, raw_file.*
     * FROM msg_entry
     * LEFT JOIN file_msg ON msg_entry._id = file_msg.MSG_ID
     * LEFT JOIN hd_thumb_file ON msg_entry._id = hd_thumb_file.HD_MSG_ID
     * LEFT JOIN raw_file ON msg_entry._id = raw_file.RAW_MSG_ID
     * WHERE msg_entry._id = id
     * @param id id
     * @return String
     */
    public static String queryAMI(long id) {
        StringBuilder builder = getAll();
        builder.append(" WHERE ").append(TABLE_NAME).append(".").append(ID).append(" = ").append(id);
        return builder.toString();
    }

    /**
     * 查询指定id的消息
     * SELECT msg_entry.* FROM msg_entry
     * WHERE msg_entry._id = id
     * @param id id
     * @return String
     */
    public static String queryMI(long id) {
        StringBuilder builder = getNon();
        builder.append(" WHERE ").append(TABLE_NAME).append(".").append(ID).append(" = ").append(id);
        return builder.toString();
    }

    /**
     * 查询未读闪信
     * SELECT * FROM msg_entry WHERE where
     * AND msg_entry.STATE = READ AND (msg_entry.TYPE & BOMB) = BOMB
     * AND msg_entry.SENDER != account
     * @param where where
     * @return String
     */
    public static String queryRM(String account, String where) {
        StringBuilder builder = getShow();
        builder.append(where);
        builder.append(" AND ");
        builder.append(MsgEntryBuilder.TABLE_NAME).append(".").append(MsgEntryBuilder.STATE);
        builder.append(" = ").append(MsgState.MSG_STATE_READ).append(" AND (");
        builder.append(MsgEntryBuilder.TABLE_NAME).append(".").append(MsgEntryBuilder.TYPE);
        builder.append(" & ").append(MsgType.MSG_TYPE_BOMB).append(") = ").append(MsgType.MSG_TYPE_BOMB);
        builder.append(" AND ").append(MsgEntryBuilder.TABLE_NAME).append(".").append(MsgEntryBuilder.SENDER);
        builder.append(" <> \'").append(account).append("\';");
        return builder.toString();
    }

    /**
     * 查询未读闪信
     * SELECT * FROM msg_entry WHERE where
     * AND msg_entry.STATE = READ AND (msg_entry.TYPE & BOMB) = BOMB
     * AND msg_entry.SENDER != account
     * @return String
     */
    public static String queryARM(String account) {
        StringBuilder builder = getShow();
        builder.append(" WHERE ");
        builder.append(MsgEntryBuilder.TABLE_NAME).append(".").append(MsgEntryBuilder.STATE);
        builder.append(" = ").append(MsgState.MSG_STATE_READ).append(" AND (");
        builder.append(MsgEntryBuilder.TABLE_NAME).append(".").append(MsgEntryBuilder.TYPE);
        builder.append(" & ").append(MsgType.MSG_TYPE_BOMB).append(") = ").append(MsgType.MSG_TYPE_BOMB);
        builder.append(" AND ").append(MsgEntryBuilder.TABLE_NAME).append(".").append(MsgEntryBuilder.SENDER);
        builder.append(" <> \'").append(account).append("\';");
        return builder.toString();
    }

    /**
     * 查询正在发送中的消息
     * SELECT msg_entry._id FROM msg_entry WHERE where
     * AND msg_entry.STATE = DEFAULT
     * AND msg_entry.SENDER = account
     * @param account account
     * @param where where
     * @return String
     */
    public static String queryMIng(String account, String where) {
        StringBuilder builder = new StringBuilder("SELECT ");
        builder.append(TABLE_NAME).append(".").append(ID).append(" FROM ").append(TABLE_NAME);
        builder.append(where);
        builder.append(" AND ");
        builder.append(MsgEntryBuilder.TABLE_NAME).append(".").append(MsgEntryBuilder.STATE);
        builder.append(" = ").append(MsgState.MSG_STATE_DEFAULT);
        builder.append(" AND ").append(MsgEntryBuilder.TABLE_NAME).append(".").append(MsgEntryBuilder.SENDER);
        builder.append(" = \'").append(account).append("\';");
        return builder.toString();
    }

    /**
     * 查询所有发送失败消息的CREATE_TIME
     * SELECT msg_entry.CREATE_TIME FROM msg_entry
     * WHERE msg_entry.STATE < SEND
     * AND msg_entry.SENDER = account
     * @param account account
     * @return String
     */
    public static String queryFail(String account) {
        StringBuilder builder = new StringBuilder("SELECT ");
        builder.append(TABLE_NAME).append(".").append(CREATE_TIME);
        builder.append(" FROM ").append(TABLE_NAME).append(" WHERE ");
        builder.append(MsgEntryBuilder.TABLE_NAME).append(".").append(MsgEntryBuilder.STATE);
        builder.append(" < ").append(MsgState.MSG_STATE_SEND);
        builder.append(" AND ").append(MsgEntryBuilder.TABLE_NAME).append(".").append(MsgEntryBuilder.SENDER);
        builder.append(" = \'").append(account).append("\';");
        return builder.toString();
    }

    /**
     * 查询已有fst的消息
     * SELECT msg_entry.CREATE_TIME, msg_entry.SENDER,
     * msg_entry.RECEIVE, msg_entry.TYPE, msg_entry.STATE
     * FROM msg_entry
     * WHERE msg_entry.CREATE_TIME IN(fsts);
     * @param where where
     * @return String
     */
    public static String querySameFst(String where) {
        StringBuilder builder = new StringBuilder("SELECT ");
        builder.append(TABLE_NAME).append(".").append(CREATE_TIME).append(", ");
        builder.append(TABLE_NAME).append(".").append(SENDER).append(", ");
        builder.append(TABLE_NAME).append(".").append(RECEIVER).append(", ");
        builder.append(TABLE_NAME).append(".").append(TYPE).append(", ");
        builder.append(TABLE_NAME).append(".").append(STATE);
        builder.append(" FROM ").append(TABLE_NAME);
        builder.append(where);
        return builder.toString();
    }

    /**
     * 查询server id已保存的消息server id
     * SELECT msg_entry.SERVER_ID FROM msg_entry
     * WHERE msg_entry.SERVER_ID IN(serverIds);
     * @param where where
     * @return String
     */
    public static String querySaved(String where) {
        StringBuilder builder = new StringBuilder("SELECT ");
        builder.append(TABLE_NAME).append(".").append(SERVER_ID);
        builder.append(" FROM ").append(TABLE_NAME);
        builder.append(where);
        return builder.toString();
    }

    /**
     * 查询server id对应的消息server id 和 state
     * SELECT msg_entry.SERVER_ID, msg_entry.STATE FROM msg_entry
     * WHERE msg_entry.SERVER_ID IN(serverIds);
     * @param where where
     * @return String
     */
    public static String queryState(String where) {
        StringBuilder builder = new StringBuilder("SELECT ");
        builder.append(TABLE_NAME).append(".").append(SERVER_ID);
        builder.append(", ").append(TABLE_NAME).append(".").append(STATE);
        builder.append(" FROM ").append(TABLE_NAME);
        builder.append(where);
        return builder.toString();
    }

    /**
     * 查询server id对应的消息
     * SELECT msg_entry.*, file_msg.*, hd_thumb_file.*, raw_file.*
     * FROM msg_entry
     * LEFT JOIN file_msg ON msg_entry._id = file_msg.MSG_ID
     * LEFT JOIN hd_thumb_file ON msg_entry._id = hd_thumb_file.HD_MSG_ID
     * LEFT JOIN raw_file ON msg_entry._id = raw_file.RAW_MSG_ID
     * WHERE msg_entry.SERVER_ID IN (server ids);
     * @param where where
     * @return String
     */
    public static String queryMS(String where) {
        StringBuilder builder  = getAll();
        builder.append(where);
        return builder.toString();
    }

    /**
     * 查询tag对应会话中的最后一条消息
     * @param tag tag
     * @return String
     */
    public static String queryMax(String tag) {
        StringBuilder builder = new StringBuilder("SELECT ");
        builder.append(TABLE_NAME).append(".* ").append(" FROM ");
        builder.append(TABLE_NAME).append(" WHERE ").append(TABLE_NAME).append(".");
        builder.append(SESSION_FLAG).append(" = \'").append(tag).append("\' AND ");
        builder.append(TABLE_NAME).append(".").append(SORT_TIME).append(" = (SELECT MAX(");
        builder.append(TABLE_NAME).append(".").append(SORT_TIME).append(") FROM ");
        builder.append(TABLE_NAME).append(" WHERE ").append(TABLE_NAME).append(".").append(SESSION_FLAG);
        builder.append(" = \'").append(tag).append("\')");
        return builder.toString();
    }


    /**
     * 查询
     * SELECT msg_entry._id, msg_entry.type,...
     * FROM msg_entry...
     * WHERE msg_entry._id = id;
     * @param id id
     * @param type type
     * @return String
     */
    public static String queryFile(long id, FileType type) {
        StringBuilder builder = new StringBuilder("SELECT ");
        builder.append(TABLE_NAME).append(".").append(ID).append(" AS ").append(MsgEntryBuilder.JOIN_ID).append(", ");
        builder.append(TABLE_NAME).append(".").append(TYPE).append(", ");

        switch (type) {
            case IS_SHOW:
                builder.append(FileMsgBuilder.TABLE_NAME).append(".* ");
                builder.append("FROM ").append(TABLE_NAME).append(" LEFT JOIN ");
                builder.append(FileMsgBuilder.TABLE_NAME).append(" ON ");
                builder.append(TABLE_NAME).append(".").append(ID).append(" = ");
                builder.append(FileMsgBuilder.TABLE_NAME).append(".").append(FileMsgBuilder.MSG_ID);
                builder.append(" WHERE ").append(TABLE_NAME).append(".").append(ID);
                builder.append(" = ").append(id).append(";");
                break;
            case IS_HD:
                builder.append(FileMsgBuilder.TABLE_NAME).append(".").append(FileMsgBuilder.FILE_TYPE).append(", ");
                builder.append(HdThumbFileBuilder.TABLE_NAME).append(".* ");
                builder.append("FROM ").append(TABLE_NAME).append(" LEFT JOIN ");
                builder.append(FileMsgBuilder.TABLE_NAME).append(" ON ");
                builder.append(TABLE_NAME).append(".").append(ID).append(" = ");
                builder.append(FileMsgBuilder.TABLE_NAME).append(".").append(FileMsgBuilder.MSG_ID);
                builder.append(" LEFT JOIN ").append(HdThumbFileBuilder.TABLE_NAME).append(" ON ");
                builder.append(TABLE_NAME).append(".").append(ID).append(" = ");
                builder.append(HdThumbFileBuilder.TABLE_NAME).append(".").append(HdThumbFileBuilder.HD_MSG_ID);
                builder.append(" WHERE ").append(TABLE_NAME).append(".").append(ID);
                builder.append(" = ").append(id).append(";");
                break;
            case IS_RAW:
                builder.append(FileMsgBuilder.TABLE_NAME).append(".").append(FileMsgBuilder.FILE_TYPE).append(", ");
                builder.append(RawFileBuilder.TABLE_NAME).append(".* ");
                builder.append("FROM ").append(TABLE_NAME).append(" LEFT JOIN ");
                builder.append(FileMsgBuilder.TABLE_NAME).append(" ON ");
                builder.append(TABLE_NAME).append(".").append(ID).append(" = ");
                builder.append(FileMsgBuilder.TABLE_NAME).append(".").append(FileMsgBuilder.MSG_ID);
                builder.append(" LEFT JOIN ").append(RawFileBuilder.TABLE_NAME).append(" ON ");
                builder.append(TABLE_NAME).append(".").append(ID).append(" = ");
                builder.append(RawFileBuilder.TABLE_NAME).append(".").append(RawFileBuilder.RAW_MSG_ID);
                builder.append(" WHERE ").append(TABLE_NAME).append(".").append(ID);
                builder.append(" = ").append(id).append(";");
                break;
            default:
                break;
        }
        return builder.toString();
    }
}
