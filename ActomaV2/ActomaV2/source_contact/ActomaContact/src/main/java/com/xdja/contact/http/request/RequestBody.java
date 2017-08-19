package com.xdja.contact.http.request;

/**
 * Created by wanghao on 2015/7/16.
 * 联系人模块所有网络请求出口需要添加参数的基类
 */
public abstract class RequestBody {

   public abstract String toJsonString();

}
