package com.example.varda.naviraklio;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.varda.naviraklio.model.AppointmentModel;
import com.example.varda.naviraklio.sql.DatabaseAppointmentHelper;
import com.example.varda.naviraklio.sql.DatabaseHelper;
import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Logger;

public class Organizer extends AppCompatActivity {
    private static final int REQUEST_NEW_APPOINTMENT = 22, REQUEST_LOCATION_MAP = 34;
    private SimpleDateFormat sdateFormat;
    private final Calendar calendar = Calendar.getInstance();
    private int year, month, day;
    private Button newAp, delAp;
    private ArrayList<Appointment> appointArrayList;
    private ArrayAdapter adapterAp;
    private ListView listViewAp;
    private String dateString, receivedDate, receivedType, receivedMovie;
    private Place receivedDestination;
    private Button navigateBtn;
    ArrayList<String> stringList;
    private List<Place> superMarkets, cinemas, gasStations;
    private int receivedDuration;
    private DatabaseAppointmentHelper appDB;
    private DatabaseHelper usDB;
    private String receivedCinema;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);
        if (savedInstanceState != null) {
        } else {

        }
        appDB = new DatabaseAppointmentHelper(this);
        usDB = new DatabaseHelper(this);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        sdateFormat = new SimpleDateFormat("EEE dd MMM yyyy HH:mm");

        createCoordinates();
        appointArrayList = readFromDB();
        if (appointArrayList == null) {
            appointArrayList = new ArrayList<>();
        }
  /*      dateString = sdateFormat.format(createDate(17, 8, 2017, 7, 00));
        appointArrayList.add(new Appointment(appointArrayList.size(), dateString, 114, new Place(new LatLng(37.977817, 23.769849), "kapou 2", "Cinema", 16, 2)));

        dateString = sdateFormat.format(createDate(18, 8, 2017, 13, 30));
        //  appointArrayList.add(new Appointment(appointArrayList.size(), dateString, 5, new Place(gasStations.get(2).getCoord(), "ekei 8", "Gas Station", 16, 2)));
        // dateString = sdateFormat.format(createDate(18, 8, 2017, 20, 00));
        appointArrayList.add(new Appointment(appointArrayList.size(), dateString, 5, new Place(new LatLng(37.974960, 23.763609), "ekei 8", "Gas Station", 16, 2)));
        dateString = sdateFormat.format(createDate(18, 8, 2017, 20, 00));
        appointArrayList.add(new Appointment(appointArrayList.size(), dateString, 114, new Place(new LatLng(37.977817, 23.769849), "kapou 2", "Cinema", 12, 2)));
*/
        stringList = new ArrayList<>();
        for (int i = 0; i < appointArrayList.size(); i++) {
            String address = appointArrayList.get(i).getPlace().getAddress();
            String type = appointArrayList.get(i).getPlace().getCoordType();
            String date = appointArrayList.get(i).getDateString();
            String movie = appointArrayList.get(i).getMovie();
            stringList.add(type + " " + address + " " + movie + "\n" + date);
        }

        adapterAp = new ArrayAdapter<String>(Organizer.this, android.R.layout.simple_list_item_single_choice, stringList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the current item from ListView
                View view = super.getView(position,convertView,parent);

                // Get the Layout Parameters for ListView Current Item View
                ViewGroup.LayoutParams params = view.getLayoutParams();

                // Set the height of the Item View
             params.height=ViewGroup.LayoutParams.WRAP_CONTENT;

                return view;
            }
        };
        listViewAp = (ListView) findViewById(R.id.listAp);
        listViewAp.setAdapter(adapterAp);
        listViewAp.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        newAp = (Button) findViewById(R.id.newAp);
        delAp = (Button) findViewById(R.id.delAp);
        navigateBtn = (Button) findViewById(R.id.navigateBtn);

        delAp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterIndex;
                adapterIndex = listViewAp.getCheckedItemPosition();
                if (adapterIndex != -1) {
                    appointArrayList.remove(adapterIndex);
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
                intent.putParcelableArrayListExtra("listKey", appointArrayList);
                startActivityForResult(intent, REQUEST_NEW_APPOINTMENT);
            }
        });

        navigateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterIndex;
                adapterIndex = listViewAp.getCheckedItemPosition();
                if (adapterIndex != -1) {
                    Intent locationIntent = new Intent(Organizer.this, PlanMap.class);
                    String type = appointArrayList.get(adapterIndex).getPlace().getCoordType();
                    locationIntent.putExtra("typeKey", type);
                    locationIntent.putExtra("listIndexKey", adapterIndex);
                    locationIntent.putParcelableArrayListExtra("listKey", appointArrayList);
                    locationIntent.putExtra("dateKey", appointArrayList.get(adapterIndex).dateString);
                    locationIntent.putExtra("durationMinsKey", appointArrayList.get(adapterIndex).getDuration());
                    if(type.equals("Cinema")){
                        locationIntent.putExtra("cinemaKey",appointArrayList.get(adapterIndex).getCinema());
                    }
                    startActivityForResult(locationIntent, REQUEST_LOCATION_MAP);

                } else {
                    Toast.makeText(Organizer.this, "No Appointments Selected", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    public ArrayList<Appointment> readFromDB() {
        ArrayList<Appointment> tempAppList;
        String currentUser = usDB.getCurrentUser().getUsername();
        tempAppList = (ArrayList<Appointment>) usDB.readSerializedObjectOfUser(currentUser);
        return tempAppList;

     /*   AppointmentModel apModel;
        ArrayList<AppointmentModel> strList;
        ArrayList<Appointment> appList = new ArrayList<>();

        strList = (ArrayList<AppointmentModel>) appDB.getAllAppointments();
        int id, openHour, closeHour, duration;
        String date, address, type;
        double lat, lon;
        for (int i = 0; i < strList.size(); i++) {
            id = strList.get(i).getId();
            openHour = Integer.parseInt(strList.get(i).getOpenHour());
            closeHour = Integer.parseInt(strList.get(i).getCloseHour());
            duration = Integer.parseInt(strList.get(i).getDuration());
            date = strList.get(i).getDate();
            address = strList.get(i).getAddress();
            type = strList.get(i).getType();
            lat = Double.parseDouble(strList.get(i).getLatitude());
            lon = Double.parseDouble(strList.get(i).getLongitude());
            appList.add(new Appointment(id, date, duration, new Place(new LatLng(lat, lon), address, type, openHour, closeHour)));
        }
        return appList;*/
    }


    public void writeToDB(ArrayList<Appointment> appList) {
        String currUser = usDB.getCurrentUser().getUsername();
        if (usDB.updateSerializedObjectOfUser(usDB.getWritableDatabase(), appList, currUser) == -1) {
            Toast.makeText(this, "Write Appointments to DB failed", Toast.LENGTH_SHORT).show();
            Log.i("DBAppointmentsControl", "Appointments DB failed to update");
        } else {
            Log.i("DBAppointmentsControl", "Appointments DB updated successfully");
        }
       /* appDB.clearAppointmentTable(appDB.getWritableDatabase());
        String openHour, closeHour, duration, date, address, type, lat, lon;
        int id;
        for (int i = 0; i < appList.size(); i++) {
            id = appList.get(i).getId();
            openHour = "" + appList.get(i).getPlace().getOpenHour();
            closeHour = "" + appList.get(i).getPlace().getCloseHour();
            duration = "" + appList.get(i).getDuration();
            date = appList.get(i).getDateString();
            address = appList.get(i).getPlace().getAddress();
            type = appList.get(i).getPlace().getCoordType();
            lat = "" + appList.get(i).getPlace().getCoord().latitude;
            lon = "" + appList.get(i).getPlace().getCoord().longitude;
            appDB.addAppointmentModel(new AppointmentModel(id, date, duration, lat, lon, address, type, openHour, closeHour));
        }*/
    }


    void createCoordinates() {
        superMarkets = new ArrayList<>();
        superMarkets.add(new Place(35.340685, 25.133643, "Chalkiadakis", "Supermarket", 9, 21));
        superMarkets.add(new Place(35.337384, 25.121930, "LIDL", "Supermarket", 9, 21));
        superMarkets.add(new Place(35.338468, 25.139354, "AB", "Supermarket", 9, 21));
        superMarkets.add(new Place(35.337481, 25.132863, "BAZAAR", "Supermarket", 9, 21));
        superMarkets.add(new Place(35.339136, 25.155434, "Sklavenitis", "Supermarket", 9, 21));
        superMarkets.add(new Place(35.341716, 25.136238, "papadaki", "Supermarket", 9, 21));
        superMarkets.add(new Place(35.326724, 25.131095, "Sklavenitis", "Supermarket", 9, 21));
        superMarkets.add(new Place(35.326251, 25.138878, "Sklavenitis", "Supermarket", 9, 21));
        superMarkets.add(new Place(35.337651, 25.126895, "Chalkiadakis", "Supermarket", 9, 21));
        superMarkets.add(new Place(35.338751, 25.119835, "Chalkiadakis", "Supermarket", 9, 21));
        superMarkets.add(new Place(35.324666, 25.133577, "Ariadni", "Supermarket", 9, 21));
        superMarkets.add(new Place(35.334394, 25.115245, "INKA", "Supermarket", 9, 21));
        superMarkets.add(new Place(35.324695, 25.124600, "AB", "Supermarket", 9, 21));
        superMarkets.add(new Place(35.323925, 25.112541, "LIDL", "Supermarket", 9, 21));
        superMarkets.add(new Place(35.319163, 25.144127, "INKA", "Supermarket", 9, 21));
        superMarkets.add(new Place(35.324660, 25.124514, "AB", "Supermarket", 9, 21));
        superMarkets.add(new Place(35.318393, 25.148246, "Chalkiadakis", "Supermarket", 9, 21));
        superMarkets.add(new Place(35.331733, 25.137689, "Chalkiadakis", "Supermarket", 9, 21));
        superMarkets.add(new Place(35.330157, 25.132282, "Chalkiadakis", "Supermarket", 9, 21));
        superMarkets.add(new Place(35.334359, 25.158718, "Chalkiadakis Max", "Supermarket", 9, 21));
        superMarkets.add(new Place(35.329072, 25.119279, "Chalkiadakis", "Supermarket", 9, 21));
        superMarkets.add(new Place(35.343307, 25.155190, "My Cretan Goods", "Supermarket", 9, 21));
        superMarkets.add(new Place(35.336788, 25.133692, "Alati tis Gis", "Supermarket", 9, 21));
        superMarkets.add(new Place(35.330241, 25.124522, "Kouts", "Supermarket", 9, 21));


        //zografou
        superMarkets.add(new Place(37.977817, 23.769849, "Daily Lewf. Papagou 114", "Supermarket", 9, 21));
        Collections.sort(superMarkets, new ComparatorCoord());


        gasStations = new ArrayList<>();
        gasStations.add(new Place(35.338674, 25.141106, "SHELL", "Gas Station", 24, 24));
        gasStations.add(new Place(35.335309, 25.141536, "EKO", "Gas Station", 24, 24));
        gasStations.add(new Place(35.333256, 25.121656, "Tsiknakis Ioannis", "Gas Station", 7, 22));
        gasStations.add(new Place(35.330283, 25.108827, "ELIN", "Gas Station", 7, 22));
        gasStations.add(new Place(35.329145, 25.117691, "Christodoulakis", "Gas Station", 7, 22));
        gasStations.add(new Place(35.338607, 25.143821, "Giannakakis", "Gas Station", 7, 22));
        gasStations.add(new Place(35.336275, 25.121359, "Hanagia", "Gas Station", 7, 22));
        gasStations.add(new Place(35.333818, 25.117024, "Stamatakis", "Gas Station", 7, 22));
        gasStations.add(new Place(35.326903, 25.131728, "Koumoulas", "Gas Station", 7, 22));
        gasStations.add(new Place(35.338667, 25.141116, "SHELL", "Gas Station", 24, 24));
        gasStations.add(new Place(35.338795, 25.141556, "SHELL", "Gas Station", 24, 24));
        gasStations.add(new Place(35.338714, 25.143423, "BP", "Gas Station", 24, 24));
        gasStations.add(new Place(35.337829, 25.141788, "BP", "Gas Station", 24, 24));
        gasStations.add(new Place(35.336477, 25.146265, "BP", "Gas Station", 24, 24));
        gasStations.add(new Place(35.334352, 25.133687, "BP", "Gas Station", 24, 24));
        gasStations.add(new Place(35.324131, 25.139945, "Mavraki", "Gas Station", 24, 24));
        gasStations.add(new Place(35.332375, 25.122159, "Samolis BP", "Gas Station", 24, 24));
        gasStations.add(new Place(35.332414, 25.112785, "Aegean", "Gas Station", 7, 22));
        gasStations.add(new Place(35.319242, 25.133003, "EKO", "Gas Station", 7, 22));
        gasStations.add(new Place(35.320312, 25.125391, "Koumoulas", "Gas Station", 7, 22));
        gasStations.add(new Place(35.321124, 25.143192, "Androulakis", "Gas Station", 7, 22));
        gasStations.add(new Place(35.331358, 25.104039, "Xenakis", "Gas Station", 24, 24));
        gasStations.add(new Place(35.341186, 25.141900, "Avis", "Gas Station", 7, 22));
        gasStations.add(new Place(35.338016, 25.160950, "EKO", "Gas Station", 7, 22));

        //zografou
        // gasStations.add(new Place(new LatLng(37.974122, 23.774079), "Revoil", "Gas Station", 7, 22));
        gasStations.add(new Place(37.967245, 23.774535, "kontino alla de prolavainei", "Gas Station", 9, 21));
        gasStations.add(new Place(37.989516, 23.744785, "eksw apo to dromo", "Gas Station", 9, 21));
        gasStations.add(new Place(37.986878, 23.752681, "sto dromo", "Gas Station", 9, 21));


        Collections.sort(gasStations, new ComparatorCoord());


        cinemas = new ArrayList<>();
        cinemas.add(new Place(35.339880, 25.119728, "Odeon Talos", "Cinema", 16, 2));
        cinemas.add(new Place(35.340889, 25.136980, "Vintsenzos Kornaros", "Cinema", 16, 2));
        cinemas.add(new Place(35.338375, 25.136216, "Astoria", "Cinema", 16, 2));
        cinemas.add(new Place(35.335669, 25.070682, "Texnopolis", "Cinema", 16, 2));
        cinemas.add(new Place(35.337980, 25.158230, "Cine Studio", "Cinema", 16, 2));
        cinemas.add(new Place(35.338573, 25.129685, "Dedalos Club", "Cinema", 16, 2));
        //zografou
        cinemas.add(new Place(37.977369, 23.770716, "Aleka", "Cinema", 16, 2));


    }

    Date createDate(int day, int month, int year, int hour, int minute) {
        Date myDate;
        GregorianCalendar gre = new GregorianCalendar(year, month, day, hour, minute);
        myDate = gre.getTime();
        return myDate;
    }

    @Override
    protected void onDestroy() {
        writeToDB(appointArrayList);
        super.onDestroy();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_NEW_APPOINTMENT
                && resultCode == Organizer.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);

            receivedDate = data.getStringExtra("dateKey");
            receivedType = data.getStringExtra("typeKey");
            receivedDestination = data.getParcelableExtra("destinationKey");
            receivedMovie = data.getStringExtra("movieKey");
            receivedCinema=data.getStringExtra("cinemaKey");
            data.getIntExtra("durationKey", receivedDuration);
            Appointment receivedAppointment = new Appointment(appointArrayList.size() - 1, receivedDate, receivedDuration, receivedDestination);
            if (receivedMovie != null) {
                receivedAppointment.setMovie(receivedMovie);
            } else {
                receivedAppointment.setMovie("");
            }
            if (receivedCinema != null) {
                receivedAppointment.setCinema(receivedCinema);
            } else {
                receivedAppointment.setCinema("");
            }
            appointArrayList.add(receivedAppointment);
            String type = appointArrayList.get(appointArrayList.size() - 1).getPlace().getCoordType();
            String date = appointArrayList.get(appointArrayList.size() - 1).getDateString();
            String address = appointArrayList.get(appointArrayList.size() - 1).getPlace().getAddress();
            String movie = appointArrayList.get(appointArrayList.size() - 1).getMovie();

            stringList.add(type + " " + address + " " + movie + "\n" + date);
            adapterAp.notifyDataSetChanged();
        }


    }
}
