package com.xdja.comm.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.text.TextUtils;

import com.xdja.dependence.uitls.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Summary:安通帐号数据库操作类</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.comm.data</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/7/13</p>
 * <p>Time:15:35</p>
 */
public class AccountDao extends FrameDao {

    private static AccountDao instance;

    public static AccountDao instance() {
        if (instance == null) {
            instance = new AccountDao();
        }
        return instance;
    }

    private SQLiteDatabase database;

    public static final String TABLE_NAME = "t_account";

    public static final String FIELD_ID = "id";
    public static final String FIELD_ACCOUNT = "account";
    public static final String FIELD_ALIAS = "alias";
    public static final String FIELD_DEVICENAME = "deviceName";
    public static final String FIELD_NICKNAME = "nickname";
    public static final String FIELD_MOBILE = "mobile";
    public static final String FIELD_MAIL = "mail";
    public static final String FIELD_AVATAR = "avatar";
    public static final String FIELD_THUMBNAIL = "thumbnail";
    public static final String FIELD_AVATARDOWNLOADURL = "avatarDownloadUrl";
    public static final String FIELD_THUMBNAILDOWNLOADURL = "thumbnailDownloadUrl";
    public static final String FIELD_NICKNAMEPY = "nicknamePy";
    public static final String FIELD_NICKNAMEPINYIN = "nicknamePinyin";
    public static final String FIELD_COMPANYCODE = "companyCode";

    @StringDef(value = {FIELD_ID, FIELD_ACCOUNT,FIELD_ALIAS, FIELD_DEVICENAME, FIELD_NICKNAME,
            FIELD_MOBILE, FIELD_MAIL, FIELD_AVATAR, FIELD_THUMBNAIL, FIELD_AVATARDOWNLOADURL,
            FIELD_THUMBNAILDOWNLOADURL, FIELD_NICKNAMEPY, FIELD_NICKNAMEPINYIN, FIELD_COMPANYCODE
    })
    public @interface ACCOUNT_FIELD {
    }

    public static final String SQL_CREATE_TABLE_ACCOUNT =
            "CREATE TABLE " + TABLE_NAME
                    + " (" + FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + FIELD_ACCOUNT + " TEXT,"
                    + FIELD_ALIAS + " TEXT,"
                    + FIELD_DEVICENAME + " TEXT,"
                    + FIELD_NICKNAME + " TEXT,"
                    + FIELD_MOBILE + " TEXT,"
                    + FIELD_MAIL + " TEXT,"
                    + FIELD_AVATAR + " TEXT,"
                    + FIELD_THUMBNAIL + " TEXT,"
                    + FIELD_AVATARDOWNLOADURL + " TEXT,"
                    + FIELD_THUMBNAILDOWNLOADURL + " TEXT,"
                    + FIELD_NICKNAMEPY + " TEXT,"
                    + FIELD_NICKNAMEPINYIN + " TEXT,"
                    + FIELD_COMPANYCODE + " TEXT)";

    public static final String SQL_UPDATE_4_FROM_3 = "ALTER TABLE " + TABLE_NAME + " ADD " + FIELD_ALIAS + " TEXT";

    public AccountDao() {
        super();
    }

    @Override
    public synchronized AccountDao open() {
        super.open();
        database = helper.getWritableDatabase();
        return this;
    }

