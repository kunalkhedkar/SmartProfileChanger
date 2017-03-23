package com.example.kunal.smartprofilechanger;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.AudioManager;
import android.os.Bundle;

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


public class MainActivity extends Activity {

    private final static String TAG = "mainActivity";
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private static final String ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION";
    private GoogleApiClient mGoogleApiClient;
    private TextView status_tv;
    boolean statusFlag = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
        // updated...................
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


    public void onClickCheckButton(View view) {

        getCurrentLocation();
    }

    private void getCurrentLocation() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.show();

        if (checkLocationPermission()) {
            // Toast.makeText(this, "write code", Toast.LENGTH_SHORT).show();
            status_tv.setText("");

            Awareness.SnapshotApi.getLocation(mGoogleApiClient).
                    setResultCallback(new ResultCallback<LocationResult>() {
                        @Override
                        public void onResult(@NonNull LocationResult locationResult) {
                            if (!locationResult.getStatus().isSuccess()) {
                                status_tv.setText("Could not get location");
                                status_tv.setTextColor(Color.RED);
                                return;
                            } else {
                                Location location = locationResult.getLocation();
                                status_tv.setText("loc is : " + location.toString());
                                Log.d(TAG, "onResult: " + location.toString());
                                status_tv.setTextColor(Color.GREEN);
                            }
                        }
                    });

        }
        dialog.dismiss();
    }

    private boolean checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "No Permission granted", Toast.LENGTH_SHORT).show();

            ActivityCompat.requestPermissions(this,
                    new String[]{ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        } else {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            // Awareness API code goes here
            return true;
        }


        return false;
    }


}



