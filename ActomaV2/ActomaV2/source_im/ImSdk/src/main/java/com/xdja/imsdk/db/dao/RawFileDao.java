package com.xdja.imsdk.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.xdja.imsdk.db.bean.RawFileDb;
import com.xdja.imsdk.db.builder.RawFileBuilder;
import com.xdja.imsdk.db.helper.UpdateArgs;

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

public class RawFileDao extends AbstractDao <RawFileDb> {
    private static RawFileDao instance;

    private RawFileDao() {
        super();
    }

    public static RawFileDao getInstance() {
        if (instance == null) {
            synchronized (RawFileDao.class) {
                if (instance == null) {
                    instance = new RawFileDao();
                }
            }
        }

        return instance;
    }

    /**
     * 插入，已开启事务
     * @param db db
     * @param entry entry
     * @return long
     */
    public long insert(SQLiteDatabase db, RawFileDb entry) {
        return insert(db, entry, RawFileBuilder.insertSql());
    }

    /**
     * 批量插入，需开启事务
     * @param list list
     */
    public void insertBatchUpgrade(SQLiteDatabase db, List<RawFileDb> list) {
        insertBatchUpgrade(db, list, RawFileBuilder.insertSql());
    }

    /**
     * 更新
     * @param args args
     */
    public void updateR(UpdateArgs args) {
        update(args);
    }

    @Override
    protected RawFileDb readEntry(Cursor cursor, int offset) {
        RawFileDb entity = new RawFileDb(
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0),
                cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1),
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2),
                cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3),
                cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4),
                cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5),
                cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6),
                cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7),
                cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8),
                cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9)
        );
        return entity;
    }

    @Override
    protected void bindValues(SQLiteStatement stmt, RawFileDb entity) {
        stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }

        Long raw_msg_id = entity.getRaw_msg_id();
        if (raw_msg_id != null) {
            stmt.bindLong(2, raw_msg_id);
        }

        String raw_file_path = entity.getRaw_file_path();
        if (raw_file_path != null) {
            stmt.bindString(3, raw_file_path);
        }

        String raw_encrypt_path = entity.getRaw_encrypt_path();
        if (raw_encrypt_path != null) {
            stmt.bindString(4, raw_encrypt_path);
        }

        String raw_file_name = entity.getRaw_file_name();
        if (raw_file_name != null) {
            stmt.bindString(5, raw_file_name);
        }

        Long raw_file_size = entity.getRaw_file_size();
        if (raw_file_size != null) {
            stmt.bindLong(6, raw_file_size);
        }

        Long raw_encrypt_size = entity.getRaw_encrypt_size();
        if (raw_encrypt_size != null) {
            stmt.bindLong(7, raw_encrypt_size);
        }

        Long raw_translate_size = entity.getRaw_translate_size();
        if (raw_translate_size != null) {
            stmt.bindLong(8, raw_translate_size);
        }

        String raw_fid = entity.getRaw_fid();
        if (raw_fid != null) {
            stmt.bindString(9, raw_fid);
        }

        Integer raw_state = entity.getRaw_state();
        if (raw_state != null) {
            stmt.bindLong(10, raw_state);
        }
    }
}
