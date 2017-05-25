package edu.uw.abourn.audioroam;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

/**
 * Created by Tyler on 5/25/2017.
 */

public class PlayerActivity extends AppCompatActivity {
    //This is primarily an activity designed for testing purposes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Button button = (Button) findViewById(R.id.playerButton);
        button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewDialog wvdialog = new WebViewDialog(PlayerActivity.this, "https://soundcloud.com/tltombstone/we-are-number-one-the-living-tombstones-remix");
                WebView wv = wvdialog.getWebView();
                WebSettings webSettings = wv.getSettings();
                webSettings.setJavaScriptEnabled(true);
            }
        });
    }
}
