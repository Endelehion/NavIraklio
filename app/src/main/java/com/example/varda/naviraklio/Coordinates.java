package com.example.varda.naviraklio;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Endelehion on 28/5/2017.
 */

public class Coordinates {
    LatLng coord;
    String name,coordType;
    public Coordinates(double latitude,double longtitude,String name, String coordType){
        coord=new LatLng(latitude,longtitude);
        this.name=name;
        this.coordType=coordType;
    }
}
