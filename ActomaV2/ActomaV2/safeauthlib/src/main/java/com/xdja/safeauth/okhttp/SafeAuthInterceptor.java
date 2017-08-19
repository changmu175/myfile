package com.xdja.safeauth.okhttp;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.xdja.frame.data.cache.SharedPreferencesUtil;
import com.xdja.frame.data.chip.TFCardManager;
import com.xdja.safeauth.bean.BusinessType;
import com.xdja.safeauth.bean.CommonResult;
import com.xdja.safeauth.bean.GetAccessTicketResult;
import com.xdja.safeauth.bean.GetRandomResult;
import com.xdja.safeauth.bean.SafeAuthSErrCode;
import com.xdja.safeauth.cip.CipManager;
import com.xdja.safeauth.exception.BusinessErrorCode;
import com.xdja.safeauth.exception.BusinessException;
import com.xdja.safeauth.exception.CipErrorCode;
import com.xdja.safeauth.exception.NetException;
import com.xdja.safeauth.exception.SafeAuthException;
import com.xdja.safeauth.log.Log;
import com.xdja.safeauth.property.SafeAuthProperty;
import com.xdja.safeauth.request.GetTicketBean;
import com.xdja.safeauth.request.SignatureUtils;
import com.xdja.safeauth.util.SafeAuthSErrCodeUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by THZ on 2016/5/23.
 */
public class SafeAuthInterceptor implements Interceptor {

    /**
     * ticket失效
     */
    private static final int INT_400 = 400;

    /**
     * ticket不存在
     */
    private static final int INT_401 = 401;

    /**
     * 服务器内部错误
     */
    public static final String INT_500 = "500";

    /**
     * 上下文
     */
    private Context context;

    /**
     * pin码
     */
    private String pin = "";

    private static final String TAG = SafeAuthInterceptor.class.getSimpleName();


    /**
     * 构造方法
     * @param context 上下文
     * @param pin pin码
     */
    public SafeAuthInterceptor(Context context, String pin) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException, SafeAuthException {
        String ticket = SharedPreferencesUtil.getTicket(context);
        Response responseSrc = null;
        boolean isTicetError = true;
        String result = null;
        if (!TextUtils.isEmpty(ticket)) {
            Request requestSrc = chain.request();
            Request.Builder buildersrc = requestSrc.newBuilder();
            buildersrc.addHeader(HeaderUtils.AUTHEN, ticket);
            responseSrc = chain.proceed(buildersrc.build());
            result  = isTicketError(responseSrc);
            if (!TextUtils.isEmpty(result) && result.equals("-1")) {
                isTicetError = true;
            } else {
                isTicetError = false;
            }
        } else {
            isTicetError = true;
        }
        Log.e(TAG, "isTicetError: " + isTicetError);
        if (isTicetError) {
            SafeAuthException exception = null;
            String random = null;
            String index = null;
            //请求获取随机数
            Map<String, Object>randomMap = getRandom(chain);
            random = (String)randomMap.get(GetRandomResult.CHLGSTR);
            index = (String)randomMap.get(GetRandomResult.INDEX);
            if (TextUtils.isEmpty(index) || TextUtils.isEmpty(random)) {
                Log.e(TAG, "getrandom index or random is null : " + index + ";" + random);
                exception = new BusinessException(BusinessErrorCode.ERROR_CONTENT_DATA, "",  BusinessType.GET_RANDOM);
                throw exception;
            }
            ticket = getAccessTicket(chain, random, index);
            //发送原始请求
            Request request = chain.request();
            Request.Builder builder1 = request.newBuilder();
            builder1.addHeader(HeaderUtils.AUTHEN, ticket);
            return chain.proceed(builder1.build());
        } else {

            if (!TextUtils.isEmpty(result)) {
                return responseSrc.newBuilder().body(ResponseBody.create(MediaType.parse(result), result)).build();
            }
            return responseSrc;
        }
    }


    /**
     * 获取随机数
     * @return
     * @throws SafeAuthException
     */
    private Map<String, Object> getRandom(Chain chain) throws IOException, SafeAuthException {
        Request.Builder builder = new Request.Builder();
        builder.url(SafeAuthProperty.Url + "ticket/getChallenge");
        String cardId = CipManager.getInstance().getCardId();
        builder.addHeader(HeaderUtils.CARDID, cardId);
        Response randomRep = chain.proceed(builder.get().build());
        String randomType = BusinessType.GET_RANDOM;
        Map<String, Object> randomMap = handleResultError(randomRep, randomType);
        return randomMap;
    }

