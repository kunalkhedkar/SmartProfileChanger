package com.example.kunal.smartprofilechanger;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    View view;

    ListView locationDetails_listview;

    MyDatabaseHelper myDatabaseHelper;


    private String[] FORM = new String[]{String.valueOf(R.string.LOCATION_NAME_DB_COLOUM), String.valueOf(R.string.LAT_DB_COLOUM), String.valueOf(R.string.LNG_DB_COLOUM), String.valueOf(R.string.SOUND_PROFILE_DB_COLOUM)};
    private int[] TO = new int[]{R.id.locationName, R.id.latitude, R.id.longitude, R.id.soundProfile};

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        myDatabaseHelper = new MyDatabaseHelper(getContext());


        locationDetails_listview = (ListView) view.findViewById(R.id.locations_detail_list_view);



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();


        build_location_list();
    }

    private void build_location_list() {

        ArrayList<HashMap<String, String>> data = populateData();

        SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), data,
                R.layout.location_item_list, FORM, TO);
        locationDetails_listview.setAdapter(simpleAdapter);

    }

    private ArrayList<HashMap<String, String>> populateData() {

        ArrayList<HashMap<String, String>> details = new ArrayList<>();
        String location_name;
        Double latitude, longitude;
        int sound_profile;


        Cursor cursor = myDatabaseHelper.getLocationFromDatabase();


        if (cursor.getCount() > 0) {


            while (cursor.moveToNext()) {
                location_name = cursor.getString(0);
                latitude = cursor.getDouble(1);
                longitude = cursor.getDouble(2);
                sound_profile = Integer.parseInt(cursor.getString(3));


                HashMap<String, String> locationDetail = new HashMap<>();

                locationDetail.put(String.valueOf(R.string.LOCATION_NAME_DB_COLOUM), location_name);
                locationDetail.put(String.valueOf(R.string.LAT_DB_COLOUM), String.valueOf(latitude));
                locationDetail.put(String.valueOf(R.string.LNG_DB_COLOUM), String.valueOf(longitude));
                locationDetail.put(String.valueOf(R.string.SOUND_PROFILE_DB_COLOUM), String.valueOf(sound_profile));


                details.add(locationDetail);

            }
        }
        return details;

    }


}
