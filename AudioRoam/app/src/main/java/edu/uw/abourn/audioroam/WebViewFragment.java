package edu.uw.abourn.audioroam;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * A simple {@link Fragment} subclass.
 */
public class WebViewFragment extends Fragment {
//    private WebView wv;
//    private String url;

    public WebViewFragment() {
        // Required empty public constructor
    }

    public static WebViewFragment newInstance(String url) { //, String trackName, String artist) {
        Log.v("WebView", "HELLO YES WE ARE ON");

        Bundle args = new Bundle();
//        args.putString("artist", artist);
//        args.putString("trackName", trackName);
        args.putString("url", url);

        WebViewFragment fragment = new WebViewFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("WebView", "YES SIR IT MAKES A VIEW");
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_web_view, container, false);

        String url = getArguments().getString("url");

        final WebView wv = (WebView) v.findViewById(R.id.wvfragment);

        //Make sure JS is loaded so SoundCloud works
        WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true);
//        wv.getSettings().setLoadWithOverviewMode(true);
//        wv.getSettings().setUseWideViewPort(true);
        settings.setUserAgentString("Chrome/57.0.2987.133");

        //Load URL from bundle
        if(url != null && !url.equals("")) {

            String html = "<!DOCTYPE html><html> <head> <meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"target-densitydpi=high-dpi\" /> <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"> <link rel=\"stylesheet\" media=\"screen and (-webkit-device-pixel-ratio:1.5)\" href=\"hdpi.css\" /></head> <body style=\"background:black;margin:0 0 0 0; padding:0 0 0 0;\"> <iframe id=\"sc-widget " +
                    "\" width=\"100%\" height=\"50%\"" + // Set Appropriate Width and Height that you want for SoundCloud Player
                    " src=\"" + "https://w.soundcloud.com/player/?url=" + url   // Set Embedded url
                    + "\" frameborder=\"no\" scrolling=\"no\"></iframe>" +
                    "<script src=\"https://w.soundcloud.com/player/api.js\" type=\"text/javascript\"></script> </body> </html> ";

//            wv.loadUrl(url);
            wv.loadDataWithBaseURL("",html,"text/html", "UTF-8", "");
            wv.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, "https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/34090443");
                    wv.scrollTo(0, 0);
                }
            });
        }




        return v;
    }

}