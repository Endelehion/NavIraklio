package com.example.varda.naviraklio;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.Comparator;

/**
 * Created by Endelehion on 28/5/2017.
 */

public class Coordinate  {
    private LatLng coord;
    private String name;
    private String coordType;

    public Coordinate(double latitude, double longtitude, String name, String coordType) {
        setCoord(new LatLng(latitude, longtitude));
        this.setName(name);
        this.setCoordType(coordType);
    }

    public LatLng getCoord() {
        return coord;
    }

    public void setCoord(LatLng coord) {
        this.coord = coord;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoordType() {
        return coordType;
    }

    public void setCoordType(String coordType) {
        this.coordType = coordType;
    }


}
