package com.example.kunal.smartprofilechanger;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import static android.app.Activity.RESULT_OK;


public class FragmentAddLocation extends Fragment {

    public final static String LAT_KEY = "LAT_KEY";
    public final static String LNG_KEY = "LNG_KEY";

    View view;
    private Spinner soundProfile;
    private Double LATITUDE, LONGITUDE;
    private TextView tv_lat, tv_lng;
    private EditText locationName;


    public FragmentAddLocation() {
        // Required empty public constructor
    }


    public static FragmentAddLocation newInstance(double latitude, double longitude) {

        FragmentAddLocation fragment = new FragmentAddLocation();

        Bundle args = new Bundle();
        args.putDouble(LAT_KEY, latitude);
        args.putDouble(LNG_KEY, longitude);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            LATITUDE = getArguments().getDouble(LAT_KEY);
            LONGITUDE = getArguments().getDouble(LNG_KEY);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_location, container, false);


        locationName = (EditText) view.findViewById(R.id.edit_location);
        tv_lat = (TextView) view.findViewById(R.id.value_lat);
        tv_lng = (TextView) view.findViewById(R.id.value_lng);
        soundProfile = (Spinner) view.findViewById(R.id.spinner_soundProfile);

        setValue_lat_lng();

        String items[] = this.getContext().getResources().getStringArray(R.array.soundProfile_array);
        ArrayAdapter adapter = new ArrayAdapter(this.getActivity(),
                R.layout.support_simple_spinner_dropdown_item, items);
        soundProfile.setAdapter(adapter);

        return view;

    }

    private void setValue_lat_lng() {

        tv_lat.setText(String.valueOf(LATITUDE));
        tv_lng.setText(String.valueOf(LONGITUDE));

    }


}

