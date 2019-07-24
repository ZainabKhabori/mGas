package om.webware.mgas.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Locale;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import om.webware.mgas.R;
import om.webware.mgas.adapters.CartRecyclerAdapter;
import om.webware.mgas.api.Feedback;
import om.webware.mgas.api.Feedbacks;
import om.webware.mgas.api.Order;
import om.webware.mgas.api.OrderService;
import om.webware.mgas.api.OrderServices;
import om.webware.mgas.api.Orders;
import om.webware.mgas.api.Services;
import om.webware.mgas.fragments.dialogs.CheckoutDialogFragment;
import om.webware.mgas.fragments.dialogs.ErrorDialogFragment;
import om.webware.mgas.server.MGasSocket;
import om.webware.mgas.server.SocketResponse;
import om.webware.mgas.tools.DatabaseHelper;

public class CartActivity extends AppCompatActivity implements CartRecyclerAdapter.OnItemClickListener {

    private TextView textViewTotalCost;
    private Button buttonCheckout;
    private Order order;
    private CartRecyclerAdapter adapter;
    private OnItemDeletedListener onItemDeletedListener;
    private Socket socket;
    private CheckoutDialogFragment fragment;
    private DatabaseHelper helper;

    public interface OnItemDeletedListener { void  onItemDeleted(int index, double totalCost); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        socket = MGasSocket.socket;

        CheckBox checkBoxClimbStairs = findViewById(R.id.checkBoxClimbStairs);
        TextView textViewClimbStairsCost = findViewById(R.id.textViewClimbStairsCost);
        TextView textViewDeliveryOption = findViewById(R.id.textViewDeliveryOption);
        TextView textViewDeliveryOptionCost = findViewById(R.id.textViewDeliveryOptionCost);
        RecyclerView recyclerViewOrderItems = findViewById(R.id.recyclerViewOrderItems);
        buttonCheckout = findViewById(R.id.buttonCheckout);
        textViewTotalCost = findViewById(R.id.textViewTotalCost);

        order = new Gson().fromJson(getIntent().getStringExtra("ORDER"), Order.class);
        onItemDeletedListener = (OnItemDeletedListener)ConsumerMainActivity.ACTIVITY;

        helper = new DatabaseHelper(this);
        String where = "where id='" + order.getDeliveryOptionId() + "'";
        Services services = (Services)helper.select(DatabaseHelper.Tables.SERVICES, where);

        checkBoxClimbStairs.setChecked(order.isClimbStairs());
        textViewDeliveryOption.setText(services.getServices().get(0).getType());

        DecimalFormat format = (DecimalFormat)DecimalFormat.getInstance(Locale.US);
        format.applyPattern("0.000");

        if(order.isClimbStairs()) {
            String stairsCost = format.format(ConsumerMainActivity.CLIMB_STAIRS_CHARGE) + " " + getString(R.string.OMR);
            textViewClimbStairsCost.setText(stairsCost);
        } else {
            String stairsCost = "0.000 " + getString(R.string.OMR);
            textViewClimbStairsCost.setText(stairsCost);
        }

        double methodCost = services.getServices().get(0).getCharge();
        String methodCostFormatted = format.format(methodCost) + " " + getString(R.string.OMR);
        textViewDeliveryOptionCost.setText(methodCostFormatted);

        adapter = new CartRecyclerAdapter(this, order.getServices().getOrderServices());
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        adapter.setOnItemClickListener(this);
        recyclerViewOrderItems.setAdapter(adapter);
        recyclerViewOrderItems.setLayoutManager(manager);

        String totalCost = format.format(order.getTotalCost()) + " " + getString(R.string.OMR);
        textViewTotalCost.setText(totalCost);

        if(order.getServices().getOrderServices().size() == 0) {
            buttonCheckout.setEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        socket.on("orderSent", orderSent);
        socket.on("orderRequestedError", orderRequestedError);
    }

    public void addMoreAction(View view) {
        finish();
    }

    public void checkoutAction(View view) {
        String loc = getIntent().getStringExtra("DELIVERY_LOC");
        fragment = CheckoutDialogFragment.createDialog(loc, new Gson().toJson(order));
        fragment.show(getSupportFragmentManager(), "CHECKOUT_DIALOG");
    }

    @Override
    public void onItemClick(View view, int index, double charge) {
        order.getServices().getOrderServices().remove(index);
        adapter.notifyItemRemoved(index);

        double cost = !order.getServices().getOrderServices().isEmpty()? order.getTotalCost() - charge : 0;
        String totalCost = new DecimalFormat("0.000").format(cost) + " " + getString(R.string.OMR);
        textViewTotalCost.setText(totalCost);
        buttonCheckout.setEnabled(!order.getServices().getOrderServices().isEmpty());

        onItemDeletedListener.onItemDeleted(index, cost);
    }

    private Emitter.Listener orderSent = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JsonObject json = new Gson().fromJson(args[0].toString(), JsonObject.class);
                    OrderServices services = new OrderServices(json.get("services").toString());
                    Feedbacks feedbacks = new Feedbacks(json.get("feedbacks").toString());
                    json.remove("services");
                    json.remove("feedbacks");
                    Order order = new Gson().fromJson(json.toString(), Order.class);
                    order.setServices(services);
                    order.setFeedbacks(feedbacks);

                    for(OrderService service: order.getServices().getOrderServices()) {
                        helper.insert(DatabaseHelper.Tables.ORDER_SERVICES, service);
                    }

                    for(Feedback feedback : order.getFeedbacks().getFeedbacks()) {
                        helper.insert(DatabaseHelper.Tables.FEEDBACK, feedback);
                    }

                    helper.insert(DatabaseHelper.Tables.ORDERS, order);
                    fragment.getWaitDialogFragment().dismiss();
                    Toast.makeText(CartActivity.this, getString(R.string.order_placed), Toast.LENGTH_LONG).show();

                    finish();
                }
            });
        }
    };

    private Emitter.Listener orderRequestedError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject res = (JSONObject)args[0];
                    SocketResponse response = new Gson().fromJson(res.toString(), SocketResponse.class);

                    fragment.getWaitDialogFragment().dismiss();

                    ErrorDialogFragment errorDialogFragment = ErrorDialogFragment.creteDialog(response.getMsg());
                    errorDialogFragment.show(getSupportFragmentManager(), "ERROR_DIALOG");
                }
            });
        }
    };
}
