package om.webware.mgas.api;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class DeliveryIssues {

    private ArrayList<DeliveryIssue> deliveryIssues;

    @SuppressLint("SimpleDateFormat")
    public DeliveryIssues(Cursor cursor) {
        deliveryIssues = new ArrayList<>();

        while (cursor.moveToNext()) {
            try {
                String id = cursor.getString(0);
                String driverId = cursor.getString(1);
                String orderId = cursor.getString(2);
                Date dateReported = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .parse(cursor.getString(3));
                String issue = cursor.getString(4);

                DeliveryIssue deliveryIssue = new DeliveryIssue(id, driverId, orderId, dateReported, issue);
                deliveryIssues.add(deliveryIssue);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public DeliveryIssues(String json) {
        DeliveryIssue[] array = new Gson().fromJson(json, DeliveryIssue[].class);
        deliveryIssues = new ArrayList<>(Arrays.asList(array));
    }

    public ArrayList<DeliveryIssue> getDeliveryIssues() {
        return deliveryIssues;
    }
}
