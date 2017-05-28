package edu.uw.abourn.audioroam;

import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.support.design.widget.BottomSheetBehavior.from;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";
    private GoogleMap mMap;
    private FloatingActionButton uploadFab;
    private BottomSheetBehavior uploadBottomSheetBehavior;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
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


        NavigationView nav = (NavigationView) findViewById(R.id.navView);
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.signOut:
                        FirebaseAuth.getInstance().signOut();
                        Intent loginIntent = new Intent(MapActivity.this, LoginActivity.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // so user cannot go "back" to map after signing out
                        startActivity(loginIntent);
                        return true;
                    case R.id.favoritesList:
                        Intent favoritesIntent = new Intent(MapActivity.this, FavoritesActivity.class);
                        startActivity(favoritesIntent);
                        return true;
                    case R.id.uploadsList:
                        // Intent uploadsIntent = new Intent(MapActivity.this, UploadsListActivity.class);
                        // startActivity(uploadsIntent);
                        return true;
                    default: return false;
                }
            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    // DEVELOPMENT COMMENT: Attach this to the onClick attribute of button inside bottom sheet.
    public void uploadTrack(View v) {

       /* Once xml is specified, get references to the views containing the data we are going to upload.
          Then, we can say view.getText().toString() and set the follwing Strings equal to that.
       */
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String artistName = "";
        String songName = "";
        String owner = user.getUid();
        String url = "";
        String comment = "";
        DateFormat format = new SimpleDateFormat("MM/dd/yy HH:mm a");
        Date date = new Date();
        String uploadTime = format.format(date);
        ArrayList<String> favoritedBy = new ArrayList<String>();
        double latitude = 0.0;
        double longitude = 0.0;
        // get last location--initialize in onConnected and then update in onLocationChanged
        // then call getLatitude and getLongitude, then change them to doubles...
        Track upload = new Track(artistName, songName, owner, url, comment, uploadTime, favoritedBy, latitude, longitude);
        DatabaseReference trackRef = mDatabase.child("tracks");
        trackRef.push().setValue(upload);


        // Then, get a reference to that newly uploaded songID, and add it to this user's list of uploads
        DatabaseReference userRef = mDatabase.child("users/" + user + "/uploads");
        // TODO: want to get the data that is already stored at location, then add the new songId to the list.
    }

    /*
    * TODO: Implement onCameraMoveListener (and associated methods)
    *
    * See links:
    * https://developers.google.com/maps/documentation/android-api/events#camera_change_events
    * https://stackoverflow.com/questions/38727517/oncamerachangelistener-is-deprecated
    *
    *
    * We should only iterate through the firebase database to look for songs on the map 1) when map is
    * created and 2) whenever the camera is moved.  By only loading markers that are present within the
    * current camera view, we avoid loading unecessary markers as well as from iterating over the database
    * excessively.
    *
    * */

}
