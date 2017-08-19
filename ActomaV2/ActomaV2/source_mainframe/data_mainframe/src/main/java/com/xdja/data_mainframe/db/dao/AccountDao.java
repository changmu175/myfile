package com.xdja.data_mainframe.db.dao;

import android.content.Context;

import com.xdja.comm_mainframe.annotations.AppScope;
import com.xdja.data_mainframe.db.bean.AccountTable;
import com.xdja.data_mainframe.db.bean.CompanyTable;
import com.xdja.data_mainframe.db.bean.MailTable;
import com.xdja.data_mainframe.db.bean.MobileTable;
import com.xdja.dependence.annotations.ContextSpe;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.dependence.uitls.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by ldy on 16/5/3.
 * 对{@link AccountDao}的调用者而言，其只需要使用{@link AccountTable}来从{@link AccountDao}里增删改查，
 * 这中间会涉及到对账号表的子表的操作。
 */
@AppScope
public class AccountDao extends RealmBaseDao<AccountTable, String> {
    private static final String ACCOUNT = "account";
    private Subscriber<? super Void> mSubscriber;

    @Inject
    public AccountDao(@ContextSpe(DiConfig.CONTEXT_SCOPE_APP)
                      Context aContext) {
        super(aContext);
    }

    /**
     * 索引相同则更新,否则创建
     *
     * @param entity 一个或多个bean对象，对{@link AccountTable}里附属的子表的account属性赋值
     */
    @Override
    public Observable<Void> createOrUpdate(final AccountTable... entity) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                if (entity==null){
                    subscriber.onError(new NullPointerException("entity不能为空"));
                    return;
                }
                Realm realm = generateRealm();
                try {
                    realm.beginTransaction();
                    //为三个子表赋account值
                    for (AccountTable accountTable:entity){
                        if (accountTable.getMobiles()!=null){
                            for (MobileTable mobileTable:accountTable.getMobiles()){
                                mobileTable.setAccount(accountTable.getAccount());
                            }
                        }
                        if (accountTable.getCompanies()!=null){
                            for (CompanyTable companyTable:accountTable.getCompanies()){
                                companyTable.setAccount(accountTable.getAccount());
                            }
                        }
                        if (accountTable.getMails()!=null){
                            for (MailTable mailTable:accountTable.getMails()){
                                mailTable.setAccount(accountTable.getAccount());
                            }
                        }
                    }
                    removeChildrenTable(realm,entity);
                    realm.copyToRealmOrUpdate(Arrays.asList(entity));
                    realm.commitTransaction();
                    subscriber.onNext(null);
                    databaseChange();
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                } finally {
                    closeRealm(realm);
                }
            }
        });
    }

    /**
     * 移除记录（指定ID集）
     *
     * @param ids 可以有多个
     */
    @Override
    public Observable<Void> remove(final String... ids) {

        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                Realm realm = generateRealm();
                try {
                    RealmResults<AccountTable> results = queryAccounts(realm, ids);
                    realm.beginTransaction();
                    removeChildrenTable(realm,ids);
                    results.deleteAllFromRealm();
                    realm.commitTransaction();
                    subscriber.onNext(null);
                    databaseChange();
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                } finally {
                    closeRealm(realm);
                }
            }
        });
    }

    /**
     * 按ID查询对象
     *
     * @param ids id
     * @return 查询结果
     */
    @Override
    public Observable<List<AccountTable>> find(final String... ids) {
        return Observable.create(new Observable.OnSubscribe<List<AccountTable>>() {
            @Override
            public void call(Subscriber<? super List<AccountTable>> subscriber) {
                final Realm realm = generateRealm();
                try {
                    subscriber.onNext(results2List(queryAccounts(realm,ids)));
                    subscriber.onCompleted();
                }catch (Exception e){
                    subscriber.onError(e);
                }finally {
                    closeRealm(realm);
                }

            }
        });
    }

    /**
     * 获取所有数据
     */
    @Override
    public Observable<List<AccountTable>> findAll() {

        return Observable.create(new Observable.OnSubscribe<List<AccountTable>>() {
            @Override
            public void call(Subscriber<? super List<AccountTable>> subscriber) {
                final Realm realm = generateRealm();
                try {
                    subscriber.onNext(results2List(realm.where(AccountTable.class).findAll()));
                    subscriber.onCompleted();
                }catch (Exception e){
                    subscriber.onError(e);
                }finally {
                    closeRealm(realm);
                }

            }
        });
    }

    private List<AccountTable> results2List(RealmResults<AccountTable> results){
        List<AccountTable> list = new ArrayList<>();
        if (results==null){
            return list;
        }
        for (AccountTable accountTable:results){
            //realm查询产生的结果实际上是一个AccountTable的代理类，当我们外面直接使用这个代理类调用setter方法时，实际上是直接操作数据库，要避免这种情况
            //所以新建了一个AccountTable
            AccountTable newAccountTable = new AccountTable(accountTable);


            RealmList<MobileTable> mobiles = accountTable.getMobiles();
            if (mobiles !=null){
                RealmList<MobileTable> mobileTables = new RealmList<>();
                for (MobileTable mobileTable: mobiles){
                    mobileTables.add(new MobileTable(mobileTable));
                }
                newAccountTable.setMobiles(mobileTables);
            }
            RealmList<MailTable> mails = accountTable.getMails();
            if (mails !=null){
                RealmList<MailTable> mailTables = new RealmList<>();
                for (MailTable mailTable: mails){
                    mailTables.add(new MailTable(mailTable));
                }
                newAccountTable.setMails(mailTables);
            }
            RealmList<CompanyTable> companies = accountTable.getCompanies();
            if (companies !=null){
                RealmList<CompanyTable> companyTables = new RealmList<>();
                for (CompanyTable companyTable: companies){
                    companyTables.add(new CompanyTable(companyTable));
                }
                newAccountTable.setCompanies(companyTables);
            }

            list.add(newAccountTable);
        }
        return list;
    }

    /**
     * 清除所有数据
     */
    @Override
    public Observable<Void> clear() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                Realm realm = generateRealm();
                try {
                    RealmResults<AccountTable> results = realm.where(AccountTable.class).findAll();
                    realm.beginTransaction();
                    removeChildrenTable(realm,results);
                    results.deleteAllFromRealm();
                    realm.commitTransaction();
                    subscriber.onNext(null);
                    databaseChange();
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                } finally {
                    closeRealm(realm);
                }
            }
        });
    }

    private void databaseChange() {
        if (mSubscriber != null && !mSubscriber.isUnsubscribed()) {
            mSubscriber.onNext(null);
        }
    }

    public Observable<Void> registerChangeListener() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(final Subscriber<? super Void> subscriber) {
                if (mSubscriber != null) {
                    LogUtil.getUtils().w("已经注册过监听器了,现在将覆盖上一个监听器");
                    mSubscriber.onCompleted();
                }
                mSubscriber = subscriber;
            }
        });
    }

    public Observable<Void> releaseChangeListener() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                if (mSubscriber != null && !mSubscriber.isUnsubscribed()) {
                    mSubscriber.onCompleted();
                }
            }
        });
    }

    /**
     * 参考{@link #removeChildrenTable(Realm, String...)}
     */
    private RealmResults<AccountTable> queryAccounts(Realm realm, String... ids) {
        RealmQuery<AccountTable> accountQuery = realm.where(AccountTable.class);
        for (int i = 0, length = ids.length; i < length; i++) {
            accountQuery.equalTo(ACCOUNT, ids[i]);
            if (i < length - 1) {
                accountQuery.or();
            }
        }
        RealmResults<AccountTable> realmResults = accountQuery.findAll();
        return realmResults;
    }

    /**
     * 参考{@link #removeChildrenTable(Realm, String...)}
     */
    private void removeChildrenTable(Realm realm,RealmResults<AccountTable> results){
        String[] ids = new String[results.size()];
        for (int i=0,length=results.size();i<length;i++){
            ids[i] = results.get(i).getAccount();
        }
        removeChildrenTable(realm,ids);
    }

    private void removeChildrenTable(Realm realm,AccountTable... entity){
        String[] ids = new String[entity.length];
        for (int i=0,length=entity.length;i<length;i++){
            ids[i] = entity[i].getAccount();
        }
        removeChildrenTable(realm,ids);
    }

    /**
     * 根据传入的id删除Account表附属的子表，包括{@link MobileTable},{@link MailTable},{@link CompanyTable},
     * 如果只是对Account表操作的话，无法删除掉其子表内容，所以要单独执行删除操作。
     */
    private void removeChildrenTable(Realm realm,String... ids){
        RealmQuery<MobileTable> mobileQuery = realm.where(MobileTable.class);
        RealmQuery<MailTable> mailQuery = realm.where(MailTable.class);
        RealmQuery<CompanyTable> companyQuery = realm.where(CompanyTable.class);
        for (int i = 0, length = ids.length; i < length; i++) {
            mobileQuery.equalTo(ACCOUNT, ids[i]);
            mailQuery.equalTo(ACCOUNT, ids[i]);
            companyQuery.equalTo(ACCOUNT, ids[i]);
            if (i < length - 1) {
                mobileQuery.or();
                mailQuery.or();
                companyQuery.or();
            }
        }
        mobileQuery.findAll().deleteAllFromRealm();
        mailQuery.findAll().deleteAllFromRealm();
        companyQuery.findAll().deleteAllFromRealm();
    }



}
