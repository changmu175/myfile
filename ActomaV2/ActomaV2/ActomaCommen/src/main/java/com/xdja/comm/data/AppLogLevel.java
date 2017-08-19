package com.xdja.comm.data;

import com.xdja.comm.R;
import com.xdja.comm.server.ActomaController;

/**
 * Created by XURJ on 2015/12/25.
 */
public enum AppLogLevel {
    DEFAULT(0, ActomaController.getApp().getString(R.string.default_type)),
    VERBOSE(2, ActomaController.getApp().getString(R.string.lengthy_log)),
    DEBUG(3, ActomaController.getApp().getString(R.string.debug_log)),
    INFO(4, ActomaController.getApp().getString(R.string.hint_log)),
    WARN(5, ActomaController.getApp().getString(R.string.warning_log)),
    ERROR(6, ActomaController.getApp().getString(R.string.error_log)),
    ASSERT(7, ActomaController.getApp().getString(R.string.assert_log)),
    CRASH(8, ActomaController.getApp().getString(R.string.exception_crash));


    private int key;

    private String description;

    AppLogLevel(int key, String description) {
        this.key = key;
        this.description = description;
    }

    public int getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }


    public static AppLogLevel getMessageType(int key){

        AppLogLevel appLogLevel = DEFAULT;

        for(AppLogLevel type : AppLogLevel.values()){

            if(type.getKey() == key){

                appLogLevel = type;

                break;
            }
        }
        return appLogLevel;
    }
}
