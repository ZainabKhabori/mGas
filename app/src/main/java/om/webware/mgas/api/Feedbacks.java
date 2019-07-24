package om.webware.mgas.api;

import android.database.Cursor;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class Feedbacks {

    private ArrayList<Feedback> feedbacks;

    public Feedbacks(Cursor cursor) {
        feedbacks = new ArrayList<>();

        while(cursor.moveToNext()) {
            String id = cursor.getString(0);
            String orderId = cursor.getString(1);
            String author = cursor.getString(2);
            String authorUserType = cursor.getString(3);
            String message = cursor.getString(4);

            Feedback feedback = new Feedback(id, orderId, author, authorUserType, message);
            feedbacks.add(feedback);
        }
    }

    public Feedbacks(String json) {
        Feedback[] array = new Gson().fromJson(json, Feedback[].class);
        feedbacks = new ArrayList<>(Arrays.asList(array));
    }

    public ArrayList<Feedback> getFeedbacks() {
        return feedbacks;
    }
}
