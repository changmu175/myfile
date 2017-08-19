package com.xdja.contact.dao.sqlbuilder;

import com.xdja.contact.database.columns.TableRequestInfo;

/**
 * Created by yangpeng on 2015/12/23.
 */
public class FriendRequestInfoDaoSqlBuilder {

    public static final String buildQuerySql(String account){
        StringBuilder builder = new StringBuilder();
        builder.append(" select * from ");
        builder.append(TableRequestInfo.TABLE_NAME);
        builder.append(" where ");
        builder.append(TableRequestInfo.ACCOUNT);
        builder.append(" = ");
        builder.append(account);
        builder.append(" order by cast( ");
        builder.append(TableRequestInfo.CREATE_TIME);
        builder.append(" as int ) desc ");
        return  builder.toString();
    }



}
