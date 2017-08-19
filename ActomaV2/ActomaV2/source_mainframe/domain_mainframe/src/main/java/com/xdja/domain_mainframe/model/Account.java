package com.xdja.domain_mainframe.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by ldy on 16/5/4.
 */
public class Account {
    public static final String ACCOUNT = "account";
    public static final String ALIAS = "alias";
    public static final String NICK_NAME = "nickName";
    public static final String NICK_NAME_PY = "nickNamePy";
    public static final String NICK_NAME_PIN_YIN = "nickNamePinyin";
    public static final String MOBILE = "mobile";
    public static final String MAIL = "mail";
    public static final String AVATAR_ID = "avatarId";
    public static final String THUMBNAIL_ID = "thumbnailId";
    public static final String COMPANY_CODE = "companyCode";
    public static final String USER_INFO = "userInfo";
    public static final String TICKET = "ticket";
    public static final String TICKET_CREATE_TIME = "ticketCreateTime";
    public static final String TICKET_VAILD_EXPIRE_TIME = "ticketVaildExpireTime";

    private String account;
    private String alias;
    private String nickName;
    private String nickNamePy;
    private String nickNamePinyin;
    private String avatarId;
    private String thumbnailId;
    private String companyCode;
    private boolean isOnLine;
    private List<String> mobiles;
    private List<String> mails;
    private List<String> companies;

    public Account() {
    }

    public Account(Map<String, Object> stringObjectMap) {
        account = (String) stringObjectMap.get(ACCOUNT);
        alias = (String) stringObjectMap.get(ALIAS);
        nickName = (String) stringObjectMap.get(NICK_NAME);
        nickNamePy = (String) stringObjectMap.get(NICK_NAME_PY);
        nickNamePinyin = (String) stringObjectMap.get(NICK_NAME_PIN_YIN);
        mobiles = Collections.singletonList((String) stringObjectMap.get(MOBILE));
        mails = Collections.singletonList((String) stringObjectMap.get(MAIL));
        avatarId = (String) stringObjectMap.get(AVATAR_ID);
        thumbnailId = (String) stringObjectMap.get(THUMBNAIL_ID);
        companyCode = (String) stringObjectMap.get(COMPANY_CODE);
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

    public String getNickNamePinyin() {
        return nickNamePinyin;
    }

    public void setNickNamePinyin(String nickNamePinyin) {
        this.nickNamePinyin = nickNamePinyin;
    }

    public String getNickNamePy() {
        return nickNamePy;
    }

    public void setNickNamePy(String nickNamePy) {
        this.nickNamePy = nickNamePy;
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

    public List<String> getMobiles() {
        return mobiles;
    }

    public void setMobiles(List<String> mobiles) {
        this.mobiles = mobiles;
    }

    public List<String> getMails() {
        return mails;
    }

    public void setMails(List<String> mails) {
        this.mails = mails;
    }

    public List<String> getCompanies() {
        return companies;
    }

    public void setCompanies(List<String> companies) {
        this.companies = companies;
    }

    @Override
    public String toString() {
        return "Account{" +
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
