package com.example.varda.naviraklio;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by Endelehion on 28/5/2017.
 */

public class Coordinate implements Parcelable {
    private LatLng coord;
    private String address;
    private String coordType;

    public Coordinate(double latitude, double longtitude, String address, String coordType) {
        setCoord(new LatLng(latitude, longtitude));
        this.setAddress(address);
        this.setCoordType(coordType);
    }

    protected Coordinate(Parcel in) {
        coord = in.readParcelable(LatLng.class.getClassLoader());
        address = in.readString();
        coordType = in.readString();
    }

    public static final Creator<Coordinate> CREATOR = new Creator<Coordinate>() {
        @Override
        public Coordinate createFromParcel(Parcel in) {
            return new Coordinate(in);
        }

        @Override
        public Coordinate[] newArray(int size) {
            return new Coordinate[size];
        }
    };

    public LatLng getCoord() {
        return coord;
    }

    public void setCoord(LatLng coord) {
        this.coord = coord;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCoordType() {
        return coordType;
    }

    public void setCoordType(String coordType) {
        this.coordType = coordType;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(coord, flags);
        dest.writeString(address);
        dest.writeString(coordType);
    }
}
