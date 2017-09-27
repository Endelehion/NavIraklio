package com.example.varda.naviraklio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Home extends AppCompatActivity {
private Button goOrganizerBtn,goMapBtn,goLogoutBtn,goProfileBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        goMapBtn=(Button) findViewById(R.id.goMapBtn);
        goLogoutBtn=(Button) findViewById(R.id.goLogoutBtn);
        goProfileBtn=(Button) findViewById(R.id.goProfileBtn);
        goOrganizerBtn=(Button) findViewById(R.id.goOrganizerBtn);

        goMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent=new Intent(Home.this,InstantMap.class);
                startActivity(mapIntent);
            }
        });

        goProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent=new Intent(Home.this,Profile.class);
                startActivity(profileIntent);
            }
        });

        goOrganizerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent organizerIntent=new Intent(Home.this,Organizer.class);
                startActivity(organizerIntent);
            }
        });

        goLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
