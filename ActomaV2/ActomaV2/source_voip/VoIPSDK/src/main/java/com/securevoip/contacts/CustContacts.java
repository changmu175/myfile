package com.securevoip.contacts;

import android.content.Context;
import android.text.TextUtils;

import com.xdja.contactcommon.ContactModuleProxy;

import java.util.List;

/**
 * 供加密电话内部使用的账户接口
 * Created by gbc on 2015/7/25.
 */
public class CustContacts {

    /**
     * 获取联系人的安通头像地址
     * @param account
     * @return
     */
    public static String getFriendPhoto(String account) {
        if (!TextUtils.isEmpty(ContactModuleProxy.getContactInfo(account).getAvatarUrl())) {
            return ContactModuleProxy.getContactInfo(account).getAvatarUrl();
        } else {
            return "";
        }
    }

    /**
     * 获取联系人的安通头像缩略图地址
     * @param account
     * @return
     */
    public static String getFriendThumbNailPhoto(String account) {
        if (!TextUtils.isEmpty(ContactModuleProxy.getContactInfo(account).getAvatarUrl())) {
            return ContactModuleProxy.getContactInfo(account).getThumbnailUrl();
        } else {
            return "";
        }
    }

    /**
     * 根据安通账户获取名称（按照备注 > 集团通讯录联系人名称 > 昵称的优先级返回）
     * @param account
     * @return
     */
    public static String getFriendName(String account) {
        if (!TextUtils.isEmpty(ContactModuleProxy.getContactInfo(account).getName())) {
            return ContactModuleProxy.getContactInfo( account).getName();
        } else {
            return account;
        }
    }

    /**
     * 获得名称全拼
     * @param account
     * @return
     */
    public static String getNicknameFullPY(String account) {
        if (!TextUtils.isEmpty(ContactModuleProxy.getContactInfo(account).getNamePinYin())) {
            return ContactModuleProxy.getContactInfo(account).getNamePinYin();
        } else {
            return "";
        }
    }

    /**
     * 获取名称简拼
     * @param account
     * @return
     */
    public static String getNicknamePY(String account) {
        if (!TextUtils.isEmpty(ContactModuleProxy.getContactInfo(account).getNamePY())) {
            return ContactModuleProxy.getContactInfo(account).getNamePY();
        } else {
            return "";
        }
    }

    /**
     * 是否是好友关系
     * @param user
     * @return
     */
    public static boolean isFriend(String user) {
        return ContactModuleProxy.isFriendRelated(user)  ;
    }

    /**
     * 是否是集团通讯里联系人
     * @param account
     * @return
     */
    public static boolean isDepartment( String account) {
        return ContactModuleProxy.isExistDepartment(account);
    }

    /**
     * 获取所有好友的头像地址
     * @param context
     * @return
     */
    public static List<String> getAllThumbNailUrls(Context context) {
        //return ContactModuleProxy.getAllThumbNailUrls(context);
        return ContactModuleProxy.getAllAvatarUrls(context);
    }

}
