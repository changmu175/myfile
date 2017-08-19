package com.xdja.data_mainframe.db.bean;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by ldy on 16/5/3.
 */
public class MailTable extends RealmObject {
    private String mail;
    @Required
    private String account;

    public MailTable() {
    }
    public MailTable(MailTable mailTable){
        mail = mailTable.getMail();
        account = mailTable.getAccount();
    }
    public MailTable(String mail) {
        this.mail = mail;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
