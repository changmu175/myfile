package com.xdja.imp.data.net;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;

import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.data.cache.UserCache;
import com.xdja.imp.data.di.DiConfig;
import com.xdja.imp.data.di.annotation.Scoped;
import com.xdja.imp_data.R;

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

import javax.inject.Inject;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by gbc on 2016/9/9.
 */
public class OkHttpsClientMe {
    private Context context;

    private UserCache userCache;

    public static final String HEAD_TICKET = "ticket";
    /**
     * 默认的PIN码
     */
    private static final String DEFAULT_PASSWORD = "111111";

    public static final int TIME_OUT_UNIT = 30 * 1000;

    private String password;

    /**
     * 是否验证服务器主机地址
     */
    private boolean isVerifyHostName = false;

    private OkHttpClient okHttpClient;

    @Inject
    public OkHttpsClientMe(@NonNull @Scoped(DiConfig.CONTEXT_SCOPE_APP)Context context, UserCache userCache) {
        this(context, userCache, DEFAULT_PASSWORD);
    }

    public OkHttpsClientMe(Context context, UserCache userCache, String pwd) {
        this.context = context;
        this.userCache = userCache;
        this.password = pwd;
    }
    public OkHttpClient.Builder getBuilder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(TIME_OUT_UNIT, TimeUnit.MILLISECONDS)
                .readTimeout(TIME_OUT_UNIT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIME_OUT_UNIT, TimeUnit.MILLISECONDS)
                .sslSocketFactory(makeSSLSocketFactory())
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return !isVerifyHostName;
                    }
                })
                .addInterceptor(
                        new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request request = chain.request();
                                Request.Builder builder = request.newBuilder();
                                builder.header(HEAD_TICKET, userCache.get().getTicket());
                                return chain.proceed(builder.build());
                            }
                        }
                )
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE));

        return builder;
    }

    /**
     * 获取SSLSocketFactory
     * @return
     */
    private SSLSocketFactory makeSSLSocketFactory() {
        SSLContext sslContext = getSSLContext(
                readKeyStore(R.raw.truststore, context));
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
    private KeyStore readKeyStore(@RawRes int res, @NonNull Context context) {
        InputStream inputStream = null;
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            inputStream = context.getResources().openRawResource(res);
            keyStore.load(inputStream, password.toCharArray());
        } catch (KeyStoreException
                | CertificateException
                | NoSuchAlgorithmException
                | IOException kse) {// modified by ycm for lint 2017/02/16
            LogUtil.getUtils().i(kse.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ie) {
                    LogUtil.getUtils().i(ie.getMessage());
                }
            }
        }
        return keyStore;// modified by ycm for lint 2017/02/16
    }

    /**
     * 获取SSL连接上下文
     *
     * @param keyStore
     * @return
     */
    @Nullable
    private SSLContext getSSLContext(@Nullable KeyStore keyStore) {
        if (keyStore == null) {
            return null;
        }
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");

            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            KeyManagerFactory keyManagerFactory =
                    KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, password.toCharArray());

            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
        } catch (NoSuchAlgorithmException
                | KeyStoreException
                | KeyManagementException
                | UnrecoverableKeyException ne) {// modified by ycm for lint 2017/02/16
            LogUtil.getUtils().i(ne.getMessage());
        } /*finally {
        }*/
        return sslContext;
    }
}
