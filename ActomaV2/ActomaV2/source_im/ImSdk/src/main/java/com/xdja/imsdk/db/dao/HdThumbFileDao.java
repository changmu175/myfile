package com.xdja.imsdk.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.xdja.imsdk.db.bean.HdThumbFileDb;
import com.xdja.imsdk.db.builder.HdThumbFileBuilder;
import com.xdja.imsdk.db.helper.UpdateArgs;

import java.util.List;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：                                               <br>
 * 创建时间：2016/11/27 下午3:06                              <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public class HdThumbFileDao extends AbstractDao <HdThumbFileDb> {
    private static HdThumbFileDao instance;

    private HdThumbFileDao() {
        super();
    }

    public static HdThumbFileDao getInstance() {
        if (instance == null) {
            synchronized (HdThumbFileDao.class) {
                if (instance == null) {
                    instance = new HdThumbFileDao();
                }
            }
        }
        return instance;
    }

    /**
     * 插入
     * @param db db
     * @param entry entry
     * @return long
     */
    public long insert(SQLiteDatabase db, HdThumbFileDb entry) {
        return insert(db, entry, HdThumbFileBuilder.insertSql());
    }

    /**
     * 批量插入， 需开启事务
     * @param list list
     */
    public void insertBatchUpgrade(SQLiteDatabase db, List<HdThumbFileDb> list) {
        insertBatchUpgrade(db, list, HdThumbFileBuilder.insertSql());
    }

    /**
     * 更新
     * @param args args
     */
    public void updateH(UpdateArgs args) {
        update(args);
    }

    @Override
    protected HdThumbFileDb readEntry(Cursor cursor, int offset) {
        HdThumbFileDb entity = new HdThumbFileDb(
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
    protected void bindValues(SQLiteStatement stmt, HdThumbFileDb entity) {
        stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }

        Long hd_msg_id = entity.getHd_msg_id();
        if (hd_msg_id != null) {
            stmt.bindLong(2, hd_msg_id);
        }

        String hd_file_path = entity.getHd_file_path();
        if (hd_file_path != null) {
            stmt.bindString(3, hd_file_path);
        }

        String hd_encrypt_path = entity.getHd_encrypt_path();
        if (hd_encrypt_path != null) {
            stmt.bindString(4, hd_encrypt_path);
        }

        String hd_file_name = entity.getHd_file_name();
        if (hd_file_name != null) {
            stmt.bindString(5, hd_file_name);
        }

        Long hd_file_size = entity.getHd_file_size();
        if (hd_file_size != null) {
            stmt.bindLong(6, hd_file_size);
        }

        Long hd_encrypt_size = entity.getHd_encrypt_size();
        if (hd_encrypt_size != null) {
            stmt.bindLong(7, hd_encrypt_size);
        }

        Long hd_translate_size = entity.getHd_translate_size();
        if (hd_translate_size != null) {
            stmt.bindLong(8, hd_translate_size);
        }

        String hd_fid = entity.getHd_fid();
        if (hd_fid != null) {
            stmt.bindString(9, hd_fid);
        }

        Integer hd_state = entity.getHd_state();
        if (hd_state != null) {
            stmt.bindLong(10, hd_state);
        }
    }
}
