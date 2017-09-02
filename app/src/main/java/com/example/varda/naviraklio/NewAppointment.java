package com.example.varda.naviraklio;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class NewAppointment extends AppCompatActivity {
    private Spinner spinner;
    private String[] placeTypes;
    private TextView dateText, timeStartText, timeEndText;
    private Button setDateTimeBtn;
    private int REQUEST_DATETIME_STRING = 43;


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
        timeStartText = (TextView) findViewById(R.id.timeStartText);
        timeEndText = (TextView) findViewById(R.id.timeEndText);
        setDateTimeBtn = (Button) findViewById(R.id.setDateTimeBtn);
        updateStartText();

        setDateTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passedExtra = "Appointment Date";
                Intent intent = new Intent(NewAppointment.this, DatePick.class);
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
                String newText = data.getStringExtra("94");
                dateText.setText(newText);
            }
        }
    }



    protected void updateStartText() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String dateString = extras.getString("stringDatekey");
            dateText.setText(dateString);
        }
    }
}
