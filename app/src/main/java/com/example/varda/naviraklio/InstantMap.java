package com.example.varda.naviraklio;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.location.places.ui.PlacePicker;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import static com.google.android.gms.location.LocationServices.*;

public class InstantMap extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private LatLng hera;
    private double lat, lon;
    private Location location;
    private GoogleApiClient mGoogleApiClient;
    private TextView instantDestLabel;
    private LocationRequest mLocationRequest;
    private final int REQUEST_CHECK_SETTINGS = 1, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9, PLACE_PICKER_REQUEST = 2;// unique identifiers
    private boolean clickedflag;
    private LatLng whereNow;
    private Marker currMark;
    private Button findButton;
    private double sumDist;
    private Spinner spinner;
    private String[] placeTypes;
    private List<Place> superMarkets;
    private List<Place> gasStations;
    private List<Place> cinemas;
    private LatLng origin;
    private Place destination;
    private PolylineOptions lineOptions;
    private boolean firstRun;
    private static final double avgDivergence = 1.8784960720510757;
    private int failCounter = 0;
    private boolean isRushHour;
    private String labelString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instant_map);
        if (savedInstanceState != null) {
            destination = savedInstanceState.getParcelable("destinationKey");
            lineOptions = savedInstanceState.getParcelable("lineOptionsKey");
            firstRun = savedInstanceState.getBoolean("firstRunKey");
            location = savedInstanceState.getParcelable("locationKey");
            labelString = savedInstanceState.getString("labelStringKey");

        } else {
            destination = null;
            lineOptions = null;
            firstRun = true;
            location = null;
            labelString = "";
        }
        // Create an instance of GoogleAPIClient.
        initMap();
        instantDestLabel = (TextView) findViewById(R.id.instantDestLabel);
        spinner = (Spinner) findViewById(R.id.spinner);
        placeTypes = getResources().getStringArray(R.array.place_type);
        ArrayAdapter dataAdapter = new ArrayAdapter<String>(InstantMap.this, android.R.layout.simple_spinner_item, placeTypes);
        dataAdapter.setDropDownViewResource(R.layout.type_spinner);
        spinner.setAdapter(dataAdapter);
        findButton = (Button) findViewById(R.id.findButton);
        createCoordinates();
        checkRushHour();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        clickedflag = false;
        mMap = googleMap;



        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                clickedflag = true;
                checkSettings();
                if (location != null) {
                    startNavigation();
                    firstRun = false;
                } else {
                    Toast toast = Toast.makeText(InstantMap.this, "Location not Available", Toast.LENGTH_LONG);
                    toast.show();
                    Log.i("Location Info", "Location not Available");
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

            intent = intentBuilder.build(InstantMap.this);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
        startActivityForResult(intent, PLACE_PICKER_REQUEST);
        */

    }

    public void initMap() {
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

    protected void findPlace(String place) {

        double dist;
        double tempDist;
        switch (place) {
            case "Supermarket":

                dist = distFrom(origin, new LatLng(superMarkets.get(0).getLat(), superMarkets.get(0).getLon()));
                Place nearestSupermarket = superMarkets.get(0);


                for (int i = 0; i < superMarkets.size(); i++) {
                    tempDist = distFrom(origin, new LatLng(superMarkets.get(i).getLat(), superMarkets.get(0).getLon()));
                    if (tempDist < dist) {
                        dist = tempDist;
                        nearestSupermarket = superMarkets.get(i);
                    }
                }


                destination = nearestSupermarket;


                break;
            case "Cinema":
                dist = distFrom(origin, new LatLng(cinemas.get(0).getLat(), cinemas.get(0).getLon()));

                Place nearestCinema = cinemas.get(0);
                for (int i = 0; i < cinemas.size(); i++) {
                    tempDist = distFrom(origin, new LatLng(cinemas.get(i).getLat(), cinemas.get(0).getLon()));
                    if (tempDist < dist) {
                        dist = tempDist;
                        nearestCinema = cinemas.get(i);
                    }
                }


                destination = nearestCinema;
                break;
            case "Gas Station":
                dist = distFrom(origin, new LatLng(gasStations.get(0).getLat(), gasStations.get(0).getLon()));
                Place nearestGasStation = gasStations.get(0);
                for (int i = 0; i < gasStations.size(); i++) {
                    tempDist = distFrom(origin, new LatLng(gasStations.get(i).getLat(), gasStations.get(0).getLon()));
                    if (tempDist < dist) {
                        dist = tempDist;
                        nearestGasStation = gasStations.get(i);
                    }
                }


                destination = nearestGasStation;
                break;
            default:
                Toast.makeText(InstantMap.this, "No Place selected", Toast.LENGTH_SHORT).show();
                destination = null;
                break;
        }
        if (destination != null) {
            String url = getDirectionsUrl(origin, new LatLng(destination.getLat(), destination.getLon()));

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API

            downloadTask.execute(url);
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        createLocation();
    }

    public void startNavigation() {
        checkSettings();
        origin = new LatLng(location.getLatitude(), location.getLongitude());
        Log.i("Location Info", "Location achieved!");
        addCurrentLocationMarker();
        String spinnerSelection;
        spinnerSelection = spinner.getSelectedItem().toString();
        findPlace(spinnerSelection);
    }


    /*protected void checkSettings() {
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
                                    InstantMap.this,
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
                            resolvable.startResolutionForResult(InstantMap.this,
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
        if (!firstRun) {
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


/*
protected void checkPermissions(){
    if (ContextCompat.checkSelfPermission(InstantMap.this, android.Manifest.
            permission.ACCESS_FINE_LOCATION) != PackageManager.
            PERMISSION_GRANTED) {

// Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(InstantMap.this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            Toast toast= Toast.makeText(InstantMap.this, "You previously revoked the Location permission", Toast.LENGTH_SHORT);
            toast.show();
            ActivityCompat.requestPermissions(InstantMap.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

        } else {

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(InstantMap.this,
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
        if (ContextCompat.checkSelfPermission(InstantMap.this, android.Manifest.
                permission.ACCESS_FINE_LOCATION) != PackageManager.
                PERMISSION_GRANTED) {

            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            // Here, thisActivity is the current activity

// Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(InstantMap.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                Toast toast = Toast.makeText(InstantMap.this, "You previously revoked the Location permission", Toast.LENGTH_SHORT);
                toast.show();
                ActivityCompat.requestPermissions(InstantMap.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(InstantMap.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }


        }

        location = FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    public void reDrawMap() {

        addCurrentLocationMarker();

        mMap.addPolyline(lineOptions);
        mMap.addMarker(new MarkerOptions().position(new LatLng(destination.getLat(), destination.getLon())).title(destination.getAddress()));
        instantDestLabel.setText(labelString);
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

    public void checkRushHour() {
        Calendar cal = Calendar.getInstance();
        Date nowTime = cal.getTime();
        Date mornRushHourStart, mornRushHourEnd, noonRushHourStart, noonRushHourEnd;
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

                    Toast toast = Toast.makeText(InstantMap.this, "permission granted", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    //Toast toast=Toast.makeText(InstantMap.this,"permission denied",Toast.LENGTH_SHORT);
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
                initMap();
                startNavigation();
            } else {
                Toast toasty = Toast.makeText(InstantMap.this, "gps remains disabled", Toast.LENGTH_SHORT);
                toasty.show();
            }
        }
        if (requestCode == PLACE_PICKER_REQUEST
                && resultCode == InstantMap.RESULT_OK) {

            final com.google.android.gms.location.places.Place place = PlacePicker.getPlace(this, data);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            String attributions = (String) place.getAttributions();
            if (attributions == null) {
                attributions = "";
            }

            instantDestLabel.setText(name + " " + address);


        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }


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
            ArrayList points = null;
            lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
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

// Drawing polyline in the Google InstantMap for the i-th route
            try {
                mMap.addPolyline(lineOptions);

                List<LatLng> LPoints = lineOptions.getPoints();
                sumDist = 0;
                if (LPoints.get(1) != null) {
                    for (int k = 0; k < LPoints.size() - 1; k++) {
                        sumDist = sumDist + distFrom(LPoints.get(k), LPoints.get(k + 1));
                    }
                } else {
                    LatLng myOrigin = new LatLng(location.getLatitude(), location.getLongitude());
                    sumDist = distFrom(myOrigin, LPoints.get(0));
                }
                int timeSec = pureCalculateTravelTime(sumDist);
                String travelTimeString = convertTime(timeSec);
                String travelDistanceString = convertDistance(sumDist);
                labelString = "Destination: " + destination.getAddress() + " Distance: " + travelDistanceString + " Travel Time: " + travelTimeString;
                instantDestLabel.setText(labelString);
                mMap.addMarker(new MarkerOptions().position(new LatLng(destination.getLat(), destination.getLon())).title(destination.getAddress()));

            } catch (NullPointerException e) {
                e.printStackTrace();
                Toast.makeText(InstantMap.this, "too many requests, retrying...", Toast.LENGTH_SHORT);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                    retryMapRequest();
                }

            }
        }

    }

    public void retryMapRequest() {
        if (failCounter == 1) {
            Toast.makeText(this, "Connection failed, Retrying...", Toast.LENGTH_SHORT).show();
        }
        if (failCounter < 20) {
            startNavigation();
        } else {
            Toast.makeText(InstantMap.this, "Connection with Location Provider Failed. Try Again Later", Toast.LENGTH_SHORT).show();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    finish();
                }
            }, 2000);
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
        savedInstanceState.putParcelable("lineOptionsKey", lineOptions);
        savedInstanceState.putBoolean("firstRunKey", firstRun);
        savedInstanceState.putParcelable("locationKey", location);
        savedInstanceState.putString("labelStringKey", labelString);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        destination = savedInstanceState.getParcelable("destinationKey");
        lineOptions = savedInstanceState.getParcelable("lineOptionsKey");
        firstRun = savedInstanceState.getBoolean("firstRunKey");
        location = savedInstanceState.getParcelable("locationKey");
        labelString = savedInstanceState.getString("labelStringKey");
    }

}
