package com.xdja.contact.callback.group;

/**
 * Created by XDJA_XA on 2015/7/28.
 *
 * 2016-01-19 wanghao 着手第一次重构
 *
 */
public interface IContactEvent {

    // 群组添加
    int EVENT_GROUP_ADDED = 0;

    //自己退群
    int QUIT = 1;
    //群解散
    int DISMISS = 2;
    //被移除
    int REMOVED = 3;
    //不同方式退出群
    //回话界面使用
    int EVENT_GROUP_QUIT = 4;


    //获取群信息完成
    int EVENT_GROUP_INFO_GET = 5;

    // 群组头像信息发生变更
    int EVENT_GROUP_AVATAR_CHANGED = 6;
    //群组昵称信息发生变更
    int EVENT_GROUP_NAME_CHANGED = 7;

    int EVENT_GROUP_CHANGED = EVENT_GROUP_AVATAR_CHANGED + EVENT_GROUP_NAME_CHANGED;

    // 群组成员添加
    int EVENT_MEMBER_ADDED = 8;
    // 群组成员数据发生变更
    int EVENT_MEMBER_UPDATED = 9;
    /**
     * 创建群组或者群组添加人员如果人员有问题提示标示
     * "blockAccounts":{"-1":["600012","600013"],"-2":["600011"],"-3":["600014","…"],}}
     * -1:群成员所在群数量超限，-2:账号不存在,-3:群成员已经在群中
    */
    int EVENT_GROUP_MEMBER_TIPS = 10;

    //
    int EVENT_GROUP_REFRESH = 11;

    // 联系人内部使用
    int EVENT_GROUP_LIST_REFRESH = 12;

    //好友请求点击接受
    int EVENT_FRIEND_CLICKED_ACCEPT = 13;
    //好友删除
    int EVENT_FRIEND_CLICKED_DELETE = 14;
    //修改好友备注 通知密信 密话更新对应的显示状态
    int EVENT_FRIEND_UPDATE_REMARK = 15;
    //好友被请求方接受请求,主动发起方收到推送
    int EVENT_FRIEND_REQUESTED_ACCEPT_PUSH = 16;
    //好友修改自己的昵称，下拉好友的时候获取到通知 密信 密话更新
    int EVENT_FRIEND_UPDATE_NICKNAME = 17;

    /**
     * 创建群组或者群组添加人员如果人员有问题提示标示
     * 存在未关联安全设备的用户
     */
    int EVENT_NO_SEC_GROUP_MEMBER_TIPS = 18;
    /**
     * 群组添加人员如果人员有问题提示标示
     * 所拉成员已经存在在群组中
     */
    int EVENT_MEMBER_IN_GROUP_TIPS = 19;
    /**
     * 联系人模块数据变更通知其他模块
     * @param event 事件类型
     * @param param1
     * @param param2
     * @param param3
     */
    void onEvent(int event, Object param1, Object param2, Object param3);

}
