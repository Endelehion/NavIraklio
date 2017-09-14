package com.example.varda.naviraklio;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NewAppointment extends AppCompatActivity {
    private static final int REQUEST_LOCATION_STRING = 34, REQUEST_DATETIME_STRING = 43, REQUEST_MOVIE = 53;
    private Spinner spinner;
    private String[] placeTypes;
    String dateString;
    private TextView dateText;
    private Button setDateTimeBtn, movieBtn, confirmAppointmentBtn,setLocationBtn;
    private TextView durationText, movieTitleLabel, cinemaLabel;
    private String duration, cinemaStringLabel, movieStringLabel, mode, cinemaDateString;
    private String moviePickedDate;
    private ArrayList<Appointment> receivedAppointArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_appointment);
        if (savedInstanceState != null) {
            dateString = savedInstanceState.getString("dateStringKey", "");
            cinemaStringLabel = savedInstanceState.getString("cinemaStringLabelKey", "");
            movieStringLabel = savedInstanceState.getString("movieStringLabelKey", "");
            duration = savedInstanceState.getString("durationStringKey", "");
            mode = savedInstanceState.getString("modeKey");
            moviePickedDate = savedInstanceState.getString("moviePickedDateKey");
        } else {
            dateString = "";
            duration = "";
            cinemaStringLabel = "";
            movieStringLabel = "";
            mode = "Supermarket";
            moviePickedDate = "";
        }
        Intent receivecIntent =new Intent();
        receivedAppointArrayList= receivecIntent.getParcelableArrayListExtra("appointListKey");

        setLocationBtn = (Button) findViewById(R.id.setLocationBtn);
        spinner = (Spinner) findViewById(R.id.spinner);
        placeTypes = getResources().getStringArray(R.array.place_type);
        ArrayAdapter dataAdapter = new ArrayAdapter<String>(NewAppointment.this, android.R.layout.simple_spinner_item, placeTypes);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        dateText = (TextView) findViewById(R.id.dateText);
        movieBtn = (Button) findViewById(R.id.movieBtn);
        setDateTimeBtn = (Button) findViewById(R.id.setDateTimeBtn);
        confirmAppointmentBtn = (Button) findViewById(R.id.confirmAppointmentBtn);
        durationText = (TextView) findViewById(R.id.durationText);
        movieTitleLabel = (TextView) findViewById(R.id.movieTitleLabel);
        cinemaLabel = (TextView) findViewById(R.id.cinemaLabel);
        updateStartText();
        if (mode.equals("Cinema")) {
            setUpCinema();
        } else if (mode.equals("Gas Station")) {
            setUpGasStation();
        } else if (mode.equals("Supermarket")) {
            setUpSupermarket();
        }

        setLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int index=receivedAppointArrayList.size()-1;
                if (!receivedAppointArrayList.isEmpty() || !dateText.getText().equals("")) {
                    Intent locationIntent = new Intent(NewAppointment.this, PlanMap.class);
                    String passedString = receivedAppointArrayList.get(index).getPlace().getCoordType();
                    locationIntent.putExtra("typeKey", passedString);
                    locationIntent.putExtra("listIndexKey", index);
                    locationIntent.putParcelableArrayListExtra("listKey", receivedAppointArrayList);
                    locationIntent.putExtra("dateKey",dateText.getText());
                    startActivityForResult(locationIntent, REQUEST_LOCATION_STRING);

                } else {
                    Toast.makeText(NewAppointment.this, "No Appointments Selected", Toast.LENGTH_SHORT).show();
                }

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinner.getSelectedItem().toString().equals("Cinema")) {
                    mode = "Cinema";
                    setUpCinema();
                } else if (spinner.getSelectedItem().toString().equals("Gas Station")) {
                    mode = "Gas Station";
                    setUpGasStation();
                } else if (spinner.getSelectedItem().toString().equals("Supermarket")) {
                    mode = "Supermarket";
                    setUpSupermarket();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        movieBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewAppointment.this, MoviePicker.class);
                startActivityForResult(intent, REQUEST_MOVIE);
            }
        });


        confirmAppointmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dateString.equals(null)) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("dateKey", dateText.getText());
                    resultIntent.putExtra("typeKey", spinner.getSelectedItem().toString());
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            }
        });


        setDateTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewAppointment.this, DateTimePick.class);
                startActivityForResult(intent, REQUEST_DATETIME_STRING);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateStartText();
        if (mode.equals("Cinema")) {
            setUpCinema();
        } else if (mode.equals("Gas Station")) {
            setUpGasStation();
        } else if (mode.equals("Supermarket")) {
            setUpSupermarket();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DATETIME_STRING) {
            if (resultCode == Activity.RESULT_OK) {
                dateString = data.getStringExtra("94");
                dateText.setText(dateString);
            }
        }
        if (requestCode == REQUEST_MOVIE) {
            if (resultCode == Activity.RESULT_OK) {
                duration = data.getStringExtra("moviePickedDurationKey");
                movieStringLabel = data.getStringExtra("moviePickedTitleKey");
                cinemaStringLabel = data.getStringExtra("moviePickedCinemaKey");
                moviePickedDate = data.getStringExtra("moviePickedDateKey");

            }
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("dateStringKey", dateString);
        savedInstanceState.putString("cinemaStringLabelKey", cinemaStringLabel);
        savedInstanceState.putString("movieStringLabelKey", movieStringLabel);
        savedInstanceState.putString("durationStringKey", duration);
        savedInstanceState.putString("modeKey", mode);
        savedInstanceState.putString("moviePickedDateKey", moviePickedDate);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        dateString = savedInstanceState.getString("dateStringKey");
        cinemaStringLabel = savedInstanceState.getString("cinemaStringLabelKey");
        movieStringLabel = savedInstanceState.getString("movieStringLabelKey");
        duration = savedInstanceState.getString("durationStringKey");
        mode = savedInstanceState.getString("modeKey");
        moviePickedDate = savedInstanceState.getString("moviePickedDateKey");
    }


    protected void updateStartText() {
        dateText.setText(dateString);
    }

    public void setUpCinema() {
        setDateTimeBtn.setVisibility(View.GONE);
        movieBtn.setVisibility(View.VISIBLE);
        movieTitleLabel.setVisibility(View.VISIBLE);
        cinemaLabel.setVisibility(View.VISIBLE);
        durationText.setText("Duration: " + duration);
        cinemaLabel.setText("Cinema: " + cinemaStringLabel);
        movieTitleLabel.setText("Movie: " + movieStringLabel);
        dateText.setText(moviePickedDate);
    }

    public void setUpGasStation() {
        setDateTimeBtn.setVisibility(View.VISIBLE);
        movieBtn.setVisibility(View.GONE);
        movieTitleLabel.setVisibility(View.INVISIBLE);
        cinemaLabel.setVisibility(View.INVISIBLE);
        durationText.setText("Duration: 0:05");
    }

    public void setUpSupermarket() {
        setDateTimeBtn.setVisibility(View.VISIBLE);
        movieBtn.setVisibility(View.GONE);
        movieTitleLabel.setVisibility(View.INVISIBLE);
        cinemaLabel.setVisibility(View.INVISIBLE);
        durationText.setText("Duration: 0:40");
    }

}



























