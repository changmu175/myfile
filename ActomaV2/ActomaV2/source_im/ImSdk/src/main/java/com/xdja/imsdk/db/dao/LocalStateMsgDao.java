package com.xdja.imsdk.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.xdja.imsdk.db.bean.LocalStateMsgDb;
import com.xdja.imsdk.db.builder.LocalStateMsgBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：                                               <br>
 * 创建时间：2016/11/27 下午3:07                              <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public class LocalStateMsgDao  extends AbstractDao <LocalStateMsgDb> {
    private static LocalStateMsgDao instance;

    private LocalStateMsgDao() {
        super();
    }

    public static LocalStateMsgDao getInstance() {
        if (instance == null) {
            synchronized (LocalStateMsgDao.class) {
                if (instance == null) {
                    instance = new LocalStateMsgDao();
                }
            }
        }
        return instance;
    }

    /**
     * 插入，需开启事务
     * @param state state
     * @return long
     */
    public long insert(LocalStateMsgDb state) {
        return insert(state, LocalStateMsgBuilder.insertSql());
    }

    /**
     * 插入，已开启事务
     * @param db db
     * @param state state
     * @return long
     */
    public long insert(SQLiteDatabase db, LocalStateMsgDb state) {
        return insert(db, state, LocalStateMsgBuilder.insertSql());
    }

    /**
     * 批量插入，已开启事务
     * @param db db
     * @param states states
     */
    public void insertBatch(SQLiteDatabase db, List<LocalStateMsgDb> states) {
        if (states == null || states.isEmpty()) {
            return;
        }
        insertBatch(db, states, LocalStateMsgBuilder.insertSql());
    }

    /**
     * 删除
     * @param sql sql
     * @return int
     */
    public int deleteL(String sql) {
        return delete(sql);
    }

    /**
     * 获取所有的本地状态消息
     */
    public List<LocalStateMsgDb> getStates() {
        List<LocalStateMsgDb> msgListDb = new ArrayList<LocalStateMsgDb>();
        Cursor cursor = null;

        try {
            cursor = query(LocalStateMsgBuilder.queryAll());
            while (cursor != null && cursor.moveToNext()) {
                msgListDb.add(readEntry(cursor, 0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            closeCursor(cursor);
        }
        return msgListDb;
    }

    @Override
    protected void bindValues(SQLiteStatement stmt, LocalStateMsgDb entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }

        Long sendTime = entity.getSendTime();
        if (sendTime != null) {
            stmt.bindLong(2, sendTime);
        }

        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(3, content);
        }
    }

    @Override
    protected LocalStateMsgDb readEntry(Cursor cursor, int offset) {
        LocalStateMsgDb entity = new LocalStateMsgDb(
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0),
                cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1),
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2)
        );
        return entity;
    }

}
