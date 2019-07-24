package om.webware.mgas.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import om.webware.mgas.R;
import om.webware.mgas.adapters.OrderViewFeedbackRecyclerAdapter;
import om.webware.mgas.adapters.OrderViewServicesRecyclerAdapter;
import om.webware.mgas.api.Feedbacks;
import om.webware.mgas.api.Location;
import om.webware.mgas.api.Locations;
import om.webware.mgas.api.Order;
import om.webware.mgas.api.OrderServices;
import om.webware.mgas.api.Service;
import om.webware.mgas.api.Services;
import om.webware.mgas.api.User;
import om.webware.mgas.fragments.dialogs.ErrorDialogFragment;
import om.webware.mgas.fragments.dialogs.WaitDialogFragment;
import om.webware.mgas.server.MGasApi;
import om.webware.mgas.server.Server;
import om.webware.mgas.tools.DatabaseHelper;

public class OrderViewActivity extends ConsumerDrawerBaseActivity {

    private WaitDialogFragment waitDialogFragment;
    public static final double CLIMB_STAIRS_CHARGE = 2.000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayoutActivityContent = findViewById(R.id.frameLayoutActivityContent);
        getLayoutInflater().inflate(R.layout.activity_order_view, frameLayoutActivityContent);

        TextView textViewLocation = findViewById(R.id.textViewLocation);
        TextView textViewDate = findViewById(R.id.textViewDate);
        TextView textViewStatus = findViewById(R.id.textViewStatus);
        TextView textViewDeliveryOption = findViewById(R.id.textViewDeliveryOption);
        TextView textViewDeliveryOptionCost = findViewById(R.id.textViewDeliveryOptionCost);
        CheckBox checkBoxClimbStairs = findViewById(R.id.checkBoxClimbStairs);
        TextView textViewClimbStairsCost = findViewById(R.id.textViewClimbStairsCost);
        TextView textViewTotalCost = findViewById(R.id.textViewTotalCost);

        boolean rtl = getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
        DatabaseHelper helper = new DatabaseHelper(this);

        User user = (User)helper.select(DatabaseHelper.Tables.USERS, null);

        Order order = new Gson().fromJson(getIntent().getStringExtra("ORDER"), Order.class);

        String whereOrderServices = "where orderId='" + order.getId() + "'";
        OrderServices orderServices = (OrderServices)helper.select(DatabaseHelper.Tables.ORDER_SERVICES, whereOrderServices);

        String whereLocation = "where id='" + order.getLocationId() + "'";
        Locations locations = (Locations)helper.select(DatabaseHelper.Tables.LOCATIONS, whereLocation);
        Location location = locations.getLocations().get(0);

        String whereOption = "where id='" + order.getDeliveryOptionId() + "'";
        Services optionServices = (Services)helper.select(DatabaseHelper.Tables.SERVICES, whereOption);
        Service option = optionServices.getServices().get(0);

        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(order.getOrderDate());
        DecimalFormat decimalFormat = new DecimalFormat("0.000");

        textViewLocation.setText(location.getAddressLine1());
        textViewDate.setText(date);
        textViewDeliveryOptionCost.setText(decimalFormat.format(option.getCharge()));
        checkBoxClimbStairs.setChecked(order.isClimbStairs());

        if(rtl) {
            textViewStatus.setText(order.getArabicStatus());
            textViewDeliveryOption.setText(option.getArabicType());
        } else {
            textViewStatus.setText(order.getStatus());
            textViewDeliveryOption.setText(option.getType());
        }

        DecimalFormat format = new DecimalFormat("0.000");

        String climbStairsCost = format.format(CLIMB_STAIRS_CHARGE) + " " + getString(R.string.OMR);
        String totalCost = format.format(order.getTotalCost()) + " " + getString(R.string.OMR);

        textViewClimbStairsCost.setText(climbStairsCost);
        textViewTotalCost.setText(totalCost);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);

        RecyclerView recyclerViewServices = findViewById(R.id.recyclerViewServices);
        OrderViewServicesRecyclerAdapter servicesAdapter =
                new OrderViewServicesRecyclerAdapter(this, orderServices.getOrderServices());

        recyclerViewServices.setLayoutManager(manager);
        recyclerViewServices.setAdapter(servicesAdapter);

        waitDialogFragment = WaitDialogFragment.createDialog();
        waitDialogFragment.setCancelable(false);
        waitDialogFragment.show(getSupportFragmentManager(), "ORDER_VIEW_WAIT");

        getFeedback(order.getId(), user.getToken());
    }

    private void getFeedback(final String order, final String token) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("authorization", "Bearer " + token);

        Server.request(this, Request.Method.GET, MGasApi.makeUrl(MGasApi.ORDER_FEEDBACK, order), headers,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.startsWith(MGasApi.IISNODE_ERROR)) {
                            getFeedback(order, token);
                        } else {
                            JsonObject json = new Gson().fromJson(response, JsonObject.class);

                            JsonArray authorsJson = json.getAsJsonArray("authors");
                            ArrayList<User> authors = new ArrayList<>();
                            for(JsonElement element : authorsJson) {
                                User author = new Gson().fromJson(element.toString(), User.class);
                                authors.add(author);
                            }

                            json.remove("authors");

                            Feedbacks feedbacks = new Gson().fromJson(json, Feedbacks.class);

                            RecyclerView.LayoutManager manager = new LinearLayoutManager(OrderViewActivity.this);

                            RecyclerView recyclerViewFeedback = findViewById(R.id.recyclerViewFeedback);
                            OrderViewFeedbackRecyclerAdapter adapter =
                                    new OrderViewFeedbackRecyclerAdapter(OrderViewActivity.this,
                                            feedbacks.getFeedbacks(), authors);

                            recyclerViewFeedback.setLayoutManager(manager);
                            recyclerViewFeedback.setAdapter(adapter);

                            waitDialogFragment.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String err = getString(R.string.unable_to_get_order_feedback_error);
                        ErrorDialogFragment errorDialogFragment = ErrorDialogFragment.creteDialog(err);
                        errorDialogFragment.show(getSupportFragmentManager(), "ORDER_VIEW_FEEDBACK_ERROR");

/*                        Log.v("SPLASH_FEEDBACK_ERR", error.networkResponse.statusCode + "");
                        Log.v("SPLASH_FEEDBACK_ERR", new String(error.networkResponse.data, StandardCharsets.UTF_8));*/
                    }
                });
    }
}
