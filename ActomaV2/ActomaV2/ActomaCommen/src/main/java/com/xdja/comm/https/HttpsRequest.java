package com.xdja.comm.https;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xdja.comm.circleimageview.XToast;
import com.xdja.comm.contacttask.ITask;
import com.xdja.comm.contacttask.TaskManager;
import com.xdja.comm.event.BusProvider;
import com.xdja.comm.event.TicketAuthErrorEvent;
import com.xdja.comm.https.ErrorCode.Error;
import com.xdja.comm.https.ErrorCode.ErrorMsg;
import com.xdja.comm.https.ErrorCode.StatusCode;
import com.xdja.comm.https.Property.HttpMethod;
import com.xdja.comm.https.Property.HttpResultSate;
import com.xdja.comm.https.Property.SignProperty;
import com.xdja.comm.https.Property.VerfiySign;
import com.xdja.comm.server.ActomaController;
import com.xdja.dependence.uitls.LogUtil;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

import javax.net.ssl.SSLHandshakeException;

/**
 * Created by THZ on 2015/7/8.
 * https请求
 */
@SuppressLint("NewApi")
public class HttpsRequest extends AsyncTask<Object, Void, Object> implements ITask {

    /**
     * ticket
     */
    public static final String TICKET = "ticket";


    /**
     * 网络操作
     */
    private final HttpHelper httpHelper;

    /**
     * 回调
     */
    private IHttpResult result;

    /**
     * 是否验签
     */
    private VerfiySign verySign = VerfiySign.NOSIGN;

    /**
     * 签名的算法
     */
    private SignProperty signProperty = SignProperty.SM3WITHSM2;


    /**
     * 请求方式
     */
    private HttpMethod method;

    /**
     * url扩展路径
     */
    private String urlPath;

    /**
     * 请求的body
     */
    private String body;
    
    /**
     * 上下文
     */
    private Context context;
    
    /**
     * ticket超时的错误码
     */
    private static final String TICKET_NOT_AUTH = "not_authorized";
    
    /**
     * ticket超时的错误状态吗
     */
    private static final int TICKET_NOT_AUTH_401 = 401;

    /**
     * 根据需要是否将toast提示展示出来
     */
    private boolean needPrompt = true;  //add by ysp

    /**
     * 构造方法
     * @param context 上下文
     * @param method 请求的方式 get，post，put，delete等
     * @param url 请求的url
     * @param urlPath url扩展路径
     */
    public HttpsRequest(Context context, HttpMethod method, String url, String urlPath) {
    	this.context = context;
        this.method = method;
        this.urlPath = urlPath;
        httpHelper = new HttpHelper(context, method, url, urlPath);
        needPrompt = true;
    }

    public void setNeedPrompt(boolean prompt){
        needPrompt = prompt;
    }


    /**
     * 发送接收数据
     * @param ticket 票据
     * @param body 请求的消息body
     * @param result 回调
     */
    public HttpsRequstResult receive(String ticket, String body, IHttpResult result) {
        this.body = body;
        this.result = result;
        if (!TextUtils.isEmpty(ticket)) {
            Header header;
            //终端签名证书dn
            header = new BasicHeader(TICKET, ticket);
            httpHelper.addHeader(header);
        }
        HttpsRequstResult requstResult = new HttpsRequstResult();
        XHttpResponse httpResponse = httpHelper.receive(body);
        LogUtil.getUtils().d("HttpsRequest receive---------");
        Object o = doResultResponse(httpResponse);
        if (o instanceof String) {
            result.onSuccess((String)o);
            requstResult.result = HttpResultSate.SUCCESS;
            requstResult.body = (String)o;

        } else if (o instanceof HttpErrorBean) {
            result.onFail((HttpErrorBean)o);
            requstResult.result = HttpResultSate.FAIL;
            requstResult.httpErrorBean = (HttpErrorBean)o;
        }
        return requstResult;
    }



    /**
     * 发送接收数据
     * @param ticket ticket票据
     * @param body 请求的消息body
     * @param result 回调
     */
    public void receiveExecute(String ticket, String body, IHttpResult result) {
        this.result = result;
        this.body = body;
        TaskManager.getInstance().putTask(this);
        if (!TextUtils.isEmpty(ticket)) {
            Header header;
            //终端签名证书dn
            header = new BasicHeader(TICKET, ticket);
            httpHelper.addHeader(header);
        }
        executeOnExecutor(TaskExcutor.getInstance().getExecutor(), body);
    }

