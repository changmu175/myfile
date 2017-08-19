package com.xdja.contact.http.wrap.params.account;

import com.xdja.comm.https.Property.HttpMethod;
import com.xdja.contact.callback.IModuleHttpCallBack;
import com.xdja.contact.http.wrap.AbstractHttpParams;

/**
 * Created by wanghao on 2015/7/16.
 * 扫一扫搜索好友账号添加查询对应的账户信息请求参数体
 */
public class QueryAccountInfoParam extends AbstractHttpParams {

    /**
     * @param args    url?params=args
     */
    public QueryAccountInfoParam(IModuleHttpCallBack callBack, String args) {
        super(callBack,args);
        
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    public String getPath() {
        return String.format("/v1/account/%1$s", args[0]);
    }


    @Override
    public String getRootPath() {
        return ACCOUNT_URL;
    }
}
