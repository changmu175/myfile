package com.xdja.contact.service;

import com.xdja.contact.bean.Member;
import com.xdja.contact.dao.MemberDao;
import com.xdja.comm.uitl.ObjectUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hkb.
 * @since 2015/7/17/0017.
 */
public class MemberService {

    private MemberDao memberDao;

    public MemberService() {
        memberDao = new MemberDao();
    }

    /**
     * 根据workid 批量查询对应的集团人员信息
     * @param workIds
     * @return 返回数据可能为空
     */
    public Map<String,Member> findMapByIds(List<String> workIds){
        Map<String,Member> mapMembers = new HashMap<>();
        List<Member> result = this.findMembersByIds(workIds);
        for(Member member : result){
            if(!ObjectUtil.stringIsEmpty(member.getAccount())) {
                mapMembers.put(member.getAccount(), member);
            }
        }
        return mapMembers;
    }

    /***
     * 根据ids 批量查询Member
     * @param workIds
     * @return
     */
    public List<Member> findMembersByIds(List<String> workIds){
        List<Member> dataSource = new ArrayList<>();
        synchronized (memberDao.helper){
            memberDao.getReadableDataBase();
            dataSource = memberDao.findMembersByIds(workIds);
            memberDao.closeDataBase();
        }
        return  dataSource;
    }



    /********以下代码未进行重构******************************************/

    public Member getMemberById(String id){
        Member memberById = null;
        synchronized (memberDao.helper){
            memberDao.getWriteDataBase();
            memberById = memberDao.query(id);
            memberDao.closeDataBase();
        }
        return  memberById;
    }
    public Member getMemberByAccount(String account){
        Member memberByAccount = null;
        synchronized (memberDao.helper){
            memberDao.getWriteDataBase();
            memberByAccount = memberDao.findMemberByAccount(account);
            memberDao.closeDataBase();
        }
        return memberByAccount;
    }

    /**
     * 获取部门下的人员信息
     * @param departid 部门表的departid
     * @return
     */
    public List<Member> getMembersInDepart(String departid){
        return getMembersInDepart(departid, false);
    }

    /**
     * 获取部门下的人员信息
     * @param departid 部门表的departid
     * @param isATuser 是否只查询安通用户
     * @return
     */
    public List<Member> getMembersInDepart(String departid,boolean isATuser){
        List<Member> membersByDepartId = null;
        synchronized (memberDao.helper){
            memberDao.getWriteDataBase();
            membersByDepartId = memberDao.getMembersByDepartId(departid, isATuser);
            memberDao.closeDataBase();
        }
        return membersByDepartId;
    }

    //[s]modify by xienana for count department member @20161124 review by tangsha
    public int getMembersCount(){
        int count ;
        synchronized (memberDao.helper){
            memberDao.getWriteDataBase();
            count = memberDao.getDepartmentMemberCount();
            memberDao.closeDataBase();
        }
        return count;
    }
    //[e]modify by xienana for count department member @20161124 review by tangsha

    public List<Member> getAllMembers(){
        List<Member> allMembers = null;
        synchronized (memberDao.helper) {
            memberDao.getWriteDataBase();
            allMembers = memberDao.getAllMembers();
            memberDao.closeDataBase();
        }
        return  allMembers;
    }
    public List<Member> getAllMembersJoinDepartment(){
        List<Member> allMembers = null;
        synchronized (memberDao.helper) {
            memberDao.getWriteDataBase();
            allMembers = memberDao.getAllMembersJoinDepartment();
            memberDao.closeDataBase();
        }
        return  allMembers;
    }


    public List<String> getMembersNoAccountInfo(){
        List<String> allMembers = null;
        synchronized (memberDao.helper) {
            memberDao.getWriteDataBase();
            allMembers = memberDao.getMembersNoAccountInfo();
            memberDao.closeDataBase();
        }
        return  allMembers;
    }

    /**
     * 获取部门下的人员信息
     * @param id 部门表的ID
     * @return
     */
    public List<Member> getMembersInDepart(int id){
        List<Member> membersByDepartId = null;
        synchronized (memberDao.helper) {
            memberDao.getWriteDataBase();
            membersByDepartId = memberDao.getMembersByDepartId(id);
            memberDao.closeDataBase();
        }
        return membersByDepartId ;
    }

    /**
     * 集团联系人搜索
     * 支持搜索集团名称简拼 全拼 帐号 模糊
     * @param keyword
     * @return
     */
    public List<Member> searchMember(String keyword){
        if(ObjectUtil.stringIsEmpty(keyword)){
            return null;
        }
        List<Member> resultMembers = new ArrayList<>();
        synchronized (memberDao.helper) {
            memberDao.getReadableDataBase();
            resultMembers = memberDao.findMembersLikethis(keyword);
            memberDao.closeDataBase();
        }
        return resultMembers;
    }


    /**
     * 批量插入集团联系人
     * @param members
     */
    public void insert(List<Member> members) {
        synchronized (memberDao.helper) {
            memberDao.getWriteDataBase();
            memberDao.insert(members);
            memberDao.closeDataBase();
        }
    }

    /**
     * 批量删除集团联系人
     * @param members
     */
    public void delete(List<Member> members){
        synchronized (memberDao.helper) {
            memberDao.getWriteDataBase();
            memberDao.delete(members);
            memberDao.closeDataBase();
        }
    }




    //删除部门表和部门成员表
    public boolean deleteDeparmentMember(){

        boolean result = false;
        //add by lwl 2177 synchronized
        synchronized (memberDao.helper) {
            memberDao.getWriteDataBase();
            result = memberDao.deleteTableData();
            memberDao.closeDataBase();
        }
        return result;
    }


    public boolean existDeptMemeberTable(){
        boolean isExist = false;
        //add by lwl 2177 synchronized
        synchronized (memberDao.helper) {
            memberDao.getWriteDataBase();
            isExist = memberDao.exits();
            memberDao.closeDataBase();
        }
        return isExist;
    }

}
