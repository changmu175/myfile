package com.xdja.contact.http.response.department;

import android.os.Parcel;
import android.os.Parcelable;

import com.xdja.contact.bean.Member;

/**
 * Created by hkb.
 * 2015/7/10/0010.
 */
public class ServerMember implements Parcelable {

    private String id;//人员编号

    private String account;//帐号（账户属性）

    private String mobiles;//手机号（通讯录自有属性），以:分隔

    private String name;

    private String namePY;

    private String nameFullPY;

    private String deptId;//所属部门编号

    private String sort;//同级部门排序号

    private String type;//更新类型；1-添加； 2-修改； 3-删除

    //private String kuepId;//账户KuepId

    private String ksf;//账户与请求账户之间的Ksf(可选)

    private String ksfId;//账户与请求账户之间的Ksf服务端Id(可选)

    private String KsfkuepId;//加密Ksf的Kuep Id

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getMobiles() {
        return mobiles;
    }

    public void setMobiles(String mobiles) {
        this.mobiles = mobiles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamePY() {
        return namePY;
    }

    public void setNamePY(String namePY) {
        this.namePY = namePY;
    }

    public String getNameFullPY() {
        return nameFullPY;
    }

    public void setNameFullPY(String nameFullPY) {
        this.nameFullPY = nameFullPY;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
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

//    public String getKuepId() {
//        return kuepId;
//    }
//
//    public void setKuepId(String kuepId) {
//        this.kuepId = kuepId;
//    }

    public String getKsf() {
        return ksf;
    }

    public void setKsf(String ksf) {
        this.ksf = ksf;
    }

    public String getKsfId() {
        return ksfId;
    }

    public void setKsfId(String ksfId) {
        this.ksfId = ksfId;
    }

    public String getKsfkuepId() {
        return KsfkuepId;
    }

    public void setKsfkuepId(String ksfkuepId) {
        KsfkuepId = ksfkuepId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.account);
        dest.writeString(this.mobiles);
        dest.writeString(this.name);
        dest.writeString(this.namePY);
        dest.writeString(this.nameFullPY);
        dest.writeString(this.deptId);
        dest.writeString(this.sort);
        dest.writeString(this.type);
//        dest.writeString(this.kuepId);
        dest.writeString(this.ksf);
        dest.writeString(this.ksfId);
        dest.writeString(this.KsfkuepId);
    }

    public Member convert2Member(){
        Member member = new Member();
        member.setMobile(getMobiles());
        member.setAccount(getAccount());
        member.setDepartId(getDeptId());
        member.setSort(getSort());
        member.setWorkId(getId());
        member.setName(getName());
        member.setNameFullPy(getNameFullPY());
        member.setNamePy(getNamePY());
        member.setType(getType());
        return member;
    }

    public ServerMember() {
    }

    protected ServerMember(Parcel in) {
        this.id = in.readString();
        this.account = in.readString();
        this.mobiles = in.readString();
        this.name = in.readString();
        this.namePY = in.readString();
        this.nameFullPY = in.readString();
        this.deptId = in.readString();
        this.sort = in.readString();
        this.type = in.readString();
//        this.kuepId = in.readString();
        this.ksf = in.readString();
        this.ksfId = in.readString();
        this.KsfkuepId = in.readString();
    }

    public static final Creator<ServerMember> CREATOR = new Creator<ServerMember>() {
        public ServerMember createFromParcel(Parcel source) {
            return new ServerMember(source);
        }

        public ServerMember[] newArray(int size) {
            return new ServerMember[size];
        }
    };

}
