package com.xdja.imp.util;


import android.database.Cursor;

import java.util.Collection;
import java.util.Map;

public class ObjectUtil<T> {

    public static boolean arrayIsEmpty(String... array) {
        return array == null || array.length <= 0;
    }

    public static boolean stringIsEmpty(String obj) {
        if (obj == null) return true;
        if (obj.length() <= 0) return true;
        return obj.equals("null") || obj.trim().length() == 0 || "".equals(obj);
    }

    public static boolean cursorIsEmpty(Cursor obj) {
        return obj == null;
    }

    public static boolean objectIsEmpty(Object obj) {
        return obj == null;
    }

    public static boolean collectionIsEmpty(Collection<?> collection) {
        return collection == null || collection.size() <= 0;
    }
    public static boolean mapIsEmpty(Map map) {
        return map == null || map.size() <= 0;
    }
}
