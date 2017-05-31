package edu.uw.abourn.audioroam;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 5/27/17.
 */

public class Track {
    public String artistName;
    public String songName;
    public String owner;
    public String url;
    public String comment;
    public String uploadTime;
    public  List<String> favoritedBy;
    public double latitude;
    public double longitude;
    public String firebaseKey;

    public Track() {
        // Default constructor required for Firebase.
    }

    public Track(String artistName, String songName, String owner, String url, String comment,
                 String uploadTime, ArrayList<String> favoritedBy, double latitude, double longitude, String firebaseKey) {
        this.artistName = artistName;
        this.songName = songName;
        this.owner = owner;
        this.url = url;
        this.comment = comment;
        this.uploadTime = uploadTime;
        this.favoritedBy = favoritedBy;
        this.latitude = latitude;
        this.longitude = longitude;
        this.firebaseKey = firebaseKey;
    }

}
