package edu.uw.abourn.audioroam;


import android.content.pm.PackageManager;
import android.graphics.Color;
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

import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;

import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.Manifest;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ListIterator;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "MapActivity";
    private GoogleMap mMap;
    private GoogleApiClient myGoogleApiClient;

    private static final int LOC_REQUEST_CODE = 1;

    private FloatingActionButton uploadFab;
    private BottomSheetBehavior uploadBottomSheetBehavior;
    private BottomSheetBehavior webviewBottomSheetBehavior;
    private DatabaseReference mDatabase;

    private ArrayList<Track> trackList;
    private ArrayList<Marker> displayedMarkers;

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
        ImageButton hamburgerIcon = (ImageButton) findViewById(R.id.hamburger);
        hamburgerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
                drawer.openDrawer(Gravity.LEFT);
            }
        });
        final DatabaseReference trackRef = mDatabase.child("tracks");
        trackList = new ArrayList<Track>();
        displayedMarkers = new ArrayList<Marker>();
        trackRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Track track = child.getValue(Track.class);
                    trackList.add(track);
                }
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

        // set up the upload bottom sheet
        final View uploadBottomSheet = findViewById(R.id.upload_bottom_sheet);
        uploadFab = (FloatingActionButton) findViewById(R.id.fab_upload);
        uploadBottomSheetBehavior = BottomSheetBehavior.from(uploadBottomSheet);

        // set up the webview bottom sheet
        final View webviewBottomSheet = findViewById(R.id.webview_bottom_sheet);
//        uploadFab = (FloatingActionButton) findViewById(R.id.fab_upload);
        webviewBottomSheetBehavior = BottomSheetBehavior.from(webviewBottomSheet);

        // collapse the sheet so it is hidden
        uploadBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        uploadBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // NOTE: we won't change the fab icon here because it only does it after the state
                // has completely transitioned (visually lagging behind the sheet)
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//                Log.v(TAG, "offset: " + slideOffset);
                if (!(slideOffset != slideOffset)) {
                    // need to check for weird invalid values that seem to happen (NaN)
                    if (slideOffset > -0.8) { // smooth icon transition between hidden and expanded
                        uploadFab.setImageResource(R.drawable.ic_keyboard_arrow_down_24dp);
                    } else {
//                        Log.v(TAG, "changed to plus, offset is: " + slideOffset);
                        uploadFab.setImageResource(R.drawable.ic_add_24dp);
                    }
                }
            }
        });

//        webviewBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        webviewBottomSheet.setVisibility(View.GONE);


        // register listener for upload fab to control bottom sheet
        uploadFab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (uploadBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    // validate and submit the uploaded track
//                    Log.v(TAG, "collapsing bottom sheet");
                    uploadBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                } else if (uploadBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN){
//                    Log.v(TAG, "expanding bottom sheet");
                    // expand the bottom sheet
                    uploadBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
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
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(18));
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Track markerInfo = (Track) marker.getTag();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                WebViewFragment wv = WebViewFragment.newInstance(markerInfo.url);
                ft.replace(R.id.webView, wv, "WebView");
                ft.commit();
                TextView artistName = (TextView) findViewById(R.id.webview_artist);
                TextView songName = (TextView) findViewById(R.id.webview_title);
                artistName.setText("by "  + markerInfo.artistName);
                final ImageButton faveBtn = (ImageButton) findViewById(R.id.favoriteBtn);

                final String firebaseKey = markerInfo.firebaseKey;
                faveBtn.setTag(firebaseKey);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference favoritesRef = mDatabase.child("users/" + user.getUid() + "/favorites/");
                favoritesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean found = false;
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            //String key = (String)faveBtn.getTag();
                            if (child.getKey().equals(firebaseKey)) {
                                faveBtn.setColorFilter(Color.RED);
                                found = true;
                            } else if (!found) {
                                faveBtn.setColorFilter(Color.GRAY);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                songName.setText(markerInfo.songName);
                View webviewBottomSheet = findViewById(R.id.webview_bottom_sheet);
                webviewBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                webviewBottomSheet.setVisibility(View.VISIBLE);
            }
        });

    }

    public void favoriteTrack(final View v) {


        final String firebaseTrackKey = (String) v.getTag();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        final DatabaseReference favoritesRef = mDatabase.child("users/" + user.getUid() + "/favorites/");
        favoritesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean favoritedBefore = false;
                ImageButton btn = (ImageButton) v;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getKey().equals(firebaseTrackKey)) {
                        child.getRef().removeValue();
                        btn.setColorFilter(Color.GRAY);
                        favoritedBefore = true;
                    }
                }
                if (!favoritedBefore) {
                    btn.setColorFilter(Color.RED);
                    favoritesRef.child(firebaseTrackKey).setValue(1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

    @Override
    public void onLocationChanged(Location location) {
        //if location has changed
        if(location != null) {
            LatLng initialLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(initialLocation));
            for(Track track: trackList) {
                float results[] = new float[1];
                Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                        track.latitude, track.longitude, results);
                if(results[0] <= (float)100) {
                    Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(track.latitude, track.longitude)));
                    marker.setTag(track);
                    displayedMarkers.add(marker);
                }
            }
            if(displayedMarkers != null) {
                ListIterator<Marker> iterator = displayedMarkers.listIterator();
                while(iterator.hasNext()) {
                    Marker next = iterator.next();
                    float results[] = new float[1];
                    Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                            next.getPosition().latitude, next.getPosition().longitude, results);
                    if(results[0] > 100) {
                        next.remove();
                        iterator.remove();
                    }

                }
            }
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
            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())));
            marker.setTag(upload);
            displayedMarkers.add(marker);
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
            uploadTime.setText("Dropped on: " + markerInfo.uploadTime);
            artist.setText("by " + markerInfo.artistName);
            comment.setText(markerInfo.comment);
        }

    }

}
