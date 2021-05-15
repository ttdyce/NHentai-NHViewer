package com.github.ttdyce.nhviewer.view;

import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.ttdyce.nhviewer.R;
import com.github.ttdyce.nhviewer.model.api.GitHubSponsorsAPI;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /* Performs animation on a TextView. */
        TextView splashLoading = findViewById(R.id.tvSplashLoading);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash_loading);
        splashLoading.startAnimation(animation);

        // TODO: 12/15/2019 hard coded SplashActivity wait for 1.5 second

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // TODO: 4/12/2021 get GitHub Sponsor status, and set nhvp
            GitHubSponsorsAPI.getIsSponsorAsyc(this, ()->{
                finish();
                Toast.makeText(getApplicationContext(), "Sponsor status: " + MainActivity.isSponsor, Toast.LENGTH_LONG).show();
            });
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    finish();
                }
            }, 1500);   //1.5 seconds}
        }
    }
}
