package com.xdja.frame.data.persistent;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xdja.dependence.annotations.ContextSpe;
import com.xdja.dependence.annotations.DiConfig;

import javax.inject.Inject;
@SuppressLint("CommitPrefEdits")
public class PreferencesUtil {

    private SharedPreferences sp;
    private Editor sharedEditor;

    @Inject
    public PreferencesUtil(@NonNull
                           @ContextSpe(DiConfig.CONTEXT_SCOPE_APP)
                           Context aContext) {
        sp = PreferenceManager.getDefaultSharedPreferences(aContext);
        sharedEditor = sp.edit();
    }

    public PreferencesUtil(Context context, String prefName) {
        sp = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        sharedEditor = sp.edit();
    }

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

    /**
     * Set a preference int value
     *
     * @param key   the preference key to set
     * @param value the value for this key
     */
    public boolean setPreferenceLongValue(String key, long value) {
        if (sharedEditor == null) {
            Editor editor = sp.edit();
            editor.putLong(key, value);
            return editor.commit();
        } else {
            sharedEditor.putLong(key, value);
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
    public Boolean gPrefBooleanValue(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }

    // For int
    public int gPrefIntValue(String key) {
        return sp.getInt(key, 0);
    }

    // For int
    public long gPrefLongValue(String key) {
        return sp.getLong(key, 0);
    }

    // For float
    public float gPrefFloatValue(String key) {
        return sp.getFloat(key, 0);
    }

    public boolean containsKey(String key) {
        return sp.contains(key);
    }

	//modify by mengbo. 2016-09-27. begin
    public void removeKey(String key) {
        if (sharedEditor == null) {
            sharedEditor = sp.edit();
        }
        sharedEditor.remove(key);
        sharedEditor.apply();
    }

    public void clear() {
        if (sharedEditor == null) {
            sharedEditor = sp.edit();
        }
        sharedEditor.clear();
        sharedEditor.apply();
    }
	//modify by mengbo. 2016-09-27. end
}
