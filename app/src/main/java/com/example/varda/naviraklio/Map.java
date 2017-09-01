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
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.google.android.gms.location.LocationServices.*;

public class Map extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private LatLng hera;
    private double lat, lon;
    private Location location;
    private GoogleApiClient mGoogleApiClient;
    private EditText searchText;
    private LocationRequest mLocationRequest;
    private final int REQUEST_CHECK_SETTINGS = 1, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9, PLACE_PICKER_REQUEST = 2;// unique identifiers
    private boolean clickedflag;
    private Button whereAmI;
    private LatLng whereNow;
    private Marker currMark;
    private Button findButton;
    private double sumDist;
    Spinner spinner;
    String[] placeTypes;
    List<Coordinate> superMarkets;
    List<GasStation> gasStations;
    List<Coordinate> cinemas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

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
        searchText = (EditText) findViewById(R.id.searchText);
        spinner = (Spinner) findViewById(R.id.spinner);
        placeTypes = getResources().getStringArray(R.array.place_type);
        ArrayAdapter dataAdapter = new ArrayAdapter<String>(Map.this, android.R.layout.simple_spinner_item, placeTypes);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        findButton = (Button) findViewById(R.id.findButton);
        whereAmI = (Button) findViewById(R.id.whereAmI);
        createCoordinates();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        createLocation();
        clickedflag = false;
        mMap = googleMap;
        hera = new LatLng(35.339332, 25.133158);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hera, 17));
        searchText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    searchText.setText("Showing Heraklion Marker");
                    mMap.addMarker(new MarkerOptions().position(hera).title("Liontaria"));
                   /* Uri gmmIntentUri = Uri.parse("geo:35.339332, 25.133158?q=restaurants");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);*/
                    //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&type=restaurant&keyword=cruise&key=YOUR_API_KEY
                    return true;
                } else {
                    return false;
                }

            }
        });


        whereAmI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedflag = true;
                createLocation();
                if (location != null) {
                    Log.i("Location Info", "Location achieved!");
                    addCurrentLocationMarker();
                } else {
                    Toast toast = Toast.makeText(Map.this, "Location not Available", Toast.LENGTH_LONG);
                    toast.show();
                    checkSettings();
                    Log.i("Location Info", "Location not Available");
                }


            }
        });

        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                clickedflag = true;
                createLocation();
                if (location != null) {
                    Log.i("Location Info", "Location achieved!");
                    addCurrentLocationMarker();
                    String spinnerSelection;
                    spinnerSelection = spinner.getSelectedItem().toString();
                    findPlace(spinnerSelection);
                } else {
                    Toast toast = Toast.makeText(Map.this, "Location not Available", Toast.LENGTH_LONG);
                    toast.show();
                    checkSettings();
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

            intent = intentBuilder.build(Map.this);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
        startActivityForResult(intent, PLACE_PICKER_REQUEST);
        */

    }

    protected void findPlace(String place) {
        LatLng origin;
        double dist;
        LatLng dest;
        double tempDist;
        origin = new LatLng(location.getLatitude(), location.getLongitude());
        switch (place) {
            case "Supermarket":

                dist = distFrom(origin, superMarkets.get(0).getCoord());
                Coordinate nearestSupermarket = superMarkets.get(0);
                for (Coordinate tempCoor : superMarkets) {
                    tempDist = distFrom(origin, tempCoor.getCoord());
                    if (tempDist < dist) {
                        dist = tempDist;
                        nearestSupermarket = tempCoor;
                    }
                }


                dest = nearestSupermarket.getCoord();


                break;
            case "Cinema":
                dist = distFrom(origin, cinemas.get(0).getCoord());

                Coordinate nearestCinema = cinemas.get(0);
                for (Coordinate tempCoor : cinemas) {
                    tempDist = distFrom(origin, tempCoor.getCoord());
                    if (tempDist < dist) {
                        dist = tempDist;
                        nearestCinema = tempCoor;
                    }
                }


                dest = nearestCinema.getCoord();
                break;
            case "Gas Station":
                dist = distFrom(origin, gasStations.get(0).getCoord());
                Coordinate nearestGasStation = gasStations.get(0);
                for (Coordinate tempCoor : gasStations) {
                    tempDist = distFrom(origin, tempCoor.getCoord());
                    if (tempDist < dist) {
                        dist = tempDist;
                        nearestGasStation = tempCoor;
                    }
                }


                dest = nearestGasStation.getCoord();
                break;
            default:
                Toast.makeText(Map.this, "No Place selected", Toast.LENGTH_SHORT).show();
                dest = null;
                break;
        }
        if (dest != null) {
            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);

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
    }

    protected void checkSettings() {
        /**check if required settings are enabled*/
        createLocation();
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
                        createLocation();
                        createLocationRequest();

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    Map.this,
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
        superMarkets.add(new Coordinate(35.340685, 25.133643, "Chalkiadakis", "Supermarket"));
        superMarkets.add(new Coordinate(35.337384, 25.121930, "LIDL", "Supermarket"));
        superMarkets.add(new Coordinate(35.338468, 25.139354, "AB", "Supermarket"));
        superMarkets.add(new Coordinate(35.337481, 25.132863, "BAZAAR", "Supermarket"));
        superMarkets.add(new Coordinate(35.339136, 25.155434, "Sklavenitis", "Supermarket"));
        superMarkets.add(new Coordinate(35.341716, 25.136238, "papadaki", "Supermarket"));
        superMarkets.add(new Coordinate(35.326724, 25.131095, "Sklavenitis", "Supermarket"));
        superMarkets.add(new Coordinate(35.326251, 25.138878, "Sklavenitis", "Supermarket"));
        superMarkets.add(new Coordinate(35.337651, 25.126895, "Chalkiadakis", "Supermarket"));
        superMarkets.add(new Coordinate(35.338751, 25.119835, "Chalkiadakis", "Supermarket"));
        superMarkets.add(new Coordinate(35.324666, 25.133577, "Ariadni", "Supermarket"));
        superMarkets.add(new Coordinate(35.334394, 25.115245, "INKA", "Supermarket"));
        superMarkets.add(new Coordinate(35.324695, 25.124600, "AB", "Supermarket"));
        superMarkets.add(new Coordinate(35.323925, 25.112541, "LIDL", "Supermarket"));
        superMarkets.add(new Coordinate(35.319163, 25.144127, "INKA", "Supermarket"));
        superMarkets.add(new Coordinate(35.324660, 25.124514, "AB", "Supermarket"));
        superMarkets.add(new Coordinate(35.318393, 25.148246, "Chalkiadakis", "Supermarket"));
        superMarkets.add(new Coordinate(35.331733, 25.137689, "Chalkiadakis", "Supermarket"));
        superMarkets.add(new Coordinate(35.330157, 25.132282, "Chalkiadakis", "Supermarket"));
        superMarkets.add(new Coordinate(35.334359, 25.158718, "Chalkiadakis Max", "Supermarket"));
        superMarkets.add(new Coordinate(35.329072, 25.119279, "Chalkiadakis", "Supermarket"));
        superMarkets.add(new Coordinate(35.343307, 25.155190, "My Cretan Goods", "Supermarket"));
        superMarkets.add(new Coordinate(35.336788, 25.133692, "Alati tis Gis", "Supermarket"));
        superMarkets.add(new Coordinate(35.330241, 25.124522, "Kouts", "Supermarket"));


        //zografou
        superMarkets.add(new Coordinate(37.977817, 23.769849, "daily", "Supermarket"));
        Collections.sort(superMarkets, new ComparatorCoord());


        gasStations = new ArrayList<>();
        gasStations.add(new GasStation(35.338674, 25.141106, "SHELL", "Gas Station",24,24));
        gasStations.add(new GasStation(35.335309, 25.141536, "EKO", "Gas Station",24,24));
        gasStations.add(new GasStation(35.333256, 25.121656, "Tsiknakis Ioannis", "Gas Station",7,22));
        gasStations.add(new GasStation(35.330283, 25.108827, "ELIN", "Gas Station",7,22));
        gasStations.add(new GasStation(35.329145, 25.117691, "Christodoulakis", "Gas Station",7,22));
        gasStations.add(new GasStation(35.338607, 25.143821, "Giannakakis", "Gas Station",7,22));
        gasStations.add(new GasStation(35.336275, 25.121359, "Hanagia", "Gas Station",7,22));
        gasStations.add(new GasStation(35.333818, 25.117024, "Stamatakis", "Gas Station",7,22));
        gasStations.add(new GasStation(35.326903, 25.131728, "Koumoulas", "Gas Station",7,22));
        gasStations.add(new GasStation(35.338667, 25.141116, "SHELL", "Gas Station",24,24));
        gasStations.add(new GasStation(35.338795, 25.141556, "SHELL", "Gas Station",24,24));
        gasStations.add(new GasStation(35.338714, 25.143423, "BP", "Gas Station",24,24));
        gasStations.add(new GasStation(35.337829, 25.141788, "BP", "Gas Station",24,24));
        gasStations.add(new GasStation(35.336477, 25.146265, "BP", "Gas Station",24,24));
        gasStations.add(new GasStation(35.334352, 25.133687, "BP", "Gas Station",24,24));
        gasStations.add(new GasStation(35.324131, 25.139945, "Mavraki", "Gas Station",24,24));
        gasStations.add(new GasStation(35.332375, 25.122159, "Samolis BP", "Gas Station",24,24));
        gasStations.add(new GasStation(35.332414, 25.112785, "Aegean", "Gas Station",7,22));
        gasStations.add(new GasStation(35.319242, 25.133003, "EKO", "Gas Station",7,22));
        gasStations.add(new GasStation(35.320312, 25.125391, "Koumoulas", "Gas Station",7,22));
        gasStations.add(new GasStation(35.321124, 25.143192, "Androulakis", "Gas Station",7,22));
        gasStations.add(new GasStation(35.331358, 25.104039, "Xenakis", "Gas Station",24,24));
        gasStations.add(new GasStation(35.341186, 25.141900, "Avis", "Gas Station",7,22));
        gasStations.add(new GasStation(35.338016, 25.160950, "EKO", "Gas Station",7,22));

        //zografou
        gasStations.add(new GasStation(37.974122, 23.774079, "Revoil", "Gas Station",7,22));

        Collections.sort(gasStations, new ComparatorCoord());


        cinemas = new ArrayList<>();
        cinemas.add(new Coordinate(35.339880, 25.119728, "Odeon Talos", "Cinema"));
        cinemas.add(new Coordinate(35.340889, 25.136980, "Vintsenzos Kornaros", "Cinema"));
        cinemas.add(new Coordinate(35.338375, 25.136216, "Astoria", "Cinema"));
        cinemas.add(new Coordinate(35.335669, 25.070682, "Texnopolis", "Cinema"));
        cinemas.add(new Coordinate(35.337980, 25.158230, "Cine Studio", "Cinema"));
        cinemas.add(new Coordinate(35.338573, 25.129685, "Dedalos Club", "Cinema"));
        //zografou
        cinemas.add(new Coordinate(37.977369, 23.770716, "Aleka", "Cinema"));
        Collections.sort(cinemas, new ComparatorCoord());


    }


    public static double distFrom(LatLng latlang1, LatLng latlang2) {
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


/*
protected void checkPermissions(){
    if (ContextCompat.checkSelfPermission(Map.this, android.Manifest.
            permission.ACCESS_FINE_LOCATION) != PackageManager.
            PERMISSION_GRANTED) {

// Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(Map.this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            Toast toast= Toast.makeText(Map.this, "You previously revoked the Location permission", Toast.LENGTH_SHORT);
            toast.show();
            ActivityCompat.requestPermissions(Map.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

        } else {

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(Map.this,
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
        if (ContextCompat.checkSelfPermission(Map.this, android.Manifest.
                permission.ACCESS_FINE_LOCATION) != PackageManager.
                PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            // Here, thisActivity is the current activity

// Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(Map.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                Toast toast = Toast.makeText(Map.this, "You previously revoked the Location permission", Toast.LENGTH_SHORT);
                toast.show();
                ActivityCompat.requestPermissions(Map.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(Map.this,
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

                    Toast toast = Toast.makeText(Map.this, "permission granted", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    //Toast toast=Toast.makeText(Map.this,"permission denied",Toast.LENGTH_SHORT);
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

                if (clickedflag) {
                    Toast toasty = Toast.makeText(Map.this, "gps enabled press again for Location", Toast.LENGTH_SHORT);
                    toasty.show();
                } else {
                    Toast toasty = Toast.makeText(Map.this, "gps enabled", Toast.LENGTH_SHORT);
                    toasty.show();
                }
            } else {
                Toast toasty = Toast.makeText(Map.this, "gps remains disabled", Toast.LENGTH_SHORT);
                toasty.show();
            }
        }
        if (requestCode == PLACE_PICKER_REQUEST
                && resultCode == Map.RESULT_OK) {

            final Place place = PlacePicker.getPlace(this, data);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            String attributions = (String) place.getAttributions();
            if (attributions == null) {
                attributions = "";
            }

            searchText.setText(name + " " + address);


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
            PolylineOptions lineOptions = null;
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

// Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);

            List<LatLng>LPoints=lineOptions.getPoints();
            sumDist=0;
            if (LPoints.get(1) != null) {
                for (int k = 0; k < LPoints.size() - 1; k++) {
                    sumDist = sumDist + distFrom(LPoints.get(k), LPoints.get(k + 1));
                }
            } else {
                LatLng myOrigin = new LatLng(location.getLatitude(), location.getLongitude());
                sumDist = distFrom(myOrigin,  LPoints.get(0));
            }
            int timeEst;

            timeEst=(int) sumDist/333; // Average Driving speed in Heraklion 20km/h or 333m/min
            searchText.setText("Distance: " + sumDist+ "Time: "+timeEst);

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


}
