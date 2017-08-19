package com.xdja.contact.util;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;

import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.contact.bean.Friend;
import com.xdja.contact.bean.Group;
import com.xdja.contact.bean.GroupMember;
import com.xdja.contact.http.response.group.ResponseCreateGroup;
import com.xdja.contact.presenter.activity.ChooseContactPresenter;
import com.xdja.contact.service.GroupInternalService;
import com.xdja.contact.usereditor.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XDJA_XA on 2015/7/21.
 */
public class GroupUtils {

    public static String getCurrentAccount(Context context) {
       /* AccountBean currentUser = AccountServer.getAccount(context);
        if (currentUser != null && currentUser.getAccount() != null) {
            return currentUser.getAccount();
        }
        return "";*/
        return ContactUtils.getCurrentAccount();
    }


    /**
     * 判断当前用户是否是某群组的群主
     * @param context 上下文
     * @param groupId 要判断的群组ID
     * @return true or false
     */
    public static boolean isGroupOwner(Context context, String groupId) {
        String loginUserAccount = getCurrentAccount(context);
        return isGroupOwner(context,groupId,loginUserAccount);
    }
    /**
     * 判断用户是否是某群组的群主
     * @param context 上下文
     * @param groupId 要判断的群组ID
     * @param account 要判断的账号
     * @return true or false
     */
    public static boolean isGroupOwner(Context context, String groupId, String account) {
        Group localGroup = GroupInternalService.getInstance().queryByGroupId(groupId);
        if(!ObjectUtil.objectIsEmpty(localGroup)){
            String ownerAccount = localGroup.getGroupOwner();
            if(!ObjectUtil.objectIsEmpty(ownerAccount) && account.equals(ownerAccount)){
                return true;
            }
        }
        return false;
    }



    public static String genDefaultGroupName(Context context, String groupId) {
        StringBuffer defaultName = new StringBuffer();
        List<GroupMember> members = GroupInternalService.getInstance().queryGroupMembers(groupId);
        if(!ObjectUtil.collectionIsEmpty(members)){
            int count = 0;
            for (GroupMember member : members) {
                // 这里暂时使用账号进行拼接
                defaultName.append(member.getAccount());
                if ((++count) > 5 || count >= members.size()) {
                    break;
                } else {
                    defaultName.append("、");
                }
            }
        }
        return defaultName.toString();

    }


    public static Group convertCreateGroupBean(ResponseCreateGroup bean) {
        Group group = new Group(true);
        group.setGroupId(bean.getGroupId() + "");
        //group.setUpdateSerial(bean.getMemberSeq() + "");
        group.setNamePY(bean.getGroupNamePy());
        group.setNameFullPY(bean.getGroupNamePinyin());

        group.setCreateTime(bean.getCreateTime()+"");//add by wal@xdja.com for 1737

        return group;
    }


    public static GroupMember genFromCurrentUser(Context context, String groupId, String createTime) {
        GroupMember member = new GroupMember(true);
        member.setCreateTime(createTime);
        member.setGroupId(groupId);
        member.setUpdateSerial("0");
        member.setAccount(ContactUtils.getCurrentAccount());
        return member;
    }

    public static void launchChooseContactActivity(Context context, String groupId, ArrayList<String> accounts) {
        Intent intent = new Intent(context, ChooseContactPresenter.class);
        intent.putExtra("group_id", groupId);
        intent.putStringArrayListExtra("raw_members", accounts);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void loadAvatarToImgView(CircleImageView avatarIv, Group groupInfo,
                                           @DrawableRes int errorImage) {
        if (groupInfo != null && !ObjectUtil.stringIsEmpty(groupInfo.getThumbnail())) {
            avatarIv.loadImage(groupInfo.getThumbnail(),true,errorImage);
        }else {
            avatarIv.loadImage(errorImage,true);
        }
    }

    public static void loadAvatarToImgView(CircleImageView avatarIv, Friend friend,
                                           @DrawableRes int errorImage) {
        if (friend != null && friend.getAvatar() != null
                && !TextUtils.isEmpty(friend.getAvatar().getThumbnail())) {
            avatarIv.loadImage(friend.getAvatar().getThumbnail(),true,errorImage);
        }else {
            avatarIv.loadImage(errorImage,true);
        }
    }

    public static void loadAvatarToImgView(CircleImageView avatarIv, UserInfo user,
                                           @DrawableRes int errorImage) {
        if (user != null && !TextUtils.isEmpty(user.getAvatar())) {
            avatarIv.loadImage(user.getAvatar(), true, errorImage);
        } else {
            avatarIv.loadImage(errorImage,true);
        }
    }

    public static void loadAvatarToImgView(CircleImageView avatarIv, String imageUrl,
                                           @DrawableRes int errorImage) {
        if(!ObjectUtil.stringIsEmpty(imageUrl)){
            avatarIv.loadImage(imageUrl, true, errorImage);
        }else {
            avatarIv.loadImage(errorImage,true);
        }
    }

    /**
     * 检测是否有emoji表情
     *
     * @param source
     * @return
     */
    public static boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) { // 如果不能匹配,则该字符是Emoji表情
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是Emoji
     *
     * @param codePoint 比较的单个字符
     * @return
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD) || ((codePoint >=
                0x20) && (codePoint <= 0xD7FF)) || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >=
                0x10000) && (codePoint <= 0x10FFFF));
    }

}
