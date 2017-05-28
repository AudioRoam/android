package edu.uw.abourn.audioroam;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 5/27/17.
 */

public class Track {
    String artistName;
    String songName;
    String owner;
    String url;
    String comment;
    String uploadTime;
    List<String> favoritedBy;
    double latitude;
    double longitude;

    public Track() {
        // Default constructor required for Firebase.
    }

    public Track(String artistName, String songName, String owner, String url, String comment,
                 String uploadTime, ArrayList<String> favoritedBy, double latitude, double longitude) {
        this.artistName = artistName;
        this.songName = songName;
        this.owner = owner;
        this.url = url;
        this.comment = comment;
        this.uploadTime = uploadTime;
        this.favoritedBy = favoritedBy;
        this.latitude = latitude;
        this.longitude = longitude;
    }

}
