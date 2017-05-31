package edu.uw.abourn.audioroam;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

public class UserUploadsListActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private final ArrayList<Track> userUploads = new ArrayList<Track>();
    private TrackAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_uploads_list);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        getTracks();
    }

    public void getTracks() {
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference uploadsListRef = mDatabase.child("users/" + user.getUid() + "/uploads");
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
                            userUploads.add(track);
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
}
