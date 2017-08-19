package com.xdja.contact.bean;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.xdja.contact.database.columns.TableDepartmentMember;
import com.xdja.contact.usereditor.bean.UserInfo;

/**
 *集团通讯录成员信息
 * Created by hkb.
 * 2015/7/8/0008.
 */
public class Member extends UserInfo implements Parcelable,BaseContact {


    public static final String ADD = "1";

    public static final String MODIFY = "2";

    public static final String DELETE = "3";


//    private int id;//主键、自增、唯一

    private String departId;//部门id

    private String name;//姓名

    private String sort;//排序

    private String namePy;//姓名拼音首字母

    private String nameFullPy;//姓名拼音

    private String account;//安通账号

    private String phone;//集团信息里面录入的电话信息

    private String workId;//工号(预留字段)

    private boolean isChecked;//

    private Avatar avatarInfo;

    private String departmentName;

    private String type;//更新类型；1-添加； 2-修改； 3-删除

    //设置别名  该字段不会显示在数据库当中
    private String alias;


    private ActomaAccount actomaAccount;

    public void setActomaAccount(ActomaAccount actomaAccount) {
        this.actomaAccount = actomaAccount;
    }

    public ActomaAccount getActomaAccount() {
        return actomaAccount;
    }


    //在本地搜索的时候 需要用到当前函数
    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Avatar getAvatarInfo() {
        return avatarInfo;
    }

    public void setAvatarInfo(Avatar avatarInfo) {
        this.avatarInfo = avatarInfo;
    }

    public String getDepartId() {
        return departId;
    }

    public void setDepartId(String departId) {
        this.departId = departId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getNamePy() {
        return namePy;
    }

    public void setNamePy(String namePy) {
        this.namePy = namePy;
    }

    public String getNameFullPy() {
        return nameFullPy;
    }

    public void setNameFullPy(String nameFullPy) {
        this.nameFullPy = nameFullPy;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    @Override
    public String getAccount() {
        return account;
    }

    @Override
    public void setAccount(String account) {
        this.account = account;
    }

    public String getMobile() {
        return phone;
    }

    public void setMobile(String phone) {
        this.phone = phone;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TableDepartmentMember.SORT,getSort());
        contentValues.put(TableDepartmentMember.MEMBER_DEPT_ID,getDepartId());
        contentValues.put(TableDepartmentMember.NAME,getName());
        contentValues.put(TableDepartmentMember.NAME_FULL_PY,getNameFullPy());
        contentValues.put(TableDepartmentMember.NAME_PY,getNamePy());
        contentValues.put(TableDepartmentMember.ACCOUNT,getAccount());
        contentValues.put(TableDepartmentMember.PHONE, getMobile());
        contentValues.put(TableDepartmentMember.WORKER_ID,getWorkId());
        return contentValues;
    }

    public Member() {
    }
    public Member(Cursor cursor) {
        departId =getStringFromCursor(cursor, TableDepartmentMember.MEMBER_DEPT_ID);
        name = getStringFromCursor(cursor, TableDepartmentMember.NAME);
        sort = getStringFromCursor(cursor, TableDepartmentMember.SORT);
        namePy = getStringFromCursor(cursor, TableDepartmentMember.NAME_PY);
        nameFullPy =  getStringFromCursor(cursor, TableDepartmentMember.NAME_FULL_PY);
        account = getStringFromCursor(cursor, TableDepartmentMember.ACCOUNT);
        phone = getStringFromCursor(cursor, TableDepartmentMember.PHONE);
        workId = getStringFromCursor(cursor, TableDepartmentMember.WORKER_ID);
    }

    public void setCursor(Cursor cursor) {
        name = getStringFromCursor(cursor, TableDepartmentMember.NAME);
        namePy = getStringFromCursor(cursor, TableDepartmentMember.NAME_PY);
        nameFullPy =  getStringFromCursor(cursor, TableDepartmentMember.NAME_FULL_PY);
        account = getStringFromCursor(cursor, TableDepartmentMember.ACCOUNT);
    }

    private String getStringFromCursor(Cursor cursor,String columnName){
        return cursor.getString(cursor.getColumnIndexOrThrow(columnName));
    }


    @Override
    public boolean equals(Object o) {
        return((Member)o).getWorkId() == getWorkId();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.departId);
        dest.writeString(this.name);
        dest.writeString(this.sort);
        dest.writeString(this.namePy);
        dest.writeString(this.nameFullPy);
        dest.writeString(this.account);
        dest.writeString(this.phone);
        dest.writeString(this.workId);
        dest.writeByte(isChecked ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.avatarInfo, 0);
        dest.writeString(this.departmentName);
    }

    protected Member(Parcel in) {
        this.departId = in.readString();
        this.name = in.readString();
        this.sort = in.readString();
        this.namePy = in.readString();
        this.nameFullPy = in.readString();
        this.account = in.readString();
        this.phone = in.readString();
        this.workId = in.readString();
        this.isChecked = in.readByte() != 0;
        this.avatarInfo = in.readParcelable(Avatar.class.getClassLoader());
        this.departmentName = in.readString();
    }

    public static final Creator<Member> CREATOR = new Creator<Member>() {
        public Member createFromParcel(Parcel source) {
            return new Member(source);
        }

        public Member[] newArray(int size) {
            return new Member[size];
        }
    };
}
