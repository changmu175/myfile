package com.xdja.imsdk.db.builder;

import android.text.TextUtils;

import com.xdja.imsdk.db.helper.SqlBuilder;

import java.util.Collection;
import java.util.List;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：会话记录表                         <br>
 * 创建时间：2016/11/25 15:51                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class SessionEntryBuilder {
    public static final String TABLE_NAME = "session_entry";

    public static final String ID = "_id";                     // 数据库id
    public static final String IM_PARTNER = "IM_PARTNER";      // 聊天对象账号
    public static final String SESSION_TYPE = "SESSION_TYPE";  // 会话类型(单聊/群聊/自定义...)
    public static final String LAST_MSG = "LAST_MSG";          // 最后一条消息数据库id
    public static final String START_TIME = "START_TIME";      // 会话创建时间
    public static final String LAST_TIME = "LAST_TIME";        // 会话最后一条消息时间
    public static final String REMINDED = "REMINDED";          // 会话中新消息数量
    public static final String SESSION_FLAG = "SESSION_FLAG";  // 会话唯一标识

    private static final String[] ALL_COLUMNS = { ID, IM_PARTNER,
            SESSION_TYPE, LAST_MSG, START_TIME,LAST_TIME, REMINDED, SESSION_FLAG };

    public static final String S_JOIN_ID = "S_JOIN_ID";

    public static final String S_TAG = "S_TAG";

    public static final String SQL_CREATE_TABLE_SESSION_ENTRY =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + IM_PARTNER + " TEXT,"
                    + SESSION_TYPE + " INTEGER DEFAULT 0,"
                    + LAST_MSG + " INTEGER DEFAULT 0,"
                    + START_TIME + " INTEGER DEFAULT 0,"
                    + LAST_TIME + " INTEGER DEFAULT 0,"
                    + REMINDED + " INTEGER DEFAULT 0,"
                    + SESSION_FLAG + " TEXT UNIQUE)";

    /**
     * 删除会话同步删除消息触发器                               <br>
     * 删除会话后，要同步删除消息详情表msg_entry中相关消息
     */
    public static final String SESSION_DELETE_MSG_ON_DELETE_TRIGGER =
            "CREATE TRIGGER session_delete_msg_on_delete_trigger " +
                    "BEFORE DELETE ON " + TABLE_NAME +

                    " BEGIN " +
                    " DELETE FROM " + MsgEntryBuilder.TABLE_NAME + " WHERE " +
                    MsgEntryBuilder.SESSION_FLAG + " = old." +
                    MsgEntryBuilder.SESSION_FLAG + ";" +
                    " END;";

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
     * 删除指定会话
     * @param tags tags
     * @return String
     */
    public static String delSql(List<String> tags) {
        return SqlBuilder.deleteSql(SessionEntryBuilder.TABLE_NAME,
                SessionEntryBuilder.SESSION_FLAG,
                tags);
    }

    /**
     * 删除所有会话
     * @return String
     */
    public static String delAll() {
        return SqlBuilder.deleteSql(SessionEntryBuilder.TABLE_NAME, null);
    }

    /**
     * 查询最新的会话，需要联合查询消息
     * SELECT session_entry.*, msg_entry.* FROM session_entry
     * LEFT JOIN msg_entry ON session_entry.LAST_MSG = msg_entry.ID
     * WHERE session_entry.LAST_TIME = (SELECT MAX(session_entry.LAST_TIME) FROM session_entry)
     * ORDER BY LAST_TIME DESC;
     * @return String
     */
    public static String queryNew() {
        StringBuilder builder = getJoinQuery();
        builder.append(" WHERE ").append(TABLE_NAME).append(".").append(LAST_MSG);
        builder.append(" = ").append("(SELECT MAX(").append(TABLE_NAME).append(".").append(LAST_MSG);
        builder.append(") FROM ").append(TABLE_NAME).append(") ").append(SqlBuilder.getOrder(LAST_TIME));
        return builder.toString();
    }

    /**
     * 查询所有会话，需要联合查询消息
     * SELECT session_entry.*, msg_entry.* FROM session_entry
     * LEFT JOIN msg_entry ON session_entry.LAST_MSG = msg_entry.ID
     * ORDER BY LAST_TIME DESC;
     * @return String
     */
    public static String queryAll() {
        StringBuilder builder = getJoinQuery();
        builder.append(SqlBuilder.getOrder(LAST_TIME));
        return builder.toString();
    }

    /**
     * 查询size会话列表，需要联合查询消息
     * SELECT session_entry.*, msg_entry.* FROM session_entry
     * LEFT JOIN msg_entry ON session_entry.LAST_MSG = msg_entry.ID
     * WHERE where ORDER BY LAST_TIME DESC LIMIT size;
     * @param where where
     * @param size size
     * @return sql
     */
    public static String querySize(String where, int size) {
        StringBuilder builder = getJoinQuery();

        if (!TextUtils.isEmpty(where)) {
            builder.append(" WHERE ").append(where);
        }
        builder.append(SqlBuilder.getOrder(LAST_TIME));
        builder.append(SqlBuilder.getLimit(Math.abs(size)));
        return builder.toString();
    }

    /**
     * 查询tag单个会话，需要联合查询消息
     * SELECT session_entry.*, msg_entry.* FROM session_entry
     * LEFT JOIN msg_entry ON session_entry.LAST_MSG = msg_entry.ID
     * WHERE where;
     * @param tag tag
     * @return sql
     */
    public static String querySM(String tag) {
        StringBuilder builder = getJoinQuery();
        builder.append(" WHERE ").append(TABLE_NAME).append(".");
        builder.append(SessionEntryBuilder.SESSION_FLAG);
        builder.append(" = ").append("\'").append(tag).append("\'");
        return builder.toString();
    }

    /**
     * 查询tag单个会话，不需要联合查询消息
     * SELECT session.* FROM session_entry
     * WHERE session_entry.SESSION_FLAG = tag;
     * @param tag tag
     * @return String
     */
    public static String queryS(String tag) {
        StringBuilder builder = getQuery();
        builder.append(" WHERE ").append(TABLE_NAME).append(".");
        builder.append(SessionEntryBuilder.SESSION_FLAG);
        builder.append(" = ").append("\'").append(tag).append("\'");
        return builder.toString();
    }

    /**
     * 查询指定会话中新消息提醒数量
     * SELECT REMINDED FROM session_entry WHERE SESSION_FLAG = 'tag';
     * @param tag 标识
     * @return sql
     */
    public static String queryR(String tag) {
        StringBuilder builder = new StringBuilder("SELECT ");
        builder.append(TABLE_NAME).append(".").append(REMINDED);
        builder.append(" FROM ").append(TABLE_NAME).append(" WHERE ");
        builder.append(TABLE_NAME).append(".").append(SESSION_FLAG).append(" = ");
        builder.append("\'").append(tag).append("\';");
        return builder.toString();
    }

    /**
     * 查询所有新消息提醒数量
     * SELECT SUM(REMINDED) FROM session_entry;
     * @return sql
     */
    public static String queryRSum() {
        StringBuilder builder = new StringBuilder("SELECT SUM(");
        builder.append(TABLE_NAME).append(".").append(REMINDED).append(")");
        builder.append(" FROM ").append(TABLE_NAME);
        return builder.toString();
    }

    /**
     * 查询所有会话tag
     * SELECT SESSION_FLAG FROM session_entry;
     * @return sql
     */
    public static String queryT() {
        StringBuilder builder = new StringBuilder("SELECT ");
        builder.append(TABLE_NAME).append(".").append(SESSION_FLAG);
        builder.append(" FROM ").append(TABLE_NAME);
        return builder.toString();
    }

    /**
     * 查询指定消息id所在的会话，不需要联合查询消息
     * SELECT * FROM session_entry WHERE SESSION_FLAG =
     * (SELECT msg_entry.SESSION_FLAG FROM msg_entry WHERE msg_entry._id = msgId);
     * @param msgId id
     * @return sql
     */
    public static String querySI(long msgId) {
        StringBuilder builder = getQuery();
        builder.append(" WHERE ").append(TABLE_NAME).append(".");
        builder.append(SESSION_FLAG).append(" = (SELECT ");
        builder.append(MsgEntryBuilder.TABLE_NAME).append(".");
        builder.append(MsgEntryBuilder.SESSION_FLAG).append(" FROM ").append(MsgEntryBuilder.TABLE_NAME);
        builder.append(" WHERE ").append(MsgEntryBuilder.TABLE_NAME).append(".");
        builder.append(MsgEntryBuilder.ID).append(" = ").append(msgId).append(");");
        return builder.toString();
    }

    /**
     * 查询指定会话tags列表所在的会话列表，需要联合查询消息
     * SELECT session_entry.*, msg_entry.* FROM session_entry
     * LEFT JOIN msg_entry ON session_entry.LAST_MSG = msg_entry.ID
     * WHERE SESSION_FLAG IN(args);
     * @param args tags
     * @return sql
     */
    public static String queryST(Collection<?> args) {
        StringBuilder builder = getJoinQuery();
        builder.append(" WHERE ").append(TABLE_NAME).append(".").append(SESSION_FLAG);
        SqlBuilder.appendColumn(builder,args);
        return builder.toString();
    }

    /**
     * 联合查询会话和消息
     * SELECT session_entry.*, msg_entry.*, file_msg.FILE_NAME
     * FROM session_entry
     * LEFT JOIN msg_entry
     * ON session_entry.LAST_MSG = msg_entry._id
     * LEFT JOIN file_msg
     * ON session_entry.LAST_MSG = file_msg.MSG_ID
     * @return StringBuilder
     */
    private static StringBuilder getJoinQuery() {
        StringBuilder builder = new StringBuilder("SELECT ");

        builder.append(TABLE_NAME).append(".").append(ID).append(" AS ").append(S_JOIN_ID).append(", ");
        builder.append(TABLE_NAME).append(".").append(SESSION_FLAG).append(" AS ").append(S_TAG).append(", ");
        builder.append(MsgEntryBuilder.TABLE_NAME).append(".").append(MsgEntryBuilder.ID);
        builder.append(" AS ").append(MsgEntryBuilder.JOIN_ID).append(", ");
        builder.append(MsgEntryBuilder.TABLE_NAME).append(".").append(MsgEntryBuilder.SESSION_FLAG);
        builder.append(" AS ").append(MsgEntryBuilder.M_TAG).append(", ");
        builder.append(FileMsgBuilder.TABLE_NAME).append(".").append(FileMsgBuilder.FILE_NAME).append(", ");
        builder.append(TABLE_NAME).append(".*, ");
        builder.append(MsgEntryBuilder.TABLE_NAME).append(".* ");
        builder.append(" FROM ").append(TABLE_NAME).append(" LEFT JOIN ");
        builder.append(MsgEntryBuilder.TABLE_NAME).append(" ON ");
        builder.append(TABLE_NAME).append(".").append(LAST_MSG).append(" = ");
        builder.append(MsgEntryBuilder.TABLE_NAME).append(".").append(MsgEntryBuilder.ID);
        builder.append(" LEFT JOIN ").append(FileMsgBuilder.TABLE_NAME);
        builder.append(" ON ").append(TABLE_NAME).append(".").append(LAST_MSG);
        builder.append(" = ").append(FileMsgBuilder.TABLE_NAME).append(".").append(FileMsgBuilder.MSG_ID);
        return builder;
    }

    /**
     * 查询会话
     * SELECT session_entry.* FROM session_entry
     * @return StringBuilder
     */
    private static StringBuilder getQuery() {
        StringBuilder builder = new StringBuilder("SELECT ");

        builder.append(TABLE_NAME).append(".* ");
        builder.append(" FROM ");
        builder.append(TABLE_NAME);
        return builder;
    }
}
