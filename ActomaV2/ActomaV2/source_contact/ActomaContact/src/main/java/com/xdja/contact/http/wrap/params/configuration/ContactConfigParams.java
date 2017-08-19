package com.xdja.contact.http.wrap.params.configuration;

import com.xdja.comm.https.Property.HttpMethod;
import com.xdja.contact.http.wrap.AbstractHttpParams;

/**
 * Created by wanghao on 2016/5/16.
 * 获取好友和群组对应的配置信息
 */
public class ContactConfigParams extends AbstractHttpParams {

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    public String getPath() {
        return "/v1/settings";
    }

    @Override
    public String getRootPath() {
        return FRIEND_URL;
    }
}
