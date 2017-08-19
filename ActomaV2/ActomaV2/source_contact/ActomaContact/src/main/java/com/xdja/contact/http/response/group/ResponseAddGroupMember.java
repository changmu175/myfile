package com.xdja.contact.http.response.group;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by wanghao on 2016/3/8.
 *
 */
public class ResponseAddGroupMember implements Serializable {

    private Map<String, List<String>> blockAccounts;

    public ResponseAddGroupMember() {}



    public Map<String, List<String>> getBlockAccounts() {
        return blockAccounts;
    }

    public void setBlockAccounts(Map<String, List<String>> blockAccounts) {
        this.blockAccounts = blockAccounts;
    }
}