     /*[S]add by tangsha for get ckms create group operation sign*/
    /**
     * 发送接收数据
     * @param ticket ticket票据
     * @param deviceId 安全卡卡号
     * @param sn 安全号证书序列号
     * @param body 请求的消息body
     * @param result 回调
     */
    public void receiveExecuteWithDeviceInfo(String ticket,String deviceId, String sn, String body, IHttpResult result) {
        this.result = result;
        this.body = body;
        TaskManager.getInstance().putTask(this);
        if(!TextUtils.isEmpty(deviceId) && !TextUtils.isEmpty(sn)) {
            BasicHeader header = new BasicHeader("cardId", deviceId);
            this.httpHelper.addHeader(header);
            BasicHeader cardSn = new BasicHeader("sn", sn);
            this.httpHelper.addHeader(cardSn);
            BasicHeader headerT = new BasicHeader("ticket", ticket);
            this.httpHelper.addHeader(headerT);
            LogUtil.getUtils().d("anTong HttpsRequest  receiveExecute cardId "+deviceId+" sn "+sn);
        }

        this.executeOnExecutor(TaskExcutor.getInstance().getExecutor(), body);// modified by ycm for lint 2017/02/13
    }
     /*[E]add by tangsha for get ckms create group operation sign*/

    @Override
    protected Object doInBackground(Object... params) {
        Object retObj = null;
        if (params != null) {
            String executeBody = String.valueOf(params[0]);
            body = executeBody;
            XHttpResponse httpResponse = httpHelper.receive(executeBody);
            LogUtil.getUtils().d("HttpsRequest doInBackground---------");
            Object o = doResultResponse(httpResponse);
            retObj = o;
            if (o instanceof HttpErrorBean) {
                HttpErrorBean httpErrorBean = (HttpErrorBean) o;
                checkTicketError(httpErrorBean, !isCancelled(), getTaskId());
            }
        }
        TaskManager.getInstance().removeTask(this);
        return retObj;
    }

    private static void checkTicketError(HttpErrorBean httpErrorBean, boolean send, String tag){
        String errCode = httpErrorBean.getErrCode();
        LogUtil.getUtils().e("HttpsRequest  checkTicketError "+errCode+" send "+send+" tag is "+tag);
        if (send && "0x9008".equals(errCode)) {
            BusProvider.getMainProvider().post(new TicketAuthErrorEvent());
        }
    }

