package com.example.kunal.smartprofilechanger;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceQueryRequest;
import com.google.android.gms.awareness.fence.FenceQueryResult;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;

/**
 * Created by kunal on 25/4/17.
 */

public class FenceOperations {

    private final static String TAG = "FenceOperations";
    private final static double RADIUS = 100;
    private final static long dwellTimeMillis = 1 * 1000;

    private Context context;
    private LocationFenceReceiver locationFenceReceiver;
    private GoogleApiClient googleApiClient;
    private PendingIntent pendingIntent;

    public FenceOperations(Context context) {
        this.context = context;
        initGoogleAwareness();
    }

    private void initGoogleAwareness() {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Awareness.API)
                .build();
        googleApiClient.connect();
    }


    public void register_SingleFence(final double lat, final double lng, final String loc_name_asfence_key) {

        unRegister_SingleFence(loc_name_asfence_key);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        AwarenessFence spaceFence = LocationFence.in(lat, lng, RADIUS, dwellTimeMillis);
        locationFenceReceiver = new LocationFenceReceiver();

        if (pendingIntent == null) {
            Intent receiverIntent = new Intent(LocationFenceReceiver.LOCATION_RECEIVER_ACTION);
            pendingIntent = PendingIntent.getBroadcast(context, 1, receiverIntent, 0);
        }
        // register to Google Awareness
        Awareness.FenceApi.updateFences(googleApiClient,
                new FenceUpdateRequest.Builder().addFence(loc_name_asfence_key, spaceFence, pendingIntent).build()).
                setResultCallback(new ResultCallbacks<Status>() {
                    @Override
                    public void onSuccess(@NonNull Status status) {
                        Log.d(TAG, "onSuccess: Fence has been registered " + loc_name_asfence_key);
                    }

                    @Override
                    public void onFailure(@NonNull Status status) {
                        Log.d(TAG, "onFailure: Fail to resister fence " + loc_name_asfence_key);
                        Toast.makeText(context, "Fail to resister fence", Toast.LENGTH_SHORT).show();
                    }
                });


    }


    public void unRegister_SingleFence(final String fenceKey) {

        Awareness.FenceApi.updateFences(
                googleApiClient,
                new FenceUpdateRequest.Builder()
                        .removeFence(fenceKey)
                        .build()).setResultCallback(new ResultCallbacks<Status>() {
            @Override
            public void onSuccess(@NonNull Status status) {
                Log.i(TAG, "Fence " + fenceKey + " successfully removed.  ");

            }

            @Override
            public void onFailure(@NonNull Status status) {

                Log.i(TAG, "Fence " + fenceKey + " could NOT be removed.");
            }
        });
    }


    public boolean isFenceExists(final String fencekey) {

        final boolean[] result = new boolean[1];
        Awareness.FenceApi.queryFences(googleApiClient,
                FenceQueryRequest.forFences(fencekey)).setResultCallback(new ResultCallback<FenceQueryResult>() {
            @Override
            public void onResult(@NonNull FenceQueryResult fenceQueryResult) {
                if (!fenceQueryResult.getStatus().isSuccess()) {

                    result[0] = false;

                } else
                    result[0] = true;

            }

        });
        return result[0];
    }


    public void registerAgainAllFence() {
        MyDatabaseHelper db = new MyDatabaseHelper(context);

        Cursor cursor = db.getLocationFromDatabase();

        if (cursor.getCount() > 0) {

            String fenceName = cursor.getString(0);

            if (!isFenceExists(fenceName)) {


                double latitude = cursor.getDouble(1);
                double longitude = cursor.getDouble(2);
                int sound_profile = Integer.parseInt(cursor.getString(3));

                register_SingleFence(latitude, longitude, fenceName);

            }


        }

    }


}
