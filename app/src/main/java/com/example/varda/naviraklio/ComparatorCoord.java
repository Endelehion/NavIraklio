package com.example.varda.naviraklio;

import com.google.android.gms.maps.model.LatLng;

import java.util.Comparator;

/**
 * Created by Endelehion on 27/6/2017.
 */

public class ComparatorCoord implements Comparator<Place> {
    @Override
    public int compare(Place compareCoord1, Place compareCoord2) {
        LatLng compLatl1 = (compareCoord1).getCoord();
        double compX1 = compLatl1.latitude;
        LatLng compLat2 = (compareCoord2).getCoord();
        double compX2 = compLat2.latitude;
        //ascending order
        return Double.compare(compX1,compX2);
    }
}
