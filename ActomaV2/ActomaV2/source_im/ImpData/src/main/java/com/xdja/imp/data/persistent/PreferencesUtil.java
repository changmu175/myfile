package com.xdja.imp.data.persistent;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xdja.comm.data.AccountBean;
import com.xdja.comm.server.AccountServer;
import com.xdja.imp.data.cache.UserEntity;
import com.xdja.imp.data.di.DiConfig;
import com.xdja.imp.data.di.annotation.Scoped;
import com.xdja.imp.data.di.annotation.UserScope;

import javax.inject.Inject;

@UserScope
public class PreferencesUtil {

    private SharedPreferences sp;
    private Editor sharedEditor;

    @Inject
    @SuppressLint("CommitPrefEdits")
    public PreferencesUtil(@Scoped(DiConfig.CONTEXT_SCOPE_APP) Context aContext) {
//        sp = PreferenceManager.getDefaultSharedPreferences(aContext);
        UserEntity userEntity = new UserEntity();
        String account = userEntity.getAccount();
        if(TextUtils.isEmpty(account)){
            AccountBean accountBean = AccountServer.getAccount();
            if (accountBean != null) {// modified by ycm for lint 2017/02/16
                account = accountBean.getAccount();
            }
        }

        sp = aContext.getSharedPreferences("configuration_" + account, 0);
        sharedEditor = sp.edit();
    }

    //Public setters

    /**
     * Set a preference string value
     *
     * @param key   the preference key to set
     * @param value the value for this key
     */
    public boolean setPreferenceStringValue(String key, String value) {
        if (sharedEditor == null) {
            Editor editor = sp.edit();
            editor.putString(key, value);
            return editor.commit();
        } else {
            sharedEditor.putString(key, value);
            return sharedEditor.commit();
        }
    }

    /**
     * Set a preference boolean value
     *
     * @param key   the preference key to set
     * @param value the value for this key
     */
    public boolean setPreferenceBooleanValue(String key, boolean value) {
        if (sharedEditor == null) {
            Editor editor = sp.edit();
            editor.putBoolean(key, value);
            return editor.commit();
        } else {
            sharedEditor.putBoolean(key, value);
            return sharedEditor.commit();
        }
    }

    /**
     * Set a preference float value
     *
     * @param key   the preference key to set
     * @param value the value for this key
     */
    public boolean setPreferenceFloatValue(String key, float value) {
        if (sharedEditor == null) {
            Editor editor = sp.edit();
            editor.putFloat(key, value);
            return editor.commit();
        } else {
            sharedEditor.putFloat(key, value);
            return sharedEditor.commit();
        }
    }

    /**
     * Set a preference int value
     *
     * @param key   the preference key to set
     * @param value the value for this key
     */
    public boolean setPreferenceIntValue(String key, int value) {
        if (sharedEditor == null) {
            Editor editor = sp.edit();
            editor.putInt(key, value);
            return editor.commit();
        } else {
            sharedEditor.putInt(key, value);
            return sharedEditor.commit();
        }
    }

    //Private static getters
    // For string
    @Nullable
    public String gPrefStringValue(String key) {
        return sp.getString(key, "");
    }

    // For boolean
    public Boolean gPrefBooleanValue(String key,boolean defaultValue) {
       return sp.getBoolean(key, defaultValue);
    }
    // For int
    public int gPrefIntValue(String key) {
        return sp.getInt(key,0);
    }
    // For float
    public float gPrefFloatValue(String key) {
        return sp.getFloat(key,0);
    }
}
