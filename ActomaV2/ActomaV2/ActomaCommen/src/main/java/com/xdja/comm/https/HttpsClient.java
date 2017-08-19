package com.xdja.comm.https;

import android.content.Context;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

/**
 * Created by THZ on 2015/7/8.
 */
public class HttpsClient {


    private static ClientConnectionManager connMgr;
    /**
     * 获取httpClient
     * @param context
     * @return
     */
    public static HttpClient getSpecialKeyStoreClient(Context context) {
        BasicHttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
        HttpProtocolParams.setUseExpectContinue(params, false);
        if (connMgr == null) {
            SchemeRegistry schReg = new SchemeRegistry();
            schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            schReg.register(new Scheme("https", CustomerSocketFactory.getSocketFactory(context), 443));
            connMgr = new ThreadSafeClientConnManager(params, schReg);
        }
        return new DefaultHttpClient(connMgr, params);
//        return new DefaultHttpClient();
    }
}
