package com.xdja.contact.http.wrap.params.group;

import com.xdja.comm.https.Property.HttpMethod;
import com.xdja.contact.callback.IModuleHttpCallBack;
import com.xdja.contact.http.request.RequestBody;
import com.xdja.contact.http.wrap.AbstractHttpParams;

/**
 *
 * 创建群组时调用请求参数体
 */
public class CreateGroupParams extends AbstractHttpParams {
    /**
     * @param request  请求参数的body
     * @param callBack
     * @param args     url?params=args
     */
    public CreateGroupParams(RequestBody request, IModuleHttpCallBack callBack, String... args) {
        super(request, callBack, args);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }

    @Override
    public String getPath() {
        return "/v1/groups";
    }

    @Override
    public String getRootPath() {
        return FRIEND_URL;
    }
}
