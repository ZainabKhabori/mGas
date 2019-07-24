package om.webware.mgas.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import io.socket.client.Socket;
import om.webware.mgas.R;
import om.webware.mgas.server.MGasSocket;
import om.webware.mgas.tools.GPSTracker;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
@SuppressWarnings("ConstantConditions")
public class OrderLocationMapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GPSTracker.OnUserLocationChangedListener {

    private GoogleMap map;

    private Socket socket;

    private int x = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_location_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        double lat = 1000000;
        double lng = 1000000;

        socket = MGasSocket.socket;
        socket.connect();

        socket.emit("clientOnline", getIntent().getStringExtra("driverId"));
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        OrderLocationMapActivityPermissionsDispatcher.setupMapWithPermissionCheck(this, googleMap);
    }

    @SuppressLint("MissingPermission")
    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void setupMap(GoogleMap googleMap) {
        map = googleMap;
        map.setMinZoomPreference(8);
        map.setMyLocationEnabled(true);

        GPSTracker tracker = new GPSTracker(this);
        tracker.setOnUserLocationChangedListener(this);

        if(tracker.canGetLocation()) {
            LatLng latLng = new LatLng(tracker.getLastKnownLocation().getLatitude(),
                    tracker.getLastKnownLocation().getLongitude());

            map.addMarker(new MarkerOptions().position(latLng));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));
        }
    }

    @Override
    public void onUserLocationChanged(Location location) {
/*        if(lat == 1000000 && lng == 1000000) {
            lat = location.getLatitude();
            lng = location.getLongitude();
        }

        lat += 0.1;
        lng += 0.1;*/

        String driverId = getIntent().getStringExtra("driverId");
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        if(driverId.equals("driver 2")) {
            if(x == 0) {
                lat = 23.582537;
                lng = 58.427095;

                x = 1;
            } else {
                lat = 23.582547;
                lng = 58.427085;

                x = 0;
            }

            Log.v("X_VALUE", x + "");
        }

        map.clear();

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        map.addMarker(new MarkerOptions().position(latLng));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));

        socket.emit("driverLocationChanged", driverId, lat, lng);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        OrderLocationMapActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
