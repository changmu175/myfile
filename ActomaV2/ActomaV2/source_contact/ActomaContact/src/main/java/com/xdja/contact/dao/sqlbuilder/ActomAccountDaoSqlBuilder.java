package com.xdja.contact.dao.sqlbuilder;

import com.xdja.contact.database.columns.TableAccountAvatar;
import com.xdja.contact.database.columns.TableActomaAccount;
import com.xdja.contact.database.columns.TableDepartmentMember;
import com.xdja.contact.database.columns.TableFriend;

/**
 * Created by yangpeng on 2015/12/25.
 */
public class ActomAccountDaoSqlBuilder {

    public static  final String queryCommonDetailSql(String account){
        StringBuffer builder = new StringBuffer();
        builder.append(" select ");

        builder.append("member.");
        builder.append(TableDepartmentMember.ACCOUNT);
        builder.append(",member.");
        builder.append(TableDepartmentMember.WORKER_ID);
        builder.append(",member.");
        builder.append(TableDepartmentMember.MEMBER_DEPT_ID);
        builder.append(",member.");
        builder.append(TableDepartmentMember.NAME);
        builder.append(",member.");
        builder.append(TableDepartmentMember.NAME_PY);
        builder.append(",member.");
        builder.append(TableDepartmentMember.NAME_FULL_PY);
        builder.append(",member.");
        builder.append(TableDepartmentMember.SORT);
        builder.append(",member.");
        builder.append(TableDepartmentMember.PHONE);

        builder.append(",avatar.");
        builder.append(TableAccountAvatar.ID);
        builder.append(",avatar.");
        builder.append(TableAccountAvatar.AVATAR);
        builder.append(",avatar.");
        builder.append(TableAccountAvatar.THUMBNAIL);


        builder.append(",friend. ");
        builder.append(TableFriend.ACCOUNT);
        builder.append(",friend.");
        builder.append(TableFriend.REMARK);
        builder.append(",friend.");
        builder.append(TableFriend.REMARK_PY);
        builder.append(",friend.");
        builder.append(TableFriend.REMARK_FULL_PY);
        builder.append(",friend.");
        builder.append(TableFriend.TYPE);
        builder.append(",friend.");
        builder.append(TableFriend.UPDATE_SERIAL);
        builder.append(",friend.");
        builder.append(TableFriend.IS_SHOW);
        builder.append(",friend.");
        builder.append(TableFriend.INITIATIVE);


        builder.append(",account. ");
        builder.append(TableActomaAccount.ACCOUNT);
        builder.append(",account. ");
        builder.append(TableActomaAccount.ALIAS);
        builder.append(",account. ");
        builder.append(TableActomaAccount.FIRST_LOGIN_TIME);
        builder.append(",account. ");
        builder.append(TableActomaAccount.NICKNAME);
        builder.append(",account. ");
        builder.append(TableActomaAccount.NICKNAME_PY);
        builder.append(",account. ");
        builder.append(TableActomaAccount.NICKNAME_FULL_PY);
        builder.append(",account. ");
        builder.append(TableActomaAccount.GENDER);
        builder.append(",account. ");
        builder.append(TableActomaAccount.EMAIL);
        builder.append(",account. ");
        builder.append(TableActomaAccount.ACTIVATE_STATUS);
        builder.append(",account. ");
        builder.append(TableActomaAccount.IDENTIFY);


        builder.append(" from ");

        builder.append(TableActomaAccount.TABLE_NAME);
        builder.append(" account ");


        builder.append(" left join ");
        builder.append(TableDepartmentMember.TABLE_NAME);
        builder.append(" member ");

        builder.append(" on ");
        builder.append(" account.");
        builder.append(TableActomaAccount.ACCOUNT);
        builder.append(" = ");
        builder.append(" member."+TableDepartmentMember.ACCOUNT);

        builder.append(" left join ");
        builder.append(TableFriend.TABLE_NAME);
        builder.append(" friend ");
        builder.append(" on ");
        builder.append(" account.");
        builder.append(TableActomaAccount.ACCOUNT);
        builder.append(" = ");
        builder.append(" friend."+TableFriend.ACCOUNT);

        builder.append(" left join ");
        builder.append(TableAccountAvatar.TABLE_NAME);
        builder.append(" avatar ");
        builder.append(" on ");
        builder.append(" account.");
        builder.append(TableActomaAccount.ACCOUNT);
        builder.append(" = ");
        builder.append(" avatar."+TableAccountAvatar.ACCOUNT);


        builder.append( " where " );
        builder.append(" account." + TableActomaAccount.ACCOUNT + " = " + account);
        return builder.toString();
    }

    public static final String queryByAccounts(String... accounts) {
        int size = accounts.length;
        StringBuffer sql = new StringBuffer();
        sql.append(" select * from ");
        sql.append(TableActomaAccount.TABLE_NAME);
        sql.append(" where ");
        sql.append(TableActomaAccount.ACCOUNT);
        sql.append(" in ");
        sql.append(" ( ");
        for (int i = 0; i < size; i++) {
            sql.append(accounts[i]);
            if (i != (size - 1)) {
                sql.append(", ");
            }
        }
        sql.append(" ) ");
        return sql.toString();
    }
}
