package om.webware.mgas.fragments.pager;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import om.webware.mgas.R;
import om.webware.mgas.fragments.dialogs.ChooseLocDialogFragment;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class RegistrationPagerLocationFragment extends Fragment implements View.OnClickListener,
        ChooseLocDialogFragment.OnDismissListener, TextWatcher {

    private TextView textViewTitle;
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

    private Context context;

    private double lat;
    private double lng;

    public RegistrationPagerLocationFragment() {
        //
    }

    public static RegistrationPagerLocationFragment createFragment() {
        return new RegistrationPagerLocationFragment();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pager_registration_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textViewTitle = view.findViewById(R.id.textViewTitle);
        LinearLayout linearLayoutSelectLocation = view.findViewById(R.id.linearLayoutSelectLocation);
        relativeLayoutDetails = view.findViewById(R.id.relativeLayoutDetails);
        editTextName = view.findViewById(R.id.editTextName);
        editTextCountry = view.findViewById(R.id.editTextCountry);
        editTextGovernorate = view.findViewById(R.id.editTextGovernorate);
        editTextProvince = view.findViewById(R.id.editTextProvince);
        editTextCity = view.findViewById(R.id.editTextCity);
        editTextStreet = view.findViewById(R.id.editTextStreet);
        editTextWay = view.findViewById(R.id.editTextWay);
        editTextBuilding = view.findViewById(R.id.editTextBuilding);
        buttonConfirm = view.findViewById(R.id.buttonConfirm);

        linearLayoutSelectLocation.setOnClickListener(this);
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
    public void onClick(View v) {
        RegistrationPagerLocationFragmentPermissionsDispatcher
                .selectLocationActionWithPermissionCheck(this);
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

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void selectLocationAction() {
        AppCompatActivity activity = (AppCompatActivity)context;
        ChooseLocDialogFragment chooseLocationDialogFragment = ChooseLocDialogFragment.createDialog();
        chooseLocationDialogFragment.setContext(context);
        chooseLocationDialogFragment.setOnDismissListener(this);
        chooseLocationDialogFragment.show(activity.getSupportFragmentManager(), "CHOOSE_LOCATION_DIALOG");
    }

    public TextView getTextViewTitle() {
        return textViewTitle;
    }

    public EditText getEditTextName() {
        return editTextName;
    }

    public EditText getEditTextCountry() {
        return editTextCountry;
    }

    public EditText getEditTextGovernorate() {
        return editTextGovernorate;
    }

    public EditText getEditTextProvince() {
        return editTextProvince;
    }

    public EditText getEditTextCity() {
        return editTextCity;
    }

    public EditText getEditTextStreet() {
        return editTextStreet;
    }

    public EditText getEditTextWay() {
        return editTextWay;
    }

    public EditText getEditTextBuilding() {
        return editTextBuilding;
    }

    public Button getButtonConfirm() {
        return buttonConfirm;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        RegistrationPagerLocationFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
