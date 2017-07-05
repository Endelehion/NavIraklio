package com.example.varda.naviraklio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TimePick extends AppCompatActivity {
    private TextView titleTextTime;
    private Button okButtonTime, cancelButtonTime;
    private String passedExtra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_pick);

        Intent intent;
        intent = getIntent();
        passedExtra = intent.getStringExtra("passedString");
        titleTextTime = (TextView) findViewById(R.id.titleTextTime);
        titleTextTime.setText(passedExtra);

        okButtonTime = (Button) findViewById(R.id.okButtonTime);
        cancelButtonTime = (Button) findViewById(R.id.cancelButtonTime);

        okButtonTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (passedExtra.equals("Appointment Starts at")) {
                    Intent intent = new Intent(TimePick.this, TimePick.class);
                    passedExtra = "Appointment Ends at";
                    intent.putExtra("passedString", passedExtra);
                    startActivity(intent);
                } else if (passedExtra.equals("Appointment Ends at")) {
                    Intent intent = new Intent(TimePick.this, NewAppointment.class);
                    passedExtra = "Completed";
                    intent.putExtra("passedString", passedExtra);
                    startActivity(intent);
                } else {
                    Toast.makeText(TimePick.this, "Something Broke", Toast.LENGTH_SHORT);
                }
            }
        });

        cancelButtonTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimePick.this, NewAppointment.class);
                startActivity(intent);
            }
        });
    }
}

