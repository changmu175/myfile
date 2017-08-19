package com.xdja.contactopproxy;

import android.content.Context;
import android.content.res.Resources;

import com.xdja.comm.event.BusProvider;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.bean.Group;
import com.xdja.contact.bean.GroupMember;
import com.xdja.contact.callback.group.IContactEvent;
import com.xdja.contactcommon.GroupProxy;
import com.xdja.imp.data.di.annotation.Scoped;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

/**
 * Created by liyingqing on 16-3-21.
 */
public class ContactCallBack implements IContactEvent {
    Context context;

    GroupEventProduce groupEventProduce;

    @Inject
    public ContactCallBack(@Scoped(com.xdja.imp.data.di.DiConfig.CONTEXT_SCOPE_APP)Context context, GroupEventProduce groupEventProduce){
        this.context = context;
        this.groupEventProduce = groupEventProduce;
        GroupProxy.addGroupListener(context,this);
    }

    @Override
    public void onEvent(int event, Object param1, Object param2, Object param3) {
        LogUtil.getUtils().e("ActomaContactCallBack onEvent event is "+event);
        try {
            switch (event) {
                // 群组添加
                case EVENT_GROUP_ADDED:
                    groupEventProduce.groupAdded((Collection<Group>) param1);
                    break;

                // 群组成员添加
                case EVENT_MEMBER_ADDED:
                    groupEventProduce.groupMemberAdded((Collection<GroupMember>) param1);
                    break;

                // 群组成员数据发生变更 发现有未加入群组的情况 add by wal@xdja.com
                case EVENT_GROUP_MEMBER_TIPS:
                    groupEventProduce.groupMemberAddedFail((Collection<String>) param1,(Collection<String>) param2,(String) param3);
                    break;

                // 群组成员数据发生变更 发现有未关联设备账号的情况 add by wal@xdja.com
                case EVENT_NO_SEC_GROUP_MEMBER_TIPS:
                    groupEventProduce.groupNosecMemberAddedFail((Collection<String>) param1,(String) param2);
                    break;

                // 群组成员数据发生变更 发现有已在群组中情况 add by wal@xdja.com for 4116
                case EVENT_MEMBER_IN_GROUP_TIPS:
                    groupEventProduce.groupMemberInGroupAddedFail((Collection<String>) param1,(String) param2);
                    break;

                // 群组成员数据发生变更
                case EVENT_MEMBER_UPDATED:
                    groupEventProduce.groupMemberUpdated((Collection<GroupMember>) param1);
                    break;

                //群会话界面使用
                // 群组头像信息发生变更
                case EVENT_GROUP_AVATAR_CHANGED:
                    groupEventProduce.groupAvatarChanged((String) param1, (String) param2);
                    break;

                //群组名称信息发生变更
                case EVENT_GROUP_NAME_CHANGED:
                    groupEventProduce.groupNameChanged((String) param1, (String) param2);
                    break;

                //不同方式退出群
                //回话界面使用
                case EVENT_GROUP_QUIT:
                    groupEventProduce.groupQuit((String) param1, (String) param2, (int) param3);
                    break;
                //获取群组信息成功
                case EVENT_GROUP_INFO_GET:
                case EVENT_GROUP_REFRESH:
                    if(param1 != null && param2 != null) {
                        //清除群组缓存中有变化的数据
                        ContactCache.getInstance().clearCache();
                        BusProvider.getMainProvider().post(new ContactProxyEvent.GetGroupInfoEvent((String) param1, (Integer) param2));
                    }
                    break;
                case EVENT_FRIEND_CLICKED_ACCEPT:
                    if(param1!=null){
                        //start:modify by wal@xdja.com for 好友事件通知
//                        BusProvider.getMainProvider().post(new ContactProxyEvent.AcceptFriendEvent((String)param1));
                        ContactProxyEvent.AcceptFriendEvent acceptFriendEvent = new  ContactProxyEvent.AcceptFriendEvent((String)param1);
                        acceptFriendEvent.setMessageStr(getResString(R.string.friend_accept_request_first_message)+ param2 +getResString(R.string.friend_accept_request_second_message));
                        BusProvider.getMainProvider().post(acceptFriendEvent);
                        //end:modify by wal@xdja.com for 好友事件通知
                    }
                    break;
                case EVENT_FRIEND_CLICKED_DELETE:
                    //[S]add by lixiaolong on 20160922. fix bug 4392. review by wal.
                    //清除联系人缓存中有变化的数据
                    ContactCache.getInstance().clearCache();
                    //[E]add by lixiaolong on 20160922. fix bug 4392. review by wal.
                    if(param1!=null){
                        BusProvider.getMainProvider().post(new ContactProxyEvent.DeletFriendClearTalkEvent((String)param1));
                    }
                    break;
                case EVENT_FRIEND_UPDATE_REMARK:
                    //清除联系人缓存中有变化的数据
                    ContactCache.getInstance().clearCache();
                    ContactProxyEvent.RemarkUpdateEvent remarkUpdateEvent = new ContactProxyEvent.RemarkUpdateEvent();
                    remarkUpdateEvent.setAccount((String)param1);
                    remarkUpdateEvent.setShowName((String)param2);
                    BusProvider.getMainProvider().post(remarkUpdateEvent);
                    break;
                case EVENT_FRIEND_REQUESTED_ACCEPT_PUSH:
                    //发送方 插入提示语
                    ArrayList<String> ownerIds =(ArrayList<String>) param1;
                    for (String ownerId : ownerIds) {
                        ContactProxyEvent.ReceiveAcceptFrientEvent receiveAcceptFrientEvent = new ContactProxyEvent.ReceiveAcceptFrientEvent();
                        receiveAcceptFrientEvent.setAccount(ownerId);
                        receiveAcceptFrientEvent.setMessage(getResString(R.string.accept_message));
                        BusProvider.getMainProvider().post(receiveAcceptFrientEvent);
                    }
                    break;
                case EVENT_FRIEND_UPDATE_NICKNAME:
                    //清除联系人缓存中有变化的数据
                    ContactCache.getInstance().clearCache();
                    ContactProxyEvent.NickNameUpdateEvent nickNameUpdateEvent = new ContactProxyEvent.NickNameUpdateEvent();
                    nickNameUpdateEvent.setAccounts((ArrayList<String>) param1);
                    BusProvider.getMainProvider().post(nickNameUpdateEvent);
                    break;
            }
        } catch (Exception e) {
            LogUtil.getUtils().e("ContactCallBack  onEvent error:"+e.getMessage());
        }
    }
    private String getResString(int resourceId) {
        Resources resources = context.getResources();
        return resources.getString(resourceId);
    }



}
