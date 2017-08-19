package com.xdja.contact.http.response.department;

import android.os.Parcel;
import android.os.Parcelable;

import com.xdja.contact.bean.Department;

/**
 * Created by hkb.
 * 2015/7/10/0010.
 */
public class ServerDepart implements Parcelable {

    private String id;

    private String name;

    private String parentId;//父级部门编号

    private String sort;//同级部门排序号

    private String type;//更新类型；1-单位添加； 2-单位信息修改； 3-单位删除

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.parentId);
        dest.writeString(this.sort);
        dest.writeString(this.type);
    }

    public ServerDepart() {
    }

    public Department convert2DepartMent(){
        Department departMent = new Department();
        departMent.setDepartmentId(getId());
        departMent.setSort(getSort());
        departMent.setSuperId(getParentId());
        departMent.setDepartmentName(getName());
        return departMent;
    }

    protected ServerDepart(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.parentId = in.readString();
        this.sort = in.readString();
        this.type = in.readString();
    }

    public static final Parcelable.Creator<ServerDepart> CREATOR = new Parcelable.Creator<ServerDepart>() {
        public ServerDepart createFromParcel(Parcel source) {
            return new ServerDepart(source);
        }

        public ServerDepart[] newArray(int size) {
            return new ServerDepart[size];
        }
    };
}
