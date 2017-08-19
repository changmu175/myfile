package com.xdja.contact.bean;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.xdja.contact.database.columns.TableActomaAccount;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.dependence.uitls.LogUtil;

/**
 * Created by wanghao on 2015/7/9.
 * 安通账号表
 */
public class ActomaAccount implements BaseContact, Parcelable {

    protected String id;
    /**
     * 安通账户
     */
    protected String account;

    /**
     * 账号别名
     */
    protected String alias;

    /**
     * 昵称
     */

    protected String nickname;
    /**
     * 绑定的手机号
     */
    protected String mobile;
    /**
     * 昵称简拼
     */
    protected String nicknamePy;
    /**
     * 昵称全拼
     */
    protected String nicknamePinyin;
    /**
     * 性别
     */
    protected String gender;
    /**
     * 邮箱
     */
    protected String mail;

    //第一次登录时间：0-该安通账号未使用过；非0-已使用过
    protected String firstLoginTime;

    // 激活状态；-1-注销，0-注册，1-正常，2-封停，3-冻结
    protected String status;

    protected String identify;

    protected Avatar avatarInfo;

    public ActomaAccount() {
    }
    //Note: 这里这样写性能太低如果数据小不明显数据大很明显(200ms;想到更好的结构表和字段对应的时候在进行优化)
    public ActomaAccount(Cursor cursor) {
        setAccount(cursor.getString(cursor.getColumnIndex(TableActomaAccount.ACCOUNT)));
        setAlias(cursor.getString(cursor.getColumnIndex(TableActomaAccount.ALIAS)));
        setNickname(cursor.getString(cursor.getColumnIndex(TableActomaAccount.NICKNAME)));
        setMobile(cursor.getString(cursor.getColumnIndex(TableActomaAccount.BIND_PHONE)));
        setNicknamePy(cursor.getString(cursor.getColumnIndex(TableActomaAccount.NICKNAME_PY)));
        setNicknamePinyin(cursor.getString(cursor.getColumnIndex(TableActomaAccount.NICKNAME_FULL_PY)));
        setGender(cursor.getString(cursor.getColumnIndex(TableActomaAccount.GENDER)));
        setEmail(cursor.getString(cursor.getColumnIndex(TableActomaAccount.EMAIL)));
        setFirstLoginTime(cursor.getString(cursor.getColumnIndex(TableActomaAccount.FIRST_LOGIN_TIME)));
        setStatus(cursor.getString(cursor.getColumnIndex(TableActomaAccount.ACTIVATE_STATUS)));
        setIdentify(cursor.getString(cursor.getColumnIndex(TableActomaAccount.IDENTIFY)));
    }
    //start:modify for update actoma acount by wal@xdja.com
    public void setIdentifyByCursor(Cursor cursor){
        setIdentify(cursor.getString(cursor.getColumnIndex(TableActomaAccount.IDENTIFY)));
    }
    //end:modify for update actoma acount by wal@xdja.com
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(TableActomaAccount.ACCOUNT, getAccount());
        values.put(TableActomaAccount.ALIAS,getAlias());
        values.put(TableActomaAccount.NICKNAME, getNickname());
        values.put(TableActomaAccount.BIND_PHONE, getMobile());
        values.put(TableActomaAccount.NICKNAME_PY, getNicknamePy());
        values.put(TableActomaAccount.NICKNAME_FULL_PY, getNicknamePinyin());
        values.put(TableActomaAccount.GENDER, getGender());
        values.put(TableActomaAccount.EMAIL, getEmail());
        values.put(TableActomaAccount.FIRST_LOGIN_TIME, getFirstLoginTime());
        values.put(TableActomaAccount.ACTIVATE_STATUS, getStatus());
        values.put(TableActomaAccount.IDENTIFY,getIdentify());
        return values;
    }

    //只有  终端显示账号的时候才调用此函数，完成业务的时候依旧使用 getAccount()
    public String showAccount(){
        if(!ObjectUtil.stringIsEmpty(alias)){
            return alias;
        }
        return account;
    }

    public String getIdentify() {
        return identify;
    }

    public void setIdentify(String identify) {
        this.identify = identify;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNicknamePy() {
        return nicknamePy;
    }

    public void setNicknamePy(String nicknamePy) {
        this.nicknamePy = nicknamePy;
    }


    public String getNicknamePinyin() {
        return nicknamePinyin;
    }

    public void setNicknamePinyin(String nicknamePinyin) {
        this.nicknamePinyin = nicknamePinyin;
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return mail;
    }

    public void setEmail(String mail) {
        this.mail = mail;
    }

    public String getFirstLoginTime() {
        return firstLoginTime;
    }

    public void setFirstLoginTime(String firstLoginTime) {
        this.firstLoginTime = firstLoginTime;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        try {
            ActomaAccount actomaAccount = (ActomaAccount) o;
            if (actomaAccount.getAccount().equals(account)) {
                return true;
            }

        } catch (Exception e) {
            LogUtil.getUtils().e("ActomaAccount equals error:"+e.getMessage());
        }
        return false;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.account);
        dest.writeString(this.alias);
        dest.writeString(this.nickname);
        dest.writeString(this.mobile);
        dest.writeString(this.nicknamePy);
        dest.writeString(this.nicknamePinyin);
        dest.writeString(this.gender);
        dest.writeString(this.mail);
        dest.writeString(this.firstLoginTime);
        dest.writeString(this.status);
        dest.writeParcelable(this.avatarInfo, 0);
    }

    protected ActomaAccount(Parcel in) {
        this.id = in.readString();
        this.account = in.readString();
        this.alias = in.readString();
        this.nickname = in.readString();
        this.mobile = in.readString();
        this.nicknamePy = in.readString();
        this.nicknamePinyin = in.readString();
        this.gender = in.readString();
        this.mail = in.readString();
        this.firstLoginTime = in.readString();
        this.status = in.readString();
        this.avatarInfo = in.readParcelable(Avatar.class.getClassLoader());
    }

    public static final Creator<ActomaAccount> CREATOR = new Creator<ActomaAccount>() {
        public ActomaAccount createFromParcel(Parcel source) {
            return new ActomaAccount(source);
        }

        public ActomaAccount[] newArray(int size) {
            return new ActomaAccount[size];
        }
    };
}
