package com.xdja.contact.dao.sqlbuilder;

import com.xdja.contact.convert.GroupConvert;
import com.xdja.contact.database.columns.TableGroup;
import com.xdja.contact.database.columns.TableGroupMember;

/**
 * Created by yangpeng on 2015/12/23.
 */
public class GroupDaoSqlBuilder {


    /**
     * 查询本地所有群组
     * @return
     */
    public static final StringBuilder queryGroups(){
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(" select * from ");
        sqlBuilder.append(TableGroup.TABLE_NAME);
        return sqlBuilder;
    }

    /**
     * 根据群id查询群信息
     * @return
     */
    public static final StringBuilder findGroupById(){
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(" select * from " );
        sqlBuilder.append(TableGroup.TABLE_NAME);
        sqlBuilder.append(" where ");
        sqlBuilder.append(TableGroup.GROUP_ID);
        sqlBuilder.append(" = ? ");
        return sqlBuilder;
    }

    /**
     * 按照服务器时间的创建顺序正序排列
     * @return
     */
    /*public static final StringBuilder ascTime(){
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(" order by ");
        sqlBuilder.append(TableGroup.SERVER_CREATE_TIME);
        sqlBuilder.append(" asc ");
        return sqlBuilder;
    }*/
    /**
     * 按照服务器时间的创建顺序倒序排列
     * @return
     */
    public static final StringBuilder descTime(){
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(" order by cast (  ");
        sqlBuilder.append(TableGroup.SERVER_CREATE_TIME);
        sqlBuilder.append(" as int ) ");
        sqlBuilder.append(" desc ");
        return sqlBuilder;
    }


    public static final StringBuilder filterUnDeleteGroups(){
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(" select ");
        sqlBuilder.append(" tgroup. ");
        sqlBuilder.append(TableGroup.ID);
        sqlBuilder.append(", ");
        sqlBuilder.append(" tgroup. ");
        sqlBuilder.append(TableGroup.GROUP_ID);
        sqlBuilder.append(", ");
        sqlBuilder.append(" tgroup. ");
        sqlBuilder.append(TableGroup.GROUP_NAME);
        sqlBuilder.append(", ");
        sqlBuilder.append(" tgroup. ");
        sqlBuilder.append(TableGroup.GROUP_NAME_FULL_PY);
        sqlBuilder.append(", ");
        sqlBuilder.append(" tgroup. ");
        sqlBuilder.append(TableGroup.GROUP_NAME_PY);
        sqlBuilder.append(", ");
        sqlBuilder.append(" tgroup. ");
        sqlBuilder.append(TableGroup.OWNER);
        sqlBuilder.append(", ");
        sqlBuilder.append(" tgroup. ");
        sqlBuilder.append(TableGroup.SERVER_CREATE_TIME);
        sqlBuilder.append(", ");
        sqlBuilder.append(" tgroup. ");
        sqlBuilder.append(TableGroup.AVATAR);
        sqlBuilder.append(", ");
        sqlBuilder.append(" tgroup. ");
        sqlBuilder.append(TableGroup.AVATAR_HASH);
        sqlBuilder.append(", ");
        sqlBuilder.append(" tgroup. ");
        sqlBuilder.append(TableGroup.THUMBNAIL);
        sqlBuilder.append(", ");
        sqlBuilder.append(" tgroup. ");
        sqlBuilder.append(TableGroup.THUMBNAIL_HASH);
        sqlBuilder.append(", ");
        sqlBuilder.append(" tgroup. ");
        sqlBuilder.append(TableGroup.UPDATE_SERIAL);
        sqlBuilder.append(", ");
        sqlBuilder.append(" tgroup. ");
        sqlBuilder.append(TableGroup.IS_DELETED);

        sqlBuilder.append(" from ");
        sqlBuilder.append(TableGroup.TABLE_NAME);
        sqlBuilder.append(" tgroup ");
        sqlBuilder.append(" left join ");
        sqlBuilder.append(TableGroupMember.TABLE_NAME);
        sqlBuilder.append(" group_member ");
        sqlBuilder.append(" on ");
        sqlBuilder.append(" tgroup.c_group_id = group_member.c_group_id  ");

        sqlBuilder.append(" where ");
        sqlBuilder.append(" tgroup.");
        sqlBuilder.append(TableGroup.IS_DELETED);
        sqlBuilder.append(" = ");
        sqlBuilder.append(GroupConvert.UN_DELETED);
        sqlBuilder.append(" and ");
        sqlBuilder.append(" group_member.");
        sqlBuilder.append(TableGroupMember.MEMBER_ACCOUNT);
        sqlBuilder.append(" = ? ");
        sqlBuilder.append(" and ");
        sqlBuilder.append(" group_member.");
        sqlBuilder.append(TableGroupMember.IS_DELETED);
        sqlBuilder.append(" = ");
        sqlBuilder.append(GroupConvert.UN_DELETED);

        sqlBuilder.append(" order by ");
        //modify by ysp@xdja.com fix bug 4610 start
        sqlBuilder.append(" group_member. ");
        sqlBuilder.append(TableGroupMember.SERVER_CREATE_TIME);
        //modify by ysp@xdja.com fix bug 4610 end
        sqlBuilder.append(" desc ");

        return sqlBuilder;
    }

