package com.xdja.contact.dao.sqlbuilder;

import android.support.annotation.NonNull;

import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.database.columns.TableAccountAvatar;
import com.xdja.contact.database.columns.TableActomaAccount;
import com.xdja.contact.database.columns.TableDepartmentMember;
import com.xdja.contact.database.columns.TableFriend;

/**
 * Created by yangpeng on 2015/12/24.
 */
public class FriendDaoSqlBuilder {


    public static final String findByIdSql(String account){
        StringBuilder sql = new StringBuilder();
        sql.append(" select * from ");
        sql.append(TableFriend.TABLE_NAME);
        sql.append(" where ");
        sql.append(TableFriend.ACCOUNT);
        sql.append(" = ");
        sql.append(account);

        return  sql.toString();
    }

    public static final String queryAllFriendSql() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(" select ");
        stringBuffer.append(" friend."+TableFriend.ACCOUNT+" as " + TableFriend.ACCOUNT);
        stringBuffer.append(",");
        stringBuffer.append(TableFriend.REMARK);
        stringBuffer.append(",");
        stringBuffer.append(TableFriend.REMARK_PY);
        stringBuffer.append(",");
        stringBuffer.append(TableFriend.REMARK_FULL_PY);
        stringBuffer.append(",");
        stringBuffer.append(TableFriend.IS_SHOW);
        stringBuffer.append(",");
        stringBuffer.append(TableFriend.TYPE);
        stringBuffer.append(",");
        stringBuffer.append(TableFriend.UPDATE_SERIAL);
        stringBuffer.append(",");
        stringBuffer.append(TableFriend.INITIATIVE);
        stringBuffer.append(",");



        stringBuffer.append(TableActomaAccount.ALIAS);
        stringBuffer.append(",");
        stringBuffer.append(TableActomaAccount.NICKNAME);
        stringBuffer.append(",");
        stringBuffer.append(TableActomaAccount.NICKNAME_PY);
        stringBuffer.append(",");
        stringBuffer.append(TableActomaAccount.NICKNAME_FULL_PY);
        stringBuffer.append(",");
        //start:fix 1212 by wal@xdja.com
        stringBuffer.append(TableActomaAccount.IDENTIFY);
        stringBuffer.append(",");
        //end:fix 1212 by wal@xdja.com

        stringBuffer.append(TableAccountAvatar.AVATAR);
        stringBuffer.append(",");
        stringBuffer.append(TableAccountAvatar.THUMBNAIL);



        stringBuffer.append(",");
        stringBuffer.append(TableDepartmentMember.NAME);
        stringBuffer.append(",");
        stringBuffer.append(TableDepartmentMember.NAME_FULL_PY);
        stringBuffer.append(",");
        stringBuffer.append(TableDepartmentMember.NAME_PY);



        stringBuffer.append(" from ");
        stringBuffer.append(TableFriend.TABLE_NAME + " as friend ");
        stringBuffer.append(" left join ");

        stringBuffer.append(TableActomaAccount.TABLE_NAME);
        stringBuffer.append(" as account ");
        stringBuffer.append(" on ");
        stringBuffer.append(" friend."+ TableFriend.ACCOUNT);
        stringBuffer.append(" = ");
        stringBuffer.append(" account."+TableActomaAccount.ACCOUNT);

        stringBuffer.append(" left join ");
        stringBuffer.append(TableAccountAvatar.TABLE_NAME);
        stringBuffer.append(" as avatar ");
        stringBuffer.append(" on ");
        stringBuffer.append(" friend."+TableFriend.ACCOUNT);
        stringBuffer.append(" = ");
        stringBuffer.append(" avatar." + TableAccountAvatar.ACCOUNT);

        stringBuffer.append(" left join ");
        stringBuffer.append(TableDepartmentMember.TABLE_NAME);
        stringBuffer.append(" as member ");
        stringBuffer.append(" on ");
        stringBuffer.append(" friend."+ TableFriend.ACCOUNT);
        stringBuffer.append(" = ");
        stringBuffer.append(" member."+TableDepartmentMember.ACCOUNT);

