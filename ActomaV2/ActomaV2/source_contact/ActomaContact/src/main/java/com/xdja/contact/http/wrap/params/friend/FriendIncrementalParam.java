package com.xdja.contact.http.wrap.params.friend;

import com.xdja.comm.https.Property.HttpMethod;
import com.xdja.contact.http.wrap.AbstractHttpParams;

/**
 * Created by wanghao on 2015/7/16.
 * 增量获取好友数据参数体
 */
public class FriendIncrementalParam extends AbstractHttpParams {


    /**
     * @param args    url?params=args
     */
    public FriendIncrementalParam(String... args) {
        super(args);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    public String getPath() {
        return String.format("/v1/friends?lastQuerySerial=%1$s", args[0]);
    }


    @Override
    public String getRootPath() {
        return FRIEND_URL;
    }
}
