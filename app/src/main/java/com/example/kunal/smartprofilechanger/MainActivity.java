package com.example.kunal.smartprofilechanger;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.HeadphoneStateResult;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;

public class MainActivity extends Activity {

    private final static String TAG = "mainActivity";
    private GoogleApiClient mGoogleApiClient;
    private TextView status_tv;
    boolean statusFlag = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
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




    public void onClickCheckButton(View view) {

        if (CheckHeadphoneStatus()) {
            status_tv.setText("headphone is plugged in");
        } else {

            status_tv.setText("headphone is NOT plugged in");
        }
    }


    boolean CheckHeadphoneStatus() {
        final boolean flag=false;

        Awareness.SnapshotApi.getHeadphoneState(mGoogleApiClient).setResultCallback(new ResultCallback<HeadphoneStateResult>() {
            @Override
            public void onResult(@NonNull HeadphoneStateResult headphoneStateResult) {

                if (headphoneStateResult.getStatus().isSuccess()) {
                    HeadphoneState headphonestate = headphoneStateResult.getHeadphoneState();
                    if (headphonestate.getState() == HeadphoneState.PLUGGED_IN) {
                        statusFlag = true;
                    } else {
                        statusFlag = false;
                    }


                }
            }
        });
        return statusFlag;
    }



}



