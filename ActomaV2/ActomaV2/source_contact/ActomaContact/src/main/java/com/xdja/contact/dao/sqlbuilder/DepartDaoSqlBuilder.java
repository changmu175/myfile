package com.xdja.contact.dao.sqlbuilder;

import android.text.TextUtils;

import com.xdja.contact.bean.Department;
import com.xdja.contact.database.columns.TableDepartment;

/**
 * Created by yangpeng on 2015/12/24.
 */
public class DepartDaoSqlBuilder {

    public static final String insertSql(Department department){
//        String sql = "replace into " + TableDepartment.TABLE_NAME + "(" + TableDepartment.DEPT_ID + "," + TableDepartment.DEPT_NAME + ","
//                + TableDepartment.SORT + "," + TableDepartment.SUPER_DEPT_ID + ")" +
//                " values(" + checkColumnNull(department.getDepartmentId()) + "," + checkColumnNull(department.getDepartmentName()) + "," +
//                checkColumnNull(department.getSort()) + "," + checkColumnNull(department.getSuperId()) + ")";
//        return sql;
        StringBuilder sql = new StringBuilder();
        sql.append(" replace into "+ TableDepartment.TABLE_NAME);
        sql.append(" ( ");
        sql.append(TableDepartment.DEPT_ID);
        sql.append(",");
        sql.append(TableDepartment.DEPT_NAME);
        sql.append(",");
        sql.append(TableDepartment.SORT);
        sql.append(",");
        sql.append(TableDepartment.SUPER_DEPT_ID);
        sql.append(" ) ");
        sql.append(" values ( ");
        sql.append(checkColumnNull(department.getDepartmentId()));
        sql.append(",");
        sql.append(checkColumnNull(department.getDepartmentName()));
        sql.append(",");
        sql.append(checkColumnNull(department.getSort()));
        sql.append(",");
        sql.append(checkColumnNull(department.getSuperId()));
        sql.append(" ) ");
        return sql.toString();

    }

    public static String checkColumnNull(String data){
        return TextUtils.isEmpty(data) ? "NULL" : "'"+data+"'";    }

    public static final String updateSql(Department department) {
        StringBuilder sql = new StringBuilder();
        sql.append("update "+ TableDepartment.TABLE_NAME);
        sql.append(" set ");
        sql.append(TableDepartment.SUPER_DEPT_ID);
        sql.append(" = ");
        sql.append(checkColumnNull(department.getSuperId()));
        sql.append(" , ");
        sql.append(TableDepartment.SORT);
        sql.append(" = ");
        sql.append(checkColumnNull(department.getSort()));
        sql.append(" , ");
        sql.append(TableDepartment.DEPT_NAME);
        sql.append(" = ");
        sql.append(checkColumnNull(department.getDepartmentName()));
        sql.append(" , ");
        sql.append(TableDepartment.DEPT_ID);
        sql.append(" = ");
        sql.append(checkColumnNull(department.getDepartmentId()));
        sql.append(" where ");
        sql.append(TableDepartment.DEPT_ID);
        sql.append(" = ");
        sql.append(department.getDepartmentId());
//        String sql = " update " + TableDepartment.TABLE_NAME + " set " + TableDepartment.DEPT_NAME + "=" + checkColumnNull(department.getDepartmentName()) + ", "
//                +TableDepartment.SORT + "=" + checkColumnNull(department.getSort()) + "," + TableDepartment.SUPER_DEPT_ID + "=" + checkColumnNull(department.getSuperId())
//                + " where " + where;
        return sql.toString();
    }


    public static final String deleteSql(Department department) {
        StringBuilder sql = new StringBuilder();
        sql.append("delete from ");
        sql.append(TableDepartment.TABLE_NAME);
        sql.append(" where ");
        sql.append(TableDepartment.DEPT_ID);
        sql.append(" = ");
        sql.append(department.getDepartmentId());

        return sql.toString();
    }

    public static final String getDepartmentByParentIdSql(String parentId){
        StringBuilder selection = new StringBuilder();
        if (TextUtils.isEmpty(parentId)) {
            selection.append(TableDepartment.SUPER_DEPT_ID);
            selection.append(" =(");
            selection.append(" select ");
            selection.append(TableDepartment.DEPT_ID);
            selection.append(" from "+ TableDepartment.TABLE_NAME);
            selection.append(" where ");
            selection.append(TableDepartment.SUPER_DEPT_ID);
            selection.append(" = ");
            selection.append(" '' ");
            selection.append(" or ");
            selection.append(TableDepartment.SUPER_DEPT_ID);
            selection.append(" ISNULL ");
            selection.append(" or ");
            selection.append(TableDepartment.SUPER_DEPT_ID);
            selection.append(" = ");
            selection.append(" 0 )");
        }else{
            selection.append(TableDepartment.SUPER_DEPT_ID);
            selection.append(" = ");
            selection.append(parentId);
        }
        StringBuffer orderBy = new StringBuffer();
        orderBy.append(" order by cast ( ");
        orderBy.append(TableDepartment.SORT);
        orderBy.append(" as int ) asc ");
        selection.append(orderBy);
        return selection.toString();
    }

    public static final String exitSql(){
        StringBuilder sql = new StringBuilder();
        sql.append("select * from ");
        sql.append("sqlite_master ");
        sql.append(" where name");
        sql.append(" = '");
        sql.append(TableDepartment.TABLE_NAME);
        sql.append("' ");//modify by wal@xdja.com for 1880
        return sql.toString();
    }
}
