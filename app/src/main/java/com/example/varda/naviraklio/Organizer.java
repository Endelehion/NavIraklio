package com.example.varda.naviraklio;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class Organizer extends AppCompatActivity {
    private static final int REQUEST_APPOINTMENT_DATE = 22, REQUEST_LOCATION_STRING = 34;
    private DatePicker datePicker;
    private SimpleDateFormat sdateFormat;
    private final Calendar calendar = Calendar.getInstance();
    private int year, month, day;
    private Button newAp, delAp;
    private ArrayList<Appointment> appointArrayList;
    private ArrayAdapter adapterAp;
    private ListView listViewAp;
    private String dateString, receivedDate, receivedType;
    private Place receivedDestination;
    private Button navigateBtn;
    ArrayList<String> stringList;
    private ArrayList<Movie> movieList;
    private List<Place> superMarkets,cinemas,gasStations;
    private int receivedDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);
        datePicker = (DatePicker) findViewById(R.id.datePicker);

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        datePicker.updateDate(year, month, day);
        sdateFormat = new SimpleDateFormat("EEE dd MMM yyyy HH:mm");

        appointArrayList = new ArrayList<>();

        dateString = sdateFormat.format(createDate(16, 8, 2017, 14, 00));
        appointArrayList.add(new Appointment(appointArrayList.size(), dateString,114, new Place(new LatLng(37.977817, 23.769849), "kapou 2","Cinema",16,2)));

        dateString = sdateFormat.format(createDate(7, 7, 2017, 12, 55));
        appointArrayList.add(new Appointment(appointArrayList.size(), dateString,40, new Place(new LatLng(37.977812, 23.769841), "ekei 8", "Supermarket",16,2)));
        stringList = new ArrayList<>();
        for (int i = 0; i < appointArrayList.size(); i++) {
            int id = appointArrayList.get(i).getId();
            String address = appointArrayList.get(i).getPlace().getAddress();
            String type = appointArrayList.get(i).getPlace().getCoordType();
            String date = appointArrayList.get(i).getDateString();
            stringList.add(id + " " + address + " " + type + " " + date);
        }

        adapterAp = new ArrayAdapter<>(Organizer.this, android.R.layout.simple_list_item_single_choice, stringList);
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
                startActivityForResult(intent, REQUEST_APPOINTMENT_DATE);
            }
        });

        navigateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterIndex;
                adapterIndex = listViewAp.getCheckedItemPosition();
                if (adapterIndex != -1) {
                    Intent locationIntent = new Intent(Organizer.this, PlanMap.class);
                    String passedString = appointArrayList.get(adapterIndex).getPlace().getCoordType();
                    locationIntent.putExtra("typeKey", passedString);
                    locationIntent.putExtra("listIndexKey", adapterIndex);
                    locationIntent.putParcelableArrayListExtra("listKey", appointArrayList);
                    startActivityForResult(locationIntent, REQUEST_LOCATION_STRING);

                } else {
                    Toast.makeText(Organizer.this, "No Appointments Selected", Toast.LENGTH_SHORT).show();
                }

            }
        });





                /*
               - American Made - [new]

- Odeon @ Talos Plaza 1 (Dolby Digital)
Σοφοκλή Βενιζέλου, Μίνωος & Πελασγών - Περιοχή Τάλ - Tηλ. 14560
20:15 - 23:00

- Odeon @ Talos Plaza 5 (Dolby Digital)
Σοφοκλή Βενιζέλου, Μίνωος & Πελασγών - Περιοχή Τάλ - Tηλ. 14560
21:40

- Odeon @ Talos Plaza 8 (Dolby Digital)
Σοφοκλή Βενιζέλου, Μίνωος & Πελασγών - Περιοχή Τάλ - Tηλ. 14560
Πεμ. έως Κυρ. 17:20

- Βιτσέντζος Κορνάρος 1 (Dolby Digital)
Μαλικούτη 18&#8211;20 - Tηλ. 2810 821400
20:15 - 22:30

- Τεχνόπολις 1 (Dolby Digital - THX)
Λ. Α. Παπανδρέου 116, Αμμουδάρα - Tηλ. 2810 821400
20:15 - 22:30

- Cars 3 - Αυτοκίνητα 3

- Τεχνόπολις 4 (Dolby Digital)
Λ. Α. Παπανδρέου 116, Αμμουδάρα - Tηλ. 2810 821400
18:00 μεταγλ. - Σαβ. Κυρ. και 16:00 μεταγλ.

- Despicable Me 3 - Εγώ, ο Απαισιότατος 3

- Odeon @ Talos Plaza 1 (Dolby Digital)
Σοφοκλή Βενιζέλου, Μίνωος & Πελασγών - Περιοχή Τάλ - Tηλ. 14560
(3D) 18:00 μεταγλ.

- Odeon @ Talos Plaza 4 (Dolby Digital)
Σοφοκλή Βενιζέλου, Μίνωος & Πελασγών - Περιοχή Τάλ - Tηλ. 14560
18:30 μεταγλ. - Σαβ. Κυρ. και 16:30 μεταγλ.

- Odeon @ Talos Plaza 5 (Dolby Digital)
Σοφοκλή Βενιζέλου, Μίνωος & Πελασγών - Περιοχή Τάλ - Tηλ. 14560
19:30 μεταγλ. - Πεμ. έως Κυρ. και 17:30 μεταγλ.

- Odeon @ Talos Plaza 7 (Dolby Digital)
Σοφοκλή Βενιζέλου, Μίνωος & Πελασγών - Περιοχή Τάλ - Tηλ. 14560
19:00 μεταγλ. - Πεμ. έως. Κυρ. και 17:00 μεταγλ.

- Βιτσέντζος Κορνάρος 1 (Dolby Digital)
Μαλικούτη 18&#8211;20 - Tηλ. 2810 821400
18:30 μεταγλ.

- Τεχνόπολις 1 (Dolby Digital - THX)
Λ. Α. Παπανδρέου 116, Αμμουδάρα - Tηλ. 2810 821400
18:30 μεταγλ. - Σαβ. Κυρ. και 16:30 μεταγλ.

- Τεχνόπολις 2 (Dolby Digital)
Λ. Α. Παπανδρέου 116, Αμμουδάρα - Tηλ. 2810 821400
(3D) 18:00 μεταγλ. - Σαβ. Κυρ. και (3D) 16:00 μεταγλ.

- Τεχνόπολις 5 (Dolby Digital - THX)
Λ. Α. Παπανδρέου 116, Αμμουδάρα - Tηλ. 2810 821400
17:30 - 19:30 μεταγλ.

- Detroit - Detroit: Μια Οργισμένη Πόλη - [new]

- Odeon @ Talos Plaza 2 (Dolby Digital)
Σοφοκλή Βενιζέλου, Μίνωος & Πελασγών - Περιοχή Τάλ - Tηλ. 14560
19:40 - 22:30

- Τεχνόπολις 4 (Dolby Digital)
Λ. Α. Παπανδρέου 116, Αμμουδάρα - Tηλ. 2810 821400
20:00

- Τεχνόπολις 5 (Dolby Digital - THX)
Λ. Α. Παπανδρέου 116, Αμμουδάρα - Tηλ. 2810 821400
23:00

- Dunkirk - Δουνκέρκη

- Cine Creta Maris (Dolby Digital)
Λιμένας Χερσονήσου 700 14 - Tηλ. 28970 27000
Πεμ. έως Κυρ. 21:00

- Odeon @ Talos Plaza 4 (Dolby Digital)
Σοφοκλή Βενιζέλου, Μίνωος & Πελασγών - Περιοχή Τάλ - Tηλ. 14560
20:30 - 22:50

- Βιτσέντζος Κορνάρος 2 (Dolby Digital)
Μαλικούτη 18&#8211;20 - Tηλ. 2810 821400
18:30 - 20:30 - 22:30

- Τεχνόπολις 2 (Dolby Digital)
Λ. Α. Παπανδρέου 116, Αμμουδάρα - Tηλ. 2810 821400
20:30 - 22:30

- Τεχνόπολις 5 (Dolby Digital - THX)
Λ. Α. Παπανδρέου 116, Αμμουδάρα - Tηλ. 2810 821400
21:15

- Logan Lucky

- Odeon @ Talos Plaza 8 (Dolby Digital)
Σοφοκλή Βενιζέλου, Μίνωος & Πελασγών - Περιοχή Τάλ - Tηλ. 14560
22:50

- Τεχνόπολις 4 (Dolby Digital)
Λ. Α. Παπανδρέου 116, Αμμουδάρα - Tηλ. 2810 821400
22:30

- Smurfs: The Lost Village - Τα Στρουμφάκια: Το Χαμένο Χωριό

- Τεχνόπολις 3 (Dolby Digital)
Λ. Α. Παπανδρέου 116, Αμμουδάρα - Tηλ. 2810 821400
18:30 μεταγλ. - Σαβ. Κυρ. και 16:30 μεταγλ.

- The Hitman`s Bodyguard - Ο Σωματοφύλακας του Εκτελεστή

- Odeon @ Talos Plaza 7 (Dolby Digital)
Σοφοκλή Βενιζέλου, Μίνωος & Πελασγών - Περιοχή Τάλ - Tηλ. 14560
21:00

- The Son of Bigfoot - Ο Γιος του Μεγαλοπατούσα

- Odeon @ Talos Plaza 2 (Dolby Digital)
Σοφοκλή Βενιζέλου, Μίνωος & Πελασγών - Περιοχή Τάλ - Tηλ. 14560
Πεμ. έως Κυρ. 17:10 μεταγλ.

- Valerian and the City of a Thousand Planets - Ο Βαλέριαν και η Πόλη με τους Χίλιους Πλανήτες

- Cine Creta Maris (Dolby Digital)
Λιμένας Χερσονήσου 700 14 - Tηλ. 28970 27000
Δευτ. έως Τετ. 21:00

- Odeon @ Talos Plaza 6 (Dolby Digital)
Σοφοκλή Βενιζέλου, Μίνωος & Πελασγών - Περιοχή Τάλ - Tηλ. 14560
19:10 - 22:00

- Odeon @ Talos Plaza 8 (Dolby Digital)
Σοφοκλή Βενιζέλου, Μίνωος & Πελασγών - Περιοχή Τάλ - Tηλ. 14560
20:00

- Τεχνόπολις 3 (Dolby Digital)
Λ. Α. Παπανδρέου 116, Αμμουδάρα - Tηλ. 2810 821400
22:30








                */


    }



    void createCoordinates() {
        superMarkets = new ArrayList<>();
        superMarkets.add(new Place(new LatLng(35.340685, 25.133643), "Chalkiadakis", "Supermarket",9,21));
        superMarkets.add(new Place(new LatLng(35.337384, 25.121930), "LIDL", "Supermarket",9,21));
        superMarkets.add(new Place(new LatLng(35.338468, 25.139354), "AB", "Supermarket",9,21));
        superMarkets.add(new Place(new LatLng(35.337481, 25.132863), "BAZAAR", "Supermarket",9,21));
        superMarkets.add(new Place(new LatLng(35.339136, 25.155434), "Sklavenitis", "Supermarket",9,21));
        superMarkets.add(new Place(new LatLng(35.341716, 25.136238), "papadaki", "Supermarket",9,21));
        superMarkets.add(new Place(new LatLng(35.326724, 25.131095), "Sklavenitis", "Supermarket",9,21));
        superMarkets.add(new Place(new LatLng(35.326251, 25.138878), "Sklavenitis", "Supermarket",9,21));
        superMarkets.add(new Place(new LatLng(35.337651, 25.126895), "Chalkiadakis", "Supermarket",9,21));
        superMarkets.add(new Place(new LatLng(35.338751, 25.119835), "Chalkiadakis", "Supermarket",9,21));
        superMarkets.add(new Place(new LatLng(35.324666, 25.133577), "Ariadni", "Supermarket",9,21));
        superMarkets.add(new Place(new LatLng(35.334394, 25.115245), "INKA", "Supermarket",9,21));
        superMarkets.add(new Place(new LatLng(35.324695, 25.124600), "AB", "Supermarket",9,21));
        superMarkets.add(new Place(new LatLng(35.323925, 25.112541), "LIDL", "Supermarket",9,21));
        superMarkets.add(new Place(new LatLng(35.319163, 25.144127), "INKA", "Supermarket",9,21));
        superMarkets.add(new Place(new LatLng(35.324660, 25.124514), "AB", "Supermarket",9,21));
        superMarkets.add(new Place(new LatLng(35.318393, 25.148246), "Chalkiadakis", "Supermarket",9,21));
        superMarkets.add(new Place(new LatLng(35.331733, 25.137689), "Chalkiadakis", "Supermarket",9,21));
        superMarkets.add(new Place(new LatLng(35.330157, 25.132282), "Chalkiadakis", "Supermarket",9,21));
        superMarkets.add(new Place(new LatLng(35.334359, 25.158718), "Chalkiadakis Max", "Supermarket",9,21));
        superMarkets.add(new Place(new LatLng(35.329072, 25.119279), "Chalkiadakis", "Supermarket",9,21));
        superMarkets.add(new Place(new LatLng(35.343307, 25.155190), "My Cretan Goods", "Supermarket",9,21));
        superMarkets.add(new Place(new LatLng(35.336788, 25.133692), "Alati tis Gis", "Supermarket",9,21));
        superMarkets.add(new Place(new LatLng(35.330241, 25.124522), "Kouts", "Supermarket",9,21));


        //zografou
        superMarkets.add(new Place(new LatLng(37.977817, 23.769849), "Daily Lewf. Papagou 114", "Supermarket",9,21));
        Collections.sort(superMarkets, new ComparatorCoord());



        gasStations = new ArrayList<>();
        gasStations.add(new Place(new LatLng(35.338674, 25.141106), "SHELL", "Gas Station", 24, 24));
        gasStations.add(new Place(new LatLng(35.335309, 25.141536), "EKO", "Gas Station", 24, 24));
        gasStations.add(new Place(new LatLng(35.333256, 25.121656), "Tsiknakis Ioannis", "Gas Station", 7, 22));
        gasStations.add(new Place(new LatLng(35.330283, 25.108827), "ELIN", "Gas Station", 7, 22));
        gasStations.add(new Place(new LatLng(35.329145, 25.117691), "Christodoulakis", "Gas Station", 7, 22));
        gasStations.add(new Place(new LatLng(35.338607, 25.143821), "Giannakakis", "Gas Station", 7, 22));
        gasStations.add(new Place(new LatLng(35.336275, 25.121359), "Hanagia", "Gas Station", 7, 22));
        gasStations.add(new Place(new LatLng(35.333818, 25.117024), "Stamatakis", "Gas Station", 7, 22));
        gasStations.add(new Place(new LatLng(35.326903, 25.131728), "Koumoulas", "Gas Station", 7, 22));
        gasStations.add(new Place(new LatLng(35.338667, 25.141116), "SHELL", "Gas Station", 24, 24));
        gasStations.add(new Place(new LatLng(35.338795, 25.141556), "SHELL", "Gas Station", 24, 24));
        gasStations.add(new Place(new LatLng(35.338714, 25.143423), "BP", "Gas Station", 24, 24));
        gasStations.add(new Place(new LatLng(35.337829, 25.141788), "BP", "Gas Station", 24, 24));
        gasStations.add(new Place(new LatLng(35.336477, 25.146265), "BP", "Gas Station", 24, 24));
        gasStations.add(new Place(new LatLng(35.334352, 25.133687), "BP", "Gas Station", 24, 24));
        gasStations.add(new Place(new LatLng(35.324131, 25.139945), "Mavraki", "Gas Station", 24, 24));
        gasStations.add(new Place(new LatLng(35.332375, 25.122159), "Samolis BP", "Gas Station", 24, 24));
        gasStations.add(new Place(new LatLng(35.332414, 25.112785), "Aegean", "Gas Station", 7, 22));
        gasStations.add(new Place(new LatLng(35.319242, 25.133003), "EKO", "Gas Station", 7, 22));
        gasStations.add(new Place(new LatLng(35.320312, 25.125391), "Koumoulas", "Gas Station", 7, 22));
        gasStations.add(new Place(new LatLng(35.321124, 25.143192), "Androulakis", "Gas Station", 7, 22));
        gasStations.add(new Place(new LatLng(35.331358, 25.104039), "Xenakis", "Gas Station", 24, 24));
        gasStations.add(new Place(new LatLng(35.341186, 25.141900), "Avis", "Gas Station", 7, 22));
        gasStations.add(new Place(new LatLng(35.338016, 25.160950), "EKO", "Gas Station", 7, 22));

        //zografou
        gasStations.add(new Place(new LatLng(37.974122, 23.774079), "Revoil", "Gas Station", 7, 22));


        Collections.sort(gasStations, new ComparatorCoord());


        cinemas = new ArrayList<>();
        cinemas.add(new Place(new LatLng(35.339880, 25.119728), "Odeon Talos", "Cinema",16,2));
        cinemas.add(new Place(new LatLng(35.340889, 25.136980), "Vintsenzos Kornaros", "Cinema",16,2));
        cinemas.add(new Place(new LatLng(35.338375, 25.136216), "Astoria", "Cinema",16,2));
        cinemas.add(new Place(new LatLng(35.335669, 25.070682), "Texnopolis", "Cinema",16,2));
        cinemas.add(new Place(new LatLng(35.337980, 25.158230), "Cine Studio", "Cinema",16,2));
        cinemas.add(new Place(new LatLng(35.338573, 25.129685), "Dedalos Club", "Cinema",16,2));
        //zografou
        cinemas.add(new Place(new LatLng(37.977369, 23.770716), "Aleka", "Cinema",16,2));
        Collections.sort(cinemas, new ComparatorCoord());


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
        if (requestCode == REQUEST_APPOINTMENT_DATE
                && resultCode == Organizer.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);

            receivedDate = data.getStringExtra("dateKey");
            receivedType = data.getStringExtra("typeKey");
            data.getIntExtra("durationKey",receivedDuration);
            appointArrayList.add(new Appointment(appointArrayList.size(), receivedDate,receivedDuration,new Place(receivedType)));
            int id = appointArrayList.get(appointArrayList.size() - 1).getId();
            String type = appointArrayList.get(appointArrayList.size() - 1).getPlace().getCoordType();
            String date = appointArrayList.get(appointArrayList.size() - 1).getDateString();
            stringList.add(id + " " + type + " " + date);
            adapterAp.notifyDataSetChanged();
        }
        if (requestCode == REQUEST_LOCATION_STRING
                && resultCode == Organizer.RESULT_OK){
            super.onActivityResult(requestCode, resultCode, data);
            Appointment receivedAppointment;
            receivedAppointment = data.getParcelableExtra("appointKey");
            receivedDestination=receivedAppointment.getPlace();
           int adapterIndex = listViewAp.getCheckedItemPosition();
            if (adapterIndex != -1) {
                appointArrayList.get(adapterIndex).setPlace(receivedDestination);
             //   appointArrayList.get(adapterIndex).getPlace().setAddress(receivedDestination.getAddress());
             //   appointArrayList.get(adapterIndex).getPlace().setCoord(receivedDestination.getCoord());

            } else {
                Toast.makeText(Organizer.this, "No Appointments Selected", Toast.LENGTH_SHORT).show();
            }
        }


    }
}
