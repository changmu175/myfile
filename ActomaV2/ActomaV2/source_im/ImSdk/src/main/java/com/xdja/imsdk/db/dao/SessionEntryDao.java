package com.xdja.imsdk.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import com.xdja.imsdk.constant.ImSdkResult;
import com.xdja.imsdk.db.bean.FileMsgDb;
import com.xdja.imsdk.db.bean.MsgEntryDb;
import com.xdja.imsdk.db.bean.SessionEntryDb;
import com.xdja.imsdk.db.builder.FileMsgBuilder;
import com.xdja.imsdk.db.builder.MsgEntryBuilder;
import com.xdja.imsdk.db.builder.SessionEntryBuilder;
import com.xdja.imsdk.db.helper.OptType.SQuery;
import com.xdja.imsdk.db.helper.UpdateArgs;
import com.xdja.imsdk.db.wrapper.SessionWrapper;
import com.xdja.imsdk.manager.ModelMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：                                               <br>
 * 创建时间：2016/11/27 下午3:08                              <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public class SessionEntryDao extends AbstractDao <SessionEntryDb> {
    private static SessionEntryDao instance;

    private SessionEntryDao() {
        super();
    }

    public static SessionEntryDao getInstance() {
        if (instance == null) {
            synchronized (SessionEntryDao.class) {
                if (instance == null) {
                    instance = new SessionEntryDao();
                }
            }
        }
        return instance;
    }

    /**
     * 插入操作
     * @param wrapper 会话
     * @return id
     */
    public long insert(SessionWrapper wrapper) {
        long rowId = 0;

        if (wrapper == null || wrapper.getSessionEntryDb() == null) {
            return rowId;
        }

        rowId = insert(wrapper.getSessionEntryDb(), SessionEntryBuilder.insertSql());
        return rowId;
    }

    /**
     * 批量插入
     * @param wrappers 会话
     */
    public void insertBatch(List<SessionWrapper> wrappers) {
        if (wrappers == null || wrappers.isEmpty()) {
            return;
        }

        List<SessionEntryDb> dbs = ModelMapper.getIns().mapSession(wrappers);

        insertBatch(dbs, SessionEntryBuilder.insertSql());
    }

    /**
     * 批量插入，已开启事务
     * @param db db
     * @param wrappers wrappers
     */
    public void insertBatch(SQLiteDatabase db, List<SessionWrapper> wrappers) {
        if (wrappers == null || wrappers.isEmpty()) {
            return;
        }

        List<SessionEntryDb> dbs = ModelMapper.getIns().mapSession(wrappers);

        insertBatch(db, dbs, SessionEntryBuilder.insertSql());
    }

    /**
     * 批量插入， 需开启事务
     * @param db db
     * @param list list
     */
    public void insertBatchUpgrade(SQLiteDatabase db, List<SessionEntryDb> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        insertBatchUpgrade(db, list, SessionEntryBuilder.insertSql());
    }

    /**
     * 删除
     * @param sql sql
     * @return int
     */
    public int deleteS(String sql) {
        return delete(sql);
    }

    /**
     * 更新
     * @param args args
     */
    public void updateS(UpdateArgs args) {
        update(args);
    }

    /**
     * 查询会话列表，需要联合查询消息
     * size>0: 往时间小的方向取值，往过去的时间取，按时间降序排列<br>
     * size<0: 往时间大的方向取值，往向前的时间取，按时间降序排列<br>
     * size=0: 取当前这条
     * begin = "", size = 0，表示取所有会话
     * begin = "", size != 0， 表示从最新一个会话开始取size大小的会话
     * begin != "", size =0, 表示取begin这一条
     * begin != "", size != 0，表示取begin开始，size大小的会话
     * @param begin begin
     * @param size size
     * @return List
     */
    public List<SessionWrapper> getSessions(String begin, int size) {
        List<SessionWrapper> result = new ArrayList<>();

        StringBuilder where = new StringBuilder();
        String sql = "";
        if (TextUtils.isEmpty(begin)) {
            if (size < 0) {
                // 没有比最新会话还要新的会话，直接返回空
                return result;
            }

            if (size > 0) {
                SessionWrapper wrapper = getSession(SessionEntryBuilder.queryNew(), SQuery.HAVE);
                if (wrapper == null || wrapper.getMsgEntryDb() == null) {
                    return result;
                }

                MsgEntryDb msgEntryDb = wrapper.getMsgEntryDb();

                where.append(SessionEntryBuilder.LAST_TIME)
                        .append(size > 0 ? " < ? " : " > ? ")
                        .append(msgEntryDb.getSort_time());
                sql = SessionEntryBuilder.querySize(where.toString(), size);
            }

            if (size == 0) {
                // 返回所有会话
                sql = SessionEntryBuilder.queryAll();
            }
        }

        if (!TextUtils.isEmpty(begin)) {
            // 查询begin对应的会话
            SessionWrapper wrapper = getSession(SessionEntryBuilder.querySM(begin), SQuery.HAVE);

            if (wrapper == null || wrapper.getMsgEntryDb() == null) {
                return result;
            }

            if (size == 0) {
                result.add(wrapper);
                return result;
            }

            MsgEntryDb msgEntryDb = wrapper.getMsgEntryDb();

            where.append(SessionEntryBuilder.LAST_TIME)
                    .append(size > 0 ? " < ? " : " > ? ")
                    .append(msgEntryDb.getSort_time());
            sql = SessionEntryBuilder.querySize(where.toString(), size);
        }

        return getSessions(sql, SQuery.HAVE);
    }

    /**
     * 查询会话列表
     * @param query 查询语句
     * @param type 是否联合查询消息
     * @return 会话列表
     */
    public List<SessionWrapper> getSessions(String query, SQuery type) {
        List<SessionWrapper> entryList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = query(query);
            if (type == SQuery.HAVE) {
                while (cursor != null && cursor.moveToNext()) {
                    entryList.add(loadSessionCursor(cursor));
                }

            }

            if(type == SQuery.NON) {
                while (cursor != null && cursor.moveToNext()) {
                    SessionEntryDb sessionEntryDb = readEntry(cursor, 0);
                    SessionWrapper wrapper = new SessionWrapper(sessionEntryDb);
                    entryList.add(wrapper);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
        }
        return entryList;
    }

    /**
     * 查询会话
     * @param query 查询语句
     * @param type 是否联合查询消息
     * @return 会话
     */
    public SessionWrapper getSession(String query, SQuery type) {
        Cursor cursor = null;
        SessionWrapper wrapper = null;
        try {
            cursor = query(query);
            if (type == SQuery.HAVE) {
                if (cursor != null && cursor.moveToNext()) {
                    wrapper = loadSessionCursor(cursor);
                }
            }

            if(type == SQuery.NON) {
                if (cursor != null && cursor.moveToNext()) {
                    SessionEntryDb sessionEntryDb = readEntry(cursor, 0);
                    wrapper = new SessionWrapper(sessionEntryDb);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
        }
        return wrapper;
    }

    /**
     * 查询新消息数量
     * @param query query
     * @return int
     */
    public int getRemind(String query) {
        int remindNum = 0;
        Cursor cursor = null;
        try {
            cursor = query(query);
            if (cursor != null && cursor.moveToNext()) {
                remindNum = cursor.getInt(0);
            }
        } catch (Exception e) {
            return ImSdkResult.RESULT_FAIL_DATABASE;
        } finally {
            closeCursor(cursor);
        }
        return remindNum;
    }

    /**
     * 查询所有会话 session flag
     * @param query query
     * @return List
     */
    public List<String> querySessions(String query) {
        List<String> tags = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = query(query);
            while (cursor != null && cursor.moveToNext()) {
                tags.add(cursor.getString(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
        }
        return tags;
    }

    /**
     * load cursor SessionEntryBuilder getJoinQuery()
     * @param cursor cursor
     * @return SessionWrapper
     */
    private SessionWrapper loadSessionCursor(Cursor cursor) {
        SessionWrapper wrapper = new SessionWrapper();
        SessionEntryDb s = new SessionEntryDb();
        s.setId(cursor.getLong(cursor.getColumnIndex(SessionEntryBuilder.S_JOIN_ID)));
        s.setIm_partner(cursor.getString(cursor.getColumnIndex(SessionEntryBuilder.IM_PARTNER)));
        s.setSession_flag(cursor.getString(cursor.getColumnIndex(SessionEntryBuilder.S_TAG)));
        s.setSession_type(cursor.getInt(cursor.getColumnIndex(SessionEntryBuilder.SESSION_TYPE)));
        s.setStart_time(cursor.getLong(cursor.getColumnIndex(SessionEntryBuilder.START_TIME)));
        s.setReminded(cursor.getInt(cursor.getColumnIndex(SessionEntryBuilder.REMINDED)));
        s.setLast_time(cursor.getLong(cursor.getColumnIndex(SessionEntryBuilder.LAST_TIME)));
        s.setLast_msg(cursor.getLong(cursor.getColumnIndex(SessionEntryBuilder.LAST_MSG)));

        MsgEntryDb m = new MsgEntryDb();
        m.setId(cursor.getLong(cursor.getColumnIndex(MsgEntryBuilder.JOIN_ID)));
        m.setServer_id(cursor.getLong(cursor.getColumnIndex(MsgEntryBuilder.SERVER_ID)));
        m.setCard_id(cursor.getString(cursor.getColumnIndex(MsgEntryBuilder.CARD_ID)));
        m.setSender(cursor.getString(cursor.getColumnIndex(MsgEntryBuilder.SENDER)));
        m.setReceiver(cursor.getString(cursor.getColumnIndex(MsgEntryBuilder.RECEIVER)));
        m.setContent(cursor.getString(cursor.getColumnIndex(MsgEntryBuilder.CONTENT)));
        m.setState(cursor.getInt(cursor.getColumnIndex(MsgEntryBuilder.STATE)));
        m.setType(cursor.getInt(cursor.getColumnIndex(MsgEntryBuilder.TYPE)));
        m.setSession_flag(cursor.getString(cursor.getColumnIndex(MsgEntryBuilder.M_TAG)));
        m.setLife_time(cursor.getInt(cursor.getColumnIndex(MsgEntryBuilder.LIFE_TIME)));
        m.setAttr(cursor.getInt(cursor.getColumnIndex(MsgEntryBuilder.ATTR)));
        m.setSort_time(cursor.getLong(cursor.getColumnIndex(MsgEntryBuilder.SORT_TIME)));
        m.setCreate_time(cursor.getLong(cursor.getColumnIndex(MsgEntryBuilder.CREATE_TIME)));
        m.setSent_time(cursor.getLong(cursor.getColumnIndex(MsgEntryBuilder.SENT_TIME)));

        FileMsgDb f = new FileMsgDb();
        f.setMsg_id(cursor.getLong(cursor.getColumnIndex(MsgEntryBuilder.JOIN_ID)));
        f.setFile_name(cursor.getString(cursor.getColumnIndex(FileMsgBuilder.FILE_NAME)));

        wrapper.setSessionEntryDb(s);
        wrapper.setMsgEntryDb(m);
        wrapper.setFileMsgDb(f);
        return wrapper;
    }

    @Override
    protected SessionEntryDb readEntry(Cursor cursor, int offset) {
        SessionEntryDb entry = new SessionEntryDb(
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0),      // id
                cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1),    // im_partner
                cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2),       // session_type
                cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3),      // last_msg
                cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4),      // start_time
                cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5),      // last_time
                cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6),       // reminded
                cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7)     // session_flag
        );
        return entry;
    }

    @Override
    protected void bindValues(SQLiteStatement stmt, SessionEntryDb entity) {
        stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }

        String im_partner = entity.getIm_partner();
        if (im_partner != null) {
            stmt.bindString(2, im_partner);
        }

        Integer session_type = entity.getSession_type();
        if (session_type != null) {
            stmt.bindLong(3, session_type);
        }

        Long last_msg = entity.getLast_msg();
        if (last_msg != null) {
            stmt.bindLong(4, last_msg);
        }

        Long start_time = entity.getStart_time();
        if (start_time != null) {
            stmt.bindLong(5, start_time);
        }

        Long last_time = entity.getLast_time();
        if (last_time != null) {
            stmt.bindLong(6, last_time);
        }

        Integer reminded = entity.getReminded();
        if (reminded != null) {
            stmt.bindLong(7, reminded);
        }

        String session_flag = entity.getSession_flag();
        if (session_flag != null) {
            stmt.bindString(8, session_flag);
        }
    }
}
