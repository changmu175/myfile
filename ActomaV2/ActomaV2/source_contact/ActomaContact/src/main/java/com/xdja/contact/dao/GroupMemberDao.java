package com.xdja.contact.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteStatement;

import com.xdja.comm.data.AccountBean;
import com.xdja.contact.bean.Avatar;
import com.xdja.contact.bean.GroupMember;
import com.xdja.contact.convert.GroupConvert;
import com.xdja.contact.dao.sqlbuilder.GroupMemberDaoSqlBuilder;
import com.xdja.contact.database.columns.TableAccountAvatar;
import com.xdja.contact.database.columns.TableActomaAccount;
import com.xdja.contact.database.columns.TableDepartmentMember;
import com.xdja.contact.database.columns.TableFriend;
import com.xdja.contact.database.columns.TableGroupMember;
import com.xdja.contact.exception.ContactDaoException;
import com.xdja.contact.usereditor.bean.UserInfo;
import com.xdja.contact.util.ContactUtils;
import com.xdja.comm.uitl.ObjectUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by XDJA_XA on 2015/7/17.
 *
 * wanghao 2016-02-03 重构
 *
 */
public class GroupMemberDao extends AbstractContactDao<GroupMember> {


    /**
     * 查询群组内群成员关联好友表和账户表组装群详情里面的人员信息
     * @param groupId
     * @return
     */
    public List<UserInfo> queryUserInfosByGroupId(String groupId){
        List<UserInfo> dataSource = new ArrayList<>();
        String loginAccount = ContactUtils.getCurrentAccount();
        AccountBean accountBean = ContactUtils.getCurrentBean();
        try{
            cursor = database.rawQuery(GroupMemberDaoSqlBuilder.queryUserInfoByGroupId().toString(),new String[]{groupId});
            while(cursor.moveToNext()){
                UserInfo userInfo = new UserInfo();
                userInfo.setAccount(cursor.getString(cursor.getColumnIndex(TableGroupMember.MEMBER_ACCOUNT)));
                userInfo.setGroupMemberNickname(cursor.getString(cursor.getColumnIndex(TableGroupMember.MEMBER_NICKNAME)));
                userInfo.setAccountNickname(cursor.getString(cursor.getColumnIndex(TableActomaAccount.NICKNAME)));
                userInfo.setRemark(cursor.getString(cursor.getColumnIndex(TableFriend.REMARK)));

                userInfo.setAlias(cursor.getString(cursor.getColumnIndex(TableActomaAccount.ALIAS)));
                //add by wal@xdja.com  start for 1738
                userInfo.setDepartmentMemberName(cursor.getString(cursor.getColumnIndex(TableDepartmentMember.NAME)));
                //add by wal@xdja.com  end for 1738
                userInfo.setAvatar(cursor.getString(cursor.getColumnIndex(TableAccountAvatar.AVATAR))); //add by ysp
                Avatar avatar = new Avatar(cursor);
                userInfo.setAvatarBean(avatar);
                //[S]add by ysp, fix bug #8793, review tangsha
                if(!ObjectUtil.stringIsEmpty(loginAccount) && loginAccount.equals(userInfo.getAccount()) && !ObjectUtil.objectIsEmpty(accountBean)) {
                    userInfo.setAccountNickname(accountBean.getNickname());
                    userInfo.setAlias(accountBean.getAlias());
                    userInfo.getAvatarBean().setThumbnail(accountBean.getThumbnail());
                    userInfo.setNickName(accountBean.getNickname());
                }
                //[E]add by ysp, fix bug #8793, review tangsha
                dataSource.add(userInfo);
            }
        }catch (Exception e){
            new ContactDaoException(e);
        }finally {
            closeCursor();
        }
        return dataSource;
    }




