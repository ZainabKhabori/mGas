package om.webware.mgas.fragments.dialogs;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import io.socket.client.Socket;
import om.webware.mgas.R;
import om.webware.mgas.server.MGasSocket;

public class OTPDialogFragment extends DialogFragment implements View.OnClickListener, TextWatcher {

    private Socket socket;
    private EditText editTextCode;
    private Button buttonVerify;
    private Context context;

    public OTPDialogFragment() {
        //
    }

    public static OTPDialogFragment createDialog() {
        return new OTPDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_otp, container);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.round_edge_dialog_background);
        getDialog().getWindow().setBackgroundDrawable(drawable);

        socket = MGasSocket.socket;
        editTextCode = view.findViewById(R.id.editTextCode);
        buttonVerify = view.findViewById(R.id.buttonVerify);

        editTextCode.requestFocus();
        editTextCode.addTextChangedListener(this);
        buttonVerify.setOnClickListener(this);
        socket.connect();
    }

    @Override
    public void onClick(View v) {
        String code = editTextCode.getText().toString();
        socket.emit("verifyOtp", code);
        this.dismiss();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        buttonVerify.setEnabled(s.length() >= 4);
    }

    @Override
    public void afterTextChanged(Editable s) {
        //
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
