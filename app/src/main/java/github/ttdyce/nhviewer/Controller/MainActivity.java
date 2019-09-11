package github.ttdyce.nhviewer.Controller;

import android.Manifest;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import github.ttdyce.nhviewer.Controller.fragment.CollectionListFragment;
import github.ttdyce.nhviewer.Controller.fragment.HistoryFragment;
import github.ttdyce.nhviewer.Controller.fragment.SettingFragment;
import github.ttdyce.nhviewer.Controller.fragment.PagerFragment;
import github.ttdyce.nhviewer.Controller.fragment.TagFragment;
import github.ttdyce.nhviewer.Model.comic.Collection;
import github.ttdyce.nhviewer.Model.comic.CollectionTool;
import github.ttdyce.nhviewer.Model.tag.TagManager;
import github.ttdyce.nhviewer.R;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {

    public static final int FRAME_HOME = R.id.frameHome;
    //back press logic
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private static final HashMap<Integer, Fragment> NAVIGATION_FRAGMENTS = new HashMap<Integer, Fragment>() {
        {
            put(R.id.nav_home, new PagerFragment());
            put(R.id.nav_collection, new CollectionListFragment());
//            put(R.id.nav_download, new DownloadFragment());
            put(R.id.nav_history, new HistoryFragment());
            put(R.id.nav_tag, new TagFragment());
            put(R.id.nav_setting, new SettingFragment());
        }
    };

    private final String TAG = "NH MainActivity";
    private final int PERMISSION_REQUEST_CODE = 1;
    private long lastBackPressed;

    //view
    private ActionBarDrawerToggle mDrawerToggle;

    //declare permission required
    private String[] myPermissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_activity, menu);

        androidx.appcompat.widget.SearchView searchView;
        searchView = (androidx.appcompat.widget.SearchView) menu.findItem(R.id.action_search).getActionView();
        setSearchView(searchView);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else if (id == R.id.action_backup) {
            CollectionTool.backup(this);
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //get permission and wait for permission result
        if (permissionRequired())
            getPermission();
        else {
            //prevent from reloading on rotation
            if (null == savedInstanceState) {
                Log.e(TAG, "Starting program...");

                initFragment(new PagerFragment());
            }

            init();

        }


    }//END onCreate

    private void init() {
        //initiate app
        initActionBarDrawer();
        //initBottomAppbar();
        initCollection();
        initTag();

        checkUpdate();
    }

    private void checkUpdate() {
        final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseRemoteConfig.activateFetched();

    }

//    private void initBottomAppbar() {
//        BottomAppBar bar = findViewById(R.id.bar);
//        FloatingActionButton fab  = findViewById(R.id.fab);
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        setSupportActionBar(bar);
//
//        fab.hide();
//
//        mDrawerToggle = new ActionBarDrawerToggle(this, drawer, bar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(mDrawerToggle);
//        mDrawerToggle.syncState();
//
//        bar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                // Handle actions based on the menu item
//                return true;
//            }
//        });
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            //quit if any permission not granted
            for (int result : grantResults) {
                if (result != PERMISSION_GRANTED) {
                    super.onBackPressed();
                    return;
                }
            }

            initFragment(new PagerFragment());
            init();
        }
    }

    /* onBackPressed()
    the checking is done by this priority order:
        close drawer ->
        fragment back stack ->
        double click back button ->
        quit app
     */
    @Override
    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int fragmentsCount = getSupportFragmentManager().getBackStackEntryCount();
            //Log.i(TAG, "onBackPressed: fragmentsCount: " + fragmentsCount);
            if (fragmentsCount > 0) {
                if (getFragmentManager().getBackStackEntryCount() > 1) {
                    getFragmentManager().popBackStack();
                } else {
                    super.onBackPressed();
                }


            } else {//fragmentsCount == 0
                if (System.currentTimeMillis() < lastBackPressed + TIME_INTERVAL )
                    super.onBackPressed();//exit app
                else
                    Snackbar.make(findViewById(android.R.id.content), "Press back again to quit", Snackbar.LENGTH_SHORT).show();


                lastBackPressed = System.currentTimeMillis();
            }
        }
    }

    private void initTag() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> tags = TagManager.getTagAll(preferences);

        if(tags.isEmpty()){
            // TODO: 2019/6/20 notify user that it is empty list
        }else{
            //put empty set
            SharedPreferences.Editor editor = preferences.edit();
            editor.putStringSet(TagFragment.KEY_PREF_TAG, tags);
            editor.apply();

        }
    }

    private void initCollection() {
        try {
            Collection.loadCollection();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        //set default collection id
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        String defaultCollectionId = preferences.getString(SettingFragment.KEY_PREF_DEFAULT_COLLECTION_ID, String.valueOf(Collection.FAVARITE_ID));
        editor.putString(SettingFragment.KEY_PREF_DEFAULT_COLLECTION_ID, defaultCollectionId);
        editor.apply();
    }

    private void initActionBarDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        setSupportActionBar(toolbar);

        // Handle navigation view item clicks here.
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment fragmentSelected = NAVIGATION_FRAGMENTS.get(id);
            replaceFragment(fragmentSelected);

            drawer.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    //Loop through variable myPermission to check/get permission
    private void getPermission() {
        // request the permission
        ActivityCompat.requestPermissions(this, myPermissions, PERMISSION_REQUEST_CODE);
    }

    private boolean permissionRequired() {
        for (String str : myPermissions) {
            if (ContextCompat.checkSelfPermission(this, str) == PERMISSION_GRANTED)
                break;
            return true;
        }

        return false;
    }

    private void setSearchView(androidx.appcompat.widget.SearchView mSearchView) {
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        ComponentName name = new ComponentName(getApplicationContext(), SearchableActivity.class);
        if (manager != null) {
            mSearchView.setSearchableInfo(manager.getSearchableInfo(name));
        } else {
            Log.i(TAG, "setSearchView: getSystemService(Context.SEARCH_SERVICE) returned null");
        }


    }

    private void initFragment(Fragment f) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(FRAME_HOME, f);
        transaction.commit();
    }

    public void replaceFragment(Fragment f) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(FRAME_HOME, f);
        transaction.addToBackStack(null);
        transaction.commit();

        Log.i(TAG, "replaceFragment: Done switching fragment");
    }

}

