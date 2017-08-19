package com.xdja.contact.dao;

import com.xdja.contact.bean.Avatar;
import com.xdja.contact.dao.sqlbuilder.AvatarDaoSqlBuilder;
import com.xdja.contact.database.columns.TableAccountAvatar;
import com.xdja.contact.exception.ContactDaoException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangpeng on 2015/8/3.
 * 对头像数据执行操作
 */
public class AvatarDao extends AbstractContactDao<Avatar> {

    public Avatar query(String account) {
        Avatar result = null;
        try {
            cursor = database.rawQuery(AvatarDaoSqlBuilder.querySql(account), null);
            if (cursor.moveToFirst()){
                result = new Avatar(cursor);
            }
        }catch (Exception e){
            new ContactDaoException(e);
        }finally {
            closeCursor();
        }
        return result;
    }

    public List<Avatar> queryAll() {
        List<Avatar> avatars = new ArrayList<Avatar>();
        try{
            String sql = " select * from " + getTableName();
            cursor = database.rawQuery(sql,null);
            while (cursor.moveToNext()){
                Avatar avatar = new Avatar(cursor);
                avatars.add(avatar);
            }
        }catch (Exception e){
            new ContactDaoException(e);
        }finally {
            closeCursor();
        }
        return avatars;
    }

    public List<String> queryUrls() {
        List<String> avatarUrls = new ArrayList<String>();
        try{
            cursor = database.rawQuery(AvatarDaoSqlBuilder.queryAllNotNullSql(), null);
            while (cursor.moveToNext()){
                avatarUrls.add(cursor.getString(cursor.getColumnIndex(TableAccountAvatar.AVATAR)));
            }
        }catch (Exception e){
            new ContactDaoException(e);
        }finally {
            closeCursor();
        }
        return avatarUrls;
    }

    public long insert(Avatar avatar) {
        long result = database.insert(getTableName(),null,avatar.getContentValues());
        return result;
    }


    /**
     * 更新数据
     *
     * @param avatar
     * @return
     */
    public int update(Avatar avatar) {
        String whereArg = TableAccountAvatar.ACCOUNT+"=?";
        return database.update(TableAccountAvatar.TABLE_NAME,avatar.getContentValues(),whereArg,new String[]{avatar.getAccount()});
    }

    protected String getTableName() {
        return TableAccountAvatar.TABLE_NAME;
    }

    /***********以下代码未重构*********************/

    public int delete(Avatar avatar) {
        return 0;
    }
}
