package om.webware.mgas.tools;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import om.webware.mgas.R;
import om.webware.mgas.fragments.dialogs.ErrorDialogFragment;
import om.webware.mgas.fragments.dialogs.WaitDialogFragment;
import om.webware.mgas.server.Server;

/**
 * Created by Zainab on 7/5/2019.
 */

public class RouteMapper {

    private Context context;
    private AppCompatActivity activity;
    private GoogleMap map;
    private LatLng origin;
    private LatLng destination;
    private ArrayList<LatLng> fiveMinutesAwayPoints;
    private RouteMappingCompleteListener listener;

    public interface RouteMappingCompleteListener { void onRouteMappingComplete(ArrayList<LatLng> points); }

    public RouteMapper(Context context, GoogleMap map, LatLng origin, LatLng destination) {
        this.context = context;
        activity = (AppCompatActivity)context;
        this.map = map;
        this.origin = origin;
        this.destination = destination;
        fiveMinutesAwayPoints = new ArrayList<>();
        listener = (RouteMappingCompleteListener)context;
    }

    public void mapRoutes() {
        String url = createRequestUrl(origin, destination);

        final WaitDialogFragment waitDialogFragment = WaitDialogFragment.createDialog();
        waitDialogFragment.setCancelable(false);
        waitDialogFragment.show(activity.getSupportFragmentManager(), "MAP_ROUTE_WAIT");

        Server.request(context, Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JsonObject wayPoints = new Gson().fromJson(response, JsonObject.class);
                        if(wayPoints.has("error_message")) {
                            waitDialogFragment.dismiss();
                            String error = context.getString(R.string.display_route_error);
                            ErrorDialogFragment errorDialogFragment = ErrorDialogFragment.creteDialog(error);
                            errorDialogFragment.show(activity.getSupportFragmentManager(), "ROUTE_DISPLAY_ERROR");

//                            Log.v("SPLASH_ROUTE_ERROR", response);
                        } else {
                            JsonArray routes = wayPoints.getAsJsonArray("routes");

                            for(int i = routes.size() - 1; i >= 0; i--) {
                                JsonObject route = routes.get(i).getAsJsonObject();

                                ArrayList<LatLng> pathPoints = getRoutePoints(route);
                                PolylineOptions options = new PolylineOptions();
                                options.addAll(pathPoints);
                                options.width(30);

                                if(i == 0) {
                                    options.color(ContextCompat.getColor(context, R.color.colorPrimary));
                                } else {
                                    options.color(ContextCompat.getColor(context, R.color.colorAccent));
                                }

                                map.addPolyline(options);
                            }

                            listener.onRouteMappingComplete(fiveMinutesAwayPoints);
                            waitDialogFragment.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        waitDialogFragment.dismiss();

                        String err = context.getString(R.string.display_route_error);
                        ErrorDialogFragment errorDialogFragment = ErrorDialogFragment.creteDialog(err);
                        errorDialogFragment.show(activity.getSupportFragmentManager(), "ROUTE_DISPLAY_ERROR");

//                        Log.v("SPLASH_ROUTE_ERROR", new String(error.networkResponse.data, StandardCharsets.UTF_8));
                    }
                });
    }

    private ArrayList<LatLng> getRoutePoints(JsonObject route) {
        JsonObject leg = route.getAsJsonArray("legs").get(0).getAsJsonObject();
        JsonArray steps = leg.getAsJsonArray("steps");

        boolean pointSet = false;

        double time = leg.getAsJsonObject("duration").get("value").getAsDouble();

        ArrayList<LatLng> routePoints = new ArrayList<>();
        for(JsonElement step : steps) {
            String points = step.getAsJsonObject().getAsJsonObject("polyline").get("points").getAsString();
            ArrayList<LatLng> decoded = decodePolyline(points);
            routePoints.addAll(decoded);

            double duration = step.getAsJsonObject().getAsJsonObject("duration").get("value").getAsDouble();
            time -= duration;

            if((time / 60) <= 5 && !pointSet) {
                fiveMinutesAwayPoints.add(decoded.get(0));
                pointSet = true;
            }
        }

        return routePoints;
    }

    private String createRequestUrl(LatLng origin, LatLng destination) {
        String start = "origin=" + origin.latitude + "," + origin.longitude;
        String end = "destination=" + destination.latitude + "," + destination.longitude;
        String alternatives = "alternatives=true";
        String key = "key=" + context.getString(R.string.google_maps_key);
        String output = "json";

        String params = TextUtils.join("&", new String[] {start, end, alternatives, key});

        return ("https://maps.googleapis.com/maps/api/directions/" + output + "?" + params);
    }

    private ArrayList<LatLng> decodePolyline(String encoded) {
        ArrayList<LatLng> points = new ArrayList<>();

        int i = 0;
        int tempLat = 0;
        int tempLng = 0;

        while(i < encoded.length()) {
            int b;
            int shift = 0;
            int result = 0;

            do {
                b = encoded.charAt(i++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while(b >= 0x20);
            int dLat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            tempLat += dLat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(i++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dLng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            tempLng += dLng;

            double lat = (double)tempLat / 1E5;
            double lng = (double) tempLng / 1E5;

            LatLng latLng = new LatLng(lat, lng);
            points.add(latLng);
        }

        return points;
    }
}
