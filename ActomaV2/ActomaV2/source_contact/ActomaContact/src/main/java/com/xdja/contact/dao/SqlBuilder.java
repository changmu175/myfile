package com.xdja.contact.dao;

import com.xdja.contact.database.columns.TableActomaAccount;
import com.xdja.contact.database.columns.TableDepartmentMember;
import com.xdja.contact.database.columns.TableFriend;
import com.xdja.contact.database.columns.TableGroup;
import com.xdja.contact.database.columns.TableGroupMember;

import java.util.List;

/**
 * Created by wanghao on 2015/12/17.
 */
public final class SqlBuilder {

    /**
     * 查询本地所有群组
     * @return
     */
    public static final StringBuilder queryLocalGroupsSql(){
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(" select * from ");
        sqlBuilder.append(TableGroup.TABLE_NAME);
        sqlBuilder.append(" order by ");
        sqlBuilder.append(TableGroup.SERVER_CREATE_TIME);
        sqlBuilder.append(" asc ");
        return sqlBuilder;
    }

    /**
     * 根据批量的群组id查询对应的群成员信息
     * @param groupIds
     * @return
     */
    public static final StringBuilder queryGroupMembersByGroupIds(List<String> groupIds){
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(" select * from " );
        sqlBuilder.append(TableGroupMember.TABLE_NAME);
        sqlBuilder.append( " where " );
        sqlBuilder.append(TableGroupMember.GROUP_ID);
        sqlBuilder.append(" in ");
        sqlBuilder.append(" ( ");
        int size = groupIds.size();
        for(int i = 0 ; i < size; i ++){
            sqlBuilder.append( groupIds.get(i));
            if (!(i == (size -1))) {
                sqlBuilder.append( ", " );
            }
        }
        sqlBuilder.append(" ) ");
        return sqlBuilder;
    }

    public static final StringBuilder queryCommonName(List<String> accounts){

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(" select " );
        sqlBuilder.append(" distinct ");
        sqlBuilder.append(" friend. ");
        sqlBuilder.append(TableFriend.REMARK);
        sqlBuilder.append(" , ");

        sqlBuilder.append(" departMember. ");
        sqlBuilder.append(TableDepartmentMember.NAME);
        sqlBuilder.append(" , ");


        sqlBuilder.append(" actomaAccount. ");
        sqlBuilder.append(TableActomaAccount.NICKNAME);
        sqlBuilder.append(" , ");

        sqlBuilder.append(" groupMember.");
        sqlBuilder.append(TableGroupMember.MEMBER_ACCOUNT);
        sqlBuilder.append(" from ");

        sqlBuilder.append(TableGroupMember.TABLE_NAME);
        sqlBuilder.append(" as groupMember");

        sqlBuilder.append(" left join ");
        sqlBuilder.append(TableFriend.TABLE_NAME);
        sqlBuilder.append(" as friend ");
        sqlBuilder.append(" on ");
        sqlBuilder.append(" groupMember.");
        sqlBuilder.append(TableGroupMember.MEMBER_ACCOUNT);
        sqlBuilder.append(" = ");
        sqlBuilder.append(" friend. ");
        sqlBuilder.append(TableFriend.ACCOUNT);

        sqlBuilder.append(" left join ");
        sqlBuilder.append(TableDepartmentMember.TABLE_NAME);
        sqlBuilder.append(" as departMember");
        sqlBuilder.append(" on ");
        sqlBuilder.append(" groupMember.");
        sqlBuilder.append(TableGroupMember.MEMBER_ACCOUNT);
        sqlBuilder.append(" = ");
        sqlBuilder.append(" departMember.");
        sqlBuilder.append(TableDepartmentMember.ACCOUNT);


        sqlBuilder.append(" left join ");
        sqlBuilder.append(TableActomaAccount.TABLE_NAME);
        sqlBuilder.append(" as actomaAccount");
        sqlBuilder.append(" on ");
        sqlBuilder.append(" groupMember.");
        sqlBuilder.append(TableGroupMember.MEMBER_ACCOUNT);
        sqlBuilder.append(" = ");
        sqlBuilder.append(" actomaAccount.");
        sqlBuilder.append(TableActomaAccount.ACCOUNT);

        sqlBuilder.append( " where " );
        sqlBuilder.append(" groupMember.");
        sqlBuilder.append(TableGroupMember.MEMBER_ACCOUNT);
        sqlBuilder.append(" in ");
        sqlBuilder.append(" ( ");
        int size = accounts.size();
        for(int i = 0 ; i < size; i ++){
            sqlBuilder.append( accounts.get(i));
            if (!(i == (size -1))) {
                sqlBuilder.append( ", " );
            }
        }
        sqlBuilder.append(" ) ");
        return sqlBuilder;
    }







}
