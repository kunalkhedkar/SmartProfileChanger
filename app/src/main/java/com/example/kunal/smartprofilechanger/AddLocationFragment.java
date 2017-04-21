package com.example.kunal.smartprofilechanger;


import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;

import static android.R.attr.radius;
import static com.example.kunal.smartprofilechanger.R.id.longitude;


public class AddLocationFragment extends Fragment {

    public final static String TAG = "LOG";
    public final static String LAT_KEY = "LAT_KEY";
    public final static String LNG_KEY = "LNG_KEY";
    private final static double RADIUS = 100;
    private final static long dwellTimeMillis = 1 * 1000;

    private GoogleApiClient googleApiClient;

    View view;
    private Spinner soundProfileSpinner;
    private Double LATITUDE, LONGITUDE;
    private TextView tv_lat, tv_lng;
    private EditText locationName;
    private Button addButton, cancelButton;


    MyDatabaseHelper myDatabaseHelper;
    private LocationFenceReceiver locationFenceReceiver;

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

    private void initGoogleAwareness() {
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(Awareness.API)
                .build();
        googleApiClient.connect();
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

        initGoogleAwareness();

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


                        createAndRegisterFence(lat, lng, loc_name);


                        setHomePageFragment();
                    } else
                        Toast.makeText(getContext(), "Location name already exists ", Toast.LENGTH_SHORT).show();
                    refillingDataToForm(loc_name, soundProfileSpinner.getSelectedItemPosition(), lat, lng);
                }



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

    private void refillingDataToForm(String loc_name, int position, Double lat, Double lng) {

        locationName.setText(loc_name);
        tv_lat.setText(String.valueOf(lat));
        tv_lng.setText(String.valueOf(lng));
        soundProfileSpinner.setSelection(position);

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


    private void createAndRegisterFence(final double lat, final double lng, final String loc_name_asfence_key) {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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


        Intent receiverIntent = new Intent(LocationFenceReceiver.LOCATION_RECEIVER_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 1, receiverIntent, 0);
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
                        Log.d(TAG, "onSuccess: Fail to resister fence " + loc_name_asfence_key);
                        Toast.makeText(getContext(), "Fail to resister fence", Toast.LENGTH_SHORT).show();
                    }
                });


    }


    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(locationFenceReceiver, new IntentFilter(LocationFenceReceiver.LOCATION_RECEIVER_ACTION));
    }

    @Override
    public void onResume() {
        super.onResume();
//        getActivity().registerReceiver(locationFenceReceiver, new IntentFilter(LocationFenceReceiver.LOCATION_RECEIVER_ACTION));
    }

    @Override
    public void onStop() {
/*        if (locationFenceReceiver!=null) {
            getActivity().unregisterReceiver(locationFenceReceiver);
            locationFenceReceiver=null;
        }
*/
        super.onStop();
    }




    private void setHomePageFragment() {
        HomeFragment homeFragment = new HomeFragment();

        FragmentManager fragmentManager = getFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_home, homeFragment).commit();


    }

}

