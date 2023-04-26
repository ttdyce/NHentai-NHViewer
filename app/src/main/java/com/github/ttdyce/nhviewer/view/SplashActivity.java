package com.github.ttdyce.nhviewer.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.ttdyce.nhviewer.R;
import com.github.ttdyce.nhviewer.model.CookieStringRequest;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /* Use a WebView to bypass cloudflare challenge (Retrieve cookie) */
        // any api url is fine :|
        String url = "https://nhentai.net";
        WebView wvInvisibleSplash = findViewById(R.id.wvInvisibleSplash);

        // uncomment this part to simulate no-cookie state, for debugging
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();

        wvInvisibleSplash.getSettings().setJavaScriptEnabled(true);
        wvInvisibleSplash.getSettings().setUserAgentString("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:109.0) Gecko/20100101 Firefox/112.0");

        wvInvisibleSplash.loadUrl(url);

        /* Performs animation on a TextView. */
        TextView splashLoading = findViewById(R.id.tvSplashLoading);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash_loading);
        splashLoading.startAnimation(animation);

        // TODO: 12/15/2019 hard coded SplashActivity wait for 1.5 second
        // TODO: 8/1/2022 hard coded 5 seconds for cloudflare challenge
        Handler handler = new Handler();
        Intent refreshCookieIntent = new Intent(this, RefreshCookieActivity.class);
        handler.postDelayed(new Runnable() {
            public void run() {
                // handle Cloudflare challenge
                String cookies = CookieManager.getInstance().getCookie(url);
                Log.d("SplashActivitiy", "url: " + url);
                Log.d("SplashActivitiy", "Got cookie: " + cookies);
                Log.d("SplashActivitiy", "User agent: " + wvInvisibleSplash.getSettings().getUserAgentString());
                if (cookies == null || !cookies.contains("cf_clearance=")) {
                    Log.e("SplashActivitiy", "Not found required cookie: cf_clearance, I think API call won't work");

                    Toast.makeText(getApplicationContext(), "Failed to bypass human checking, use manual mode", Toast.LENGTH_LONG).show();
                    startActivity(refreshCookieIntent);
                } else {
                    CookieStringRequest.challengeCookies = cookies;
                    CookieStringRequest.userAgent = wvInvisibleSplash.getSettings().getUserAgentString();
                }

                // return to MainActivity
                finish();
            }
        }, 2 * 1000);   // 2 seconds
    }
}
