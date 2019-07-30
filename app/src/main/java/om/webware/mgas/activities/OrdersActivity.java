package om.webware.mgas.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import com.google.gson.Gson;

import om.webware.mgas.R;
import om.webware.mgas.adapters.OrdersRecyclerAdapter;
import om.webware.mgas.api.Feedbacks;
import om.webware.mgas.api.Order;
import om.webware.mgas.api.OrderServices;
import om.webware.mgas.api.Orders;
import om.webware.mgas.tools.DatabaseHelper;

public class OrdersActivity extends ConsumerDrawerBaseActivity implements OrdersRecyclerAdapter.OnItemClickListener {

    private DatabaseHelper helper;
    private Orders orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayoutActivityContent = findViewById(R.id.frameLayoutActivityContent);
        getLayoutInflater().inflate(R.layout.activity_orders, frameLayoutActivityContent);

        helper = new DatabaseHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        orders = (Orders) helper.select(DatabaseHelper.Tables.ORDERS, null);

        RecyclerView recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        OrdersRecyclerAdapter adapter = new OrdersRecyclerAdapter(this, orders.getOrders());
        adapter.setOnItemClickListener(this);

        recyclerViewOrders.setLayoutManager(manager);
        recyclerViewOrders.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int index) {
        Order order = orders.getOrders().get(index);

        String whereServices = "where orderId='" + order.getId() + "'";
        OrderServices orderServices = (OrderServices)helper.select(DatabaseHelper.Tables.ORDER_SERVICES, whereServices);

        String whereFeedback = "where orderId='" + order.getId() + "'";
        Feedbacks feedbacks = (Feedbacks)helper.select(DatabaseHelper.Tables.FEEDBACK, whereFeedback);

        order.setServices(orderServices);
        order.setFeedbacks(feedbacks);

        Intent intent = new Intent(this, OrderViewActivity.class);
        intent.putExtra("ORDER", new Gson().toJson(order));
        startActivity(intent);
    }
}
