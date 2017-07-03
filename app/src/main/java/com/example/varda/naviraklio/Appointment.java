package com.example.varda.naviraklio;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Time;
import java.util.Date;

/**
 * Created by Endelehion on 18/6/2017.
 */

public class Appointment {

    String type;
    String dateString;
    LatLng coord;
    int id;


    public Appointment(int id, String type, String dateString, double lat, double lon){
        this.type=type;
        this.dateString=dateString;
        this.coord=new LatLng(lat,lon);
        this.id=id;
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

}
