package com.xdja.contact.http.response.account;

import java.util.List;

/**
 * Created by wanghao on 2015/8/3.
 * 增量更新账户信息
 */
public class ResponseIncrementAccounts {

    private String lastUpdateId;

    private List<ResponseActomaAccount> accounts;

    public String getLastUpdateId() {
        return lastUpdateId;
    }

    public void setLastUpdateId(String lastUpdateId) {
        this.lastUpdateId = lastUpdateId;
    }

    public List<ResponseActomaAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<ResponseActomaAccount> accounts) {
        this.accounts = accounts;
    }
}
