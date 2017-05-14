package com.example.kunal.smartprofilechanger;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.awareness.fence.FenceState;

import static java.security.AccessController.getContext;

/**
 * Created by kunal on 22/4/17.
 */

public class LocationFenceReceiver extends BroadcastReceiver {

    public static final String LOCATION_RECEIVER_ACTION = "com.example.kunal.smartprofilechanger.LOCATION_RECEIVER_ACTION";
    MyDatabaseHelper db;
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("LOG", "Inside onReceive: ");

        this.context = context;
        db = new MyDatabaseHelper(context);

        FenceState fenceState = FenceState.extract(intent);


        Cursor cursor = db.getLocationFromDatabase();

        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {


                if (TextUtils.equals(fenceState.getFenceKey(), cursor.getString(0))) {

                    if (fenceState.getCurrentState() == FenceState.TRUE) {
                        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setRingerMode(Integer.parseInt(cursor.getString(3)));

                        Toast.makeText(context, "Inside " + cursor.getString(0), Toast.LENGTH_LONG).show();
                        showNotification(cursor.getString(0), getSoundProfileInStringFormat(Integer.parseInt(cursor.getString(3))));

                    }
                }
            }
        }


    }

    private void showNotification(String loc_name, String soundProfile) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Location notification")
                        .setContentText("Inside " + loc_name + ",Sound profile is changed to " + soundProfile);

        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(101, mBuilder.build());

    }


    private String getSoundProfileInStringFormat(int sound_profile_code) {

        String soundProfileArray[] = context.getResources().getStringArray(R.array.soundProfile_array);

        if (sound_profile_code == AudioManager.RINGER_MODE_NORMAL) {


            return soundProfileArray[0];

        } else if (sound_profile_code == AudioManager.RINGER_MODE_VIBRATE) {

            return soundProfileArray[1];

        } else if (sound_profile_code == AudioManager.RINGER_MODE_SILENT) {

            return soundProfileArray[2];

        }

        return "Default";

    }



}
