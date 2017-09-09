package com.example.varda.naviraklio;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class NewAppointment extends AppCompatActivity {
    private static final int REQUEST_LOCATION_STRING = 34, REQUEST_DATETIME_STRING = 43;
    private Spinner spinner;
    private String[] placeTypes;
    String dateString = "";
    private TextView dateText;
    private Button setDateTimeBtn, movieBtn, confirmAppointmentBtn;
    private Place receivedDestination;
    private TextView addressText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_appointment);

        spinner = (Spinner) findViewById(R.id.spinner);
        placeTypes = getResources().getStringArray(R.array.place_type);
        ArrayAdapter dataAdapter = new ArrayAdapter<String>(NewAppointment.this, android.R.layout.simple_spinner_item, placeTypes);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        dateText = (TextView) findViewById(R.id.dateText);
        movieBtn = (Button) findViewById(R.id.movieBtn);
        setDateTimeBtn = (Button) findViewById(R.id.setDateTimeBtn);
        confirmAppointmentBtn = (Button) findViewById(R.id.confirmAppointmentBtn);
        updateStartText();


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinner.getSelectedItem().toString().equals("Cinema")) {
                    setDateTimeBtn.setVisibility(View.GONE);
                    movieBtn.setVisibility(View.VISIBLE);
                } else {
                    setDateTimeBtn.setVisibility(View.VISIBLE);
                    movieBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (spinner.getSelectedItem().toString().equals("Cinema")) {
                    setDateTimeBtn.setVisibility(View.GONE);
                    movieBtn.setVisibility(View.VISIBLE);
                } else {
                    setDateTimeBtn.setVisibility(View.VISIBLE);
                    movieBtn.setVisibility(View.GONE);
                }

            }
        });

        movieBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(NewAppointment.this,MoviePicker.class);
                startActivity(intent);
            }
        });


        confirmAppointmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dateString.equals(null)) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("dateKey", dateString);
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
        if (requestCode == REQUEST_LOCATION_STRING) {
            if (resultCode == Activity.RESULT_OK) {
                receivedDestination = data.getParcelableExtra("coordKey");
                addressText.setText(receivedDestination.getAddress());
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("dateStringKey", dateString);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        dateString = savedInstanceState.getString("dateStringKey");
    }


    protected void updateStartText() {
        dateText.setText(dateString);
    }
}


























