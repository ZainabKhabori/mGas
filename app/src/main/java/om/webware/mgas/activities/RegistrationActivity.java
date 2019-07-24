package om.webware.mgas.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import com.google.gson.Gson;
import com.rd.PageIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import om.webware.mgas.R;
import om.webware.mgas.adapters.RegistrationPagerAdapter;
import om.webware.mgas.api.Consumer;
import om.webware.mgas.api.Location;
import om.webware.mgas.api.User;
import om.webware.mgas.customViews.LockableViewPager;
import om.webware.mgas.fragments.dialogs.ErrorDialogFragment;
import om.webware.mgas.fragments.dialogs.OTPDialogFragment;
import om.webware.mgas.fragments.dialogs.WaitDialogFragment;
import om.webware.mgas.fragments.pager.RegistrationPagerInfoFragment;
import om.webware.mgas.fragments.pager.RegistrationPagerLocationFragment;
import om.webware.mgas.server.MGasSocket;
import om.webware.mgas.server.SocketResponse;
import om.webware.mgas.tools.DatabaseHelper;
import om.webware.mgas.tools.SavedObjects;

@SuppressWarnings("ConstantConditions")
public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private Socket socket;
    private LockableViewPager viewPagerRegistration;

    private RegistrationPagerLocationFragment locationFragment;
    private RegistrationPagerInfoFragment infoFragment;

    private WaitDialogFragment waitDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        viewPagerRegistration = findViewById(R.id.viewPagerRegistration);
        PageIndicatorView pageIndicatorViewRegistration = findViewById(R.id.pageIndicatorViewRegistration);
        RegistrationPagerAdapter adapter = new RegistrationPagerAdapter(getSupportFragmentManager(), this);

        viewPagerRegistration.setAdapter(adapter);
        pageIndicatorViewRegistration.setViewPager(viewPagerRegistration);

        viewPagerRegistration.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                infoFragment = (RegistrationPagerInfoFragment)getSupportFragmentManager().getFragments().get(0);
                locationFragment = (RegistrationPagerLocationFragment)getSupportFragmentManager().getFragments().get(1);
                locationFragment.getButtonConfirm().setOnClickListener(RegistrationActivity.this);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        socket = MGasSocket.socket;
        socket.on("consumerRegistrationError", consumerRegistrationError);
        socket.on("otpSent", otpSent);
        socket.on("otpVerificationDone", otpVerificationDone);
        socket.on("consumerRegistrationSuccess", consumerRegistrationSuccess);
        socket.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        socket.off();
    }

    @Override
    public void onClick(View v) {
        JSONObject details = new JSONObject();
        try {
            details.put("fname", infoFragment.getEditTextFirstName().getText().toString());
            details.put("lname", infoFragment.getEditTextLastName().getText().toString());
            details.put("mobileNo", Integer.parseInt(infoFragment.getEditTextMobile().getText().toString()));
            details.put("password", infoFragment.getEditTextPassword().getText().toString());
            details.put("email", infoFragment.getEditTextEmail().getText().toString());
            details.put("idNo", Integer.parseInt(infoFragment.getEditTextIdNo().getText().toString()));

            String address = locationFragment.getEditTextCountry().getText().toString() + ", " +
                    locationFragment.getEditTextGovernorate().getText().toString() + ", " +
                    locationFragment.getEditTextProvince().getText().toString() + ", " +
                    locationFragment.getEditTextCity().getText().toString() + ", " +
                    locationFragment.getEditTextStreet().getText().toString() + ", " +
                    locationFragment.getEditTextWay().getText().toString() + ", " +
                    locationFragment.getEditTextBuilding().getText().toString() + " " + getString(R.string.building);

            details.put("locName", locationFragment.getEditTextName().getText().toString());
            details.put("addressLine1", address);
            details.put("longitude", locationFragment.getLng());
            details.put("latitude", locationFragment.getLat());

            waitDialogFragment = WaitDialogFragment.createDialog();
            waitDialogFragment.setCancelable(false);
            waitDialogFragment.show(getSupportFragmentManager(), "REGISTRATION_WAIT_DIALOG");

            socket.emit("consumerRegister", details.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Emitter.Listener consumerRegistrationError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject json = (JSONObject)args[0];
                    SocketResponse response = new Gson().fromJson(json.toString(), SocketResponse.class);

                    ErrorDialogFragment errorDialogFragment = ErrorDialogFragment.creteDialog(response.getMsg());
                    errorDialogFragment.show(getSupportFragmentManager(), "ERROR_DIALOG");

                    infoFragment.getWaitDialogFragment().dismiss();
                }
            });
        }
    };

    private Emitter.Listener otpSent = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    OTPDialogFragment otpDialogFragment = OTPDialogFragment.createDialog();
                    otpDialogFragment.setContext(RegistrationActivity.this);
                    otpDialogFragment.setCancelable(false);
                    otpDialogFragment.show(getSupportFragmentManager(), "REGISTER_OTP_DIALOG");
                }
            });
        }
    };

    private Emitter.Listener otpVerificationDone = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject json = (JSONObject)args[0];
                    SocketResponse response = new Gson().fromJson(json.toString(), SocketResponse.class);

                    if(response.getStatusCode() == 200) {
                        infoFragment.getWaitDialogFragment().dismiss();
                        viewPagerRegistration.setLocked(false);
                        viewPagerRegistration.setCurrentItem(2);
                    }
                    else {
                        ErrorDialogFragment errorDialogFragment = ErrorDialogFragment.creteDialog(response.getMsg());
                        errorDialogFragment.show(getSupportFragmentManager(), "OTP_VERIFICATION_ERROR_DIALOG");
                        infoFragment.getButtonSendOTP().setEnabled(true);
                    }
                }
            });
        }
    };

    private Emitter.Listener consumerRegistrationSuccess = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject json = (JSONObject)args[0];

                    try {
                        User user = new Gson().fromJson(json.get("user").toString(), User.class);
                        user.setToken(json.getString("token"));
                        Consumer consumer = new Gson().fromJson(json.get("consumer").toString(), Consumer.class);
                        Location location = new Gson().fromJson(json.get("mainLocation").toString(), Location.class);

                        DatabaseHelper helper = new DatabaseHelper(RegistrationActivity.this);
                        helper.insert(DatabaseHelper.Tables.USERS, user);
                        helper.insert(DatabaseHelper.Tables.CONSUMERS, consumer);
                        helper.insert(DatabaseHelper.Tables.LOCATIONS, location);

                        SavedObjects.getSavedObjects().remove("CHOOSE_LOC_DIALOG_VIEW");
                        SavedObjects.getSavedObjects().remove("CHOOSE_LOC_LAT");
                        SavedObjects.getSavedObjects().remove("CHOOSE_LOC_LNG");

                        waitDialogFragment.dismiss();

                        Intent intent = new Intent(RegistrationActivity.this, ConsumerMainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
}
