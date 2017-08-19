package com.xdja.imsdk.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.xdja.imsdk.db.bean.DeletedMsgDb;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：                                               <br>
 * 创建时间：2016/11/27 上午12:59                              <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public class DeletedMsgDao extends AbstractDao <DeletedMsgDb> {
    private static DeletedMsgDao instance;

    private DeletedMsgDao() {
        super();
    }

    public static DeletedMsgDao getInstance() {
        if (instance == null) {
            synchronized (DeletedMsgDao.class) {
                if (instance == null) {
                    instance = new DeletedMsgDao();
                }
            }
        }
        return instance;
    }

    /**
     * 查询删除表中已删除的消息server ids
     * @param query query
     * @return List
     */
    public List<Long> getIds(String query) {
        List<Long> ids = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = query(query);
            while (cursor != null && cursor.moveToNext()) {
                ids.add(cursor.getLong(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
        }
        return ids;
    }

    @Override
    protected void bindValues(SQLiteStatement stmt, DeletedMsgDb entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }

        Long msg_id = entity.getMsg_id();
        if (msg_id != null) {
            stmt.bindLong(2, msg_id);
        }

        String server_id = entity.getServer_id();
        if (server_id != null) {
            stmt.bindString(3, server_id);
        }
    }

    @Override
    protected DeletedMsgDb readEntry(Cursor cursor, int offset) {
        DeletedMsgDb entity = new DeletedMsgDb(
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0),
                cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1),
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2)
        );
        return entity;
    }
}