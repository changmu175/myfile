package com.xdja.contact.bean.enumerate;

import com.xdja.comm.server.ActomaController;
import com.xdja.contact.R;

/**
 * Created by wanghao on 2015/12/22.
 * 业务处理异常错误码以及描述
 */
public enum ServiceErrorCode {
    //Start:modify by wal@xdja.com for string
//    NOT_AUTHORIZED("not_authorized","ticket验证未通过"),
//
//    REQUEST_PARAMS_NOT_VALID("request_params_not_valid","请求参数非法"),
//
//    REQUEST_PARAMS_ERROR("request_params_error","请求参数格式错误"),
//
//    INTERNAL_SERVER_ERROR("internal_server_error","服务器内部异常"),
//
//    FRIENDS_LIMIT_ERROR("friends_limit","好友数量达到上限"),
//
//    EXCEPTION_HANDLE_ERROR("exception_handle_error","异常处理错误"),
//
//    EXCEPTION_FRIEND_NOT_EXIST("friend_account_not_exists","该联系人帐号无效"),
//
//    EXCEPTION_ALREADY_FRIEND("already_friend","对方已是你的好友"),
//
//    EXCEPTION_NO_USER_FOUND("no_users_found","未找到该用户"),
//
//    EXCEPTION_NOT_FRIEND("not_friend","该用户已不是你的好友"),
//
//    EXCEPTION_NO_FRIEND_REQ("no_friend_req","该好友请求已失效");
//
    NOT_AUTHORIZED("not_authorized", ActomaController.getApp().getString(R.string.contact_service_not_authorized)),

    REQUEST_PARAMS_NOT_VALID("request_params_not_valid",ActomaController.getApp().getString(R.string.contact_service_request_params_not_valid)),

    REQUEST_PARAMS_ERROR("request_params_error",ActomaController.getApp().getString(R.string.contact_service_request_params_error)),

    INTERNAL_SERVER_ERROR("internal_server_error",ActomaController.getApp().getString(R.string.contact_service_internal_server_error)),

    FRIENDS_LIMIT_ERROR("friends_limit",ActomaController.getApp().getString(R.string.friend_friends_max_limit)),//[s]modify by xienana for contact limit @20161205

    EXCEPTION_HANDLE_ERROR("exception_handle_error",ActomaController.getApp().getString(R.string.contact_service_exception_handle_error)),

    EXCEPTION_FRIEND_NOT_EXIST("friend_account_not_exists",ActomaController.getApp().getString(R.string.contact_service_friend_account_not_exists)),

    EXCEPTION_ALREADY_FRIEND("already_friend",ActomaController.getApp().getString(R.string.contact_service_already_friend)),

    EXCEPTION_NO_USER_FOUND("no_users_found",ActomaController.getApp().getString(R.string.contact_service_no_users_found)),

    EXCEPTION_NOT_FRIEND("not_friend",ActomaController.getApp().getString(R.string.contact_service_not_friend)),

    EXCEPTION_NO_FRIEND_REQ("no_friend_req",ActomaController.getApp().getString(R.string.contact_service_no_friend_req)),
	//[s]modify by xienana for contact limit @20161205
    GROUP_MAX_NUM_LIMIT("group_max_num_limit",ActomaController.getApp().getString(R.string.group_max_num_limit)),
    GROUP_MEMBER_MAX_NUM_LIMIT("group_member_max_num_limit",ActomaController.getApp().getString(R.string.group_members_max_num_limit)),

    FRIEND_REQUEST_FRIEND_LIMIT("friend_friends_limit",ActomaController.getApp().getString(R.string.friend_friends_max_limit));
	//[e]modify by xienana for contact limit @20161205
    //End:modify by wal@xdja.com for string

    private String code;

    private String description;

    ServiceErrorCode(String code, String description) {

        this.code = code;

        this.description = description;
    }

    public static boolean isMatch(String key){
        boolean isMatch = false;
        for(ServiceErrorCode errorCode: ServiceErrorCode.values()){
            if(errorCode.getCode().equals(key)){
                isMatch = true;break;
            }
        }
        return isMatch;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
