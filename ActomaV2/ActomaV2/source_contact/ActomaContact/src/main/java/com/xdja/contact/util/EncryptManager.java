package com.xdja.contact.util;

import com.xdja.comm.encrypt.IEncryptUtils;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.contact.bean.Friend;
import com.xdja.contact.bean.Member;
import com.xdja.contact.service.FriendService;
import com.xdja.contact.service.MemberService;

import java.util.List;
import java.util.Map;

/**
 * Created by wanghao on 2016/4/27.
 * 第三方加密相关的动作都在当前对象里面实现
 */
public class EncryptManager {

    /**
     * 过滤好友服务器返回数据，移除加密通道
     *
     * @param deleteFriendList
     */
    public static void closeEncryptionChannel(List<String> deleteFriendList) {
        Map map = IEncryptUtils.queryAccountAppEncryptSwitchStatus();
        if (!ObjectUtil.mapIsEmpty(map) && !ObjectUtil.collectionIsEmpty(deleteFriendList)) {
            String encryptAccount = (String) map.get("destAccount");
            //[S]modify by lixiaolong on 20160927. fix bug 4555. review by wangchao1.
            if (deleteFriendList.contains(encryptAccount)) {
                MemberService memberService = new MemberService();
                Member member = memberService.getMemberByAccount(encryptAccount);
                if (ObjectUtil.objectIsEmpty(member)) {
                    BroadcastManager.sendBroadcastDeleteFriendOrDepartMember(encryptAccount);
                }
            }
            //[E]modify by lixiaolong on 20160927. fix bug 4555. review by wangchao1.
        }
    }

    /**
     * 过滤集团通讯录服务器返回数据，移除加密通道
     *
     * @param delDepartmentMemberList
     */
    public static void closeDepartmentEncryptionChannel(List<String> delDepartmentMemberList) {
        Map map = IEncryptUtils.queryAccountAppEncryptSwitchStatus();
        if (!ObjectUtil.mapIsEmpty(map) && !ObjectUtil.collectionIsEmpty(delDepartmentMemberList)) {
            String encryptAccount = (String) map.get("destAccount");
            if (delDepartmentMemberList.contains(encryptAccount)) {
                FriendService friendService = new FriendService();
                Friend friend = friendService.findById(encryptAccount);
                if (ObjectUtil.objectIsEmpty(friend) || !friend.isShow()) {
                    BroadcastManager.sendBroadcastDeleteFriendOrDepartMember(encryptAccount);
                    BroadcastManager.sendBroadcastDelDeprtMemberCloseTransfer();
                }
            }
        }
    }
}
