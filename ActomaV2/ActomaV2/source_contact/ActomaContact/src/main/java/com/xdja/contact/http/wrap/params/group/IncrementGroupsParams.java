package com.xdja.contact.http.wrap.params.group;

import com.xdja.comm.https.Property.HttpMethod;
import com.xdja.contact.http.wrap.AbstractHttpParams;

/**
 *
 * 增量获取群组请求体
 *
 */
public class IncrementGroupsParams extends AbstractHttpParams {
    /**
     * @param args     url?params=args
     */
    public IncrementGroupsParams(String... args) {
        super(args);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    public String getPath() {
        return String.format("/v1/groups/new/generate?seq=%1$s", args[0]);
    }

    @Override
    public String getRootPath() {
        return FRIEND_URL;
    }
}
