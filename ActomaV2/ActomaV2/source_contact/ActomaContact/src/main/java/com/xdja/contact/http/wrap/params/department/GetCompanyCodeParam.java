package com.xdja.contact.http.wrap.params.department;

import com.xdja.comm.https.Property.HttpMethod;
import com.xdja.contact.callback.IModuleHttpCallBack;
import com.xdja.contact.http.wrap.AbstractHttpParams;

/**
 * Created by xienana on 2016/12/14.
 */
public class GetCompanyCodeParam extends AbstractHttpParams{

    public GetCompanyCodeParam(String... args){
        super(args);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    public String getPath() {
       return String.format("/v1/contact/eccode/%1$s", args[0]);
    }

    @Override
    public String getRootPath() {
        return FRIEND_URL;
    }
}
