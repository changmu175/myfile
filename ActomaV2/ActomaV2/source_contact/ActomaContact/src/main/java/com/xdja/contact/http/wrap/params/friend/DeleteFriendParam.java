package com.xdja.contact.http.wrap.params.friend;

import com.xdja.comm.https.Property.HttpMethod;
import com.xdja.contact.callback.IModuleHttpCallBack;
import com.xdja.contact.http.wrap.AbstractHttpParams;

/**
 * Created by wanghao on 2015/7/16.
 * 解除好友关系请求参数体
 */
public class DeleteFriendParam extends AbstractHttpParams {
    /**
     * @param args    url?params=args
     */
    public DeleteFriendParam(IModuleHttpCallBack callBack, String... args) {
        super(callBack , args);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.DELETE;
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
