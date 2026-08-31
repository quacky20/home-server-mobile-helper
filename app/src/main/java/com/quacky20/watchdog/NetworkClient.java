package com.quacky20.watchdog;

import android.content.Context;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.net.Socket;
import java.net.InetAddress;
import java.io.IOException;

public class NetworkClient {

    private final SSLContext sslContext;

    public NetworkClient(Context context) throws Exception {

        // Load the DigiCert Global Root G2 certificate
        CertificateFactory certificateFactory =
                CertificateFactory.getInstance("X.509");

        InputStream certificateInput =
                context.getResources().openRawResource(
                        R.raw.digicert_global_root_g2
                );

        Certificate certificate;

        try {
            certificate = certificateFactory.generateCertificate(
                    certificateInput
            );
        } finally {
            certificateInput.close();
        }

        // Create a new KeyStore
        KeyStore keyStore = KeyStore.getInstance(
                KeyStore.getDefaultType()
        );

        keyStore.load(null, null);

        // Add our trusted root
        keyStore.setCertificateEntry(
                "digicert_global_root_g2",
                certificate
        );

        // Create TrustManager using our KeyStore
        TrustManagerFactory trustManagerFactory =
                TrustManagerFactory.getInstance(
                        TrustManagerFactory.getDefaultAlgorithm()
                );

        trustManagerFactory.init(keyStore);

        // Create SSL context
        sslContext = SSLContext.getInstance("TLS");

        sslContext.init(
                null,
                trustManagerFactory.getTrustManagers(),
                null
        );
    }

    public HttpsURLConnection openConnection(String url)
            throws Exception {

        HttpsURLConnection connection =
                (HttpsURLConnection)
                        new java.net.URL(url).openConnection();

        connection.setSSLSocketFactory(
                new Tls12SocketFactory(
                        sslContext.getSocketFactory()
                )
        );

        return connection;
    }

    public static String getTlsInfo() {

        StringBuilder result = new StringBuilder();

        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, null, null);

            SSLSocketFactory factory =
                    context.getSocketFactory();

            SSLSocket socket =
                    (SSLSocket) factory.createSocket();

            result.append("Supported protocols:\n");

            String[] supported =
                    socket.getSupportedProtocols();

            for (String protocol : supported) {
                result.append(protocol).append("\n");
            }

            result.append("\nEnabled protocols:\n");

            String[] enabled =
                    socket.getEnabledProtocols();

            for (String protocol : enabled) {
                result.append(protocol).append("\n");
            }

            result.append("\nEnabled cipher suites:\n");

            String[] ciphers =
                    socket.getEnabledCipherSuites();

            for (String cipher : ciphers) {
                result.append(cipher).append("\n");
            }

            socket.close();

        } catch (Exception e) {
            result.append(e.toString());
        }

        return result.toString();
    }

    private static class Tls12SocketFactory extends SSLSocketFactory {

        private final SSLSocketFactory delegate;

        Tls12SocketFactory(SSLSocketFactory delegate) {
            this.delegate = delegate;
        }

        private SSLSocket enableTls12(Socket socket) {
            SSLSocket sslSocket = (SSLSocket) socket;

            sslSocket.setEnabledProtocols(
                    new String[]{"TLSv1.2"}
            );

            return sslSocket;
        }

        @Override
        public String[] getDefaultCipherSuites() {
            return delegate.getDefaultCipherSuites();
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return delegate.getSupportedCipherSuites();
        }

        @Override
        public Socket createSocket(
                Socket socket,
                String host,
                int port,
                boolean autoClose
        ) throws IOException {

            return enableTls12(
                    delegate.createSocket(
                            socket,
                            host,
                            port,
                            autoClose
                    )
            );
        }

        @Override
        public Socket createSocket(
                String host,
                int port
        ) throws IOException {

            return enableTls12(
                    delegate.createSocket(host, port)
            );
        }

        @Override
        public Socket createSocket(
                String host,
                int port,
                InetAddress localHost,
                int localPort
        ) throws IOException {

            return enableTls12(
                    delegate.createSocket(
                            host,
                            port,
                            localHost,
                            localPort
                    )
            );
        }

        @Override
        public Socket createSocket(
                InetAddress host,
                int port
        ) throws IOException {

            return enableTls12(
                    delegate.createSocket(host, port)
            );
        }

        @Override
        public Socket createSocket(
                InetAddress address,
                int port,
                InetAddress localAddress,
                int localPort
        ) throws IOException {

            return enableTls12(
                    delegate.createSocket(
                            address,
                            port,
                            localAddress,
                            localPort
                    )
            );
        }
    }
}