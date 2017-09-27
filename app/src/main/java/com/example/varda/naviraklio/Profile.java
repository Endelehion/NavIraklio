package com.example.varda.naviraklio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.varda.naviraklio.model.User;
import com.example.varda.naviraklio.sql.DatabaseHelper;

import java.util.ArrayList;


public class Profile extends AppCompatActivity {

    private TextView usernameField, passwordField, firstNameField, lastNameField, addressField, telephoneField, creditCardField;
    private Button editProfileBtn,profileBackHomeBtn;
    private DatabaseHelper usersDB;
    private User user;
    private static final int REQUEST_PROFILE_EDIT=943;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        usersDB = new DatabaseHelper(this);
        if (savedInstanceState != null) {
            user = savedInstanceState.getParcelable("userKey");
        } else {
            user=usersDB.getCurrentUser();
        }
        profileBackHomeBtn=(Button) findViewById(R.id.profileBackHomeBtn);
        editProfileBtn=(Button) findViewById(R.id.editProfileBtn);
        usernameField = (TextView) findViewById(R.id.usernameField);
        passwordField = (TextView) findViewById(R.id.passwordField);
        firstNameField = (TextView) findViewById(R.id.firstNameField);
        lastNameField = (TextView) findViewById(R.id.lastNameField);
        addressField = (TextView) findViewById(R.id.addressField);
        telephoneField = (TextView) findViewById(R.id.telephoneField);
        creditCardField = (TextView) findViewById(R.id.creditCardField);
        usernameField.setText(user.getUsername());
        passwordField.setText(user.getPassword());
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        addressField.setText(user.getAddress());
        telephoneField.setText(user.getTel());
        creditCardField.setText(user.getCreditCard());


        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user!=null){
                    Intent editIntent=new Intent(Profile.this,SignupActivity.class);
                    editIntent.putExtra("editUserKey",user);
                    startActivity(editIntent);
                }else{
                    Toast.makeText(Profile.this,"user is null",Toast.LENGTH_SHORT).show();
                }
            }
        });

        profileBackHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backHomeIntent = new Intent(Profile.this, Home.class);
                backHomeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(backHomeIntent);
                finish();
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
        user=usersDB.getCurrentUser();
        usernameField.setText(user.getUsername());
        passwordField.setText(user.getPassword());
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        addressField.setText(user.getAddress());
        telephoneField.setText(user.getTel());
        creditCardField.setText(user.getCreditCard());

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable("userKey", user);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        user = savedInstanceState.getParcelable("userKey");
    }
}
