package com.xdja.contact.http.wrap.params.account;

import com.xdja.comm.https.Property.HttpMethod;
import com.xdja.contact.callback.IModuleHttpCallBack;
import com.xdja.contact.http.wrap.AbstractHttpParams;

/**
 * Created by wanghao on 2016/5/19.
 * 1 点击 账户详情界面触发
 * 2 用户进入发送密信界面时触发(不包括详情界面触发的加密消息)​
 * 3 用户在加密电话界面点击拨打加密电话时触发(不包括详情界面触发的加密电话)
 *
 *  请求路径如下:/accountInfo/{account}/{identify}
 */
public class PullAccountInfoParam extends AbstractHttpParams{

    public PullAccountInfoParam(String account,String updateSerial){
        super(account,updateSerial);
    }
   //add by lwl start
    public PullAccountInfoParam(IModuleHttpCallBack callBack, String account,String updateSerial) {
        super(callBack,account,updateSerial);

    }
    //add by lwl end

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    public String getPath() {
        //start:fix 1212 by wal@xdja.com
//        return String.format("/v1/accountInfo/%1$s/%2$s", args[0], ObjectUtil.stringIsEmpty(args[1]) ? "0" : args[1]);
       // return String.format("/v1/accountInfo/%1$s/%2$s", args[0], ObjectUtil.stringIsEmpty(args[1]) ? "1" : args[1]);
        return String.format("/v1/accountInfo/%1$s/%2$s", args[0], 1);//identify  是其他值 拉不下数据
        //end:fix 1212 by wal@xdja.com
    }

    @Override
    public String getRootPath() {
        return ACCOUNT_URL;
    }
}
