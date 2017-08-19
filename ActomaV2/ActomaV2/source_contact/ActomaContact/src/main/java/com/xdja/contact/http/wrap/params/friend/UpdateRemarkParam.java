package com.xdja.contact.http.wrap.params.friend;

import com.xdja.comm.https.Property.HttpMethod;
import com.xdja.contact.callback.IModuleHttpCallBack;
import com.xdja.contact.http.request.RequestBody;
import com.xdja.contact.http.wrap.AbstractHttpParams;

/**
 * Created by wanghao on 2015/7/16.
 * 为好友设置备注请求体
 */
public class UpdateRemarkParam extends AbstractHttpParams {
    /**
     * @param request 请求参数的body
     * @param args    url?params=args
     */
    public UpdateRemarkParam(RequestBody request, IModuleHttpCallBack callBack, String... args) {
        super(request,callBack,args);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.PUT;
    }

    @Override
    public String getPath() {
        return String.format("/v1/friends/%1$s/remark", args[0]);
    }

    @Override
    public String getRootPath() {
        return FRIEND_URL;
    }
}
