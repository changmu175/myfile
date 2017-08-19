package com.xdja.contact.dao;

import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import com.xdja.contact.bean.Department;
import com.xdja.contact.bean.ErrorDepartment;
import com.xdja.contact.callback.DatabaseListener;
import com.xdja.contact.dao.sqlbuilder.DepartDaoSqlBuilder;
import com.xdja.contact.database.columns.TableDepartment;
import com.xdja.contact.exception.ContactDaoException;
import com.xdja.comm.uitl.ObjectUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hkb on 2015/7/10.
 */
public class DepartDao extends AbstractContactDao<Department> {


    public Department query(String id) {
        try {
            cursor = database.query(TableDepartment.TABLE_NAME, null, TableDepartment.DEPT_ID + "=?", new String[]{id}, null, null, null);
            if (cursor.moveToFirst()) {
                return new Department(cursor);
            }
        }catch (Exception e){
            new ContactDaoException(e);
        }finally {
            closeCursor();
        }
        return null;
    }

    /**
     * 查询所有的部门
     *
     * @return
     */
    public List<Department> queryAll() {
        List<Department> result = new ArrayList<Department>();
        try {
            cursor = database.query(getTableName(),null,null,null,null,null,null);
            while (cursor.moveToNext()) {
                Department department = new Department(cursor);
                result.add(department);
            }
        } catch (Exception e) {
            new ContactDaoException(e);
        } finally {
            closeCursor();
        }
        return result;
    }

    @Override
    protected String getTableName() {
        return TableDepartment.TABLE_NAME;
    }

    /**
     * 保存部门
     *
     * @param baseContact
     * @return result > -1 : 保存成功 ； result < -1 ： 失败
     */
    public long insert(Department baseContact) {
        long result = -1;
        try {
            result = database.insert(TableDepartment.TABLE_NAME, null, baseContact.getContentValues());
        } catch (Exception e) {
            new ContactDaoException(e);
        }
        return result;
    }


    /**
     * 批量保存部门,提高效率
     *
     * @param departments
     */
    public void insert(List<Department> departments,DatabaseListener databaseListener) {
        ArrayList<String> sqls = new ArrayList<>();
        List<ErrorDepartment> errorDepartments = new ArrayList<>();
        for (Department department : departments) {
            if(ObjectUtil.objectIsEmpty(department)) continue;
            sqls.add(DepartDaoSqlBuilder.insertSql(department));
        }
        database.beginTransaction();
        for (int i = 0; i < sqls.size(); i++) {
            SQLiteStatement stat = database.compileStatement(sqls.get(i));
            long rowID = stat.executeInsert();
            if(rowID == -1){
                Department department = departments.get(i);
                ErrorDepartment errorHsitory = buildErrorHsitory(department, "insert");
                errorDepartments.add(errorHsitory);

            }
            if(databaseListener != null){
                databaseListener.onInsert(sqls.size(), i + 1);
            }
        }
        database.setTransactionSuccessful();
        database.endTransaction();
        //处理错误信息,将错误信息保存数据库
        dealWithErrorDepartment(errorDepartments);
    }

    /**
     * 批量更新部门,提高效率
     *
     * @param departments
     */
    public void update(List<Department> departments) {
        ArrayList<String> sqls = new ArrayList<>();
        List<ErrorDepartment> errorDepartments = new ArrayList<>();
        List<ErrorDepartment> results = new ArrayList<>();
        for (Department department : departments) {
            if(ObjectUtil.objectIsEmpty(department))continue;
            if(!ObjectUtil.stringIsEmpty(department.getDepartmentId())){
                sqls.add(DepartDaoSqlBuilder.updateSql(department));
            }else{
                continue;
            }
        }
        database.beginTransaction();
        for (int i = 0;i<sqls.size();i++) {
            SQLiteStatement stat = database.compileStatement(sqls.get(i));
            int rowId = stat.executeUpdateDelete();
            if(rowId == -1){
                Department department = departments.get(i);
                ErrorDepartment hsitory = buildErrorHsitory(department, "update");
                errorDepartments.add(hsitory);
            }
        }
        database.setTransactionSuccessful();
        database.endTransaction();
        //处理错误信息,将错误信息保存数据库
        dealWithErrorDepartment(errorDepartments);
    }

    /**
     * 处理部门错误信息
     * @param errorDepartments
     */
    private void dealWithErrorDepartment(List<ErrorDepartment> errorDepartments){
        ErrorDepartmentDao errorDepartmentDao = new ErrorDepartmentDao();
        for (ErrorDepartment errorDepartment : errorDepartments) {
            ErrorDepartment result = errorDepartmentDao.queryByMemberId(errorDepartment.getMemberId());
            if (result != null) {
                result.setMemberId(errorDepartment.getMemberId());
                result.setType(errorDepartment.getType());
                result.setReason(errorDepartment.getReason());
                result.setUpdateTime(String.valueOf(System.currentTimeMillis()));
                result.setCreateTime(errorDepartment.getCreateTime());
                errorDepartmentDao.update(result);
            } else {
                errorDepartmentDao.insert(errorDepartment);
            }
        }
    }

