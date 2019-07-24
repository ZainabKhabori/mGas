package om.webware.mgas.activities;

import android.animation.ObjectAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.TextView;

import om.webware.mgas.R;

public class RequestQueueActivity extends DriverDrawerBaseActivity {

    private ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayoutActivityContent = findViewById(R.id.frameLayoutActivityContent);
        getLayoutInflater().inflate(R.layout.activity_request_queue, frameLayoutActivityContent);

        TextView textViewTitle = findViewById(R.id.textViewTitle);
        textViewTitle.setText(getString(R.string.orders));
    }
}