    /**
     * 批量查询在对应群组中的群成员信息
     * @param groupIds
     * @return
     */
    public List<GroupMember> queryMembersInGroupIds(List<String> groupIds){
        List<GroupMember> dataSource = new ArrayList<>();
        try {
            cursor = database.rawQuery(GroupMemberDaoSqlBuilder.queryGroupMembersByGroupIdsSql(groupIds).toString(), null);
            while (cursor.moveToNext()) {
                GroupMember result = new GroupMember(cursor);
                dataSource.add(result);
            }
        }catch (Exception e){
            new ContactDaoException(e);
        }finally {
            closeCursor();
        }
        return dataSource;
    }

    public GroupMember query(String id) {
        return null;
    }

    /**
     * 根据ID查找群组成员
     * @return Group
     */
    public GroupMember query(String groudId, String account) {
        GroupMember result = new GroupMember();
        try {
            cursor = database.rawQuery(GroupMemberDaoSqlBuilder.groupMemberQuerySql(groudId, account),null);
            if (cursor.moveToFirst()) {
                result = new GroupMember(cursor);
            }
        } catch (Exception e) {
            new ContactDaoException(e);
        } finally {
            closeCursor();
        }
        return result;
    }

    public List<GroupMember> queryAll() {
        List<GroupMember> groupMembers = new ArrayList<GroupMember>();
        try{
            String sql = " select * from " + getTableName();
            cursor = database.rawQuery(sql,null);
            while(cursor.moveToNext()){
                GroupMember groupMember = new GroupMember(cursor);
                groupMembers.add(groupMember);
            }
        }catch (Exception e){
            new ContactDaoException(e);
        }finally {
            closeCursor();
        }
        return groupMembers;
    }

    /**
     * 统计群成员内成员总数
     * @return
     */
    public long countMembers(){
        long result = 0l;
        try {
            cursor = database.rawQuery("select count ( " + TableGroupMember.GROUP_ID + " ) from " + getTableName(), null);
            if(cursor.moveToFirst()){
                result = cursor.getLong(0);
            }
        } catch (Exception e) {
            new ContactDaoException(e);
        } finally {
            closeCursor();
        }
        return result;
    }


    @Override
    protected String getTableName() {
        return TableGroupMember.TABLE_NAME;
    }

    public long insert(GroupMember groupMember) {
        long result = -1;
        ContentValues values = groupMember.getContentValues();
        try {
            result = database.insert(getTableName(), null, values);
        } catch (Exception e) {
            new ContactDaoException(e);
        }

        return result;
    }

    /**
     *
     * @param member
     * @return result > 0 更新成功; result <=0 更新失败
     */
    public int update(GroupMember member) {
        try {
            ContentValues values = member.getContentValues();
            String whereArgs = TableGroupMember.MEMBER_ACCOUNT + " = ? "
                    + " and " + TableGroupMember.GROUP_ID + " =? ";
            return database.update(getTableName(), values, whereArgs,
                    new String[]{member.getAccount(), member.getGroupId()});
        }catch (Exception e){
            new ContactDaoException(e);
        }
        return 0;
    }

    /**
     * @param member
     * @return result > 0 :删除成功 ; 否则失败
     */
    public int delete(GroupMember member) {
        /*String whereArgs = TableGroupMember.MEMBER_ACCOUNT + " = ? "
                +" and " + TableGroupMember.GROUP_ID + " =? ";
        return database.delete(getTableName(), whereArgs,
                new String[]{member.getAccount(), member.getGroupId()});*/
        StringBuffer sql = new StringBuffer();
        sql.append(" update ");
        sql.append( getTableName());
        sql.append(" set ");
        sql.append( TableGroupMember.IS_DELETED );
        sql.append(" = ");
        sql.append(GroupConvert.DELETED);
        sql.append(" where ");
        sql.append(TableGroupMember.MEMBER_ACCOUNT);
        sql.append(" = ");
        sql.append(member.getAccount());
        sql.append(" and ");
        sql.append(TableGroupMember.GROUP_ID);
        sql.append(" = ");
        sql.append(member.getGroupId());
        try {
            SQLiteStatement statement = database.compileStatement(sql.toString());
            return statement.executeUpdateDelete();
        }catch (Exception e){
            new ContactDaoException(e);
        }
        return 0;
    }