        return stringBuffer.toString();
    }


    public static final String whereShowFriendSql(){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(" where ");
        stringBuffer.append(" friend." + TableFriend.IS_SHOW);
        stringBuffer.append(" = ");
        stringBuffer.append(" 1 ");
        return stringBuffer.toString();
    }

    @NonNull
    public static final StringBuffer baseSqlBuilder() {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT  ");

        sql.append("a.");
        sql.append(TableFriend.ID);
        sql.append(",");
        sql.append("a.");
        sql.append(TableFriend.ACCOUNT);
        sql.append(",");

        sql.append("f.");
        sql.append(TableFriend.REMARK);
        sql.append(",");
        sql.append("f.");
        sql.append(TableFriend.IS_SHOW);
        sql.append(",");
        sql.append("f.");
        sql.append(TableFriend.REMARK_FULL_PY);
        sql.append(",");
        sql.append("f.");
        sql.append(TableFriend.REMARK_PY);
        sql.append(",");
        sql.append("f.");
        sql.append(TableFriend.TYPE);
        sql.append(",");
        sql.append("f.");
        sql.append(TableFriend.UPDATE_SERIAL);
        sql.append(",");
        sql.append("f.");
        sql.append(TableFriend.INITIATIVE);
        sql.append(",");

        //Account字段
        sql.append("a.");
        sql.append(TableActomaAccount.ALIAS);
        sql.append(",");
        sql.append("a.");
        sql.append(TableActomaAccount.ACTIVATE_STATUS);
        sql.append(",");
        sql.append("a.");
        sql.append(TableActomaAccount.BIND_PHONE);
        sql.append(",");
        sql.append("a.");
        sql.append(TableActomaAccount.EMAIL);
        sql.append(",");
        sql.append("a.");
        sql.append(TableActomaAccount.FIRST_LOGIN_TIME);
        sql.append(",");
        sql.append("a.");
        sql.append(TableActomaAccount.GENDER);
        sql.append(",");
        sql.append("a.");
        sql.append(TableActomaAccount.NICKNAME);
        sql.append(",");
        sql.append("a.");
        sql.append(TableActomaAccount.NICKNAME_FULL_PY);
        sql.append(",");
        sql.append("a.");
        sql.append(TableActomaAccount.NICKNAME_PY);
        sql.append(",");
        sql.append("a.");
        sql.append(TableActomaAccount.IDENTIFY);
        //头像表信息
        sql.append(",");
        sql.append("av.");
        sql.append(TableAccountAvatar.AVATAR);
        sql.append(",");
        sql.append("av.");
        sql.append(TableAccountAvatar.THUMBNAIL);
        sql.append(",");

        //集团通讯录成员信息
        sql.append("m.");
        sql.append(TableDepartmentMember.NAME);
        sql.append(",");
        sql.append("m.");
        sql.append(TableDepartmentMember.NAME_PY);
        sql.append(",");
        sql.append("m.");
        sql.append(TableDepartmentMember.NAME_FULL_PY);
        /*sql.append(",");
        sql.append("m.");
        sql.append(TableDepartmentMember.C_REQ_ACCOUNT);*/
        /*sql.append(",");
        sql.append(TableDepartmentMember.WORKER_ID);
        sql.append(",");
        sql.append(TableDepartmentMember.SORT);
*/
        sql.append(" FROM ");
        sql.append(TableFriend.TABLE_NAME);
        sql.append(" f ");
        sql.append(" LEFT JOIN ");
        sql.append(TableActomaAccount.TABLE_NAME);
        sql.append(" a ");
        sql.append(" ON ");

        sql.append(" f. ");
        sql.append(TableFriend.ACCOUNT);
        sql.append(" = ");
        sql.append(" a.");
        sql.append(TableActomaAccount.ACCOUNT);
        sql.append(" LEFT JOIN ");
        sql.append(TableAccountAvatar.TABLE_NAME);
        sql.append(" av ");
        sql.append(" ON ");
        sql.append(" f.");
        sql.append(TableFriend.ACCOUNT);
        sql.append(" = ");
        sql.append(" av.");
        sql.append(TableAccountAvatar.ACCOUNT);
        sql.append(" LEFT JOIN ");
        sql.append(TableDepartmentMember.TABLE_NAME);
        sql.append(" m ");
        sql.append(" ON ");
        sql.append("f.");
        sql.append(TableFriend.ACCOUNT);
        sql.append(" = ");
        sql.append(" m.");
        sql.append(TableDepartmentMember.ACCOUNT);
        LogUtil.getUtils().d("好友查询SQL:" + sql.toString());
        return sql;
    }

    public static final StringBuilder queryFriendSql(String account, boolean containIsDel){
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(" where f.");
        sqlBuilder.append(TableFriend.ACCOUNT);
        sqlBuilder.append(" = ");
        sqlBuilder.append(account);
        if (!containIsDel) {
            sqlBuilder.append(" AND ");
            sqlBuilder.append(TableFriend.IS_SHOW);
            sqlBuilder.append(" = 1 ");
        }
        return sqlBuilder;
    }

    public static final StringBuilder searchSql(String key){
        StringBuilder sql =new StringBuilder();
        sql.append(" where ");

        sql.append("f.");
        sql.append(TableFriend.IS_SHOW);
        sql.append(" = ");
        sql.append("'1'");

        //modify by lwl start keywork  alias account
        sql.append(" and ");

        sql.append("(");
        sql.append("(");
        sql.append(TableActomaAccount.ALIAS);
        sql.append(" is not null ");
        sql.append(" and ");
        sql.append("(a.");
        sql.append(TableActomaAccount.ALIAS);
        sql.append(" like ");
        sql.append(builderSearchArgs(key));
        sql.append(getKeywordOr(key));
        sql.append(")");
        sql.append(")");

        sql.append(" or ");
        sql.append("(");
        sql.append(TableActomaAccount.ALIAS);
        sql.append(" is null ");
        sql.append(" and ");
        sql.append("(a.");
        sql.append(TableActomaAccount.ACCOUNT);
        sql.append(" like ");
        sql.append(builderSearchArgs(key));
        sql.append(getKeywordOr(key));
        sql.append(")");
        sql.append(")");

        sql.append(")");
        //modify by lwl end

        return sql;

    }
    //add by lwl  or REMARK REMARK_PY NICKNAME NAME NAME_PY NAME_FULL_PY  REMARK_FULL_PY NICKNAME_FULL_PY NICKNAME_PY
    public static String  getKeywordOr(String key){
        StringBuilder sql=new StringBuilder();
        sql.append(" or ");
        sql.append(TableFriend.REMARK);
        sql.append(" like ");
        sql.append(builderSearchArgs(key));

        sql.append(" or ");
        sql.append(TableFriend.REMARK_PY);
        sql.append(" like ");
        sql.append(builderSearchArgs(key));

        sql.append(" or ");
        sql.append(TableActomaAccount.NICKNAME);
        sql.append(" like");
        sql.append(builderSearchArgs(key));
        //增加通讯录表关联
        sql.append(" or ");
        sql.append(TableDepartmentMember.NAME);
        sql.append(" like ");
        sql.append(builderSearchArgs(key));

        sql.append(" or ");
        sql.append(TableDepartmentMember.NAME_PY);
        sql.append(" like ");
        sql.append(builderSearchArgs(key));

        sql.append(" or ");
        sql.append(TableDepartmentMember.NAME_FULL_PY);
        sql.append("  like ");
        sql.append(builderSearchArgs(key));

        sql.append(" or ");
        if (key.length() > 1) {
            sql.append(TableFriend.REMARK_FULL_PY);
            sql.append(" like ");
            sql.append(builderSearchArgs(key));

            sql.append(" or ");
            sql.append(TableActomaAccount.NICKNAME_FULL_PY);
            sql.append(" like ");
            sql.append(builderSearchArgs(key));

            sql.append(" or ");
        }
        sql.append(TableActomaAccount.NICKNAME_PY);
        sql.append(" like ");
        sql.append(builderSearchArgs(key));
        return  sql.toString();
    }

    /**
     * 搜索时根据输入的搜索条件拼装模糊查询需要的关键字
     *
     * @param searchKey
     * @return
     */
    public static String builderSearchArgs(String searchKey) {
        StringBuilder builder = new StringBuilder();
        builder.append("'");
        builder.append("%");
        builder.append(searchKey);
        builder.append("%");
        builder.append("'");
        return builder.toString();
    }

    /**
     * <pre>
     *     根据输入的账号或者手机搜索本地好友，过滤已经删除的好友
     * </pre>
     * @param key 账号或者手机号
     * @return StringBuffer
     */
    public static final StringBuffer buildLocalSearchSql(String key) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(" select ");
        stringBuffer.append(" friend."+TableFriend.ACCOUNT+" as " + TableFriend.ACCOUNT);
        stringBuffer.append(",");
        stringBuffer.append(TableFriend.REMARK);
        stringBuffer.append(",");
        stringBuffer.append(TableFriend.REMARK_PY);
        stringBuffer.append(",");
        stringBuffer.append(TableFriend.REMARK_FULL_PY);
        stringBuffer.append(",");
        stringBuffer.append(TableFriend.IS_SHOW);
        stringBuffer.append(",");
        stringBuffer.append(TableFriend.TYPE);
        stringBuffer.append(",");
        stringBuffer.append(TableFriend.UPDATE_SERIAL);
        stringBuffer.append(",");
        stringBuffer.append(TableFriend.INITIATIVE);
        stringBuffer.append(",");
        //add by lwl alises  start
        stringBuffer.append(TableActomaAccount.ALIAS);
        stringBuffer.append(",");
        //add by lwl alises  start
        stringBuffer.append(TableActomaAccount.NICKNAME);
        stringBuffer.append(",");
        stringBuffer.append(TableActomaAccount.NICKNAME_PY);
        stringBuffer.append(",");
        stringBuffer.append(TableActomaAccount.NICKNAME_FULL_PY);
        stringBuffer.append(",");
        stringBuffer.append(TableActomaAccount.BIND_PHONE);

        stringBuffer.append(" from ");
        stringBuffer.append(TableFriend.TABLE_NAME + " as friend ");
        stringBuffer.append(" left join ");

        stringBuffer.append(TableActomaAccount.TABLE_NAME);
        stringBuffer.append(" as account ");
        stringBuffer.append(" on ");
        stringBuffer.append(" friend."+ TableFriend.ACCOUNT);
        stringBuffer.append(" = ");
        stringBuffer.append(" account."+TableActomaAccount.ACCOUNT);

        stringBuffer.append(" where ");

        stringBuffer.append("friend.");
        stringBuffer.append(TableFriend.IS_SHOW);
        stringBuffer.append(" = ");
        stringBuffer.append("'1'");

        stringBuffer.append(" and ");
        stringBuffer.append(" (account.");
        stringBuffer.append(TableActomaAccount.ALIAS);
        stringBuffer.append(" = ");
        //start:add by wal@xdja.com for 683
        stringBuffer.append("'");
        //end:add by wal@xdja.com for 683
        stringBuffer.append(key);
        //start:add by wal@xdja.com for 683
        stringBuffer.append("'");
        //end:add by wal@xdja.com for 683

        stringBuffer.append(" or ");
        stringBuffer.append(" account.");
        stringBuffer.append(TableActomaAccount.BIND_PHONE);
        stringBuffer.append(" = ");
        //start:add by wal@xdja.com for 683
        stringBuffer.append("'");
        //end:add by wal@xdja.com for 683
        stringBuffer.append(key);
        //start:add by wal@xdja.com for 683
        stringBuffer.append("'");
        //end:add by wal@xdja.com for 683

        //*/add by wal@xdja.com for 672
        stringBuffer.append(" or ");
        stringBuffer.append(" account.");
        stringBuffer.append(TableActomaAccount.ACCOUNT);
        stringBuffer.append(" = ");
        //start:add by wal@xdja.com for 683
        stringBuffer.append("'");
        //end:add by wal@xdja.com for 683
        stringBuffer.append(key);
        //start:add by wal@xdja.com for 683
        stringBuffer.append("'");
        //end:add by wal@xdja.com for 683
        //*/
        stringBuffer.append(")");
        return stringBuffer;
    }

    /**
     * 获取最大的update serial
     * @return
     */
    public static final String queryMaxUpdateSerialSql(){
        StringBuilder sql = new StringBuilder();
        sql.append("select max( cast ( ");
        sql.append(TableFriend.UPDATE_SERIAL);
        sql.append(" as int ) )");
        sql.append(" from "+TableFriend.TABLE_NAME);
        return sql.toString();
    }
}
