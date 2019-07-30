package om.webware.mgas.fragments.dialogs;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import om.webware.mgas.R;

public class AddressSearchDialogFragment extends DialogFragment implements View.OnClickListener, TextWatcher {

    private EditText editTextCountry;
    private EditText editTextGovernorate;
    private EditText editTextProvince;
    private EditText editTextCity;
    private EditText editTextStreet;
    private EditText editTextWay;
    private EditText editTextName;
    private Button buttonSearch;

    private Context context;

    private OnDismissListener onDismissListener;

    public interface OnDismissListener { void onDismiss(String address); }

    public AddressSearchDialogFragment() {
        //
    }

    public static AddressSearchDialogFragment createDialog() {
        return new AddressSearchDialogFragment();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String country = editTextCountry.getText().toString().trim();
        String governorate = editTextGovernorate.getText().toString().trim();
        String province = editTextProvince.getText().toString().trim();
        String city = editTextCity.getText().toString().trim();
        String street = editTextStreet.getText().toString().trim();
        String way = editTextWay.getText().toString().trim();

        boolean allFilled = !country.isEmpty() && !governorate.isEmpty() && !province.isEmpty() &&
                !city.isEmpty() && !street.isEmpty() && !way.isEmpty();

        buttonSearch.setEnabled(allFilled);
    }

    @Override
    public void afterTextChanged(Editable s) {
        //
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_address_search, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextCountry = view.findViewById(R.id.editTextCountry);
        editTextGovernorate = view.findViewById(R.id.editTextGovernorate);
        editTextProvince = view.findViewById(R.id.editTextProvince);
        editTextCity = view.findViewById(R.id.editTextCity);
        editTextStreet = view.findViewById(R.id.editTextStreet);
        editTextWay = view.findViewById(R.id.editTextWay);
        editTextName = view.findViewById(R.id.editTextName);
        buttonSearch = view.findViewById(R.id.buttonSearch);

        editTextCountry.addTextChangedListener(this);
        editTextGovernorate.addTextChangedListener(this);
        editTextProvince.addTextChangedListener(this);
        editTextCity.addTextChangedListener(this);
        editTextStreet.addTextChangedListener(this);
        editTextWay.addTextChangedListener(this);

        buttonSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String country = editTextCountry.getText().toString();
        String governorate = editTextGovernorate.getText().toString();
        String province = editTextProvince.getText().toString();
        String city = editTextCity.getText().toString();
        String street = editTextStreet.getText().toString();
        String way = editTextWay.getText().toString() + " way";
        String name = editTextName.getText().toString();

        String address = country + ", " + governorate + ", " + province + ", " + city + ", " + street + ", " + way;

        if(!name.isEmpty()) {
            address += ", " + name;
        }

        if(onDismissListener != null) {
            onDismissListener.onDismiss(address);
        }

        dismiss();
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
