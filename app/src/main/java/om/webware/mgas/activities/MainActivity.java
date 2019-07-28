package om.webware.mgas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import io.socket.client.Socket;
import om.webware.mgas.R;
import om.webware.mgas.api.Service;
import om.webware.mgas.api.Services;
import om.webware.mgas.api.User;
import om.webware.mgas.fragments.dialogs.ErrorDialogFragment;
import om.webware.mgas.server.MGasApi;
import om.webware.mgas.server.MGasSocket;
import om.webware.mgas.server.Server;
import om.webware.mgas.tools.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    private User user;
    private DatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = new DatabaseHelper(this);
        user = (User)helper.select(DatabaseHelper.Tables.USERS, null);

        if(user.getToken() != null && Server.getNetworkAvailability(this)) {
            validateToken();
        } else {
            goToNext();
        }
    }

    private void validateToken() {
        JsonObject body = new JsonObject();
        body.addProperty("token", user.getToken());

        Server.request(MainActivity.this, Request.Method.POST, MGasApi.TOKEN_EXPIRY, body.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.startsWith(MGasApi.IISNODE_ERROR)) {
                            validateToken();
                        } else {
                            JsonObject res = new Gson().fromJson(response, JsonObject.class);
                            boolean expired = res.get("expired").getAsBoolean();

                            if(!expired) {
                                makeRequest();
                            } else {
                                helper.dropDatabase();
                                user = (User)helper.select(DatabaseHelper.Tables.USERS, null);
                                goToNext();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String err = getString(R.string.unable_to_connect_to_server_error);
                        ErrorDialogFragment fragment = ErrorDialogFragment.creteDialog(err);
                        fragment.show(getSupportFragmentManager(), "SERVER_CONNECTION_ERROR_DIALOG");

/*                        Log.v("SPLASH_ERR_TOKEN", "" + error.networkResponse.statusCode);
                        Log.v("SPLASH_ERR_TOKEN", "" + new String(error.networkResponse.data,
                                StandardCharsets.UTF_8));*/
                    }
                });
    }

    private void makeRequest() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("authorization", "Bearer " + user.getToken());
        Server.request(MainActivity.this, Request.Method.GET, MGasApi.SERVICES, headers,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.startsWith(MGasApi.IISNODE_ERROR)) {
                            makeRequest();
                        } else {
                            Services services = new Services(response);
                            helper.emptyTable(DatabaseHelper.Tables.SERVICES);

                            for(Service service : services.getServices()) {
                                helper.insert(DatabaseHelper.Tables.SERVICES, service);
                            }

                            Socket socket = MGasSocket.socket;
                            socket.connect();
                            socket.emit("userConnected", user.getId());

                            goToNext();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String err = getString(R.string.unable_to_connect_to_server_error);
                        ErrorDialogFragment fragment = ErrorDialogFragment.creteDialog(err);
                        fragment.show(getSupportFragmentManager(), "SERVER_CONNECTION_ERROR_DIALOG");

                        Log.v("SPLASH_ERR_SERVICES", "" + error.networkResponse.statusCode);
                        Log.v("SPLASH_ERR_SERVICES", "" + new String(error.networkResponse.data,
                                StandardCharsets.UTF_8));
                    }
                });
    }

    private void goToNext() {
        final Intent intent;
        if(user.getToken() == null) {
            intent = new Intent(this, LoginActivity.class);
        } else {
            if(user.getUserType().equals("consumer") || user.getUserType().equals("guest")) {
                intent = new Intent(this, ConsumerMainActivity.class);
            } else {
                intent = new Intent(this, DriverMainActivity.class);
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}
