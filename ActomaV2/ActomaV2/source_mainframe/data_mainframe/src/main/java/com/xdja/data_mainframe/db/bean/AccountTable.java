package com.xdja.data_mainframe.db.bean;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by ldy on 16/5/3.
 */
public class AccountTable extends RealmObject{
    @PrimaryKey
    private String account;
    private String alias;
    private String nickName;
    private String nickNamePy;
    private String nickNamePinyin;
    private String avatarId;
    private String thumbnailId;
    private String companyCode;
    private boolean isOnLine;

    private RealmList<MobileTable> mobiles;
    private RealmList<MailTable> mails;
    private RealmList<CompanyTable> companies;

    public AccountTable() {
    }

    public AccountTable(String account) {
        this.account = account;
    }

    public AccountTable(AccountTable accountTable){
        account = accountTable.getAccount();
        alias = accountTable.getAlias();
        nickName = accountTable.getNickName();
        nickNamePy = accountTable.getNickNamePy();
        nickNamePinyin = accountTable.getNickNamePinyin();
        avatarId = accountTable.getAvatarId();
        thumbnailId = accountTable.getThumbnailId();
        companyCode = accountTable.getCompanyCode();
        isOnLine = accountTable.isOnLine();
        mobiles = accountTable.getMobiles();
        mails = accountTable.getMails();
        companies = accountTable.getCompanies();
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNickNamePy() {
        return nickNamePy;
    }

    public void setNickNamePy(String nickNamePy) {
        this.nickNamePy = nickNamePy;
    }

    public String getNickNamePinyin() {
        return nickNamePinyin;
    }

    public void setNickNamePinyin(String nickNamePinyin) {
        this.nickNamePinyin = nickNamePinyin;
    }

    public String getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(String avatarId) {
        this.avatarId = avatarId;
    }

    public String getThumbnailId() {
        return thumbnailId;
    }

    public void setThumbnailId(String thumbnailId) {
        this.thumbnailId = thumbnailId;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public boolean isOnLine() {
        return isOnLine;
    }

    public void setOnLine(boolean onLine) {
        isOnLine = onLine;
    }

    public RealmList<MobileTable> getMobiles() {
        return mobiles;
    }

    public void setMobiles(RealmList<MobileTable> mobiles) {
        this.mobiles = mobiles;
    }

    public RealmList<MailTable> getMails() {
        return mails;
    }

    public void setMails(RealmList<MailTable> mails) {
        this.mails = mails;
    }

    public RealmList<CompanyTable> getCompanies() {
        return companies;
    }

    public void setCompanies(RealmList<CompanyTable> companies) {
        this.companies = companies;
    }

    @Override
    public String toString() {
        return "AccountTable{" +
                "account='" + account + '\'' +
                ", alias='" + alias + '\'' +
                ", nickName='" + nickName + '\'' +
                ", nickNamePy='" + nickNamePy + '\'' +
                ", nickNamePinyin='" + nickNamePinyin + '\'' +
                ", avatarId='" + avatarId + '\'' +
                ", thumbnailId='" + thumbnailId + '\'' +
                ", companyCode='" + companyCode + '\'' +
                ", isOnLine=" + isOnLine +
                ", mobiles=" + mobiles +
                ", mails=" + mails +
                ", companies=" + companies +
                '}';
    }
}
