package om.webware.mgas.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import om.webware.mgas.fragments.dialogs.ErrorDialogFragment;

public class GPSTracker implements LocationListener {

    private AppCompatActivity activity;

    private LocationManager locationManager;
    private String bestProvider;

    private static final int MIN_DIST = 0;
    private static final int MIN_TIME = 0;

    private OnUserLocationChangedListener onUserLocationChangedListener;

    public interface OnUserLocationChangedListener { void onUserLocationChanged(Location location); }

    @SuppressLint("MissingPermission")
    public GPSTracker(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        activity = (AppCompatActivity)context;

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(true);
        criteria.setSpeedRequired(true);
        criteria.setCostAllowed(true);

        bestProvider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DIST, this);
    }

    public boolean canGetLocation() {
        String error;
        ErrorDialogFragment dialogFragment;

        if(bestProvider.equals(LocationManager.NETWORK_PROVIDER) && !locationManager.isProviderEnabled(bestProvider)) {
            error = "Network unavailable.";
            dialogFragment = ErrorDialogFragment.creteDialog(error);
            dialogFragment.show(activity.getSupportFragmentManager(), "NETWORK_UNAVAILABLE_ERROR_DIALOG");
            return false;
        }

        if(bestProvider.equals(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(bestProvider)) {
            error = "GPS is currently disable. Please enable it before proceeding.";
            dialogFragment = ErrorDialogFragment.creteDialog(error);
            dialogFragment.show(activity.getSupportFragmentManager(), "GPS_DISABLED_ERROR_DIALOG");
            return false;
        }

        return true;
    }

    public void setOnUserLocationChangedListener(OnUserLocationChangedListener onUserLocationChangedListener) {
        this.onUserLocationChangedListener = onUserLocationChangedListener;
    }

    @SuppressLint("MissingPermission")
    public Location getLastKnownLocation() {
        return locationManager.getLastKnownLocation(bestProvider);
    }

    @Override
    public void onLocationChanged(Location location) {
        if(onUserLocationChangedListener != null) {
            onUserLocationChangedListener.onUserLocationChanged(location);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Deprecated, will never be called from now on
    }

    @Override
    public void onProviderEnabled(String provider) {
        //
    }

    @Override
    public void onProviderDisabled(String provider) {
        //
    }
}
