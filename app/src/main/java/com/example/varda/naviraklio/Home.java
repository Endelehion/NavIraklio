package com.example.varda.naviraklio;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Home extends AppCompatActivity {
    private Button goOrganizerBtn, goMapBtn, goLogoutBtn, goProfileBtn;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 176;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        goMapBtn = (Button) findViewById(R.id.goMapBtn);
        goLogoutBtn = (Button) findViewById(R.id.goLogoutBtn);
        goProfileBtn = (Button) findViewById(R.id.goProfileBtn);
        goOrganizerBtn = (Button) findViewById(R.id.goOrganizerBtn);
        checkPermissions();
        goMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(Home.this, InstantMap.class);
                startActivity(mapIntent);
            }
        });

        goProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(Home.this, Profile.class);
                startActivity(profileIntent);
            }
        });

        goOrganizerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent organizerIntent = new Intent(Home.this, Organizer.class);
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

    public void checkPermissions() {
        if (ContextCompat.checkSelfPermission(Home.this, android.Manifest.
                permission.ACCESS_FINE_LOCATION) != PackageManager.
                PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                Toast toast = Toast.makeText(Home.this, "You previously revoked the Location permission", Toast.LENGTH_SHORT);
                toast.show();
                ActivityCompat.requestPermissions(Home.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(Home.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }


        }
    }

    /**
     * handle permission request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // Location task you need to do.

                    Toast toast = Toast.makeText(Home.this, "permission granted", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    //Toast toast=Toast.makeText(PlanMap.this,"permission denied",Toast.LENGTH_SHORT);
                    // toast.show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


}
