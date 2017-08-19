package com.xdja.contact.http.wrap;

import android.text.TextUtils;

import com.xdja.comm.event.BusProvider;
import com.xdja.comm.event.TicketAuthErrorEvent;
import com.xdja.comm.https.HttpErrorBean;
import com.xdja.comm.https.HttpsRequest;
import com.xdja.comm.https.HttpsRequstResult;
import com.xdja.comm.server.ActomaController;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.callback.IModuleHttpCallBack;

/**
 * Created by wanghao on 2015/7/15.
 */
public class HttpRequestWrap {

    private HttpsRequest httpsRequest;

    /**
     * 所有请求
     *
     * @param params
     */
    public void request(IHttpParams params) {
        request(params,true);
    }

    /**
     * 所有请求
     *
     * @param params
     */
    public void request(IHttpParams params, boolean needPrompt) {
        LogUtil.getUtils().d("thz - request url=" + params.getUrl() + params.getPath());
        LogUtil.getUtils().d("thz - request body=" + params.getBody());
        //add try catch by lwl illegalargument 742
        if(params != null && TextUtils.isEmpty(params.getTicket()) == false) {
            try {
                httpsRequest = new HttpsRequest(ActomaController.getApp(), params.getMethod(), params.getUrl(), params.getPath());
                httpsRequest.setNeedPrompt(needPrompt);
                httpsRequest.receiveExecute(params.getTicket(), params.getBody(), params.getResult());
            } catch (IllegalArgumentException e) {
                HttpErrorBean errorBean = new HttpErrorBean();
                errorBean.setMessage("参数格式错误");
                params.getResult().onFail(errorBean);
            }
        }else{
            LogUtil.getUtils().e("thz - request ticket is empty!!!");
        }
    }

    /*[S]add by tangsha for get ckms create group operation sign*/
    public void requestWithDevice(IHttpParams params){
        LogUtil.getUtils().d("thz - requestWithDevice request url=" + params.getUrl() + params.getPath());
        LogUtil.getUtils().d("thz - requestWithDevice request body=" + params.getBody());
        //add try catch by lwl illegalargument 742
        try{
            httpsRequest = new HttpsRequest(ActomaController.getApp(), params.getMethod(), params.getUrl(), params.getPath());
            httpsRequest.receiveExecuteWithDeviceInfo(params.getTicket(),params.getDeviceId(),params.getDeviceSn(), params.getBody(), params.getResult());
        }catch (IllegalArgumentException e){
            HttpErrorBean errorBean=new HttpErrorBean();
            errorBean.setMessage("参数格式错误");
            params.getResult().onFail(errorBean);
        }
    }
    /*[E]add by tangsha for get ckms create group operation sign*/

    /**
     * 同步请求
     *
     * @param params
     */
    public HttpsRequstResult synchronizedRequest(IHttpParams params) {
        LogUtil.getUtils().d("thz - synchronizedRequest----request url=" + params.getUrl() + params.getPath());
        LogUtil.getUtils().d("thz - synchronizedRequest----request body=" + params.getBody());
        httpsRequest = new HttpsRequest(ActomaController.getApp(), params.getMethod(), params.getUrl(), params.getPath());
        return httpsRequest.receive(params.getTicket(), params.getBody(), new IModuleHttpCallBack() {
            @Override
            public void onFail(HttpErrorBean httpErrorBean) {
            }

            @Override
            public void onSuccess(String s) {
                LogUtil.getUtils().d("lwla - lwla onSuccess");
            }

            @Override
            public void onErr() {

            }
        });
    }

}
