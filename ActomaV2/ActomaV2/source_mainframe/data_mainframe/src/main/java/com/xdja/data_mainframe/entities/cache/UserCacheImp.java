package com.xdja.data_mainframe.entities.cache;

import android.support.annotation.Nullable;

import com.xdja.domain_mainframe.model.Account;

import java.util.List;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.data_mainframe.entities.cache</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/26</p>
 * <p>Time:19:36</p>
 */
public class UserCacheImp implements UserCache {

    private Account account;

    private String ticket;

    public UserCacheImp(Account account, String ticket) {
        this.account = account;
        this.ticket = ticket;
    }

    @Override
    @Nullable
    public String getAccount() {
        return account.getAccount();
    }

    @Override
    public void setAccount(String account) {
        this.account.setAccount(account);
    }

    @Nullable
    @Override
    public String getTicket() {
        return ticket;
    }

    @Override
    public void setTicket(String ticket){
        this.ticket = ticket;
    }

    @Override
    public void setNickName(String nickName, String nickNamePinYin, String nickNamePy) {
        account.setNickName(nickName);
        account.setNickNamePinyin(nickNamePinYin);
        account.setNickNamePy(nickNamePy);
    }

    @Override
    public void setAvatar(String avatarId, String thumbnailId) {
        account.setAvatarId(avatarId);
        account.setThumbnailId(thumbnailId);
    }

    @Override
    public void setMobiles(List<String> mobiles) {
        account.setMobiles(mobiles);
    }

    @Override
    public String getAlias() {
        return account.getAlias();
    }

    @Override
    public void setAlias(String alias) {
        account.setAlias(alias);
    }

    @Override
    public String getCompanyCode() {
        return account.getCompanyCode();
    }

    @Override
    public void setCompanyCode(String companyCode) {
        account.setCompanyCode(companyCode);
    }

    @Override
    public List<String> getMails() {
        return account.getMails();
    }

    @Override
    public void setMails(List<String> mails) {
        account.setMails(mails);
    }

    @Override
    public List<String> getCompanies() {
        return account.getCompanies();
    }

    @Override
    public void setCompanies(List<String> companies) {
        account.setCompanies(companies);
    }

    @Override
    public String getNickName() {
        return account.getNickName();
    }

    @Override
    public String getNickNamePinyin() {
        return account.getNickNamePinyin();
    }

    @Override
    public String getNickNamePy() {
        return account.getNickNamePy();
    }

    @Override
    public String getAvatarId() {
        return account.getAvatarId();
    }

    @Override
    public String getThumbnailId() {
        return account.getAvatarId();
    }

    @Override
    public List<String> getMobiles() {
        return account.getMobiles();
    }
}
