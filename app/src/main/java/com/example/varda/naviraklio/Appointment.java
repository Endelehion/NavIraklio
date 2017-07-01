package com.example.varda.naviraklio;

import java.sql.Time;
import java.util.Date;

/**
 * Created by Endelehion on 18/6/2017.
 */

public class Appointment {

    String type;
    String dateString;

    public Appointment(String type, String dateString){
        this.type=type;
        this.dateString=dateString;
    }
}
