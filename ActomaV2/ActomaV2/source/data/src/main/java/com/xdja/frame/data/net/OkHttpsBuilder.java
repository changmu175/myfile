package com.xdja.frame.data.net;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;

import com.xdja.dependence.annotations.ContextSpe;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.frame.data.R;

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
import javax.net.ssl.TrustManagerFactory;

import okhttp3.OkHttpClient;

/**
 * <p>Summary:融入单项认证的OkHttpClient</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.actoma.net</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/7/14</p>
 * <p>Time:19:57</p>
 */
public class OkHttpsBuilder{
    /**
     * 默认的PIN码
     */
    private static final String DEFAULT_PASSWORD = "111111";
    /**
     * 默认的超时时间
     */
    public static final int CONN_TIME_OUT_UNIT = 30 * 1000;
    private final int READ_TIME_OUT_UNIT = 15 * 1000;
    private final int WRITE_TIME_OUT_UNIT = 15 * 1000;
    /**
     * 是否验证服务器主机地址
     */
    private final boolean isVerifyHostName = false;

    private String password;

    private Context context;

    @Inject
    public OkHttpsBuilder(@NonNull @ContextSpe(DiConfig.CONTEXT_SCOPE_APP)
                          Context context) {
        this(DEFAULT_PASSWORD, context);
    }

    /**
     * {@link OkHttpsBuilder}
     *
     * @param password 安全卡PIN码
     */
    public OkHttpsBuilder(String password, @NonNull Context context) {
        this.password = password;
        this.context = context;
    }

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
                );
        return builder;
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
            inputStream = this.context.getResources().openRawResource(res);
            keyStore.load(inputStream, password.toCharArray());
        } catch (KeyStoreException kse) {
            LogUtil.getUtils().i(kse.getMessage());
        } catch (CertificateException ce) {
            LogUtil.getUtils().i(ce.getMessage());
        } catch (NoSuchAlgorithmException ne) {
            LogUtil.getUtils().i(ne.getMessage());
        } catch (IOException ie) {
            LogUtil.getUtils().i(ie.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ie) {
                    LogUtil.getUtils().i(ie.getMessage());
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
    @Nullable
    @SuppressLint("TrulyRandom")
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

            sslContext.init(
                    keyManagerFactory.getKeyManagers(),
                    trustManagerFactory.getTrustManagers(),
                    new SecureRandom()
            );
        } catch (NoSuchAlgorithmException ne) {
            LogUtil.getUtils().i(ne.getMessage());
        } catch (KeyStoreException ke) {
            LogUtil.getUtils().i(ke.getMessage());
        } catch (UnrecoverableKeyException ue) {
            LogUtil.getUtils().i(ue.getMessage());
        } catch (KeyManagementException ke) {
            LogUtil.getUtils().i(ke.getMessage());
        } finally {

        }
        return sslContext;
    }
}
