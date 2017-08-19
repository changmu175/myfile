package com.xdja.contact.http.wrap.params.group;

import com.xdja.comm.https.Property.HttpMethod;
import com.xdja.contact.callback.IModuleHttpCallBack;
import com.xdja.contact.http.wrap.AbstractHttpParams;

/**
 * Created by XDJA_XA on 2015/7/21.
 * 解散群组请求体
 */
public class DismissGroupParams extends AbstractHttpParams {
    /**
     * @param callBack
     * @param args     url?params=args
     */
    public DismissGroupParams(IModuleHttpCallBack callBack, String... args) {
        super(null,callBack, args);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.DELETE;
    }

    @Override
    public String getPath() {
        return String.format("/v1/groups/%1$s", args[0]);
    }

    @Override
    public String getRootPath() {
        return FRIEND_URL;
    }
}
