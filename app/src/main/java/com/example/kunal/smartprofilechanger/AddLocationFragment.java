package com.example.kunal.smartprofilechanger;


import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class AddLocationFragment extends Fragment {

    public final static String LAT_KEY = "LAT_KEY";
    public final static String LNG_KEY = "LNG_KEY";

    View view;
    private Spinner soundProfileSpinner;
    private Double LATITUDE, LONGITUDE;
    private TextView tv_lat, tv_lng;
    private EditText locationName;
    private Button addButton, cancelButton;


    MyDatabaseHelper myDatabaseHelper;

    public AddLocationFragment() {
        // Required empty public constructor
    }


    public static AddLocationFragment newInstance(double latitude, double longitude) {

        AddLocationFragment fragment = new AddLocationFragment();

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

        view = inflater.inflate(R.layout.fragment_add_location, container, false);


        myDatabaseHelper = new MyDatabaseHelper(getContext());

        locationName = (EditText) view.findViewById(R.id.edit_location);
        tv_lat = (TextView) view.findViewById(R.id.value_lat);
        tv_lng = (TextView) view.findViewById(R.id.value_lng);
        soundProfileSpinner = (Spinner) view.findViewById(R.id.spinner_soundProfile);

        addButton = (Button) view.findViewById(R.id.addButton);
        cancelButton = (Button) view.findViewById(R.id.cancelButton);

        // add
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNotEmptyField()) {
                    String loc_name = locationName.getText().toString();
                    int soundProfile = getSoundProfileCode(soundProfileSpinner.getSelectedItem().toString());
                    Double lat = Double.parseDouble(tv_lat.getText().toString());
                    Double lng = Double.parseDouble(tv_lng.getText().toString());


                    if (myDatabaseHelper.insertLocationToDatabase(loc_name, lat, lng, soundProfile)) {
                        Toast.makeText(getContext(), "Location added successfully", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(getContext(), "Fail to add Location,Try again ", Toast.LENGTH_SHORT).show();
                }

                setHomePageFragment();

            }
        });


        //  cancel
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Cancel", Toast.LENGTH_SHORT).show();


                setHomePageFragment();

            }
        });





        setValue_lat_lng();

        String items[] = this.getContext().getResources().getStringArray(R.array.soundProfile_array);
        ArrayAdapter adapter = new ArrayAdapter(this.getActivity(),
                R.layout.support_simple_spinner_dropdown_item, items);
        soundProfileSpinner.setAdapter(adapter);

        return view;

    }


    private int getSoundProfileCode(String s) {
        String soundProfileArray[] = getContext().getResources().getStringArray(R.array.soundProfile_array);

        if (s.equals(soundProfileArray[0])) {

            return AudioManager.RINGER_MODE_NORMAL;

        } else if (s.equals(soundProfileArray[1])) {

            return AudioManager.RINGER_MODE_VIBRATE;

        } else {
            return AudioManager.RINGER_MODE_SILENT;
        }
    }


    public boolean isNotEmptyField() {
        if (locationName.getText().toString().length() <= 0)
            return false;
        else
            return true;
    }

    private void setValue_lat_lng() {

        tv_lat.setText(String.valueOf(LATITUDE));
        tv_lng.setText(String.valueOf(LONGITUDE));

    }

    private void setHomePageFragment() {
        HomeFragment homeFragment = new HomeFragment();

        FragmentManager fragmentManager = getFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_home, homeFragment).commit();


    }

}

