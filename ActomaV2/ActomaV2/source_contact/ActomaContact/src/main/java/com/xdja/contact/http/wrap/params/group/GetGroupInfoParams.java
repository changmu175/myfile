package com.xdja.contact.http.wrap.params.group;

import com.xdja.comm.https.Property.HttpMethod;
import com.xdja.contact.callback.IModuleHttpCallBack;
import com.xdja.contact.http.wrap.AbstractHttpParams;

/**
 * 获取群组信息请求体
 */
public class GetGroupInfoParams extends AbstractHttpParams {
    /**
     * @param callBack
     * @param args     url?params=args
     */
    public GetGroupInfoParams(IModuleHttpCallBack callBack, String... args) {
        super(null,callBack, args);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    public String getPath() {
        return String.format("/v1/groups/detail/%1$s", args[0]);
    }

    @Override
    public String getRootPath() {
        return FRIEND_URL;
    }
}
