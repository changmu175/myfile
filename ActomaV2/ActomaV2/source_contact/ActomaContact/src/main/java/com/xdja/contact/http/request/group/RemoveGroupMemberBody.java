package com.xdja.contact.http.request.group;

import com.alibaba.fastjson.JSON;
import com.xdja.contact.http.request.RequestBody;
import com.xdja.comm.uitl.ObjectUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XDJA_XA on 2015/7/23.
 * 2016-02-01 wanghao
 *
 */
public class RemoveGroupMemberBody extends RequestBody {

    public class RemoveMembersInfo {

        private String account;

        public RemoveMembersInfo(String account) {
            this.account = account;
        }

        public String getAccounts() {
            return this.account;
        }

        public void setAccounts(String account) {
            this.account = account;
        }
    }

    private List<RemoveMembersInfo> removeMembersInfos;

    public void addRequestParams(RemoveMembersInfo removeMembersInfo) {
        if (ObjectUtil.collectionIsEmpty(removeMembersInfos)) {
            removeMembersInfos = new ArrayList<>();
        }
        removeMembersInfos.add(removeMembersInfo);
    }

    public List<RemoveMembersInfo> getRemoveMembersInfos() {
        return removeMembersInfos;
    }

    public void setRemoveMembersInfos(List<RemoveMembersInfo> removeMembersInfos) {
        this.removeMembersInfos = removeMembersInfos;
    }

    @Override
    public String toJsonString() {
        return JSON.toJSONString(removeMembersInfos);
    }
}
