package com.xdja.contact.http.response.configuration;

import com.xdja.contact.http.response.BaseResponse;
import com.xdja.contact.util.PreferenceUtils;

/**
 * Created by wanghao on 2016/5/16.
 * 获取好友和群组对应的配置信息
 */
public class ResponseContactConfig extends BaseResponse {

    private String groupLimit;

    private String groupMemberLimit;

    private String friendLimit;

    public ResponseContactConfig(){}


    public void setContactConfiguration(){
        PreferenceUtils.setGroupLimitConfiguration(groupLimit);
        PreferenceUtils.setGroupMemberLimitConfiguration(groupMemberLimit);
        PreferenceUtils.setFriendLimitConfiguration(friendLimit);

    }

    public String getFriendLimit() {
        return friendLimit;
    }

    public void setFriendLimit(String friendLimit) {
        this.friendLimit = friendLimit;
    }

    public String getGroupLimit() {
        return groupLimit;
    }

    public void setGroupLimit(String groupLimit) {
        this.groupLimit = groupLimit;
    }

    public String getGroupMemberLimit() {
        return groupMemberLimit;
    }

    public void setGroupMemberLimit(String groupMemberLimit) {
        this.groupMemberLimit = groupMemberLimit;
    }
}
