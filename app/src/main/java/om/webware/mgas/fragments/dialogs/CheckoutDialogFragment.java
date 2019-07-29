package om.webware.mgas.fragments.dialogs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import io.socket.client.Socket;
import om.webware.mgas.R;
import om.webware.mgas.activities.AddLocationActivity;
import om.webware.mgas.api.BankCard;
import om.webware.mgas.api.Location;
import om.webware.mgas.api.Locations;
import om.webware.mgas.api.Order;
import om.webware.mgas.api.User;
import om.webware.mgas.server.MGasSocket;
import om.webware.mgas.tools.DatabaseHelper;

public class CheckoutDialogFragment extends DialogFragment
        implements PaymentDialogFragment.OnPaymentMethodSelectedListener,
        DeliverToDialogFragment.OnLocationSelectedListener {

    private Context context;
    private AppCompatActivity activity;

    private TextView textViewPaymentMethod;
    private TextView textViewLocation;

    private Order order;
    private DatabaseHelper helper;

    private int paymentIndex;

    private Locations locations;
    private ArrayList<String> names;

    private BankCard card;
    private Location location;

    private Socket socket;
    private WaitDialogFragment waitDialogFragment;

    public CheckoutDialogFragment() {
        //
    }

    public static CheckoutDialogFragment createDialog(String currentLoc, String orderJson) {
        CheckoutDialogFragment fragment = new CheckoutDialogFragment();
        Bundle args = new Bundle();
        args.putString("CURRENT_LOC", currentLoc);
        args.putString("ORDER_JSON", orderJson);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_checkout, container);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getContext(),
                R.drawable.round_edge_dialog_background));

        socket = MGasSocket.socket;

        String currentLoc = getArguments().getString("CURRENT_LOC");
        order = new Gson().fromJson(getArguments().getString("ORDER_JSON"), Order.class);

        helper = new DatabaseHelper(getContext());
        locations = (Locations)helper.select(DatabaseHelper.Tables.LOCATIONS, null);

        names = new ArrayList<>();
        for(Location location: locations.getLocations()) {
            names.add(location.getLocationName() + " (" + location.getCity() + ")");
        }

        paymentIndex = 0;
        location = locations.getLocations().get(names.indexOf(currentLoc));

        textViewPaymentMethod = view.findViewById(R.id.textViewPaymentMethod);
        textViewLocation = view.findViewById(R.id.textViewLocation);

        textViewPaymentMethod.setText(getString(R.string.cash_payment));
        textViewLocation.setText(currentLoc);

        ImageButton imageButtonEditPaymentMethod = view.findViewById(R.id.imageButtonEditPaymentMethod);
        ImageButton imageButtonEditLocation = view.findViewById(R.id.imageButtonEditLocation);
        Button buttonPlaceOrder = view.findViewById(R.id.buttonPlaceOrder);

        imageButtonEditPaymentMethod.setOnClickListener(editPaymentMethodAction);
        imageButtonEditLocation.setOnClickListener(editLocationAction);
        buttonPlaceOrder.setOnClickListener(placeOrderAction);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == AppCompatActivity.RESULT_OK) {
            String locationJson = getActivity().getIntent().getStringExtra("LOCATION_JSON");
            String name = getActivity().getIntent().getStringExtra("ITEM");

            location = new Gson().fromJson(locationJson, Location.class);
            textViewLocation.setText(name);
        }
    }

    @Override
    public void onPaymentMethodSelected(int index, BankCard card) {
        String text;
        paymentIndex = index;
        String cardNumber = card.getCardNo();
        if(index > 1) {
            text = cardNumber.replace(cardNumber.substring(0, cardNumber.length() - 4), "x");
            this.card = card;
        } else {
            text = cardNumber;
        }
        textViewPaymentMethod.setText(text);
    }

    @Override
    public void onLocationSelected(int index, Location location) {
        textViewLocation.setText(location.getLocationName());

        if(index == 0) {
            Intent intent = new Intent(getActivity(), AddLocationActivity.class);
            startActivityForResult(intent, 1);
        } else {
            this.location = location;
        }
    }

    private View.OnClickListener editPaymentMethodAction = new View.OnClickListener() {
        @SuppressWarnings("ConstantConditions")
        @Override
        public void onClick(View v) {
            PaymentDialogFragment fragment = PaymentDialogFragment.createDialog();
            fragment.setListener(CheckoutDialogFragment.this);
            fragment.show(getActivity().getSupportFragmentManager(), "PAYMENT_DIALOG");
        }
    };

    private View.OnClickListener editLocationAction = new View.OnClickListener() {
        @SuppressWarnings("ConstantConditions")
        @Override
        public void onClick(View v) {
            DeliverToDialogFragment fragment = DeliverToDialogFragment
                    .createDialog(locations.getLocations(), names);
            fragment.setListener(CheckoutDialogFragment.this);
            fragment.show(getActivity().getSupportFragmentManager(), "LOCATIONS_DIALOG");
        }
    };

    private View.OnClickListener placeOrderAction = new View.OnClickListener() {
        @SuppressWarnings("ConstantConditions")
        @Override
        public void onClick(View v) {
            if(paymentIndex >= 1) {
                // go to payment gateway
                // if index > 1 send card data as well
            } else {
                waitDialogFragment = WaitDialogFragment.createDialog();
                waitDialogFragment.setCancelable(false);
                waitDialogFragment.show(getActivity().getSupportFragmentManager(), "SEND_ORDER_WAIT");

                order.setLocationId(location.getId());
                String orderJson = new Gson().toJson(order);
                socket.emit("orderRequested", orderJson);
                dismiss();
            }
        }
    };

    public WaitDialogFragment getWaitDialogFragment() {
        return waitDialogFragment;
    }
}
