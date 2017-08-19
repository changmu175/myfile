package com.xdja.contact.http.wrap.params.group;

import com.xdja.comm.https.Property.HttpMethod;
import com.xdja.contact.callback.IModuleHttpCallBack;
import com.xdja.contact.http.wrap.AbstractHttpParams;

/**
 * Created by XDJA_XA on 2015/7/21.
 * 群成员增量请求体
 */
public class IncrementGroupMembersParam extends AbstractHttpParams {
    /**
     * @param request  请求参数的body
     * @param callBack
     */
    public IncrementGroupMembersParam(Object request, IModuleHttpCallBack callBack) {
        super(request, callBack);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }

    @Override
    public String getPath() {
        return "/v1/groups/members/generate";
    }

    @Override
    public String getRootPath() {
        return FRIEND_URL;
    }
}
