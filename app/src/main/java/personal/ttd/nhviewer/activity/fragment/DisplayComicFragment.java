package personal.ttd.nhviewer.activity.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import personal.ttd.nhviewer.R;
import personal.ttd.nhviewer.activity.DisplayInnerPageActivity;
import personal.ttd.nhviewer.api.MyApi;
import personal.ttd.nhviewer.comic.Comic;

public abstract class DisplayComicFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "DisplayComicFragment";
    private final int PAGELIMIT = 20;
    SwipeRefreshLayout mySwipeRefreshLayout;
    private DisplayComicAdapter mAdapter;
    protected ArrayList<Comic> comicsToDisplay = new ArrayList<>();
    private int page = 1;
    protected Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.content_display_comic, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle("Collection");
        //setProgressBar();
        setRefreshLayout();
        //this.comicsToDisplay = getFreshComicToDisplay();
        getFreshComicToDisplay();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //do when show
            refreshRecycleView();
        }
    }

    protected abstract void getFreshComicToDisplay();
    protected void setComicToDisplay(ArrayList<JSONObject> jsonObjects){
        setRecycleView();
    }

    private void setRefreshLayout() {
        mySwipeRefreshLayout = getView().findViewById(R.id.srDisplayComic);

        mySwipeRefreshLayout.setRefreshing(true);
        mySwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
                        refreshRecycleView();
                    }
                }
        );//End RefreshListener
    }

    private void refreshRecycleView() {
        page = 1;

        mAdapter.clear();
        comicsToDisplay.clear();
        getFreshComicToDisplay();
        mAdapter.addComic(getComicsToDisplayByPage(page));
        mAdapter.notifyDataSetChanged();

    }

//    private void setProgressBar() {
//        DisplayComic = getView().findViewById(R.id.pbDisplayComic);
//        DisplayComic.setVisibility(View.VISIBLE);
//    }

    private void setRecycleView() {
        RecyclerView mRecyclerView = getView().findViewById(R.id.rvDisplayComic);

        mAdapter = new DisplayComicAdapter();
        GridLayoutManager mLayoutManager = new GridLayoutManager(mContext, 3);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.addComic(getComicsToDisplayByPage(page));
        mAdapter.notifyDataSetChanged();

        mRecyclerView.addOnScrollListener(getEndlessScrollListener(mAdapter));
        Log.i("adapter count", "setRecycleView: adapter count: " + mAdapter.getItemCount());
        //mRecyclerView.addOnScrollListener(getEndlessScrollListener(mAdapter)  );
        mySwipeRefreshLayout.setRefreshing(false);

    }

    /*
    Starting index:  (p-1) * PAGELIMIT + 1;
    at page 1: we need (1-1)*20 = 0
    at page 2: we need (2-1)*20 = 20

    Ending index:   (startIndex + PAGELIMIT > this.comicsToDisplay.size()) ? this.comicsToDisplay.size() : startIndex + PAGELIMIT;
    at page 1: we need comicsToDisplay.size()-1, OR  1 + 20 = 21
    at page 1: we need comicsToDisplay.size()-1, OR  21 + 20 = 41
     */
    private ArrayList<Comic> getComicsToDisplayByPage(int p) {
        int startIndex, endIndex;
        ArrayList<Comic> comics = new ArrayList<>();

        startIndex = (p - 1) * PAGELIMIT;
        endIndex = (startIndex + PAGELIMIT > this.comicsToDisplay.size()) ? this.comicsToDisplay.size() : startIndex + PAGELIMIT;
        //endIndex comic didn't show! Each time 0 ~ endIndex-1

        for (int i = startIndex; i < endIndex; i++) {
            comics.add(this.comicsToDisplay.get(i));
        }

        return comics;
    }

    private RecyclerView.OnScrollListener getEndlessScrollListener(final DisplayComicAdapter mAdapter) {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    Log.i("OnScrollListener", "End of list!! bool to show: " + (comicsToDisplay.size() > page * PAGELIMIT));
                    if (comicsToDisplay.size() > page * PAGELIMIT) {
                        mAdapter.addComic(getComicsToDisplayByPage(++page));
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        };
    }

    //Data adapter, change the dataset for different source of data
    public class DisplayComicAdapter extends RecyclerView.Adapter<DisplayComicFragment.DisplayComicAdapter.ViewHolder> {
        private final String TAG = "DisplayComicAdapter";
        private ArrayList<Comic> mDataset = new ArrayList<>();

        public void addComic(ArrayList<Comic> comics) {
            mDataset.addAll(comics);
        }

        // Create new views (invoked by the layout manager)
        @NonNull
        @Override
        public DisplayComicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                 int viewType) {
            // create a new view
            View v = (View) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.comics_item, parent, false);
            return new DisplayComicAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            final Comic c = mDataset.get(position);
            String thumbLink;
            thumbLink = MyApi.Companion.getThumbLink(c.getMid(), c.getTypes());/*TODO perhaps types[0] == thumb's format*/

            //Log.i(TAG, "onBindViewHolder: position: " + position + c.getTitle());
            holder.tvTitle.setText(c.getTitle());
            Glide.with(holder.itemView.getContext())
                    .load(thumbLink)
                    .into(holder.ivThumb);
            //set onClick listener
            holder.cvComicItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
//                    intent.setClass(MainActivity.myContext, DisplayInnerPageActivity.class);
                    intent.setClass(mContext, DisplayInnerPageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Comic", c);
                    mContext.startActivity(intent);
                }
            });
            //set onLongClick listener
//            holder.cvComicItem.setOnLongClickListener(new View.OnLongClickListener() {});
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        public void clear() {
            mDataset.clear();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public CardView cvComicItem;
            public TextView tvTitle;
            public ImageView ivThumb;

            public ViewHolder(View v) {
                super(v);
                tvTitle = v.findViewById(R.id.tvTitle);
                ivThumb = v.findViewById(R.id.ivThumb);
                cvComicItem = v.findViewById(R.id.cvComicItem);
            }
        }


    }

    //AsncTask for getting json from server
    public class JsonTask extends AsyncTask<String, Void, ArrayList<JSONObject>> {

        @Override
        protected ArrayList<JSONObject> doInBackground(String... strings) {
            ArrayList<JSONObject> comicJsons = new ArrayList<>();

            for (String s : strings) {
                try {
                    JSONObject json = getJson(s);
                    comicJsons.add(json);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }

            return comicJsons;
        }

        @Override
        protected void onPostExecute(ArrayList<JSONObject> jsonObjects) {
            super.onPostExecute(jsonObjects);

            setComicToDisplay(jsonObjects);
        }

        JSONObject getJsonBAK(String infoLink) throws JSONException {
            StringBuilder inputLine = new StringBuilder();

            try {
                URL url = new URL(infoLink);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(url.openStream()));


                while (in.readLine() != null) {
                    inputLine.append(in.readLine());
                }

                in.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }

            Log.e(TAG, "getJson, link: " +infoLink);
            if(inputLine.toString().equals("null"))
                return null;
            else
                return new JSONObject(inputLine.toString());

        }

        JSONObject getJson(String url) throws IOException, JSONException {
            InputStream is = new URL(url).openStream();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);
                JSONObject json = new JSONObject(jsonText);
                return json;
            } finally {
                is.close();
            }
        }
        String readAll(Reader rd) throws IOException {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return sb.toString();
        }
    }

}
