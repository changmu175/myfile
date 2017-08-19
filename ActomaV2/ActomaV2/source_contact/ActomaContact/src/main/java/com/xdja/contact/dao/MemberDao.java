package com.xdja.contact.dao;

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.xdja.contact.bean.ActomaAccount;
import com.xdja.contact.bean.Avatar;
import com.xdja.contact.bean.Department;
import com.xdja.contact.bean.ErrorDepartment;
import com.xdja.contact.bean.Member;
import com.xdja.contact.dao.sqlbuilder.MemberDaoSqlBuilder;
import com.xdja.contact.database.columns.TableDepartmentMember;
import com.xdja.contact.exception.ContactDaoException;
import com.xdja.contact.util.ContactUtils;
import com.xdja.comm.uitl.ObjectUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangpeng on 2015/7/13.
 */
public class MemberDao extends AbstractContactDao<Member>{


    public List<Member> findMembersByIds(List<String> workIds){
        List<Member> result = new ArrayList<>();
        try {
            cursor = database.rawQuery(MemberDaoSqlBuilder.queryMembersWithinWorkIds(workIds),null);
            while(cursor.moveToNext()){
                Member member = new Member(cursor);
                result.add(member);
            }
        }catch (Exception e){
            new ContactDaoException(e);
        }finally {
            closeCursor();
        }
        return result;
    }











    /*********以下代码未重构********************************/








    public Member query(String id) {
        Member result = null;
        try {
            String[] selectionArgs = new String[]{id};
            String sql = MemberDaoSqlBuilder.queryAllSql(MemberDaoSqlBuilder.queryDepartmentsMobileArgsSql()).toString() + " where " + TableDepartmentMember.WORKER_ID + " = ?";
            cursor = database.rawQuery(sql,selectionArgs);
            if (cursor.moveToFirst()) {
                result = new Member(cursor);
                Avatar avatar = new Avatar(cursor);
                //start:add by wal@xdja.com for 2609
                ActomaAccount account=new ActomaAccount();
                account.setAccount(result.getAccount());
                account.setIdentifyByCursor(cursor);
                result.setActomaAccount(account);
                //end:add by wal@xdja.com for 2609
                result.setAvatarInfo(avatar);
            }
        } catch (Exception e) {
            new ContactDaoException(e);
        } finally {
            closeCursor();
        }
        return result;
    }

    public List<Member> queryAll() {
        List<Member> members = new ArrayList<>();
        try {
            cursor = database.query(TableDepartmentMember.TABLE_NAME, null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                Member member = new Member(cursor);
                members.add(member);
            }
        }catch (Exception e){
            new ContactDaoException(e);
        }finally {
            closeCursor();
        }
        return members;
    }

    @Override
    protected String getTableName() {
        return TableDepartmentMember.TABLE_NAME;
    }

    public long insert(@NonNull Member member) {
        long result = database.insert(getTableName(), null, member.getContentValues());
        return result;
    }

    public int update(Member member) {
        if (TextUtils.isEmpty(member.getWorkId()))
            return -1;
        String whereArg = TableDepartmentMember.WORKER_ID + "=?";
        return database.update(getTableName(), member.getContentValues(), whereArg, new String[]{String.valueOf(member.getWorkId())});
    }

    public int delete(Member member) {
        return 0;
    }


    /**
     * 批量保存member,提高效率
     *
     * @param members
     */
    public void insert(List<Member> members) {
        try {
            List<String> sqls = new ArrayList<String>();
            List<ErrorDepartment> errorDepartments = new ArrayList<>();
            for (Member member : members) {
                if (member == null) {
                    continue;
                }
                sqls.add(MemberDaoSqlBuilder.insertSql(member));
            }
            database.beginTransaction();
            for (int i = 0; i < sqls.size(); i++) {
                SQLiteStatement stat = database.compileStatement(sqls.get(i));
                long rowId = stat.executeInsert();
                if (rowId == -1) {
                    Member member = members.get(i);
                    ErrorDepartment hsitory = buildErrorHsitory(member, "insert");
                    errorDepartments.add(hsitory);
                }
            }
            database.setTransactionSuccessful();
            database.endTransaction();
            dealWithErrorDepartment(errorDepartments);
        } catch (Exception e) {
            new ContactDaoException(e);
        }
    }


