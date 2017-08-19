package com.xdja.contact.http.request;

/**
 * Created by wanghao on 2016/4/20.
 * 与服务端对接所有api路径包装
 */
public interface IServerApiPath {

    String ACCOUNT_URL = "accountUrl";

    String FRIEND_URL = "contactUrl";

    String getRootPath();

}
