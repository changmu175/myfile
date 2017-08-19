package com.xdja.contact.dao;

import com.xdja.contact.bean.Group;
import com.xdja.contact.dao.sqlbuilder.GroupDaoSqlBuilder;
import com.xdja.contact.database.columns.TableGroup;
import com.xdja.contact.exception.ContactDaoException;
import com.xdja.contact.util.ContactUtils;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.dependence.uitls.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by XDJA_XA on 2015/7/9.
 *
 *  wanghao 2016-01-19 只是对当前文件内函数调整，文件内功能一致的函数暂时未执行合并
 *  2016-01-21 修正和合并动作暂时执行到目前情况
 */
public class GroupDao extends AbstractContactDao<Group> {


    public Group query(String id) {
        Group result = null;
        try {
            String[] selectionArgs = new String[]{id};
            cursor = database.rawQuery(GroupDaoSqlBuilder.findGroupById().toString(), selectionArgs);
            if(cursor.moveToFirst()){
                result = new Group(cursor);
            }
        } catch (Exception e) {
            new ContactDaoException(e);
        } finally {
            closeCursor();
        }
        return result;
    }

    /**
     * 查询本地所有群组信息(包含已经删除的群组),这里查询的就是所有的群组数据,按照时间倒序
     * 备注:本地群组的删除不是物理上的删除，仅仅只是修改一个状态不显示而已
     * @return
     */
    public List<Group> queryGroups(){
        List<Group> dataSource = new ArrayList<Group>();
        try {
            cursor = database.rawQuery(GroupDaoSqlBuilder.queryGroups().append(GroupDaoSqlBuilder.descTime()).toString(), null);
            while (cursor.moveToNext()) {
                Group group = new Group(cursor);
                dataSource.add(group);
            }
        } catch (Exception e) {
            new ContactDaoException(e);
        } finally {
            closeCursor();
        }
        return dataSource;
    }

    /**
     * @see GroupDao#queryGroups();
     * @return
     */
    public Map<String,Group> queryMapGroups(){
        Map<String,Group> dataSource = new HashMap<String,Group>();
        try {
            cursor = database.rawQuery(GroupDaoSqlBuilder.queryGroups().append(GroupDaoSqlBuilder.descTime()).toString(), null);
            while (cursor.moveToNext()) {
                Group group = new Group(cursor);
                dataSource.put(group.getGroupId(),group);
            }
        } catch (Exception e) {
            new ContactDaoException(e);
        } finally {
            closeCursor();
        }
        return dataSource;
    }



    public List<Group> queryAll() {
        //return getAllGroups(TableGroup.SERVER_CREATE_TIME, true);
        return filterUnDeleteGroups();
    }

    /**<b>
     * 查询所有群组并根据显示状态过滤
     * <li>1 如果群组已经删除则不显示 反之显示(这里的删除是群主解散了群组，对于群成员来讲就是删除，客户端收到推送就设置不显示)</li>
     * <li>2 如果群组当前状态是未删除则表示两种情况 一: 当前用户在当前群且群未解散 二: 当前群未解散但是当前用户已经被剔除群</li>
     * </b>
     * <p>综上:返回的数据过滤掉群主已经解散的群组和用户已经被剔除的群</p>
     * @return
     */
    public List<Group> filterUnDeleteGroups() {
        List<Group> dataSource = new ArrayList<Group>();
        String currentAccount = ContactUtils.getCurrentAccount();
        try {
            cursor = database.rawQuery(GroupDaoSqlBuilder.filterUnDeleteGroups().toString(), new String[]{currentAccount});
            while (cursor.moveToNext()) {
                Group group = new Group(cursor);
                dataSource.add(group);
            }
        } catch (Exception e) {
            new ContactDaoException(e);
        } finally {
            closeCursor();
        }
        return dataSource;
    }

    public long insert(Group group) {
        long result = -1;
        try {
            result = database.insert(getTableName(), null, group.getContentValues());
        } catch (Exception e) {
            new ContactDaoException(e);
        }
        return result;
    }

    public int update(Group group) {
        int result = 0;
        try {
            String whereArgs = TableGroup.GROUP_ID + " = ? ";
            result = database.update(getTableName(), group.getContentValues(), whereArgs, new String[]{group.getGroupId()});
        }catch (Exception e){
            new ContactDaoException(e);
        }
        return result;
    }

    public int delete(Group group) {
        int result = 0;
        String whereArgs = TableGroup.GROUP_ID + " = ? ";
        try {
            result = database.delete(getTableName(), whereArgs, new String[]{group.getGroupId()});
        } catch (Exception e) {
            new ContactDaoException(e);
        }
        return result;
    }

    /**
     * 根据groupId 查询群组
     * @return Group
     */
    public Group queryGroupById(String groupId) {
        return query(groupId);
    }

    /**
     * 查询最大的群组更新序列
     * @return  最大的群组更新序列
     */
    public String initMaxUpdateSerial() {
        String result = "0";
        try{
            cursor = database.rawQuery(GroupDaoSqlBuilder.queryMaxUpdateSerial().toString(), null);
            if(cursor.moveToFirst()){
                result = cursor.getString(0);
                if(ObjectUtil.stringIsEmpty(result)){
                   result = "0";
                }
            }
        }catch (Exception e){
            new ContactDaoException(e);
        }finally {
            closeCursor();
        }
        return result;
    }


    /**
     * 统计群总数
     * @return
     */
    public long countGroups(){
        long result = 0l;
        try {
            cursor = database.rawQuery("select count ( " + TableGroup.GROUP_ID + " ) from " + getTableName(), null);
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
        return TableGroup.TABLE_NAME;
    }

    public List<Group> queryGroupByKey(String key) {
        List<Group> dataSource = new ArrayList<>();
        try{
            cursor = database.rawQuery(GroupDaoSqlBuilder.queryGroupByKey(key).append(GroupDaoSqlBuilder.descTime()).toString(), null);
            while(cursor.moveToNext()) {
                Group group = new Group(cursor);
                dataSource.add(group);
            }
        } catch (Exception e) {
            LogUtil.getUtils().e("GroupDao queryGroupByKey Exception:"+e.getMessage());
        }finally {
            cursor.close();
        }
        return dataSource;
    }

    /**
     * tangsha add for 8110
     * 查询没有群成员信息的群组
     */
    public List<Group> queryGroupNoMemberInfo() {
        List<Group> dataSource = new ArrayList<>();
        try{
            cursor = database.rawQuery(GroupDaoSqlBuilder.queryGroupNoMemberInfo().toString(), null);
            if(cursor != null) {
                while (cursor.moveToNext()) {
                    Group group = new Group(cursor);
                    dataSource.add(group);
                }
            }
        } catch (Exception e) {
            LogUtil.getUtils().e("GroupDao queryGroupNoMemberInfo Exception:"+e.getMessage());
        }finally {
            if(cursor != null) {
                cursor.close();
            }
        }
        return dataSource;
    }
}
