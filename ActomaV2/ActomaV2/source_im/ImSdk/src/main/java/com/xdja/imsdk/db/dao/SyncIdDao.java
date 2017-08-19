package com.xdja.imsdk.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.xdja.imsdk.db.bean.SyncIdDb;
import com.xdja.imsdk.db.builder.SyncIdBuilder;
import com.xdja.imsdk.db.helper.UpdateArgs;

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

public class SyncIdDao extends AbstractDao <SyncIdDb> {
    private static SyncIdDao instance;

    private SyncIdDao() {
        super();
    }

    public static SyncIdDao getInstance() {
        if (instance == null) {
            synchronized (SyncIdDao.class) {
                if (instance == null) {
                    instance = new SyncIdDao();
                }
            }
        }
        return instance;
    }

    public void insertBatch(List<SyncIdDb> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        insertBatch(ids, SyncIdBuilder.insertSql());
    }

    /**
     * 插入，需开启事务
     * @param db db
     * @param ids ids
     */
    public void insertBatchUpgrade(SQLiteDatabase db, List<SyncIdDb> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        insertBatchUpgrade(db, ids, SyncIdBuilder.insertSql());
    }

    /**
     * 更新单条记录
     * @param args args
     */
    public void updateS(UpdateArgs args) {
        update(args);
    }

    /**
     * 批量更新
     * @param argsList argsList
     */
    public void updateSBatch(List<UpdateArgs> argsList) {
        updateBatch(SyncIdBuilder.updateValueSql(), argsList);
    }

    /**
     * 获取指定的sync id
     * @param key key
     * @return long
     */
    public long getSyncId(String key) {
        long value = 0;
        Cursor cursor = null;
        SyncIdDb db = null;
        try {
            cursor = query(SyncIdBuilder.queryValue(key));
            if (cursor != null && cursor.moveToNext()) {
                db = readEntry(cursor, 0);
            }
            if (db != null) {
                value = Long.valueOf(db.getId_value());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
        }

        return value;
    }

    /**
     * 查询sync id数量
     * @return int
     */
    public int getCount() {
        int count = 0;
        Cursor cursor = null;
        try {
            cursor = query(SyncIdBuilder.queryCount());
            if (cursor != null && cursor.moveToNext()) {
                count = cursor.getCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
        }

        return count;
    }

    @Override
    protected SyncIdDb readEntry(Cursor cursor, int offset) {
        SyncIdDb entity = new SyncIdDb(
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0),
                cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1),
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2)
        );
        return entity;
    }

    @Override
    protected void bindValues(SQLiteStatement stmt, SyncIdDb entry) {
        stmt.clearBindings();
        Long id = entry.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }

        String id_type = entry.getId_type();
        if (id_type != null) {
            stmt.bindString(2, id_type);
        }

        String id_value = entry.getId_value();
        if (id_value != null) {
            stmt.bindString(3, id_value);
        }
    }
}
