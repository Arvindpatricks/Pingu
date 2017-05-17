package com.hackadroid.pingu.com.hackadroid.pingu.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.log4j.Logger;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hackadroid.pingu.R;

import static android.support.design.R.attr.title;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final Logger log3 = Logger.getLogger(LocationActivity.class);
    public int GPSoff = 0;
    public double latitude;
    public double longitude;
    GoogleMap googleMap;
    /* GPS Constant Permission */
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;

    /* Position */
    private static final int MINIMUM_TIME = 10000;  // 10s
    private static final int MINIMUM_DISTANCE = 50; // 50m

    TextView txtLat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        SupportMapFragment supportMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        supportMapFragment.getMapAsync(this);
        Toast.makeText(getApplicationContext(), "map", Toast.LENGTH_LONG).show();
        Button b = (Button) findViewById(R.id.get_loc);
        txtLat = (TextView) findViewById(R.id.loc1);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log3.info("Inside Button click !");
                //Check for Connection
                GPSoff = checkGPSConnection();
                if (GPSoff == 0) {

                    showMessageOKCancel("You need to turn Location On",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(onGPS);
                                }
                            });
                } else {
                    Toast.makeText(getApplicationContext(), "GPS is enabled!", Toast.LENGTH_LONG).show();
                }

                //Get the current location
                getLocation();


            }

        });


    }


    /**
     *checkGPSConnection checks for the GPS
     *
     *@return An integer value which is equal to 1 if GPS is enabled and 0 if disabled
     */

    private int checkGPSConnection() {
        try {

            GPSoff = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);

        } catch (Settings.SettingNotFoundException e) {

            e.printStackTrace();
        }
        return GPSoff;
    }

    /*
     *Alert Dialog Box for enabling GPS connection settings
     */
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(LocationActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    /*
      The self-check for access permission is done before fetching current location for best provider network

     */
    private void getLocation() {

        String bestprovider;
        Criteria locationCriteria;
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationCriteria = new Criteria();
        Toast.makeText(getApplicationContext(), "Get Current location", Toast.LENGTH_LONG).show();
        bestprovider = manager.getBestProvider(locationCriteria, true);
        Location location;

        // API 23: we have to check if ACCESS_FINE_LOCATION and/or ACCESS_COARSE_LOCATION permission are granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // No one provider activated: prompt GPS
            if (bestprovider == null || bestprovider.equals("")) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }

            // At least one provider activated. Get the coordinates
            switch (bestprovider) {

                case "passive": {
                    manager.requestLocationUpdates(bestprovider, MINIMUM_TIME, MINIMUM_DISTANCE, (LocationListener) this.getParentActivityIntent());
                    break;
                }

                case "network":
                    break;

                case "gps":
                    break;


            }


            if (manager != null) {
                location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    LatLng lalng = new LatLng(latitude,longitude);
                    googleMap.addMarker(new MarkerOptions().position(lalng).title("Current Location"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(lalng));
                    googleMap.setTrafficEnabled(true);
                    txtLat.setText("Lat:" + latitude + "Lon:" + longitude);
                }
            }


        } else {
            // The ACCESS_COARSE_LOCATION is denied, then I request it and manage the result in
            // onRequestPermissionsResult() using the constant MY_PERMISSION_ACCESS_Coarse_LOCATION
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "else - if", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSION_ACCESS_COARSE_LOCATION);
            }
            // The ACCESS_FINE_LOCATION is denied, then I request it and manage the result in
            // onRequestPermissionsResult() using the constant MY_PERMISSION_ACCESS_FINE_LOCATION
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "else  - else ", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSION_ACCESS_FINE_LOCATION);
            }

        }
        //   return location;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_COARSE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted

                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_LONG).show();

                } else {
                    // permission denied
                    Toast.makeText(getApplicationContext(), "Access denied for coarse location", Toast.LENGTH_LONG).show();


                }
                break;
            }
            case MY_PERMISSION_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_LONG).show();
                } else {
                    // permission denied

                    Toast.makeText(getApplicationContext(), "Access denied for fine location", Toast.LENGTH_LONG).show();
                }
                break;
            }

        }
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * In this case, we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device.
     * This method will only be triggered once the user has installed
     Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap Map) {
        googleMap = Map;



    }

}





