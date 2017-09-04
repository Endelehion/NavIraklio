package com.example.varda.naviraklio;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class NewAppointment extends AppCompatActivity {
    private static final int REQUEST_LOCATION_STRING = 34, REQUEST_DATETIME_STRING = 43;
    private Spinner spinner;
    private String[] placeTypes;
    String dateString = "";
    private TextView dateText, timeStartText, timeEndText;
    private Button setDateTimeBtn, setLocationBtn,confirmAppointmentBtn;
    private Coordinate receivedDestination;
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
        setLocationBtn = (Button) findViewById(R.id.setLocationBtn);
        dateText = (TextView) findViewById(R.id.dateText);
        timeEndText = (TextView) findViewById(R.id.timeEndText);
        addressText = (TextView) findViewById(R.id.addressText);
        setDateTimeBtn = (Button) findViewById(R.id.setDateTimeBtn);
        confirmAppointmentBtn = (Button) findViewById(R.id.confirmAppointmentBtn);
        updateStartText();



        confirmAppointmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(receivedDestination!=null && !dateString.equals(null)){
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("coordKey", receivedDestination);
                    resultIntent.putExtra("dateKey",dateString);
                    resultIntent.putExtra("typeKey",spinner.getSelectedItem().toString());
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

        setLocationBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent locationIntent = new Intent(NewAppointment.this, PlanMap.class);
                String passedString = spinner.getSelectedItem().toString();
                locationIntent.putExtra("typeKey", passedString);
                startActivityForResult(locationIntent, REQUEST_LOCATION_STRING);
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


























