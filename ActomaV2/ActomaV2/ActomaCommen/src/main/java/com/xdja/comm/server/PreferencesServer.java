package com.xdja.comm.server;


import android.content.Context;

import com.xdja.frame.data.persistent.PreferencesUtil;


public class PreferencesServer {

    private static PreferencesServer wrapper;

    private PreferencesUtil preferencesUtil;

    public static PreferencesServer getWrapper(Context cxt) {
        if (wrapper == null)
            wrapper = new PreferencesServer(cxt);
        return wrapper;
    }

    private PreferencesServer(Context aContext) {
        this.preferencesUtil = new PreferencesUtil(aContext);
    }

    //Public setters

    /**
     * Set a preference string value
     *
     * @param key   the preference key to set
     * @param value the value for this key
     */
    public void setPreferenceStringValue(String key, String value) {
        this.preferencesUtil.setPreferenceStringValue(key,value);
    }



    /**
     * Set a preference int value
     *
     * @param key   the preference key to set
     * @param value the value for this key
     */
    public void setPreferenceIntValue(String key, int value) {
       this.preferencesUtil.setPreferenceIntValue(key,value);
    }


    /**
     * Set a preference long value
     *
     * @param key   the preference key to set
     * @param value the value for this key
     */
    public void setPreferenceLongValue(String key, long value) {
        this.preferencesUtil.setPreferenceLongValue(key,value);
    }

    /**
     * Set a preference boolean value
     *
     * @param key   the preference key to set
     * @param value the value for this key
     */
    public void setPreferenceBooleanValue(String key, boolean value) {
        this.preferencesUtil.setPreferenceBooleanValue(key,value);
    }

    //Private static getters
    // For string
    public String gPrefStringValue(String key) {
       return this.preferencesUtil.gPrefStringValue(key);
    }

    // For boolean
    public Boolean gPrefBooleanValue(String key,boolean defaultValue) {
        return this.preferencesUtil.gPrefBooleanValue(key, defaultValue);
    }
    // For int
    public int gPrefIntValue(String key) {
        return this.preferencesUtil.gPrefIntValue(key);
    }

    public long gPrefLongValue(String key) {
        return this.preferencesUtil.gPrefLongValue(key);
    }
	//add by mengbo. 2016-09-27. begin
    public void clearPreference() {
        this.preferencesUtil.clear();
    }
	//add by mengbo. 2016-09-27. end
}
