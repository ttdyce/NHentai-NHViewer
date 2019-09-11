package github.ttdyce.nhviewer.Controller;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
        Fragment fragment = new SearchableFragment();
        Bundle bundle = new Bundle();

        bundle.putString("query", query);
        fragment.setArguments(bundle);

        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.frameSearchable, fragment);
        transaction.commit();
    }


}
