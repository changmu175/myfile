package com.xdja.contact.dao.sqlbuilder;

import android.text.TextUtils;

import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.bean.Member;
import com.xdja.contact.database.columns.TableAccountAvatar;
import com.xdja.contact.database.columns.TableActomaAccount;
import com.xdja.contact.database.columns.TableDepartment;
import com.xdja.contact.database.columns.TableDepartmentMember;

import java.util.List;

/**
 * Created by yangpeng on 2015/12/23.
 */
public class MemberDaoSqlBuilder {


    /**
     * 集团通讯录查找成员SQL语句构建器
     * @return
     */
    public static final StringBuilder queryAllSql(StringBuilder queryMobileArgs) {
        StringBuilder builder = new StringBuilder();
        builder.append(" SELECT ");
        builder.append("m.");
        builder.append(TableDepartmentMember.ACCOUNT);
        builder.append(",");
        builder.append(TableDepartmentMember.WORKER_ID);
        builder.append(",");
        builder.append(TableDepartmentMember.MEMBER_DEPT_ID);
        builder.append(",");
        builder.append(TableDepartmentMember.NAME);
        builder.append(",");
        builder.append(TableDepartmentMember.NAME_PY);
        builder.append(",");
        builder.append(TableDepartmentMember.NAME_FULL_PY);
        /*builder.append(",");
        builder.append(TableDepartmentMember.PHONE);*/
        builder.append(",");
        builder.append(TableDepartmentMember.SORT);

        builder.append(",a. ");
        builder.append(TableAccountAvatar.ID);
        builder.append(",");
        builder.append(TableAccountAvatar.AVATAR);
        builder.append(",");
        builder.append(TableAccountAvatar.THUMBNAIL);

        builder.append(", account. ");
        builder.append(TableActomaAccount.ID);
//        builder.append(queryMobileArgs); //modify for update actoma acount by wal@xdja.com

        //Start:add by wal@xdja.com for 3585
        //[s]remove by xienana for bug 5205 @20161025 [review by wangalei]
        /*builder.append(", account. ");
        builder.append(TableActomaAccount.ACCOUNT);*/
        //[e]remove by xienana for bug 5205 @20161025 [review by wangalei]
        builder.append(", account. ");
        builder.append(TableActomaAccount.ALIAS);
        builder.append(", account. ");
        builder.append(TableActomaAccount.NICKNAME);
        builder.append(", account. ");
        builder.append(TableActomaAccount.BIND_PHONE);
        builder.append(", account. ");
        builder.append(TableActomaAccount.NICKNAME_PY);
        builder.append(", account. ");
        builder.append(TableActomaAccount.NICKNAME_FULL_PY);
        builder.append(", account. ");
        builder.append(TableActomaAccount.GENDER);
        builder.append(", account. ");
        builder.append(TableActomaAccount.EMAIL);
        builder.append(", account. ");
        builder.append(TableActomaAccount.FIRST_LOGIN_TIME);
        builder.append(", account. ");
        builder.append(TableActomaAccount.ACTIVATE_STATUS);
        //end:add by wal@xdja.com for 3585

        //start:modify for update actoma acount by wal@xdja.com
        builder.append(", account. ");
        builder.append(TableActomaAccount.IDENTIFY);
        builder.append(queryMobileArgs);
        //end:modify for update actoma acount by wal@xdja.com

        builder.append(" FROM ");
        builder.append(TableDepartmentMember.TABLE_NAME);
        builder.append(" m ");
        builder.append(" LEFT JOIN ");
        builder.append(TableAccountAvatar.TABLE_NAME);
        builder.append(" a ");
        builder.append(" ON a.");
        builder.append(TableAccountAvatar.ACCOUNT);
        builder.append(" = m.");
        builder.append(TableDepartmentMember.ACCOUNT);

        builder.append(" LEFT JOIN ");
        builder.append(TableActomaAccount.TABLE_NAME);
        builder.append(" account ");
        builder.append(" ON account.");
        builder.append(TableActomaAccount.ACCOUNT);
        builder.append(" = m.");
        builder.append(TableDepartmentMember.ACCOUNT);

        builder.append(" ");
        return builder;
    }

