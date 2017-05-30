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

    public static WebViewFragment newInstance() {
        Log.v("WebView", "HELLO YES WE ARE ON");

        Bundle args = new Bundle();
        args.putString("url", "https://soundcloud.com/jakeowen/dont-think-i-cant-love-you");

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

        WebView wv = (WebView) v.findViewById(R.id.wvfragment);

        //Make sure JS is loaded so SoundCloud works
        WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true);

        //Load URL from bundle
        if(url != null && !url.equals("")) {
            wv.loadUrl(url);
            wv.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
        }


        return v;
    }

}