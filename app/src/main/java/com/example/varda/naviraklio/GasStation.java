package com.example.varda.naviraklio;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Endelehion on 4/7/2017.
 */

public class GasStation extends Coordinate {
    Date opensAt, closesAt;
    public GasStation(double latitude, double longtitude, String name, String coordType, int openHour,  int closeHour) {
        super(latitude, longtitude, name, coordType);
        opensAt=createTime(openHour);
        closesAt=createTime(closeHour);
    }

    private Date createTime(int hour) {
        Date myDate;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, hour);
        myDate = cal.getTime();
        return myDate;
    }
}
