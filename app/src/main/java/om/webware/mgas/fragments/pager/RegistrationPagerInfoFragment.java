package om.webware.mgas.fragments.pager;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import io.socket.client.Socket;
import om.webware.mgas.R;
import om.webware.mgas.fragments.dialogs.WaitDialogFragment;
import om.webware.mgas.server.MGasSocket;

@SuppressWarnings("ConstantConditions")
public class RegistrationPagerInfoFragment extends Fragment implements View.OnClickListener, TextWatcher {

    private TextInputEditText editTextFirstName;
    private TextInputEditText editTextLastName;
    private TextInputEditText editTextMobile;
    private TextInputEditText editTextPassword;
    private TextInputEditText editTextEmail;
    private TextInputEditText editTextIdNo;
    private CheckBox checkBoxAccept;
    private Button buttonSendOTP;

    private Socket socket;

    private AppCompatActivity activity;

    private WaitDialogFragment waitDialogFragment;

    public RegistrationPagerInfoFragment() {
        //
    }

    public static RegistrationPagerInfoFragment createFragment() {
        return new RegistrationPagerInfoFragment();
    }

    public void setActivity(Context context) {
        activity = (AppCompatActivity)context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pager_registration_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        socket = MGasSocket.socket;

        editTextFirstName = view.findViewById(R.id.editTextFirstName);
        editTextLastName = view.findViewById(R.id.editTextLastName);
        editTextMobile = view.findViewById(R.id.editTextMobile);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextIdNo = view.findViewById(R.id.editTextIdNo);
        checkBoxAccept = view.findViewById(R.id.checkBoxAccept);
        buttonSendOTP = view.findViewById(R.id.buttonSendOTP);

        editTextFirstName.addTextChangedListener(this);
        editTextLastName.addTextChangedListener(this);
        editTextMobile.addTextChangedListener(this);
        editTextPassword.addTextChangedListener(this);
        editTextEmail.addTextChangedListener(this);
        editTextIdNo.addTextChangedListener(this);
        checkBoxAccept.setOnClickListener(this);
        buttonSendOTP.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == buttonSendOTP.getId()) {
            waitDialogFragment = WaitDialogFragment.createDialog();
            waitDialogFragment.setCancelable(false);
            waitDialogFragment.show(activity.getSupportFragmentManager(), "WAIT_DIALOG");

            socket.emit("consumerRegisterOtp", Integer.parseInt(editTextMobile.getText().toString()));
            buttonSendOTP.setEnabled(false);
        } else {
            String fName = editTextFirstName.getText().toString();
            String lName = editTextLastName.getText().toString();
            String mobile = editTextMobile.getText().toString();
            String pass = editTextPassword.getText().toString();
            String email = editTextEmail.getText().toString();
            String id = editTextIdNo.getText().toString();
            boolean accepted = checkBoxAccept.isChecked();

            buttonSendOTP.setEnabled(!fName.isEmpty() && !lName.isEmpty() && !mobile.isEmpty() && !pass.isEmpty() &&
                    !email.isEmpty() && !id.isEmpty() && accepted);
            if(buttonSendOTP.isEnabled()) {
                buttonSendOTP.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.colorAccent));
            } else {
                buttonSendOTP.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.colorAccentDark));
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String fName = editTextFirstName.getText().toString();
        String lName = editTextLastName.getText().toString();
        String mobile = editTextMobile.getText().toString();
        String pass = editTextPassword.getText().toString();
        String email = editTextEmail.getText().toString();
        String id = editTextIdNo.getText().toString();
        boolean accepted = checkBoxAccept.isChecked();

        buttonSendOTP.setEnabled(!fName.isEmpty() && !lName.isEmpty() && !mobile.isEmpty() && !pass.isEmpty() &&
                !email.isEmpty() && !id.isEmpty() && accepted);
    }

    @Override
    public void afterTextChanged(Editable s) {
        //
    }

    public TextInputEditText getEditTextFirstName() {
        return editTextFirstName;
    }

    public TextInputEditText getEditTextLastName() {
        return editTextLastName;
    }

    public TextInputEditText getEditTextMobile() {
        return editTextMobile;
    }

    public TextInputEditText getEditTextPassword() {
        return editTextPassword;
    }

    public TextInputEditText getEditTextEmail() {
        return editTextEmail;
    }

    public TextInputEditText getEditTextIdNo() {
        return editTextIdNo;
    }

    public Button getButtonSendOTP() {
        return buttonSendOTP;
    }

    public WaitDialogFragment getWaitDialogFragment() {
        return waitDialogFragment;
    }
}