    /**
     * 集团通讯录保存成员构建器
     * @param member
     * @return
     */
    public static final String insertSql(Member member) {
        StringBuilder builder = new StringBuilder();
        builder.append(" replace into ");
        builder.append(TableDepartmentMember.TABLE_NAME);
        builder.append(" ( ");
        builder.append(TableDepartmentMember.ACCOUNT);
        builder.append(" , ");
        builder.append(TableDepartmentMember.MEMBER_DEPT_ID);
        builder.append(" , ");
        builder.append(TableDepartmentMember.NAME);
        builder.append(" , ");
        builder.append(TableDepartmentMember.NAME_FULL_PY);
        builder.append(" , ");
        builder.append(TableDepartmentMember.NAME_PY);
        builder.append(" , ");
        builder.append(TableDepartmentMember.PHONE);
        builder.append(" , ");
        builder.append(TableDepartmentMember.SORT);
        builder.append(" , ");
        builder.append(TableDepartmentMember.WORKER_ID);
        builder.append(" ) ");
        builder.append("values(");
        builder.append(checkColumnNull(member.getAccount()));
        builder.append(" , ");
        builder.append(checkColumnNull(member.getDepartId()));
        builder.append(" , ");
        builder.append(checkColumnNull(member.getName()));
        builder.append(" , ");
        builder.append(checkColumnNull(member.getNameFullPy()));
        builder.append(" , ");
        builder.append(checkColumnNull(member.getNamePy()));
        builder.append(" , ");
        builder.append(checkColumnNull(member.getMobile()));
        builder.append(" , ");
        builder.append(checkColumnNull(member.getSort()));
        builder.append(" , ");
        builder.append(checkColumnNull(member.getWorkId()));
        builder.append(" )");
        /*String sql = " replace into " + TableDepartmentMember.TABLE_NAME + "(" + TableDepartmentMember.C_REQ_ACCOUNT + "," + TableDepartmentMember.MEMBER_DEPT_ID + "," + TableDepartmentMember.NAME + ","
                + TableDepartmentMember.NAME_FULL_PY + "," + TableDepartmentMember.NAME_PY + "," + TableDepartmentMember.PHONE + "," + TableDepartmentMember.SORT + ","
                + TableDepartmentMember.WORKER_ID + ")" +
                " values(" + checkColumnNull(member.getAccount()) + "," + checkColumnNull(member.getDepartId()) + "," + checkColumnNull(member.getName()) + "," +
                checkColumnNull(member.getNameFullPy()) + "," + checkColumnNull(member.getNamePy()) + "," + checkColumnNull(member.getMobile()) + "," + member.getSort() + "," + checkColumnNull(member.getWorkId()) + ")";*/
        return builder.toString();
    }

    public static String checkColumnNull(String data) {
        return TextUtils.isEmpty(data) ? "NULL" : "'" + data + "'";
    }

    public static final String updateSql(Member member, String where) {
        StringBuilder builder = new StringBuilder();
        builder.append("update ");
        builder.append(TableDepartmentMember.TABLE_NAME);
        builder.append(" set ");
        builder.append( TableDepartmentMember.MEMBER_DEPT_ID );
        builder.append(" = ");
        builder.append(checkColumnNull(member.getDepartId()));
        builder.append(" , ");
        builder.append(TableDepartmentMember.NAME);
        builder.append(" = ");
        builder.append(checkColumnNull(member.getName()));
        builder.append(" , ");
        builder.append(TableDepartmentMember.NAME_FULL_PY);
        builder.append(" = ");
        builder.append(checkColumnNull(member.getNameFullPy()));
        builder.append(",");
        builder.append(TableDepartmentMember.NAME_PY);
        builder.append(" = ");
        builder.append(checkColumnNull(member.getNamePy()));
        builder.append(" , ");
        builder.append(TableDepartmentMember.PHONE);
        builder.append(" = ");
        builder.append(checkColumnNull(member.getMobile()));
        builder.append(" , ");
        builder.append(TableDepartmentMember.SORT);
        builder.append(" = ");
        builder.append(checkColumnNull(member.getSort()));
        builder.append(" , ");
        builder.append(TableDepartmentMember.WORKER_ID);
        builder.append(" = ");
        builder.append(checkColumnNull(member.getWorkId()));
        builder.append(" ");
        builder.append(" where ");
        builder.append(where);
//        String sql = "update " + TableDepartmentMember.TABLE_NAME + " set " + TableDepartmentMember.MEMBER_DEPT_ID + " = " + checkColumnNull(member.getDepartId()) + " , " + TableDepartmentMember.NAME + " = " + checkColumnNull(member.getName()) + ", " + TableDepartmentMember.NAME_FULL_PY + " = " + checkColumnNull(member.getNameFullPy()) + ", " +
//                TableDepartmentMember.NAME_PY + " = " + checkColumnNull(member.getNamePy()) + " , " + TableDepartmentMember.PHONE + " = " + checkColumnNull(member.getMobile()) + ", " + TableDepartmentMember.SORT + " = " +
//                checkColumnNull(member.getSort()) + " , " + TableDepartmentMember.WORKER_ID + " =" + checkColumnNull(member.getWorkId()) + " " + " where " + where;
        return builder.toString();
    }

