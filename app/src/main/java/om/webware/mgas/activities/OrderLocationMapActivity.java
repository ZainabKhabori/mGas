package om.webware.mgas.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.ArrayList;

import io.socket.client.Socket;
import om.webware.mgas.R;
import om.webware.mgas.tools.GPSTracker;
import om.webware.mgas.tools.RouteMapper;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
@SuppressWarnings("ConstantConditions")
public class OrderLocationMapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GPSTracker.OnUserLocationChangedListener, RouteMapper.RouteMappingCompleteListener,
        GoogleMap.OnCameraMoveListener, CompoundButton.OnCheckedChangeListener {

    private GoogleMap map;
    private Marker driver;

    private boolean firstSetup;

    private boolean focus;
    private float zoom;

    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_location_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        firstSetup = true;
        focus = false;

        ToggleButton toggleButtonFocus = findViewById(R.id.toggleButtonFocus);
        toggleButtonFocus.setOnCheckedChangeListener(this);
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
        map.setOnCameraMoveListener(this);

        zoom = 19;

        GPSTracker tracker = new GPSTracker(this);
        tracker.setOnUserLocationChangedListener(this);
    }

    @Override
    public void onUserLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if(firstSetup) {
            firstSetup = false;

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

            String locationJson = getIntent().getStringExtra("LOC");
            om.webware.mgas.api.Location loc = new Gson().fromJson(locationJson, om.webware.mgas.api.Location.class);
            LatLng dest = new LatLng(loc.getLatitude(), loc.getLongitude());

            MarkerOptions options = new MarkerOptions();
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
            map.addMarker(options.position(dest));

            RouteMapper mapper = new RouteMapper(this, map, latLng, dest);
            mapper.mapRoutes();
        }

        if(driver != null) {
            driver.remove();
        }

        driver = map.addMarker(new MarkerOptions().position(latLng));

        if(focus) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }
    }

    // Five minutes away points
    @Override
    public void onRouteMappingComplete(ArrayList<LatLng> points) {

    }

    @Override
    public void onCameraMove() {
        zoom = map.getCameraPosition().zoom;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        focus = isChecked;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        OrderLocationMapActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
