package om.webware.mgas.activities;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import om.webware.mgas.R;
import om.webware.mgas.fragments.dialogs.ErrorDialogFragment;
import om.webware.mgas.fragments.dialogs.OTPDialogFragment;
import om.webware.mgas.fragments.dialogs.WaitDialogFragment;
import om.webware.mgas.server.MGasSocket;
import om.webware.mgas.server.SocketResponse;

@SuppressWarnings("ConstantConditions")
public class ForgotPasswordActivity extends AppCompatActivity implements TextWatcher {

    private TextInputEditText editTextMobile;
    private TextInputEditText editTextNewPassword;
    private Button buttonReset;

    private Socket socket;

    private WaitDialogFragment waitDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        editTextMobile = findViewById(R.id.editTextMobile);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        buttonReset = findViewById(R.id.buttonReset);

        editTextMobile.addTextChangedListener(this);
        editTextNewPassword.addTextChangedListener(this);

        waitDialogFragment = WaitDialogFragment.createDialog();
        waitDialogFragment.setCancelable(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        socket = MGasSocket.socket;
        socket.on("resetPasswordError", resetPasswordError);
        socket.on("otpSent", otpSent);
        socket.on("otpVerificationDone", otpVerificationDone);
        socket.on("resetPasswordSuccess", resetPasswordSuccess);
    }

    @Override
    protected void onPause() {
        super.onPause();
        socket.off();
    }

    public void resetAction(View view) {
        String mobile = editTextMobile.getText().toString();
        String newPass = editTextNewPassword.getText().toString();

        waitDialogFragment.show(getSupportFragmentManager(), "WAIT_DIALOG");
        socket.emit("resetPassword", mobile, newPass);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String mobile = editTextMobile.getText().toString();
        String newPass = editTextNewPassword.getText().toString();

        buttonReset.setEnabled(!mobile.isEmpty() && !newPass.isEmpty());
        if(buttonReset.isEnabled()) {
            buttonReset.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorAccent));
        } else {
            buttonReset.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorAccentDark));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        //
    }

    private Emitter.Listener resetPasswordError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject res = (JSONObject)args[0];
                    SocketResponse response = new Gson().fromJson(res.toString(), SocketResponse.class);

                    ErrorDialogFragment errorDialogFragment = ErrorDialogFragment.creteDialog(response.getMsg());
                    errorDialogFragment.show(getSupportFragmentManager(), "RESET_PASS_ERROR_DIALOG");

                    waitDialogFragment.dismiss();
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
                    waitDialogFragment.dismiss();

                    OTPDialogFragment otpDialogFragment = OTPDialogFragment.createDialog();
                    otpDialogFragment.setContext(ForgotPasswordActivity.this);
                    otpDialogFragment.setCancelable(false);
                    otpDialogFragment.show(getSupportFragmentManager(), "RESET_PASS_OTP_DIALOG");
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
                        waitDialogFragment.show(getSupportFragmentManager(), "RESET_PASS_WAIT_DIALOG");
                        socket.emit("resetToNewPassword");
                    }
                    else {
                        ErrorDialogFragment errorDialogFragment = ErrorDialogFragment.creteDialog(response.getMsg());
                        errorDialogFragment.show(getSupportFragmentManager(), "OTP_VERIFICATION_ERROR_DIALOG");
                    }
                }
            });
        }
    };

    private Emitter.Listener resetPasswordSuccess = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject json = (JSONObject)args[0];
                    SocketResponse response = new Gson().fromJson(json.toString(), SocketResponse.class);

                    Toast.makeText(ForgotPasswordActivity.this, response.getMsg(), Toast.LENGTH_LONG).show();

                    waitDialogFragment.dismiss();

                    Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    };
}
