package edu.uw.abourn.audioroam;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class WebViewDialog {
    private WebView wv;

    public WebViewDialog(Context context, String url) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("SoundCloud");

        wv = new WebView(context);
        wv.loadUrl(url);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);

                return true;
            }
        });

        alert.setView(wv);
        alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                wv.destroy();
                dialog.dismiss();
            }
        });
        alert.show();
    }

    //Returns the WebView itself from the dialog
    public WebView getWebView() {
        return wv;
    }

}