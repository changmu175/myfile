package com.xdja.contact.bean;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.xdja.comm.server.ActomaController;
import com.xdja.contact.R;
import com.xdja.contact.database.columns.TableActomaAccount;
import com.xdja.contact.database.columns.TableFriend;
import com.xdja.contact.util.AlphaUtils;
import com.xdja.comm.uitl.ObjectUtil;

/**
 * Created by wanghao on 2015/7/8.
 * 好友
 */
public class Friend implements BaseContact, Parcelable {

    public static final String DELETE = "-1";

    public static final String ADD = "0";

    public static final String MODIFY = "1";


    public static final int CONTACT_ITEM = 0;

    public static final int ALPHA = 1;

    public static final int NEW_FRIEND = 2;

    public static final String AT_OBJECT = "0";

    public static final String NORMAL_OBJECT = "1";

    private ActomaAccount actomaAccount;

    private Member member;

    private Avatar avatar;

    private String account;
    /**
     * 备注
     */
    private String remark;
    /**
     * 备注简拼
     */
    private String remarkPy;
    /**
     * 备注全拼
     */
    private String remarkPinyin;
    /**
     * 类型:安通+团队 普通好友
     * 0 : AT_OBJECT 安通+团队
     * 1 : NORMAL 普通好友
     */
    private String type;
    /**
     * 更新标示
     */
    private String updateSerial;

    /**联系人列表显示索引和正常显示item专用*/
    private int viewType;
    /**
     * 快速索引标示数据库不需要使用此字段
     */
    private String indexChar;
    /**
     * 是否显示在好友列表：删除好友的时候修改此状态
     * 0 : 不显示 1 :显示
     */
    private String isShow;

    private boolean isChecked;

    /**
     * 增量状态: -1 已经删除，0-正常，1-修改
     *
     *   public static final String DELETE = "-1";

         public static final String ADD = "0";

         public static final String MODIFY = "1";
     *
     */
    private String state;


    //是否是好友发起方，0-是，1-否
    private String initiative;


    //设置别名  该字段不会显示在数据库当中
    private String alias;


    public Friend(){}

    public Friend(Cursor cursor){
        setAccount(cursor.getString(cursor.getColumnIndex(TableFriend.ACCOUNT)));
        setRemark(cursor.getString(cursor.getColumnIndex(TableFriend.REMARK)));
        setRemarkPy(cursor.getString(cursor.getColumnIndex(TableFriend.REMARK_PY)));
        setRemarkPinyin(cursor.getString(cursor.getColumnIndex(TableFriend.REMARK_FULL_PY)));
        setType(cursor.getString(cursor.getColumnIndex(TableFriend.TYPE)));
        setUpdateSerial(cursor.getString(cursor.getColumnIndex(TableFriend.UPDATE_SERIAL)));
        setIsShow(cursor.getString(cursor.getColumnIndex(TableFriend.IS_SHOW)));
        setInitiative(cursor.getString(cursor.getColumnIndex(TableFriend.INITIATIVE)));
        //add alias by lwl
        if(-1!=cursor.getColumnIndex(TableActomaAccount.ALIAS))
        setAlias(cursor.getString(cursor.getColumnIndex(TableActomaAccount.ALIAS)));
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(TableFriend.ACCOUNT, getAccount());
        values.put(TableFriend.REMARK, getRemark());
        values.put(TableFriend.REMARK_PY, getRemarkPy());
        values.put(TableFriend.REMARK_FULL_PY, getRemarkPinyin());
        values.put(TableFriend.TYPE, getType());
        values.put(TableFriend.UPDATE_SERIAL, getUpdateSerial());
        values.put(TableFriend.IS_SHOW, isShow() ? "1" : "0");
        values.put(TableFriend.INITIATIVE,getInitiative());
        return values;
    }


    //在本地搜索的时候 需要用到当前函数
    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public String getInitiative() {
        return initiative;
    }

    public void setInitiative(String initiative) {
        this.initiative = initiative;
    }


    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    //----备注 > 名称 > 昵称 > 帐号
    public String showName(){
        if(!ObjectUtil.stringIsEmpty(getRemark())){
            return getRemark();
        }
        if(!ObjectUtil.objectIsEmpty(member) && !ObjectUtil.stringIsEmpty(member.getName())){
            return member.getName();
        }
        if(!ObjectUtil.objectIsEmpty(actomaAccount) && !ObjectUtil.stringIsEmpty(actomaAccount.getNickname())){
            return actomaAccount.getNickname();
        }
        if(!ObjectUtil.objectIsEmpty(actomaAccount) && !ObjectUtil.stringIsEmpty(actomaAccount.getAlias())){
            return actomaAccount.getAlias();
        }
        return getAccount();
    }


