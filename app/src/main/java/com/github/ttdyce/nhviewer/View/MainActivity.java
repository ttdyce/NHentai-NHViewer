package com.github.ttdyce.nhviewer.View;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.github.ttdyce.nhviewer.ComicListFragment;
import com.github.ttdyce.nhviewer.Presenter.ComicListPresenter;
import com.github.ttdyce.nhviewer.R;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements ComicListPresenter.ComicListView, ComicListFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ComicListPresenter comicListPresenter = new ComicListPresenter(this);
        init();

    }

    private void init() {

        BottomNavigationView bottomNavigation = findViewById(R.id.navigation);
        NavigationUI.setupWithNavController(bottomNavigation, Navigation.findNavController(this, R.id.fragmentNavHost));

    }

    @Override
    public void updateText(String text) {
//        TextView tvMain = findViewById(R.id.tvMain);
//        tvMain.append(text);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}