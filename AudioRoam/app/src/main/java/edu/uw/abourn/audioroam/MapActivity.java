package edu.uw.abourn.audioroam;


import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.content.Intent;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.Manifest;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.support.design.widget.BottomSheetBehavior.from;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "MapActivity";
    private GoogleMap mMap;
    private GoogleApiClient myGoogleApiClient;

    private static final int LOC_REQUEST_CODE = 1;

    private FloatingActionButton uploadFab;
    private BottomSheetBehavior uploadBottomSheetBehavior;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if(myGoogleApiClient == null) {
            myGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this) //what objects can respond to callbacks
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API) //which api I want client to connect to
                    .build();
        }
    }

    //Calls to connect to the Google API client when the application is started
    @Override
    protected void onStart() {
        myGoogleApiClient.connect();
        super.onStart();

//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

        // set up the upload bottom sheet
        View uploadBottomSheet = findViewById(R.id.upload_bottom_sheet);
        uploadBottomSheetBehavior = BottomSheetBehavior.from(uploadBottomSheet);
        // collapse the sheet so it is "peeking"
        uploadBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        // register listener for upload fab
        uploadFab = (FloatingActionButton) findViewById(R.id.fab_upload);
        uploadFab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // bottom sheet as view
                if (uploadBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    uploadBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    // TODO: change fab to submit here
                } else {
                    // return to "peek" state
                    uploadBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    // TODO: change fab back to upload button
                }
                Log.v(TAG, " New BottomSheetState: " + uploadBottomSheetBehavior.getState());
            }
        });

    }

    //Disconnects from the Google API client when the application is stopped
    //Checks to see if the application has permission to save a file
    @Override
    protected void onStop() {
        myGoogleApiClient.disconnect();
        super.onStop();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(10000);
        request.setFastestInterval(5000);

        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            // if permission granted, generate a listener for location change/send location request
            mMap.setMyLocationEnabled(true);
            LocationServices.FusedLocationApi.requestLocationUpdates(myGoogleApiClient, request, this);
        } else {
            // request the permission if not there..
            // get permission
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOC_REQUEST_CODE);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //if location has changed
        if(location != null) {
            LatLng initialLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(initialLocation));
        }
    }
    
}
