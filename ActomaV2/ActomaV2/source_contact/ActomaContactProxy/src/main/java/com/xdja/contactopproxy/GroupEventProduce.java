package com.xdja.contactopproxy;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import com.xdja.comm.event.BusProvider;
import com.xdja.comm.server.AccountServer;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.bean.Group;
import com.xdja.contact.bean.GroupMember;
import com.xdja.contact.callback.group.IContactEvent;
import com.xdja.contact.service.GroupInternalService;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.contactcommon.ContactModuleProxy;
import com.xdja.contactcommon.GroupProxy;
import com.xdja.contactcommon.dto.ContactDto;
import com.xdja.frame.presenter.ActivityStack;
import com.xdja.imp.data.di.annotation.Scoped;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by liyingqing on 16-3-22.
 */
public class GroupEventProduce {

    Context context;

    @Inject
    public GroupEventProduce(@Scoped(com.xdja.imp.data.di.DiConfig.CONTEXT_SCOPE_APP)Context context){
        this.context = context;
    }
    /**
     * 群组添加
     *
     * @param addedGroups Collection<Group>
     */
    protected void groupAdded(Collection<Group> addedGroups) {
        if (addedGroups == null || addedGroups.isEmpty()) {
            return;
        }
        //判断当前用户是否登陆成功
        //从主框架获取信息
        String account = AccountServer.getAccount().getAccount();
        if(TextUtils.isEmpty(account)){
            return;
        }

        for (Group group : addedGroups) {
            //判断当前登陆账户是否群主
            if (!account.equals(group.getGroupOwner())) {
                SendGroupUpdateEvent(group.getGroupId());//add by wal@xdja.com for 4160
                continue;
            }

            List<GroupMember> groupMembers = GroupInternalService.getInstance().getMembers(group);
            if (groupMembers == null) {
                continue;
            }

            String textStr = "";
            int memberCount = 0;//成员数量（不算群主）
            for (GroupMember groupMember : groupMembers) {
                if (groupMember.getAccount().equals(group.getGroupOwner())) {

                    textStr = getResString(R.string.groupUpdate_show_self) + getResString(R.string.groupUpdate_invite) + textStr;
                } else {
                    ContactDto contactDto = ContactModuleProxy.getContactInfo(groupMember.getAccount());
                    if (memberCount == 0) {
                        if (groupMember.getAccount().equals(group.getGroupOwner())) {
                            textStr += getResString(R.string.groupUpdate_show_self);
                        } else {
                            if (contactDto != null) {
                                textStr += contactDto.getName();
                            }
//                            textStr += groupMember.getDisplayName();
                        }
                    } else {
                        if (groupMember.getAccount().equals(group.getGroupOwner())) {
                            textStr += getResString(R.string.groupUpdate_separate) + getResString(R.string.groupUpdate_show_self);
                        } else {
//                            textStr += getResString(R.string.groupUpdate_separate) + groupMember.getDisplayName();
                            textStr += getResString(R.string.groupUpdate_separate) + contactDto.getName();
                        }
                    }
                }

                memberCount++;
            }

            textStr += getResString(R.string.groupUpdate_add);
            //分发事件 log for 6063
            String groupId = group.getGroupId();
            LogUtil.getUtils().w("GroupEventProduce groupAdded "+groupId+" textStr "+textStr);
            SendGroupSystemMessageEvent(groupId,textStr);
        }

    }


