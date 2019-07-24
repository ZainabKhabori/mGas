package om.webware.mgas.api;

import android.database.Cursor;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Zainab on 5/17/2019.
 */

public class OrderServices {

    private ArrayList<OrderService> orderServices;

    public OrderServices(Cursor cursor) {
        orderServices = new ArrayList<>();

        while (cursor.moveToNext()) {
            String orderId = cursor.getString(0);
            String serviceId = cursor.getString(1);
            int quantity = cursor.getInt(2);

            OrderService orderService = new OrderService(orderId, serviceId, quantity);
            orderServices.add(orderService);
        }
    }

    public OrderServices(String json) {
        OrderService[] array = new Gson().fromJson(json, OrderService[].class);
        orderServices = new ArrayList<>(Arrays.asList(array));
    }

    public ArrayList<OrderService> getOrderServices() {
        return orderServices;
    }
}
