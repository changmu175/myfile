package com.xdja.dao;

import android.app.Application;
import android.support.annotation.NonNull;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.xdja.data_mainframe.db.bean.AccountTable;
import com.xdja.data_mainframe.db.bean.MobileTable;
import com.xdja.data_mainframe.db.dao.AccountDao;
import com.xdja.data_mainframe.entities.AccountEntityDataMapper;
import com.xdja.data_mainframe.util.LogError;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class AccountDaoTest extends ApplicationTestCase<Application> {
    private AccountDao accountDao;

    public AccountDaoTest() {
        super(Application.class);
    }

    public void testFindAll(){
        AccountDao accountDao = new AccountDao(getContext());
        accountDao.findAll().subscribe(new Action1<List<AccountTable>>() {
            @Override
            public void call(List<AccountTable> accountTables) {
                Log.d("ldy111", accountTables.toString());
            }
        });
    }

    public void testAdd(){
        AccountDao accountDao = new AccountDao(getContext());
        accountDao.clear().subscribe();
        accountDao.findAll().subscribe(new Action1<List<AccountTable>>() {
            @Override
            public void call(List<AccountTable> accountTables) {
                Log.d("ldy111", accountTables.toString());
            }
        });
        accountDao.createOrUpdate(new AccountTable("123")).subscribe();
        accountDao.findAll().subscribe(new Action1<List<AccountTable>>() {
            @Override
            public void call(List<AccountTable> accountTables) {
                Log.d("ldy111", accountTables.toString());
            }
        });
    }

    public void testClear(){
        AccountDao accountDao = new AccountDao(getContext());
        accountDao.clear().subscribe();
    }

    public void testFind(){
        AccountDao accountDao = new AccountDao(getContext());
        accountDao.clear().subscribe();
        accountDao.createOrUpdate(new AccountTable("123")).subscribe();
        accountDao.find("123").subscribe(new Action1<List<AccountTable>>() {
            @Override
            public void call(List<AccountTable> accountTables) {
                Log.d("ldy111", accountTables.toString());
            }
        });
    }

    public void testUpdate(){
        accountDao = new AccountDao(getContext());
        accountDao.clear().subscribe();

        AccountTable accountTable = new AccountTable("123");
        accountTable.setOnLine(true);
        RealmList<MobileTable> mobileTables = new RealmList<MobileTable>();
        mobileTables.add(new MobileTable("111111"));
        accountTable.setMobiles(mobileTables);

        accountDao.createOrUpdate(accountTable).subscribe();
        accountDao.findAll().subscribe(new Action1<List<AccountTable>>() {
            @Override
            public void call(List<AccountTable> accountTables) {
                Log.d("ldy111", accountTables.toString());
            }
        });

//        updateCurrentAccountTableMobile(Arrays.asList("2222")).subscribe();
        AccountTable accountTable1 = new AccountTable("123");
//        RealmList<MobileTable> realmList = new RealmList<>();
//        realmList.add(new MobileTable("1234"));
//        accountTable1.setMobiles(realmList);
        accountDao.createOrUpdate(accountTable1).subscribe();
        accountDao.findAll().subscribe(new Action1<List<AccountTable>>() {
            @Override
            public void call(List<AccountTable> accountTables) {
                Log.d("ldy111", accountTables.toString());
            }
        });
    }

    public void testMobile(){
        accountDao = new AccountDao(getContext());
        Realm realm = Realm.getDefaultInstance();
        Log.i("ldy111:testMobile",realm.where(MobileTable.class).findAll().toString());
    }

    public void testUpdateMobile(){
        accountDao = new AccountDao(getContext());
        accountDao.clear().subscribe();

        AccountTable accountTable = new AccountTable("123");
        accountTable.setOnLine(true);
        RealmList<MobileTable> mobileTables = new RealmList<MobileTable>();
        mobileTables.add(new MobileTable("111111"));
        accountTable.setMobiles(mobileTables);

        accountDao.createOrUpdate(accountTable).subscribe();
        testMobile();

        saveMobile("22222");
        testMobile();
    }

    private void saveMobile(@NonNull String mobile) {
        List<String> mobiles = Collections.singletonList(mobile);
        updateCurrentAccountTableMobile(mobiles).subscribe(new Subscriber<Void>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Void aVoid) {

            }
        });
    }
    public Observable<AccountTable> getCurrentAccountTable() {
        return accountDao.findAll().map(new Func1<List<AccountTable>, AccountTable>() {
            @Override
            public AccountTable call(List<AccountTable> accountTables) {
                for (AccountTable accountTable : accountTables) {
                    if (accountTable.isOnLine())
                        return accountTable;
                }
                return null;
            }
        });
    }
    public Observable<Void> updateCurrentAccountTableMobile(final List<String> mobiles) {
        return getCurrentAccountTable()
                .flatMap(new Func1<AccountTable, Observable<Void>>() {
                    @Override
                    public Observable<Void> call(AccountTable accountTable) {
                        AccountEntityDataMapper accountEntityDataMapper = new AccountEntityDataMapper();
                        accountTable.setMobiles(accountEntityDataMapper.getEntityMobiles(mobiles));
                        Log.d("ldy111:update", accountTable.toString());
                        return createOrUpdateCurrentAccountInfo(accountTable);
                    }
                });
    }

    public Observable<Void> createOrUpdateCurrentAccountInfo(AccountTable account) {
        return accountDao.createOrUpdate(account);
    }

    public void testLogout(){
        createOnlineAccount();
        logoutCurrentAccountTableMobile().subscribe(new LogError());
        printAccount();
    }

    private void printAccount(){
        accountDao = new AccountDao(getContext());
        accountDao.findAll().subscribe(new Action1<List<AccountTable>>() {
            @Override
            public void call(List<AccountTable> accountTables) {
                Log.d("ldy111", accountTables.toString());
            }
        });
    }
    private void createOnlineAccount(){
        accountDao = new AccountDao(getContext());
        accountDao.clear().subscribe();

        AccountTable accountTable = new AccountTable("123");
        accountTable.setOnLine(true);
        RealmList<MobileTable> mobileTables = new RealmList<MobileTable>();
        mobileTables.add(new MobileTable("111111"));
        accountTable.setMobiles(mobileTables);

        accountDao.createOrUpdate(accountTable).subscribe();
    }

    private Observable<Void> logoutCurrentAccountTableMobile() {
        return getCurrentAccountTable()
                .flatMap(new Func1<AccountTable, Observable<Void>>() {
                    @Override
                    public Observable<Void> call(AccountTable accountTable) {
                        accountTable.setOnLine(false);
                        return createOrUpdateCurrentAccountInfo(accountTable);
                    }
                });
    }
}