    public static final String deleteSql(Member member, String where) {
        if (member == null) {
            return null;
        }
        String sql = " delete from " + TableDepartmentMember.TABLE_NAME + " where " + where;
        return sql;
    }

    /**
     *
     * @param searchKey
     * @param pageSize  每页显示条数
     * @param index     第几页从1开始
     * @return
     */
    public static final String buildSearchSql(String searchKey, int pageSize, int index) {
        String searchArgs = buildSearchArg(searchKey);
        StringBuilder builder = queryAllSql(queryDepartmentsMobileArgsSql());
        builder.append(" where ");
        builder.append(TableDepartmentMember.NAME);
        builder.append(" like ");
        builder.append(searchArgs);
        builder.append(" or ");
        builder.append(TableDepartmentMember.NAME_PY);
        builder.append(" like ");
        builder.append(searchArgs);
        if (searchKey.length() > 1) {
            builder.append(" or ");
            builder.append(TableDepartmentMember.NAME_FULL_PY);
            builder.append(" like ");
            builder.append(searchArgs);
        }
        //搜索集团联系人不匹配绑定手机号
        /*builder.append(" or account.");
        builder.append(TableActomaAccount.BIND_PHONE);
        builder.append(" like ");
        builder.append(searchArgs);*/

        //start:add by wal#xdja.com for 3585
//        builder.append(" or account.");
//        builder.append(TableActomaAccount.ACCOUNT);
//        builder.append(" like ");
//        builder.append(searchArgs);

        builder.append(" or ");
        builder.append("(");
        builder.append("(");
        builder.append(TableActomaAccount.ALIAS);
        builder.append(" is not null ");
        builder.append(" and ");
        builder.append("(account.");
        builder.append(TableActomaAccount.ALIAS);
        builder.append(" like ");
        builder.append(searchArgs);
        builder.append(getKeywordOr(searchKey));
        builder.append(")");
        builder.append(")");

        builder.append(" or ");
        builder.append("(");
        builder.append(TableActomaAccount.ALIAS);
        builder.append(" is null ");
        builder.append(" and ");
        builder.append("(account.");
        builder.append(TableActomaAccount.ACCOUNT);
        builder.append(" like ");
        builder.append(searchArgs);
        builder.append(getKeywordOr(searchKey));
        builder.append(")");
        builder.append(")");

        builder.append(")");
        //end:add by wal#xdja.com for 3585
        //分页查询备用
        String offset = " order by " + TableDepartmentMember.WORKER_ID + " limit " + pageSize + " offset " + (pageSize * (index - 1));//size:每页显示条数，index页
        return builder.toString();
    }
    //start:add by wal#xdja.com for 3585
    public static String  getKeywordOr(String key){
        StringBuilder sql=new StringBuilder();

        sql.append(" or ");
        sql.append(TableActomaAccount.NICKNAME);
        sql.append(" like");
        sql.append(buildSearchArg(key));

        sql.append(" or ");
        if (key.length() > 1) {
            sql.append(TableActomaAccount.NICKNAME_FULL_PY);
            sql.append(" like ");
            sql.append(buildSearchArg(key));
            sql.append(" or ");
        }
        sql.append(TableActomaAccount.NICKNAME_PY);
        sql.append(" like ");
        sql.append(buildSearchArg(key));
        return  sql.toString();
    }
    //end:add by wal#xdja.com for 3585