    /**
     * 处理部门错误信息
     * @param errorDepartments
     */
    private void dealWithErrorDepartment(List<ErrorDepartment> errorDepartments){
        ErrorDepartmentDao errorDepartmentDao = new ErrorDepartmentDao();
        for (ErrorDepartment errorDepartment : errorDepartments) {
            ErrorDepartment result = errorDepartmentDao.queryByMemberId(errorDepartment.getMemberId());
            if (result != null) {
                result.setMemberId(errorDepartment.getMemberId());
                result.setType(errorDepartment.getType());
                result.setReason(errorDepartment.getReason());
                result.setUpdateTime(String.valueOf(System.currentTimeMillis()));
                result.setCreateTime(errorDepartment.getCreateTime());
                errorDepartmentDao.update(result);
            } else {
                errorDepartmentDao.insert(errorDepartment);
            }
        }
    }

    /**
     * 批量删除member by worker_id,提高效率
     *
     * @param members
     */
    public void delete(List<Member> members) {
        List<String> sqls = new ArrayList<String>();
        for (Member member : members) {
            if (member == null) {
                continue;
            }
            sqls.add(MemberDaoSqlBuilder.deleteSql(member, TableDepartmentMember.WORKER_ID + " = " + member.getWorkId()));
        }
        database.beginTransaction();
        for (String sql : sqls) {
            SQLiteStatement stat = database.compileStatement(sql);
            stat.executeUpdateDelete();
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    /**
     * 删除 member by id
     *
     * @param id
     * @return boolean
     */
    public boolean deleteById(String id) {
        String whereArg = TableDepartmentMember.MEMBER_DEPT_ID + " = ?";
        int result = database.delete(TableDepartmentMember.TABLE_NAME, whereArg, new String[]{id});
        return result > 0;
    }

    /**
     * 删除所有的member信息
     *
     * @return
     */
    public boolean deleteAllMembers() {
        int result = database.delete(TableDepartmentMember.TABLE_NAME, null, null);
        return result > 0;
    }


    /**
     * 模糊查询
     *
     * @param searchKey
     * @return
     */
    public List<Member> findMembersLikethis(String searchKey) {
        List<Member> members = new ArrayList<Member>();
        String sql = MemberDaoSqlBuilder.buildSearchSql(searchKey, 0, 0);
        try {
            cursor = database.rawQuery(sql, null);
            //AccountBean account = AccountServer.getAccount(context);
            String currentAccount = ContactUtils.getCurrentAccount();
            while (cursor.moveToNext()) {
                Member member = new Member(cursor);
                if (!ObjectUtil.stringIsEmpty(currentAccount) && currentAccount.equals(member.getAccount())) {
                    continue;
                }
                member.setActomaAccount(new ActomaAccount(cursor)); //add by wal@xdja.com  for 3585
                member.setAvatarInfo(new Avatar(cursor));
                members.add(member);
            }
        } catch (Exception e) {
            new ContactDaoException(e);
        } finally {
            closeCursor();
        }
        return members;
    }


    /**
     * 查询所有部门人员
     *
     * @return
     */
    public List<Member> getAllMembers() {
        List<Member> result = new ArrayList<>();
        try {
            cursor = database.rawQuery("select * from " + TableDepartmentMember.TABLE_NAME, null);
            while (cursor.moveToNext()) {
                Member member = new Member(cursor);
                result.add(member);
            }
        } catch (Exception e) {
            new ContactDaoException(e);
        } finally {
           closeCursor();
        }
        return result;
    }

    public List<Member> getAllMembersJoinDepartment() {
        List<Member> result = new ArrayList<>();
        try {
            cursor = database.rawQuery(MemberDaoSqlBuilder.likeSearchSql(), null);
            while (cursor.moveToNext()) {
                Member member = new Member(cursor);
                member.setAvatarInfo(new Avatar(cursor));

                ActomaAccount actomaAccount = new ActomaAccount(cursor);
                member.setNickName(actomaAccount.getNickname());
                member.setActomaAccount(actomaAccount);

                Department department = new Department(cursor);
                member.setDepartmentName(department.getDepartmentName());

                result.add(member);
            }
        } catch (Exception e) {
            new ContactDaoException(e);
        } finally {
           closeCursor();
        }
        return result;
    }

    public List<String> getMembersNoAccountInfo() {
        List<String> result = new ArrayList<>();
        try {
            cursor = database.rawQuery(MemberDaoSqlBuilder.selectNoAccountInfoInMember(), null);
            while (cursor.moveToNext()) {
               result.add(cursor.getString(cursor.getColumnIndex(TableDepartmentMember.ACCOUNT)));
            }
        } catch (Exception e) {
            new ContactDaoException(e);
        } finally {
            closeCursor();
        }
        return result;
    }



    /**
     * 根据部门id查询当前部门下的所有成员
     *
     * @param deptId
     * @param isATUser 是否为安通用户
     * @return
     */
    public List<Member> getMembersByDepartId(String deptId, boolean isATUser) {
        List<Member> result = new ArrayList<>();
        try {
            cursor = database.rawQuery(MemberDaoSqlBuilder.getMemberByIdSqlBuilder(deptId,isATUser), null);
            while (cursor.moveToNext()) {
                Member member = new Member(cursor);
                Avatar avatar = new Avatar(cursor);
                member.setAvatarInfo(avatar);
                //start:modify for update actoma acount by wal@xdja.com
                // 此处指传了account的identify用于更新单个数据，以后若需要其他数据，在sql里在添加。
                ActomaAccount account=new ActomaAccount();
                account.setAccount(member.getAccount());
                account.setIdentifyByCursor(cursor);
                member.setActomaAccount(account);
                //end:modify for update actoma acount by wal@xdja.com
                result.add(member);
            }
        } catch (Exception e) {
            new ContactDaoException(e);
        } finally {
            closeCursor();
        }
        return result;
    }

    //[s]modify by xienana for count department member @20161124 review by tangsha
    public int getDepartmentMemberCount(){
        int count = 0;
        try {
            cursor = database.rawQuery("select * from " + TableDepartmentMember.TABLE_NAME, null);
            count = cursor.getCount();
        } catch (Exception e) {
            new ContactDaoException(e);
        } finally {
            closeCursor();
        }
        return count;
    }
    //[s]modify by xienana for count department member @20161124 review by tangsha

    /**
     * 根据部门id查询当前部门下的所有成员
     *
     * @param deptId
     * @return
     */
    public List<Member> getMembersByDepartId(int deptId) {
        List<Member> result = new ArrayList<Member>();
        try {
            StringBuilder builder = MemberDaoSqlBuilder.queryAllSql(MemberDaoSqlBuilder.queryDepartmentsMobileArgsSql());
            builder.append(" where ");
            builder.append(TableDepartmentMember.WORKER_ID);
            builder.append(" = ");
            builder.append(deptId);

            cursor = database.rawQuery(builder.toString(), null);
            while (cursor.moveToNext()) {
                Member member = new Member(cursor);
                Avatar avatar = new Avatar(cursor);
                member.setAvatarInfo(avatar);
                result.add(member);
            }
        } catch (Exception e) {
            new ContactDaoException(e);
        } finally {
            closeCursor();
        }
        return result;
    }


    /**
     * 根据 account 查询member
     *
     * @return Group
     */
    public Member findMemberByAccount(String account) {
        Member result = null;
        try {
            String sql = MemberDaoSqlBuilder.queryAllSql(MemberDaoSqlBuilder.queryDepartmentsMobileArgsSql()).toString() + " where m." + TableDepartmentMember.ACCOUNT + " = ?";
            String[] selectionArgs = new String[]{String.valueOf(account)};
            cursor = database.rawQuery(sql, selectionArgs);
            if (cursor.moveToFirst()) {
                result = new Member(cursor);
                Avatar avatar = new Avatar(cursor);
                result.setAvatarInfo(avatar);
            }
        } catch (Exception e) {
            new ContactDaoException(e);
        } finally {
            closeCursor();
        }
        return result;
    }

    private ErrorDepartment buildErrorHsitory(Member member, String reson) {
        ErrorDepartment errorDepartment = new ErrorDepartment();
        errorDepartment.setMemberId(member.getAccount());
        errorDepartment.setType(reson);
        errorDepartment.setReason(reson);
        return errorDepartment;
    }


    public boolean exits(){
        boolean exits = false;
        try {
//            String sql = " select * from sqlite_master where name=" + "'" + getTableName() + "'";
            cursor = database.rawQuery(MemberDaoSqlBuilder.exitSql(), null);
            if (cursor.moveToFirst()){
                exits = true;
            }
        }catch (Exception e){
            new ContactDaoException(e);
        }finally {
            closeCursor();
        }
        return exits;
    }


    public boolean deleteTableData(){
        boolean deleteBool = false;
        try {
            String where = " 1 = 1  ";
            int result = database.delete(getTableName(),where,null);
            if(result >= 0){
                deleteBool = true;
            }
        }catch (Exception e){
            new ContactDaoException(e);
        }
        return deleteBool;
    }

}