    /**
     * 查询所有的账户信息
     *
     * @return
     */
    @Nullable
    public synchronized List<AccountBean> query() {
        if (helper == null)
            return null;
        Cursor cursor = null;
        try {
            cursor = database.query(TABLE_NAME,
                    null, null, null, null, null, null);
            if (cursor!= null && cursor.moveToFirst()) {
                List<AccountBean> accountBeans = new ArrayList<>();
                while (!cursor.isAfterLast()) {
                    AccountBean accountBean = new AccountBean();
                    accountBean.setAccount(cursor.getString(cursor.getColumnIndex(FIELD_ACCOUNT)));
                    accountBean.setAlias(cursor.getString(cursor.getColumnIndex(FIELD_ALIAS)));
                    accountBean.setDeviceName(cursor.getString(cursor.getColumnIndex(FIELD_DEVICENAME)));
                    accountBean.setNickname(cursor.getString(cursor.getColumnIndex(FIELD_NICKNAME)));
                    accountBean.setMobile(cursor.getString(cursor.getColumnIndex(FIELD_MOBILE)));
                    accountBean.setMail(cursor.getString(cursor.getColumnIndex(FIELD_MAIL)));
                    accountBean.setAvatar(cursor.getString(cursor.getColumnIndex(FIELD_AVATAR)));
                    accountBean.setThumbnail(cursor.getString(cursor.getColumnIndex(FIELD_THUMBNAIL)));
                    accountBean.setAvatarDownloadUrl(cursor.getString(cursor.getColumnIndex(FIELD_AVATARDOWNLOADURL)));
                    accountBean.setThumbnailDownloadUrl(cursor.getString(cursor.getColumnIndex(FIELD_THUMBNAILDOWNLOADURL)));
                    accountBean.setNicknamePy(cursor.getString(cursor.getColumnIndex(FIELD_NICKNAMEPY)));
                    accountBean.setNicknamePinyin(cursor.getString(cursor.getColumnIndex(FIELD_NICKNAMEPINYIN)));
                    accountBean.setCompanyCode(cursor.getString(cursor.getColumnIndex(FIELD_COMPANYCODE)));
                    accountBeans.add(accountBean);
                    cursor.moveToNext();
                }
                return accountBeans;
            }
        } catch (Exception e) {
            LogUtil.getUtils().i("数据查询失败"+e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 查询数据库中第一条用户数据（理论上只有这一条数据）
     *
     * @return
     */
    @Nullable
    public synchronized AccountBean queryFirst() {
        List<AccountBean> acs = query();
        if (acs != null && !acs.isEmpty()) {
            return acs.get(0);
        }
        return null;
    }

    /**
     * 将用户信息保存到数据库中（如果数据库中已经存在别的用户信息，会先将其删除然后保存）
     *
     * @param accountBean
     * @return
     */
    public synchronized boolean save(@Nullable AccountBean accountBean) {
        if (helper == null || accountBean == null)
            return false;
        AccountBean account = queryFirst();
        if (account != null && !TextUtils.isEmpty(account.getAccount())) {
            database.delete(TABLE_NAME, FIELD_ACCOUNT + "= ?", new String[]{account.getAccount()});
        }

        ContentValues values = new ContentValues();
        values.put(FIELD_ACCOUNT, accountBean.getAccount());
        values.put(FIELD_ALIAS, accountBean.getAlias());
        values.put(FIELD_DEVICENAME, accountBean.getDeviceName());
        values.put(FIELD_NICKNAME, accountBean.getNickname());
        values.put(FIELD_MOBILE, accountBean.getMobile());
        values.put(FIELD_MAIL, accountBean.getMail());
        values.put(FIELD_AVATAR, accountBean.getAvatar());
        values.put(FIELD_THUMBNAIL, accountBean.getThumbnail());
        values.put(FIELD_AVATARDOWNLOADURL, accountBean.getAvatarDownloadUrl());
        values.put(FIELD_THUMBNAILDOWNLOADURL, accountBean.getThumbnailDownloadUrl());
        values.put(FIELD_NICKNAMEPY, accountBean.getNicknamePy());
        values.put(FIELD_NICKNAMEPINYIN, accountBean.getNicknamePinyin());
        values.put(FIELD_COMPANYCODE, accountBean.getCompanyCode());
        long num = database.insert(TABLE_NAME, null, values);
        if (num < 0) {
            LogUtil.getUtils().i("数据插入失败");
            return false;
        }
        return true;
    }

    /**
     * 更新账户信息
     *
     * @param acb 待更新的数据实体
     * @return
     */
    public synchronized boolean update(@Nullable AccountBean acb) {
        if (acb == null || TextUtils.isEmpty(acb.getAccount())) {
            return false;
        }

        AccountBean old = queryFirst();
        if (old == null
                || TextUtils.isEmpty(old.getAccount())
                || !old.getAccount().equals(acb.getAccount())) {
            return false;
        }
        ContentValues values = new ContentValues();
        values.put(FIELD_ALIAS, acb.getAlias());
        values.put(FIELD_DEVICENAME, acb.getDeviceName());
        values.put(FIELD_NICKNAME, acb.getNickname());
        values.put(FIELD_MOBILE, acb.getMobile());
        values.put(FIELD_MAIL, acb.getMail());
        values.put(FIELD_AVATAR, acb.getAvatar());
        values.put(FIELD_THUMBNAIL, acb.getThumbnail());
        values.put(FIELD_AVATARDOWNLOADURL, acb.getAvatarDownloadUrl());
        values.put(FIELD_THUMBNAILDOWNLOADURL, acb.getThumbnailDownloadUrl());
        values.put(FIELD_NICKNAMEPY, acb.getNicknamePy());
        values.put(FIELD_NICKNAMEPINYIN, acb.getNicknamePinyin());
        values.put(FIELD_COMPANYCODE, acb.getCompanyCode());
        database.update(TABLE_NAME, values, FIELD_ACCOUNT + " = ?", new String[]{old.getAccount()});
        return true;
    }

    /**
     * 更新账户的一个字段
     *
     * @param field 字段名称
     * @param value 字段值
     * @return 是否成功
     */
    public synchronized boolean updateField(@ACCOUNT_FIELD String field, @Nullable String value) {
        if (TextUtils.isEmpty(field))
            return false;

        AccountBean old = queryFirst();

        if (old == null
                || TextUtils.isEmpty(old.getAccount()))
            return false;

        ContentValues values = new ContentValues();
        values.put(field, value);
        database.update(TABLE_NAME, values, FIELD_ACCOUNT + "= ?", new String[]{old.getAccount()});
        return true;
    }
}
