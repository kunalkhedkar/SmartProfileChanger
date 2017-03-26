package com.example.kunal.smartprofilechanger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;

import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;

import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;

import static java.lang.Thread.sleep;


public class MainActivity extends Activity {

    private final static String TAG = "mainActivity";
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private static final String ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION";
    boolean statusFlag = false;
    private GoogleApiClient mGoogleApiClient;
    private TextView status_tv;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
        // updated
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status_tv = (TextView) findViewById(R.id.status);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .build();
        mGoogleApiClient.connect();


    }


    public void onclick_changeProfileButton(View view) {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);


    }


    public void onClick_getCurrentLocationButton(View view) {


        checkGPS();
        getCurrentLocation();


    }


    public void onClick_getMapLocationButton(View view) {
        //TODO
    }


    public void checkGPS() {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Enable Location");

            builder.setMessage("Please enable location service to continue!!");

            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    dialog.dismiss();
                }
            });

            AlertDialog diag = builder.create();
            diag.show();


        }
    }


    private void getCurrentLocation() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Retrieving Location");
        dialog.setMessage("please wait");
        dialog.setCancelable(false);
        dialog.show();


        if (checkLocationPermission()) {
            status_tv.setText("");

            Awareness.SnapshotApi.getLocation(mGoogleApiClient).
                    setResultCallback(new ResultCallback<LocationResult>() {
                        @Override
                        public void onResult(@NonNull LocationResult locationResult) {
                            if (!locationResult.getStatus().isSuccess()) {
                                status_tv.setText("Could not get location");
                                status_tv.setTextColor(Color.RED);

                                if (dialog.isShowing())
                                    dialog.dismiss();

                            } else {
                                Location location = locationResult.getLocation();
                                status_tv.setText("loc is : " + location.toString());
                                Log.d(TAG, "onResult: " + location.toString());
                                status_tv.setTextColor(Color.GREEN);

                                if (dialog.isShowing())
                                    dialog.dismiss();
                            }
                        }
                    });
        }
    }

    private boolean checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "No Permission granted", Toast.LENGTH_SHORT).show();

            ActivityCompat.requestPermissions(this,
                    new String[]{ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        } else {
            //Permission granted
            return true;
        }


        return false;
    }


}



