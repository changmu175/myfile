package com.xdja.safeauth.net;

import android.content.Context;

import com.xdja.safeauth.log.Log;
import com.xdja.safeauth.okhttp.OkHttpInterceptor;
import com.xdja.safeauth.okhttp.SafeAuthInterceptor;

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
import javax.net.ssl.TrustManagerFactory;

import okhttp3.OkHttpClient;
import xdja.safeauthlib.R;

/**
 * okhttpClient的构造器
 */
public class OkHttpsBuilder{

    /**
     * tag
     */
    private static final String TAG = OkHttpsBuilder.class.getSimpleName();
    /**
     * 默认的PIN码
     */
    private static final String DEFAULT_PASSWORD = "111111";
    /**
     * 默认的超时时间
     */
    private final int CONN_TIME_OUT_UNIT = 2*15 * 1000;
    private final int READ_TIME_OUT_UNIT = 2*15 * 1000;
    private final int WRITE_TIME_OUT_UNIT = 2*15 * 1000;
    /**
     * 是否验证服务器主机地址
     */
    private final boolean isVerifyHostName = false;

    /**
     * 正式密码
     */
    private String pin;

    /**
     * 上下文
     */
    private Context context;

    /**
     * 构造方法
     * @param context 上下文
     */
    public OkHttpsBuilder(Context context) {
        this(DEFAULT_PASSWORD, context);
    }

    /**
     * {@link OkHttpsBuilder}
     *
     * @param pin 安全卡PIN码
     */
    public OkHttpsBuilder(String pin, Context context) {
        this.pin = pin;
        this.context = context;
    }


    /**
     * 构建httpsokHttpClient对象
     * @return OkHttpClient.Builder
     */
    public OkHttpClient.Builder build() {
        SSLContext sslContext = getSSLContext(readKeyStore(R.raw.truststore));
        if (sslContext == null) {
            return null;
        }
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(CONN_TIME_OUT_UNIT, TimeUnit.MILLISECONDS)
                .readTimeout(READ_TIME_OUT_UNIT, TimeUnit.MILLISECONDS)
                .writeTimeout(WRITE_TIME_OUT_UNIT, TimeUnit.MILLISECONDS)
                .sslSocketFactory(sslContext.getSocketFactory())
                .hostnameVerifier(
                        new HostnameVerifier() {
                            @Override
                            public boolean verify(String hostname, SSLSession session) {
                                return true;
                            }
                        }
                ) ;
        builder.addInterceptor(new SafeAuthInterceptor(context, pin));
        builder.addInterceptor(OkHttpInterceptor.getOkHttpLogLevel());
        return builder;
    }

    /**
     * 读取本地证书
     *
     * @param res 证书资源ID
     * @return 获取到的证书
     */
    private KeyStore readKeyStore(int res) {
        InputStream inputStream = null;
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            inputStream = this.context.getResources().openRawResource(res);
            keyStore.load(inputStream, DEFAULT_PASSWORD.toCharArray());
        } catch (KeyStoreException kse) {
            Log.e(TAG, kse.getMessage());
        } catch (CertificateException ce) {
            Log.e(TAG, ce.getMessage());
        } catch (NoSuchAlgorithmException ne) {
            Log.e(TAG, ne.getMessage());
        } catch (IOException ie) {
            Log.e(TAG, ie.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ie) {
                }
            }
            return keyStore;
        }
    }

    /**
     * 获取SSL连接上下文
     *
     * @param keyStore
     * @return
     */
    private SSLContext getSSLContext(KeyStore keyStore) {
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
            keyManagerFactory.init(keyStore, DEFAULT_PASSWORD.toCharArray());

            sslContext.init(
                    keyManagerFactory.getKeyManagers(),
                    trustManagerFactory.getTrustManagers(),
                    new SecureRandom()
            );
        } catch (NoSuchAlgorithmException ne) {
            Log.e(TAG, ne.getMessage());
        } catch (KeyStoreException ke) {
            Log.e(TAG, ke.getMessage());
        } catch (UnrecoverableKeyException ue) {
            Log.e(TAG, ue.getMessage());
        } catch (KeyManagementException ke) {
            Log.e(TAG, ke.getMessage());
        } finally {
        }
        return sslContext;
    }
}
