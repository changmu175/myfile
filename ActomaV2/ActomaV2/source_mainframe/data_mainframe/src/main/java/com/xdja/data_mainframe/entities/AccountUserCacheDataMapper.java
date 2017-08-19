package com.xdja.data_mainframe.entities;

import com.xdja.comm_mainframe.annotations.AppScope;
import com.xdja.data_mainframe.entities.cache.UserCache;
import com.xdja.domain_mainframe.model.Account;

import javax.inject.Inject;

/**
 * Created by ldy on 16/5/13.
 */
@AppScope
public class AccountUserCacheDataMapper {

    @Inject
    public AccountUserCacheDataMapper() {
    }

    /**
     * 会直接将{@link Account}的isOnLine属性置为true
     */
    public Account transform(UserCache userCache) {
        Account account = new Account();
        account.setCompanyCode(userCache.getCompanyCode());
        account.setAlias(userCache.getAlias());
        account.setAccount(userCache.getAccount());
        account.setMobiles(userCache.getMobiles());
        account.setMails(userCache.getMails());
        account.setCompanies(userCache.getCompanies());
        account.setAvatarId(userCache.getAvatarId());
        account.setThumbnailId(userCache.getThumbnailId());
        account.setNickName(userCache.getNickName());
        account.setNickNamePinyin(userCache.getNickNamePinyin());
        account.setNickNamePy(userCache.getNickNamePy());
        account.setOnLine(true);
        return account;
    }
}