    /**
     * 删除所有的群组成员信息
     * @return
     */
    public boolean deleteAllGroupMembers(){
        int result = database.delete(getTableName(),null,null);
        return result > 0 ;
    }


    /**
     * 获取指定群组的成员更新序号
     * @param groupIds 指定群组，如为null，表示当前数据库中的所有群组
     * @return  map<string, Long>，key群组ID，value为该群组的成员更新序列
     */
    public Map<String, Long> initUpdateSerialMap(List<String> groupIds) {
        Map<String, Long> result = new HashMap<String, Long>();
        for(String groupId : groupIds){
            result.put(groupId,0l);
        }
        try {
            String groupId = "";
            String serial = "";
            cursor = database.rawQuery(GroupMemberDaoSqlBuilder.updateSerialMapSql(groupIds), null);
            while (cursor.moveToNext()) {
                groupId = cursor.getString(0);
                serial = cursor.getString(1);
                if(ObjectUtil.stringIsEmpty(groupId)){
                    continue;
                }else{
                    if(ObjectUtil.stringIsEmpty(serial)){
                        result.put(groupId, 0l);
                    }else{
                        result.put(groupId, Long.valueOf(serial));
                    }
                }
            }
        } catch (Exception e) {
            new ContactDaoException(e);
        } finally {
            closeCursor();
        }
        return result;
    }

    public List<GroupMember> queryByMemberIds(Map<String,String> maps){
        List<GroupMember> dataSource = new ArrayList<GroupMember>();
        try{
            cursor = database.rawQuery(GroupMemberDaoSqlBuilder.buildQuerySql(maps),null);
            while(cursor.moveToNext()){
                GroupMember member = new GroupMember(cursor);
                dataSource.add(member);
            }
        }catch (Exception e){
            new ContactDaoException(e);
        }finally {
           closeCursor();
        }
        return dataSource;
    }


    /**
     * key : groupId#account; value : groupId#account;
     * @return
     */
    public Map<String,String> queryGroupMembersKeyMap(){
        Map<String,String> keyMap = new HashMap<>();
        try{
            String split = "#";
            cursor = database.rawQuery(GroupMemberDaoSqlBuilder.queryGroupMemberKeyMap(),null);
            while(cursor.moveToNext()){
                StringBuffer keyValue = new StringBuffer();
                keyValue.append(cursor.getString(0));
                keyValue.append(split);
                keyValue.append(cursor.getString(1));
                keyMap.put(keyValue.toString(), keyValue.toString());
            }
        }catch (Exception e){
            new ContactDaoException(e);
        }finally {
            closeCursor();
        }
        return keyMap;
    }


    /**
     * key : groupId#account; value : groupId#account;
     * @return
     */
    public Map<String,String> getSerialIsZeroMap(){
        Map<String,String> keyMap = new HashMap<>();
        try{
            String args = " where " + TableGroupMember.UPDATE_SERIAL + " = 0 ";
            String sql = GroupMemberDaoSqlBuilder.queryGroupMemberKeyMap() + args;
            String split = "#";
            cursor = database.rawQuery(sql,null);
            while(cursor.moveToNext()){
                StringBuffer keyValue = new StringBuffer();
                keyValue.append(cursor.getString(0));
                keyValue.append(split);
                keyValue.append(cursor.getString(1));
                keyValue.append(split);//add by lwl 3005
                keyValue.append(cursor.getString(2));
                keyMap.put(keyValue.toString(),keyValue.toString());
            }
        }catch (Exception e){
            new ContactDaoException(e);
        }finally {
            closeCursor();
        }
        return keyMap;
    }

