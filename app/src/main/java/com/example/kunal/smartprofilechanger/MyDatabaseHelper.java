package com.example.kunal.smartprofilechanger;

import android.content.ContentValues;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.sql.SQLIntegrityConstraintViolationException;

import static java.sql.Types.DOUBLE;

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
                " (LOCATION_NAME TEXT  PRIMARY KEY,LAT DOUBLE,LNG DOUBLE,SOUND_PROFILE INTEGER)");

/*
        db.execSQL("create table " + LOCATION_TABLE +
                " ( " + R.string.LOCATION_NAME_DB_COLOUM + " TEXT ," +
                "  + R.string.LAT_DB_COLOUM + " DOUBLE," +
                 " + R.string.LNG_DB_COLOUM + "DOUBLE," +
                 " + R.string.SOUND_PROFILE_DB_COLOUM + " INTEGER)");
*/

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


        result = db.insertWithOnConflict(LOCATION_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);


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
