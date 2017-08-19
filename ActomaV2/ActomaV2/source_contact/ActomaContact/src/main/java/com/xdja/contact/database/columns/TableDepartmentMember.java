package com.xdja.contact.database.columns;

/**
 * Created by wanghao on 2015/7/7.
 */
public class TableDepartmentMember extends AbsTableOperate {

    /**
     * 通讯录部门成员表名
     */
    public static final String TABLE_NAME = "t_depart_members";
    /**
     * 部门id
     */
    public static final String MEMBER_DEPT_ID = "c_dept_id";
    /**
     * 名称
     */
    public static final String NAME = "c_name";
    /**
     * 部门之内排序
     */
    public static final String SORT = "c_sort";
    /**
     * 名称简拼
     */
    public static final String NAME_PY = "c_name_py";
    /**
     * 名称全拼
     */
    public static final String NAME_FULL_PY = "c_name_full_py";
    /**
     * 安通账号
     */
    public static final String ACCOUNT = "c_account";
    /**
     * 电话(可能存在多个电话) 区别于 TableActomaAccount#BINDE_PHONE
     */
    public static final String PHONE = "c_phone";
    /**
     * 工号
     */
    public static final String WORKER_ID = "c_worker_id";



    @Override
    protected Class getCls() {
        return TableDepartmentMember.class;
    }
}
