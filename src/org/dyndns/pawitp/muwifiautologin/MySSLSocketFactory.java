package org.dyndns.pawitp.muwifiautologin;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MySSLSocketFactory extends SSLSocketFactory {

    private SSLContext mSslContext = SSLContext.getInstance("TLS");

    public MySSLSocketFactory(KeyStore truststore, final byte[] trustedDer) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        super(truststore);

        // Basically a "trust-all" trust manager
        // (except if "trustedDer" is set, it will only trust that certificate)
        // Cisco-based system uses an invalid certificate
        // Aruba-based system uses *.mahidol.ac.th wildcard certificate and so this class should not be used.
        // Aruba-IC-based system uses default secure.arubanetworks.com certificate
        TrustManager tm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                if (trustedDer != null) {
                    // If trustedDer is set, we pin to only trust this certificate
                    if (chain.length == 0) {
                        throw new CertificateException("No certificate chain provided");
                    } else if (!Arrays.equals(chain[0].getEncoded(), trustedDer)) {
                        throw new CertificateException("Certificate does not match pinned certificate " + chain[0]);
                    }
                }
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        mSslContext.init(null, new TrustManager[] { tm }, null);
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
        return mSslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    @Override
    public Socket createSocket() throws IOException {
        return mSslContext.getSocketFactory().createSocket();
    }
}