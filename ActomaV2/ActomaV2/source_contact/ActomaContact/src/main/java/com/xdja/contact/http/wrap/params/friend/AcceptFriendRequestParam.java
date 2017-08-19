package com.xdja.contact.http.wrap.params.friend;

import com.xdja.comm.https.Property.HttpMethod;
import com.xdja.contact.callback.IModuleHttpCallBack;
import com.xdja.contact.http.wrap.AbstractHttpParams;

/**
 * Created by wanghao on 2015/7/16.
 * 接受好友请求参数体
 */
public class AcceptFriendRequestParam extends AbstractHttpParams  {

    /**
     * @param callBack
     * @param friendAccount
     */
    public AcceptFriendRequestParam(IModuleHttpCallBack callBack, String friendAccount) {
        super(callBack,friendAccount);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.PUT;
    }

    @Override
    public String getPath() {
        return String.format("/v1/friends/%1$s", args[0]);
    }

    @Override
    public String getRootPath() {
        return FRIEND_URL;
    }
}
