package om.webware.mgas.server;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class StringJsonRequest extends JsonRequest<String> {

    private Map<String, String> headers;

    StringJsonRequest(int method, String url, Map<String, String> headers, String json,
                      Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, json, listener, errorListener);
        this.headers = headers;
    }

    @Override
    public void cancel() { super.cancel(); }

    @Override
    protected void deliverResponse(String response) { super.deliverResponse(response); }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String parsedHeader = HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET);
            String jsonString = new String(response.data, parsedHeader);
            return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers == null ? super.getHeaders() : headers;
    }

    @Override
    public RetryPolicy getRetryPolicy() {
        return new DefaultRetryPolicy( 0,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }
}