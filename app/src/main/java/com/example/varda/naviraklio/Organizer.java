package com.example.varda.naviraklio;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Organizer extends AppCompatActivity {
    DatePicker datePicker;
    SimpleDateFormat sdateFormat;
    final Calendar c = Calendar.getInstance();
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH);
    int day = c.get(Calendar.DAY_OF_MONTH);
    ArrayList<Appointment> arListAp;
    ArrayAdapter adapterAp;
    ListView listViewAp;
    String dateString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        datePicker.updateDate(year, month, day);
        sdateFormat= new SimpleDateFormat("EEE d MMM yyyy HH:mm");

        arListAp = new ArrayList<>();
        Date myDate;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, 9);
        cal.set(Calendar.DATE, 24);
        cal.set(Calendar.YEAR, 2013);
        cal.set(Calendar.HOUR, 13);
        cal.set(Calendar.MINUTE, 45);
        cal.set(Calendar.SECOND, 52);
        myDate = cal.getTime();
        dateString=sdateFormat.format(myDate);
        arListAp.add(new Appointment("Cinema", dateString));
        cal.set(Calendar.MONTH, 4);
        cal.set(Calendar.DATE, 23);
        cal.set(Calendar.YEAR, 2017);
        cal.set(Calendar.HOUR, 12);
        cal.set(Calendar.MINUTE, 41);
        cal.set(Calendar.SECOND, 59);
        myDate = cal.getTime();

        dateString=sdateFormat.format(myDate);
        arListAp.add(new Appointment("Gas", dateString));

        ArrayList<String> stringList= new ArrayList<>();
        for (int i = 0; i < arListAp.size(); i++) {
            stringList.add(arListAp.get(i).type+" "+arListAp.get(i).dateString);
        }

        adapterAp = new ArrayAdapter<>(Organizer.this, android.R.layout.simple_list_item_1, stringList);
        listViewAp=(ListView) findViewById(R.id.listAp);
        listViewAp.setAdapter(adapterAp);
    }


}
