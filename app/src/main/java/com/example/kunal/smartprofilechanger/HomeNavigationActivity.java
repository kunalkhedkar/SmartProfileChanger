package com.example.kunal.smartprofilechanger;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

public class HomeNavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private boolean firstTimeRunActivity = true;
    private static final int PLACE_PICKER_REQUEST_CODE = 123;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private static final String ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION";
    double latitude, longitude;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog progressDialog_placePicker;
    private BroadcastReceiver locationFenceReceiver;
    private SwitchCompat switchCompat;
    private Intent GPSServiceintent;

    public static final String TAG = "smartprofilechanger";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        initGoogleAwareness();

        registerLocationFenceReceiver();

        GPSServiceintent = new Intent(this, GPSService.class);

        //----------------------------------------------------------


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*  NOT USING floating button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();

        // switch button
        switchCompat = (SwitchCompat) MenuItemCompat.getActionView(menu.findItem(R.id.nav_switchOnOffGPS_item)).findViewById(R.id.drawer_switch_onOffGPS);

        if (GPSService.isInstanceCreated() && firstTimeRunActivity) {
            switchCompat.setChecked(true);
            firstTimeRunActivity = false;
        }

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (switchCompat.isChecked()) {
                    firstTimeRunActivity = false;
                    Toast.makeText(HomeNavigationActivity.this, "Activated", Toast.LENGTH_SHORT).show();
                    startService(GPSServiceintent);
                } else {
                    firstTimeRunActivity = false;
                    Toast.makeText(HomeNavigationActivity.this, "DeActivated", Toast.LENGTH_SHORT).show();
                    stopService(GPSServiceintent);
                }
            }
        });





        DisplayHomePage();

    }

    private void initGoogleAwareness() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void registerLocationFenceReceiver() {

        locationFenceReceiver = new LocationFenceReceiver();
        this.registerReceiver(locationFenceReceiver, new IntentFilter(LocationFenceReceiver.LOCATION_RECEIVER_ACTION));


    }


    private void DisplayHomePage() {

        HomeFragment homeFragment = new HomeFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_home, homeFragment).commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_addLocation_current) {


            getLocationFrom_currentLocation();


        } else if (id == R.id.nav_addLocation_map) {
            // place picker ->get response -> add location frag call


            getLocationFrom_map();


        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void getLocationFrom_currentLocation() {

        checkGPS();

        getCurrentLocation();

    }


    public void checkGPS() {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
           /* AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Enable Location");

            builder.setMessage("Please enable location service to continue!!");

            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    dialog.dismiss();
                }
            });

            AlertDialog diag = builder.create();
            diag.show();
*/

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Enable GPS");
            alertDialog.setMessage("Enable GPS in your settings for receiving active location details.");


            //Positive "YES" button
            alertDialog.setPositiveButton("SETTINGS", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    dialog.dismiss();

                }
            });

            // Negative "NO" Button
            alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    // Do nothing

                    dialog.cancel();
                }
            });

            alertDialog.show();

        }
    }


    private void getCurrentLocation() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Retrieving Location");
        dialog.setMessage("please wait");
        dialog.setCancelable(false);
        dialog.show();


        if (checkLocationPermission()) {

            Awareness.SnapshotApi.getLocation(mGoogleApiClient).
                    setResultCallback(new ResultCallback<LocationResult>() {
                        @Override
                        public void onResult(@NonNull LocationResult locationResult) {
                            if (!locationResult.getStatus().isSuccess()) {
                                Toast.makeText(HomeNavigationActivity.this, "Could not get location", Toast.LENGTH_SHORT).show();

                                if (dialog.isShowing())
                                    dialog.dismiss();

                            } else {
                                Location location = locationResult.getLocation();

                                Double latitude = location.getLatitude();
                                double longitude = location.getLongitude();

                                startFragmentAddLocation(latitude, longitude);

                                if (dialog.isShowing())
                                    dialog.dismiss();
                            }
                        }
                    });
        }
    }

    private boolean checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "No Permission granted", Toast.LENGTH_SHORT).show();

            ActivityCompat.requestPermissions(this,
                    new String[]{ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        } else {
            //Permission granted
            return true;
        }


        return false;
    }


    // from maps ------------------------------------------------------------
    private void getLocationFrom_map() {

        progressDialog_placePicker = new ProgressDialog(this);
        progressDialog_placePicker.setMessage("Loading... ");
        progressDialog_placePicker.show();

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            Intent intent = builder.build(this);

            startActivityForResult(intent, PLACE_PICKER_REQUEST_CODE);


        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }


    }


    // map response
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);

                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;

                if (progressDialog_placePicker.isShowing())
                    progressDialog_placePicker.dismiss();


                // call add location screen
                startFragmentAddLocation(latitude, longitude);


            }

        }
    }


    // start fragment - add location screen

    private void startFragmentAddLocation(double latitude, double longitude) {

        AddLocationFragment fragment_addLocation_ = AddLocationFragment.newInstance(latitude, longitude);
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_home, fragment_addLocation_).commitAllowingStateLoss();


    }


}



