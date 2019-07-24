package om.webware.mgas.server;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import java.util.Map;

public class Server {

    public static void request(Context context, int method, String url, Response.Listener<String> successListener,
                               Response.ErrorListener errorListener) {
        request(context, method, url, null, null, successListener, errorListener);
    }

    public static void request(Context context, int method, String url, String body,
                               Response.Listener<String> successListener, Response.ErrorListener errorListener) {
        request(context, method, url, null, body, successListener, errorListener);
    }

    public static void request(Context context, int method, String url, Map<String, String> headers,
                               Response.Listener<String> successListener, Response.ErrorListener errorListener) {
        request(context, method, url, headers, null, successListener, errorListener);
    }

    public static void request(Context context, int method, String url, final Map<String, String> headers, String body,
                               Response.Listener<String> successListener, Response.ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(method, url, headers, body, successListener, errorListener);

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    public static boolean getNetworkAvailability(Context context) {
        ConnectivityManager manager = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        return (info != null && info.isConnected());
    }
}