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

public class ErrorDialogFragment extends DialogFragment implements View.OnClickListener {

    public ErrorDialogFragment() {
        //
    }

    public static ErrorDialogFragment creteDialog(String error) {
        ErrorDialogFragment errorDialogFragment = new ErrorDialogFragment();
        Bundle args = new Bundle();

        args.putString("ERROR", error);
        errorDialogFragment.setArguments(args);

        return errorDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_error, container);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getContext(),
                R.drawable.round_edge_dialog_background));

        TextView textViewError = view.findViewById(R.id.textViewError);
        ImageView imageViewDismiss = view.findViewById(R.id.imageViewDismiss);

        textViewError.setText(getArguments() != null ? getArguments().getString("ERROR") : null);
        imageViewDismiss.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
