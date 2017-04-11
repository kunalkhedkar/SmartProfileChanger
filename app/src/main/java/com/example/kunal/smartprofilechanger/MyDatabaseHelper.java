package com.example.kunal.smartprofilechanger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kunal on 6/4/17.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "db_LocationDetails";
    private static final String LOCATION_TABLE = "table_location";


    public MyDatabaseHelper(Context context) {

        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + LOCATION_TABLE +
                " (LOCATION_NAME TEXT,LAT DOUBLE,LNG DOUBLE,SOUND_PROFILE INTEGER)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + LOCATION_TABLE);
        onCreate(db);

    }

    public boolean insertLocationToDatabase(
            String LOCATION_NAME, double LAT, double LNG, int SOUND_PROFILE) {

        long result = -1;

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put("LOCATION_NAME", LOCATION_NAME);
        contentValues.put("LAT", LAT);
        contentValues.put("LNG", LNG);
        contentValues.put("SOUND_PROFILE", SOUND_PROFILE);

        try {

            result = db.insert(LOCATION_TABLE, null, contentValues);

        } catch (Exception e) {
            return false;
        }

        if (result == -1) {
            return false;
        } else
            return true;
    }


    public Cursor getLocationFromDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor result = db.rawQuery("select * from " + LOCATION_TABLE, null);
        return result;
    }

}