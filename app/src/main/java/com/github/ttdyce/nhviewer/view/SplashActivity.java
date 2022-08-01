package com.github.ttdyce.nhviewer.view;

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
import com.github.ttdyce.nhviewer.model.api.GitHubSponsorsAPI;
import com.google.firebase.auth.FirebaseAuth;
import com.github.ttdyce.nhviewer.model.CookieStringRequest;
import com.github.ttdyce.nhviewer.model.api.NHAPI;
import com.github.ttdyce.nhviewer.model.api.PopularType;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /* Use a WebView to bypass cloudflare challenge (Retrieve cookie) */
        // any api url is fine :|
        String url = NHAPI.URLs.search("english", 1, PopularType.none);
        WebView wvInvisibleSplash = findViewById(R.id.wvInvisibleSplash);

        // uncomment this part to simulate no-cookie state, for debugging
//        CookieManager.getInstance().removeAllCookies(new ValueCallback<Boolean>() {
//            @Override
//            public void onReceiveValue(Boolean value) {
//                // leave empty
//            }
//        });

        wvInvisibleSplash.getSettings().setJavaScriptEnabled(true);
        wvInvisibleSplash.loadUrl(url);

        /* Performs animation on a TextView. */
        TextView splashLoading = findViewById(R.id.tvSplashLoading);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash_loading);
        splashLoading.startAnimation(animation);

        // TODO: 12/15/2019 hard coded SplashActivity wait for 1.5 second
        // TODO: 8/1/2022 hard coded 5 seconds for cloudflare challenge
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // TODO: 4/12/2021 get GitHub Sponsor status, and set nhvp
            GitHubSponsorsAPI.getIsSponsorAsyc(this, () -> {
                Toast.makeText(getApplicationContext(), "Sponsor status: " + MainActivity.isSponsor, Toast.LENGTH_LONG).show();
            });
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // handle Cloudflare challenge
                String cookies = CookieManager.getInstance().getCookie(url);
                Log.d("SplashActivitiy", "url: " + url);
                Log.d("SplashActivitiy", "Got cookie: " + cookies);
                Log.d("SplashActivitiy", "User agent: " + wvInvisibleSplash.getSettings().getUserAgentString());
                CookieStringRequest.challengeCookies = cookies;
                CookieStringRequest.userAgent = wvInvisibleSplash.getSettings().getUserAgentString();

                // return to MainActivity
                finish();
            }
        }, 5 * 1000);   //5 seconds

    }
}