    /**
     * 查询群组内最大序列
     * @return
     */
    public static StringBuilder queryMaxUpdateSerial(){
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(" select max( cast ( ");
        sqlBuilder.append(TableGroup.UPDATE_SERIAL);
        sqlBuilder.append(" as int)) from ");
        sqlBuilder.append(TableGroup.TABLE_NAME);
        return sqlBuilder;
    }

    /**
     * 根据群组名称关键字查找群组
     * @param key
     * @return
     */
    public static StringBuilder queryGroupByKey(CharSequence key) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select * from t_group where ");
        sqlBuilder.append(TableGroup.GROUP_NAME);
        sqlBuilder.append(" like '%");
        sqlBuilder.append(key);
        sqlBuilder.append("%' ");
        return sqlBuilder;
    }

    /**
     * tangsha add for 8110
     * 查询没有群成员信息的群组
     */
    public static StringBuilder queryGroupNoMemberInfo() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(" select ");
        sqlBuilder.append(" tgroup. ");
        sqlBuilder.append(TableGroup.ID);
        sqlBuilder.append(", ");
        sqlBuilder.append(" tgroup. ");
        sqlBuilder.append(TableGroup.GROUP_ID);
        sqlBuilder.append(", ");
        sqlBuilder.append(" tgroup. ");
        sqlBuilder.append(TableGroup.GROUP_NAME);
        sqlBuilder.append(", ");
        sqlBuilder.append(" tgroup. ");
        sqlBuilder.append(TableGroup.GROUP_NAME_FULL_PY);
        sqlBuilder.append(", ");
        sqlBuilder.append(" tgroup. ");
        sqlBuilder.append(TableGroup.GROUP_NAME_PY);
        sqlBuilder.append(", ");
        sqlBuilder.append(" tgroup. ");
        sqlBuilder.append(TableGroup.OWNER);
        sqlBuilder.append(", ");
        sqlBuilder.append(" tgroup. ");
        sqlBuilder.append(TableGroup.SERVER_CREATE_TIME);
        sqlBuilder.append(", ");
        sqlBuilder.append(" tgroup. ");
        sqlBuilder.append(TableGroup.AVATAR);
        sqlBuilder.append(", ");
        sqlBuilder.append(" tgroup. ");
        sqlBuilder.append(TableGroup.AVATAR_HASH);
        sqlBuilder.append(", ");
        sqlBuilder.append(" tgroup. ");
        sqlBuilder.append(TableGroup.THUMBNAIL);
        sqlBuilder.append(", ");
        sqlBuilder.append(" tgroup. ");
        sqlBuilder.append(TableGroup.THUMBNAIL_HASH);
        sqlBuilder.append(", ");
        sqlBuilder.append(" tgroup. ");
        sqlBuilder.append(TableGroup.UPDATE_SERIAL);
        sqlBuilder.append(", ");
        sqlBuilder.append(" tgroup. ");
        sqlBuilder.append(TableGroup.IS_DELETED);

        sqlBuilder.append(" from ");
        sqlBuilder.append(TableGroup.TABLE_NAME);
        sqlBuilder.append(" as tgroup");

        sqlBuilder.append(" left join ");
        sqlBuilder.append(TableGroupMember.TABLE_NAME);
        sqlBuilder.append(" as group_member");

        sqlBuilder.append(" on ");
        sqlBuilder.append(" tgroup.c_group_id = group_member.c_group_id  ");

        sqlBuilder.append(" where ");
        sqlBuilder.append(" tgroup.");
        sqlBuilder.append(TableGroup.IS_DELETED);
        sqlBuilder.append(" = ");
        sqlBuilder.append(GroupConvert.UN_DELETED);
        sqlBuilder.append(" and ");
        sqlBuilder.append(" group_member.");
        sqlBuilder.append(TableGroupMember.GROUP_ID);
        sqlBuilder.append(" is null");
        return sqlBuilder;
    }
}
