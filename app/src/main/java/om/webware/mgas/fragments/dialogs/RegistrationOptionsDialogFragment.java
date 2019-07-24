package om.webware.mgas.fragments.dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.socket.client.Socket;
import om.webware.mgas.R;
import om.webware.mgas.activities.DriverRegistrationActivity;
import om.webware.mgas.activities.RegistrationActivity;
import om.webware.mgas.server.MGasSocket;

/**
 * Created by Zainab on 7/1/2019.
 */

public class RegistrationOptionsDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private TextView textViewConsumer;
    private TextView textViewDriver;

    private Socket socket;

    public RegistrationOptionsDialogFragment() {
        //
    }

    public static RegistrationOptionsDialogFragment createDialog() {
        return new RegistrationOptionsDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_registration_options, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        socket = MGasSocket.socket;

        textViewConsumer = view.findViewById(R.id.textViewConsumer);
        textViewDriver = view.findViewById(R.id.textViewDriver);

        textViewConsumer.setOnClickListener(this);
        textViewDriver.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == textViewConsumer.getId()) {
            Intent intent = new Intent(getContext(), RegistrationActivity.class);
            startActivity(intent);
        } else if(v.getId() == textViewDriver.getId()) {
            Intent intent =  new Intent(getContext(), DriverRegistrationActivity.class);
            startActivity(intent);
        }
    }
}
