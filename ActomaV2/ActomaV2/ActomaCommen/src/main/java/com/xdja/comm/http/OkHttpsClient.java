package com.xdja.comm.http;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;

import com.xdja.comm.R;
import com.xdja.dependence.uitls.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * <p>Summary:融入单项认证的OkHttpClient</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.actoma.net</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/7/14</p>
 * <p>Time:19:57</p>
 */
public class OkHttpsClient {
    /**
     * 默认的证书密码
     */
    private static final String DEFAULT_PASSWORD = "111111";

    /**
     * okhttpsCliet单例
     */
    private static OkHttpsClient okHttpsClient;

    private Context context;

    private OkHttpClient.Builder builder;

    public static final int TIME_OUT_UNIT = 30 * 1000;
    /**
     * 安全卡PIN码
     */
    private String password;

    /**
     * 是否验证服务器主机地址
     */
    private boolean isVerifyHostName = false;

    /**
     * {@link OkHttpsClient}
     *
     * @param context  上下文句柄
     * @param password 证书密码
     */
    public OkHttpsClient(Context context, String password) {
        this.context = context;
        this.password = password;
    }
    /**
     * {@link OkHttpsClient}
     *
     * @param context  上下文句柄
     */
    public OkHttpsClient(Context context) {
        this.context = context;
        this.password = DEFAULT_PASSWORD;

        builder = new OkHttpClient.Builder()
                .connectTimeout(TIME_OUT_UNIT, TimeUnit.MILLISECONDS)
                .readTimeout(TIME_OUT_UNIT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIME_OUT_UNIT, TimeUnit.MILLISECONDS)
                .sslSocketFactory(makeSSLSocketFactory())
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return !isVerifyHostName;
                    }
                })
                /*.addInterceptor(
                        new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request request = chain.request();
                                Request.Builder builder = request.newBuilder();
                                builder.header(HEAD_TICKET, userCache.get().getTicket());
                                return chain.proceed(builder.build());
                            }
                        }
                )*/
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE));
    }



    /**
     * 获取{@link #okHttpsClient}
     *
     * @return 获取到的单例
     */
    public static OkHttpsClient getInstance(Context context) {
        if (okHttpsClient == null) {
            okHttpsClient = new OkHttpsClient(context);
            //okHttpsClient.build();
        }
        return okHttpsClient;
    }

    public OkHttpClient getOkHttpClient() {
        if (null != okHttpsClient) {
            return builder.build();
        } else {
            return null;
        }
    }

    public OkHttpClient.Builder getOkHttpClientBuilder() {
        return builder;
    }
    /**
     * 获取SSLSocketFactory
     * @return
     */
    private  SSLSocketFactory makeSSLSocketFactory() {
        SSLContext sslContext = getSSLContext(
                readKeyStore(R.raw.truststore));
        if (sslContext == null) {
            return null;
        }
        return sslContext.getSocketFactory();
    }

    /**
     * 读取本地证书
     *
     * @param res 证书资源ID
     * @return 获取到的证书
     */
    @Nullable
    private KeyStore readKeyStore(@RawRes int res) {
        InputStream inputStream = null;
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            inputStream = context.getResources().openRawResource(res);
            keyStore.load(inputStream, password.toCharArray());
        } catch (CertificateException
                | KeyStoreException
                | NoSuchAlgorithmException
                | IOException ce) {
            LogUtil.getUtils().i(ce.getMessage()); // modified by ycm for lint 2017/02/13
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ie) {
                    LogUtil.getUtils().i(ie.getMessage());
                }
            }
        }
        return keyStore;
    }



    /**
     * 获取SSL连接上下文
     *
     * @param keyStore
     * @return
     */
    @Nullable
    @SuppressLint("TrulyRandom") /*随机数的安全问题*/
    private SSLContext getSSLContext(@Nullable KeyStore keyStore) {
        if (keyStore == null) {
            return null;
        }
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");

            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            KeyManagerFactory keyManagerFactory =
                    KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, password.toCharArray());

            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
        } catch (NoSuchAlgorithmException
                | KeyStoreException
                | UnrecoverableKeyException
                | KeyManagementException ne) {// modified by ycm for lint 2017/02/13
            LogUtil.getUtils().i(ne.getMessage());
        } /*finally {
        }*/
        return sslContext;
    }
}
