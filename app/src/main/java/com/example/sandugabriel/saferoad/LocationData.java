package com.example.sandugabriel.saferoad;

public class LocationData {
    private static final String LOG_TAG = "LocationData";

    private double lat, lng, speed;
    String timestamp;

    LocationData(double lat, double lng, double speed, String timestamp) {
        this.lat = lat;
        this.lng = lng;
        this.speed = speed;
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public double getlat() {
        return lat;
    }

    public double getlng() {
        return lng;
    }

    public double getSpeed() {
        return speed;
    }

    @Override
    public String toString() {
        return timestamp + "," + lat + "," + lng + "," + speed;
    }
}
