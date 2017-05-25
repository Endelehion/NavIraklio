package com.example.varda.naviraklio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;

public class Organizer extends AppCompatActivity {
    DatePicker datePicker;
    final Calendar c = Calendar.getInstance();
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH);
    int day = c.get(Calendar.DAY_OF_MONTH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        datePicker.updateDate(year,month,day);
        Log.i("dategamw"," m"+month+" d"+day+" y"+year);
            }
}
