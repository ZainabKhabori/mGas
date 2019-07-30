package om.webware.mgas.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Locale;

import om.webware.mgas.R;
import om.webware.mgas.api.User;
import om.webware.mgas.server.Server;
import om.webware.mgas.tools.DatabaseHelper;

public class ConsumerDrawerBaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private ImageButton imageViewToggle;

    private DatabaseHelper helper;
    private boolean open = false;

    private String currentLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_drawer_base);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentLanguage = preferences.getString("APP_LANG", Locale.getDefault().getLanguage());

        final boolean rtl = getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
        helper = new DatabaseHelper(this);

        imageViewToggle = findViewById(R.id.imageButtonToggle);

        final int drawableOpen;
        final int drawableClose;
        if(rtl) {
            drawableOpen = R.drawable.half_circle_arrow_left;
            drawableClose = R.drawable.half_circle_arrow_right;
            imageViewToggle.setImageDrawable(ContextCompat.getDrawable(this, drawableOpen));
        } else {
            drawableOpen = R.drawable.half_circle_arrow_right;
            drawableClose = R.drawable.half_circle_arrow_left;
        }

        drawer = findViewById(R.id.drawerLayoutDrawer);
        drawer.setScrimColor(Color.TRANSPARENT);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

                float slideX = drawerView.getWidth() * slideOffset;

                if(rtl) {
                    imageViewToggle.setTranslationX(-slideX);
                } else {
                    imageViewToggle.setTranslationX(slideX);
                }

                if(!open) {
                    imageViewToggle.setImageDrawable(ContextCompat
                            .getDrawable(ConsumerDrawerBaseActivity.this, drawableClose));
                    if(slideOffset == 1) {
                        open = true;
                    }
                } else {
                    imageViewToggle.setImageDrawable(ContextCompat
                            .getDrawable(ConsumerDrawerBaseActivity.this, drawableOpen));
                    if(slideOffset == 0) {
                        open = false;
                    }
                }
            }
        };

        drawer.addDrawerListener(toggle);

        NavigationView navigationView = findViewById(R.id.navigationViewDrawer);

        if(rtl) {
            navigationView.setTextDirection(View.TEXT_DIRECTION_RTL);
        }

        MenuItem services = navigationView.getMenu().findItem(R.id.navItemServices);
        SpannableString spannableString = new SpannableString(services.getTitle());
        spannableString.setSpan(new TextAppearanceSpan(this, R.style.NavSectionHeaderTextColor),
                0, spannableString.length(), 0);
        services.setTitle(spannableString);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
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

        if(user.getUserType().equals("guest")) {
            navigationView.getMenu().findItem(R.id.navItemProfile).setVisible(false);
            navigationView.getMenu().findItem(R.id.navItemOrders).setVisible(false);
            navigationView.getMenu().findItem(R.id.navItemLotteries).setVisible(false);
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
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent;

        String name = getIntent().getStringExtra("NAME") == null? "" : getIntent().getStringExtra("NAME");

        if(id == R.id.navItemHome && !(this instanceof ConsumerMainActivity)) {
            intent = new Intent(this, ConsumerMainActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.navItemProfile && !(this instanceof ConsumerProfileActivity)) {
            intent = new Intent(this, ConsumerProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.navItemOrders && !(this instanceof OrdersActivity)) {
            intent = new Intent(this, OrdersActivity.class);
            startActivity(intent);
        } else if (id == R.id.navItemLotteries && !(this instanceof LotteriesActivity)) {
            intent = new Intent(this, LotteriesActivity.class);
            startActivity(intent);
        } else if (id == R.id.navItemSettings && !(this instanceof SettingsActivity)) {
            intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.navItemTermsAndConditions) {

        } else if(id == R.id.navItemIndustrialGasRefill && (name.equals("") ||
                !name.equals(getString(R.string.menu_industrial_gas_refill)))) {
            intent = new Intent(this, ComingSoonActivity.class);
            intent.putExtra("NAME", getString(R.string.menu_industrial_gas_refill));
            startActivity(intent);
        } else if(id == R.id.navItemCompositeCylinder && (name.equals("") ||
                !name.equals(getString(R.string.menu_composite_cylinder)))) {
            intent = new Intent(this, ComingSoonActivity.class);
            intent.putExtra("NAME", getString(R.string.menu_composite_cylinder));
            startActivity(intent);
        } else if(id == R.id.navItemGasAccessories && (name.equals("") ||
                !name.equals(getString(R.string.gas_accessories_items)))) {
            intent = new Intent(this, ComingSoonActivity.class);
            intent.putExtra("NAME", getString(R.string.gas_accessories_items));
            startActivity(intent);
        } else if(id == R.id.navItemLogout) {
            helper.dropDatabase();
            intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void actionDrawerToggle(View view) {
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }
}
