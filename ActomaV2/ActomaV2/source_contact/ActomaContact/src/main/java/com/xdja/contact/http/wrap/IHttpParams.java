package com.xdja.contact.http.wrap;

import com.xdja.comm.https.IHttpResult;
import com.xdja.comm.https.Property.HttpMethod;

/**
 * Created by wanghao on 2015/7/15.
 */
public interface IHttpParams {

    HttpMethod getMethod();

    String getUrl();

    String getBody();

    String getTicket();

    IHttpResult getResult();

    String getPath();

    String getDeviceId();

    String getDeviceSn();
}
