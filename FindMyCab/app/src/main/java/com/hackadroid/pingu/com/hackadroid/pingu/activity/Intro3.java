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
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.log4j.Logger;

import com.hackadroid.pingu.R;

import static android.support.design.R.attr.title;

public class Intro3 extends AppCompatActivity {
    private static final Logger log3 = Logger.getLogger(MapViewFragment.class);
    public int GPSoff = 0;
    /* GPS Constant Permission */
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;

    /* Position */
    private static final int MINIMUM_TIME = 10000;  // 10s
    private static final int MINIMUM_DISTANCE = 50; // 50m


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro3);

        Button b = (Button) findViewById(R.id.b1);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log3.info("Inside Button click !");
                Toast.makeText(getApplicationContext(), "button activity", Toast.LENGTH_LONG).show();
                checkConnection();
                findloc();
                           }

        });

    }

    private void checkConnection() {

         /* GPS */
        String bestprovider;
        Criteria locationCriteria;
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Toast.makeText(getApplicationContext(), "Entering check", Toast.LENGTH_LONG).show();


        locationCriteria = new Criteria();
        locationCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
        locationCriteria.setAltitudeRequired(false);
        locationCriteria.setBearingRequired(false);
        locationCriteria.setCostAllowed(true);
        locationCriteria.setPowerRequirement(Criteria.NO_REQUIREMENT);


    /*    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getApplicationContext(), "GPS is disabled!Please turn on ", Toast.LENGTH_LONG).show();
            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(myIntent);
        } else {
            Toast.makeText(getApplicationContext(), "GPS is enabled!", Toast.LENGTH_LONG).show();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            manager.requestLocationUpdates(providerName, 20000, 100, locationListener);
        }*/

        try {

            GPSoff = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
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



    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(Intro3.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    private void findloc() {
 /* GPS */
        String bestprovider;
        Criteria locationCriteria;
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationCriteria = new Criteria();

        String provider = LocationManager.NETWORK_PROVIDER;
                Toast.makeText(getApplicationContext(), "Get Current location", Toast.LENGTH_LONG).show();



       bestprovider = manager.getBestProvider(locationCriteria, true);

        // API 23: we have to check if ACCESS_FINE_LOCATION and/or ACCESS_COARSE_LOCATION permission are granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // No one provider activated: prompt GPS
            if (bestprovider == null || bestprovider.equals("")) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }

            // At least one provider activated. Get the coordinates
            switch (bestprovider) {
                case "passive":
                    manager.requestLocationUpdates(bestprovider, MINIMUM_TIME, MINIMUM_DISTANCE, (LocationListener) this.getParentActivityIntent());
                    Location location = manager.getLastKnownLocation(bestprovider);
                    break;

                case "network":
                    break;

                case "gps":
                    break;

            }

            // One or both permissions are denied.
        }
        else {

            // The ACCESS_COARSE_LOCATION is denied, then I request it and manage the result in
            // onRequestPermissionsResult() using the constant MY_PERMISSION_ACCESS_Coarse_LOCATION
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSION_ACCESS_COARSE_LOCATION);
            }
            // The ACCESS_FINE_LOCATION is denied, then I request it and manage the result in
            // onRequestPermissionsResult() using the constant MY_PERMISSION_ACCESS_FINE_LOCATION
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this,
                        new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                        MY_PERMISSION_ACCESS_FINE_LOCATION);
            }

        }
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
                    } else {
                        // permission denied

                        Toast.makeText(getApplicationContext(), "Access denied for fine location", Toast.LENGTH_LONG).show();
                    }
                    break;
                }

            }
        }
   }





