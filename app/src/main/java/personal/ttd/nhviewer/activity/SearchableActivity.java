package personal.ttd.nhviewer.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;

import personal.ttd.nhviewer.R;
import personal.ttd.nhviewer.activity.fragment.SearchableFragment;
import personal.ttd.nhviewer.activity.fragment.deprecated.MainFragment;

public class SearchableActivity extends AppCompatActivity {

    public static final String TAG = "SearchableActivity";
    private String query;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);


        Log.i(TAG, "onCreate: SearchableActivity");

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            this.query = query;
        }
        //set View
        setTitle("Result of: " + query);
        setFragment();
    }

    private void setFragment() {
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
