package com.xdja.data_mainframe.db.bean;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by ldy on 16/5/3.
 */
public class MobileTable extends RealmObject {
    private String mobile;
    @Required
    private String account;

    public MobileTable() {
    }
    public MobileTable(MobileTable mobileTable){
        mobile = mobileTable.getMobile();
        account = mobileTable.getAccount();
    }
    public MobileTable(String mobile) {
        this.mobile = mobile;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
