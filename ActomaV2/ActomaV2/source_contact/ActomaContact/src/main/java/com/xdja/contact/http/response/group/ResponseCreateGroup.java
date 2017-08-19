package com.xdja.contact.http.response.group;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by XDJA_XA on 2015/7/23.
 */
public class ResponseCreateGroup implements Serializable {
    /*
    {"groupId":204,
     "groupNamePy":"csz",
     "groupNamePinyin":"ceshizu",
     "ksgId":"66",
     "memberSeq":0}
     */

    /**
     * server分配的id
     */
    private long groupId;
    /**
     * 群组名称首字母
     */
    private String groupNamePy;
    /**
     * 群组名称全拼
     */
    private String groupNamePinyin;
    /**
     * ksgId
     */
    private String ksgId;
    /**
     * 成员更新序列
     */
    private long memberSeq;
    /**
     * 群创建时间
     */
    private long createTime; //add by wal@xdja.com for 1737

    /**
     * 创建群组时未成功的账号
     * "blockAccounts":{"-1":["600012","600013"],"-2":["600011"],"-3":["600014","…"],}}
     * -1:群成员所在群数量超限，-2:账号不存在,-3:群成员已经在群中
     */
    private Map<String,List<String>> blockAccounts;

    public static final String OUT_OF_GROUP_RANGE = "-1";

    public static final String ACCOUNT_NOT_EXIST = "-2";

    public static final String MEMBER_EXIST_IN_GROUP = "-3";


    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getGroupNamePy() {
        return groupNamePy;
    }

    public void setGroupNamePy(String groupNamePy) {
        this.groupNamePy = groupNamePy;
    }

    public String getGroupNamePinyin() {
        return groupNamePinyin;
    }

    public void setGroupNamePinyin(String groupNamePinyin) {
        this.groupNamePinyin = groupNamePinyin;
    }

    public String getKsgId() {
        return ksgId;
    }

    public void setKsgId(String ksgId) {
        this.ksgId = ksgId;
    }

    public long getMemberSeq() {
        return memberSeq;
    }

    public void setMemberSeq(long memberSeq) {
        this.memberSeq = memberSeq;
    }

    //start:add by wal@xdja.com for 1737
    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
    //end:add by wal@xdja.com for 1737

    public Map<String, List<String>> getBlockAccounts() {
        return blockAccounts;
    }

    public void setBlockAccounts(Map<String, List<String>> blockAccounts) {
        this.blockAccounts = blockAccounts;
    }
}
