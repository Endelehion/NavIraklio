package com.example.varda.naviraklio.model;

/**
 * Created by johnis on 23/9/2017.
 */

public class AppointmentModel {
    private int id;
    private String date;
    private String duration;
    private String latitude;
    private String longitude;
    private String address;
    private String type;
    private String openHour;
    private String closeHour;

    public AppointmentModel() {
    }

    public AppointmentModel(int id, String date, String duration, String latitude, String longitude, String address, String type, String openHour, String closeHour) {
        this.id = id;
        this.date = date;
        this.duration = duration;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.type = type;
        this.openHour = openHour;
        this.closeHour = closeHour;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOpenHour() {
        return openHour;
    }

    public void setOpenHour(String openHour) {
        this.openHour = openHour;
    }

    public String getCloseHour() {
        return closeHour;
    }

    public void setCloseHour(String closeHour) {
        this.closeHour = closeHour;
    }
}
