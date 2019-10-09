package com.github.ttdyce.nhviewer.View;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import com.github.ttdyce.nhviewer.Model.Room.AppDatabase;
import com.github.ttdyce.nhviewer.Model.Room.ComicCollectionDao;
import com.github.ttdyce.nhviewer.Model.Room.ComicCollectionEntity;
import com.github.ttdyce.nhviewer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public static final String KEY_PREF_DEFAULT_LANGUAGE = "default_language";
    private static AppDatabase appDatabase;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        askForLanguage();
        init();
    }

    private void init() {
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.firebase_default_config);
        final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseRemoteConfig.fetch(5) // TODO: 2019/10/10 fetching each 5 seconds for debug
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "remote config is fetched.");
                            firebaseRemoteConfig.activateFetched();
                        }
                    }
                });

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

    // TODO: 2019/10/1 askForLanguage() seems too dirty
    private void askForLanguage() {
        String languageNotSet = "not set";
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        pref.edit().clear().commit();// TODO: 2019/10/1 default language is always clearing
        String storedLanguage = pref.getString(KEY_PREF_DEFAULT_LANGUAGE, languageNotSet);

        if (storedLanguage.equals(languageNotSet)) {
            //pop up dialog for setting default language
            final String[] languageArray = getResources().getStringArray(R.array.languages);
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                    getApplicationContext(),
                    android.R.layout.simple_list_item_1,
                    languageArray);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Set your default language");

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(KEY_PREF_DEFAULT_LANGUAGE, "All");
                    editor.apply();

                    dialog.dismiss();

                    Navigation.findNavController(MainActivity.this, R.id.fragmentNavHost).navigate(R.id.indexFragment);
//                    refreshRecyclerView(1);
                }
            });
            builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(KEY_PREF_DEFAULT_LANGUAGE, languageArray[which]);
                    editor.apply();

                    Navigation.findNavController(MainActivity.this, R.id.fragmentNavHost).navigate(R.id.indexFragment);
//                    refreshRecyclerView(1);
                }
            });

            builder.show();
        }
    }

    //Singleton database
    public static AppDatabase getAppDatabase() {
        return appDatabase;
    }

}