package com.xdja.contact.http.wrap.params.account;

import com.xdja.comm.https.Property.HttpMethod;
import com.xdja.contact.callback.IModuleHttpCallBack;
import com.xdja.contact.http.request.RequestBody;
import com.xdja.contact.http.wrap.AbstractHttpParams;

/**
 * Created by tangsha on 2016/7/15.
 */
public class GetCkmsOperSignParam extends AbstractHttpParams {

    public GetCkmsOperSignParam(RequestBody param, IModuleHttpCallBack callBack) {
        super(param,callBack);
    }
    
    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }

    @Override
    public String getPath() {
        return String.format("/v1/ckms/signbase64");
    }

    @Override
    public String getRootPath() {
        return ACCOUNT_URL;
    }
}
