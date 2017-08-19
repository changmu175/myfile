package com.xdja.contact.http.wrap.params.friend;

import com.xdja.comm.https.Property.HttpMethod;
import com.xdja.contact.callback.IModuleHttpCallBack;
import com.xdja.contact.http.request.RequestBody;
import com.xdja.contact.http.wrap.AbstractHttpParams;

/**
 * Created by wanghao on 2015/7/16.
 * 添加好友发起好友请求参数体
 * serverApi /friendRequests/{friendAccount}
 */
public class AddFriendParam extends AbstractHttpParams {
    /**
     * @param args    url?params=args
     */
    public AddFriendParam(RequestBody param, IModuleHttpCallBack callBack, String... args) {
        super(param,callBack,args);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }

    @Override
    public String getPath() {
        return String.format("/v1/friendRequests/%1$s", args[0]);
    }

    @Override
    public String getRootPath() {
        return FRIEND_URL;
    }
}