    public String getComparatorColumn() {
        if (!ObjectUtil.stringIsEmpty(getRemarkPinyin())) {
            return getRemarkPinyin().toLowerCase();
        } else if(!ObjectUtil.objectIsEmpty(getMember()) && !ObjectUtil.stringIsEmpty(getMember().getNameFullPy())){
            return getMember().getNameFullPy().toLowerCase();
        }else if (!ObjectUtil.objectIsEmpty(getActomaAccount()) &&
                !ObjectUtil.stringIsEmpty(getActomaAccount().getNicknamePinyin())){
            return getActomaAccount().getNicknamePinyin().toLowerCase();
        } else{
            //add by lwl  start
            if(!ObjectUtil.objectIsEmpty(actomaAccount) && !ObjectUtil.stringIsEmpty(actomaAccount.getAlias())){
                return actomaAccount.getAlias().toLowerCase();
            }
            //add by lwl end
            return getAccount().toLowerCase();
        }
    }

    public ActomaAccount getActomaAccount() {
        return actomaAccount;
    }

    public void setActomaAccount(ActomaAccount actomaAccount) {
        this.actomaAccount = actomaAccount;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemarkPy() {
        return remarkPy;
    }

    public void setRemarkPy(String remarkPy) {
        this.remarkPy = remarkPy;
    }

    public String getRemarkPinyin() {
        return remarkPinyin;
    }

    public void setRemarkPinyin(String remarkPinyin) {
        this.remarkPinyin = remarkPinyin;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public boolean isShow() {
        return "1".equals(isShow);
    }

    public void setIsShow(String isShow) {
        this.isShow = isShow;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUpdateSerial() {
        return updateSerial;
    }

    public void setUpdateSerial(String updateSerial) {
        this.updateSerial = updateSerial;
    }

    public String getIndexChar() {
        return indexChar;
    }

    public void setIndexChar(String indexChar) {
        this.indexChar = indexChar;
    }


    public Boolean getIsChecked() {
        return isChecked;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(Boolean isChecked) {
        this.isChecked = isChecked;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null){
            return false;
        }
        return ((Friend)o).getAccount().equals(this.account);
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    //和老徐统一定义 安通+团队:-10000
    public static final String ANTONG_TEAM_ACCOUNT = "-10000";
    public static class Builder{
        /**
         * 组装安通+团队数据
         * @return
         */
        public static Friend buildAtFriend(){
            Friend atFriend = new Friend();
            atFriend.setAccount(ANTONG_TEAM_ACCOUNT);
            atFriend.setType(AT_OBJECT);
            atFriend.setIsShow("1");
            atFriend.setViewType(1);
            atFriend.setRemark(ActomaController.getApp().getString(R.string.actoma_team_title));//modify by wal@xdja.com for string 安通+团队
            atFriend.setRemarkPy("anttd");
            atFriend.setRemarkPinyin("antong+");
            atFriend.setViewType(CONTACT_ITEM);
            return atFriend;
        }

        /**
         * 组装新的好友 数据
         * @return
         */
        public static Friend buildNewFriend(){
            Friend newFriend = new Friend();
            newFriend.setViewType(NEW_FRIEND);
            newFriend.setIndexChar(AlphaUtils.XING);//add by wangalei for 1018
            newFriend.setRemark(ActomaController.getApp().getString(R.string.contact_new_friend));//modify by wal@xdja.com for string 新的好友
            newFriend.setRemarkPinyin("xindehaoyou");
            newFriend.setRemarkPy("xdhy");
            return newFriend;
        }

        public static Friend buildNormalFriend(String account){
            Friend friend = new Friend();
            friend.setAccount(account);
            friend.setIsShow("1");
            friend.setState("0");
            friend.setType(NORMAL_OBJECT);
            return friend;
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.actomaAccount, 0);
        dest.writeParcelable(this.avatar, 0);
        dest.writeParcelable(this.member, 0);//add by wal@xdja.com for 3666
        dest.writeString(this.account);
        dest.writeString(this.remark);
        dest.writeString(this.remarkPy);
        dest.writeString(this.remarkPinyin);
        dest.writeString(this.type);
        dest.writeString(this.updateSerial);
        dest.writeInt(this.viewType);
        dest.writeString(this.indexChar);
        dest.writeString(this.isShow);
        dest.writeByte(isChecked ? (byte) 1 : (byte) 0);
        dest.writeString(this.state);
    }

    protected Friend(Parcel in) {
        this.actomaAccount = in.readParcelable(ActomaAccount.class.getClassLoader());
        this.avatar = in.readParcelable(Avatar.class.getClassLoader());
        this.member = in.readParcelable(Member.class.getClassLoader());//add by wal@xdja.com for 3666
        this.account = in.readString();
        this.remark = in.readString();
        this.remarkPy = in.readString();
        this.remarkPinyin = in.readString();
        this.type = in.readString();
        this.updateSerial = in.readString();
        this.viewType = in.readInt();
        this.indexChar = in.readString();
        this.isShow = in.readString();
        this.isChecked = in.readByte() != 0;
        this.state = in.readString();
    }

    public static final Creator<Friend> CREATOR = new Creator<Friend>() {
        public Friend createFromParcel(Parcel source) {
            return new Friend(source);
        }

        public Friend[] newArray(int size) {
            return new Friend[size];
        }
    };
}
