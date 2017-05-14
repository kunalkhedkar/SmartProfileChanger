package com.example.kunal.smartprofilechanger;

import android.*;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import static com.google.android.gms.wearable.DataMap.TAG;

/**
 * Created by kunal on 7/5/17.
 * [  locationManager.requestLocationUpdates ]
 * if gps update location listner is continiusally running then only fence trigger
 * so this service will keep running loation update listner
 */

public class GPSService extends Service {

    private LocationManager locationManager;
    private LocationListener mListener;
    private static GPSService instance = null;


    @Override
    public void onCreate() {

        instance = this;
        super.onCreate();
    }


    public static boolean isInstanceCreated() {
        return instance != null;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;


    }
/*
    @Override
    public void onS(Intent intent, int startId) {
        super.onStart(intent, startId);


        LocationUpdateService();
    }
*/

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LocationUpdateService();


        return super.onStartCommand(intent, flags, startId);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        instance = null;

        Log.d(HomeNavigationActivity.TAG, "onDestroy: ");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(mListener);
    }

    public void LocationUpdateService() {

        mListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Previously mock location is cleared.
                // getLastKnownLocation(LocationManager.GPS_PROVIDER); will not return mock location.
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }


        };


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 0, 0, mListener);


    }


}
