package com.github.ttdyce.nhviewer.View;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import com.github.ttdyce.nhviewer.Model.Room.AppDatabase;
import com.github.ttdyce.nhviewer.Model.Room.ComicCollectionDao;
import com.github.ttdyce.nhviewer.Model.Room.ComicCollectionEntity;
import com.github.ttdyce.nhviewer.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

    }

    private void init() {
        //init Collections
        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "ComicCollection").build();

//        final AppDatabase db = Room.databaseBuilder(getApplicationContext(),
//                AppDatabase.class, "ComicCollection")
//                .fallbackToDestructiveMigration().build();
        final ComicCollectionDao dao = appDatabase.comicCollectionDao();

        // TODO: 2019/9/25 Database is always clearing for better debugging
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        appDatabase.clearAllTables();
                        dao.insert(ComicCollectionEntity.create("History", "2"));
                        dao.insert(ComicCollectionEntity.create("Favorite", "2"));
                        dao.insert(ComicCollectionEntity.create("ReadLater", "2"));
                        for (ComicCollectionEntity entity :
                                dao.getAll()) {
                            Log.i("InsideThread", "Found entity: " + entity.getName());
                        }
                        Log.i("InsideThread", "Thread ended!");
                    }
                }).start();
        //app bar
        Toolbar myToolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(myToolbar);

        //Link bottom navigation view with jetpack navigation
        NavController navController = Navigation.findNavController(this, R.id.fragmentNavHost);
        BottomNavigationView bottomNavigation = findViewById(R.id.navigation);

        NavigationUI.setupWithNavController(bottomNavigation, navController);


    }

    //Singleton database
    public static AppDatabase getAppDatabase(){
        return appDatabase;
    }

}