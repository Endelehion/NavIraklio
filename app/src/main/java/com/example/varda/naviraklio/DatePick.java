package com.example.varda.naviraklio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DatePick extends AppCompatActivity {
    private TextView titleTextDate;
    private Button okButtonDate, cancelButtonDate;
    private String passedExtra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_pick);
        Intent intent;
        intent = getIntent();
        passedExtra = intent.getStringExtra("passedString");
        titleTextDate = (TextView) findViewById(R.id.titleTextDate);
        titleTextDate.setText(passedExtra);

        okButtonDate = (Button) findViewById(R.id.okButtonDate);
        cancelButtonDate = (Button) findViewById(R.id.cancelButtonDate);

        okButtonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(DatePick.this,TimePick.class);
                if(passedExtra.equals("Appointment Date")){
                    passedExtra="Appointment Starts at";
                intent.putExtra("passedString",passedExtra) ;
                    startActivity(intent);
                }
                else if(passedExtra.equals("Appointment Starts at")){
                    passedExtra="Appointment Ends at";
                    intent.putExtra("passedString",passedExtra);
                    startActivity(intent);
                }
                else if(passedExtra.equals("Appointment Ends at")){
                    passedExtra="Completed";
                    intent.putExtra("passedString",passedExtra);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(DatePick.this,"Something Broke",Toast.LENGTH_SHORT);
                }
            }
        });

        cancelButtonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DatePick.this,NewAppointment.class);
                startActivity(intent);
            }
        });
    }


}
