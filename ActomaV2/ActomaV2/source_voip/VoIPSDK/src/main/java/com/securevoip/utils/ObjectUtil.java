package com.securevoip.utils;


import android.database.Cursor;

import java.util.Collection;

public class ObjectUtil<T> {


    public static boolean stringIsEmpty(String obj) {
        if (obj == null) return true;
        if (obj.length() <= 0) return true;
        return false;
    }

    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    public static boolean cursorIsEmpty(Cursor obj) {
        if (obj == null) return true;
        if (obj.getCount() <= 0) return true;
        return false;
    }

    public static boolean objectIsEmpty(Object obj) {
        if (obj == null) return true;
        return false;
    }

    public static boolean collectionIsEmpty(Collection<?> collection) {
        if (collection == null) return true;
        if (collection.size() <= 0) return true;
        return false;
    }
}
