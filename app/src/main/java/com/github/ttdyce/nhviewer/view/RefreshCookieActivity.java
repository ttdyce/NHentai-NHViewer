package com.github.ttdyce.nhviewer.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.ttdyce.nhviewer.R;
import com.github.ttdyce.nhviewer.model.CookieStringRequest;

public class RefreshCookieActivity extends AppCompatActivity {
    String url = "https://nhentai.net";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh_cookie);

        /* Use a WebView to bypass cloudflare challenge (Retrieve cookie) */
        WebView wvRefreshCookie = findViewById(R.id.wvRefreshCookie);

        // clear cookie on create. on App open and Preference page item click enter this part
        CookieManager.getInstance().removeAllCookies(new ValueCallback<Boolean>() {
            @Override
            public void onReceiveValue(Boolean value) {
                // leave empty
            }
        });

        wvRefreshCookie.getSettings().setJavaScriptEnabled(true);
//        wvInvisibleSplash.getSettings().setUserAgentString("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:109.0) Gecko/20100101 Firefox/110.0");
        wvRefreshCookie.loadUrl(url);

        String cookies = CookieManager.getInstance().getCookie(url);
        Log.d("SplashActivitiy", "url: " + url);
        Log.d("SplashActivitiy", "Got cookie: " + cookies);
        Log.d("SplashActivitiy", "User agent: " + wvRefreshCookie.getSettings().getUserAgentString());
        checkCookie(wvRefreshCookie.getSettings().getUserAgentString());
    }

    private void checkCookie(String userAgent) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                String cookies = CookieManager.getInstance().getCookie(url);
                if (cookies == null || !cookies.contains("cf_clearance=")) {
                    Log.e("SplashActivitiy", "Not found required cookie: cf_clearance, try again soon...");
                    checkCookie(userAgent);
                } else {
                    Log.d("SplashActivitiy", "url: " + url);
                    Log.d("SplashActivitiy", "Got cookie: " + cookies);
//                Log.d("SplashActivitiy", "User agent: " + wvInvisibleSplash.getSettings().getUserAgentString());
                    CookieStringRequest.challengeCookies = cookies;
                    CookieStringRequest.userAgent = userAgent;
                    Toast.makeText(getApplicationContext(), "Saved cookie, page loading should be work now (" + cookies.substring(0, 20) + "...", Toast.LENGTH_LONG).show();

                    finish();
                }
            }
        }, 500);

    }
}
