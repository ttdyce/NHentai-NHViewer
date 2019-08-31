package github.ttdyce.nhviewer.Controller;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import github.ttdyce.nhviewer.R;
import github.ttdyce.nhviewer.Controller.fragment.SearchableFragment;

public class SearchableActivity extends AppCompatActivity {
    public static final String TAG = "SearchableActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);

        init();
    }

    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.searchable_activity, menu);

        return true;
    }

    private void init() {
        String query = "";
        Intent intent = getIntent();
        Toolbar toolbar = findViewById(R.id.toolbar);

        if (Intent.ACTION_SEARCH.equals(intent.getAction()))
            query = intent.getStringExtra(SearchManager.QUERY);

        //set View
        setSearchableFragment(query);
        setSupportActionBar(toolbar);
    }

    private void setSearchableFragment(String query) {
        FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.Fragment fragment = new SearchableFragment();
        Bundle bundle = new Bundle();

        bundle.putString("query", query);
        fragment.setArguments(bundle);

        android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.frameSearchable, fragment);
        transaction.commit();
    }


}
