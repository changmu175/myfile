package com.xdja.contact.http.wrap.params.group;

import com.xdja.comm.https.Property.HttpMethod;
import com.xdja.contact.callback.IModuleHttpCallBack;
import com.xdja.contact.http.wrap.AbstractHttpParams;

/**
 * 群组内添加成员请求参数体
 */
public class AddGroupMembersParams extends AbstractHttpParams {
    /**
     * @param body  请求参数的body
     * @param callBack
     * @param args     url?params=args
     */
    public AddGroupMembersParams(Object body, IModuleHttpCallBack callBack, String... args) {
        super(body, callBack, args);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }

    @Override
    public String getPath() {
        return String.format("/v1/groups/%1$s/members", args[0]);
    }

    @Override
    public String getRootPath() {
        return FRIEND_URL;
    }
}
