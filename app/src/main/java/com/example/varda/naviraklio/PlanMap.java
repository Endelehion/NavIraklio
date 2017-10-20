package com.example.varda.naviraklio;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.google.android.gms.location.LocationServices.*;

public class PlanMap extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private LatLng hera, origin;
    private double lat, lon;
    private Location location;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private final int REQUEST_CHECK_SETTINGS = 1, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9, PLACE_PICKER_REQUEST = 2;// unique identifiers
    private boolean clickedflag;
    private String selectedTypeString, receivedDateString;
    private final String APPOINTMENT_STRINGID = "appointKey";
    private LatLng whereNow;
    private Marker currMark;
    private Button mapConfirmBtn;
    private double sumDist;
    private TextView destinationText;
    private ToggleButton myLocationToggle, customMarkerToggle;
    private String[] placeTypes;
    private Place destination;
    private ArrayList<Appointment> receivedAppointList;
    private ArrayList<Place> superMarkets, cinemas, gasStations;
    private Intent receivedIntent;
    private int receivedListIndex;
    private SimpleDateFormat dateFormat;
    private int receivedDuration;
    private boolean appBeforeWithTravelTime;
    private boolean appAfterWithTravelTime;
    private boolean appSame;
    private boolean appAfterWithoutTravelTime;
    private boolean appBeforeWithoutTravelTime;
    private boolean appInside;
    private boolean appInclusive;
    private static final double avgDivergence = 1.8784960720510757;
    private boolean isRushHour;
    private final Date nowTime = Calendar.getInstance().getTime();
    private boolean isSecondAppointBeforeValid;
    private boolean isSecondAppointAfterValid;
    private int receivedDurationInSec;
    private boolean appointmentValidated, firstRun;
    private String receivedCinema;
    private int validCounter;
    private PolylineOptions lineOptions;
    private int failCounter = 0;
    private String labelString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_map);

        if (savedInstanceState != null) {
            destination = savedInstanceState.getParcelable("destinationKey");
            lineOptions = savedInstanceState.getParcelable("lineOptionsKey");
            appointmentValidated = savedInstanceState.getBoolean("appointmentValidatedKey");
            firstRun = savedInstanceState.getBoolean("firstRunKey");
            location = savedInstanceState.getParcelable("locationKey");
            labelString = savedInstanceState.getString("labelStringKey");

        } else {
            destination = null;
            appointmentValidated = false;
            lineOptions = null;
            firstRun = true;
            location = null;
            labelString = "";
        }
        if (!isNetworkConnected()) {
            exitMap("noInternet");
        }
        mapInit();

        placeTypes = getResources().getStringArray(R.array.place_type);
        ArrayAdapter dataAdapter = new ArrayAdapter<String>(PlanMap.this, android.R.layout.simple_spinner_item, placeTypes);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mapConfirmBtn = (Button) findViewById(R.id.mapConfirmBtn);
        destinationText = (TextView) findViewById(R.id.destinationText);
        dateFormat = new SimpleDateFormat("EEE dd MMM yyyy HH:mm");
        receivedIntent = getIntent();
        receivedListIndex = receivedIntent.getIntExtra("listIndexKey", 0);
        receivedDateString = receivedIntent.getStringExtra("dateKey");
        receivedAppointList = receivedIntent.getParcelableArrayListExtra("listKey");
        receivedDuration = receivedIntent.getIntExtra("durationMinsKey", 0);
        receivedDurationInSec = receivedDuration * 60;
        selectedTypeString = receivedIntent.getStringExtra("typeKey");
        receivedCinema = receivedIntent.getStringExtra("cinemaKey");

        createCoordinates();

        checkRushHour();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        super.onBackPressed();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;


        mapConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appointmentValidated) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("destinationKey", (Parcelable) destination);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } else {
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                }

            }
        });





        /*
        // Place Picker
        Intent intent = null;
        try {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            //intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);

            intent = intentBuilder.build(PlanMap.this);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
        startActivityForResult(intent, PLACE_PICKER_REQUEST);
        */

    }

    public void mapInit() {
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .enableAutoManage(this, this)
                    .build();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public void startNavigation() {
        if (!isNetworkConnected()) {
            return;
        }
        mMap.clear();
        clickedflag = true;
        checkSettings();
        if (location != null) {
            Log.i("Location Info", "Location achieved!");
            addCurrentLocationMarker();
            origin = new LatLng(location.getLatitude(), location.getLongitude());

            if (selectedTypeString != null) {
                try {
                    appointmentValidated = findPlace(selectedTypeString);
                    firstRun = false;
                } catch (ParseException e) {
                    firstRun = true;
                    e.printStackTrace();
                }
            }

        } else {
            Toast toast = Toast.makeText(PlanMap.this, "Location not Available", Toast.LENGTH_LONG);
            toast.show();
            Log.i("Location Info", "Location not Available");
        }
    }


    protected boolean findPlace(String place) throws ParseException {

        checkRushHour();
        boolean typeNullFlag = false, appointValid = false;

        ArrayList<Place> placeList = new ArrayList<>();


        switch (place) {
            case "Supermarket":
                placeList = superMarkets;
                break;
            case "Cinema":
                placeList = cinemas;
                break;
            case "Gas Station":
                placeList = gasStations;
                break;
            default:
                Toast.makeText(PlanMap.this, "No Place selected", Toast.LENGTH_SHORT).show();
                destination = null;
                typeNullFlag = true;
                break;
        }
        if (place.equals("Cinema")) {
            if (receivedCinema.contains("Odeon")) {
                destination = cinemas.get(0);
            } else if (receivedCinema.contains("Texnopolis")) {
                destination = cinemas.get(3);
            } else if (receivedCinema.contains("Kornaros")) {
                destination = cinemas.get(1);
            } else {
                destination = null;
            }
        } else {
            destination = findNearestOpenPlace(placeList, origin);
        }


        if (!typeNullFlag && destination != null) {

            LatLng destinationLatLng = new LatLng(destination.getLat(), destination.getLon());
            Date appointTime, appointTimeFrame[], nowArriveTime;
            Calendar cal = Calendar.getInstance();
            boolean askUser = false;
            int originToMainTravelTime;
            validCounter = 0;

            originToMainTravelTime = calculateTravelTime(origin, destinationLatLng, isRushHour);
            appointTime = dateFormat.parse(receivedDateString);                         //appointTime: Tested Appointment Date
            appointTimeFrame = appointWindow(appointTime, receivedDurationInSec, -300, 300);               //appointTimeFrame[1]: Tested Appointment start, appointTimeFrame[0]: Tested Appointment end, all with added 5min window

            cal.setTime(nowTime);
            cal.add(Calendar.SECOND, originToMainTravelTime);
            nowArriveTime = cal.getTime();
            if (appointTimeFrame[0].after(nowArriveTime)) {                 //check if he can make it in time,  appointment valid only if now+travelTime < appointmentStart
                for (int i = 0; i < receivedAppointList.size(); i++) {      //check other appointments

                    if (dateFormat.parse(receivedAppointList.get(i).getDateString()).before(nowTime)) {
                        validCounter++;
                        continue;
                    }
                    testAppointment(i, appointTime);
                    if (appSame || appInside || appInclusive || appAfterWithoutTravelTime && appBeforeWithoutTravelTime) {  //Check if main appointment overlaps with another at the selected time

                        appointValid = false;
                        Log.i("AppointmentControl", "Main Appointment overlaps with secondary, Main start time:" + appointTimeFrame[0] + " end time:" + appointTimeFrame[1]);
                        Log.i("AppointmentControl", "Main Appointment overlaps with secondary, Main Coordinates:" + origin);
                        Log.i("AppointmentControl", "Main Appointment overlaps with secondary, Secondary start time:" + appointTimeFrame[0] + " end time:" + appointTimeFrame[1]);
                        exitMap("invalid");
                        break;
                    } else {
                        if (appAfterWithTravelTime || appBeforeWithTravelTime) {                //check if they overlap with travel time
                            if (appAfterWithoutTravelTime || appBeforeWithoutTravelTime) {      //check if they overlap without travel time

                                appointValid = false;
                                Toast.makeText(PlanMap.this, "Main Appointment overlaps with secondary", Toast.LENGTH_SHORT).show();
                                Log.i("AppointmentControl", "Main Appointment overlaps with secondary, Main start time:" + appointTimeFrame[0] + " end time:" + appointTimeFrame[1]);
                                Log.i("AppointmentControl", "Main Appointment overlaps with secondary, Main Coordinates:" + origin);
                                Log.i("AppointmentControl", "Main Appointment overlaps with secondary, Secondary start time:" + appointTimeFrame[0] + " end time:" + appointTimeFrame[1]);
                                exitMap("invalid");
                                break;
                            } else {

                                ArrayList<Place> samePointsList;
                                samePointsList = findSameDirectionPoints(placeList, origin, destinationLatLng);
                                if (samePointsList.isEmpty()) {
                                    destination = findNearestOpenPlace(placeList, new LatLng(receivedAppointList.get(i).getPlace().getLat(), receivedAppointList.get(i).getPlace().getLon())); //found samedirection
                                } else {
                                    destination = findNearestOpenPlace(samePointsList, new LatLng(receivedAppointList.get(i).getPlace().getLat(), receivedAppointList.get(i).getPlace().getLon()));
                                }
                                if (destination != null) {
                                    testAppointment(i, appointTime);
                                } else {

                                    Intent intent = new Intent();
                                    setResult(Activity.RESULT_CANCELED, intent);
                                    exitMap("closed");

                                }

                                if (appSame || appInside || appInclusive || appAfterWithTravelTime || appBeforeWithTravelTime) {
                                    appointValid = false;
                                    Toast.makeText(PlanMap.this, "Main Appointment overlaps with secondary", Toast.LENGTH_SHORT).show();
                                    Log.i("AppointmentControl", "Main Appointment overlaps with secondary, Main start time:" + appointTimeFrame[0] + " end time:" + appointTimeFrame[1]);
                                    Log.i("AppointmentControl", "Main Appointment overlaps with secondary, Main Coordinates:" + origin);
                                    Log.i("AppointmentControl", "Main Appointment overlaps with secondary, Secondary start time:" + receivedAppointList.get(i).getDateString());
                                    Log.i("AppointmentControl", "Main Appointment overlaps with secondary, Secondary Coordinates:" + new LatLng(receivedAppointList.get(i).getPlace().getLat(), receivedAppointList.get(i).getPlace().getLon()));
                                    exitMap("invalid");
                                    break;


                                } else {
                                    if (samePointsList.isEmpty()) {
                                        askUser = true;

                                    }
                                    validCounter++;
                                }
                            }
                        } else {
                            validCounter++;

                        }
                    }
                }
            } else {
                appointValid = false;

                Log.i("AppointmentControl", "Cant make it in time, Main start time:" + appointTimeFrame[0] + " end time:" + appointTimeFrame[1]);
                Log.i("AppointmentControl", "Cant make it in time, Main Coordinates:" + origin);
                exitMap("noTime");
            }

            appointValid = validCounter == receivedAppointList.size();


            if (appointValid) {
                Toast.makeText(PlanMap.this, "Appointment Valid", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PlanMap.this, "Appointment Invalid", Toast.LENGTH_SHORT).show();
                Log.i("AppointmentControl", "invalid confirm");
            }


            // Getting URL to the Google Directions API
            if (appointValid && !askUser) {


                String url = getDirectionsUrl(origin, destinationLatLng);

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
            } else if (appointValid && askUser) {
                showWarnDialog();
            }
        }

        if (destination == null) {
            Toast.makeText(PlanMap.this, "All " + place + "s" + " are closed at the selected time", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            setResult(Activity.RESULT_CANCELED, intent);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    finish();
                }
            }, 2000);
        }
        return appointValid;
    }

    public boolean checkIfopen(Place testedPlace, Date appointTime, Date appointEnd) {
        Calendar calendar = Calendar.getInstance();
        Date openTime, closeTime;
        calendar.setTime(appointTime);
        calendar.set(Calendar.HOUR_OF_DAY, testedPlace.getOpenHour());
        openTime = calendar.getTime();
        calendar.setTime(appointTime);
        calendar.set(Calendar.HOUR_OF_DAY, testedPlace.getCloseHour());
        closeTime = calendar.getTime();
        if (closeTime.before(openTime)) {
            calendar.add(Calendar.DATE, 1);
            closeTime = calendar.getTime();
        }
        boolean isOpen = appointTime.after(openTime) && appointTime.before(closeTime) && appointEnd.after(openTime) && appointEnd.before(closeTime) || closeTime.equals(openTime);
        return isOpen;
    }

    public void showWarnDialog() {
        AlertDialog.Builder warnDialog = new AlertDialog.Builder(PlanMap.this);
        warnDialog.setTitle("Warning: Different Direction");
        warnDialog
                .setMessage("The closest route to your selected appointment is out of your way for your other appointments, Do you want to proceed?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String url = getDirectionsUrl(origin, new LatLng(destination.getLat(), destination.getLon()));

                        DownloadTask downloadTask = new DownloadTask();

                        // Start downloading json data from Google Directions API
                        downloadTask.execute(url);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exitMap("user");
                        dialog.cancel();
                    }
                });

        warnDialog.show();
    }

    public void showSettingsDialog(String reason) {
        AlertDialog.Builder settingsDialog = new AlertDialog.Builder(PlanMap.this);
        String titleMsg = "error", dialogMsg = "error";
        if (reason.equals("noInternet")) {
            titleMsg = "No Internet Connection";
            dialogMsg = "Internet connection could not be established, check your settings and try again";
        } else if (reason.equals("connection_failed")) {
            titleMsg = "Location Request Failed";
            dialogMsg = "Connection with Location Provider Failed. Disable and re-enable your GPS setting and try again";
        }
        settingsDialog.setTitle(titleMsg);
        settingsDialog
                .setMessage(dialogMsg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        finish();
                        dialog.cancel();
                    }
                });

        settingsDialog.show();

    }

    public void exitMap(final String reason) {
        if (reason.equals("user")) {
            Toast.makeText(PlanMap.this, "Appointment canceled by user", Toast.LENGTH_SHORT).show();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    finish();
                }
            }, 2000);
        } else if (reason.equals("invalid")) {
            Toast.makeText(PlanMap.this, "Appointment invalid: time overlaps with another", Toast.LENGTH_SHORT).show();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    finish();
                }
            }, 2000);
        } else if (reason.equals("noTime")) {
            Toast.makeText(PlanMap.this, "Appointment invalid: Can't make it in time", Toast.LENGTH_SHORT).show();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    finish();
                }
            }, 2000);
        } else if (reason.equals("closed")) {
            Toast.makeText(PlanMap.this, "They are all closed at the selected time", Toast.LENGTH_SHORT).show();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    finish();
                }
            }, 2000);
        } else if (reason.equals("connection_failed")) {
            showSettingsDialog(reason);
        } else if (reason.equals("noInternet")) {
            showSettingsDialog(reason);
        }

    }

    public void checkRushHour() {
        Calendar cal = Calendar.getInstance();
        Date mornRushHourStart, mornRushHourEnd, noonRushHourStart, noonRushHourEnd;
        cal.setTime(nowTime);
        cal.set(Calendar.HOUR_OF_DAY, 8);
        cal.set(Calendar.MINUTE, 0);
        mornRushHourStart = cal.getTime();
        cal.add(Calendar.HOUR_OF_DAY, 1);
        mornRushHourEnd = cal.getTime();
        cal.set(Calendar.HOUR_OF_DAY, 13);
        noonRushHourStart = cal.getTime();
        cal.add(Calendar.HOUR_OF_DAY, 2);
        noonRushHourEnd = cal.getTime();


        //if rushHour
//max euclidDistance 8km min rush hour avg speed 14km/h max time 34min,
        isRushHour = nowTime.after(mornRushHourStart) && nowTime.before(mornRushHourEnd) || nowTime.after(noonRushHourStart) && nowTime.before(noonRushHourEnd);
    }

    public ArrayList<Place> findSameDirectionPoints(ArrayList<Place> placeList, LatLng start, LatLng end) {

        ArrayList<Place> sameDirList = new ArrayList<>();
        for (int i = 0; i < placeList.size(); i++) {
            boolean isInSameDirection = testIfSameDirection(start, end, new LatLng(placeList.get(i).getLat(), placeList.get(i).getLon()));
            if (isInSameDirection) {
                sameDirList.add(placeList.get(i));
            }
        }
        return sameDirList;
    }

    public boolean testIfSameDirection(LatLng start, LatLng end, LatLng tested) {
        double xA = start.latitude, yA = start.longitude;
        double xC = tested.latitude, yC = tested.longitude;
        double xB = end.latitude, yB = end.longitude;        // Origin(xA,yA)  MainAppointment(xB,yB)  SecondAppointment(xC,yC), Conditions for secondary Appointment to be in the same Direction as main.
        boolean xyPosB = xB > xA && yB > yA && xC >= xA && xC <= xB && yC >= yA && yC <= yB;       // if(xA<xB) and yA<yB then for secondary to be in the same direction: xA<=xC<=xB  and yA<=yC<=yB  must be true
        boolean xyNegB = xB < xA && yB < yA && xC <= xA && xC >= xB && yC <= yA && yC >= yB;       // if(xA>xB) and yA>yB then for secondary to be in the same direction: xB<=xC<=xA  and yB<=yC<=yA  must be true
        boolean xPosyNegB = xB > xA && yB < yA && xC >= xA && xC <= xB && yC <= yA && yC >= yB;    // if(xA<xB) and yA>yB then for secondary to be in the same direction: xA<=xC<=xB  and yB<=yC<=yA  must be true
        boolean xNegyPosB = xB < xA && yB > yA && xC <= xA && xC >= xB && yC >= yA && yC <= yB;    // if(xA>xB) and yA<yB then for secondary to be in the same direction: xB<=xC<=xA  and yA<=yC<=yB  must be true
        boolean isInSameDirection = xyPosB || xyNegB || xPosyNegB || xNegyPosB;
        return isInSameDirection;
    }

    public void testAppointment(int index, Date appointTime) throws ParseException {
        LatLng destinationLatLng = new LatLng(destination.getLat(), destination.getLon());
        LatLng receivedAppointListLatLng = new LatLng(receivedAppointList.get(index).getPlace().getLat(), receivedAppointList.get(index).getPlace().getLon());
        Date tenMinBeforeSecondAppoint, tenMinBeforeAppoint, arrivalMainBefore, appointArriveTimeAfterSecond, appointArriveTimeBeforeSecond, appointTimeFrame[], listDateTimeFrame[], arrivalSecondBefore, arrivalSecondAfter, nowArriveTime, listTime, mornRushHourStart, mornRushHourEnd, noonRushHourStart, noonRushHourEnd;
        Calendar cal = Calendar.getInstance();
        appointTimeFrame = appointWindow(appointTime, receivedDurationInSec, -300, 300);
        int listDuration, originToSecondTravelTime, mainToSecondTravelTime;
        listTime = dateFormat.parse(receivedAppointList.get(index).getDateString());            //listTime: appointment(s) Date on List
        listDuration = receivedAppointList.get(index).getDuration() * 60;                            //listDuration: appointment(s) int duration on list
        mainToSecondTravelTime = calculateTravelTime(destinationLatLng, receivedAppointListLatLng, isRushHour);
        originToSecondTravelTime = calculateTravelTime(origin, receivedAppointListLatLng, isRushHour);
        cal.setTime(nowTime);
        cal.add(Calendar.SECOND, originToSecondTravelTime);
        arrivalSecondBefore = cal.getTime();               //listDateTimeFrameBefore[0]: Tested appointment Start time, listDateTimeFrameAfter[1]: Tested appointment EndTime plus the travelTime from main to tested locations, all with added 5min window
        listDateTimeFrame = appointWindow(listTime, listDuration, -300, 300);                                                                         //listDateTimeFrameAfter[0]: Tested appointment Start time, listDateTimeFrameAfter[1]: Tested appointment EndTime, all with added 5min window
        cal.setTime(appointTimeFrame[1]);
        cal.add(Calendar.SECOND, mainToSecondTravelTime);
        arrivalSecondAfter = cal.getTime();                                                                                                   //listDateTimeFrameAfter[0]: Tested appointment Start time minus the travelTime from main to tested locations, listDateTimeFrameAfter[1]: Tested appointment EndTime, all with added 5min window
        cal.setTime(listDateTimeFrame[1]);
        cal.add(Calendar.SECOND, mainToSecondTravelTime);
        arrivalMainBefore = cal.getTime();

        appBeforeWithTravelTime = listDateTimeFrame[1].before(appointTimeFrame[1]) && appointTimeFrame[0].before(listDateTimeFrame[1]) || arrivalMainBefore.after(appointTimeFrame[0]) && listDateTimeFrame[0].before(appointTimeFrame[0]) && listDateTimeFrame[0].after(nowTime) && arrivalSecondBefore.before(listDateTimeFrame[0]);   //Case main appointment start is within ending time of tested appointment, ending time is the actual time plus the travel time from tested to main appointment locations, appointStart < listEnd < appointEnd
        appAfterWithTravelTime = listDateTimeFrame[0].after(appointTimeFrame[0]) && listDateTimeFrame[0].before(appointTimeFrame[1]) || arrivalSecondAfter.after(listDateTimeFrame[0]) && listDateTimeFrame[0].after(appointTimeFrame[1]) && listDateTimeFrame[0].after(nowTime);       //Case main appointment is within the starting time of tested appointment, starting time of tested appointment is the actual time minus the travelTime from main to tested appointment locations,  locationAppointStart < listStart < appointEnd
        appSame = appointTimeFrame[0] == listDateTimeFrame[0] && appointTimeFrame[1] == listDateTimeFrame[1];                           //Case main appointment is at the same time with tested appointment, appointStart == listStart && appointEnd == listEnd
        appAfterWithoutTravelTime = listDateTimeFrame[0].after(appointTimeFrame[0]) && listDateTimeFrame[0].before(appointTimeFrame[1]);       //Case main appointment is within the starting time of tested appointment,  locationAppointStart < listStart < appointEnd
        appBeforeWithoutTravelTime = listDateTimeFrame[1].before(appointTimeFrame[1]) && appointTimeFrame[0].before(listDateTimeFrame[1]);   //Case main appointment start is within ending time of tested appointment,  appointStart < listEnd < appointEnd


        appInside = listDateTimeFrame[0].before(appointTimeFrame[0]) && appointTimeFrame[0].before(listDateTimeFrame[1]) && appointTimeFrame[1].after(listDateTimeFrame[0]) && appointTimeFrame[1].before(listDateTimeFrame[1]);  //Case main appointment's start and end times are within tested appointment's timeFrame   listAppointStart < appointStart < listAppointEnd  &&  listAppointStart < appointEnd <listAppointEnd
        appInclusive = listDateTimeFrame[0].after(appointTimeFrame[0]) && listDateTimeFrame[0].before(appointTimeFrame[1]) && listDateTimeFrame[1].after(appointTimeFrame[0]) && listDateTimeFrame[1].before(appointTimeFrame[1]); //Case tested appointment's start and end times are within main appointment's timeFrame  appointStart < listAppointStart < appointEnd   &&  appointStart < listAppointEnd <appointEnd

/*
        cal.setTime(nowTime);                                                                       //second appointment 10min wait window
        cal.add(Calendar.SECOND, originToSecondTravelTime + listDuration + mainToSecondTravelTime);
        appointArriveTimeAfterSecond = cal.getTime();                                               //appointArriveTimeAfterSecond: the arrival time for main appointment after the secondary appointment
        cal.setTime(appointTimeFrame[0]);
        cal.add(Calendar.MINUTE, 10);
        tenMinBeforeAppoint = cal.getTime();


        boolean inBetweenAppsBefore = (nowTime.before(listDateTimeFrame[0]) && listDateTimeFrame[1].before(appointTimeFrame[0])); //true if tested appointment start time is after nowTime and end time is before the main appointment's start time
        isSecondAppointBeforeValid = inBetweenAppsBefore && appointArriveTimeAfterSecond.before(appointTimeFrame[0]) && appointArriveTimeAfterSecond.after(tenMinBeforeAppoint); // appointArriveTimeAfterSecond must be within 10min window before main appointment's start for second appointment to be valid


        cal.setTime(appointTimeFrame[1]);                                                                       //second appointment 10min wait window
        cal.add(Calendar.SECOND, mainToSecondTravelTime);
        appointArriveTimeBeforeSecond = cal.getTime();                                               //appointArriveTimeBeforeSecond: the arrival time for tested appointment after the main appointment
        cal.setTime(listDateTimeFrame[0]);
        cal.add(Calendar.MINUTE, 10);
        tenMinBeforeSecondAppoint = cal.getTime();


        boolean isSecondAppAfterMain = (appointTimeFrame[1].before(listDateTimeFrame[0])); //true if tested appointment start time is after nowTime and end time is before the main appointment's start time
        isSecondAppointAfterValid = isSecondAppAfterMain && appointArriveTimeBeforeSecond.before(listDateTimeFrame[0]) && appointArriveTimeBeforeSecond.after(tenMinBeforeSecondAppoint); // appointArriveTimeBeforeSecond must be within 10min window before tested appointment's start for main appointment to be valid
*/

    }

    public Date[] appointWindow(Date appointTime, int duration, int startOffset, int endOffset) {
        Date timeWindow[] = new Date[2];
        Calendar cal = Calendar.getInstance();
        cal.setTime(appointTime);
        cal.add(Calendar.SECOND, startOffset);
        timeWindow[0] = cal.getTime();
        cal.setTime(appointTime);
        cal.add(Calendar.SECOND, duration + endOffset);
        timeWindow[1] = cal.getTime();
        return timeWindow;

    }

    public int calculateTravelTime(LatLng origin, LatLng destination, boolean rushHour) {
        int travelTime;
        double avgSpeed, distance;
        if (rushHour) {
            avgSpeed = 3.88888;
        }         // rush hour avg speed 14km/h == 233.3333m/min  == 3.8888m/sec
        else {
            avgSpeed = 6.94444;          //normal avg speed 25km/h == 416.6666m/min ==6.94444m/sec
        }
        distance = distFrom(origin, destination);
        travelTime = (int) Math.ceil(distance / avgSpeed);

        return travelTime;
    }

    public int pureCalculateTravelTime(double distance) {
        int travelTime;
        double avgSpeed;
        if (isRushHour) {
            avgSpeed = 3.88888;
        }         // rush hour avg speed 14km/h == 233.3333m/min  == 3.8888m/sec
        else {
            avgSpeed = 6.94444;          //normal avg speed 25km/h == 416.6666m/min ==6.94444m/sec
        }
        travelTime = (int) Math.ceil(distance / avgSpeed);
        return travelTime;
    }

    public String convertTime(int timeSeconds) {
        int days = 0, hours = 0, minutes = 0, seconds = 0, time = 0;    //1day = 86400s   1hour=3600s
        String timeString = "";
        days = timeSeconds / 86400;
        time = timeSeconds % 86400;
        hours = time / 3600;
        time = time % 3600;
        minutes = time / 60;
        time = time % 60;
        seconds = time;
        if (days != 0) {
            timeString = timeString.concat(days + "d ");
        }
        if (hours != 0) {
            timeString = timeString.concat(hours + "h ");
        }
        if (minutes != 0) {
            timeString = timeString.concat(minutes + "min ");
        }
        if (seconds != 0) {
            timeString = timeString.concat(seconds + "sec ");
        }
        return timeString;
    }


    @Nullable
    protected Place findNearestOpenPlace(ArrayList<Place> placeList, LatLng startPoint) throws ParseException {
        double dist = 0, tempDist;
        Place nearestPlace = null;
        boolean nullFlag = false;
        Date appointTime,appointEnd;
        appointTime = dateFormat.parse(receivedDateString);
        Calendar cal = Calendar.getInstance();
        cal.setTime(appointTime);
        cal.add(Calendar.SECOND,receivedDurationInSec);
        appointEnd = cal.getTime();
        for (int i = 0; i < placeList.size(); i++) {
            if (checkIfopen(placeList.get(i), appointTime,appointEnd)) {
                dist = distFrom(startPoint, new LatLng(placeList.get(i).getLat(), placeList.get(i).getLon()));
                nearestPlace = placeList.get(i);
                nullFlag = false;
                break;
            } else {
                nearestPlace = null;
                nullFlag = true;
            }
        }
        if (!nullFlag) {
            for (int i = 0; i < placeList.size(); i++) {
                if (checkIfopen(placeList.get(i), appointTime,appointEnd)) {
                    tempDist = distFrom(startPoint, new LatLng(placeList.get(i).getLat(), placeList.get(i).getLon()));
                    if (tempDist < dist) {
                        dist = tempDist;
                        nearestPlace = placeList.get(i);
                    }
                }
            }
        }

        return nearestPlace;
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        createLocation();
    }


    public void reDrawMap() {

        addCurrentLocationMarker();

        mMap.addPolyline(lineOptions);
        mMap.addMarker(new MarkerOptions().position(new LatLng(destination.getLat(), destination.getLon())).title(destination.getAddress()));
        destinationText.setText(labelString);
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        hera = new LatLng(35.339332, 25.133158);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hera, 17));
        if (firstRun) {
            startNavigation();
        } else {
            reDrawMap();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void addCurrentLocationMarker() {
        lat = location.getLatitude();
        lon = location.getLongitude();
        if (currMark != null) {
            currMark.remove();
        }
        whereNow = new LatLng(lat, lon);
        currMark = mMap.addMarker(new MarkerOptions().position(whereNow).title("EDO"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(whereNow, 17));
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
         gasStations.add(new Place(37.974122, 23.774079, "Revoil", "Gas Station", 7, 22));
        /*gasStations.add(new Place(37.967245, 23.774535, "kontino alla de prolavainei", "Gas Station", 9, 21));
        gasStations.add(new Place(37.989516, 23.744785, "eksw apo to dromo", "Gas Station", 9, 21));
        gasStations.add(new Place(37.986878, 23.752681, "sto dromo", "Gas Station", 9, 21));*/


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


  /*  public static double calcDist(LatLng latlang1, LatLng latlang2) {
        double lat1 = latlang1.latitude;
        double lng1 = latlang1.longitude;
        double lat2 = latlang2.latitude;
        double lng2 = latlang2.longitude;

        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;
        System.out.println("harv dist: " + dist);
        return dist;
    }*/

    public static double distFrom(LatLng latlang1, LatLng latlang2) {
        float[] results = new float[3];
        Location.distanceBetween(latlang1.latitude, latlang1.longitude, latlang2.latitude, latlang2.longitude, results);
        double dist;
        dist = (double) results[0];
        dist = dist * avgDivergence;
        /* double lat1 = latlang1.latitude;
        double lng1 = latlang1.longitude;
        double lat2 = latlang2.latitude;
        double lng2 = latlang2.longitude;

        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;
        System.out.println(dist);*/
        return dist;
    }

    public static double pureDistFrom(LatLng latlang1, LatLng latlang2) {
        float[] results = new float[3];
        Location.distanceBetween(latlang1.latitude, latlang1.longitude, latlang2.latitude, latlang2.longitude, results);
        double dist;
        dist = (double) results[0];
        /* double lat1 = latlang1.latitude;
        double lng1 = latlang1.longitude;
        double lat2 = latlang2.latitude;
        double lng2 = latlang2.longitude;

        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;
        System.out.println(dist);*/
        return dist;
    }

    public String convertDistance(double distance) {
        int km = 0, m = 0;
        String distanceString = "";
        km = (int) distance / 1000;
        distance = distance % 1000;
        m = (int) Math.ceil(distance);
        if (km != 0) {
            distanceString = distanceString.concat(km + "km ");
        }
        if (m != 0) {
            distanceString = distanceString.concat(m + "m ");
        }
        return distanceString;
    }


/*
protected void checkPermissions(){
    if (ContextCompat.checkSelfPermission(PlanMap.this, android.Manifest.
            permission.ACCESS_FINE_LOCATION) != PackageManager.
            PERMISSION_GRANTED) {

// Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(PlanMap.this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            Toast toast= Toast.makeText(PlanMap.this, "You previously revoked the Location permission", Toast.LENGTH_SHORT);
            toast.show();
            ActivityCompat.requestPermissions(PlanMap.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

        } else {

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(PlanMap.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }




    }
}
*/


    protected void createLocation() {
        if (ContextCompat.checkSelfPermission(PlanMap.this, android.Manifest.
                permission.ACCESS_FINE_LOCATION) != PackageManager.
                PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(PlanMap.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                Toast toast = Toast.makeText(PlanMap.this, "You previously revoked the Location permission", Toast.LENGTH_SHORT);
                toast.show();
                ActivityCompat.requestPermissions(PlanMap.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(PlanMap.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }


        }

        location = FusedLocationApi.getLastLocation(mGoogleApiClient);
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

                    Toast toast = Toast.makeText(PlanMap.this, "permission granted", Toast.LENGTH_SHORT);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mapInit();
                startNavigation();
            }
        } else {
            Toast toasty = Toast.makeText(PlanMap.this, "gps remains disabled", Toast.LENGTH_SHORT);
            toasty.show();
        }
    }



   /* protected void checkSettings() {
        */

    /**
     * check if required settings are enabled
     *//*
        createLocationRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result = SettingsApi
                .checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates states = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        createLocationRequest();

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    PlanMap.this,
                                    REQUEST_CHECK_SETTINGS);


                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.

                        break;
                }
            }
        });
    }*/
    protected void checkSettings() {
        if (!isNetworkConnected()) {
            return;
        }
        /**check if required settings are enabled*/
        createLocationRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                createLocationRequest();
                if (location == null) {
                    Toast.makeText(PlanMap.this, "Location Request Failed, Retrying...", Toast.LENGTH_SHORT);
                    for (int i = 0; i < 4; i++) {
                        if (location != null) {
                            break;
                        } else if (location == null && i == 3) {
                            exitMap("connection_failed");
                        } else {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            createLocationRequest();
                        }

                    }
                }
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(PlanMap.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }


//TODO orientation Map change

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                ArrayList points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);
                mMap.addPolyline(lineOptions);
            }
            /*
            ArrayList<LatLng> latlngs=new ArrayList<>();
            for (int k = 0; k < points.size(); k++){
                latlngs.add((LatLng) points.get(k));
            }
            int size = latlngs.size() - 1;
            float[] results = new float[1];
            sumDist = 0;

            for(int n = 0; n < size; n++){
                Location.distanceBetween(
                        latlngs.get(n).latitude,
                        latlngs.get(n).longitude,
                        latlngs.get(n+1).latitude,
                        latlngs.get(n+1).longitude,
                        results);
                sumDist += results[0];
            }*/

// Drawing polyline in the Google PlanMap for the i-th route
            try {

                mMap.addPolyline(lineOptions);
                mMap.addMarker(new MarkerOptions().position(new LatLng(destination.getLat(), destination.getLon())).title(destination.getAddress()));
                List<LatLng> LPoints = lineOptions.getPoints();


                sumDist = 0;
                if (LPoints.get(1) != null) {
                    for (int k = 0; k < LPoints.size() - 1; k++) {
                        sumDist = sumDist + pureDistFrom(LPoints.get(k), LPoints.get(k + 1));
                    }
                } else {
                    LatLng myOrigin = new LatLng(location.getLatitude(), location.getLongitude());
                    sumDist = pureDistFrom(myOrigin, LPoints.get(0));
                }
                int timeSec = pureCalculateTravelTime(sumDist);
                String travelTimeString = convertTime(timeSec);
                String travelDistanceString = convertDistance(sumDist);
                labelString = "Destination: " + destination.getAddress() + " Distance: " + travelDistanceString + " Travel Time: " + travelTimeString;
                destinationText.setText(labelString);
                //  searchText.setText("Distance: " + sumDist+ "Time: "+timeEst);
            } catch (NullPointerException nullEx) {
                nullEx.printStackTrace();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                failCounter++;
                retryMapRequest();
                Log.i("Map Requests", "amount exceeded");
            }
        }

    }

    public void displayExceptionMessage(String msg) {

    }

    public void retryMapRequest() {
        if (failCounter == 1) {
            Toast.makeText(this, "Connection failed, Retrying...", Toast.LENGTH_SHORT).show();
        }
        if (failCounter < 20) {
            startNavigation();
        } else {
            exitMap("connection_failed");
        }

    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable("destinationKey", destination);
        savedInstanceState.putBoolean("appointmentValidatedKey", appointmentValidated);
        savedInstanceState.putParcelable("lineOptionsKey", lineOptions);
        savedInstanceState.putBoolean("firstRunKey", firstRun);
        savedInstanceState.putParcelable("locationKey", location);
        savedInstanceState.putString("labelStringKey", labelString);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        destination = savedInstanceState.getParcelable("destinationKey");
        appointmentValidated = savedInstanceState.getBoolean("appointmentValidatedKey");
        lineOptions = savedInstanceState.getParcelable("lineOptionsKey");
        firstRun = savedInstanceState.getBoolean("firstRunKey");
        location = savedInstanceState.getParcelable("locationKey");
        labelString = savedInstanceState.getString("labelStringKey");
    }

}
