package webrelay;

import android.content.Context;
import com.xdja.dependence.uitls.LogUtil;
import org.json.JSONObject;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import util.JsonUtil;
import webrelay.bean.FailureBase;
import webrelay.bean.SuccessBase;
import webrelay.bean.TicketErrorBase;


/**
 * Created by gbc on 2016/10/11.
 */
public class OKHttp3Util {

    private static final String TAG = "OKHttpUtil";

    private static OkHttps3 okHttps = null;

    private static OKHttp3Util okHttpUtil = new OKHttp3Util();

    public static OKHttp3Util getInstance(Context cxt){
        if (okHttps ==null){

            synchronized (OKHttp3Util.class){
                if (okHttps ==null)
                    okHttps =new OkHttps3(cxt);
            }

        }
        return okHttpUtil;
    }

    private static final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
    /**
     * POST提交Json数据
     * @param url
     * @param json
     * @throws IOException
     */
    public  void post(String url, String json, Callback callback) {

        RequestBody body= RequestBody.create(JSON,json);

        Request request=new Request.Builder()
                .url(url)
                .post(body)
                .build();
        okHttps.getOkHttpClient().newCall(request).enqueue(callback);
    }


    public <S extends SuccessBase,F extends FailureBase,T extends TicketErrorBase> void post(
            String url,String json,String ticket,final JsonCallback<S,F,T> jsonCallback){

        LogUtil.getUtils(TAG).d("TicketErrorBase:" + url + " url");
        LogUtil.getUtils(TAG).d("TicketErrorBase:" + json + " json");
        LogUtil.getUtils(TAG).d("TicketErrorBase:" + ticket + " ticket");

        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.getUtils(TAG).d("TicketErrorBase:" + e.getMessage() + " code");
                jsonCallback.onNetworkError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result=response.body().string();
                LogUtil.getUtils(TAG).d("TicketErrorBase:" + result + " result " + response.message());
                JSONObject jsonObject= JsonUtil.getJsonObject(result);
                int code=response.code();
                LogUtil.getUtils(TAG).d("TicketErrorBase:" + code + " code");
                if ( code==400 || code==401){
                    T ticketError=JsonUtil.jsonToObject(result, jsonCallback.getTicketFailureType());
                    jsonCallback.onTicketError(ticketError);
                    return;
                }

                if (response.isSuccessful()){

                    if (jsonObject.has("error")){

                        F faiureObject= JsonUtil.jsonToObject(result, jsonCallback.getFailureType());
                        jsonCallback.onFailure(faiureObject);

                        LogUtil.getUtils(TAG).d("TicketErrorBase:" + jsonObject + "   error");
                        return;
                    }

                    S faiureObject= JsonUtil.jsonToObject(result, jsonCallback.getSuccessType());
                    jsonCallback.onSuccess(faiureObject);

                }
            }
        };

        RequestBody body= RequestBody.create(JSON,json);
        Request request=new Request.Builder()
                .addHeader("ticket", ticket)
                .url(url)
                .post(body)
                .build();
        okHttps.getOkHttpClient().newCall(request).enqueue(callback);
    }

}
