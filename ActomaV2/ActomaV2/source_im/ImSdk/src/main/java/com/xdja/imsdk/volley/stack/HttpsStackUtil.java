package com.xdja.imsdk.volley.stack;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import android.annotation.SuppressLint;
/**
 * https请求使用
 * author gbc
 */
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

public class HttpsStackUtil {
	private final static String TAG = "HttpsurlStack";
	
	 /**
     * 默认的PIN码
     */
    private static String DEFAULT_PASSWORD = "111111";
	
	public static HurlStack getHttpsStack(Context context, int storeId, String pwd) {
        if (!TextUtils.isEmpty(pwd)) {
            DEFAULT_PASSWORD = pwd;
        }
		SSLContext sslContext = getSSLContext(readKeyStore(context, storeId));
		return new HurlStack(null, sslContext.getSocketFactory());
	}

    /**
     * 读取本地证书
     *
     * @param res 证书资源ID
     * @return 获取到的证书
     */
    @SuppressWarnings("finally")
	private static KeyStore readKeyStore(Context cxt, int res) {
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
    @SuppressLint("TrulyRandom")
	private static SSLContext getSSLContext(KeyStore keyStore) {
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
