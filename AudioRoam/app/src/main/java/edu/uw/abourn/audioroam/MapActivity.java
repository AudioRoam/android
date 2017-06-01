package edu.uw.abourn.audioroam;


import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.content.res.ColorStateList;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.Manifest;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.ads.formats.NativeAd;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.R.attr.format;
import static android.support.design.widget.BottomSheetBehavior.from;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "MapActivity";
    private GoogleMap mMap;
    private GoogleApiClient myGoogleApiClient;

    private static final int LOC_REQUEST_CODE = 1;

    private FloatingActionButton uploadFab;
    private BottomSheetBehavior uploadBottomSheetBehavior;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mDatabase = FirebaseDatabase.getInstance().getReference();
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
        /*
        Button btn = (Button) findViewById(R.id.testButton);

         btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                WebViewFragment wv = WebViewFragment.newInstance();
                ft.replace(R.id.upload_bottom_sheet, wv, "WebView");
                ft.commit();
            }
        }); */
        ImageButton hamburgerIcon = (ImageButton) findViewById(R.id.hamburger);
        hamburgerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
                drawer.openDrawer(Gravity.LEFT);
            }
        });
        getMarkers();
    }

    public void getMarkers() {
        final DatabaseReference trackRef = mDatabase.child("tracks");
        trackRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Track track = child.getValue(Track.class);
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(track.latitude, track.longitude)));
                    marker.setTag(track);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        trackRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Track track = dataSnapshot.getValue(Track.class);
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(track.latitude, track.longitude)));
                    marker.setTag(track);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

                if (uploadBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    // validate and submit the uploaded track
                    Log.v(TAG, "collapsing bottom sheet");
                    // TODO: form validation toggle for submit button
                    // TODO: if form valid, submit track; else give feedback (snackbar?)
                    // TODO: on submit success:
                    //     - collapse bottom sheet and provide feedback on map (STATE_COLLAPSED)
                    //          - center on added pin, do some fancy confirmation animation
                    //     - revert button to open state (pink w/ add icon)
                    // TODO: all of the above (and maybe below) as helper function
                    uploadBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    uploadFab.setImageResource(R.drawable.ic_add_24dp);
                } else {
                    Log.v(TAG, "expanding bottom sheet");
                    Log.v(TAG, "fabsize: " + uploadFab.getSize());

                    // expand the bottom sheet
                    uploadBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    uploadFab.setImageResource(R.drawable.ic_keyboard_arrow_down_24dp);
                }
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
                        Intent favoritesIntent = new Intent(MapActivity.this, UploadsOrFavoritesListActivity.class);
                        favoritesIntent.putExtra("firebaseKey", "favorites");
                        startActivity(favoritesIntent);
                        return true;
                    case R.id.uploadsList:
                        Intent uploadsIntent = new Intent(MapActivity.this, UploadsOrFavoritesListActivity.class);
                        uploadsIntent.putExtra("firebaseKey", "uploads");
                        startActivity(uploadsIntent);
                        return true;
                    default: return false;
                }
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
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
        mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(Marker marker) {
                Track markerInfo = (Track) marker.getTag();
                String firebaseTrackKey = markerInfo.firebaseKey;
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference userRef = mDatabase.child("users");
                userRef.child(user.getUid() + "/favorites/" + firebaseTrackKey).setValue(1);
            }
        });

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

    //TODO: Make it so that a user is able to click on markers if they are in close proximity to the marker
    @Override
    public void onLocationChanged(Location location) {
        //if location has changed
        if(location != null) {
            LatLng initialLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(initialLocation));
        }
    }

    public void uploadTrack(View v) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Location location = null;
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            location = LocationServices.FusedLocationApi.getLastLocation(myGoogleApiClient);
        } else {

            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOC_REQUEST_CODE);
        }
        EditText artistInput = (EditText)findViewById(R.id.artistInput);
        EditText songInput = (EditText)findViewById(R.id.songNameInput);
        EditText songUrlInput = (EditText)findViewById(R.id.songUrlInput);
        EditText commentInput = (EditText) findViewById(R.id.commentInput);

        String artistName = artistInput.getText().toString();
        String songName = songInput.getText().toString();
        String owner = user.getUid();
        String url = songUrlInput.getText().toString();
        String comment = commentInput.getText().toString();

        if (!artistName.isEmpty() && !songName.isEmpty() && !url.isEmpty()) {

            DateFormat format = new SimpleDateFormat("MM/dd/yy HH:mm a");
            Date date = new Date();
            String uploadTime = format.format(date);
            ArrayList<String> favoritedBy = new ArrayList<String>(); // could probably delete...

            DatabaseReference trackRef = mDatabase.child("tracks");
            String trackId = trackRef.push().getKey();
            Track upload = new Track(artistName, songName, owner, url, comment, uploadTime, favoritedBy, location.getLatitude(), location.getLongitude(), trackId);
            trackRef.child(trackId).setValue(upload);

            // empty the inputs for future uploads
            artistInput.setText(null);
            songInput.setText(null);
            songUrlInput.setText(null);
            commentInput.setText(null);

            // Hide the bottom sheet once track is uploaded
            View uploadBottomSheet = findViewById(R.id.upload_bottom_sheet);
            uploadBottomSheetBehavior = BottomSheetBehavior.from(uploadBottomSheet);
            uploadBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            Snackbar.make(v, "Dropping the beat...", Snackbar.LENGTH_SHORT).show();

            DatabaseReference userRef = mDatabase.child("users");
            userRef.child(user.getUid() + "/uploads/" + trackId).setValue(1);
        } else {
            Snackbar.make(v, "Artist, Song, and URL Cannot be blank", Snackbar.LENGTH_SHORT).show();
        }
    }

    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private final View mContents;

        CustomInfoWindowAdapter() {
            mContents = getLayoutInflater().inflate(R.layout.marker_info_window, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            render(marker, mContents);
            return mContents;
        }

        private void render(Marker marker, View view) {
            final Track markerInfo = (Track) marker.getTag();
            TextView songTitle = (TextView) view.findViewById(R.id.songTitle);
            TextView uploadTime = (TextView) view.findViewById(R.id.uploadTime);
            TextView artist = (TextView) view.findViewById(R.id.artist);
            TextView comment = (TextView) view.findViewById(R.id.comment);

            songTitle.setText(markerInfo.songName);
            uploadTime.setText(markerInfo.uploadTime);
            artist.setText(markerInfo.artistName);
            comment.setText(markerInfo.comment);
        }

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
    * 
        OnCreate
            Load all the markers onto the map
            Set on click listener for each marker

        markerOnClick
            if (this marker is within a certain distance)
                do the OnClick stuff
                inflate bottom layout by passing in the url
                Show information about the marker
            else
                Snackbar user? Nah

        onLocationChanged
            see if there are markers within a certain range
                if so, change the appearance of the marker
     */
}
