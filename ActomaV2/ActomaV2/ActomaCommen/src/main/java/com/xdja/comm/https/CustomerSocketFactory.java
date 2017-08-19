package com.xdja.comm.https;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;

import android.annotation.SuppressLint;
import android.content.Context;

//import com.xdja.cipherkey.R;

/**
 * CustomerSocketFactory
 */
public class CustomerSocketFactory extends SSLSocketFactory {
    private static final String PASSWD = "111111";
    private final SSLContext sslContext = SSLContext.getInstance("TLS");
    public CustomerSocketFactory(KeyStore truststore)
            throws NoSuchAlgorithmException, KeyManagementException,
            KeyStoreException, UnrecoverableKeyException {
        super(truststore);

        TrustManager tm = new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            @SuppressWarnings("TrustAllX509TrustManager")
            @Override
            public void checkClientTrusted(
                    X509Certificate[] chain,
                    String authType)
                    throws java.security.cert.CertificateException {
            }
            @SuppressWarnings("TrustAllX509TrustManager")
            @Override
            public void checkServerTrusted(
                    X509Certificate[] chain,
                    String authType)
                    throws java.security.cert.CertificateException {
            }
        };
        sslContext.init(null, new TrustManager[]{tm}, new SecureRandom());
    }
    @SuppressLint("BadHostnameVerifier")
    public static SSLSocketFactory getSocketFactory(Context context) {
        InputStream input = null;
        try {
            input = context.getAssets().open("truststore.bks");
            KeyStore trustStore = KeyStore.getInstance(KeyStore
                    .getDefaultType());

            trustStore.load(input, PASSWD.toCharArray());

            SSLSocketFactory factory = new CustomerSocketFactory(trustStore);
            factory.setHostnameVerifier(new X509HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }

                @Override
                public void verify(String s, SSLSocket sslSocket) throws IOException {

                }

                @Override
                public void verify(String s, X509Certificate x509Certificate) throws SSLException {

                }

                @Override
                public void verify(String s, String[] strings, String[] strings1) throws SSLException {

                }
            });

            return factory;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                input = null;
            }
        }
    }
}
