package com.jsproject.carijalaneuy;

/**
 * Created by J on 20/07/2016.
 */
public class Jalur {
    private double lat, lng;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public Jalur(double lat, double lng) {

        this.lat = lat;
        this.lng = lng;
    }
}
