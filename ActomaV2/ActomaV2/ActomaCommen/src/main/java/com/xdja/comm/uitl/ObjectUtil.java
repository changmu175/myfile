package com.xdja.comm.uitl;


import android.database.Cursor;

import java.util.Collection;
import java.util.Map;

public class ObjectUtil<T> {

    public static boolean arrayIsEmpty(String... array){
        if(array == null)return true;
        return array.length <= 0;
    }

    public static boolean stringIsEmpty(String obj) {
        if (obj == null) return true;
        if (obj.length() <= 0) return true;
        if (obj.equals("null")) return true;
        if (obj.trim().length() == 0) return true;
        return "".equals(obj);
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
        if (map == null) return true;
        return map.size() <= 0;
    }
}
