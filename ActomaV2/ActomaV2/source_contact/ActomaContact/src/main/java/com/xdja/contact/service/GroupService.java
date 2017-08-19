package com.xdja.contact.service;

import com.xdja.contact.bean.Group;
import com.xdja.contact.dao.GroupDao;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：ActomaV2
 * 类描述：分享转发中，获取群信息接口
 * 创建人：ycm
 * 创建时间：2016/11/1 17:11
 * 修改人：ycm
 * 修改时间：2016/11/1 17:11
 * 修改备注：
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 */
public class GroupService {
    private GroupDao groupDao;

    public GroupService(){
        this.groupDao = new GroupDao();
    }
    /**
     * 查询所有好友已经排除了已经删除的好友
     * @return
     */
    public List<Group> queryGroups(){
        List<Group> result = new ArrayList<Group>();
        synchronized (groupDao.helper){
            groupDao.getReadableDataBase();
            result = groupDao.queryAll();
            groupDao.closeDataBase();
        }
        return result;
    }
}
