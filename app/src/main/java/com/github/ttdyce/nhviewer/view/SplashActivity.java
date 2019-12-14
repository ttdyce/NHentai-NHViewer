package com.github.ttdyce.nhviewer.view;

import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.ttdyce.nhviewer.R;

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
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                finish();
            }
        }, 1500);   //1.5 seconds
    }
}
