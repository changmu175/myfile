package com.xdja.contact.dao.sqlbuilder;

import com.xdja.contact.convert.GroupConvert;
import com.xdja.contact.database.columns.TableAccountAvatar;
import com.xdja.contact.database.columns.TableActomaAccount;
import com.xdja.contact.database.columns.TableDepartmentMember;
import com.xdja.contact.database.columns.TableFriend;
import com.xdja.contact.database.columns.TableGroup;
import com.xdja.contact.database.columns.TableGroupMember;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yangpeng on 2015/12/23.
 */
public class GroupMemberDaoSqlBuilder {

    /**
     * 根据群id查询对应的群成员，同时关联好友表和账户表 和头像表
     * @return
     */
    public static final StringBuilder queryUserInfoByGroupId(){
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(" select ");

        sqlBuilder.append(" distinct ");  //add by ysp, fix bug #8525.
        sqlBuilder.append(" members. ");
        sqlBuilder.append(TableGroupMember.MEMBER_ACCOUNT);
        sqlBuilder.append(" ,");
        sqlBuilder.append(" members. ");
        sqlBuilder.append(TableGroupMember.MEMBER_NICKNAME);
        sqlBuilder.append(" ,");
        sqlBuilder.append(" friend. ");
        sqlBuilder.append(TableFriend.REMARK);
        sqlBuilder.append(" ,");
        sqlBuilder.append(" account. ");
        sqlBuilder.append(TableActomaAccount.NICKNAME);
        sqlBuilder.append(" ,");
        //add by lwl start
        sqlBuilder.append(" account. ");
        sqlBuilder.append(TableActomaAccount.ALIAS);
        sqlBuilder.append(" ,");
        //add by lwl end
        //add by wal@xdja.com  start for 1738
        sqlBuilder.append(" tdepartmembers. ");
        sqlBuilder.append(TableDepartmentMember.NAME);
        sqlBuilder.append(" ,");
        //add by wal@xdja.com end for 1738
        sqlBuilder.append(" avatar.* ");

        sqlBuilder.append(" from ");
        sqlBuilder.append(TableGroup.TABLE_NAME);
        sqlBuilder.append(" as tgroup");


        sqlBuilder.append(" left join ");
        sqlBuilder.append(TableGroupMember.TABLE_NAME);
        sqlBuilder.append(" as members ");

        sqlBuilder.append(" on ");
        sqlBuilder.append(" tgroup. ");
        sqlBuilder.append(TableGroup.GROUP_ID);
        sqlBuilder.append(" = ");
        sqlBuilder.append(" members. ");
        sqlBuilder.append(TableGroupMember.GROUP_ID);



        sqlBuilder.append(" left join ");
        sqlBuilder.append(TableActomaAccount.TABLE_NAME);
        sqlBuilder.append(" as account ");
        sqlBuilder.append(" on ");
        sqlBuilder.append(" members. ");
        sqlBuilder.append(TableGroupMember.MEMBER_ACCOUNT);
        sqlBuilder.append(" = ");
        sqlBuilder.append(" account. ");
        sqlBuilder.append(TableActomaAccount.ACCOUNT);


        sqlBuilder.append(" left join ");
        sqlBuilder.append(TableFriend.TABLE_NAME);
        sqlBuilder.append(" as friend ");
        sqlBuilder.append(" on ");
        sqlBuilder.append(" members. ");
        sqlBuilder.append(TableGroupMember.MEMBER_ACCOUNT);
        sqlBuilder.append(" = ");
        sqlBuilder.append(" friend. ");
        sqlBuilder.append(TableFriend.ACCOUNT);

        //add by wal@xdja.com  start for 1738
        sqlBuilder.append(" left join ");
        sqlBuilder.append(TableDepartmentMember.TABLE_NAME);
        sqlBuilder.append(" as tdepartmembers ");
        sqlBuilder.append(" on ");
        sqlBuilder.append(" members. ");
        sqlBuilder.append(TableGroupMember.MEMBER_ACCOUNT);
        sqlBuilder.append(" = ");
        sqlBuilder.append(" tdepartmembers. ");
        sqlBuilder.append(TableDepartmentMember.ACCOUNT);
        //add by wal@xdja.com  end for 1738

        sqlBuilder.append(" left join ");
        sqlBuilder.append(TableAccountAvatar.TABLE_NAME);
        sqlBuilder.append(" as avatar ");
        sqlBuilder.append(" on ");
        sqlBuilder.append(" members. ");
        sqlBuilder.append(TableGroupMember.MEMBER_ACCOUNT);
        sqlBuilder.append(" = ");
        sqlBuilder.append(" avatar. ");
        sqlBuilder.append(TableAccountAvatar.ACCOUNT);

        sqlBuilder.append(" where tgroup. ");
        sqlBuilder.append(TableGroup.GROUP_ID);
        sqlBuilder.append(" = ? ");
        sqlBuilder.append(" and ");
        sqlBuilder.append(" members.");
        sqlBuilder.append(TableGroupMember.IS_DELETED);
        sqlBuilder.append(" = ");
        sqlBuilder.append(GroupConvert.UN_DELETED);

        sqlBuilder.append(" order by cast ( members. ");
        sqlBuilder.append(TableGroupMember.SERVER_CREATE_TIME);
        sqlBuilder.append(" as int ) ");
        sqlBuilder.append(" asc ");
        return sqlBuilder;
    }




