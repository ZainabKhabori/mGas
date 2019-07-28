package om.webware.mgas.fragments.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import om.webware.mgas.R;
import om.webware.mgas.server.MGasSocket;
import om.webware.mgas.tools.GPSTracker;
import om.webware.mgas.tools.SavedObjects;

public class ChooseLocDialogFragment extends DialogFragment implements View.OnClickListener, OnMapReadyCallback,
        AddressSearchDialogFragment.OnDismissListener, GoogleMap.OnMapClickListener, GPSTracker.OnUserLocationChangedListener {

    private Socket socket;

    private GPSTracker tracker;
    private GoogleMap map;
    private OnDismissListener onDismissListener;

    private String address;
    private double lat;
    private double lng;
    private double currentLat;
    private double currentLng;

    private Context context;
    private AppCompatActivity activity;

    private ProgressBar progressBarWait;
    private LinearLayout linearLayoutSearch;
    private LinearLayout linearLayoutCurrentLocation;
    private LinearLayout linearLayoutConfirm;

    private ImageView imageViewSearch;
    private ImageView imageViewCurrentLocation;
    private ImageView imageViewConfirm;

    private WaitDialogFragment waitDialogFragment;

    public interface OnDismissListener { void onDismiss(double lat, double lng, String address); }

    public ChooseLocDialogFragment() {
        //
    }

    public static ChooseLocDialogFragment createDialog() {
        return new ChooseLocDialogFragment();
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        try {
            return inflater.inflate(R.layout.fragment_dialog_choose_loc, container);
        } catch(InflateException e) {
            View view = (View)SavedObjects.getSavedObjects().get("CHOOSE_LOC_DIALOG_VIEW");
            ((ViewGroup)view.getParent()).removeView(view);
            return view;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        socket = MGasSocket.socket;
        socket.on("locationFound", locationFound);

        progressBarWait = view.findViewById(R.id.progressBarWait);
        linearLayoutSearch = view.findViewById(R.id.linearLayoutSearch);
        linearLayoutCurrentLocation = view.findViewById(R.id.linearLayoutCurrentLocation);
        linearLayoutConfirm = view.findViewById(R.id.linearLayoutConfirm);

        imageViewSearch = view.findViewById(R.id.imageViewSearch);
        imageViewCurrentLocation = view.findViewById(R.id.imageViewCurrentLocation);
        imageViewConfirm = view.findViewById(R.id.imageViewConfirm);

        if(SavedObjects.getSavedObjects().containsKey("CHOOSE_LOC_LAT")) {
            lat = (double)SavedObjects.getSavedObjects().get("CHOOSE_LOC_LAT");
            lng = (double)SavedObjects.getSavedObjects().get("CHOOSE_LOC_LNG");
        } else {
            lat = 1000000;
            lng = 1000000;
        }

        setupGPS();

        if (getFragmentManager() != null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager()
                    .findFragmentById(R.id.mapChooseLocation);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        SavedObjects.getSavedObjects().put("CHOOSE_LOC_DIALOG_VIEW", getView());
        SavedObjects.getSavedObjects().put("CHOOSE_LOC_LAT", lat);
        SavedObjects.getSavedObjects().put("CHOOSE_LOC_LNG", lng);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        setupMap(googleMap);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        lat = latLng.latitude;
        lng = latLng.longitude;

        map.clear();
        map.addMarker(new MarkerOptions().position(latLng));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == linearLayoutSearch.getId()) {
            searchAction();
        } else if(v.getId() == linearLayoutCurrentLocation.getId()) {
            currentLocationAction();
        } else if(v.getId() == linearLayoutConfirm.getId()) {
            confirmAction();
        }
    }

    @Override
    public void onUserLocationChanged(Location location) {
        Log.v("SPLASH_MAP_LOC", "user location changed");

        if(map != null) {
            Log.v("SPLASH_MAP_LOC", "map != null");

            progressBarWait.setVisibility(View.GONE);
            tracker.setOnUserLocationChangedListener(null);

            currentLat = location.getLatitude();
            currentLng = location.getLongitude();

            setToCurrentLocation();
        }
    }

    @Override
    public void onDismiss(String address) {
        this.address = address;

        waitDialogFragment = WaitDialogFragment.createDialog();
        waitDialogFragment.setCancelable(false);
        waitDialogFragment.show(activity.getSupportFragmentManager(), "WAIT_DIALOG");

        socket.emit("addressSpecified", address);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        SavedObjects.getSavedObjects().put("CHOOSE_LOC_DIALOG_VIEW", getView());
        SavedObjects.getSavedObjects().put("CHOOSE_LOC_LAT", lat);
        SavedObjects.getSavedObjects().put("CHOOSE_LOC_LNG", lng);
    }

    public void setContext(Context context) {
        this.context = context;
        activity = (AppCompatActivity)context;
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @SuppressLint("MissingPermission")
    public void setupMap(GoogleMap googleMap) {
        map = googleMap;
        map.setMinZoomPreference(8);
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setOnMapClickListener(this);

        ColorStateList colorStateList = ContextCompat.getColorStateList(context, R.color.colorAccent);
        imageViewSearch.setImageTintList(colorStateList);
        imageViewCurrentLocation.setImageTintList(colorStateList);
        imageViewConfirm.setImageTintList(colorStateList);

        linearLayoutSearch.setOnClickListener(this);
        linearLayoutCurrentLocation.setOnClickListener(this);
        linearLayoutConfirm.setOnClickListener(this);

        if(lat == 1000000 && lng == 1000000) {
            if(tracker.canGetLocation()) {
                currentLat = tracker.getLastKnownLocation().getLatitude();
                currentLng = tracker.getLastKnownLocation().getLongitude();
                setToCurrentLocation();
            }
        } else {
            tracker.setOnUserLocationChangedListener(null);

            LatLng latLng = new LatLng(lat, lng);
            map.clear();
            map.addMarker(new MarkerOptions().position(latLng));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));

            address = null;
        }
    }

    public void setupGPS() {
        tracker = new GPSTracker(context);
        tracker.setOnUserLocationChangedListener(this);
    }

    public void searchAction() {
        AddressSearchDialogFragment addressSearchDialogFragment = AddressSearchDialogFragment.createDialog();
        addressSearchDialogFragment.setContext(context);
        addressSearchDialogFragment.setOnDismissListener(this);
        addressSearchDialogFragment.show(activity.getSupportFragmentManager(), "ADDRESS_SEARCH_DIALOG");
    }

    public void currentLocationAction() {
        lat = currentLat;
        lng = currentLng;
        setToCurrentLocation();
    }

    public void confirmAction() {
        if(onDismissListener != null) {
            onDismissListener.onDismiss(lat, lng, address);
        }

        dismiss();
    }

    public void setToCurrentLocation() {
        LatLng latLng = new LatLng(currentLat, currentLng);
        map.clear();
        map.addMarker(new MarkerOptions().position(latLng));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));

        address = null;
    }

    private Emitter.Listener locationFound = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject latLng = (JSONObject)args[0];
                    try {
                        lat = latLng.getDouble("lat");
                        lng = latLng.getDouble("lng");

                        LatLng location = new LatLng(lat, lng);
                        map.clear();
                        map.addMarker(new MarkerOptions().position(location));
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 19));

                        waitDialogFragment.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
}