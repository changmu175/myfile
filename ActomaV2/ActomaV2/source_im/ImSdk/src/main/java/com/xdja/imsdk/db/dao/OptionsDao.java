package com.xdja.imsdk.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.xdja.imsdk.db.bean.OptionsDb;
import com.xdja.imsdk.db.builder.OptionsBuilder;

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

public class OptionsDao extends AbstractDao <OptionsDb> {
    private static OptionsDao instance;

    private OptionsDao() {
        super();
    }

    public static OptionsDao getInstance() {
        if (instance == null) {
            synchronized (OptionsDao.class) {
                if (instance == null) {
                    instance = new OptionsDao();
                }
            }
        }
        return instance;
    }

    /**
     * 批量保存配置项
     * @param options 配置项
     */
    public void insertBatch(List<OptionsDb> options) {
        insertBatch(options, OptionsBuilder.insertSql());
    }


    /**
     * 查询所有配置
     * @return 所有配置信息
     */
    public List<OptionsDb> getAll() {
        List<OptionsDb> optionList = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = query(OptionsBuilder.queryAll());
            while (cursor != null && cursor.moveToNext()) {
                optionList.add(readEntry(cursor, 0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
        }
        return optionList;

    }

    @Override
    protected void bindValues(SQLiteStatement stmt, OptionsDb entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }

        String property = entity.getProperty();
        if (property != null) {
            stmt.bindString(2, property);
        }

        String value = entity.getValue();
        if (value != null) {
            stmt.bindString(3, value);
        }

    }
    @Override
    protected OptionsDb readEntry(Cursor cursor, int offset) {
        OptionsDb entity = new OptionsDb(
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0),
                cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1),
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2)
        );
        return entity;
    }
}
