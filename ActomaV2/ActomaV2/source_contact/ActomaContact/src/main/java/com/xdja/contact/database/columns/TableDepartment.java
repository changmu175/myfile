package com.xdja.contact.database.columns;

/**
 * Created by wanghao on 2015/7/7.
 */
public class TableDepartment extends  AbsTableOperate{

    /**
     * 通讯录部门表名
     */
    public static final String TABLE_NAME = "t_department";
    /**
     * 自增id
     */
//    public static final String ID = "_id";
    /**
     * 部门名称
     */
    public static final String DEPT_NAME = "c_dept_name";
    /**
     * 父部门
     */
    public static final String SUPER_DEPT_ID = "c_super_dept_id";
    /**
     * 部门排序
     */
    public static final String SORT = "c_sort";
    /**
     * 部门id
     */
    public static final String DEPT_ID = "c_dept_id";




    @Override
    protected Class getCls() {
        return TableDepartment.class;
    }
}