    /**
     * 根据批量的群组id查询对应的群成员信息
     * @param groupIds
     * @return
     */
    public static final StringBuilder queryGroupMembersByGroupIdsSql(List<String> groupIds){
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
            if (i != (size -1)) {
                sqlBuilder.append( ", " );
            }
        }
        sqlBuilder.append(" ) ");
        sqlBuilder.append(" and ");
        sqlBuilder.append(TableGroupMember.IS_DELETED);
        sqlBuilder.append(" = ");
        sqlBuilder.append(GroupConvert.UN_DELETED);
        return sqlBuilder;
    }


    /**
     * GroupMemberDao 查询群组成员对象------根据搜索输入的条件查询群组SQL语句构造器
     * @param searchKey
     * @return
     */
    public static final String groupMmemberBuildSearchSql(String searchKey){
        String searchArgs = builderSearchArgs(searchKey);
        StringBuilder sb = new StringBuilder();
        sb.append(" select * from " );
        sb.append(TableGroupMember.TABLE_NAME);
        sb.append(" where ");
        sb.append(TableGroupMember.MEMBER_ACCOUNT);
        sb.append(" like ");
        sb.append(searchArgs);
        sb.append(" or ");
        sb.append(TableGroupMember.MEMBER_NICKNAME);
        sb.append(" like ");
        sb.append(searchArgs);
        sb.append(" or ");
        sb.append(TableGroupMember.NICKNAME_FULL_PY);
        sb.append(" like ");
        sb.append(searchArgs);
        sb.append(" or ");
        sb.append(TableGroupMember.NICKNAME_PY);
        sb.append(" like ");
        sb.append(searchArgs);
        return sb.toString();
    }


    /**
     * GroupMemberDao搜索时根据输入的搜索条件拼装模糊查询需要的关键字
     * @param searchKey
     * @return
     */
    public static String builderSearchArgs(String searchKey){
        StringBuilder builder = new StringBuilder();
        builder.append("'");
        builder.append("%");
        builder.append(searchKey);
        builder.append("%");
        builder.append("'");
        return builder.toString();
    }

    /**
     * GroupMemberDao根据id查询成员sql语句构造器
     * @param maps
     * @return
     */
    public static final String buildQuerySql(Map<String,String> maps){
        int size = maps.size();
        Set<String> keys = maps.keySet();
        String[] keyArray = keys.toArray(new String[0]);
        StringBuffer buffer = new StringBuffer();
        buffer.append("  select * from ");
        buffer.append(TableGroupMember.TABLE_NAME);
        buffer.append( " where " );
        buffer.append(TableGroupMember.GROUP_ID);
        buffer.append(" in ");
        buffer.append(" ( ");
        for(int i = 0 ; i < size; i ++){
            buffer.append(maps.get(keyArray[i]));
            if (i != (size -1)) {
                buffer.append( ", " );
            }
        }
        buffer.append(" ) ");
        buffer.append(" and ");

        buffer.append(TableGroupMember.MEMBER_ACCOUNT);
        buffer.append(" in ");
        buffer.append(" ( ");
        for(int i = 0 ; i < size; i ++){
            buffer.append(keyArray[i]);
            if (i != (size -1)) {
                buffer.append( ", " );
            }
        }
        buffer.append(" ) ");

        return buffer.toString();
    }

    /**
     *  获取指定群组的成员更新序号
     * @param groupIds
     * @return
     */
    public static String updateSerialMapSql(List<String> groupIds){
        StringBuilder builder = new StringBuilder();
        int size = groupIds.size();
        builder.append(" select ");
        builder.append(TableGroupMember.GROUP_ID);
        builder.append(" , max( cast( ");
        builder.append(TableGroupMember.UPDATE_SERIAL);
        builder.append(" as int) ) as lastSerial ");
        builder.append(" from ");
        builder.append(TableGroupMember.TABLE_NAME);

        builder.append( " where " );
        builder.append(TableGroupMember.GROUP_ID);
        builder.append(" in ");
        builder.append(" ( ");
        for(int i = 0 ; i < size; i ++){
            builder.append(groupIds.get(i));
            if (i != (size -1)) {
                builder.append( ", " );
            }
        }
        builder.append(" ) ");
        builder.append(" group by ");
        builder.append(TableGroupMember.GROUP_ID);
        return builder.toString();
    }

    /**
     * 根据ID查找群组成员sql语句构造器
     * @param groudId
     * @param account
     * @return
     */
    public static final String groupMemberQuerySql(String groudId, String account){
        StringBuilder builder = new StringBuilder();
        builder.append("select * from ");
        builder.append(TableGroupMember.TABLE_NAME);
        builder.append(" where ");
        builder.append(TableGroupMember.GROUP_ID);
        builder.append(" = ");
        builder.append(groudId);
        builder.append(" AND ");
        builder.append(TableGroupMember.MEMBER_ACCOUNT);
        builder.append(" = ");
        builder.append(account);
        return builder.toString();
    }

    /**
     * 查询群成员key Map
     * @return
     */
    public static final String queryGroupMemberKeyMap(){
        StringBuilder builder = new StringBuilder();
        builder.append(" select ");
        builder.append(TableGroupMember.GROUP_ID);
        builder.append(",");
        builder.append(TableGroupMember.MEMBER_ACCOUNT);
        builder.append(",");
        builder.append(TableGroupMember.INVITE_ACOCUNT);//add by lwl 3005
        builder.append(" from ");
        builder.append(TableGroupMember.TABLE_NAME);
        return builder.toString();

    }

    //add by ysp@xdja.com
    /**
     * 根据key查询userinfo
     * @param key
     * @return
     */
    public static final StringBuilder queryUserInfoByGroupIdAndKey(String key){
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(" select ");

        sqlBuilder.append(" members. ");
        sqlBuilder.append(TableGroupMember.MEMBER_ACCOUNT);
        sqlBuilder.append(" ,");
        sqlBuilder.append(" members. ");
        sqlBuilder.append(TableGroupMember.MEMBER_NICKNAME);
        sqlBuilder.append(" ,");
        sqlBuilder.append(" friend. ");
        sqlBuilder.append(TableFriend.REMARK);
        sqlBuilder.append(" ,");
        sqlBuilder.append(" account. ");
        sqlBuilder.append(TableActomaAccount.NICKNAME);
        sqlBuilder.append(" ,");
        //add by lwl start
        sqlBuilder.append(" account. ");
        sqlBuilder.append(TableActomaAccount.ALIAS);
        sqlBuilder.append(" ,");
        //add by lwl end
        //add by wal@xdja.com  start for 1738
        sqlBuilder.append(" tdepartmembers. ");
        sqlBuilder.append(TableDepartmentMember.NAME);
        sqlBuilder.append(" ,");
        //add by wal@xdja.com end for 1738
        sqlBuilder.append(" avatar.* ");

        sqlBuilder.append(" from ");
        sqlBuilder.append(TableGroup.TABLE_NAME);
        sqlBuilder.append(" as tgroup");


        sqlBuilder.append(" left join ");
        sqlBuilder.append(TableGroupMember.TABLE_NAME);
        sqlBuilder.append(" as members ");

        sqlBuilder.append(" on ");
        sqlBuilder.append(" tgroup. ");
        sqlBuilder.append(TableGroup.GROUP_ID);
        sqlBuilder.append(" = ");
        sqlBuilder.append(" members. ");
        sqlBuilder.append(TableGroupMember.GROUP_ID);



        sqlBuilder.append(" left join ");
        sqlBuilder.append(TableActomaAccount.TABLE_NAME);
        sqlBuilder.append(" as account ");
        sqlBuilder.append(" on ");
        sqlBuilder.append(" members. ");
        sqlBuilder.append(TableGroupMember.MEMBER_ACCOUNT);
        sqlBuilder.append(" = ");
        sqlBuilder.append(" account. ");
        sqlBuilder.append(TableActomaAccount.ACCOUNT);


        sqlBuilder.append(" left join ");
        sqlBuilder.append(TableFriend.TABLE_NAME);
        sqlBuilder.append(" as friend ");
        sqlBuilder.append(" on ");
        sqlBuilder.append(" members. ");
        sqlBuilder.append(TableGroupMember.MEMBER_ACCOUNT);
        sqlBuilder.append(" = ");
        sqlBuilder.append(" friend. ");
        sqlBuilder.append(TableFriend.ACCOUNT);

        //add by wal@xdja.com  start for 1738
        sqlBuilder.append(" left join ");
        sqlBuilder.append(TableDepartmentMember.TABLE_NAME);
        sqlBuilder.append(" as tdepartmembers ");
        sqlBuilder.append(" on ");
        sqlBuilder.append(" members. ");
        sqlBuilder.append(TableGroupMember.MEMBER_ACCOUNT);
        sqlBuilder.append(" = ");
        sqlBuilder.append(" tdepartmembers. ");
        sqlBuilder.append(TableDepartmentMember.ACCOUNT);
        //add by wal@xdja.com  end for 1738

        sqlBuilder.append(" left join ");
        sqlBuilder.append(TableAccountAvatar.TABLE_NAME);
        sqlBuilder.append(" as avatar ");
        sqlBuilder.append(" on ");
        sqlBuilder.append(" members. ");
        sqlBuilder.append(TableGroupMember.MEMBER_ACCOUNT);
        sqlBuilder.append(" = ");
        sqlBuilder.append(" avatar. ");
        sqlBuilder.append(TableAccountAvatar.ACCOUNT);

        sqlBuilder.append(" where tgroup. ");
        sqlBuilder.append(TableGroup.GROUP_ID);
        sqlBuilder.append(" = ? ");
        sqlBuilder.append(" and ");

        sqlBuilder.append("members. ");
        sqlBuilder.append(TableGroupMember.IS_DELETED);
        sqlBuilder.append(" = 0 and ");

        //如果别名为null，才查询数字账号
        sqlBuilder.append("((case when ");
        sqlBuilder.append(TableActomaAccount.ALIAS);
        sqlBuilder.append(" is null then ");
        sqlBuilder.append("members. ");
        sqlBuilder.append(TableGroupMember.MEMBER_ACCOUNT);
        sqlBuilder.append(" like '%");
        sqlBuilder.append(key);
        sqlBuilder.append("%' end)");

        //匹配集团通讯录拼音全拼
        sqlBuilder.append(" or ");
        sqlBuilder.append("tdepartmembers. ");
        sqlBuilder.append(TableDepartmentMember.NAME_FULL_PY);
        sqlBuilder.append(" like '%");
        sqlBuilder.append(key);
        sqlBuilder.append("%' ");


        //匹配集团通讯录拼音简拼
        sqlBuilder.append(" or ");
        sqlBuilder.append("tdepartmembers. ");
        sqlBuilder.append(TableDepartmentMember.NAME_PY);
        sqlBuilder.append(" like '%");
        sqlBuilder.append(key);
        sqlBuilder.append("%' ");

        //匹配集团通讯录昵称全拼
        sqlBuilder.append(" or ");
        sqlBuilder.append("account. ");
        sqlBuilder.append(TableActomaAccount.NICKNAME_FULL_PY);
        sqlBuilder.append(" like '%");
        sqlBuilder.append(key);
        sqlBuilder.append("%' ");

        //匹配集团通讯录昵称简拼
        sqlBuilder.append(" or ");
        sqlBuilder.append("account. ");
        sqlBuilder.append(TableActomaAccount.NICKNAME_PY);
        sqlBuilder.append(" like '%");
        sqlBuilder.append(key);
        sqlBuilder.append("%' ");

        //匹配集团通讯录昵称
        sqlBuilder.append(" or ");
        sqlBuilder.append("tdepartmembers. ");
        sqlBuilder.append(TableDepartmentMember.NAME);
        sqlBuilder.append(" like '%");
        sqlBuilder.append(key);
        sqlBuilder.append("%' ");

        //匹配集团通讯录账号
        sqlBuilder.append(" or ");
        sqlBuilder.append("tdepartmembers. ");
        sqlBuilder.append(TableDepartmentMember.ACCOUNT);
        sqlBuilder.append(" like '%");
        sqlBuilder.append(key);
        sqlBuilder.append("%' ");

        //匹配备注
        sqlBuilder.append(" or ");
        sqlBuilder.append("friend. ");
        sqlBuilder.append(TableFriend.REMARK);
        sqlBuilder.append(" like '%");
        sqlBuilder.append(key);
        sqlBuilder.append("%' ");

        //匹配备注全拼
        sqlBuilder.append(" or ");
        sqlBuilder.append("friend. ");
        sqlBuilder.append(TableFriend.REMARK_FULL_PY);
        sqlBuilder.append(" like '%");
        sqlBuilder.append(key);
        sqlBuilder.append("%' ");

        //匹配备注简拼
        sqlBuilder.append(" or ");
        sqlBuilder.append("friend. ");
        sqlBuilder.append(TableFriend.REMARK_PY);
        sqlBuilder.append(" like '%");
        sqlBuilder.append(key);
        sqlBuilder.append("%' ");

        //匹配群成员显示全拼
        sqlBuilder.append(" or ");
        sqlBuilder.append("account. ");
        sqlBuilder.append(TableActomaAccount.NICKNAME_FULL_PY);
        sqlBuilder.append(" like '%");
        sqlBuilder.append(key);
        sqlBuilder.append("%' ");

        //匹配群成员显示简拼
        sqlBuilder.append(" or ");
        sqlBuilder.append("account. ");
        sqlBuilder.append(TableActomaAccount.NICKNAME_PY);
        sqlBuilder.append(" like '%");
        sqlBuilder.append(key);
        sqlBuilder.append("%' ");

        //匹配群昵称
        sqlBuilder.append(" or ");
        sqlBuilder.append("members. ");
        sqlBuilder.append(TableGroupMember.MEMBER_NICKNAME);
        sqlBuilder.append(" like '%");
        sqlBuilder.append(key);
        sqlBuilder.append("%' ");

        //匹配群昵称拼音简拼
        sqlBuilder.append(" or ");
        sqlBuilder.append("members. ");
        sqlBuilder.append(TableGroupMember.NICKNAME_PY);
        sqlBuilder.append(" like '%");
        sqlBuilder.append(key);
        sqlBuilder.append("%' ");

        //匹配群昵称拼音全拼
        sqlBuilder.append(" or ");
        sqlBuilder.append("members. ");
        sqlBuilder.append(TableGroupMember.NICKNAME_FULL_PY);
        sqlBuilder.append(" like '%");
        sqlBuilder.append(key);
        sqlBuilder.append("%' ");

        //匹配昵称
        sqlBuilder.append(" or ");
        sqlBuilder.append(" account. ");
        sqlBuilder.append(TableActomaAccount.NICKNAME);
        sqlBuilder.append(" like '%");
        sqlBuilder.append(key);
        sqlBuilder.append("%' ");

        //匹配自定义账号
        sqlBuilder.append(" or ");
        sqlBuilder.append(" account. ");
        sqlBuilder.append(TableActomaAccount.ALIAS);
        sqlBuilder.append(" like '%");
        sqlBuilder.append(key);
        sqlBuilder.append("%')");

        return sqlBuilder;
    }

}