    /**
     * 群组成员添加
     *
     * @param groupMembers
     */
    protected void groupMemberAdded(Collection<GroupMember> groupMembers) {
        if (groupMembers == null || groupMembers.isEmpty()) {
            return;
        }

        String account = AccountServer.getAccount().getAccount();
        if(TextUtils.isEmpty(account)){
            return;
        }

        //先获取所有的邀请者ID
        HashMap<String, String> groupIdMap = new HashMap<>();

        for (GroupMember groupMember : groupMembers) {
            //[s]modify by xienana for bug 5649 [review by tangsha]
            groupIdMap.put(groupMember.getGroupId()+"_"+groupMember.getInviteAccount()+"_"+groupMember.getCreateTime(),groupMember.getInviteAccount());
            //start:add by wal@xdja.com for 1739
            if(account.equals(groupMember.getAccount())){
                groupIdMap.clear();
                groupIdMap.put(groupMember.getGroupId()+"_"+groupMember.getInviteAccount()+"_"+groupMember.getCreateTime(),groupMember.getInviteAccount());
                break;
            }
            //[e]modify by xienana for bug 5649 [review by tangsha]
            //end:add by wal@xdja.com for 1739
        }

        //如果所有的人员都没有邀请者ID，说明数据错误，直接返回
        if(groupIdMap.isEmpty()){
            return;
        }
        for (Map.Entry<String,String> entry : groupIdMap.entrySet()) {
            String key = entry.getKey();
            String [] keys = key.split("_");
            String inviteAccountId = entry.getValue();
            String textStr = "";
            if (account.equals(inviteAccountId)) {
                textStr = getResString(R.string.groupUpdate_show_self);//你
            } else {
                ContactDto inviteAccount = GroupProxy.getGroupMemberInfo(context, keys[0], inviteAccountId);
                if (inviteAccount == null) {
                    return;
                }
                textStr = inviteAccount.getName();//邀请者显示名称
            }
            textStr += getResString(R.string.groupUpdate_invite);//+ 邀请

            int memberCount = 0;//成员数量（不算群主）
            for (GroupMember groupMember : groupMembers) {
                    //[s]modify by xienana for bug 5649 [review by tangsha]
                if (groupMember.getInviteAccount().equals(inviteAccountId) && !groupMember.getAccount().equals(inviteAccountId) && groupMember.getGroupId().equals(keys[0])
                       && groupMember.getCreateTime().equals(keys[2])) {
                    //[e]modify by xienana for bug 5649 [review by tangsha]
                    if (memberCount != 0) {
                        textStr += getResString(R.string.groupUpdate_separate);
                    }
                    if (groupMember.getAccount().equals(account)) {
                        textStr += getResString(R.string.groupUpdate_show_self);
                    } else {
                        ContactDto contactDto = ContactModuleProxy.getContactInfo(groupMember.getAccount());
                        if (contactDto != null) {
                            textStr += contactDto.getName();
                        }else {
                            textStr += groupMember.getAccount();
                        }
                    }

                    memberCount++;
                }

            }

            //如果有此人邀请的人
            if (memberCount > 0) {
                textStr += getResString(R.string.groupUpdate_add);
                LogUtil.getUtils().w("GroupEventProduce groupMemberAdded "+keys[0]+" textStr "+textStr);
                SendGroupSystemMessageEvent(keys[0], textStr);
            }
        }

    }

    /**
     *群成员添加时出现满群，或者账号不存在，导致不能入群 by wal@xdja.com
     *  @param outRangeNames
     *  @param notAccountNames
     */
    protected void  groupMemberAddedFail(Collection<String> outRangeNames,Collection<String> notAccountNames,String groupId){
        if (outRangeNames == null || notAccountNames == null || ObjectUtil.stringIsEmpty(groupId)) {
            return;
        }

        String account = AccountServer.getAccount().getAccount();
        if(TextUtils.isEmpty(account)){
            return;
        }

        if (outRangeNames.size()>0){
            StringBuffer rangeNamesString = new StringBuffer();
            int nameCount = 0;
            for (String rangeName : outRangeNames){
                if (nameCount != 0) {
                    rangeNamesString.append("、");
                }
                rangeNamesString.append(rangeName);
                nameCount++;
            }
            if (nameCount > 0) {
                rangeNamesString.append(getResString(R.string.group_member_out_range_fail_add));
                SendGroupSystemMessageEvent(groupId, rangeNamesString.toString());
            }
        }

        if (!notAccountNames.isEmpty()){
            StringBuffer notAccountNameString = new StringBuffer();
            int notAccountNameCount = 0;
            for (String notAccountName : notAccountNames){
                if (notAccountNameCount != 0) {
                    notAccountNameString.append("、");
                }
                notAccountNameString.append(notAccountName);
                notAccountNameCount++;
            }
            if (notAccountNameCount > 0) {
                notAccountNameString.append(getResString(R.string.group_member_not_account_fail_add));
                SendGroupSystemMessageEvent(groupId, notAccountNameString.toString());
            }
        }

    }

