package com.xdja.data_mainframe.entities;

import android.support.annotation.NonNull;

import com.xdja.comm_mainframe.annotations.AppScope;
import com.xdja.data_mainframe.db.bean.AccountTable;
import com.xdja.data_mainframe.db.bean.CompanyTable;
import com.xdja.data_mainframe.db.bean.MailTable;
import com.xdja.data_mainframe.db.bean.MobileTable;
import com.xdja.domain_mainframe.model.Account;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import io.realm.RealmList;

/**
 * Created by ldy on 16/5/4.
 */
@AppScope
public class AccountEntityDataMapper {
    @Inject
    public AccountEntityDataMapper() {
    }

    public Account transform(AccountTable accountEntity) {
        Account account = null;
        if (accountEntity != null) {
            account = new Account();
            account.setAccount(accountEntity.getAccount());
            account.setAlias(accountEntity.getAlias());
            account.setNickName(accountEntity.getNickName());
            account.setNickNamePinyin(accountEntity.getNickNamePinyin());
            account.setNickNamePy(accountEntity.getNickNamePy());
            account.setAvatarId(accountEntity.getAvatarId());
            account.setThumbnailId(accountEntity.getThumbnailId());
            account.setCompanyCode(accountEntity.getCompanyCode());
            account.setOnLine(accountEntity.isOnLine());
            account.setMobiles(getMobiles(accountEntity.getMobiles()));
            account.setMails(getMails(accountEntity.getMails()));
            account.setCompanies(getCompanies(accountEntity.getCompanies()));
        }
        return account;
    }

    public List<Account> transform(Collection<AccountTable> accountEntityCollection){
        List<Account> list = new ArrayList<>();
        if (accountEntityCollection!=null){
            for (AccountTable accountTable:accountEntityCollection){
                list.add(transform(accountTable));
            }
        }
        return list;
    }
    
    public AccountTable transform(Account account){
        AccountTable accountEntity = null;
        if (account!=null){
            accountEntity = new AccountTable();
            accountEntity.setAccount(account.getAccount());
            accountEntity.setAlias(account.getAlias());
            accountEntity.setNickName(account.getNickName());
            accountEntity.setNickNamePinyin(account.getNickNamePinyin());
            accountEntity.setNickNamePy(account.getNickNamePy());
            accountEntity.setAvatarId(account.getAvatarId());
            accountEntity.setThumbnailId(account.getThumbnailId());
            accountEntity.setCompanyCode(account.getCompanyCode());
            accountEntity.setOnLine(account.isOnLine());
            accountEntity.setMobiles(getEntityMobiles(account.getMobiles()));
            accountEntity.setMails(getEntityMails(account.getMails()));
            accountEntity.setCompanies(getEntityCompanies(account.getCompanies()));
        }
        return accountEntity;
    }

    public @NonNull List<String> getMobiles(RealmList<MobileTable> mobileTables){
        List<String> list = new ArrayList<>();
        if (mobileTables!=null){
            for (MobileTable mobileTable:mobileTables){
                list.add(mobileTable.getMobile());
            }
        }
        return list;
    }
    public @NonNull List<String> getMails(RealmList<MailTable> mobileTables){
        List<String> list = new ArrayList<>();
        if (mobileTables!=null){
            for (MailTable mailTable:mobileTables){
                list.add(mailTable.getMail());
            }
        }
        return list;
    }
    public @NonNull List<String> getCompanies(RealmList<CompanyTable> mobileTables){
        List<String> list = new ArrayList<>();
        if (mobileTables!=null){
            for (CompanyTable companyTable:mobileTables){
                list.add(companyTable.getCompany());
            }
        }
        return list;
    }

    public RealmList<MobileTable> getEntityMobiles(List<String> mobiles){
        RealmList<MobileTable> list = new RealmList<>();
        if (mobiles!=null){
            for (String mobile:mobiles){
                list.add(new MobileTable(mobile));
            }
        }
        return list;
    }
    public RealmList<MailTable> getEntityMails(List<String> mails){
        RealmList<MailTable> list = new RealmList<>();
        if (mails!=null){
            for (String mail:mails){
                list.add(new MailTable(mail));
            }
        }
        return list;
    }
    public RealmList<CompanyTable> getEntityCompanies(List<String> companies){
        RealmList<CompanyTable> list = new RealmList<>();
        if (companies!=null){
            for (String company:companies){
                list.add(new CompanyTable(company));
            }
        }
        return list;
    }
}
