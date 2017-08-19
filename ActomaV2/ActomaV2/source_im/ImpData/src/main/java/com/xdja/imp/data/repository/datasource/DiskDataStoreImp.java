package com.xdja.imp.data.repository.datasource;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xdja.comm.server.ActomaController;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.data.entity.SessionParam;
import com.xdja.imp.data.persistent.DbUtil;
import com.xdja.imp.data.persistent.PreferencesUtil;
import com.xdja.imp.domain.model.KeyValuePair;
import com.xdja.imp_data.R;
import com.xdja.xutils.db.sqlite.WhereBuilder;
import com.xdja.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Summary:数据硬盘存储</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.repository.datasource</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/3</p>
 * <p>Time:17:09</p>
 */
public class DiskDataStoreImp implements DiskDataStore {

    private PreferencesUtil preferencesUtil;

    private DbUtil dbUtil;

    @Inject
    public DiskDataStoreImp(PreferencesUtil preferencesUtil,
                            DbUtil dbUtil) {
        this.preferencesUtil = preferencesUtil;
        this.dbUtil = dbUtil;
    }

    /**
     * @param keyValuePair 待保存的数据
     * @return 保存结果
     */
    public Observable<Boolean> saveKeyValuePairs(@NonNull KeyValuePair<String, String> keyValuePair) {

        if (keyValuePair == null || TextUtils.isEmpty(keyValuePair.getKey())) {
            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_cache_data_null)));
        }

        return Observable.just(keyValuePair)
                .map(
                        new Func1<KeyValuePair<String, String>, Boolean>() {
                            @Override
                            public Boolean call(@NonNull KeyValuePair<String, String> keyValuePair) {
                                LogUtil.getUtils().i("存储数据到本地 --- Key : " + keyValuePair.getKey());
                                LogUtil.getUtils().i("\t\t\tValue : " + keyValuePair.getValue());
                                return preferencesUtil
                                        .setPreferenceStringValue(
                                                keyValuePair.getKey(), keyValuePair.getValue()
                                        );
                            }
                        }
                );
    }

    /**
     * @param keyValuePair 待保存的数据
     * @return 保存结果
     */
    @Override
    public Observable<Boolean> saveIntKeyValuePairs(@NonNull KeyValuePair<String, Integer> keyValuePair) {

        if (keyValuePair == null || TextUtils.isEmpty(keyValuePair.getKey())) {
            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_cache_data_null)));
        }

        return Observable.just(keyValuePair)
                .map(
                        new Func1<KeyValuePair<String, Integer>, Boolean>() {
                            @Override
                            public Boolean call(@NonNull KeyValuePair<String, Integer> keyValuePair) {
                                LogUtil.getUtils().i("存储数据到本地 --- Key : " + keyValuePair.getKey());
                                LogUtil.getUtils().i("\t\t\tValue : " + keyValuePair.getValue());
                                return preferencesUtil
                                        .setPreferenceIntValue(
                                                keyValuePair.getKey(), keyValuePair.getValue()
                                        );
                            }
                        }
                );
    }
    @Override
    public Observable<String> queryStringSharePref(@Nullable String key) {

        if (TextUtils.isEmpty(key)) {
            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_query_key_null)));
        }

        return Observable.just(key)
                .map(
                        new Func1<String, String>() {
                            @Override
                            public String call(String s) {
                                String value = preferencesUtil.gPrefStringValue(s);
                                LogUtil.getUtils().i(
                                        "从SharePreference中查询到缓存数据，Key : "
                                                + s + " ,Value : " + value
                                );
                                return value;
                            }
                        }
                );
    }

    @Override
    public Observable<Integer> queryIntegerSharePref(@Nullable String key) {
        if (TextUtils.isEmpty(key)) {
            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_query_key_null)));
        }

        return Observable.just(key)
                .map(
                        new Func1<String, Integer>() {
                            @Override
                            public Integer call(String s) {
                                int value = preferencesUtil.gPrefIntValue(s);
                                LogUtil.getUtils().i(
                                        "从SharePreference中查询到缓存数据，Key : "
                                                + s + " ,Value : " + value
                                );
                                return value;
                            }
                        }
                );
    }

    @Override
    public Observable<Boolean> saveOrUpdateLocalSingleSession(@NonNull SessionParam sessionParam) {
        if (TextUtils.isEmpty(sessionParam.getFlag())) {
            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_conversation_data_error)));
        }
        return Observable.just(sessionParam)
                .flatMap(
                        new Func1<SessionParam, Observable<Boolean>>() {
                            @Override
                            public Observable<Boolean> call(SessionParam sessionParam) {
                                try {
                                    dbUtil.get().saveOrUpdate(sessionParam);
                                } catch (DbException ex) {
                                    return Observable.error(ex);
                                }
                                return Observable.just(Boolean.TRUE);
                            }
                        }
                );
    }

    @Override
    public Observable<Boolean> saveOrUpdateLocalSession(@NonNull List<SessionParam> sessionParams) {
        return Observable.just(sessionParams)
                .flatMap(new Func1<List<SessionParam>, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(List<SessionParam> sessionParams) {
                        try {
                            dbUtil.get().saveOrUpdateAll(sessionParams);
                        } catch (DbException e) {
                            return Observable.error(e);
                        }
                        return Observable.just(Boolean.TRUE);
                    }
                });
    }


    @Override
    public Observable<List<SessionParam>> getLocalSessionParams() {
        return Observable.just(dbUtil)
                .flatMap(
                        new Func1<DbUtil, Observable<List<SessionParam>>>() {
                            @Override
                            public Observable<List<SessionParam>> call(DbUtil dbUtil) {
                                try {
                                    List<SessionParam> all = dbUtil.get().findAll(SessionParam.class);
                                    //add by licong@xdja.com,fix bug 2309 view by zya@xdja.com,2016/8/2
                                    if(all == null){
                                        all = new ArrayList<>();
                                    }//end
                                    return Observable.just(all);
                                } catch (DbException ex) {
                                    LogUtil.getUtils().e(ex.getMessage());
                                    return Observable.error(ex);
                                }
                            }
                        }
                );
    }

    @Override
    public Observable<SessionParam> getLocalSingleSession(@NonNull final String flag) {
        return Observable.just(dbUtil)
                .flatMap(
                        new Func1<DbUtil, Observable<SessionParam>>() {
                            @Override
                            public Observable<SessionParam> call(DbUtil dbUtil) {
                                try {
                                    SessionParam sp = dbUtil.get()
                                            .findFirst(SessionParam.class,
                                                    WhereBuilder.b("flag", "=", flag));
                                    return Observable.just(sp);
                                } catch (DbException ex) {
                                    LogUtil.getUtils().e(ex.getMessage());
                                    return Observable.error(ex);
                                }
                            }
                        }
                );
    }

    @Override
    public Observable<Boolean> deleteLocalSingleSession(@NonNull SessionParam sessionParam) {
        if (TextUtils.isEmpty(sessionParam.getFlag())) {
            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_conversation_data_error)));
        }
        return Observable.just(sessionParam)
                .flatMap(
                        new Func1<SessionParam, Observable<Boolean>>() {
                            @Override
                            public Observable<Boolean> call(SessionParam sessionParam) {
                                try {
                                    dbUtil.get().delete(sessionParam);
                                } catch (DbException ex) {
                                    return Observable.error(ex);
                                }
                                return Observable.just(Boolean.TRUE);
                            }
                        }
                );
    }

    /**
     * 释放本地存储
     *
     * @return
     */
    @Override
    public Observable<Integer> releaseDiskDataStore() {
        if (null != preferencesUtil) {
            preferencesUtil = null;
        }
        if (null != dbUtil) {
            dbUtil.closeDbutil();
            dbUtil = null;
        }
        return Observable.just(1);
    }
}
