package om.webware.mgas.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import om.webware.mgas.R;
import om.webware.mgas.api.Consumer;
import om.webware.mgas.api.Location;
import om.webware.mgas.api.Locations;
import om.webware.mgas.api.User;
import om.webware.mgas.customViews.CircularImageView;
import om.webware.mgas.server.Server;
import om.webware.mgas.tools.DatabaseHelper;

public class ConsumerProfileActivity extends ConsumerDrawerBaseActivity {

    private CircularImageView imageViewDp;
    private TextView textViewName;
    private EditText editTextEmail;
    private EditText editTextPhone;
    private TextView textViewIdNo;
    private EditText editTextBirthday;
    private TextView textViewAge;
    private ImageView imageViewSelectedGender;
    private RadioGroup radioGroupGender;
    private RadioButton radioButtonMale;
    private RadioButton radioButtonFemale;
    private RadioButton radioButtonUnspecified;
    private TextView textViewLocation;
    private EditText editTextLocationName;
    private FloatingActionButton floatingActionButtonEdit;
    private ImageButton imageButtonConfirm;
    private ImageButton imageButtonCancel;

    private DatabaseHelper helper;
    private User user;
    private Consumer consumer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayoutActivityContent = findViewById(R.id.frameLayoutActivityContent);
        getLayoutInflater().inflate(R.layout.activity_consumer_profile, frameLayoutActivityContent);

        imageViewDp = findViewById(R.id.imageViewDp);
        textViewName = findViewById(R.id.textViewName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        textViewIdNo = findViewById(R.id.textViewIdNo);
        editTextBirthday = findViewById(R.id.editTextBirthday);
        textViewAge = findViewById(R.id.textViewAge);
        imageViewSelectedGender = findViewById(R.id.imageViewSelectedGender);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        radioButtonMale = findViewById(R.id.radioButtonMale);
        radioButtonFemale = findViewById(R.id.radioButtonFemale);
        radioButtonUnspecified = findViewById(R.id.radioButtonUnspecified);
        textViewLocation = findViewById(R.id.textViewLocation);
        editTextLocationName = findViewById(R.id.editTextLocationName);
        floatingActionButtonEdit = findViewById(R.id.floatingActionButtonEdit);
        imageButtonConfirm = findViewById(R.id.imageButtonConfirm);
        imageButtonCancel = findViewById(R.id.imageButtonCancel);

        helper = new DatabaseHelper(this);
        user = (User)helper.select(DatabaseHelper.Tables.USERS, null);
        consumer = (Consumer)helper.select(DatabaseHelper.Tables.CONSUMERS, null);

        String whereLocation = "where id='" + consumer.getMainLocationId() + "'";
        Locations locations = (Locations)helper.select(DatabaseHelper.Tables.LOCATIONS, whereLocation);
        Location location = locations.getLocations().get(0);

        String name = user.getfName() + " " + user.getlName();
        textViewName.setText(name);

        editTextEmail.setText(user.getEmail());
        editTextPhone.setText(String.valueOf(user.getMobileNo()));
        textViewIdNo.setText(String.valueOf(user.getIdNo()));
        textViewLocation.setText(location.getAddressLine1());
        editTextLocationName.setText(consumer.getMainLocationName());

        if(user.getDisplayPicUrl() != null && user.getDisplayPicThumb() != null) {
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

        if(consumer.getDateOfBirth() != null && consumer.getAge() != 0) {
            String birthday = new SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(consumer.getDateOfBirth());
            editTextBirthday.setText(birthday);
            textViewAge.setText(String.valueOf(consumer.getAge()));
        }

        if((int)consumer.getGender() != 0) {
            String gender = new String(new char[] {consumer.getGender()});
            if(gender.toLowerCase().equals("m")) {
                imageViewSelectedGender.setImageResource(R.drawable.ic_male);
            } else {
                imageViewSelectedGender.setImageResource(R.drawable.ic_female);
            }
        }
    }

    public void editAction(View view) {
        ArrayList<TextView> textViews = new ArrayList<>();
        textViews.add(textViewName);
        textViews.add(editTextEmail);
        textViews.add(editTextPhone);
        textViews.add(editTextBirthday);
        textViews.add(textViewLocation);
        textViews.add(editTextLocationName);

        for(TextView textView : textViews) {
            textView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            textView.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.textColorPrimary));
            textView.setEnabled(true);
        }

        imageViewDp.setBorderColor(ContextCompat.getColor(this, R.color.colorAccent));
        imageViewDp.setBorderThickness(10);

        floatingActionButtonEdit.hide();
        imageButtonConfirm.setVisibility(View.VISIBLE);
        imageButtonCancel.setVisibility(View.VISIBLE);
    }

    public void confirmAction(View view) {
    }

    public void cancelAction(View view) {
        ArrayList<TextView> textViews = new ArrayList<>();
        textViews.add(textViewName);
        textViews.add(editTextEmail);
        textViews.add(editTextPhone);
        textViews.add(editTextBirthday);
        textViews.add(textViewLocation);
        textViews.add(editTextLocationName);

        for(TextView textView : textViews) {
            textView.setTextColor(ContextCompat.getColor(this, R.color.textColorPrimary));
            textView.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.transparent));
            textView.setEnabled(false);
        }

        imageViewDp.setBorderColor(Color.TRANSPARENT);
        imageViewDp.setBorderThickness(0);

        floatingActionButtonEdit.show();
        imageButtonConfirm.setVisibility(View.GONE);
        imageButtonCancel.setVisibility(View.GONE);
    }
}
