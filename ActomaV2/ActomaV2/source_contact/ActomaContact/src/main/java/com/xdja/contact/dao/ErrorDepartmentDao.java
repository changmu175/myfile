package com.xdja.contact.dao;

import com.xdja.contact.bean.ErrorDepartment;
import com.xdja.contact.database.columns.TableActomaAccount;
import com.xdja.contact.database.columns.error.TableErrorDepartment;
import com.xdja.contact.exception.ContactDaoException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghao on 2015/7/15.
 */
public class ErrorDepartmentDao extends AbstractContactDao<ErrorDepartment> {


    public ErrorDepartment query(String id) {
        ErrorDepartment errorDepartment = null;
        try {
            cursor = database.query(TableErrorDepartment.TABLE_NAME, null, TableErrorDepartment.ID + "=?", new String[]{id}, null, null, null);
            if (cursor.moveToFirst()) {
                errorDepartment = new ErrorDepartment(cursor);
            }
        }catch (Exception e) {
            new ContactDaoException(e);
        }finally {
            closeCursor();
        }
        return errorDepartment;
    }

    /**
     * 查询 by memberId
     *
     * @param memberId
     * @return
     */
    public ErrorDepartment queryByMemberId(String memberId) {
        ErrorDepartment result = null;
        try {
            String[] whereArgs = new String[]{memberId};
            String sql = "select * from " + TableErrorDepartment.TABLE_NAME + " where " + TableErrorDepartment.MEMBER_ID + " = ?";
            cursor = database.rawQuery(sql, whereArgs);
            if(cursor.moveToFirst()){
                result = new ErrorDepartment(cursor);
            }
        } catch (Exception e) {
            new ContactDaoException(e);
        } finally {
            closeCursor();
        }
        return result;

    }

    /**
     * 查询 by departmentId
     *
     * @param departmentId
     * @return
     */
    public ErrorDepartment queryByDepartmentId(String departmentId) {
        ErrorDepartment result = null;
        try {
            String[] whereArgs = new String[]{departmentId};
            String sql = "select * from " + TableErrorDepartment.TABLE_NAME + " where " + TableErrorDepartment.DEPT_ID + " =? ";
            cursor = database.rawQuery(sql, whereArgs);
            if(cursor.moveToFirst()) {
                result = new ErrorDepartment(cursor);
            }
        } catch (Exception e) {
            new ContactDaoException(e);
        } finally {
            closeCursor();
        }
        return result;
    }

    /**
     * 查询所有错误部门数据
     *
     * @return
     */
    public List<ErrorDepartment> queryAll() {
        List<ErrorDepartment> dataSource = new ArrayList<ErrorDepartment>();
        try {
            cursor = database.query(getTableName(),null,null,null,null,null,null);
            while (cursor.moveToNext()) {
                ErrorDepartment department = new ErrorDepartment(cursor);
                dataSource.add(department);
            }
        } catch (Exception e) {
            new ContactDaoException(e);
        } finally {
            closeCursor();
        }
        return dataSource;
    }

    @Override
    protected String getTableName() {
        return TableErrorDepartment.TABLE_NAME;
    }

    /**
     * 保存所有错误的部门数据
     *
     * @param errorDepartment
     * @return
     */
    public long insert(ErrorDepartment errorDepartment) {
        long result = -1;
        try {
            errorDepartment.setCreateTime(String.valueOf(System.currentTimeMillis()));
            result = database.insert(TableErrorDepartment.TABLE_NAME, null, errorDepartment.getContentValues());
        } catch (Exception e) {
            new ContactDaoException(e);
        }
        return result;
    }

    /**
     * 更新错误的部门数据
     *
     * @param errorDepartment
     * @return
     */
    public int update(ErrorDepartment errorDepartment) {
        int result = 0;
        try {
            errorDepartment.setUpdateTime(String.valueOf(System.currentTimeMillis()));
            String whereClause = TableErrorDepartment.DEPT_ID + " =  ? ";
            result = database.update(TableErrorDepartment.TABLE_NAME, errorDepartment.getContentValues(), whereClause, new String[]{errorDepartment.getDeptId()});
        } catch (Exception e) {
            new ContactDaoException(e);
        }
        return result;
    }

    /**
     * 删除错误的部门数据
     *
     * @param errorDepartment
     * @return
     */
    public int delete(ErrorDepartment errorDepartment) {
        int result = 0;
        try {
            String whereClause = TableActomaAccount.ACCOUNT + " = ? ";
            String whereArgs = errorDepartment.getDeptId();
            result = database.delete(TableActomaAccount.TABLE_NAME, whereClause, new String[]{whereArgs});
        } catch (Exception e) {
            new ContactDaoException(e);
        }
        return result;
    }
}
