package com.example.kunal.smartprofilechanger;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    View view;
    TextView tv_showdata;

    MyDatabaseHelper myDatabaseHelper;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        myDatabaseHelper = new MyDatabaseHelper(getContext());


        tv_showdata = (TextView) view.findViewById(R.id.showdata);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        getdata();
    }

    private void getdata() {
        tv_showdata.setText("");

        Cursor cursor = myDatabaseHelper.getLocationFromDatabase();


        if (cursor.getCount() > 0) {


            while (cursor.moveToNext()) {
                String loc_name = cursor.getString(0);
                double lat = cursor.getDouble(1);
                double lng = cursor.getDouble(2);
                int sound_profile = Integer.parseInt(cursor.getString(3));

                tv_showdata.append(loc_name);
                tv_showdata.append("\n" + lat);
                tv_showdata.append("\n" + lng);
                tv_showdata.append("\n" + sound_profile);
                tv_showdata.append("\n---------------------------------------------------------\n");


                cursor.moveToNext();

            }

        }


    }

}
