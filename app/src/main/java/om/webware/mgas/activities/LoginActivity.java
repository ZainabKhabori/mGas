package om.webware.mgas.activities;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.logging.Level;

import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.EngineIOException;
import io.socket.engineio.client.Transport;
import io.socket.engineio.client.transports.WebSocket;
import om.webware.mgas.R;
import om.webware.mgas.api.BankCard;
import om.webware.mgas.api.BankCards;
import om.webware.mgas.api.Consumer;
import om.webware.mgas.api.DeliveryIssue;
import om.webware.mgas.api.DeliveryIssues;
import om.webware.mgas.api.Driver;
import om.webware.mgas.api.Feedback;
import om.webware.mgas.api.Location;
import om.webware.mgas.api.Locations;
import om.webware.mgas.api.Order;
import om.webware.mgas.api.OrderService;
import om.webware.mgas.api.Orders;
import om.webware.mgas.api.Service;
import om.webware.mgas.api.Services;
import om.webware.mgas.api.User;
import om.webware.mgas.fragments.dialogs.ErrorDialogFragment;
import om.webware.mgas.fragments.dialogs.OTPDialogFragment;
import om.webware.mgas.fragments.dialogs.RegistrationOptionsDialogFragment;
import om.webware.mgas.fragments.dialogs.WaitDialogFragment;
import om.webware.mgas.server.MGasApi;
import om.webware.mgas.server.MGasSocket;
import om.webware.mgas.server.Server;
import om.webware.mgas.server.SocketResponse;
import om.webware.mgas.tools.AndroidLoggingHandler;
import om.webware.mgas.tools.DatabaseHelper;

@SuppressWarnings("ConstantConditions")
public class LoginActivity extends AppCompatActivity implements TextWatcher {

    private Socket socket;

    private TextInputEditText editTextMobile;
    private TextInputEditText editTextPassword;
    private Button buttonLogin;

