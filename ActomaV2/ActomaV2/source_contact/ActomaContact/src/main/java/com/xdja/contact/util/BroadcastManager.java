package com.xdja.contact.util;

import android.content.Intent;

import com.xdja.comm.server.ActomaController;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.comm.uitl.RegisterActionUtil;
import com.xdja.contact.bean.Friend;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghao on 2016/4/27.
 * 联系人模块所有的通知都放在当前对象处理
 */
public class BroadcastManager {

    /**
     * 下拉刷新好友列表发送广播通知 voip 和 simc
     */
    public static void sendFriendsUpdateBroadcast(List<Friend> list){
        ArrayList<String> accounts = new ArrayList<String>();
        for(Friend friend : list){
            accounts.add(friend.getAccount());
        }
        Intent intent = new Intent();
        intent.setAction(RegisterActionUtil.ACTION_FRIEND_PULL_UPDATE);
        intent.putStringArrayListExtra("accounts", accounts);
        ActomaController.getApp().sendBroadcast(intent);
    }

    /**
     * 刷新好友列表
     * 1 好友列表数据下拉刷新成功
     * 2 好友列表: A 添加B为好友，B接受；A收到推送执行增量更新成功
     */
    public static void refreshFriendList(){
        Intent intent = new Intent();
        intent.setAction(RegisterActionUtil.ACTION_REFRESH_LIST);
        ActomaController.getApp().sendBroadcast(intent);
    }

    /**
     * 刷新好友请求列表
     * 好友请求下载完成
     */
    public static void refreshFriendRequestList(){
        Intent intent = new Intent();
        intent.setAction(RegisterActionUtil.ACTION_FRIEND_REQUEST);
        ActomaController.getApp().sendBroadcast(intent);
    }

    /**
     * 删除好友或者集团通讯录成员更改三方加密联系上方布局
     * @param account
     */
    public static void sendBroadcastDeleteFriendOrDepartMember(String account){
        Intent intent = new Intent();
        intent.setAction(RegisterActionUtil.ACTION_DELETE_FRIEND_OR_DEPARTMEMBER);
        intent.putExtra("account", account);
        ActomaController.getApp().sendBroadcast(intent);
    }
    /**
     * 删除集团人员关闭三方加密通道小盾牌
     */
    public static void sendBroadcastDelDeprtMemberCloseTransfer(){
        Intent intent = new Intent();
        intent.setAction(RegisterActionUtil.ACTION_DELETE_DEPARTMEMBER_CLOSE_TRANSFER);
        ActomaController.getApp().sendBroadcast(intent);
    }

    /**
     * 告诉contactFrame去刷新顶部菜单上的名称
     */
    public static void sendBroadcastRefreshTabName(){
        Intent intent = new Intent();
        intent.setAction(RegisterActionUtil.ACTION_CONTACT_REFRESH_TAB_NAME);
        ActomaController.getApp().sendBroadcast(intent);
    }

    /**
     * 给主框架发广播告诉昵称或者备注改变
     * @param account
     */
    public static void sendBroadcastRefreshName(String account){
        Intent intent = new Intent();
        intent.setAction(RegisterActionUtil.ACTION_CHANGE_NICK_NAME);
        intent.putExtra("account", account);
        ActomaController.getApp().sendBroadcast(intent);
    }

    /**
     * 告诉主框架好友被删除
     */
    public static void sendBroadcastCloseTransfer(){
        Intent intent = new Intent();
        intent.setAction(RegisterActionUtil.ACTION_CLOSE_FRAME_SAFETRANSFER);
        intent.putExtra("time", System.nanoTime());
        ActomaController.getApp().sendBroadcast(intent);
        //[S] remove by LiXiaolong on 20160824. fix bug 3303. review by WangChao1.
//        Intent intent2 = new Intent();
//        intent2.setAction(RegisterActionUtil.ACTION_DELETE_FRIEND_CLOSE_TRANSFER);
//        ActomaController.getApp().sendBroadcast(intent2);
        //[E] remove by LiXiaolong on 20160824. fix bug 3303. review by WangChao1.
    }

    /**
     * 显示名称变更
     * @param showName
     */
    public static void openFrameSafeSwitch(String showName) {
        //打开 主框架 加密服务主开关
        Intent intent = new Intent();
        intent.setAction(RegisterActionUtil.ACTION_OPEN_FRAME_SWITCH);
        intent.putExtra("nickName", showName);
        ActomaController.getApp().sendBroadcast(intent);
    }


    /**
     * 开启第三方加密
     */
    public static void sendBroadcast() {
        Intent intent = new Intent();
        intent.setAction(RegisterActionUtil.ACTION_SELECTED_OPEN_TRANSFER);
        ActomaController.getApp().sendBroadcast(intent);
    }

    //好友列表点击开启安全通信
    public static void sendOpenSafeTransferBroadcast(String account){
        Intent intent = new Intent();
        intent.setAction(RegisterActionUtil.ACTION_OPEN_FRAME_SAFETRANSFER);
        intent.putExtra("account", account);
        ActomaController.getApp().sendBroadcast(intent);
    }

    /**
     * 刷新集团联系人列表
     */
    public static void refreshCompanyContact(){
        Intent intent = new Intent();
        intent.setAction(RegisterActionUtil.ACTION_DEPARTMENT_DOWNLOAD_SUCCESS);
        ActomaController.getApp().sendBroadcast(intent);
    }

    /**
     * 单个用户信息更新完成通知相关账户信息界面更新
     */
    public static void refreshCommonDetail(String workID,String account){//fix 1212 by wal@xdja.com
        Intent intent = new Intent();
        intent.setAction(RegisterActionUtil.ACTION_ACCOUNT_INFO_DOWNLOAD_SUCCESS);
        //start:modify for update actoma acount by wal@xdja.com
        if(ObjectUtil.stringIsEmpty(workID)){
            intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_TYPE,RegisterActionUtil.EXTRA_KEY_DATA_TYPE_ACCOUNT);
            intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_KEY,account);
        }else{
            intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_TYPE,RegisterActionUtil.EXTRA_KEY_DATA_TYPE_WORK_ID);
            intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_KEY,workID);
        }
//        intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_KEY,account);//fix 1212 by wal@xdja.com
        //end:modify for update actoma acount by wal@xdja.com
        ActomaController.getApp().sendBroadcast(intent);
    }

}
