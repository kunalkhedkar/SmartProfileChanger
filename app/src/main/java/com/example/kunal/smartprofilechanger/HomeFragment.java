package com.example.kunal.smartprofilechanger;


import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    View view;
    ImageView listview_menu;
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


        build_location_list();

    /*    listview_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View parentrow= (View) v.getParent();

                PopupMenu popupMenu=new PopupMenu(getContext(),v);
                MenuInflater inflater=popupMenu.getMenuInflater();
                inflater.inflate(R.menu.item_menu,popupMenu.getMenu());
                popupMenu.show();

            }
        });
*/
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
                R.layout.location_item_list, FORM, TO) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {


                View v = super.getView(position, convertView, parent);

                listview_menu = (ImageView) v.findViewById(R.id.list_menu);
                listview_menu.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        // menu [ delete,edit ]

                        PopupMenu popupMenu = new PopupMenu(getContext(), v);
                        MenuInflater inflater = popupMenu.getMenuInflater();
                        inflater.inflate(R.menu.item_menu, popupMenu.getMenu());

                        popupMenu.show();


                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                switch (item.getItemId()) {

                                    case R.id.delete_menu:
                                        Toast.makeText(getContext(), "TODO delete", Toast.LENGTH_SHORT).show();
                                        return true;
                                    case R.id.edit_menu:
                                        Toast.makeText(getContext(), "TODO edit", Toast.LENGTH_SHORT).show();
                                        return true;
                                }


                                return false;
                            }
                        });

                    }
                });
                return v;
            }


        };

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
                locationDetail.put(String.valueOf(R.string.SOUND_PROFILE_DB_COLOUM), getSoundProfileInStringFormat(sound_profile));


                details.add(locationDetail);

            }
        }
        return details;

    }

    private String getSoundProfileInStringFormat(int sound_profile_code) {

        String soundProfileArray[] = getContext().getResources().getStringArray(R.array.soundProfile_array);

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
