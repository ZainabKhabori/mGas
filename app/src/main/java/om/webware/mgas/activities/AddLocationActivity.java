package om.webware.mgas.activities;

import android.Manifest;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;

import om.webware.mgas.R;
import om.webware.mgas.api.Location;
import om.webware.mgas.api.User;
import om.webware.mgas.fragments.dialogs.ChooseLocDialogFragment;
import om.webware.mgas.fragments.dialogs.ErrorDialogFragment;
import om.webware.mgas.fragments.dialogs.WaitDialogFragment;
import om.webware.mgas.server.MGasApi;
import om.webware.mgas.server.Server;
import om.webware.mgas.tools.DatabaseHelper;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class AddLocationActivity extends AppCompatActivity implements ChooseLocDialogFragment.OnDismissListener, TextWatcher {

    private RelativeLayout relativeLayoutDetails;
    private EditText editTextName;
    private EditText editTextCountry;
    private EditText editTextGovernorate;
    private EditText editTextProvince;
    private EditText editTextCity;
    private EditText editTextStreet;
    private EditText editTextWay;
    private EditText editTextBuilding;
    private Button buttonConfirm;

    private double lat;
    private double lng;

    private WaitDialogFragment waitDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        relativeLayoutDetails = findViewById(R.id.relativeLayoutDetails);
        editTextName = findViewById(R.id.editTextName);
        editTextCountry = findViewById(R.id.editTextCountry);
        editTextGovernorate = findViewById(R.id.editTextGovernorate);
        editTextProvince = findViewById(R.id.editTextProvince);
        editTextCity = findViewById(R.id.editTextCity);
        editTextStreet = findViewById(R.id.editTextStreet);
        editTextWay = findViewById(R.id.editTextWay);
        editTextBuilding = findViewById(R.id.editTextBuilding);
        buttonConfirm = findViewById(R.id.buttonConfirm);

        editTextName.addTextChangedListener(this);
        editTextCountry.addTextChangedListener(this);
        editTextGovernorate.addTextChangedListener(this);
        editTextProvince.addTextChangedListener(this);
        editTextCity.addTextChangedListener(this);
        editTextStreet.addTextChangedListener(this);
        editTextWay.addTextChangedListener(this);
        editTextBuilding.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String name = editTextName.getText().toString().trim();
        String country = editTextCountry.getText().toString().trim();
        String governorate = editTextGovernorate.getText().toString().trim();
        String province = editTextProvince.getText().toString().trim();
        String city = editTextCity.getText().toString().trim();
        String street = editTextStreet.getText().toString().trim();
        String way = editTextWay.getText().toString().trim();
        String building = editTextBuilding.getText().toString().trim();

        boolean allFilled = !name.isEmpty() && !country.isEmpty() && !governorate.isEmpty() &&
                !province.isEmpty() && !city.isEmpty() && !street.isEmpty() && !way.isEmpty() &&
                !building.isEmpty();

        buttonConfirm.setEnabled(allFilled);
    }

    @Override
    public void afterTextChanged(Editable s) {
        //
    }

    @Override
    public void onDismiss(double lat, double lng, String address) {
        relativeLayoutDetails.setVisibility(View.VISIBLE);

        this.lat = lat;
        this.lng = lng;

        if(address != null) {
            String[] splitAddress = address.split(", ");

            editTextCountry.setText(splitAddress[0]);
            editTextGovernorate.setText(splitAddress[1]);
            editTextProvince.setText(splitAddress[2]);
            editTextCity.setText(splitAddress[3]);
            editTextStreet.setText(splitAddress[4]);
            editTextWay.setText(splitAddress[5]);

            if(splitAddress.length == 7) {
                editTextName.setText(splitAddress[6]);
            }

            editTextBuilding.setText("");
        } else {
            editTextName.setText("");
            editTextCountry.setText("");
            editTextGovernorate.setText("");
            editTextProvince.setText("");
            editTextCity.setText("");
            editTextStreet.setText("");
            editTextWay.setText("");
            editTextBuilding.setText("");
        }
    }

    public void selectLocationAction(View view) {
        AddLocationActivityPermissionsDispatcher.showChooseLocDialogWithPermissionCheck(this);
    }

    public void confirmAction(View view) {
        String address = editTextCountry.getText().toString() + ", " +
                editTextGovernorate.getText().toString() + ", " +
                editTextProvince.getText().toString() + ", " +
                editTextCity.getText().toString() + ", " +
                editTextStreet.getText().toString() + ", " +
                editTextWay.getText().toString() + ", " +
                editTextBuilding.getText().toString() + " building";

        String name = editTextName.getText().toString().trim();

        JsonObject json = new JsonObject();
        json.addProperty("addressLine1", address);
        json.addProperty("longitude", lng);
        json.addProperty("latitude", lat);
        json.addProperty("name", name);

        DatabaseHelper helper = new DatabaseHelper(this);
        User user = (User)helper.select(DatabaseHelper.Tables.USERS, null);

        waitDialogFragment = WaitDialogFragment.createDialog();
        waitDialogFragment.setCancelable(false);
        waitDialogFragment.show(getSupportFragmentManager(), "ADD_LOC_WAIT_DIALOG");

        addLocation(user.getId(), user.getToken(), json.toString(), helper);
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void showChooseLocDialog() {
        ChooseLocDialogFragment chooseLocationDialogFragment = ChooseLocDialogFragment.createDialog();
        chooseLocationDialogFragment.setContext(this);
        chooseLocationDialogFragment.setOnDismissListener(this);
        chooseLocationDialogFragment.show(getSupportFragmentManager(), "CHOOSE_LOCATION_DIALOG");
    }

    private void addLocation(final String id, final String token, final String body, final DatabaseHelper helper) {
        String url = MGasApi.makeUrl(MGasApi.ADD_LOCATION, id);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("authorization", "Bearer " + token);

        Server.request(this, Request.Method.POST, url, headers, body,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.startsWith(MGasApi.IISNODE_ERROR)) {
                            addLocation(id, token, body, helper);
                        } else {
                            Location location = new Gson().fromJson(response, Location.class);
                            helper.insert(DatabaseHelper.Tables.LOCATIONS, location);

                            waitDialogFragment.dismiss();
                            Toast.makeText(AddLocationActivity.this, getString(R.string.location_added),
                                    Toast.LENGTH_LONG).show();

                            String item = location.getLocationName() + " (" + location.getCity() + ")";
                            Intent intent = new Intent();
                            intent.putExtra("ITEM", item);
                            intent.putExtra("LOCATION_JSON", response);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        waitDialogFragment.dismiss();

                        String err = getString(R.string.add_location_error);
                        ErrorDialogFragment fragment = ErrorDialogFragment.creteDialog(err);
                        fragment.show(getSupportFragmentManager(), "ADD_LOC_ERROR_DIALOG");

/*                        Log.v("SPLASH_ADD_LOC_ERR", error.networkResponse.statusCode + "");
                        Log.v("SPLASH_ADD_LOC_ERR", new String(error.networkResponse.data, StandardCharsets.UTF_8));*/
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AddLocationActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
