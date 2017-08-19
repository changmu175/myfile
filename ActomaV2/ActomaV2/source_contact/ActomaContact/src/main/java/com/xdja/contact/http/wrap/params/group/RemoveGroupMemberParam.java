package com.xdja.contact.http.wrap.params.group;

import com.xdja.comm.https.Property.HttpMethod;
import com.xdja.contact.callback.IModuleHttpCallBack;
import com.xdja.contact.http.wrap.AbstractHttpParams;

/**
 * Created by XDJA_XA on 2015/7/21.
 * 删除群成员请求体参数
 */
public class RemoveGroupMemberParam extends AbstractHttpParams {
    /**
     * @param request  请求参数的body
     * @param callBack
     * @param args     url?params=args
     */
    public RemoveGroupMemberParam(Object request, IModuleHttpCallBack callBack, String... args) {
        super(request, callBack, args);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.PUT;
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
