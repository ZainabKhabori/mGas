package om.webware.mgas.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import om.webware.mgas.R;

public class ComingSoonActivity extends ConsumerDrawerBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayoutActivityContent = findViewById(R.id.frameLayoutActivityContent);
        getLayoutInflater().inflate(R.layout.activity_coming_soon, frameLayoutActivityContent);
    }
}
