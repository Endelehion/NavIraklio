package com.example.varda.naviraklio;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Endelehion on 18/6/2017.
 */

public class Appointment {

    String type;
    String dateString;
    private String address;
    LatLng coord;
    int id;


    public Appointment(int id, String type, String address, String dateString, double lat, double lon) {
        this.type = type;
        this.dateString = dateString;
        this.coord = new LatLng(lat, lon);
        this.id = id;
        this.setAddress(address);
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public LatLng getCoord() {
        return coord;
    }

    public void setCoord(LatLng coord) {
        this.coord = coord;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