    /**
     *群成员添加时出现未关联设备账号，提示不能入群 by wal@xdja.com
     *  @param noSecNames
     */
    protected void  groupNosecMemberAddedFail(Collection<String> noSecNames,String groupId){
        if (noSecNames == null || ObjectUtil.stringIsEmpty(groupId)) {
            return;
        }

        String account = AccountServer.getAccount().getAccount();
        if(TextUtils.isEmpty(account)){
            return;
        }

        if (!noSecNames.isEmpty()){
            StringBuffer noSecNamesString = new StringBuffer();
            int nameCount = 0;
            for (String noSecName : noSecNames){
                if (nameCount != 0) {
                    noSecNamesString.append("、");
                }
                noSecNamesString.append(noSecName);
                nameCount++;
            }
            if (nameCount > 0) {
                noSecNamesString.append(getResString(R.string.group_member_no_sec_fail_add));
                SendGroupSystemMessageEvent(groupId, noSecNamesString.toString());
            }
        }
    }
    /**
     *群成员添加时出现已是群组成员，提示不能入群 by wal@xdja.com for 4116
     *  @param inGroupNames
     */
    protected void  groupMemberInGroupAddedFail(Collection<String> inGroupNames,String groupId){
        if (inGroupNames == null || ObjectUtil.stringIsEmpty(groupId)) {
            return;
        }

        String account = AccountServer.getAccount().getAccount();
        if(TextUtils.isEmpty(account)){
            return;
        }

        if (!inGroupNames.isEmpty()){
            StringBuffer inGroupNamesString = new StringBuffer();
            int nameCount = 0;
            for (String inGroupName : inGroupNames){
                if (nameCount != 0) {
                    inGroupNamesString.append("、");
                }
                inGroupNamesString.append(inGroupName);
                nameCount++;
            }
            if (nameCount > 0) {
                inGroupNamesString.append(getResString(R.string.group_member_in_group_fail_add));
                SendGroupSystemMessageEvent(groupId, inGroupNamesString.toString());
            }
        }
    }
    /**
     * 群组成员修改
     *
     * @param groupMembers
     */
    protected void groupMemberUpdated(Collection<GroupMember> groupMembers) {
        if (groupMembers == null) {
            return;
        }

        ArrayList<String> groupIds = new ArrayList<>();
        for (GroupMember member : groupMembers) {
            if (!groupIds.contains(member.getGroupId())) {
                groupIds.add(member.getGroupId());
                SendGroupUpdateEvent(member.getGroupId());
            }
        }
    }

    /**
     * 群组头像修改
     *
     * @param groupId   群组ID
     * @param avatarUrl 头像Url
     */
    protected void groupAvatarChanged(String groupId, String avatarUrl) {
        if (TextUtils.isEmpty(groupId) || TextUtils.isEmpty(avatarUrl)) {
            return;
        }

        //通知会话列表进行刷新
        SendGroupUpdateEvent(groupId);
    }

    /**
     * 群组名称信息发生变更
     *
     * @param groupId 群组ID
     * @param newName 新群组名称
     */
    protected void groupNameChanged(String groupId, String newName) {
        if (groupId == null) {
            return;
        }

        Group group = GroupInternalService.getInstance().queryByGroupId(groupId);
        if (group == null) {
            return;
        }

        String account = AccountServer.getAccount().getAccount();
        if(TextUtils.isEmpty(account)){
            return;
        }
        //add by lwl start 2087
        if(group.getGroupName()==null||group.getGroupName().equals(newName)){
            return;
        }
        //add by lwl end 2087

        String textStr = "";
        if (account.equals(group.getGroupOwner())) {
            textStr += getResString(R.string.groupUpdate_show_self);
        } else {
            ContactDto contactDto = ContactModuleProxy.getGroupMemberInfo(group.getGroupId(), group.getGroupOwner());
            if (contactDto != null) {
                textStr += contactDto.getName();
            } else {
                return;
            }
        }
        textStr += getResString(R.string.groupUpdate_updateName);
        textStr += getResString(R.string.groupUpdate_doubleQuote_before);
        textStr += newName;
        textStr += getResString(R.string.groupUpdate_doubleQuote_end);


        SendGroupSystemMessageEvent(group.getGroupId(), textStr);

        //通知会话列表进行刷新
        SendGroupUpdateEvent(groupId);
    }


