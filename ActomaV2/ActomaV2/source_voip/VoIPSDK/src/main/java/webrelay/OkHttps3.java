package webrelay;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.xdja.voipsdk.R;

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

public class OkHttps3{
    private final String TAG = "OkHttpClient";

    private OkHttpClient.Builder builder;

    private Context context;
    /**
     * 默认的PIN码
     */
    private static final String DEFAULT_PASSWORD = "111111";

    public static final int TIME_OUT_UNIT = 30 * 1000;


    /**
     * 是否验证服务器主机地址
     */
    private boolean isVerifyHostName = false;


    /**
     * 是否验证证书
     */
    private boolean isVerifyCert=true;

    public OkHttps3(Context cxt) {
        this.context = cxt;
        this.builder = new OkHttpClient.Builder()
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

    public OkHttpClient getOkHttpClient() {
        return builder.build();
    }


    /**
     * 获取SSLSocketFactory
     * @return
     */
    private SSLSocketFactory makeSSLSocketFactory() {
        SSLContext sslContext = getSSLContext(
                readKeyStore(context, R.raw.truststore ));
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
    @SuppressLint("FinallyBlockCannotCompleteNormally")
    private KeyStore readKeyStore(Context cxt, int res) {
        InputStream inputStream = null;
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            inputStream = cxt.getResources().openRawResource(res);
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
    @SuppressLint("EmptyFinallyBlock")
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
