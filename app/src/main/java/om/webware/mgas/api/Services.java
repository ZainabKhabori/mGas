package om.webware.mgas.api;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Services {

    private ArrayList<Service> services;

    @SuppressLint("SimpleDateFormat")
    public Services(Cursor cursor) {
        services = new ArrayList<>();

        while(cursor.moveToNext()) {
            try {
                String id = cursor.getString(0);
                String type = cursor.getString(1);
                String arabicType = cursor.getString(2);
                String cylinderSize = cursor.getString(3);
                String arabicCylinderSize = cursor.getString(4);
                double charge = cursor.getDouble(5);
                Date dateModified = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .parse(cursor.getString(6));

                Service service = new Service(id, type, arabicType, cylinderSize, arabicCylinderSize,
                        charge, dateModified);

                services.add(service);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public Services(String json) {
        Service[] array = new Gson().fromJson(json, Service[].class);
        services = new ArrayList<>(Arrays.asList(array));
    }

    public ArrayList<Service> getServices() {
        return services;
    }
}
