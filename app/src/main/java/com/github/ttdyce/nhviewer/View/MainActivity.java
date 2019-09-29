package com.github.ttdyce.nhviewer.View;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import com.github.ttdyce.nhviewer.Model.Room.AppDatabase;
import com.github.ttdyce.nhviewer.Model.Room.ComicCollectionDao;
import com.github.ttdyce.nhviewer.Model.Room.ComicCollectionEntity;
import com.github.ttdyce.nhviewer.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

    }

    private void init() {
        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, AppDatabase.DB_NAME)
                .fallbackToDestructiveMigration().build();

//        deleteDatabase(AppDatabase.DB_NAME);
        //init Collections
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        ComicCollectionDao dao = appDatabase.comicCollectionDao();
                        if (dao.notExist(AppDatabase.COL_COLLECTION_HISTORY, -1)) {
                            dao.insert(ComicCollectionEntity.create(AppDatabase.COL_COLLECTION_HISTORY, -1, new Date()));
                            dao.insert(ComicCollectionEntity.create(AppDatabase.COL_COLLECTION_FAVORITE, -1, new Date()));
                            dao.insert(ComicCollectionEntity.create(AppDatabase.COL_COLLECTION_NEXT, -1, new Date()));
                        }
                    }
                }).start();
        //app bar
        Toolbar myToolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(myToolbar);

        //Link bottom navigation view with jetpack navigation
        BottomNavigationView bottomNavigation = findViewById(R.id.navigation);

        NavigationUI.setupWithNavController(bottomNavigation, Navigation.findNavController(this, R.id.fragmentNavHost));
    }

    //Singleton database
    public static AppDatabase getAppDatabase() {
        return appDatabase;
    }

}