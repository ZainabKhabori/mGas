package om.webware.mgas.api;

import android.database.Cursor;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Zainab on 5/17/2019.
 */

public class Locations {

    private ArrayList<Location> locations;

    public Locations(Cursor cursor) {
        locations = new ArrayList<>();

        while(cursor.moveToNext()) {
            String id = cursor.getString(0);
            double longitude = cursor.getDouble(1);
            double latitude = cursor.getDouble(2);
            String addressLine1 = cursor.getString(3);
            String addressLine2 = cursor.getString(4);
            String city = cursor.getString(5);
            String province = cursor.getString(6);
            String governorate = cursor.getString(7);
            String country = cursor.getString(8);
            String locationName = cursor.getString(9);

            Location location = new Location(id, longitude, latitude, addressLine1, addressLine2,
                    city, province, governorate, country, locationName);
            locations.add(location);
        }
    }

    public Locations(String json) {
        Location[] array = new Gson().fromJson(json, Location[].class);
        locations = new ArrayList<>(Arrays.asList(array));
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }
}
