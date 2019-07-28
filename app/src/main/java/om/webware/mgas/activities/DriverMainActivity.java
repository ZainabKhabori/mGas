package om.webware.mgas.activities;

import android.Manifest;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import om.webware.mgas.R;
import om.webware.mgas.adapters.IncomingOrdersRecyclerAdapter;
import om.webware.mgas.api.Feedbacks;
import om.webware.mgas.api.Order;
import om.webware.mgas.api.OrderServices;
import om.webware.mgas.api.User;
import om.webware.mgas.server.MGasSocket;
import om.webware.mgas.tools.DatabaseHelper;
import om.webware.mgas.tools.GPSTracker;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class DriverMainActivity extends DriverDrawerBaseActivity implements NavigationView.OnNavigationItemSelectedListener,
        GPSTracker.OnUserLocationChangedListener, IncomingOrdersRecyclerAdapter.OnItemClickListener {

    private Socket socket;

    private DatabaseHelper helper;
    private User user;

    private IncomingOrdersRecyclerAdapter adapter;
    private ArrayList<Order> orders;
    private ArrayList<User> users;
    private ArrayList<om.webware.mgas.api.Location> locations;

    private ProgressBar progressBarWait;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayoutActivityContent = findViewById(R.id.frameLayoutActivityContent);
        getLayoutInflater().inflate(R.layout.activity_driver_main, frameLayoutActivityContent);

        progressBarWait = findViewById(R.id.progressBarWait);

        TextView textViewTitle = findViewById(R.id.textViewTitle);
        textViewTitle.setText(getString(R.string.orders));

        DriverMainActivityPermissionsDispatcher.setupGPSWithPermissionCheck(this);

        helper = new DatabaseHelper(this);
        user = (User)helper.select(DatabaseHelper.Tables.USERS, null);

        RecyclerView recyclerViewIncomingOrders = findViewById(R.id.recyclerViewIncomingOrders);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        DividerItemDecoration decoration = new DividerItemDecoration(recyclerViewIncomingOrders.getContext(),
                manager.getOrientation());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

/*        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();*/

        String ordersJson = preferences.getString("INCOMING_ORDERS", "empty");
        String userJson = preferences.getString("INCOMING_ORDERS_USERS", "empty");
        String locationsJson = preferences.getString("INCOMING_ORDERS_LOCATIONS", "empty");

        if(!ordersJson.equals("empty") && !userJson.equals("empty") && !locationsJson.equals("empty")) {
            Type ordersType = new TypeToken<ArrayList<Order>>(){}.getType();
            Type usersType = new TypeToken<ArrayList<User>>(){}.getType();
            Type locationsType = new TypeToken<ArrayList<om.webware.mgas.api.Location>>(){}.getType();

            orders = new Gson().fromJson(ordersJson, ordersType);
            users = new Gson().fromJson(userJson, usersType);
            locations = new Gson().fromJson(locationsJson, locationsType);
        } else {
            orders = new ArrayList<>();
            users = new ArrayList<>();
            locations = new ArrayList<>();
        }

        adapter = new IncomingOrdersRecyclerAdapter(this, orders, users, locations);
        adapter.setOnItemClickListener(this);

        recyclerViewIncomingOrders.setLayoutManager(manager);
        recyclerViewIncomingOrders.addItemDecoration(decoration);
        recyclerViewIncomingOrders.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        socket = MGasSocket.socket;
        socket.emit("clientOnline", user.getId());
        socket.emit("driverStatusChanged", user.getId(), "online");
        socket.on("orderReceived", orderReceived);
    }

    @Override
    protected void onPause() {
        super.onPause();
        socket.emit("driverStatusChanged", user.getId(), "busy");

        String ordersJson = new Gson().toJson(orders);
        String usersJson = new Gson().toJson(users);
        String locationsJson = new Gson().toJson(locations);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("INCOMING_ORDERS", ordersJson);
        editor.putString("INCOMING_ORDERS_USERS", usersJson);
        editor.putString("INCOMING_ORDERS_LOCATIONS", locationsJson);

        editor.apply();
    }

    @Override
    public void onUserLocationChanged(Location location) {
        if(progressBarWait.getVisibility() != View.GONE) {
            progressBarWait.setVisibility(View.GONE);
            Toast.makeText(this, "You are connected", Toast.LENGTH_LONG).show();
        }

        double lat = location.getLatitude();
        double lng = location.getLongitude();

        if(socket.connected()) {
            socket.emit("driverLocationChanged", user.getId(), lat, lng);
        }
    }

    @Override
    public void onItemClick(View view, int index) {

    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void setupGPS() {
        GPSTracker tracker = new GPSTracker(this);
        tracker.setOnUserLocationChangedListener(this);
    }

    private void acceptOrderAction() {

    }

    private Emitter.Listener orderReceived = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JsonObject json = new Gson().fromJson(args[0].toString(), JsonObject.class);
                    OrderServices services = new OrderServices(json.getAsJsonObject("order").get("services").toString());
                    Feedbacks feedbacks = new Feedbacks(json.getAsJsonObject("order").get("feedbacks").toString());
                    json.getAsJsonObject("order").remove("services");
                    json.getAsJsonObject("order").remove("feedbacks");
                    Order order = new Gson().fromJson(json.getAsJsonObject("order").toString(), Order.class);
                    order.setServices(services);
                    order.setFeedbacks(feedbacks);
                    User consumerInfo = new Gson().fromJson(json.getAsJsonObject("consumerInfo").toString(), User.class);
                    om.webware.mgas.api.Location loc = new Gson().fromJson(json.getAsJsonObject("orderLocation").toString(),
                            om.webware.mgas.api.Location.class);

                    Log.v("SPLASH_ORDERS", orders.size() + "");

                    orders.add(order);
                    users.add(consumerInfo);
                    locations.add(loc);

                    adapter.notifyItemInserted(orders.size() - 1);
                }
            });
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        DriverMainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
