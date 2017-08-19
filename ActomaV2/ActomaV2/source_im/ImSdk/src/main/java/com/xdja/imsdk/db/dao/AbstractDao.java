package com.xdja.imsdk.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.xdja.imsdk.constant.ImSdkResult;
import com.xdja.imsdk.db.DbHelper;
import com.xdja.imsdk.db.helper.SqlBuilder;
import com.xdja.imsdk.db.helper.UpdateArgs;
import com.xdja.imsdk.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：                                               <br>
 * 创建时间：2016/11/27 上午12:42                          <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public abstract class AbstractDao<T> {
    protected static final Object DB_LOCK = new Object();

    public AbstractDao() {

    }

    /**
     * 需开启事务，单条保存
     * @param entry entry
     * @param sql sql
     * @return id
     */
    protected long insert(T entry, String sql) {
        return executeInsert(entry, sql);
    }

    /**
     * 需开启事务，批量保存
     * @param entries entries
     * @param sql sql
     */
    protected void insertBatch(Iterable<T> entries, String sql) {
        executeInsertBatch(entries, sql);
    }

    protected void insertBatchUpgrade(SQLiteDatabase db, Iterable<T> entries, String sql) {
        executeUpgradeInsertBatch(db, entries, sql);
    }

    /**
     * 需开启事务，删除
     * @param sql 删除语句
     * @return int
     */
    protected int delete(String sql) {
        return executeDelete(sql);
    }

    /**
     * 需开启事务，单条刷新
     * @param args 更新参数
     */
    protected void update(UpdateArgs args) {
        String tableName = args.getName();
        ContentValues values = args.getValues();
        String whereClause[] = args.getWhereClause();
        String whereArgs[] = args.getWhereArgs();

        int setValuesSize = values.size();
        int bindArgsSize = (whereArgs == null) ? setValuesSize :
                (setValuesSize + whereArgs.length);
        String[] bindArgs = new String[bindArgsSize];
        String[] valuesColumns = new String[setValuesSize];
        int i = 0;

        for (String colName : values.keySet()) {
            valuesColumns[i] = colName;
            bindArgs[i++] = String.valueOf(values.get(colName));
        }

        if (whereArgs != null) {
            for (String where : whereArgs) {
                bindArgs[i++] = where;
            }
        }

        String sql = SqlBuilder.updateSql(tableName, valuesColumns, whereClause);
        executeUpdate(sql, bindArgs);
    }

    /**
     * 需开启事务，批量刷新
     * @param sql 刷新语句
     * @param args 更新参数
     */
    protected void updateBatch(String sql, List<UpdateArgs> args) {
        if (args == null || args.isEmpty()) {
            return;
        }

        List<String[]> bindArgsList = new ArrayList<>();
        for (UpdateArgs arg:args) {
            ContentValues values = arg.getValues();
            String[] whereArgs = arg.getWhereArgs();
            int setValuesSize = values.size();
            int bindArgsSize = (whereArgs == null) ? setValuesSize :
                    (setValuesSize + whereArgs.length);
            String[] bindArgs = new String[bindArgsSize];
            int i = 0;

            for (String colName : values.keySet()) {
                bindArgs[i++] = String.valueOf(values.get(colName));
            }

            if (whereArgs != null) {
                for (String where : whereArgs) {
                    bindArgs[i++] = where;
                }
            }

            bindArgsList.add(bindArgs);
        }

        executeUpdateBatch(sql, bindArgsList);
    }

    /**
     * 已开启事务，单条保存
     * @param db db
     * @param entry entry
     * @param sql sql
     * @return id
     */
    protected long insert(SQLiteDatabase db, T entry, String sql) {
        return executeInsert(db, entry, sql);
    }

    /**
     * 已开启事务，批量保存
     * @param entries entries
     * @param sql sql
     */
    protected void insertBatch(SQLiteDatabase db, Iterable<T> entries, String sql) {
        executeInsertBatch(db, entries, sql);
    }

    /**
     * 已开启事务，单条更新
     * @param db db
     * @param args args
     */
    protected void update(SQLiteDatabase db, UpdateArgs args) {
        if (args == null) {
            return;
        }
        String tableName = args.getName();
        ContentValues values = args.getValues();
        String whereClause[] = args.getWhereClause();
        String whereArgs[] = args.getWhereArgs();

        int setValuesSize = values.size();
        int bindArgsSize = (whereArgs == null) ? setValuesSize :
                (setValuesSize + whereArgs.length);
        String[] bindArgs = new String[bindArgsSize];
        String[] valuesColumns = new String[setValuesSize];
        int i = 0;

        for (String colName : values.keySet()) {
            valuesColumns[i] = colName;
            bindArgs[i++] = String.valueOf(values.get(colName));
        }

        if (whereArgs != null) {
            for (String where : whereArgs) {
                bindArgs[i++] = where;
            }
        }

        String sql = SqlBuilder.updateSql(tableName, valuesColumns, whereClause);
        executeUpdate(db, sql, bindArgs);
    }

    /**
     * 已开启事务，批量刷新
     * @param db db
     * @param sql 刷新语句
     * @param args 更新参数
     */
    protected void updateBatch(SQLiteDatabase db, String sql, List<UpdateArgs> args) {
        if (args == null || args.isEmpty()) {
            return;
        }

        List<String[]> bindArgsList = new ArrayList<>();
        for (UpdateArgs arg:args) {
            ContentValues values = arg.getValues();
            String[] whereArgs = arg.getWhereArgs();
            int setValuesSize = values.size();
            int bindArgsSize = (whereArgs == null) ? setValuesSize :
                    (setValuesSize + whereArgs.length);
            String[] bindArgs = new String[bindArgsSize];
            int i = 0;

            for (String colName : values.keySet()) {
                bindArgs[i++] = values.get(colName) + "";
            }

            if (whereArgs != null) {
                for (String where : whereArgs) {
                    bindArgs[i++] = where;
                }
            }

            bindArgsList.add(bindArgs);
        }

        executeUpdateBatch(db, sql, bindArgsList);
    }

    /**
     * 查询
     * @param sql 查询语句
     * @return cursor
     */
    protected Cursor query(String sql) {
        return executeQuery(sql);
    }

    /**
     * 关闭cursor
     * @param cursor cursor
     */
    protected void closeCursor(Cursor cursor) {
        synchronized (DB_LOCK) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * 需开启事务，插入
     * @param entity entity
     * @param sql sql
     * @return long
     */
    private long executeInsert(T entity, String sql) {
        long rowId = 0;
        synchronized (DB_LOCK) {
            try {
                SQLiteDatabase db = DbHelper.getInstance().getDatabase();
                SQLiteStatement stmt = db.compileStatement(sql);
                db.beginTransaction();
                try {
                    bindValues(stmt, entity);
                    rowId = stmt.executeInsert();
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return ImSdkResult.RESULT_FAIL_DATABASE;
                } finally {
                    db.endTransaction();
                }
            } catch (Exception e) {
//				e.printStackTrace();
                Logger.getLogger().e("database already closed !!!");
            }
        }

        return rowId;
    }

    /**
     * 已开启事务，插入
     * @param db db
     * @param entity entity
     * @param sql sql
     * @return long
     */
    private long executeInsert(SQLiteDatabase db, T entity, String sql) {
        long rowId = 0;
        try {
            SQLiteStatement stmt = db.compileStatement(sql);
            bindValues(stmt, entity);
            rowId = stmt.executeInsert();
        } catch (Exception e) {
//            e.printStackTrace();
            Logger.getLogger().e("database already closed !!!");
        }
        return rowId;
    }

    private void executeUpgradeInsertBatch(SQLiteDatabase db, Iterable<T> entities, String sql) {
        synchronized (DB_LOCK) {
            try {
                SQLiteStatement stmt = db.compileStatement(sql);
                db.beginTransaction();
                try {
                    for (T entity : entities) {
                        bindValues(stmt, entity);
                        stmt.executeInsert();
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            } catch (Exception e) {
//                e.printStackTrace();
                Logger.getLogger().e("database already closed !!!");
            }
        }
    }

    private void executeInsertBatch(Iterable<T> entities, String sql) {
        synchronized (DB_LOCK) {
            try {
                SQLiteDatabase db = DbHelper.getInstance().getDatabase();
                SQLiteStatement stmt = db.compileStatement(sql);
                db.beginTransaction();
                try {
                    for (T entity : entities) {
                        bindValues(stmt, entity);
                        stmt.executeInsert();
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            } catch (Exception e) {
//				e.printStackTrace();
                Logger.getLogger().e("database already closed !!!");
            }
        }
    }

    private void executeInsertBatch(SQLiteDatabase db, Iterable<T> entities, String sql) {
        try {
            SQLiteStatement stmt = db.compileStatement(sql);
            for (T entity : entities) {
                bindValues(stmt, entity);
                stmt.executeInsert();
            }
        } catch (Exception e) {
//				e.printStackTrace();
            Logger.getLogger().e("database already closed !!!");
        }
    }

    private int executeDelete(String sql) {
        synchronized (DB_LOCK) {
            try {
                SQLiteDatabase db = DbHelper.getInstance().getDatabase();
                db.beginTransaction();
                try {
                    db.execSQL(sql);
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return ImSdkResult.RESULT_FAIL_DATABASE;
                } finally {
                    db.endTransaction();
                }
            } catch (Exception e) {
//				e.printStackTrace();
                Logger.getLogger().e("database already closed !!!");
            }
            return ImSdkResult.RESULT_OK;
        }
    }

    private void executeUpdate(String sql, String[] bindArgs) {
        synchronized (DB_LOCK) {
            try {
                SQLiteDatabase db = DbHelper.getInstance().getDatabase();
                SQLiteStatement stmt = db.compileStatement(sql);
                db.beginTransaction();
                try {
                    stmt.bindAllArgsAsStrings(bindArgs);
                    stmt.execute();
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    db.endTransaction();
                }
            } catch (Exception e) {
//				e.printStackTrace();
                Logger.getLogger().e("database already closed !!!");
            }
        }
    }

    private void executeUpdate(SQLiteDatabase db, String sql, String[] bindArgs) {
        try {
            SQLiteStatement stmt = db.compileStatement(sql);
            stmt.bindAllArgsAsStrings(bindArgs);
            stmt.execute();
        } catch (Exception e) {
//				e.printStackTrace();
            Logger.getLogger().e("database already closed !!!");
        }
    }

    private void executeUpdateBatch(String sql, List<String[]> bindArgsList) {
        synchronized (DB_LOCK) {
            try {
                SQLiteDatabase db = DbHelper.getInstance().getDatabase();
                SQLiteStatement stmt = db.compileStatement(sql);
                db.beginTransaction();
                try {
                    for (String[] bindArgs : bindArgsList) {
                        stmt.bindAllArgsAsStrings(bindArgs);
                        stmt.execute();
//                        stmt.executeUpdateDelete();// TODO: 2016/12/14 liming test 此语句作用
                    }
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    db.endTransaction();
                }
            } catch (Exception e) {
//				e.printStackTrace();
                Logger.getLogger().e("database already closed !!!");
            }
        }
    }

    private void executeUpdateBatch(SQLiteDatabase db, String sql, List<String[]> bindArgsList) {
        try {
            SQLiteStatement stmt = db.compileStatement(sql);
            for (String[] bindArgs : bindArgsList) {
                stmt.bindAllArgsAsStrings(bindArgs);
                stmt.execute();
//                        stmt.executeUpdateDelete();// TODO: 2016/12/14 liming test 此语句作用
            }

        } catch (Exception e) {
//				e.printStackTrace();
            Logger.getLogger().e("database already closed !!!");
        }
    }

    private Cursor executeQuery(String sql) {
        Cursor cursor = null;
        synchronized (DB_LOCK) {
            try {
                String[] args = null;
                SQLiteDatabase db = DbHelper.getInstance().getDatabase();
                cursor = db.rawQuery(sql, args);
            } catch (Exception e) {
//				e.printStackTrace();
                Logger.getLogger().e("database already closed !!!");
            }
            return cursor;
        }
    }

    abstract protected T readEntry(Cursor cursor, int offset);

    abstract protected void bindValues(SQLiteStatement stmt, T entity);
}
