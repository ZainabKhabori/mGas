package om.webware.mgas.fragments.dialogs;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import om.webware.mgas.R;
import om.webware.mgas.adapters.DeliverToRecyclerAdapter;
import om.webware.mgas.api.Location;

/**
 * Created by Zainab on 7/09/2019.
 */

public class DeliverToDialogFragment extends DialogFragment implements View.OnClickListener,
        DeliverToRecyclerAdapter.OnItemClickListener {

    private ArrayList<Location> locations;
    private int selected;
    private OnLocationSelectedListener listener;

    public interface OnLocationSelectedListener { void onLocationSelected(int index, Location location); }

    public DeliverToDialogFragment() {
        //
    }

    public static DeliverToDialogFragment createDialog(ArrayList<Location> locations, ArrayList<String> names) {
        DeliverToDialogFragment fragment = new DeliverToDialogFragment();
        Bundle args = new Bundle();
        args.putString("LOCATIONS", new Gson().toJson(locations));
        args.putString("NAMES", new Gson().toJson(names));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_payment, container);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getContext(),
                R.drawable.round_edge_dialog_background));

        RecyclerView recyclerViewCards = view.findViewById(R.id.recyclerViewCards);
        Button buttonConfirm = view.findViewById(R.id.buttonConfirm);

        buttonConfirm.setOnClickListener(this);

        Type locationsType = new TypeToken<ArrayList<Location>>(){}.getType();
        Type namesType = new TypeToken<ArrayList<String>>(){}.getType();
        locations = new Gson().fromJson(getArguments().getString("LOCATIONS"), locationsType);
        ArrayList<String> names = new Gson().fromJson(getArguments().getString("NAMES"), namesType);

        Location location = new Location();
        location.setLocationName(getString(R.string.another_location));
        locations.add(0, location);
        names.add(0, getString(R.string.another_location));

        DeliverToRecyclerAdapter adapter = new DeliverToRecyclerAdapter(getContext(), names);
        adapter.setOnItemClickListener(this);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());

        recyclerViewCards.setAdapter(adapter);
        recyclerViewCards.setLayoutManager(manager);

        selected = 0;
    }

    @Override
    public void onClick(View v) {
        if(listener != null) {
            listener.onLocationSelected(selected, locations.get(selected));
            dismiss();
        }
    }

    @Override
    public void onItemClick(View view, int index) {
        selected = index;
    }

    public void setListener(OnLocationSelectedListener listener) {
        this.listener = listener;
    }
}
