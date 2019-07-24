package om.webware.mgas.api;

import android.database.Cursor;

/**
 * Created by Zainab on 5/17/2019.
 */

public class Location {

    private String id;
    private double longitude;
    private double latitude;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String province;
    private String governorate;
    private String country;
    private String locationName;

    public Location() {
        //
    }

    public Location(String id, double longitude, double latitude, String addressLine1,
                    String addressLine2, String city, String province, String governorate,
                    String country, String locationName) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.province = province;
        this.governorate = governorate;
        this.country = country;
        this.locationName = locationName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getGovernorate() {
        return governorate;
    }

    public void setGovernorate(String governorate) {
        this.governorate = governorate;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}
