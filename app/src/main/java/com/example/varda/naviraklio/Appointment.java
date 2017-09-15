package com.example.varda.naviraklio;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Endelehion on 18/6/2017.
 */

public class Appointment implements Parcelable {

    String dateString;
    Place place;
    int id, duration;


    public Appointment(int id, String dateString, int duration, Place place) {

        this.dateString = dateString;
        this.id = id;
        this.place = place;
        this.duration = duration;
    }


    protected Appointment(Parcel in) {
        dateString = in.readString();
        place = in.readParcelable(Place.class.getClassLoader());
        id = in.readInt();
        duration = in.readInt();
    }

    public static final Creator<Appointment> CREATOR = new Creator<Appointment>() {
        @Override
        public Appointment createFromParcel(Parcel in) {
            return new Appointment(in);
        }

        @Override
        public Appointment[] newArray(int size) {
            return new Appointment[size];
        }
    };

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dateString);
        dest.writeParcelable(place, flags);
        dest.writeInt(id);
        dest.writeInt(duration);
    }
}