package com.example.varda.naviraklio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
    protected void goProfile(View view){
        Intent profileIntent=new Intent(Home.this, Profile.class);
        startActivity(profileIntent);
    }
    protected void goCalendar(View view){
        Intent profileIntent=new Intent(Home.this, Organizer.class);
        startActivity(profileIntent);
    }
    protected void goMap(View view){
        Intent profileIntent=new Intent(Home.this, Map.class);
        startActivity(profileIntent);
    }
    protected void goLogout(View view){
        Intent profileIntent=new Intent(Home.this, LoginActivity.class);
        startActivity(profileIntent);
    }
}
