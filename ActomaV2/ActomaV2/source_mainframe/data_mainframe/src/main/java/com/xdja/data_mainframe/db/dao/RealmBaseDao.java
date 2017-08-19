package com.xdja.data_mainframe.db.dao;

import android.content.Context;

import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by ldy on 16/5/3.
 */
public abstract class RealmBaseDao<T, K> implements EntityDao<T, K> {
    protected Map<Thread, Realm> realmCache;

    public RealmBaseDao(Context context) {
        RealmConfiguration config = new RealmConfiguration.Builder(context.getApplicationContext()).build();
        Realm.setDefaultConfiguration(config);
    }

    protected Realm generateRealm() {
        return Realm.getDefaultInstance();
    }

    protected void closeRealm(Realm realm) {
        if (realm != null)
            realm.close();
    }
}
