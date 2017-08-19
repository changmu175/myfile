package com.xdja.imsdk.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.xdja.imsdk.db.bean.FileMsgDb;
import com.xdja.imsdk.db.builder.FileMsgBuilder;
import com.xdja.imsdk.db.helper.UpdateArgs;

import java.util.List;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：                                               <br>
 * 创建时间：2016/11/27 下午3:06                           <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public class FileMsgDao extends AbstractDao <FileMsgDb> {
    private static FileMsgDao instance;

    private FileMsgDao() {
        super();
    }

    public static FileMsgDao getInstance() {
        if (instance == null) {
            synchronized (FileMsgDao.class) {
                if (instance == null) {
                    instance = new FileMsgDao();
                }
            }
        }
        return instance;
    }

    /**
     * 保存文件
     * @param fileMsgDb 文件
     * @return id
     */
    public long insert(SQLiteDatabase db, FileMsgDb fileMsgDb) {
        return insert(db, fileMsgDb, FileMsgBuilder.insertSql());
    }

    /**
     * 批量保存文件，需开启事务
     * @param files files
     */
    public void insertBatchUpgrade(SQLiteDatabase db, List<FileMsgDb> files) {
        if (files == null || files.isEmpty()) {
            return;
        }

        insertBatchUpgrade(db, files, FileMsgBuilder.insertSql());
    }

    /**
     * 更新
     * @param args args
     */
    public void updateF(UpdateArgs args) {
        update(args);
    }

    @Override
    protected void bindValues(SQLiteStatement stmt, FileMsgDb entity) {
        stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }

        String file_path = entity.getFile_path();
        if (file_path != null) {
            stmt.bindString(2, file_path);
        }

        String entityFile_path = entity.getEncrypt_path();
        if (entityFile_path != null) {
            stmt.bindString(3, entityFile_path);
        }

        String file_name = entity.getFile_name();
        if (file_name != null) {
            stmt.bindString(4, file_name);
        }

        Long file_size = entity.getFile_size();
        if (file_size != null) {
            stmt.bindLong(5, file_size);
        }

        Long encrypt_size = entity.getEncrypt_size();
        if (encrypt_size != null) {
            stmt.bindLong(6, encrypt_size);
        }

        Long translate_size = entity.getTranslate_size();
        if (translate_size != null) {
            stmt.bindLong(7, translate_size);
        }

        String suffix = entity.getSuffix();
        if (suffix != null) {
            stmt.bindString(8, suffix);
        }

        String fid = entity.getFid();
        if (fid != null) {
            stmt.bindString(9, fid);
        }

        Integer file_state = entity.getFile_state();
        if (file_state != null) {
            stmt.bindLong(10, file_state);
        }

        Long msg_id = entity.getMsg_id();
        if (msg_id != null) {
            stmt.bindLong(11, msg_id);
        }

        Integer type = entity.getType();
        if (type != null) {
            stmt.bindLong(12, type);
        }

        String extra_info = entity.getExtra_info();
        if (extra_info != null) {
            stmt.bindString(13, extra_info);
        }
    }

    @Override
    protected FileMsgDb readEntry(Cursor cursor, int offset) {
        FileMsgDb entity = new FileMsgDb(
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0),
                cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1),
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2),
                cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3),
                cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4),
                cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5),
                cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6),
                cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7),
                cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8),
                cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9),
                cursor.isNull(offset + 10) ? null : cursor.getLong(offset + 10),
                cursor.isNull(offset + 11) ? null : cursor.getInt(offset + 11),
                cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12)
        );
        return entity;
    }
}
