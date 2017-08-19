package com.xdja.contact.http.wrap;

import com.alibaba.fastjson.JSONObject;
import com.xdja.comm.https.IHttpResult;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.server.PreferencesServer;
import com.xdja.contact.callback.IModuleHttpCallBack;
import com.xdja.contact.http.request.IServerApiPath;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.frame.data.chip.TFCardManager;

/**
 * Created by wanghao on 2015/7/16.
 * 联系人模块请求体针对对应请求的url进行实现
 */

public abstract class AbstractHttpParams implements IHttpParams,IServerApiPath {

    protected String[] args;

    protected Object request;

    protected IModuleHttpCallBack callBack;

    String TAG = "anTong AbstractHttpParams";

    /*Map<String,String> urlMap = new HashMap<String,String>();

    {
        urlMap.put(ACCOUNT_URL,"https://11.12.109.25:9443/account/api");
        urlMap.put(FRIEND_URL,"https://11.12.109.36:8097/contact-web/api");
    }*/

    public AbstractHttpParams(Object request){
        this(request,null);
    }


    /**
     * 针对只需要传递参数不需要回调结果的业务请求进行实现
     */
    public AbstractHttpParams(String... args) {
        this(null,null,args);
    }


    /**
     * 针对普通的获取资源请求需要请求体
     * @param args  url?params=args
     */
    public AbstractHttpParams(IModuleHttpCallBack callBack,String... args) {
        this(null,callBack,args);
    }


    /**
     * @param request 请求参数的body
     * @param args    url?params=args
     */
    public AbstractHttpParams(Object request, IModuleHttpCallBack callBack, String... args) {
        this.request = request;
        this.callBack = callBack;
        this.args = args;
    }


    @Override
    public String getUrl() {
        return PreferencesServer.getWrapper(ActomaController.getApp()).
                gPrefStringValue (getRootPath());
    }

    /**
     * 获取请求体包装的数据参数
     * @return
     */
    @Override
    public String getBody() {
        if (ObjectUtil.objectIsEmpty(request))
            return "";
        //request.toJsonString();
        return JSONObject.toJSONString(request);
    }

    @Override
    public String getTicket() {

        String ticket =  PreferencesServer.getWrapper(ActomaController.getApp()).gPrefStringValue("ticket");
        LogUtil.getUtils().d(TAG+" getTicket "+ticket);
        return ticket;
    }

    /*[S]add by tangsha for CKMS operation sign*/
    @Override
    public String getDeviceId() {
        String id = TFCardManager.getCardId().getResult();
        LogUtil.getUtils().d(TAG+" getDeviceId "+id);
        return id;
    }

    @Override
    public String getDeviceSn() {
        String sn = TFCardManager.getSm2EncCertSnString();
        LogUtil.getUtils().d(TAG+" getDeviceSn "+sn);
        return sn;
    }
    /*[E]add by tangsha for CKMS operation sign*/

    @Override
    public IHttpResult getResult() {
        return callBack;
    }

    @Override
    public abstract String getPath();

}

