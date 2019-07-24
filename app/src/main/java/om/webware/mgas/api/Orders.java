package om.webware.mgas.api;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Zainab on 5/17/2019.
 */

public class Orders {

    private ArrayList<Order> orders;

    @SuppressLint("SimpleDateFormat")
    public Orders(Cursor cursor) {
        orders = new ArrayList<>();

        while (cursor.moveToNext()) {
            try {
                String id = cursor.getString(0);
                String consumerId = cursor.getString(1);
                String driverId = cursor.getString(2);
                String locationId = cursor.getString(3);

                Date orderDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .parse(cursor.getString(4));
                String deliveryOptionId = cursor.getString(5);
                boolean climbStairs = Boolean.valueOf(cursor.getInt(6) + "");
                double totalCost = cursor.getDouble(7);
                String status = cursor.getString(8);
                String arabicStatus = cursor.getString(9);

                Order order = new Order(id, consumerId, driverId, locationId, orderDate, deliveryOptionId,
                        climbStairs, totalCost, status, arabicStatus, null, null);
                orders.add(order);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Orders(String json) {
        try {
            JSONArray jsonArray = new JSONArray(json);
            orders = new ArrayList<>();

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                OrderServices services = new OrderServices(object.get("services").toString());
                Feedbacks feedbacks = new Feedbacks(object.get("feedbacks").toString());

                object.remove("services");
                object.remove("feedbacks");

                Order order = new Gson().fromJson(object.toString(), Order.class);
                order.setServices(services);
                order.setFeedbacks(feedbacks);

                orders.add(order);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }
}
