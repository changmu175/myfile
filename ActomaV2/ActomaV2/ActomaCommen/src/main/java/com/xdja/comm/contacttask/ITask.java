package com.xdja.comm.contacttask;

/**
 * Created by wanghao on 2015/12/22.
 * 所有推送业务接口
 * 针对单一的增量业务需要实现AbstractTask
 * 针对需要频繁在一个任务里多次调用服务器的需要使用类似AbstractAccountTask
 * note :
 *
 */
public interface ITask {

    //通讯录更新推送
    String PUSH_CONTACT_UPDATE = "contactUpdate";
    //通讯录解绑
    String PUSH_CONTACT_UNBIND = "contactUnbind";
    /**
     * 账户推送更新
     */
    String PUSH_ACCOUNT_UPDATE = "accountUpdate";
    /**好友模块推送*******************/

    String PUSH_REQUEST = "friendReq";

    String PUSH_ACCEPT = "acceptFriend";

    String PUSH_DELETE = "deleteFriend";

    String PUSH_MODIFY_REMARK = "modifyRemark";

    /*****群组模块推送标示******************/
    //创建群组成功推送通知
    String PUSH_CREATE_GROUP = "createGroup";
    //解散群组推送通知
    String PUSH_DELETE_GROUP = "deleteGroup";
    //修改群组名称推送通知
    String PUSH_UPDATE_GROUP_NAME = "updateGroupName";
    //修改群组头像推送通知
    String PUSH_UPDATE_GROUP_AVATAR = "updateGroupAvatar";


    //添加成员通知推送，格式 addGroupMember#origin#groupid
    String PUSH_ADD_GROUP_MEMBER = "addGroupMember";
    //移除成员通知推送，格式 removeGroupMember#{groupid}
    String PUSH_REMOVE_GROUP_MEMBER = "removeGroupMember";
    //退出群组推送，格式 quitGroup#{groupid}
    String PUSH_QUIT_GROUP = "quitGroup";
    //修改群组个人昵称推送，格式 updateGroupNickname#{groupid}
    String PUSH_UPDATE_GROUP_NICKNAME  = "updateGroupNickname";





    //获取联系人相关配置
    String CONFIGURATION_TASK = "configurationTask";

    String INCREMENT_ACCOUNT_TASK = "incrementAccountTask";

    String INCREMENT_FRIEND_TASK = "incrementFriendTask";

    String INCREMENT_GROUP_TASK = "incrementGroupTask";

    String INCREMENT_DEPART_TASK = "incrementDepartTask";
    String INCREMENT_DEPART_MEMBER_TASK = "incrementDepartMemberTask";//add by lwl

    String PUSH_INCREMENT_GROUP_TASK= "pushincrementGroupTask";//add by wal@xdja.com for 3859

    String getTaskId();

    String getReason();

    void template();

}
