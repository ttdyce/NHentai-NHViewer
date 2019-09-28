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
    private static NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

    }

    private void init() {
        // TODO: 2019/9/25 Database is always clearing for better debugging
        deleteDatabase(AppDatabase.DB_NAME);
        //init Collections
        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, AppDatabase.DB_NAME)
                .fallbackToDestructiveMigration().build();

//        final AppDatabase db = Room.databaseBuilder(getApplicationContext(),
//                AppDatabase.class, "ComicCollection")
//                .fallbackToDestructiveMigration().build();
        final ComicCollectionDao dao = appDatabase.comicCollectionDao();

        // TODO: 2019/9/25 Database is always clearing for better debugging
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        dao.insert(ComicCollectionEntity.create(AppDatabase.COL_COLLECTION_HISTORY, -1));
                        dao.insert(ComicCollectionEntity.create(AppDatabase.COL_COLLECTION_FAVORITE, -1));
                        dao.insert(ComicCollectionEntity.create(AppDatabase.COL_COLLECTION_NEXT, -1));
                        for (ComicCollectionEntity entity :
                                dao.getAll()) {
                            Log.i("InsideThread", "Found entity: " + entity.getName());
                        }
                        Log.i("InsideThread", "Thread ended!");
                    }
                }).start();
        navController = Navigation.findNavController(this, R.id.fragmentNavHost);
        //app bar
        Toolbar myToolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(myToolbar);

        //Link bottom navigation view with jetpack navigation
        BottomNavigationView bottomNavigation = findViewById(R.id.navigation);

        NavigationUI.setupWithNavController(bottomNavigation, navController);


    }

    //Singleton database
    public static AppDatabase getAppDatabase(){
        return appDatabase;
    }
    //Singleton
    public static NavController getNavController(){
        return navController;
    }

}