package om.webware.mgas.fragments.dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import om.webware.mgas.R;

public class AlertDialogFragment extends DialogFragment implements View.OnClickListener {

    public AlertDialogFragment() {
        //
    }

    public static AlertDialogFragment createDialog(String title, String body) {
        AlertDialogFragment fragment = new AlertDialogFragment();
        Bundle args = new Bundle();

        args.putString("TITLE", title);
        args.putString("BODY", body);

        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_alert, container);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getContext(),
                R.drawable.round_edge_dialog_background));

        TextView textViewTitle = view.findViewById(R.id.textViewTitle);
        TextView textViewBody = view.findViewById(R.id.textViewBody);
        ImageView imageViewDismiss = view.findViewById(R.id.imageViewDismiss);

        String title = getArguments().getString("TITLE");
        String body = getArguments().getString("BODY");

        textViewTitle.setText(title);
        textViewBody.setText(body);
        imageViewDismiss.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
