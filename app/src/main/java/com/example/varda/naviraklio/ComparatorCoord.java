package com.example.varda.naviraklio;

import com.google.android.gms.maps.model.LatLng;

import java.util.Comparator;

/**
 * Created by Endelehion on 27/6/2017.
 */

public class ComparatorCoord implements Comparator<Place> {
    @Override
    public int compare(Place compareCoord1, Place compareCoord2) {
        double compX1 = compareCoord1.getLat();
        double compX2 = compareCoord2.getLat();
        //ascending order
        return Double.compare(compX1,compX2);
    }
}
