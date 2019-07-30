package om.webware.mgas.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Locale;

import om.webware.mgas.R;
import om.webware.mgas.api.User;
import om.webware.mgas.server.Server;
import om.webware.mgas.tools.DatabaseHelper;
import om.webware.mgas.tools.GPSTracker;

public class DriverDrawerBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private DatabaseHelper helper;

    private String currentLanguage;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_drawer_base);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentLanguage = preferences.getString("APP_LANG", Locale.getDefault().getLanguage());

        Toolbar toolbarDriver = findViewById(R.id.toolbarDriver);
        setSupportActionBar(toolbarDriver);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = findViewById(R.id.drawerLayoutDriverMain);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbarDriver, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationViewDriver = findViewById(R.id.navigationViewDriver);
        navigationViewDriver.setNavigationItemSelectedListener(this);

        helper = new DatabaseHelper(this);

        View header = navigationViewDriver.getHeaderView(0);
        ImageView imageViewDp = header.findViewById(R.id.imageViewDp);
        TextView textViewUsername = header.findViewById(R.id.textViewUsername);

        User user = (User)helper.select(DatabaseHelper.Tables.USERS, null);

        String username = user.getfName() + " " + user.getlName();
        textViewUsername.setText(username);

        if(user.getDisplayPicThumb() != null && user.getDisplayPicUrl() != null) {
            if(Server.getNetworkAvailability(this)) {
                Picasso.get().load(user.getDisplayPicUrl()).into(imageViewDp);
            } else {
                byte[] thumb = user.getDisplayPicThumb();
                Bitmap bitmap = BitmapFactory.decodeByteArray(thumb, 0, thumb.length);
                int width = imageViewDp.getWidth();
                int height = imageViewDp.getHeight();
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, width, height, false);
                imageViewDp.setImageBitmap(scaled);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(!currentLanguage.equals(preferences.getString("APP_LANG", Locale.getDefault().getLanguage()))) {
            currentLanguage = preferences.getString("APP_LANG", Locale.getDefault().getLanguage());
            recreate();
        }
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Intent intent;
        switch(menuItem.getItemId()) {
            case R.id.navItemProfile:
                break;
            case R.id.navItemIncomingRequests:
                if(!(this instanceof DriverMainActivity)) {
                    intent = new Intent(this, DriverMainActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.navItemRequestQueue:
                if(!(this instanceof RequestQueueActivity)) {
                    intent = new Intent(this, RequestQueueActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.navItemAllRequests:
                if(!(this instanceof DriverOrdersActivity)) {
                    intent = new Intent(this, DriverOrdersActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.navItemReportedIssues:
                break;
            case R.id.navItemSettings:
                break;
            case R.id.navItemTermsAndConditions:
                break;
            case R.id.navItemLogout:
                helper.dropDatabase();
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