    private WaitDialogFragment waitDialogFragment;
    private boolean rtl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);












        AndroidLoggingHandler.reset(new AndroidLoggingHandler());
        java.util.logging.Logger.getLogger(Socket.class.getName()).setLevel(Level.FINEST);
        java.util.logging.Logger.getLogger(io.socket.engineio.client.Socket.class.getName()).setLevel(Level.FINEST);
        java.util.logging.Logger.getLogger(Manager.class.getName()).setLevel(Level.FINEST);

        editTextMobile = findViewById(R.id.editTextMobile);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        editTextMobile.addTextChangedListener(this);
        editTextPassword.addTextChangedListener(this);

        rtl = getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    @Override
    protected void onResume() {
        super.onResume();

        socket = MGasSocket.socket;

        socket.io().on(Socket.EVENT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.v("SPLASH_ERROR", "error event fired");
                EngineIOException exception = (EngineIOException)args[0];
                exception.printStackTrace();
            }
        });

        socket.on("loginError", loginError);
        socket.on("otpSent", otpSent);
        socket.on("otpVerificationDone", otpVerificationDone);
        socket.on("loginSuccess", loginSuccess);
        socket.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        socket.off();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String mobile = editTextMobile.getText().toString();
        String password = editTextPassword.getText().toString();

        buttonLogin.setEnabled(!mobile.isEmpty() && !password.isEmpty());
    }

    @Override
    public void afterTextChanged(Editable s) {
        //
    }

    public void forgotPasswordAction(View view) {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    public void loginAction(View view) {
        int mobile = Integer.parseInt(editTextMobile.getText().toString());
        String pass = editTextPassword.getText().toString();

        waitDialogFragment = WaitDialogFragment.createDialog();
        waitDialogFragment.setCancelable(false);
        waitDialogFragment.show(getSupportFragmentManager(), "WAIT_DIALOG");

        Log.v("SPLASH_SOCKET", socket.connected() + "");

        socket.emit("login", mobile, pass);
    }

    public void goRegisterAction(View view) {
        RegistrationOptionsDialogFragment fragment = RegistrationOptionsDialogFragment.createDialog();
        fragment.show(getSupportFragmentManager(), "REGISTER_OPT_DIALOG_FRAG");
    }

    public void guestLoginAction(View view) {
        waitDialogFragment = WaitDialogFragment.createDialog();
        waitDialogFragment.setCancelable(false);
        waitDialogFragment.show(getSupportFragmentManager(), "WAIT_DIALOG");

        getGuestToken();
    }

    private void getGuestToken() {
        Server.request(this, Request.Method.GET, MGasApi.GUEST_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.startsWith(MGasApi.IISNODE_ERROR)) {
                            getGuestToken();
                        } else {
                            JsonObject res = new Gson().fromJson(response, JsonObject.class);
                            User user = new User();
                            user.setToken(res.get("token").getAsString());
                            user.setfName(getString(R.string.guest));
                            user.setlName("");
                            user.setUserType(res.get("userType").getAsString());

                            DatabaseHelper helper = new DatabaseHelper(LoginActivity.this);
                            helper.insert(DatabaseHelper.Tables.USERS, user);

                            Intent intent = new Intent(LoginActivity.this, ConsumerMainActivity.class);

                            getServices(user.getToken(), intent, helper);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //
                    }
                });
    }

    private void getServices(final String token, final Intent intent, final DatabaseHelper helper) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("authorization", "Bearer " + token);
        Server.request(LoginActivity.this, Request.Method.GET, MGasApi.SERVICES, headers,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.startsWith(MGasApi.IISNODE_ERROR)) {
                            getServices(token, intent, helper);
                        } else {
                            Services services = new Services(response);
                            for(Service service : services.getServices()) {
                                helper.insert(DatabaseHelper.Tables.SERVICES, service);
                            }

                            waitDialogFragment.dismiss();

                            startActivity(intent);
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            waitDialogFragment.dismiss();

                            String msgJsonStr = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            JSONObject msgJson = new JSONObject(msgJsonStr);
                            String msg = msgJson.getString("error");

                            ErrorDialogFragment errorDialogFragment = ErrorDialogFragment.creteDialog(msg);
                            errorDialogFragment.show(getSupportFragmentManager(), "ERROR_DIALOG");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private Emitter.Listener loginError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject res = (JSONObject)args[0];
                    SocketResponse response = new Gson().fromJson(res.toString(), SocketResponse.class);

                    String msg;
                    if(rtl) {
                        msg = response.getArabicMsg();
                    } else {
                        msg = response.getMsg();
                    }

                    ErrorDialogFragment errorDialogFragment = ErrorDialogFragment.creteDialog(msg);
                    errorDialogFragment.show(getSupportFragmentManager(), "ERROR_DIALOG");

                    waitDialogFragment.dismiss();
                }
            });
        }
    };

    private Emitter.Listener otpVerificationDone = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject json = (JSONObject)args[0];
                    SocketResponse response = new Gson().fromJson(json.toString(), SocketResponse.class);

                    if(response.getStatusCode() == 200) {
                        waitDialogFragment.show(getSupportFragmentManager(), "LOGIN_USER_INFO_WAIT_DIALOG");
                        socket.emit("loginUserInfo");
                    }
                    else {
                        ErrorDialogFragment errorDialogFragment = ErrorDialogFragment.creteDialog(response.getMsg());
                        errorDialogFragment.show(getSupportFragmentManager(), "OTP_VERIFICATION_ERROR_DIALOG");
                    }

                    Log.v("SPLASH_OTP", response.getArabicMsg());
                }
            });
        }
    };

    private Emitter.Listener otpSent = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            waitDialogFragment.dismiss();

            OTPDialogFragment otpDialogFragment = OTPDialogFragment.createDialog();
            otpDialogFragment.setContext(LoginActivity.this);
            otpDialogFragment.setCancelable(false);
            otpDialogFragment.show(getSupportFragmentManager(), "LOGIN_OTP_DIALOG");
        }
    };

    private Emitter.Listener loginSuccess = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject json = (JSONObject)args[0];

                    try {
                        final DatabaseHelper helper = new DatabaseHelper(LoginActivity.this);

                        User user = new Gson().fromJson(json.get("user").toString(), User.class);
                        user.setToken(json.getString("token"));
                        Orders orders = new Orders(json.get("orders").toString());

                        socket.emit("userConnected", user.getId());

                        helper.insert(DatabaseHelper.Tables.USERS, user);

                        for(Order order : orders.getOrders()) {
                            helper.insert(DatabaseHelper.Tables.ORDERS, order);

                            for(OrderService service : order.getServices().getOrderServices()) {
                                helper.insert(DatabaseHelper.Tables.ORDER_SERVICES, service);
                            }

                            for(Feedback feedback : order.getFeedbacks().getFeedbacks()) {
                                helper.insert(DatabaseHelper.Tables.FEEDBACK, feedback);
                            }
                        }

                        final Intent intent;

                        if(user.getUserType().equals("consumer")) {
                            Consumer consumer = new Gson().fromJson(json.get("consumer").toString(), Consumer.class);
                            BankCards cards = new BankCards(json.get("cards").toString());
                            Locations locations = new Locations(json.get("locations").toString());

                            helper.insert(DatabaseHelper.Tables.CONSUMERS, consumer);

                            for(BankCard card : cards.getBankCards()) {
                                helper.insert(DatabaseHelper.Tables.BANK_CARDS, card);
                            }

                            for(Location location : locations.getLocations()) {
                                helper.insert(DatabaseHelper.Tables.LOCATIONS, location);
                            }

                            intent = new Intent(LoginActivity.this, ConsumerMainActivity.class);
                        } else {
                            Driver driver = new Gson().fromJson(json.get("driver").toString(), Driver.class);
                            DeliveryIssues issues = new DeliveryIssues(json.get("deliveryIssues").toString());
                            Locations locations = new Locations(json.get("orderLocations").toString());

                            helper.insert(DatabaseHelper.Tables.DRIVERS, driver);

                            for(DeliveryIssue issue : issues.getDeliveryIssues()) {
                                helper.insert(DatabaseHelper.Tables.DELIVERY_ISSUES, issue);
                            }

                            for(Location location : locations.getLocations()) {
                                helper.insert(DatabaseHelper.Tables.LOCATIONS, location);
                            }

                            intent = new Intent(LoginActivity.this, DriverMainActivity.class);
                        }

                        getServices(user.getToken(), intent, helper);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
}
