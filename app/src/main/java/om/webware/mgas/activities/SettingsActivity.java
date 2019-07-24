package om.webware.mgas.activities;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import om.webware.mgas.R;

public class SettingsActivity extends AppCompatActivity {

    private LinearLayout linearLayoutArabic;
    private TextView textViewArabic;
    private LinearLayout linearLayoutEnglish;
    private TextView textViewEnglish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        linearLayoutArabic = findViewById(R.id.linearLayoutArabic);
        linearLayoutEnglish = findViewById(R.id.linearLayoutEnglish);

        textViewArabic = findViewById(R.id.textViewArabic);
        textViewEnglish = findViewById(R.id.textViewEnglish);
    }

    public void chooseArabicAction(View view) {
        linearLayoutArabic.setBackgroundResource(R.drawable.round_edged_background_primary);
        textViewArabic.setTextColor(ContextCompat.getColor(this, R.color.textColorPrimary));

        linearLayoutEnglish.setBackgroundResource(0);
        textViewEnglish.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }

    public void chooseEnglishAction(View view) {
        linearLayoutEnglish.setBackgroundResource(R.drawable.round_edged_background_primary);
        textViewEnglish.setTextColor(ContextCompat.getColor(this, R.color.textColorPrimary));

        linearLayoutArabic.setBackgroundResource(0);
        textViewArabic.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }


}
