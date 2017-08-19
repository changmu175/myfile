package com.xdja.contact.dao.sqlbuilder;

import android.text.TextUtils;

import com.xdja.contact.bean.Avatar;
import com.xdja.contact.database.columns.TableAccountAvatar;

/**
 * Created by yangpeng on 2015/12/25.
 */
public class AvatarDaoSqlBuilder {

    public static final String querySql(String account){
        StringBuilder sql = new StringBuilder();
        sql.append("select * from ");
        sql.append(TableAccountAvatar.TABLE_NAME);
        sql.append(" where ");
        sql.append(TableAccountAvatar.ACCOUNT);
        sql.append(" = ");
        sql.append(account);
        return sql.toString();
    }

    public static final String updateSql(Avatar avatar, String where){
        StringBuilder sql = new StringBuilder();
        sql.append("update "+TableAccountAvatar.TABLE_NAME);
        sql.append(" set ");
        sql.append(TableAccountAvatar.ACCOUNT);
        sql.append(" = ");
        sql.append(checkColumnNull(avatar.getAccount()));
        sql.append(",");
        sql.append(TableAccountAvatar.THUMBNAIL);
        sql.append(" = ");
        sql.append(checkColumnNull(avatar.getThumbnail()));
        sql.append(",");
        sql.append(TableAccountAvatar.AVATAR);
        sql.append(" = ");
        sql.append(checkColumnNull(avatar.getAvatar()));
        sql.append(" where ");
        sql.append(TableAccountAvatar.ACCOUNT);
        sql.append(" = ");
        sql.append(avatar.getAccount());
        return sql.toString();
    }

    public static final String insertSql(Avatar avatar){
        StringBuilder sql = new StringBuilder();
        sql.append(" insert into ");
        sql.append(TableAccountAvatar.TABLE_NAME);
        sql.append(" ( ");
        sql.append(TableAccountAvatar.ACCOUNT);
        sql.append(",");
        sql.append(TableAccountAvatar.AVATAR);
        sql.append(",");
        sql.append(TableAccountAvatar.THUMBNAIL);
        sql.append(" ) ");
        sql.append(" values ( ");
        sql.append(checkColumnNull(avatar.getAccount()));
        sql.append(",");
        sql.append(checkColumnNull(avatar.getAvatar()));
        sql.append(",");
        sql.append(checkColumnNull(avatar.getThumbnail()));
        sql.append(" ) ");
        return sql.toString();
    }

    public static final String queryAllNotNullSql(){
        StringBuilder sql = new StringBuilder();
        sql.append("select "+TableAccountAvatar.AVATAR);
        sql.append(" from "+TableAccountAvatar.TABLE_NAME);
        sql.append(" where ");
        sql.append(TableAccountAvatar.AVATAR);
        sql.append(" NOT LIKE \"\" ");
        return sql.toString();
    }


    public static String checkColumnNull(String data){
        return TextUtils.isEmpty(data) ? "NULL" : "'"+data+"'";
    }
}
