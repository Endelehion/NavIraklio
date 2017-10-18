package com.example.varda.naviraklio;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
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
    private TextView dateText, typeLabel;
    private Button setDateTimeBtn, movieBtn, confirmAppointmentBtn, setLocationBtn, cancelNewAppointBtn;
    private TextView durationText, movieTitleLabel, cinemaLabel;
    private String duration, cinemaStringLabel, movieStringLabel, mode, cinemaDateString;
    private String moviePickedDate;
    private ArrayList<Appointment> receivedAppointArrayList;
    private Place receivedDestination;
    private String movieDuration;
    private String preMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_appointment);
        if (savedInstanceState != null) {
            dateString = savedInstanceState.getString("dateStringKey", "");
            cinemaStringLabel = savedInstanceState.getString("cinemaStringLabelKey", "");
            movieStringLabel = savedInstanceState.getString("movieStringLabelKey", "");
            duration = savedInstanceState.getString("durationStringKey", "");
            movieDuration = savedInstanceState.getString("movieDurationStringKey", "");
            mode = savedInstanceState.getString("modeKey");
            preMode = savedInstanceState.getString("preModeKey");
            moviePickedDate = savedInstanceState.getString("moviePickedDateKey");
            receivedDestination = savedInstanceState.getParcelable("destinationKey");
        } else {
            dateString = "";
            duration = "";
            cinemaStringLabel = "";
            movieStringLabel = "";
            movieDuration="";
            mode = "Supermarket";
            moviePickedDate = "";
            preMode="";
            receivedDestination = null;
        }
        Intent receivedIntent = getIntent();
        receivedAppointArrayList = new ArrayList<>();
        receivedAppointArrayList = receivedIntent.getParcelableArrayListExtra("listKey");
        setLocationBtn = (Button) findViewById(R.id.setLocationBtn);
        spinner = (Spinner) findViewById(R.id.spinner);
        placeTypes = getResources().getStringArray(R.array.place_type);
        ArrayAdapter dataAdapter = new ArrayAdapter<String>(NewAppointment.this, R.layout.type_spinner, placeTypes);
        dataAdapter.setDropDownViewResource(R.layout.type_spinner);
        spinner.setAdapter(dataAdapter);
        dateText = (TextView) findViewById(R.id.dateText);
        movieBtn = (Button) findViewById(R.id.movieBtn);
        setDateTimeBtn = (Button) findViewById(R.id.setDateTimeBtn);
        cancelNewAppointBtn = (Button) findViewById(R.id.cancelNewAppointBtn);
        confirmAppointmentBtn = (Button) findViewById(R.id.confirmAppointmentBtn);
        durationText = (TextView) findViewById(R.id.durationText);
        movieTitleLabel = (TextView) findViewById(R.id.movieTitleLabel);
        cinemaLabel = (TextView) findViewById(R.id.cinemaLabel);
        typeLabel = (TextView) findViewById(R.id.typeLabel);

        if (mode.equals("Cinema")) {
            setUpCinema();
        } else if (mode.equals("Gas Station")) {
            setUpGasStation();
        } else if (mode.equals("Supermarket")) {
            setUpSupermarket();
        } else if (mode.equals("Ragnarok")) {
            invokeRagnarok();
        }

        setLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (receivedAppointArrayList != null && !dateString.equals("") && !durationText.getText().equals("Duration: ")) {
                    int index = receivedAppointArrayList.size() - 1;
                    int minDuration, hours, mins;

                    hours = Integer.parseInt(durationText.getText().toString().substring(durationText.getText().toString().indexOf("on: ") + 4, durationText.getText().toString().indexOf("on: ") + 5));
                    mins = Integer.parseInt(durationText.getText().toString().substring(durationText.getText().toString().indexOf("on: ") + 6, durationText.getText().toString().indexOf("on: ") + 8));
                    minDuration = hours * 60 + mins;
                    Intent locationIntent = new Intent(NewAppointment.this, PlanMap.class);
                    if (spinner.getSelectedItemPosition() != -1) {
                        locationIntent.putExtra("typeKey", spinner.getSelectedItem().toString());
                    } else {
                        locationIntent.putExtra("typeKey", "Supermarket");
                    }
                    locationIntent.putExtra("listIndexKey", index);
                    locationIntent.putParcelableArrayListExtra("listKey", receivedAppointArrayList);
                    locationIntent.putExtra("dateKey", dateString);
                    locationIntent.putExtra("durationMinsKey", minDuration);
                    if (mode.equals("Cinema")) {
                        locationIntent.putExtra("cinemaKey", cinemaStringLabel);
                    }
                    preMode=mode;
                    startActivityForResult(locationIntent, REQUEST_LOCATION_STRING);

                } else {
                    Toast.makeText(NewAppointment.this, "All fields must be filled before location set", Toast.LENGTH_SHORT).show();
                }

            }
        });

        cancelNewAppointBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(Activity.RESULT_CANCELED, intent);
                finish();
            }
        });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!mode.equals("Ragnarok")) {
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
                if (!dateString.equals(null) && mode.equals("Ragnarok")) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("destinationKey", (Parcelable) receivedDestination);
                    resultIntent.putExtra("dateKey", dateString);
                    if (spinner.getSelectedItemPosition() != -1) {
                        resultIntent.putExtra("typeKey", spinner.getSelectedItem().toString());
                    } else {
                        resultIntent.putExtra("typeKey", "Supermarket");
                    }
                    if (spinner.getSelectedItem().toString().equals("Cinema") && movieStringLabel != null && cinemaStringLabel!=null) {
                        resultIntent.putExtra("movieKey", movieStringLabel);
                        resultIntent.putExtra("cinemaKey", cinemaStringLabel);

                    }
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(NewAppointment.this, "Date or Location not set", Toast.LENGTH_SHORT).show();
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
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        super.onBackPressed();
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
        } else if (mode.equals(("Ragnarok"))) {
            invokeRagnarok();
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
                movieDuration = data.getStringExtra("moviePickedDurationKey");
                movieStringLabel = data.getStringExtra("moviePickedTitleKey");
                cinemaStringLabel = data.getStringExtra("moviePickedCinemaKey");
                moviePickedDate = data.getStringExtra("moviePickedDateKey");

            }
        }
        if (requestCode == REQUEST_LOCATION_STRING) {
            if (resultCode == Activity.RESULT_OK) {
                receivedDestination = data.getParcelableExtra("destinationKey");
                mode = "Ragnarok";
                invokeRagnarok();
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
        savedInstanceState.putParcelable("destinationKey", receivedDestination);
        savedInstanceState.putString("movieDurationKey", movieDuration);
        savedInstanceState.putString("preModeKey", preMode);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        dateString = savedInstanceState.getString("dateStringKey");
        cinemaStringLabel = savedInstanceState.getString("cinemaStringLabelKey");
        movieStringLabel = savedInstanceState.getString("movieStringLabelKey");
        duration = savedInstanceState.getString("durationStringKey");
        mode = savedInstanceState.getString("modeKey");
        preMode = savedInstanceState.getString("preModeKey");
        moviePickedDate = savedInstanceState.getString("moviePickedDateKey");
        receivedDestination = savedInstanceState.getParcelable("destinationKey");
        movieDuration=savedInstanceState.getString("movieDurationKey");
    }


    protected void updateStartText() {
        dateText.setText(dateString);
        durationText.setText("Duration: "+duration);
        cinemaLabel.setText("Cinema: "+cinemaStringLabel);
        movieTitleLabel.setText("Movie: "+movieStringLabel);

    }

    public void setUpCinema() {
        dateString = moviePickedDate;
        setDateTimeBtn.setVisibility(View.GONE);
        movieBtn.setVisibility(View.VISIBLE);
        movieTitleLabel.setVisibility(View.VISIBLE);
        cinemaLabel.setVisibility(View.VISIBLE);
        duration=movieDuration;
        confirmAppointmentBtn.setVisibility(View.INVISIBLE);
        updateStartText();
    }

    public void setUpGasStation() {
        setDateTimeBtn.setVisibility(View.VISIBLE);
        movieBtn.setVisibility(View.GONE);
        movieTitleLabel.setVisibility(View.INVISIBLE);
        cinemaLabel.setVisibility(View.INVISIBLE);
        duration="0:05";
        confirmAppointmentBtn.setVisibility(View.INVISIBLE);
        updateStartText();
    }

    public void setUpSupermarket() {
        setDateTimeBtn.setVisibility(View.VISIBLE);
        movieBtn.setVisibility(View.GONE);
        movieTitleLabel.setVisibility(View.INVISIBLE);
        cinemaLabel.setVisibility(View.INVISIBLE);
        duration="0:40";
        confirmAppointmentBtn.setVisibility(View.INVISIBLE);
        updateStartText();
    }

    public void invokeRagnarok() {
        movieBtn.setVisibility(View.INVISIBLE);
        setLocationBtn.setVisibility(View.INVISIBLE);
        setDateTimeBtn.setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.INVISIBLE);
        typeLabel.setVisibility(View.INVISIBLE);
        confirmAppointmentBtn.setVisibility(View.VISIBLE);
        cancelNewAppointBtn.setVisibility(View.VISIBLE);
        updateStartText();
        if(!preMode.equals("Cinema")){
            cinemaLabel.setVisibility(View.INVISIBLE);
            movieTitleLabel.setVisibility(View.INVISIBLE);
        }

    }

}



























