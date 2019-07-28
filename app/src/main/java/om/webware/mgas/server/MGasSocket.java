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
            socket = IO.socket(MGasApi.HOST);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
