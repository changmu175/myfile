package com.xdja.contact.http.wrap.params.group;

import com.xdja.comm.https.Property.HttpMethod;
import com.xdja.contact.callback.IModuleHttpCallBack;
import com.xdja.contact.http.request.RequestBody;
import com.xdja.contact.http.wrap.AbstractHttpParams;

/**
 * Created by XDJA_XA on 2015/7/21.
 * 更新群组内当前用户昵称参数体
 */
public class UpdateNicknameParam extends AbstractHttpParams {
    /**
     * @param request  请求参数的body
     * @param callBack
     * @param args     群组ID
     */
    public UpdateNicknameParam(RequestBody request, IModuleHttpCallBack callBack, String... args) {
        super(request, callBack, args);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.PUT;
    }

    @Override
    public String getPath() {
        return String.format("/v1/groups/%1$s/members/nickname", args[0]);
    }

    @Override
    public String getRootPath() {
        return FRIEND_URL;
    }
}
