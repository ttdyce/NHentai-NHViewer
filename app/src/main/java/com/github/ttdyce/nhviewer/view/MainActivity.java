package com.github.ttdyce.nhviewer.view;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import com.github.ttdyce.nhviewer.BuildConfig;
import com.github.ttdyce.nhviewer.R;
import com.github.ttdyce.nhviewer.model.firebase.Updater;
import com.github.ttdyce.nhviewer.model.room.AppDatabase;
import com.github.ttdyce.nhviewer.model.room.ComicCollectionDao;
import com.github.ttdyce.nhviewer.model.room.ComicCollectionEntity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements Updater.OnUpdateNeededListener {
    public static final String KEY_PREF_DEFAULT_LANGUAGE = "key_default_language";
    public static final String KEY_PREF_DEMO_MODE = "key_demo_mode";
    public static final String KEY_PREF_ENABLE_SPLASH = "key_enable_splash";
    public static final String KEY_PREF_CHECK_UPDATE = "key_check_update";
    public static final String KEY_PREF_LAST_VERSION_OPENED = "key_last_version_opened";
    public static final CharSequence KEY_PREF_VERSION = "key_version";
    private static final String TAG = "MainActivity";
    private static AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);//replacing the SplashTheme
        //Open SplashActivity
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean enabledSplash = pref.getBoolean(KEY_PREF_ENABLE_SPLASH, true);
        if (enabledSplash)
            startActivity(new Intent(this, SplashActivity.class));// TODO: 12/15/2019 Open SplashActivity from MainActivity

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkMigration();
        tryAskForLanguage();
        init();
    }

    private void checkMigration() {
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String currentVersion = BuildConfig.VERSION_NAME;
        String lastVersion = pref.getString(KEY_PREF_LAST_VERSION_OPENED, "0.0.0");
        if(!currentVersion.equals(lastVersion)){
            //it is first time open after update / simply first time open

            if(currentVersion.equals("2.6.0") || lastVersion.equals("0.0.0")){
                //fix for 2.5.0 -> 2.6.0
                pref.edit().remove(KEY_PREF_DEFAULT_LANGUAGE).commit();
            }

        }

        pref.edit().putString(KEY_PREF_LAST_VERSION_OPENED, currentVersion).apply();
    }

    private void init() {

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean enabledCheckUpdate = pref.getBoolean(KEY_PREF_CHECK_UPDATE, true);
        if (enabledCheckUpdate)
            Updater.with(this).onUpdateNeeded(this).check();

        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, AppDatabase.DB_NAME)
                .addMigrations(AppDatabase.MIGRATION_1_2).build();

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

    }

    //Link bottom navigation view with jetpack navigation
    private void initNavigation() {
        NavController navController = Navigation.findNavController(this, R.id.fragmentNavHost);
        navController.setGraph(R.navigation.nav_app);
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        NavigationUI.setupWithNavController(bottomNavigation, navController);
//        Navigation.findNavController(this, R.id.fragmentNavHost)
    }

    @Override
    public void onUpdateNeeded(final String updateUrl) {
        AlertDialog alert = new MaterialAlertDialogBuilder(this, R.style.DialogTheme)
                .setTitle(getString(R.string.new_version_available))
                .setMessage(getString(R.string.new_version_desc))
                .setPositiveButton(getString(R.string.new_version_download_github),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }).setNegativeButton(getString(R.string.new_version_cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();

        alert.show();
    }

    @SuppressLint("ApplySharedPref")
    private void tryAskForLanguage() {
        String comicLanguage = SettingsFragment.Language.notSet.toString();
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        try {
            comicLanguage = pref.getString(KEY_PREF_DEFAULT_LANGUAGE, SettingsFragment.Language.notSet.toString());
        } catch (ClassCastException e) {
            pref.edit().remove(KEY_PREF_DEFAULT_LANGUAGE).commit();
        }

        if (!comicLanguage.equals(SettingsFragment.Language.notSet.toString()) ) {
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

        builder.setTitle(getString(R.string.set_default_language));
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = pref.edit();

                editor.putString(KEY_PREF_DEFAULT_LANGUAGE, SettingsFragment.Language.all.toString());
                editor.apply();

            }
        });
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = pref.edit();

                editor.putString(KEY_PREF_DEFAULT_LANGUAGE, String.valueOf(which));
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