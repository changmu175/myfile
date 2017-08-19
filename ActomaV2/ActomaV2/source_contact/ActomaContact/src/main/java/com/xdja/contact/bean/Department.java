package com.xdja.contact.bean;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.xdja.contact.database.columns.TableDepartment;


/**
 * Created by hkb.
 * 2015/7/8/0008.
 */
public class Department implements Parcelable,BaseContact {

    public static final String ADD = "1";

    public static final String MODIFY = "2";

    public static final String DELETE = "3";


//    private String id;//主键、自增、唯一
    private String departmentId;//部门id
    private String departmentName;//部门名称
    private String superId;//上级部门id
    private String sort;//同级部门排序

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getSuperId() {
        return superId;
    }

    public void setSuperId(String superId) {
        this.superId = superId;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(TableDepartment.DEPT_ID,getDepartmentId());
        values.put(TableDepartment.DEPT_NAME,getDepartmentName());
        values.put(TableDepartment.SUPER_DEPT_ID,getSuperId());
        values.put(TableDepartment.SORT,getSort());
        return values;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.departmentId);
        dest.writeString(this.departmentName);
        dest.writeString(this.superId);
        dest.writeString(this.sort);
    }

    public Department() {
    }

    public Department(Cursor cursor){
        setDepartmentId(getStringFromCursor(cursor, TableDepartment.DEPT_ID));
        setDepartmentName(getStringFromCursor(cursor, TableDepartment.DEPT_NAME));
        setSuperId(getStringFromCursor(cursor, TableDepartment.SUPER_DEPT_ID));
        setSort(getStringFromCursor(cursor, TableDepartment.SORT));
    }
    private String getStringFromCursor(Cursor cursor,String columnName){
        return cursor.getString(cursor.getColumnIndexOrThrow(columnName));
    }

    protected Department(Parcel in) {
        this.departmentId = in.readString();
        this.departmentName = in.readString();
        this.superId = in.readString();
        this.sort = in.readString();
    }

    public static final Parcelable.Creator<Department> CREATOR = new Parcelable.Creator<Department>() {
        public Department createFromParcel(Parcel source) {
            return new Department(source);
        }

        public Department[] newArray(int size) {
            return new Department[size];
        }
    };
}
