package com.github.ttdyce.nhviewer.View;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.github.ttdyce.nhviewer.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

    }

    private void init() {
        //Link bottom navigation view with jetpack navigation
        NavController navController = Navigation.findNavController(this, R.id.fragmentNavHost);
        BottomNavigationView bottomNavigation = findViewById(R.id.navigation);

        NavigationUI.setupWithNavController(bottomNavigation, navController);

    }

}