package om.webware.mgas.server;

import android.util.Log;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.client.Url;
import io.socket.engineio.client.transports.WebSocket;
import io.socket.parseqs.ParseQS;
import io.socket.yeast.Yeast;
import okhttp3.OkHttpClient;

public class MGasSocket {

    public static Socket socket;
    static {
        try {
            HostnameVerifier verifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    HostnameVerifier hostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
//                    Log.v("SPLASH_HOSTNAME", hostname);
                    return hostnameVerifier.verify(hostname, session);
                }
            };

            TrustManager[] trustAll = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            try {
                                chain[0].checkValidity();
                            } catch (Exception e) {
                                throw new CertificateException("Certificate not valid or trusted.");
                            }
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            try {
                                chain[0].checkValidity();
//                                Log.v("SPLASH_SOCKET", "trusted");
                            } catch (Exception e) {
//                                Log.v("SPLASH_SOCKET", "untrusted");
                                throw new CertificateException("Certificate not valid or trusted.");
                            }
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            X509TrustManager trustManager = (X509TrustManager)trustAll[0];

            SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, trustAll, null);

            OkHttpClient client = new OkHttpClient.Builder()
                    .hostnameVerifier(verifier)
                    .sslSocketFactory(context.getSocketFactory(), trustManager)
                    .build();

            IO.Options options = new IO.Options();
            options.callFactory = client;
            options.webSocketFactory = client;


/*            URI source = new URI(MGasApi.HOST);
            URL url = Url.parse(source);
            URI uri = url.toURI();

            String path = "/socket.io";
            String namespace = uri.getPath();

            String host = uri.getHost();
            String scheme = uri.getScheme();
            int port = uri.getPort();
            String query = uri.getRawQuery() == null ? "" : uri.getRawQuery();

            boolean ipv6 = host.contains(":");




            Map<String, String> queryMap = new HashMap<>();
            String schema = "wss";

            String portString = "";
            if (port > 0 && port != 443) {
                portString = ":" + port;
            }

            String derivedQuery = ParseQS.encode(queryMap);
            if (derivedQuery.length() > 0) {
                derivedQuery = "?" + derivedQuery;
            }

            String socketUrl = schema + "://" + (ipv6 ? "[" + host + "]" : host) + portString + path + derivedQuery;


            Log.v("SPLASH_PATH", path);
            Log.v("SPLASH_NAMESPACE", namespace);
            Log.v("SPLASH_HOST", host);
            Log.v("SPLASH_SCHEME", scheme);
            Log.v("SPLASH_PORT", port + "");
            Log.v("SPLASH_QUERY", query);
            Log.v("SPLASH_IPV6", ipv6 + "");
            Log.v("SPLASH_URL", socketUrl);*/


//            Log.v("SPLASH_NAME", WebSocket.NAME);

            socket = IO.socket(MGasApi.HOST, options);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