    /**
     * 搜索时根据输入的搜索条件拼装模糊查询需要的关键字
     *
     * @param searchKey
     * @return
     */
    public static String buildSearchArg(String searchKey) {
        StringBuilder builder = new StringBuilder();
        builder.append("'");
        builder.append("%");
        builder.append(searchKey);
        builder.append("%");
        builder.append("'");
        return builder.toString();
    }

    /**
     * MemberDao
     * @return
     */
    public static final StringBuilder queryDepartmentsMobileArgsSql(){
        StringBuilder builder = new StringBuilder();
        builder.append(", m.");
        builder.append(TableDepartmentMember.PHONE);
        return builder;
    }

    /**
     * MemberDao集团通讯录模糊搜索SQL语句构造器
     * @return
     */
    public static final String likeSearchSql(){
        StringBuilder builder = new StringBuilder();
        builder.append(" SELECT ");
        builder.append("member.");
        builder.append(TableDepartmentMember.ACCOUNT);
        builder.append(",");
        builder.append(TableDepartmentMember.WORKER_ID);
        builder.append(",");
        builder.append("member.");
        builder.append(TableDepartmentMember.MEMBER_DEPT_ID);
        builder.append(",");
        builder.append(TableDepartmentMember.NAME);
        builder.append(",");
        builder.append(TableDepartmentMember.NAME_PY);
        builder.append(",");
        builder.append(TableDepartmentMember.NAME_FULL_PY);
        builder.append(",");
        builder.append("member.");
        builder.append(TableDepartmentMember.SORT);
        builder.append(",");
        builder.append("member.");
        builder.append(TableDepartmentMember.PHONE);

        builder.append(",avatar. ");
        builder.append(TableAccountAvatar.ID);
        builder.append(",");
        builder.append(TableAccountAvatar.AVATAR);
        builder.append(",");
        builder.append(TableAccountAvatar.THUMBNAIL);

        builder.append(",depart. ");
        builder.append(TableDepartment.DEPT_NAME);
        builder.append(",depart. ");
        builder.append(TableDepartment.DEPT_ID);
        builder.append(",depart. ");
        builder.append(TableDepartment.SUPER_DEPT_ID);
        builder.append(",depart. ");
        builder.append(TableDepartment.SORT);


        builder.append(",account. ");
        builder.append(TableActomaAccount.ACCOUNT);
        builder.append(",");
        /*builder.append(" account. ");
        builder.append(TableActomaAccount.BIND_PHONE);
        builder.append(",");*/
        builder.append(TableActomaAccount.FIRST_LOGIN_TIME);
        builder.append(",");
        builder.append(TableActomaAccount.NICKNAME);
        builder.append(",");
        builder.append(TableActomaAccount.NICKNAME_PY);
        builder.append(",");
        builder.append(TableActomaAccount.NICKNAME_FULL_PY);
        builder.append(",");
        builder.append(TableActomaAccount.GENDER);
        builder.append(",");
        builder.append(TableActomaAccount.EMAIL);
        builder.append(",");
        builder.append(TableActomaAccount.ACTIVATE_STATUS);
        builder.append(",");
        builder.append(TableActomaAccount.ALIAS);//add by lwl
        builder.append(",");
        builder.append(TableActomaAccount.IDENTIFY);//add by lwl
        builder.append(" FROM ");


        builder.append(TableDepartmentMember.TABLE_NAME);
        builder.append(" member ");

        builder.append(" LEFT JOIN ");
        builder.append(TableDepartment.TABLE_NAME);
        builder.append(" depart ");
        builder.append(" ON member.");
        builder.append(TableDepartmentMember.MEMBER_DEPT_ID);
        builder.append(" = depart.");
        builder.append(TableDepartment.DEPT_ID);

        builder.append(" LEFT JOIN ");
        builder.append(TableActomaAccount.TABLE_NAME);
        builder.append(" account ");
        builder.append(" ON member.");
        builder.append(TableDepartmentMember.ACCOUNT);
        builder.append(" = account.");
        builder.append(TableActomaAccount.ACCOUNT);


        builder.append(" LEFT JOIN ");
        builder.append(TableAccountAvatar.TABLE_NAME);
        builder.append(" avatar ");
        builder.append(" ON member.");
        builder.append(TableDepartmentMember.ACCOUNT);
        builder.append(" = avatar.");
        builder.append(TableAccountAvatar.ACCOUNT);
        LogUtil.getUtils().i("-------likeSearchSql-----------"+builder.toString());
        return builder.toString();
    }

