package com.xdja.contact.dao.sqlbuilder;

import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.bean.FriendRequestHistory;
import com.xdja.contact.database.columns.TableAccountAvatar;
import com.xdja.contact.database.columns.TableActomaAccount;
import com.xdja.contact.database.columns.TableFriendHistory;

import java.util.List;

/**
 * Created by yangpeng on 2015/12/24.
 */
public class FriendHistoryRequestDaoSqlBuilder {

    public static final String querySql(String reqAccount,String recAccount){
        StringBuilder builder = new StringBuilder();
        builder.append("select * from ");
        builder.append(TableFriendHistory.TABLE_NAME);
        builder.append(" where ");
        builder.append(TableFriendHistory.C_REQ_ACCOUNT);
        builder.append(" = ");
        builder.append(reqAccount);
        builder.append(" and ");
        builder.append(TableFriendHistory.C_REC_ACCOUNT);
        builder.append(" = ");
        builder.append(recAccount);
        return builder.toString();
    }

    public static final String builderHistorySql(){
        StringBuffer sqlBuilder = new StringBuffer();

        sqlBuilder.append(" select ");
        sqlBuilder.append(TableFriendHistory.C_REQ_ACCOUNT + " ,");
        sqlBuilder.append(TableFriendHistory.C_REC_ACCOUNT + " ,");
        sqlBuilder.append(TableFriendHistory.SHOW_ACCOUNT + " ,");
        sqlBuilder.append(TableFriendHistory.CREATE_TIME + ",");
        sqlBuilder.append(TableFriendHistory.UPDATE_SERIAL + ",");
        sqlBuilder.append(TableFriendHistory.STATE + ",");
        sqlBuilder.append(TableFriendHistory.IS_READ + ",");
        sqlBuilder.append(TableFriendHistory.LAST_REQUEST_INFO + ",");


        sqlBuilder.append(TableActomaAccount.ALIAS + ",");
        sqlBuilder.append(TableActomaAccount.NICKNAME + ",");
        sqlBuilder.append(TableActomaAccount.NICKNAME_PY + ",");
        sqlBuilder.append(TableActomaAccount.NICKNAME_FULL_PY + ",");
        sqlBuilder.append(TableAccountAvatar.THUMBNAIL + ",");
        sqlBuilder.append(TableAccountAvatar.AVATAR);

        sqlBuilder.append(" from ");
        sqlBuilder.append(TableFriendHistory.TABLE_NAME + " as history ");
        sqlBuilder.append(" left join ");
        sqlBuilder.append(TableActomaAccount.TABLE_NAME);
        sqlBuilder.append(" as account ");
        sqlBuilder.append(" on ");
        sqlBuilder.append(TableFriendHistory.SHOW_ACCOUNT);
        sqlBuilder.append(" = ");
        sqlBuilder.append(" account."+ TableActomaAccount.ACCOUNT);

        sqlBuilder.append(" left join ");
        sqlBuilder.append(TableAccountAvatar.TABLE_NAME);
        sqlBuilder.append(" as avatar ");
        sqlBuilder.append(" on ");
        sqlBuilder.append(TableFriendHistory.SHOW_ACCOUNT);
        sqlBuilder.append(" = ");
        sqlBuilder.append(" avatar."+ TableAccountAvatar.ACCOUNT);


        sqlBuilder.append(" order by ");
        sqlBuilder.append(" cast (history.");
        sqlBuilder.append(TableFriendHistory.CREATE_TIME);
        sqlBuilder.append(" as int )");
        sqlBuilder.append(" desc ");
        LogUtil.getUtils().i("获取历史请求数据---:sql"+sqlBuilder.toString());
        return sqlBuilder.toString();
    }


    /**
     * 删除
     * @param account
     * @return
     */
    public static  final String deleteSql(String account){
        StringBuilder sql = new StringBuilder();
        sql.append(" delete * from ");
        sql.append(TableFriendHistory.TABLE_NAME);
        sql.append(" where ");
        sql.append(TableFriendHistory.C_REQ_ACCOUNT);
        sql.append(" = ");
        sql.append(account);
        return sql.toString();
    }

    /**
     * 统计未读数量
     * @return
     */
    public static  final String countNewFriendsSql(){
        StringBuilder sql = new StringBuilder();
        sql.append(" select count ( ");
        sql.append(TableFriendHistory.IS_READ);
        sql.append(" ) ");
        sql.append(" from ");
        sql.append(TableFriendHistory.TABLE_NAME);
        sql.append(" where ");
        sql.append(TableFriendHistory.IS_READ);
        sql.append(" = ");
        sql.append("0");
        //start:add by wangalei for 996
        sql.append(" and ");
        sql.append(TableFriendHistory.C_REQ_ACCOUNT);
        sql.append(" = ");
        sql.append(TableFriendHistory.SHOW_ACCOUNT);
        //end:add by wangalei for 996
        return  sql.toString();
    }


    /**
     *  修正制定账号的历史请求状态
     * @param accounts
     * @param state
     * @return
     */
    public static final String updateHistoriesRequest(List<String> accounts, String state){
        int size = accounts.size();
        StringBuilder sql = new StringBuilder();
        sql.append(" update ");
        sql.append(TableFriendHistory.TABLE_NAME);
        sql.append(" set ");
        sql.append(TableFriendHistory.STATE);
        sql.append(" = ");
        sql.append(state);
        sql.append(" where ");
        sql.append(TableFriendHistory.SHOW_ACCOUNT);
        sql.append(" in ");
        sql.append(" ( ");
        for (int i = 0; i < size; i++) {
            sql.append(accounts.get(i));
            if (i != (size - 1)) {
                sql.append(", ");
            }
        }
        sql.append(" ) ");
        return sql.toString();
    }

    /**
     * 更新未读状态
     * @return
     */
    public static final String updateIsReadSql(){
        StringBuilder sql = new StringBuilder();
        sql.append(" update ");
        sql.append(TableFriendHistory.TABLE_NAME);
        sql.append(" set ");
        sql.append(TableFriendHistory.IS_READ);
        sql.append(" = ");
        sql.append(FriendRequestHistory.READED);
        sql.append(" where ");
        sql.append(TableFriendHistory.IS_READ);
        sql.append(" = ");
        sql.append(FriendRequestHistory.UNREAD);
        return sql.toString();
    }

    /**
     *  获取最大的update serial
     * @return
     */
    public static  final String queryMaxUpdateSerialSql(){
        StringBuilder sql = new StringBuilder();
        sql.append(" select max ( cast ( ");
        sql.append(TableFriendHistory.UPDATE_SERIAL);
        sql.append(" as int ");
        sql.append(" ) )");
        sql.append(" from ");
        sql.append(TableFriendHistory.TABLE_NAME);
        return sql.toString();
    }


}
