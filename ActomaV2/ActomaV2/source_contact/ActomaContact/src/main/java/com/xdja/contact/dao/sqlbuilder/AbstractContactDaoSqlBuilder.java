package com.xdja.contact.dao.sqlbuilder;

import com.xdja.contact.database.columns.TableAccountAvatar;
import com.xdja.contact.database.columns.TableActomaAccount;
import com.xdja.contact.database.columns.TableDepartmentMember;
import com.xdja.contact.database.columns.TableFriend;
import com.xdja.contact.database.columns.TableGroupMember;
import com.xdja.comm.uitl.ObjectUtil;

import java.util.List;

/**
 * Created by yangpeng on 2015/12/23.
 */
public class AbstractContactDaoSqlBuilder {

    public static final StringBuilder queryCommonNameSql(String groupId,List<String> accounts){

        StringBuilder sqlBuilder = new StringBuilder();
        //start:add by wal@xdja.com for 2575
        sqlBuilder.append(" select distinct" );

        sqlBuilder.append(" actomaAccount.");
        sqlBuilder.append(TableActomaAccount.ACCOUNT);
        sqlBuilder.append(" , ");
        //end:add by wal@xdja.com for 2575

        sqlBuilder.append(" friend. ");
        sqlBuilder.append(TableFriend.REMARK);
        sqlBuilder.append(" , ");

        sqlBuilder.append(" departMember. ");
        sqlBuilder.append(TableDepartmentMember.NAME);
        sqlBuilder.append(" , ");


        sqlBuilder.append(" actomaAccount. ");
        sqlBuilder.append(TableActomaAccount.NICKNAME);
        sqlBuilder.append(" , ");

        sqlBuilder.append(" avatar. ");
        sqlBuilder.append(TableAccountAvatar.THUMBNAIL);
        sqlBuilder.append(" , ");


        sqlBuilder.append(" avatar. ");
        sqlBuilder.append(TableAccountAvatar.AVATAR);

        //start:add by wal@xdja.com for 2575
        if(!ObjectUtil.stringIsEmpty(groupId)){
            sqlBuilder.append(" , ");
            sqlBuilder.append(" groupMember. ");
            sqlBuilder.append(TableGroupMember.MEMBER_NICKNAME);
//            sqlBuilder.append(" actomaAccount.");
//            sqlBuilder.append(TableActomaAccount.ACCOUNT);
        }
        //end:add by wal@xdja.com for 2575

//add by lwl start  add alias
        sqlBuilder.append(" , ");
        sqlBuilder.append(" actomaAccount.");
        sqlBuilder.append(TableActomaAccount.ALIAS);
//add by lwl end  add alias


        sqlBuilder.append(" from ");
        sqlBuilder.append(TableActomaAccount.TABLE_NAME);
        sqlBuilder.append(" as actomaAccount");



        sqlBuilder.append(" left join ");
        sqlBuilder.append(TableGroupMember.TABLE_NAME);
        sqlBuilder.append(" as groupMember");
        sqlBuilder.append(" on ");
        sqlBuilder.append(" groupMember.");
        sqlBuilder.append(TableGroupMember.MEMBER_ACCOUNT);
        sqlBuilder.append(" = ");
        sqlBuilder.append(" actomaAccount.");
        sqlBuilder.append(TableActomaAccount.ACCOUNT);


        sqlBuilder.append(" left join ");
        sqlBuilder.append(TableFriend.TABLE_NAME);
        sqlBuilder.append(" as friend ");
        sqlBuilder.append(" on ");
        sqlBuilder.append(" actomaAccount.");
        sqlBuilder.append(TableActomaAccount.ACCOUNT);
        sqlBuilder.append(" = ");
        sqlBuilder.append(" friend. ");
        sqlBuilder.append(TableFriend.ACCOUNT);

        sqlBuilder.append(" left join ");
        sqlBuilder.append(TableDepartmentMember.TABLE_NAME);
        sqlBuilder.append(" as departMember");
        sqlBuilder.append(" on ");
        sqlBuilder.append(" actomaAccount.");
        sqlBuilder.append(TableActomaAccount.ACCOUNT);
        sqlBuilder.append(" = ");
        sqlBuilder.append(" departMember.");
        sqlBuilder.append(TableDepartmentMember.ACCOUNT);

        sqlBuilder.append(" left join ");
        sqlBuilder.append(TableAccountAvatar.TABLE_NAME);
        sqlBuilder.append(" as avatar");
        sqlBuilder.append(" on ");
        sqlBuilder.append(" actomaAccount.");
        sqlBuilder.append(TableActomaAccount.ACCOUNT);
        sqlBuilder.append(" = ");
        sqlBuilder.append(" avatar.");
        sqlBuilder.append(TableAccountAvatar.ACCOUNT);

        sqlBuilder.append( " where " );

        /*sqlBuilder.append(TableFriend.IS_SHOW);
        sqlBuilder.append( " = ");
        sqlBuilder.append(" 1 ");
        sqlBuilder.append(" and ");*/

        if(!ObjectUtil.stringIsEmpty(groupId)){
            sqlBuilder.append( " groupMember. ");
            sqlBuilder.append(TableGroupMember.GROUP_ID);
            sqlBuilder.append(" = ");
            sqlBuilder.append(groupId);
            sqlBuilder.append(" and ");
        }

        sqlBuilder.append(" actomaAccount.");
        sqlBuilder.append(TableActomaAccount.ACCOUNT);
        sqlBuilder.append(" in ");
        sqlBuilder.append(" ( ");
        int size = accounts.size();
        for(int i = 0 ; i < size; i ++){
            sqlBuilder.append( accounts.get(i));
            if (i != (size -1)) {
                sqlBuilder.append( ", " );
            }
        }
        sqlBuilder.append(" ) ");
        return sqlBuilder;
    }
}
