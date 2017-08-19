package com.xdja.comm.uitl;

import java.util.Collection;

/**
 * Created by hkb.
 * 2015/7/11/0011.
 */
public class ListUtils {

    /**
     * 检测List是否为空
     * @param collection
     * @return
     */
    public static boolean isEmpty(Collection collection) {// modified by ycm for lint 2017/02/13
        return collection == null || collection.size() == 0;
    }
}
