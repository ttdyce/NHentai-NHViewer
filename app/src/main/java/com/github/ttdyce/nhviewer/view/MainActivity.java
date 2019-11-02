package com.github.ttdyce.nhviewer.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import com.github.ttdyce.nhviewer.R;
import com.github.ttdyce.nhviewer.model.firebase.Updater;
import com.github.ttdyce.nhviewer.model.room.AppDatabase;
import com.github.ttdyce.nhviewer.model.room.ComicCachedEntity;
import com.github.ttdyce.nhviewer.model.room.ComicCollectionDao;
import com.github.ttdyce.nhviewer.model.room.ComicCollectionEntity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Updater.OnUpdateNeededListener {
    public static final String KEY_PREF_DEFAULT_LANGUAGE = "key_default_language";
    private static final String TAG = "MainActivity";
    private static AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tryAskForLanguage();
        init();
        tryBackup();
    }

    private void tryBackup() {


        new Thread(new Runnable() {
            @Override
            public void run() {
                List<ComicCollectionEntity> collectionEntities = appDatabase.comicCollectionDao().getAll();
                List<ComicCachedEntity> comicCachedEntities = appDatabase.comicCachedDao().getAll();

                Socket socket;
                String host = "192.168.128.57";
                int port = 3333;

                try {
                    socket = new Socket(host, port);
                    Log.d(TAG, "tryBackup: connected to " + host);
//                    Toast.makeText(MainActivity.this, "tryBackup: connected to " + host, Toast.LENGTH_SHORT).show();

                    // get the output stream from the socket.
                    OutputStream outputStream = socket.getOutputStream();
                    // create a data output stream from the output stream so we can send data through it
                    DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

                    Log.d(TAG, "Sending table ComicCollection");
                    dataOutputStream.write("Table name".getBytes());
                    dataOutputStream.write("ComicCollection".getBytes());
                    socket.getInputStream().read();

                    for (ComicCollectionEntity e :collectionEntities){
                        dataOutputStream.write(e.toJson().getBytes());
                        socket.getInputStream().read();
                    }
                    dataOutputStream.write("EOF".getBytes());
                    socket.getInputStream().read();


                    Log.d(TAG, "Sending table ComicCached");
                    dataOutputStream.write("Table name".getBytes());
                    dataOutputStream.write("ComicCached".getBytes());
                    socket.getInputStream().read();

                    for (ComicCachedEntity e :comicCachedEntities){
                        dataOutputStream.write(e.toJson().getBytes());
                        socket.getInputStream().read();
                    }
                    dataOutputStream.write("EOF".getBytes());
                    socket.getInputStream().read();

                    dataOutputStream.write("END".getBytes());
                    dataOutputStream.flush();
                    dataOutputStream.close();

                    Log.d(TAG, "Closing socket and terminating program.");
                    socket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Backup failed");
//                    Toast.makeText(this, "Backup failed", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void init() {
        Updater.with(this).onUpdateNeeded(this).check();

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

//        initNavigation();
    }

    //Link bottom navigation view with jetpack navigation
    private void initNavigation() {
        NavController navController = Navigation.findNavController(this, R.id.fragmentNavHost);
        navController.setGraph(R.navigation.nav_app);
        BottomNavigationView bottomNavigation = findViewById(R.id.navigation);
        NavigationUI.setupWithNavController(bottomNavigation, navController);
//        Navigation.findNavController(this, R.id.fragmentNavHost)
    }

    @Override
    public void onUpdateNeeded(final String updateUrl) {
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.DialogTheme)
                .setTitle("New version available")
                .setMessage("Check out my coolest update on Github!")
                .setPositiveButton("Download (Github)",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }).setNegativeButton("No, thanks",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
        dialog.show();
    }

    private void tryAskForLanguage() {
        String languageNotSet = "not set";
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String storedLanguage = pref.getString(KEY_PREF_DEFAULT_LANGUAGE, languageNotSet);
        if (!languageNotSet.equals(storedLanguage)) {
            initNavigation();
            return;
        }

        //pop up dialog for setting default language
        final String[] languageArray = getResources().getStringArray(R.array.languages);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                getApplicationContext(),
                android.R.layout.simple_list_item_1,
                languageArray);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);

        builder.setTitle("Set your default language");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = pref.edit();

                editor.putString(KEY_PREF_DEFAULT_LANGUAGE, "All");
                editor.apply();

            }
        });
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = pref.edit();

                editor.putString(KEY_PREF_DEFAULT_LANGUAGE, languageArray[which]);
                editor.apply();

            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                initNavigation();
            }
        });
        builder.show();
    }

    //Singleton database
    public static AppDatabase getAppDatabase() {
        return appDatabase;
    }

}