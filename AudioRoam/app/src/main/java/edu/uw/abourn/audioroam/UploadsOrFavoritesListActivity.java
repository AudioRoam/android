package edu.uw.abourn.audioroam;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UploadsOrFavoritesListActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private final ArrayList<Track> tracks = new ArrayList<Track>();
    private TrackAdapter adapter;
    private ListView trackList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploads_or_favorites_list);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        String firebaseKey = intent.getStringExtra("firebaseKey");
        getTracks(firebaseKey);
        adapter = new TrackAdapter(this, R.layout.track_list_item, R.id.songName, tracks);
        trackList = (ListView)findViewById(R.id.trackList);
        TextView listTitle = (TextView)findViewById(R.id.listTitle);
        if (firebaseKey.equals("uploads")) {
            listTitle.setText("My Uploads");
        } else {
            listTitle.setText("Favorites");
        }
    }

    public void getTracks(String firebaseKey) {
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference uploadsListRef = mDatabase.child("users/" + user.getUid() + "/" + firebaseKey);
        final DatabaseReference tracksRef = mDatabase.child("tracks");
        uploadsListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String trackId = child.getKey();
                    DatabaseReference singleTrackRef = tracksRef.child(trackId);
                    singleTrackRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Track track = dataSnapshot.getValue(Track.class);
                            tracks.add(0, track);
                            //Log.v("Track" , track.artistName);
                            trackList.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void openUrl(View v) {
        String url = (String) v.getTag();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
