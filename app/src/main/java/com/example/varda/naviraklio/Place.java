package com.example.varda.naviraklio;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Endelehion on 28/5/2017.
 */

public class Place implements Parcelable {
    private LatLng coord;
    private String address;
    private String coordType;
    private int openHour;
    private int closeHour;

    public Place(LatLng coord, String address, String coordType, int openHour,  int closeHour) {
        this.coord=coord;
        this.setAddress(address);
        this.setCoordType(coordType);
        this.openHour=openHour;
        this.closeHour=closeHour;
    }

    public Place(String coordType){
        this.coordType=coordType;
    }

    protected Place(Parcel in) {
        coord = in.readParcelable(LatLng.class.getClassLoader());
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
        dest.writeParcelable(coord, flags);
        dest.writeString(address);
        dest.writeString(coordType);
        dest.writeInt(openHour);
        dest.writeInt(closeHour);
    }
}
