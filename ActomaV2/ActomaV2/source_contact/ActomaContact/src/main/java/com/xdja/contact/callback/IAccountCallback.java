package com.xdja.contact.callback;

import com.xdja.contact.http.response.account.ResponseActomaAccount;

import java.util.List;

/**
 * Created by wanghao on 2016/2/28.
 * 增量好友  账户增量更新动作
 */
public interface IAccountCallback extends IPullCallback {

    void onAccountSuccess(List<ResponseActomaAccount> accountList);

}
