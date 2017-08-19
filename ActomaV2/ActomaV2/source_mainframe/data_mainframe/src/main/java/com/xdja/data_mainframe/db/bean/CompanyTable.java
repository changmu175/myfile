package com.xdja.data_mainframe.db.bean;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by ldy on 16/5/3.
 */
public class CompanyTable extends RealmObject {
    private String company;
    @Required
    private String account;
    public CompanyTable() {
    }
    public CompanyTable(CompanyTable companyTable){
        company = companyTable.getCompany();
        account = companyTable.getAccount();
    }

    public CompanyTable(String company) {
        this.company = company;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
