package com.example.varda.naviraklio;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by Endelehion on 28/5/2017.
 */

public class Place implements Parcelable,Serializable {
    private double lat,lon;
    private String address;
    private String coordType;
    private int openHour;
    private int closeHour;

    public Place(double lat,double lon, String address, String coordType, int openHour,  int closeHour) {
        this.lat=lat;
        this.lon=lon;
        this.setAddress(address);
        this.setCoordType(coordType);
        this.openHour=openHour;
        this.closeHour=closeHour;
    }

    public Place(String coordType){
        this.coordType=coordType;
    }


    protected Place(Parcel in) {
        lat = in.readDouble();
        lon = in.readDouble();
        address = in.readString();
        coordType = in.readString();
        openHour = in.readInt();
        closeHour = in.readInt();
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
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

    public int getOpenHour() {
        return openHour;
    }

    public void setOpenHour(int openHour) {
        this.openHour = openHour;
    }

    public int getCloseHour() {
        return closeHour;
    }

    public void setCloseHour(int closeHour) {
        this.closeHour = closeHour;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(lat);
        dest.writeDouble(lon);
        dest.writeString(address);
        dest.writeString(coordType);
        dest.writeInt(openHour);
        dest.writeInt(closeHour);
    }
}
