package personal.ttd.nhviewer.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import personal.ttd.nhviewer.DebugTag;
import personal.ttd.nhviewer.R;
import personal.ttd.nhviewer.activity.fragment.deprecated.MainFragment;
import personal.ttd.nhviewer.api.NHapi;
import personal.ttd.nhviewer.comic.Comic;
import personal.ttd.nhviewer.file.Storage;

import static personal.ttd.nhviewer.api.NHapi.userAgent;

public class SearchableActivity extends AppCompatActivity {

    public static final String TAG = "SearchableActivity";
    private int page;
    private String query;
    private MainFragment.ComicsAdapter mAdapter;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_searching);


        Log.i(TAG, "onCreate: SearchableActivity");
        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //doMySearch(query);
            Log.i(DebugTag.TAG, "onCreate: query=" + query);
            this.query = query;
        }
        //set View
        setTitle("Result of: " + query);
        setRecycleView();
    }

    private void setRecycleView() {
        progressBar = findViewById(R.id.pbSearching);
        progressBar.setVisibility(View.VISIBLE);

        page = 1;
        RecyclerView mRecyclerView = findViewById(R.id.rvSearching);

        //TODO: mAdapter disabled, fixing context
        //mAdapter = new MainFragment.ComicsAdapter();
        getComics(NHapi.getSearchInfoLink(query), page, mAdapter);
        Log.i(TAG, "setRecycleView: NHapi:" + NHapi.getSearchInfoLink(query) + page);

        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 3);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(getEndlessScrollListener());
        registerForContextMenu(mRecyclerView);
    }

    private RecyclerView.OnScrollListener getEndlessScrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    Log.i(TAG, "End of list!!");
                    progressBar.setVisibility(View.VISIBLE);
                    getComics(NHapi.getSearchInfoLink(query), ++page, mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            }
        };
    }


    public void getComics(String url, int page, final MainFragment.ComicsAdapter adapter) {
        RequestQueue queue = Volley.newRequestQueue(this);
        //final Document[] doc = new Document[1];

        StringRequest documentRequest = new StringRequest( //
                Request.Method.GET, //
                url + page, //
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: " + response);

                        //doc[0] = Jsoup.parse(response);

                        adapter.addComic(getComicsData(response));
                        progressBar.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }
                }, //
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Error handling
                        System.out.println("Houston we have a problem ... !");
                        error.printStackTrace();
                    }

                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("User-Agent", userAgent);
                return headers;
            }
        }; //

        // Add the request to the queue...
        queue.add(documentRequest);

        // ... and wait for the document.
        // NOTE: Be aware of user experience here. We don't want to freeze the app...
        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                Log.i(TAG, "onRequestFinished: Finished");
            }
        });

    }

    private ArrayList<Comic> getComicsData(String response) {
        ArrayList<Comic> comics;
        comics = new ArrayList<>();
        JSONObject resObj = null;

        try {

            resObj = new JSONObject(response);

            for (int i = 0; i < resObj.getJSONArray("result").length(); i++) {
                JSONObject obj = null;
                obj = resObj.getJSONArray("result").getJSONObject(i);

                String id = obj.getString("id");
                String mid = obj.getString("media_id");
                String title = obj.getJSONObject("title").getString("japanese");
                String t = obj.getJSONObject("images").getJSONObject("thumbnail").getString("t");
                String ThumbLink = NHapi.getThumbLink(mid, t);

                Comic comic = new Comic();
                comic.setTitle(title);
                comic.setThumbLink(ThumbLink);
                comic.setId(id);

                Log.e(TAG, String.format("Finished %d gallery", i));
                Log.i(TAG, "getComicsData: ThumbLink: " + ThumbLink);

                comics.add(comic);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return comics;
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int clickedItemPosition = item.getGroupId();
        Comic c = mAdapter.getComicByPos(clickedItemPosition);

        switch (item.getItemId()) {
            case 1://add to collection
                try {
                    Storage.addCollection(c);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                Snackbar.make(findViewById(android.R.id.content), "Successfully saved to collection", Snackbar.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
        // do something!
        return super.onContextItemSelected(item);
    }

}
