package com.example.varda.naviraklio;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePick extends AppCompatActivity {
    private TextView titleTextDate;
    private Button okButtonDate, cancelButtonDate;
    private Button dateToggle, timeToggle;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private SimpleDateFormat selectedDateTime;
    private TextView titleDate;
    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_pick);
        titleDate = (TextView) findViewById(R.id.titleDate);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        dateToggle = (Button) findViewById(R.id.dateToggle);
        timeToggle = (Button) findViewById(R.id.timeToggle);
        okButtonDate = (Button) findViewById(R.id.okButtonDate);
        cancelButtonDate = (Button) findViewById(R.id.cancelButtonDate);
        selectedDateTime = new SimpleDateFormat("EEE d MMM yyyy HH:mm");
        datePicker.setVisibility(View.VISIBLE);
        timePicker.setVisibility(View.GONE);
        printDateTime();


        okButtonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });


        dateToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timePicker.getVisibility() == View.VISIBLE) {
                    timePicker.setVisibility(View.GONE);
                    datePicker.setVisibility(View.VISIBLE);

                    //datePicker.requestLayout();
                }
            }
        });

        timeToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (datePicker.getVisibility() == View.VISIBLE) {
                    datePicker.setVisibility(View.GONE);
                    timePicker.setVisibility(View.VISIBLE);
                    //timePicker.requestLayout();
                }
            }
        });

        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                printDateTime();

            }
        });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                printDateTime();
            }
        });


        cancelButtonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    Date createDate(int day, int month, int year, int hour, int minute) {
        Date myDate;
        GregorianCalendar gre = new GregorianCalendar(year, month, day, hour, minute);
        myDate = gre.getTime();
        return myDate;
    }

    protected void printDateTime() {

        if (Build.VERSION.SDK_INT < 23) {
            titleDate.setText(selectedDateTime.format(createDate(DatePick.this.datePicker.getDayOfMonth(),
                    DatePick.this.datePicker.getMonth(), DatePick.this.datePicker.getYear(),timePicker.getCurrentHour(),timePicker.getCurrentMinute())));
        } else {
            titleDate.setText(selectedDateTime.format(createDate(DatePick.this.datePicker.getDayOfMonth(),
                    DatePick.this.datePicker.getMonth(), DatePick.this.datePicker.getYear(),timePicker.getHour(),timePicker.getMinute())));
        }

    }
}