    public static final String selectNoAccountInfoInMember(){
        StringBuilder builder = new StringBuilder();
        builder.append(" SELECT ");
        builder.append("member.");
        builder.append(TableDepartmentMember.ACCOUNT);

        builder.append(" FROM ");
        builder.append(TableDepartmentMember.TABLE_NAME);
        builder.append(" member");

        builder.append(" LEFT JOIN ");
        builder.append(TableActomaAccount.TABLE_NAME);
        builder.append(" account ");
        builder.append(" ON member.");
        builder.append(TableDepartmentMember.ACCOUNT);
        builder.append(" = account.");
        builder.append(TableActomaAccount.ACCOUNT);

        builder.append(" WHERE ");
        builder.append("account.");
        builder.append(TableActomaAccount.ACCOUNT);
        builder.append(" is null AND ");
        builder.append("member.");
        builder.append(TableDepartmentMember.ACCOUNT);
        builder.append(" is not null");

        LogUtil.getUtils().i("-------selectNoAccountInfoInMember likeSearchSql-----------"+builder.toString());
        return builder.toString();
    }

    public static final String exitSql(){
        StringBuilder builder = new StringBuilder();
        builder.append(" select ");
        builder.append(" * ");
        builder.append(" from ");
        builder.append(" sqlite_master ");
        builder.append(" where ");
        builder.append(" name ");
        builder.append(" = ");
        builder.append(" '");//modify by wal@xdja.com for 1880
        builder.append(TableDepartmentMember.TABLE_NAME);
        builder.append("' ");//modify by wal@xdja.com for 1880
        return builder.toString();
    }

    public static  final String getMemberByIdSqlBuilder(String deptId, boolean isATUser){
        StringBuilder where = new StringBuilder();
        where.append(TableDepartmentMember.MEMBER_DEPT_ID);
        where.append(" = ");
        //如果部门id为空就查询根级目录
        if(TextUtils.isEmpty(deptId)){
            where.append(" ( SELECT ");
            where.append(" c_dept_id ");
            where.append(" FROM ");
            where.append(" t_department ");
            where.append(" WHERE ");
            where.append(" c_super_dept_id = '' ");
            where.append(" OR c_super_dept_id ISNULL ");
            where.append(" OR c_super_dept_id = '0' ");
            where.append(" )");
        }else{
            where.append(deptId);
        }
//            if (TextUtils.isEmpty(deptId)) {
//                deptId = "( SELECT " +
//                        " c_dept_id " +
//                        " FROM " +
//                        " t_department " +
//                        " WHERE " +
//                        " c_super_dept_id = '' " +
//                        " OR c_super_dept_id ISNULL " +
//                        " OR c_super_dept_id = '0' " +
//                        " )";
//            }
//            String where = TableDepartmentMember.MEMBER_DEPT_ID + " = " + deptId;
        if (isATUser) {
            where.append(" and m. ");
            where.append(TableDepartmentMember.ACCOUNT );
            where.append(" NOTNULL ");
//                where += " and m." + TableDepartmentMember.C_REQ_ACCOUNT + " NOTNULL";
        }
        StringBuilder builder = MemberDaoSqlBuilder.queryAllSql(MemberDaoSqlBuilder.queryDepartmentsMobileArgsSql());
        builder.append(" where ");
        builder.append(where);
        builder.append(" order by ");
        //[s]modify by xienana for bug department member sort @20161122 review by tangsha
        builder.append("cast");
        builder.append("("+TableDepartmentMember.SORT);
        builder.append(" as int)");
        //[e]modify by xienana for bug department member sort @20161122 review by tangsha

        return builder.toString();
    }



    public static String queryMembersWithinWorkIds(List<String> workIds){
        int length = workIds.size();
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(" select * from ");
        sqlBuilder.append(TableDepartmentMember.TABLE_NAME);
        sqlBuilder.append(" where ");
        sqlBuilder.append(TableDepartmentMember.WORKER_ID);
        sqlBuilder.append(" in ");
        sqlBuilder.append(" ( ");
        for (int i = 0; i < length; i++) {
            sqlBuilder.append(workIds.get(i));
            if (i != (length - 1)) {
                sqlBuilder.append(", ");
            }
        }
        sqlBuilder.append(" ) ");
        return sqlBuilder.toString();
    }



}
