package com.xdja.contact.http.wrap.params.account;

import com.xdja.comm.https.Property.HttpMethod;
import com.xdja.contact.http.wrap.AbstractHttpParams;

/**
 * Created by wanghao on 2015/8/15.
 * 根据账号批量获取账户信息
 */
public class BatchAccessAccountParam extends AbstractHttpParams {


    public BatchAccessAccountParam(Object request) {
        super(request);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }

    @Override
    public String getPath() {
        return String.format("/v1/account/batch");
    }

    @Override
    public String getRootPath() {
        return ACCOUNT_URL;
    }
}
