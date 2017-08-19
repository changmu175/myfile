package com.xdja.contact.service;

import android.content.Context;

import com.xdja.contact.bean.Department;
import com.xdja.contact.callback.DatabaseListener;
import com.xdja.contact.dao.DepartDao;

import java.util.List;

/**
 * @author hkb.
 * @since 2015/7/17/0017.
 */
public class DepartService {

    private DepartDao departDao;

    public DepartService(Context context) {
        departDao = new DepartDao();
    }

    /**
     * 查询所有部门信息
     *
     * @return
     */
    public List<Department> getDepartmentAll() {
        List<Department> departments  = null;
        synchronized (departDao.helper) {
            departDao.getWriteDataBase();
             departments = departDao.queryAll();
            departDao.closeDataBase();
        }
        return departments;
    }

    public Department getDepartmentById(String departid){
        Department department  = null;
        synchronized (departDao.helper) {
            departDao.getWriteDataBase();
            department = departDao.query(departid);
            departDao.closeDataBase();
        }
        return department;
    }
    public Department getDepartmentById(String departid,boolean isOpenCloseDB){
        Department department  = null;
        synchronized (departDao.helper) {
            departDao.getWriteDataBase();
            department = departDao.query(departid);
            if(isOpenCloseDB){
                departDao.closeDataBase();
            }
        }
        return department;
    }

    /**
     * 获取根目录部门
     *
     * @return
     */
    public List<Department> getRootDepartment() {
        List<Department> departmentByParentId = null;
        synchronized (departDao.helper){
            departDao.getWriteDataBase();
            departmentByParentId = departDao.getDepartmentByParentId(null);
            departDao.closeDataBase();
        }
        return departmentByParentId;
    }

    /**
     * 获取部门下的子部门信息
     *
     * @param departId 部门id
     * @return
     */
    public List<Department> getChildDepartment(String departId) {
        List<Department> departmentByParentId = null;
        synchronized (departDao.helper){
            departDao.getWriteDataBase();
            departmentByParentId = departDao.getDepartmentByParentId(departId);
            departDao.closeDataBase();
        }
        return departmentByParentId;
    }

    public void insert(List<Department> members, DatabaseListener databaseListener) {
        synchronized (departDao.helper) {
            departDao.getWriteDataBase();
            departDao.insert(members, databaseListener);
            departDao.closeDataBase();
        }
    }

    public void delete(List<Department> departments) {
        synchronized (departDao.helper) {
            departDao.getWriteDataBase();
            departDao.delete(departments);
            departDao.closeDataBase();
        }
    }


    public boolean existTable(){
        boolean isExist = false;
        //add by lwl 2177 synchronized
        synchronized (departDao.helper) {
            departDao.getWriteDataBase();
            isExist = departDao.exits();
            departDao.closeDataBase();
        }
        return isExist;
    }




    //删除部门表和部门成员表
    public boolean deleteDeparment(){
        boolean result = false;
        //add by lwl 2177 synchronized
        synchronized (departDao.helper) {
            departDao.getWriteDataBase();
            result = departDao.deleteTableData();
            departDao.closeDataBase();
        }
        return result;
    }
}
