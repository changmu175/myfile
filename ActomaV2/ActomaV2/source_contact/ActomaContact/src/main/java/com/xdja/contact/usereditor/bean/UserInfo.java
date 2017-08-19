package com.xdja.contact.usereditor.bean;

import com.xdja.contact.bean.Avatar;
import com.xdja.comm.uitl.ObjectUtil;

/**
 * Created by hkb.
 * 2015/7/13/0013.
 *
 * 2016-01-29 wanghao 重构
 */
public class UserInfo {

    /**
     * 群组管理人员时表示 添加人员按钮
     */
    public static final int ID_ADD_USER = -1000;
    /**
     * 群组管理人员时表示 删除人员按钮
     */
    public static final int ID_DEL_USER = -1001;

    protected int id; //

    protected String name;

    protected String avatar;

    protected String account;

    protected String nickName;

    protected String mobile;


    /**
     * 优先 备注 群昵称 账户昵称 帐号 (这里不需要集团姓名)
     */
    //private String showName;

    private String groupMemberNickname;
    /*******2016-02-03 添加   在群详情加载群成员信息动作*********************/
    private String remark;

    private Avatar avatarBean;
    //add by wal@xdja.com  start for 1738
    private String departmentMemberName;
    //add by wal@xdja.com  end for 1738
    private String accountNickname;
    private String alias;
    public String getAccountNickname() {
        return accountNickname;
    }

    public void setAccountNickname(String accountNickname) {
        this.accountNickname = accountNickname;
    }

    public Avatar getAvatarBean() {
        return avatarBean;
    }

    public void setAvatarBean(Avatar avatarBean) {
        this.avatarBean = avatarBean;
    }

    public String getGroupMemberNickname() {
        return groupMemberNickname;
    }

    public void setGroupMemberNickname(String groupMemberNickname) {
        this.groupMemberNickname = groupMemberNickname;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
    //add by wal@xdja.com  start for 1738
    public String getDepartmentMemberName() {
        return departmentMemberName;
    }

    public void setDepartmentMemberName(String departmentMemberName) {
        this.departmentMemberName = departmentMemberName;
    }
    //add by wal@xdja.com  end for 1738

    public String getShowName() {
        if(!ObjectUtil.stringIsEmpty(remark))
            return remark;
        if(!ObjectUtil.stringIsEmpty(groupMemberNickname))
            return groupMemberNickname;
        //add by wal@xdja.com  start for 1738
        if(!ObjectUtil.stringIsEmpty(departmentMemberName))
            return departmentMemberName;
        //add by wal@xdja.com  end for 1738
        if(!ObjectUtil.stringIsEmpty(accountNickname))
            return accountNickname;
        if(!ObjectUtil.stringIsEmpty(alias))
            return alias;
        return account;
    }

//    public void setShowName(String showName) {
//        this.showName = showName;
//    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public UserInfo(){}

    public UserInfo(Builder builder){
        this.id = builder.id;
        this.name = builder.name;
        this.avatar = builder.avatar;
        this.account = builder.account;
        this.nickName = builder.nickName;
        this.mobile = builder.phone;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }



    public static class Builder{

        private int id;

        private String name;

        private String avatar;

        private String account;

        private String nickName;

        private String phone;

        public Builder(int id){
            this.id = id;
        }

        public Builder setName(String name){
            this.name = name;
            return this;
        }

        public Builder setAvatar(String avatar){
            this.avatar = avatar;
            return this;
        }

        public Builder setAccount(String account){
            this.account = account;
            return this;
        }

        public Builder setNickName(String nickName){
            this.nickName = nickName;
            return this;
        }

        public Builder setPhone(String phone){
            this.phone = phone;
            return this;
        }

        public UserInfo create(){
            return new UserInfo(this);
        }
    }
}