    /**
     * key : groupId#account; value : groupId#account;
     * @return
     */
    public Map<String,String> getDeletedGroupMemberMap(){
        Map<String,String> keyMap = new HashMap<>();
        try{
            String args = " where " + TableGroupMember.IS_DELETED + " = " + GroupConvert.DELETED;
            String sql = GroupMemberDaoSqlBuilder.queryGroupMemberKeyMap() + args;
            String split = "#";
            cursor = database.rawQuery(sql,null);
            while(cursor.moveToNext()){
                StringBuffer keyValue = new StringBuffer();
                keyValue.append(cursor.getString(0));
                keyValue.append(split);
                keyValue.append(cursor.getString(1));
                keyMap.put(keyValue.toString(),keyValue.toString());
            }
        }catch (Exception e){
            new ContactDaoException(e);
        }finally {
            closeCursor();
        }
        return keyMap;
    }

    //add by ysp@xdja.com
    /**
     * 批量删除多个联系人
     * @param members
     * @return  大于0表示成功，否则失败。
     */
    public int deleteMultiMember(List<GroupMember> members) {
        StringBuffer sql = new StringBuffer();
        sql.append(" update ");
        sql.append( getTableName());
        sql.append(" set ");
        sql.append( TableGroupMember.IS_DELETED );
        sql.append(" = ");
        sql.append(GroupConvert.DELETED);
        sql.append(" where "+TableGroupMember.MEMBER_ACCOUNT+" in (");


        for (int i = 0; i < members.size(); ++i) {
            sql.append(members.get(i).getAccount());
            if(!(i == members.size() - 1)) {  //不是倒数第一个的时候加 ,
                sql.append(", ");
            }
        }


        sql.append(") and ");
        sql.append(TableGroupMember.GROUP_ID);
        sql.append(" = ");
        sql.append(members.get(0).getGroupId());
        try {
            SQLiteStatement statement = database.compileStatement(sql.toString());
            return statement.executeUpdateDelete();
        }catch (Exception e){
            new ContactDaoException(e);
        }
        return 0;
    }

    //add by ysp@xdja.com
    /**
     *通过关键字查询群组内所有成员信息
     * @param groupId
     * @param key
     * @return
     */
    public List<UserInfo> getUserInfoByKey(String groupId, String key) {
        List<UserInfo> dataSource = new ArrayList<>();
        try{
            cursor = database.rawQuery(GroupMemberDaoSqlBuilder.queryUserInfoByGroupIdAndKey(key).toString(),new String[]{groupId});
            while(cursor.moveToNext()){
                UserInfo userInfo = new UserInfo();
                userInfo.setAccount(cursor.getString(cursor.getColumnIndex(TableGroupMember.MEMBER_ACCOUNT)));
                userInfo.setGroupMemberNickname(cursor.getString(cursor.getColumnIndex(TableGroupMember.MEMBER_NICKNAME)));
                userInfo.setAccountNickname(cursor.getString(cursor.getColumnIndex(TableActomaAccount.NICKNAME)));
                userInfo.setRemark(cursor.getString(cursor.getColumnIndex(TableFriend.REMARK)));

                userInfo.setAlias(cursor.getString(cursor.getColumnIndex(TableActomaAccount.ALIAS)));
                //add by wal@xdja.com  start for 1738
                userInfo.setDepartmentMemberName(cursor.getString(cursor.getColumnIndex(TableDepartmentMember.NAME)));
                //add by wal@xdja.com  end for 1738
                //add by ysp@xdja.com, start
                userInfo.setAvatar(cursor.getString(cursor.getColumnIndex(TableAccountAvatar.AVATAR)));
                //add by ysp@xdja.com, end
                Avatar avatar = new Avatar(cursor);
                userInfo.setAvatarBean(avatar);

                dataSource.add(userInfo);
            }
        }catch (Exception e){
            new ContactDaoException(e);
        }finally {
            closeCursor();
        }
        return dataSource;
    }

}
