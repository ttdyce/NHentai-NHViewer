package com.github.ttdyce.nhviewer.view;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import com.github.ttdyce.nhviewer.BuildConfig;
import com.github.ttdyce.nhviewer.R;
import com.github.ttdyce.nhviewer.model.MyDistributeListener;
import com.github.ttdyce.nhviewer.model.room.AppDatabase;
import com.github.ttdyce.nhviewer.model.room.ComicCollectionDao;
import com.github.ttdyce.nhviewer.model.room.ComicCollectionEntity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import com.microsoft.appcenter.distribute.Distribute;

import java.util.Date;


public class MainActivity extends AppCompatActivity {
    public static final String KEY_PREF_DEFAULT_LANGUAGE = "key_default_language";
    public static final String KEY_PREF_DEMO_MODE = "key_demo_mode";
    public static final String KEY_PREF_ENABLE_SPLASH = "key_enable_splash";
    public static final String KEY_PREF_CHECK_UPDATE = "key_check_update";
    public static final String KEY_PREF_LAST_VERSION_OPENED = "key_last_version_opened";
    public static final CharSequence KEY_PREF_VERSION = "key_version";
    public static final String KEY_PREF_PROXY = "key_proxy";
    public static final String KEY_PREF_PROXY_HOST = "key_proxy_host";
    public static final String KEY_PREF_PROXY_PORT = "key_proxy_port";
    public static final String KEY_PREF_LOGIN = "key_login";
    public static final String KEY_PREF_ACCOUNT = "key_account";
    public static final String KEY_PREF_LOGOUT = "key_logout";
    public static final String KEY_PREF_NHVP_PROXY = "key_nhvp_proxy";
    public static final String KEY_PREF_CURRENT_USERNAME = "current_username";
    public static final String KEY_PREF_IS_SPONSOR = "key_pref_is_sponsor";
    private static final String TAG = "MainActivity";
    public static String currentUsername = null;
    private static AppDatabase appDatabase;
    public static String proxyHost;
    public static int proxyPort;
    // shortcut stored in MainActivity, not the most accurate one. See NHAPI.isSponsor for fresh data
    public static boolean isSponsor = false;

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
        if (!currentVersion.equals(lastVersion)) {
            //it is first time open after update / simply first time open

            if (currentVersion.equals("2.6.0") || lastVersion.equals("0.0.0")) {
                //fix for 2.5.0 -> 2.6.0
                pref.edit().remove(KEY_PREF_DEFAULT_LANGUAGE).commit();
            }

        }

        pref.edit().putString(KEY_PREF_LAST_VERSION_OPENED, currentVersion).apply();
    }

    private void init() {
        // setup vs-app-center
        Distribute.setListener(new MyDistributeListener());
        AppCenter.start(getApplication(), "9803719c-1913-46ad-8bfe-826d98538d3c",
                Analytics.class, Crashes.class, Distribute.class);

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean enabledCheckUpdate = pref.getBoolean(KEY_PREF_CHECK_UPDATE, true);
        AppCenter.setEnabled(enabledCheckUpdate); // for all services

        // API proxy
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        SharedPreferences.Editor editor = pref.edit();

        if (firebaseAuth.getCurrentUser() == null)
            editor.putBoolean(KEY_PREF_NHVP_PROXY, false);
        editor.apply();

        // TODO: 5/1/2021 get username from storage
        currentUsername = pref.getString(KEY_PREF_CURRENT_USERNAME, null);

        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, AppDatabase.DB_NAME)
                .addMigrations(AppDatabase.MIGRATION_1_2).build();

//        deleteDatabase(AppDatabase.DB_NAME);
        //init Collections
        // todo replace with executor-things (java concurrent)
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

        //proxy
        proxyHost = pref.getString(KEY_PREF_PROXY_HOST, "");
        try {
            proxyPort = Integer.parseInt(pref.getString(KEY_PREF_PROXY_PORT, "8080"));
        } catch (NumberFormatException e) {
            Log.e(TAG, "init ignorable error: setting proxyPort to default (8080)");
            proxyPort = 8080;
        }

        Log.d(TAG, "init: proxyHost: " + proxyHost);
        Log.d(TAG, "init: proxyPort: " + proxyPort);

    }

    //Link bottom navigation view with jetpack navigation
    private void initNavigation() {
        NavController navController = Navigation.findNavController(this, R.id.fragmentNavHost);
        navController.setGraph(R.navigation.nav_app);
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        NavigationUI.setupWithNavController(bottomNavigation, navController);
//        Navigation.findNavController(this, R.id.fragmentNavHost)
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

        if (!comicLanguage.equals(SettingsFragment.Language.notSet.toString())) {
            initNavigation();
            return;
        }

        //pop up dialog for setting default language
        final String[] languageArray = getResources().getStringArray(R.array.languages);
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
        builder.setItems(languageArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "onClick: init language clicked: " + which);
                SharedPreferences.Editor editor = pref.edit();

                editor.putString(KEY_PREF_DEFAULT_LANGUAGE, String.valueOf(which));
                editor.apply();

                final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                String language = pref.getString(KEY_PREF_DEFAULT_LANGUAGE, SettingsFragment.Language.notSet.toString());
                if (SettingsFragment.Language.notSet.toString().equals(language)) {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(KEY_PREF_DEFAULT_LANGUAGE, SettingsFragment.Language.all.toString());
                    editor.apply();
                }

                Log.d(TAG, "onClick: after init language, before dismiss dialog: " + pref.getString(KEY_PREF_DEFAULT_LANGUAGE, "not set"));
                initNavigation();
            }
        });
        builder.show();
    }

    //Singleton database
    public static AppDatabase getAppDatabase() {
        return appDatabase;
    }

    public static boolean isProxied() {
        if ("".equals(proxyHost))
            return false;

        return true;
    }

}