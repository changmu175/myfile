package com.xdja.contact.http.wrap.params.account;

import com.xdja.comm.https.Property.HttpMethod;
import com.xdja.contact.http.wrap.AbstractHttpParams;

/**
 * Created by wanghao on 2015/7/28.
 * 增量更新账户信息数据参数体
 */
public class AccountIncrementalParam extends AbstractHttpParams {

    /**
     *
     * @param lastUpdateId 最后更新标识
     * @param batchCount 更新数量
     */
    public AccountIncrementalParam(String lastUpdateId, String batchCount) {
        super(lastUpdateId,batchCount);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    public String getPath() {
        return String.format("/v1/accounts/increment?lastUpdateId=%1$s&batchSize=%2$s", args[0], args[1]);
    }

    @Override
    public String getRootPath() {
        return ACCOUNT_URL;
    }
}
