package com.xdja.contact.util;

import android.content.Context;

import com.xdja.comm.server.ActomaController;
import com.xdja.comm.server.PreferencesServer;
import com.xdja.comm.uitl.ObjectUtil;

/**
 * @author hkb.
 * @since 2015/8/3/0003.
 */
public class PreferenceUtils {

    public static int getDeptLastUpdateId(Context mContext) {
        return PreferencesServer.getWrapper(mContext).gPrefIntValue("DeptLastUpdateId"+ContactUtils.getCurrentAccount());
    }

    public static int getPersonLastUpdateId(Context mContext) {
        return PreferencesServer.getWrapper(mContext).gPrefIntValue("PersonLastUpdateId"+ContactUtils.getCurrentAccount());
    }

    public static void savetDeptLastUpdateId(Context mContext,String id){
        savetDeptLastUpdateId(mContext, Integer.valueOf(id));
    }
    public static void savetDeptLastUpdateId(Context mContext,int id){
        PreferencesServer.getWrapper(mContext).setPreferenceIntValue("DeptLastUpdateId"+ContactUtils.getCurrentAccount(), id);
    }
    public static void savePersonLastUpdateId(Context mContext,String id) {
        savePersonLastUpdateId(mContext, Integer.valueOf(id));
    }
    public static void savePersonLastUpdateId(Context mContext,int id) {
        PreferencesServer.getWrapper(mContext).setPreferenceIntValue("PersonLastUpdateId"+ContactUtils.getCurrentAccount(), id);
    }
    //保存账户最后更新id
    public static void saveAccountLastUpdateId(Context context,String lastUpdateId){
        PreferencesServer.getWrapper(context).setPreferenceStringValue("account_last_updateId"+ContactUtils.getCurrentAccount(), lastUpdateId);
    }

    /**
     * 获取最后一个更新账户的标示，如果不存在就设置为0
     * @param context
     * @return
     */
    public static String getLastAccountUpdateId(Context context){
        String lastUpdateId = PreferencesServer.getWrapper(context).gPrefStringValue("account_last_updateId"+ContactUtils.getCurrentAccount());
        if(ObjectUtil.stringIsEmpty(lastUpdateId))lastUpdateId = "0";
        return lastUpdateId;
    }


    public static void setDefaultUpdateId(Context context){
        savetDeptLastUpdateId(context,"0");
        savePersonLastUpdateId(context,"0");
        saveAccountLastUpdateId(context, "0");
    }


    public static final String DEFAULT_GROUP_LIMIT = "groupLimit";
    public static final String DEFAULT_GROUP_MEMBER_LIMIT = "groupMemberLimit";
    public static final String DEFAULT_FRIEND_LIMIT = "friendLimit";
    /* if get contact config from server error ,local init config data.
    so if server config changed ,please replay init local data*/
    public static final int LOCAL_GROUP_LIMIT_DATA = 200;
    public static final int LOCAL_GROUP_MEMBER_LIMIT_DATA = 200;
    public static final int LOCAL_FRIEND_LIMIT_DATA = 1000;

    /**
     * 设置默认的联系人配置数据
     */
    public static void setDefaultConfig(){
        Context context = ActomaController.getApp();
        PreferencesServer.getWrapper(context).setPreferenceIntValue(DEFAULT_GROUP_LIMIT, LOCAL_GROUP_LIMIT_DATA);
        PreferencesServer.getWrapper(context).setPreferenceIntValue(DEFAULT_GROUP_MEMBER_LIMIT, LOCAL_GROUP_MEMBER_LIMIT_DATA);
        PreferencesServer.getWrapper(context).setPreferenceIntValue(DEFAULT_FRIEND_LIMIT, LOCAL_FRIEND_LIMIT_DATA);
    }

    public static void setGroupLimitConfiguration(String value){
        Context context = ActomaController.getApp();
        if(ObjectUtil.stringIsEmpty(value)) {
            PreferencesServer.getWrapper(context).setPreferenceIntValue(DEFAULT_GROUP_LIMIT, LOCAL_GROUP_LIMIT_DATA);
        }else{
            PreferencesServer.getWrapper(context).setPreferenceIntValue(DEFAULT_GROUP_LIMIT,Integer.parseInt(value.trim()));//modify by wal@xdja.com for max group member
        }
    }

    public static void setGroupMemberLimitConfiguration(String value){
        Context context = ActomaController.getApp();
        if(ObjectUtil.stringIsEmpty(value)) {
            PreferencesServer.getWrapper(context).setPreferenceIntValue(DEFAULT_GROUP_MEMBER_LIMIT, LOCAL_GROUP_MEMBER_LIMIT_DATA);
        }else{
            PreferencesServer.getWrapper(context).setPreferenceIntValue(DEFAULT_GROUP_MEMBER_LIMIT, Integer.parseInt(value.trim()));//modify by wal@xdja.com for max group member
        }
    }
    //Start:add by wal@xdja.com for max group member
    public static int getGroupMemberLimitConfiguration(){
        Context context = ActomaController.getApp();
        int number = PreferencesServer.getWrapper(context).gPrefIntValue(DEFAULT_GROUP_MEMBER_LIMIT);
        if (number > 0){
            return number;
        }else{
            return LOCAL_GROUP_MEMBER_LIMIT_DATA;
        }
    }
    //End:add by wal@xdja.com for max group member

    public static void setFriendLimitConfiguration(String value){
        Context context = ActomaController.getApp();
        if(ObjectUtil.stringIsEmpty(value)) {
            PreferencesServer.getWrapper(context).setPreferenceIntValue(DEFAULT_FRIEND_LIMIT, LOCAL_FRIEND_LIMIT_DATA);
        }else{
            PreferencesServer.getWrapper(context).setPreferenceIntValue(DEFAULT_FRIEND_LIMIT,Integer.parseInt(value.trim()));//modify by wal@xdja.com for max group member
        }
    }


}

