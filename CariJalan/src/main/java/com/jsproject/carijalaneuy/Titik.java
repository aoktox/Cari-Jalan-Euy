package com.jsproject.carijalaneuy;

import java.io.Serializable;

/**
 * Created by J on 20/07/2016.
 */
public class Titik implements Serializable{
    int id_titik;
    String nama;
    double lat, lng;

    public int getId_titik() {
        return id_titik;
    }

    public void setId_titik(int id_titik) {
        this.id_titik = id_titik;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

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

    public Titik(int id_titik, String nama, double lat, double lng) {

        this.id_titik = id_titik;
        this.nama = nama;
        this.lat = lat;
        this.lng = lng;
    }
}
