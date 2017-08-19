package com.xdja.imsdk.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.xdja.imsdk.db.bean.DuplicateIdDb;
import com.xdja.imsdk.db.builder.DuplicateIdBuilder;

import java.util.List;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：                                               <br>
 * 创建时间：2016/11/27 下午3:02                              <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public class DuplicateIdDao extends AbstractDao <DuplicateIdDb> {
    private static DuplicateIdDao instance;

    private DuplicateIdDao() {
        super();
    }

    public static DuplicateIdDao getInstance() {
        if (instance == null) {
            synchronized (DuplicateIdDao.class) {
                if (instance == null) {
                    instance = new DuplicateIdDao();
                }
            }
        }
        return instance;
    }

    /**
     * 批量插入重复消息
     * @param db db
     * @param dbs dbs
     */
    public void insertBatch(SQLiteDatabase db, List<DuplicateIdDb> dbs) {
        if (dbs == null || dbs.isEmpty()) {
            return;
        }

        insertBatch(db, dbs, DuplicateIdBuilder.insertSql());
    }

    /**
     * 批量插入，需开启事务
     * @param list list
     */
    public void insertBatchUpgrade(SQLiteDatabase db, List<DuplicateIdDb> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        insertBatchUpgrade(db, list, DuplicateIdBuilder.insertSql());
    }


    @Override
    protected DuplicateIdDb readEntry(Cursor cursor, int offset) {
        DuplicateIdDb entity = new DuplicateIdDb(
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0),
                cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1),
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2)
        );
        return entity;
    }

    @Override
    protected void bindValues(SQLiteStatement stmt, DuplicateIdDb entry) {
        stmt.clearBindings();
        Long id = entry.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }

        String send_time = entry.getSend_time();
        if (send_time != null) {
            stmt.bindString(2, send_time);
        }

        String server_id = entry.getServer_id();
        if (server_id != null) {
            stmt.bindString(3, server_id);
        }
    }
}