    /**
     * 有成员退群
     *
     * @param groupId   群组ID
     * @param account   成员ID
     * @param eventType 成员退出类型
     *                  IContactEvent.QUIT 群成员主动退出</b>
     *                  IContactEvent.DISMISS 群主解散群</b>
     *                  REMOVED 群成员被移除</b>
     */
    protected void groupQuit(String groupId, String account, int eventType) {
        if (TextUtils.isEmpty(groupId) || TextUtils.isEmpty(account)) {
            return;
        }

        switch (eventType) {
            case IContactEvent.QUIT:
                groupMemberQuit(groupId, account);
                break;
            case IContactEvent.DISMISS:
                groupOwnerDismissGroup(groupId, account);
                break;
            case IContactEvent.REMOVED:
                groupOwnerRemoveMember(groupId, account);
                break;
        }
    }
    /**
     * 群成员主动退群
     *
     * @param groupId
     * @param account
     */
    private void groupMemberQuit(final String groupId, final String account) {
        String loginAccount = AccountServer.getAccount().getAccount();
        if(TextUtils.isEmpty(loginAccount)){
            return;
        }
        //如果是自己退出群组，直接删除会话
        if (loginAccount.equals(account)) {
           runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   ContactProxyEvent.QuitGroupNeedClearMessageEvent clearMessageEvent = new ContactProxyEvent.QuitGroupNeedClearMessageEvent(groupId);
                   BusProvider.getMainProvider().post(clearMessageEvent);
               }
           });
            return;
        }

        ContactDto groupOwner = ContactModuleProxy.getGroupMemberInfo(groupId, account);
        if (groupOwner == null) {
            return;
        }

        String textStr = groupOwner.getName();
        textStr += getResString(R.string.groupUpdate_groupMember_quit);

        SendGroupSystemMessageEvent(groupId,textStr);
    }


    /**
     * 群主解散群组
     *
     * @param groupId
     * @param account
     */
    private void groupOwnerDismissGroup(final String groupId, final String account) {
        ContactDto groupOwner = ContactModuleProxy.getGroupMemberInfo(groupId, account);
        if (groupOwner == null) {
            return;
        }
        String loginAccount = AccountServer.getAccount().getAccount();
        if(TextUtils.isEmpty(loginAccount)){
            return;
        }
        //如果是自己退出群组，直接删除会话
        if (loginAccount.equals(account)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ContactProxyEvent.QuitGroupNeedClearMessageEvent clearMessageEvent = new ContactProxyEvent.QuitGroupNeedClearMessageEvent(groupId);
                    BusProvider.getMainProvider().post(clearMessageEvent);
                }
            });
            return;
        }


        String textStr = groupOwner.getName();
        textStr += getResString(R.string.groupUpdate_dissolve);

        SendGroupSystemMessageEvent(groupId,textStr);
    }


    /**
     * 群主将成员踢出群组
     *
     * @param groupId
     * @param account
     */
    private void groupOwnerRemoveMember(String groupId, String account) {
        Group group = GroupInternalService.getInstance().queryByGroupId(groupId);
        if (group == null) {
            LogUtil.getUtils().e("GroupEventProduce groupOwnerRemoveMember group == null, groupId "+groupId+" account "+account);
            return;
        }

        String loginAccount = AccountServer.getAccount().getAccount();
        if(TextUtils.isEmpty(loginAccount)){
            LogUtil.getUtils().e("GroupEventProduce groupOwnerRemoveMember loginAccount empty, groupId "+groupId+" account "+account);
            return;
        }

        ContactDto groupOwner = ContactModuleProxy.getGroupMemberInfo(groupId, group.getGroupOwner());
        if (groupOwner == null) {
            LogUtil.getUtils().e("GroupEventProduce groupOwnerRemoveMember groupOwner empty, groupId "+groupId+" account "+account);
            return;
        }

        ContactDto groupMember = ContactModuleProxy.getGroupMemberInfo(groupId, account);
        if (groupMember == null) {
            LogUtil.getUtils().e("GroupEventProduce groupOwnerRemoveMember groupMember empty, groupId "+groupId+" account "+account);
            return;
        }

        String textStr = "";

        //如果自己是群组
        if (loginAccount.equals(group.getGroupOwner())) {
            textStr += getResString(R.string.groupUpdate_show_self);
        } else {
            textStr += groupOwner.getName();
        }

        textStr += getResString(R.string.groupUpdate_initiative_delete);

        //如果是自己被移出群组
        if (loginAccount.equals(account)) {
            textStr += getResString(R.string.groupUpdate_show_self);
        } else {
            textStr += groupMember.getName();
        }

        textStr += getResString(R.string.groupUpdate_move);

        SendGroupSystemMessageEvent(groupId, textStr);
    }

    private String getResString(int resourceId) {
        Resources resources = context.getResources();
        return resources.getString(resourceId);
    }

    /**
     * 发送生成群组通知消息通知事件
     * @param groupId 群ID
     * @param textStr 消息内容
     */
    private void SendGroupSystemMessageEvent(final String groupId,final String textStr){
        //分发事件
        runOnUiThread(new Runnable() {
             @Override
             public void run() {
                ContactProxyEvent.GroupSystemMessageEvent createGroupEvent = new ContactProxyEvent.GroupSystemMessageEvent();
                createGroupEvent.setGroupId(groupId);
                createGroupEvent.setMessageStr(textStr);
                BusProvider.getMainProvider().post(createGroupEvent);
                LogUtil.getUtils().e("GroupEventProduce SendGroupSystemMessageEvent:groupid:"
                     + createGroupEvent.getGroupId() + ";Message:" + createGroupEvent.getMessageStr());
             }
         });

    }

    /**
     * 发送群组信息变更事件
     * @param groupId 群组
     */
    private void SendGroupUpdateEvent(final String groupId){
        //清除联系人/群组缓存数据
        ContactCache.getInstance().clearCache();
        runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ContactProxyEvent.GroupUpdateEvent groupUpdateEvent = new ContactProxyEvent.GroupUpdateEvent(groupId);
                    BusProvider.getMainProvider().post(groupUpdateEvent);
                }
            });
    }

    private void runOnUiThread(Runnable runnable){
        Activity activity = ActivityStack.getInstanse().getTopActivity();
        if(activity != null) {
            activity.runOnUiThread(runnable);
        }else{
            LogUtil.getUtils().e("runOnUiThread activity is null!!!");
        }
    }
}
