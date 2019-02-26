package personal.ttd.nhviewer.activity;

import android.Manifest;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import personal.ttd.nhviewer.R;
import personal.ttd.nhviewer.activity.fragment.deprecated.CollectionFragment;
import personal.ttd.nhviewer.activity.fragment.deprecated.DownloadFragment;
import personal.ttd.nhviewer.activity.fragment.HistoryFragment;
import personal.ttd.nhviewer.activity.fragment.deprecated.PagerFragment;
import personal.ttd.nhviewer.Saver.file.Storage;

public class MainActivity extends AppCompatActivity{

    //back press logic
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private final String TAG = "From MainActivity";
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

        android.support.v7.widget.SearchView mSearchView;
        mSearchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.action_search).getActionView();
        setSearchView(mSearchView);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_search) {

        } else if (id == R.id.action_update) {
            Storage.updateDatabase(this);
            //NHTranlator.Companion.updateFromJson(this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //prevent from reloading on rotation
        if (null == savedInstanceState) {
            Log.e(TAG, "Starting program...");
            //get permission
            if (permissionRequired()) {
                getPermission();
            }

            //initiate
            initFragment(new PagerFragment());
        }
        initActionBarDrawer();

    }//END onCreate


    private void initActionBarDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        mDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        setSupportActionBar(toolbar);

        navigationView.setNavigationItemSelectedListener(item -> {
            // Handle navigation view item clicks here.
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                replaceFragment(new PagerFragment());
            } else if (id == R.id.nav_collection) {
                replaceFragment(new CollectionFragment());
            } else if (id == R.id.nav_download) {
                replaceFragment(new DownloadFragment());
            } else if (id == R.id.nav_history) {
                replaceFragment(new HistoryFragment());
            } else if (id == R.id.nav_setting) {

            } else if (id == R.id.nav_send) {

            }

            drawer.closeDrawer(GravityCompat.START);
            return true;
        });
    }


    /*
    Loop through variable myPermission to check/get permission
     */
    void getPermission() {
        // request the permission
        ActivityCompat.requestPermissions(this, myPermissions, 1);
    }

    boolean permissionRequired() {
        for (String str :
                myPermissions) {
            if (ContextCompat.checkSelfPermission(this, str) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    /* onBackPressed()
    checking is done ordered by priority:
        close drawer ->
        fragment back stack ->
        double click back button ->
        quit app
     */
    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int fragmentsCount = getSupportFragmentManager().getBackStackEntryCount();
            Log.i(TAG, "onBackPressed: fragmentsCount: " + fragmentsCount);
            if (fragmentsCount == 0) {
                //exit app
                if (lastBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                    super.onBackPressed();
                    return;
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Press back to quit", Snackbar.LENGTH_SHORT).show();
                }

                lastBackPressed = System.currentTimeMillis();
            } else {
                //fragmentsCount > 0
                if (getFragmentManager().getBackStackEntryCount() > 1) {
                    getFragmentManager().popBackStack();
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    private void setSearchView(android.support.v7.widget.SearchView mSearchView) {
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        ComponentName name = new ComponentName(getApplicationContext(), SearchableActivity.class);
        if(manager!=null){
            mSearchView.setSearchableInfo(manager.getSearchableInfo(name));
        }else{
            Log.i(TAG, "setSearchView: getSystemService(Context.SEARCH_SERVICE) returned null");
        }


    }

    void initFragment(Fragment f) {
        FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.frameHome, f);
        transaction.commit();
    }

    void replaceFragment(Fragment f) {
        FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.frameHome, f);
        transaction.addToBackStack(null);
        transaction.commit();

        Log.i(TAG, "replaceFragment: Done switching fragment");
    }

}

