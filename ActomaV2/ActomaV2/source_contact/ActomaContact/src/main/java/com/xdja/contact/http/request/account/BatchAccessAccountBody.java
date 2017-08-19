package com.xdja.contact.http.request.account;

import com.alibaba.fastjson.JSON;
import com.xdja.contact.http.request.RequestBody;

import java.util.Collection;

/**
 * Created by wanghao on 2015/8/15.
 * 批量请求账户信息请求体
 */
@Deprecated
public class BatchAccessAccountBody extends RequestBody {

    private Collection<String> accounts;

    public Collection<String> getAccounts() {
        return accounts;
    }

    public void setAccounts(Collection<String> accounts) {
        this.accounts = accounts;
    }

    @Override
    public String toJsonString() {
        return JSON.toJSONString(getAccounts());
    }
}