    private String checkColumnNull(String data){
        return TextUtils.isEmpty(data) ? "NULL" : "'"+data+"'";
    }

    /**
     * 更新部门
     *
     * @param baseContact
     * @return
     */
    public int update(Department baseContact) {
        int result = -1;
        try {
            result = database.update(TableDepartment.TABLE_NAME, baseContact.getContentValues(), null, null);
        } catch (Exception e) {
            new ContactDaoException(e);
        }
        return result;

    }

    public int delete(Department baseContact) {
        int result = -1;
        try {
            database.delete(TableDepartment.TABLE_NAME, TableDepartment.DEPT_ID+"=?", new String[]{baseContact.getDepartmentId()});
        } catch (Exception e) {
            new ContactDaoException(e);
        }
        return result;
    }
    public int delete(String departid) {
        int result = -1;
        try {
            database.delete(TableDepartment.TABLE_NAME, TableDepartment.DEPT_ID+"=?", new String[]{departid});
        } catch (Exception e) {
            new ContactDaoException(e);
        }
        return result;
    }

    /**
     * 批量更新部门,提高效率
     *
     * @param departments
     */
    public void delete(List<Department> departments) {
        ArrayList<String> sqls = new ArrayList<>();
        for (Department department : departments) {
            if(ObjectUtil.objectIsEmpty(department))continue;
            sqls.add(DepartDaoSqlBuilder.deleteSql(department));
        }
        database.beginTransaction();
        for (String sql : sqls) {
            SQLiteStatement stat = database.compileStatement(sql);
            stat.executeUpdateDelete();
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }


    public List<Department> getDepartmentByParentId(String parentId) {

//        String selection = null;
//        if (TextUtils.isEmpty(parentID)) {
//            selection = TableDepartment.SUPER_DEPT_ID + " =( " +
//                    " SELECT " +
//                    " c_dept_id " +
//                    " FROM " +
//                    " t_department " +
//                    " WHERE " +
//                    " c_super_dept_id = '' " +
//                    " OR c_super_dept_id ISNULL " +
//                    " OR c_super_dept_id = '0' " +
//                    " )";
//        } else {
//            selection = TableDepartment.SUPER_DEPT_ID + "= '" + parentID + "'";
//        }
        List<Department> departments = new ArrayList<>();
        try {
            cursor = database.query(TableDepartment.TABLE_NAME, null, DepartDaoSqlBuilder.getDepartmentByParentIdSql(parentId), null, null, null, null);
            while (cursor.moveToNext()) {
                Department department = new Department();
                department.setDepartmentId(cursor.getString(cursor.getColumnIndex(TableDepartment.DEPT_ID)));
                department.setDepartmentName(cursor.getString(cursor.getColumnIndex(TableDepartment.DEPT_NAME)));
                department.setSort(cursor.getString(cursor.getColumnIndex(TableDepartment.SORT)));
                department.setSuperId(cursor.getString(cursor.getColumnIndex(TableDepartment.SUPER_DEPT_ID)));
                departments.add(department);
            }
        }catch (Exception e){
            new ContactDaoException(e);
        }finally {
            closeCursor();
        }
       /* if (cursor != null) {
            List<Department> departments = new ArrayList<>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Department department = new Department();
                department.setDepartmentId(cursor.getString(cursor.getColumnIndex(TableDepartment.DEPT_ID)));
                department.setDepartmentName(cursor.getString(cursor.getColumnIndex(TableDepartment.DEPT_NAME)));
                department.setSort(cursor.getString(cursor.getColumnIndex(TableDepartment.SORT)));
                department.setSuperId(cursor.getString(cursor.getColumnIndex(TableDepartment.SUPER_DEPT_ID)));
                departments.add(department);
                cursor.moveToNext();
            }
            return departments;
        }*/
        return departments;
    }

    private ErrorDepartment buildErrorHsitory(Department department,String reason){
        ErrorDepartment errorDepartment = new ErrorDepartment();
        errorDepartment.setDeptId(department.getDepartmentId());
        errorDepartment.setReason(reason);
        errorDepartment.setType(reason);
        return errorDepartment;
    }



    public boolean exits(){
        boolean exits = false;
        try {
//            String sql = " select * from sqlite_master where name=" + "'" + getTableName() + "'";
            cursor = database.rawQuery(DepartDaoSqlBuilder.exitSql(), null);
            if(cursor.moveToFirst()){
                exits = true;
            }
        }catch (Exception e){
            new ContactDaoException(e);
        }finally {
            closeCursor();
        }
        return exits;
    }


    public boolean deleteTableData(){
        boolean deleteBool = false;
        try {
            //String sql = "delete from " + "'" + getTableName() + "'";
            int result = database.delete(getTableName(),null,null);
            if(result >= 0){
                deleteBool = true;
            }
        }catch (Exception e){
            new ContactDaoException(e);
        }
        return deleteBool;
    }
}
