package om.webware.mgas.activities;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import om.webware.mgas.R;
import om.webware.mgas.tools.LocaleChanger;

public class SettingsActivity extends ConsumerDrawerBaseActivity {

    private LinearLayout linearLayoutArabic;
    private TextView textViewArabic;
    private LinearLayout linearLayoutEnglish;
    private TextView textViewEnglish;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleChanger.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayoutActivityContent = findViewById(R.id.frameLayoutActivityContent);
        getLayoutInflater().inflate(R.layout.activity_settings, frameLayoutActivityContent);

        linearLayoutArabic = findViewById(R.id.linearLayoutArabic);
        linearLayoutEnglish = findViewById(R.id.linearLayoutEnglish);

        textViewArabic = findViewById(R.id.textViewArabic);
        textViewEnglish = findViewById(R.id.textViewEnglish);

        boolean rtl = getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;

        if(rtl) {
            linearLayoutArabic.setBackgroundResource(R.drawable.round_edged_background_primary);
            textViewArabic.setTextColor(ContextCompat.getColor(this, R.color.textColorPrimary));
        } else {
            linearLayoutEnglish.setBackgroundResource(R.drawable.round_edged_background_primary);
            textViewEnglish.setTextColor(ContextCompat.getColor(this, R.color.textColorPrimary));
        }
    }

    public void chooseArabicAction(View view) {
        linearLayoutArabic.setBackgroundResource(R.drawable.round_edged_background_primary);
        textViewArabic.setTextColor(ContextCompat.getColor(this, R.color.textColorPrimary));

        linearLayoutEnglish.setBackgroundResource(0);
        textViewEnglish.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

        Log.v("SPLASH_CHOOSE", "choose arb");

        LocaleChanger.changeLanguage(this, "ar");
        recreate();
    }

    public void chooseEnglishAction(View view) {
        linearLayoutEnglish.setBackgroundResource(R.drawable.round_edged_background_primary);
        textViewEnglish.setTextColor(ContextCompat.getColor(this, R.color.textColorPrimary));

        linearLayoutArabic.setBackgroundResource(0);
        textViewArabic.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

        Log.v("SPLASH_CHOOSE", "choose eng");

        LocaleChanger.changeLanguage(this, "en");
        recreate();
    }
}