    /**
     * 获取ticket
     * @param random
     * @param index
     * @return
     * @throws IOException
     * @throws SafeAuthException
     */
    private String getAccessTicket(Chain chain, String random, String index)  throws IOException, SafeAuthException {

        //请求获取accessTicket
        String ticketType = BusinessType.GET_ACCESS_TICKE;
        SafeAuthException exception = null;
        if(TextUtils.isEmpty(pin)){
            /*[S]modify by tangsha @20160816 for safe card lock because use default pin init ticket*/
            pin = TFCardManager.getPin();
        /*[E]modify by tangsha @20160816 for safe card lock because use default pin init ticket*/
        }
        if(TextUtils.isEmpty(pin)){
            throw new BusinessException(CipErrorCode.GET_TICKET_DATA_ERROR, "", ticketType);
        }
        GetTicketBean ticketBean = SignatureUtils.sm2SignData(pin, random, index);
        if (ticketBean == null) {
            throw new BusinessException(CipErrorCode.GET_TICKET_DATA_ERROR, "", ticketType);
        }
        Request.Builder builderTicket = new Request.Builder();
        builderTicket.addHeader(HeaderUtils.X_VERSON, ticketBean.getVersion());
        builderTicket.addHeader(HeaderUtils.X_CARDID, ticketBean.getCardId());
        builderTicket.addHeader(HeaderUtils.X_SN, ticketBean.getSn());
        builderTicket.addHeader(HeaderUtils.X_TIMESTAMP, ticketBean.getTimestamp());
        builderTicket.addHeader(HeaderUtils.X_INDEX, ticketBean.getIndex());
        builderTicket.addHeader(HeaderUtils.X_SINGATURE, ticketBean.getSignature());
        builderTicket.url(SafeAuthProperty.Url + "ticket/verifyChallenge");
        Response ticketRes = chain.proceed(builderTicket.post(RequestBody.create(MediaType.parse("application/json;charest=utf-8"), "")).build());

        Map<String, Object> ticketMap = handleResultError(ticketRes, ticketType);
        String ticket = (String)ticketMap.get(GetAccessTicketResult.TICKET);
        if (TextUtils.isEmpty(ticket)) {
            exception = new BusinessException(BusinessErrorCode.ERROR_CONTENT_DATA, "", ticketType);
            throw exception;
        }
        boolean isSave = SharedPreferencesUtil.setTicket(context, ticket);
        if (!isSave) {
            isSave = SharedPreferencesUtil.setTicket(context, ticket);
            if (!isSave) {
                exception = new BusinessException(BusinessErrorCode.SAVE_TICKET_ERR, "", ticketType);
                Log.e(TAG, "getAccessTicket save ticket  error");
                throw exception;
            }

        }
        return ticket;
    }




    /**
     * 处理网络返回错误数据
     * @param response
     * @param mark
     * @return
     * @throws SafeAuthException
     */
    private Map<String, Object> handleResultError(Response response, String mark) throws SafeAuthException {
        SafeAuthException exception = null;
        if (response.isSuccessful()) {
            Gson gson = new Gson();
            JsonReader jsonReader = new JsonReader(new InputStreamReader(response.body().byteStream()));
            Map<String, Object> mapResult = gson.fromJson(jsonReader, new TypeToken<Map<String, Object>>() {}.getType());
            if (mapResult == null) {
                exception = new BusinessException(BusinessErrorCode.ERROR_SERVER_EMPTY, "", mark);
                throw exception;
            }
            String code = (String)mapResult.get(CommonResult.CODE);
            if (TextUtils.isEmpty(code)) {
                exception = new BusinessException(BusinessErrorCode.ERROR_SERVER_CODE, "", mark);
                throw exception;
            }
            if (!code.equals(BusinessErrorCode.SUCCESS)) {
                Log.e(TAG, "handleResultError server code :" + code);
                exception = new BusinessException(code, "", mark);
                throw exception;
            }
            return  mapResult;
        } else {
            Log.e(TAG, "handleResultError " + "mark :" + mark +  ";response code :" + response.code());
            exception = new NetException(response.code() + "", response.message(), mark);
            throw exception;
        }

    }


    /**
     * 处理网络返回错误数据
     * @param response
     * @return
     * @throws SafeAuthException
     */
    private String isTicketError(Response response) throws SafeAuthException, IOException {
        if (response.isSuccessful()) {
            return null;
        } else {
            if (response.body() != null) {
                if (response.code() == INT_401 || response.code() == INT_400) {
                    SafeAuthException exception = null;
                    Gson gson = new Gson();
                    String content = new String(response.body().bytes());
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content.getBytes());
                    JsonReader jsonReader = new JsonReader(new InputStreamReader(byteArrayInputStream));
                    Map<String, Object> mapResult = gson.fromJson(jsonReader, new TypeToken<Map<String, Object>>() {
                    }.getType());
                    if (mapResult == null) {
                        exception = new BusinessException(BusinessErrorCode.ERROR_SERVER_EMPTY, "", "");
                        throw exception;
                    }

                    Object object = mapResult.get(SafeAuthSErrCodeUtil.ERR_CODE);
                    if (object != null) {
                        String errorCode = (String) object;
                        Log.e(TAG, "isTicketError response code :" + response.code() + "; server code : " + errorCode);
                        if (!TextUtils.isEmpty(errorCode))  {
                            if (response.code() == INT_401) {
                                if (errorCode.equals(SafeAuthSErrCode.X_9006)) {
                                    return "-1";
                                }
                            } else if (response.code() == INT_400) {
                                if (errorCode.equals(SafeAuthSErrCode.X_9007)) {
                                    return "-1";
                                }
                            }
                        }
                    }
                    return content;
                }
            }
        }
        return null;
    }
}
