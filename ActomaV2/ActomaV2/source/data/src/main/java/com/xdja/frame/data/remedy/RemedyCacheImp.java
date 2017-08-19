package com.xdja.frame.data.remedy;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.xdja.frame.data.persistent.PreferencesUtil;

/**
 * Created by xdja-fanjiandong on 2016/3/22.
 */
public class RemedyCacheImp implements RemedyCache {

    private PreferencesUtil util;
    private Gson gson;

    private final String DEFAULT_VALUE = "1";

    public RemedyCacheImp(PreferencesUtil util, Gson gson) {
        this.util = util;
        this.gson = gson;
    }

    @Override
    public <T> boolean cache(String _id, T t) {

        if (t instanceof Integer) {
            return util.setPreferenceIntValue(_id, ((Integer) t));
        }

        if (t instanceof Boolean) {
            return util.setPreferenceBooleanValue(_id, ((Boolean) t));
        }

        if (t instanceof Float) {
            return util.setPreferenceFloatValue(_id, ((Float) t));
        }

        if (t instanceof Long) {
            return util.setPreferenceLongValue(_id, ((Long) t));
        }

        if (t instanceof String) {
            return util.setPreferenceStringValue(_id, ((String) t));
        }

        return util.setPreferenceStringValue(_id, this.gson.toJson(t));
    }

    @Override
    public boolean simpleCache(String _id) {
        return util.setPreferenceStringValue(_id, DEFAULT_VALUE);
    }

    @Override
    public boolean readSimpleCache(String _id) {
        return util.containsKey(_id);
    }

    @Override
    @Nullable
    public <T> T readCache(String _id, Class<T> cls) {
        if (cls == Integer.class) {
            return ((T) Integer.valueOf(util.gPrefIntValue(_id)));
        }

        if (cls == Boolean.class) {
            return ((T) Boolean.valueOf(util.gPrefBooleanValue(_id, false)));
        }

        if (cls == Float.class) {
            return ((T) Float.valueOf(util.gPrefFloatValue(_id)));
        }

        if (cls == Long.class) {
            return ((T) Long.valueOf(util.gPrefLongValue(_id)));
        }

        if (cls == String.class) {
            return ((T) util.gPrefStringValue(_id));
        }

        String value = util.gPrefStringValue(_id);
        try {
            return gson.fromJson(value, cls);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    @Override
    public void removeCache(String _id) {
         util.removeKey(_id);
    }
}
