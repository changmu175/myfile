package com.xdja.contact.http.engine;

import android.content.Context;

import com.xdja.comm.https.HttpErrorBean;
import com.xdja.comm.https.HttpsRequest;
import com.xdja.comm.https.HttpsRequstResult;
import com.xdja.comm.https.IHttpResult;
import com.xdja.contact.http.wrap.IHttpParams;

/**
 * @author hkb.
 * @since 2015/7/29/0029.
 */
public class RequestTask {

    private Context context;

    private IHttpParams params;

    public RequestTask(Context context, IHttpParams params) {
        this.context = context;
        this.params = params;
    }

    public HttpsRequstResult execute(){

        HttpsRequest request = new HttpsRequest(context, params.getMethod(), params.getUrl(), params.getPath());

        return request.receive(params.getTicket(), params.getBody(), new IHttpResult() {
            @Override
            public void onFail(HttpErrorBean errorBean) {
            }

            @Override
            public void onSuccess(String body) {}

            @Override
            public void onErr() {

            }
        });
    }

}
