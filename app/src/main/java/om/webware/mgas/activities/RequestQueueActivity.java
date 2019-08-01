package om.webware.mgas.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import om.webware.mgas.R;
import om.webware.mgas.api.Location;
import om.webware.mgas.api.Order;
import om.webware.mgas.api.User;

public class RequestQueueActivity extends DriverDrawerBaseActivity {

    private ArrayList<Order> orders;
    private ArrayList<User> users;
    private ArrayList<Location> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayoutActivityContent = findViewById(R.id.frameLayoutActivityContent);
        getLayoutInflater().inflate(R.layout.activity_request_queue, frameLayoutActivityContent);

        TextView textViewTitle = findViewById(R.id.textViewTitle);
        textViewTitle.setText(getString(R.string.orders));
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String queueJson = preferences.getString("REQUEST_QUEUE", "empty");

        if(queueJson.equals("empty")) {
            orders = new ArrayList<>();
            users = new ArrayList<>();
            locations = new ArrayList<>();
        } else {

        }
    }
}
