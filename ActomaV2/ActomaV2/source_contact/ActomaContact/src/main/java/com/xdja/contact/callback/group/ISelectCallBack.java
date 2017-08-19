package com.xdja.contact.callback.group;

import java.util.Map;

/**
 * Created by XDJA_XA on 2015/7/29.
 *
 * wanghao 2016-03-08 修改
 */
public interface ISelectCallBack {

    void callBackCount(int count);

    void selectedCallback(Map<String, Object> selectedMap);
}