    public static void checkTicketError(HttpsRequstResult requstResult, boolean send, String tag){
        if(requstResult != null && requstResult.result == HttpResultSate.FAIL && requstResult.httpErrorBean != null){
            checkTicketError(requstResult.httpErrorBean,send, tag);
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        LogUtil.getUtils().d("HttpsRequest onPostExecute---------");
        if (o instanceof String) {
            result.onSuccess((String)o);
        } else if (o instanceof HttpErrorBean) {
            HttpErrorBean httpErrorBean = (HttpErrorBean) o;//add by lwl ticket guo qi
            if ("0x9008".equals(httpErrorBean.getErrCode())) {
                result.onErr();
                return;
            }
            if(Error.TIME_ERR.equals(httpErrorBean.getErrCode())//add by lwl
                    || Error.NET_ERROR.equals(httpErrorBean.getErrCode())
                    || Error.IO_ERR.equals(httpErrorBean.getErrCode())){
                if(needPrompt) {
                    XToast.show(ActomaController.getApp(), httpErrorBean.getMessage());
                }
                result.onErr();
                return;
            }
            result.onFail(httpErrorBean);
        }
    }

    /**
     * 处理返回的数据结构
     * @param xHttpResponse 返回的数据结构
     */
    private Object doResultResponse(XHttpResponse xHttpResponse) {
        HttpResponse httpResponse=xHttpResponse.getHttpResponse();
        IOException  ioException=xHttpResponse.getIoException();
        if(ioException!=null){//add by lwl
            if(ioException instanceof SSLHandshakeException){
                return genErrorBean(StatusCode.NET_ERROR, Error.TIME_ERR, ErrorMsg.TIME_ERR);
            }else if(ioException instanceof HttpHostConnectException){
                return genErrorBean(StatusCode.NET_ERROR, Error.NET_ERROR, ErrorMsg.NET_ERROR);
            }else{
                return genErrorBean(StatusCode.NET_ERROR, Error.IO_ERR, ErrorMsg.IO_ERR);
            }
        }
        if (httpResponse == null) {
            return genErrorBean(StatusCode.SERVER_EMPTY, Error.SERVER_EMPTY, ErrorMsg.SERVER_EMPTY);
        } else if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
            String responseBody = getResponceBody(httpResponse);
            LogUtil.getUtils().i("thz  httpsRequest responseBody SC_OK : " + responseBody);
            return responseBody;
        } else {
            String responseBody = getResponceBody(httpResponse);
            LogUtil.getUtils().e("thz  httpsRequest responseBody Error ! "+responseBody
                    +" StatusCode "+httpResponse.getStatusLine().getStatusCode()
                    +" taskId "+getTaskId());
            if (TextUtils.isEmpty(responseBody)) {
                return genErrorBean(StatusCode.SERVER_EMPTY, Error.SERVER_EMPTY, ErrorMsg.SERVER_EMPTY);
            } else {
                try {
                    Gson gson = getGson();
                    ErrorBean errorBean = gson.fromJson(responseBody, ErrorBean.class);
                    if (errorBean == null) {
                        return genErrorBean(StatusCode.SERVER_EMPTY, Error.SERVER_EMPTY, ErrorMsg.SERVER_EMPTY);
                    } else {
                        HttpErrorBean errorHttpBean = new HttpErrorBean();
                        errorHttpBean.setMessage(errorBean.getMessage());
                        errorHttpBean.setErrCode(errorBean.getErrCode());
                        errorHttpBean.setHostId(errorBean.getHostId());
                        errorHttpBean.setRequestId(errorBean.getRequestId());
                        errorHttpBean.setStatus(httpResponse.getStatusLine().getStatusCode());
                        startTicketTimeOut(context, errorHttpBean);
                        return errorHttpBean;
                    }
                } catch (Exception ex) {
                    return genErrorBean(StatusCode.SERVER_EMPTY, Error.SERVER_EMPTY, ErrorMsg.SERVER_EMPTY);
                }

            }

        }
    }
    
    /**
     * ticket超时重新认证获取ticket
     * @param context 上下文
     * @param errorBean 错误的数据结构体
     */
    public void startTicketTimeOut(Context context, HttpErrorBean errorBean) {
    	if (!TextUtils.isEmpty(errorBean.getErrCode())) {
    		if (errorBean.getStatus() == TICKET_NOT_AUTH_401 && errorBean.getErrCode().equalsIgnoreCase(TICKET_NOT_AUTH)) {
    			try {
    			    Intent intent = new Intent(context, Class.forName("com.xdja.actoma.service.GuardService"));
    			    intent.setAction("com.xdja.actoma.service.authticket");
                    context.startService(intent);
    			} catch (ClassNotFoundException ex){
    			    LogUtil.getUtils().e("thz  "+ ex.getMessage());
    			}
    		}
		}
    	
    }


    /**
     * 获取response的body数据
     * @param httpResponse HttpResponse
     * @return body
     */
    private String getResponceBody(HttpResponse httpResponse) {
        if (httpResponse == null) {
            return null;
        }
        String responseBody = null;
        try {
            responseBody = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseBody;
    }

    /**
     * 生成错误的数据结构
     * @param status 状态码
     * @param statusCode 错误码
     * @param errorMsg 错误信息
     * @return HttpErrorBean
     */
    private HttpErrorBean genErrorBean(int status, String statusCode, String errorMsg) {
        HttpErrorBean errorBean = new HttpErrorBean();
        errorBean.setStatus(status);
        errorBean.setErrCode(statusCode);
        errorBean.setMessage(errorMsg);
        return errorBean;
    }

      /**
     * 获取Gson对象
     * @return gson
     */
    private Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder.create();
    }

    @Override
    public String getTaskId() {
        return SystemClock.elapsedRealtimeNanos()+urlPath;
    }

    @Override
    public String getReason() {
        return "";
    }

    @Override
    public void template() {

    }
}
