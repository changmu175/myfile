package com.securevoip.contacts;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by wanghao on 2015/1/26.
 */
public class Contact  implements Parcelable, Comparable<String> {

    /**
     * 自增id
     */
    private String id;
    /**
     * 联系人 id (或者说是：密信号码)值唯一
     */
    private String contactId;
    /**
     *联系人名称
     */
    private String contactName;
    /**
     * 联系人性别 (0 ： 女  1 ： 男) 暂时用不到
     */
    private String gender;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 联系人名称全拼
     */
    private String fullSpell;

    /**
     * 联系人名称简拼
     */
    private String simplicitySpell;


    private int viewType;

    private String indexChar;


    public Contact(){

    }


    public ContentValues buildContentValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put("phone_number", getContactId());
        contentValues.put("contact_id", getId());
        contentValues.put("display_name", getContactName());
        contentValues.put("photo_id", 0);
        contentValues.put("last_time_used", 0);
        contentValues.put("times_used", 0);
        contentValues.put("name_py", getFullSpell());
        contentValues.put("type", 0);
        return contentValues;
    }


    public Contact(Cursor cursor){
        setId(cursor.getString(cursor.getColumnIndex(ContactsModuleColumnsConstant.COLUMN_ID)));
        setContactId(cursor.getString(cursor.getColumnIndex(ContactsModuleColumnsConstant.COLUMN_CONTACT_ID)));
        setContactName(cursor.getString(cursor.getColumnIndex(ContactsModuleColumnsConstant.COLUMN_CONTACT_NAME)));
        setGender(cursor.getString(cursor.getColumnIndex(ContactsModuleColumnsConstant.COLUMN_CONTACT_GENDER)));
        setCreateTime(cursor.getString(cursor.getColumnIndex(ContactsModuleColumnsConstant.COLUMN_CONTACT_CREATE_TIME)));
        setFullSpell(cursor.getString(cursor.getColumnIndex(ContactsModuleColumnsConstant.COLUMN_CONTACT_NAME_FULL_SPELL)));
        setSimplicitySpell(cursor.getString(cursor.getColumnIndex(ContactsModuleColumnsConstant.COLUMN_CONTACT_NAME_SIMPLICITY_SPELL)));
    }
    /*public Contact(ActomFriendBean friendBean){
        setId(friendBean.getActomId());
        setContactId(friendBean.getAccount());
        String name = friendBean.getRemark();
        if(TextUtils.isEmpty(name)){
            name = friendBean.getNickname();
            if(TextUtils.isEmpty(name)){
                name = friendBean.getAccount();
            }
        }
        setFullSpell(HanziToPinyin.getFullSpell(name));
        setSimplicitySpell(HanziToPinyin.getSimplicitySpell(name));
        setContactName(name);
        setCreateTime(friendBean.getCreateTime());
    }*/

    public void setContentValues(ContentValues values,boolean isUpdate){
        if(isUpdate){
            values.put(ContactsModuleColumnsConstant.COLUMN_ID,getId());
        }
        values.put(ContactsModuleColumnsConstant.COLUMN_CONTACT_ID,getContactId());
        values.put(ContactsModuleColumnsConstant.COLUMN_CONTACT_NAME,getContactName());
        values.put(ContactsModuleColumnsConstant.COLUMN_CONTACT_GENDER,getGender());
        values.put(ContactsModuleColumnsConstant.COLUMN_CONTACT_CREATE_TIME, System.currentTimeMillis());
        values.put(ContactsModuleColumnsConstant.COLUMN_CONTACT_NAME_FULL_SPELL,getFullSpell());
        values.put(ContactsModuleColumnsConstant.COLUMN_CONTACT_NAME_SIMPLICITY_SPELL,getSimplicitySpell());
    }

    public String getIndexChar() {
        return indexChar;
    }

    public void setIndexChar(String indexChar) {
        this.indexChar = indexChar;
    }


    public String getFullSpell() {
        return fullSpell;
    }

    public void setFullSpell(String fullSpell) {
        this.fullSpell = fullSpell;
    }

    public String getSimplicitySpell() {
        return simplicitySpell;
    }

    public void setSimplicitySpell(String simplicitySpell) {
        this.simplicitySpell = simplicitySpell;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public int compareTo(String another) {
        return 0;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }


    @Override
    public void writeToParcel(Parcel arg0, int arg1) {
        arg0.writeString(this.getId());
        arg0.writeString(this.getContactId());
        arg0.writeString(this.getContactName());
        arg0.writeString(this.getGender());
        arg0.writeString(this.getCreateTime());
        arg0.writeString(this.getFullSpell());
        arg0.writeString(this.getSimplicitySpell());

    }
    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        public Contact createFromParcel(Parcel in) {

            Contact txlUser = new Contact();

            txlUser.setId(in.readString());
            txlUser.setContactId(in.readString());
            txlUser.setContactName(in.readString());
            txlUser.setGender(in.readString());
            txlUser.setCreateTime(in.readString());
            txlUser.setFullSpell(in.readString());
            txlUser.setSimplicitySpell(in.readString());

            return txlUser;

        }

        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };


    public class ContactsModuleColumnsConstant {

        public static final String TABLE_NAME = "contact";

        public static final String COLUMN_ID = "_id";

        public static final String COLUMN_CONTACT_ID = "contact_id";

        public static final String COLUMN_CONTACT_NAME = "contact_name";

        public static final String COLUMN_CONTACT_GENDER = "contact_gender";

        public static final String COLUMN_CONTACT_NAME_FULL_SPELL = "contact_name_full_spell";

        public static final String COLUMN_CONTACT_NAME_SIMPLICITY_SPELL = "contact_name_simpilicity_spell";

        public static final String COLUMN_CONTACT_CREATE_TIME = "contact_create_time";

    }
}
