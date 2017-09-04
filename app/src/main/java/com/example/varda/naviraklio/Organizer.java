package com.example.varda.naviraklio;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.IllegalFormatCodePointException;

public class Organizer extends AppCompatActivity {
    private static final int REQUEST_APPOINTMENT = 22;
    private DatePicker datePicker;
    private SimpleDateFormat sdateFormat;
    private final Calendar calendar = Calendar.getInstance();
    private int year, month, day;
    private Button newAp, delAp;
    private ArrayList<Appointment> arListAp;
    private ArrayAdapter adapterAp;
    private ListView listViewAp;
    private String dateString, receivedDate, receivedType;
    private Coordinate receivedDestination;
    ArrayList<String> stringList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        datePicker = (DatePicker) findViewById(R.id.datePicker);

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        datePicker.updateDate(year, month, day);
        sdateFormat = new SimpleDateFormat("EEE dd MMM yyyy HH:mm");

        arListAp = new ArrayList<>();

        dateString = sdateFormat.format(createDate(day, month, 2017, 15, 35));
        arListAp.add(new Appointment(arListAp.size(), "ekei 4","Cinema", dateString, 37.977817, 23.769849));

        dateString = sdateFormat.format(createDate(7, 7, 2017, 12, 55));
        arListAp.add(new Appointment(arListAp.size(), "kapou 11", "Gas", dateString, 37.977817, 23.769849));

        stringList = new ArrayList<>();
        for (int i = 0; i < arListAp.size(); i++) {
            int id=arListAp.get(i).getId();
            String address=arListAp.get(i).getAddress();
            String type=arListAp.get(i).getType();
            String date=arListAp.get(i).getDateString();
            stringList.add(id + " " + address+" "+type+" "+ date);
        }

        adapterAp = new ArrayAdapter<>(Organizer.this, android.R.layout.simple_list_item_single_choice, stringList);
        listViewAp = (ListView) findViewById(R.id.listAp);
        listViewAp.setAdapter(adapterAp);
        listViewAp.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        newAp = (Button) findViewById(R.id.newAp);
        delAp = (Button) findViewById(R.id.delAp);

        delAp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterIndex;
                adapterIndex = listViewAp.getCheckedItemPosition();
                if (adapterIndex != -1) {
                    arListAp.remove(adapterIndex);
                    stringList.remove(adapterIndex);
                    adapterAp.notifyDataSetChanged();
                    listViewAp.clearChoices();

                } else {
                    Toast.makeText(Organizer.this, "No Appointments Selected", Toast.LENGTH_SHORT).show();
                }
            }
        });


        newAp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Organizer.this, NewAppointment.class);
                startActivityForResult(intent, REQUEST_APPOINTMENT);
            }
        });
    }

    Date createDate(int day, int month, int year, int hour, int minute) {
        Date myDate;
        GregorianCalendar gre = new GregorianCalendar(year, month, day, hour, minute);
        myDate = gre.getTime();
        return myDate;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_APPOINTMENT
                && resultCode == Organizer.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            receivedDestination = data.getParcelableExtra("coordKey");
            receivedDate = data.getStringExtra("dateKey");
            receivedType = data.getStringExtra("typeKey");
            arListAp.add(new Appointment(arListAp.size(), receivedType, receivedDate, receivedDestination.getAddress(),
                    receivedDestination.getCoord().latitude, receivedDestination.getCoord().longitude));
            int id=arListAp.get(arListAp.size()-1).getId();
            String address=arListAp.get(arListAp.size()-1).getAddress();
            String type=arListAp.get(arListAp.size()-1).getType();
            String date=arListAp.get(arListAp.size()-1).getDateString();
            stringList.add(id + " " + address+" "+type+" "+ date);
            adapterAp.notifyDataSetChanged();
            Log.i("Elegxos", "koudsfdfgfgdfg");
        }


    }
}
