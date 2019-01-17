package personal.ttd.nhviewer.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import personal.ttd.nhviewer.R;
import personal.ttd.nhviewer.Volley.VolleyCallback;
import personal.ttd.nhviewer.api.MyApi;
import personal.ttd.nhviewer.comic.Comic;
import personal.ttd.nhviewer.glide.GlideApp;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


public class DisplayInnerPageActivity extends AppCompatActivity {

    private final String TAG = "From ComicDisplay";
    private int lastVisibleItemPosition;
    private Comic comicShowing;
    private RecyclerView mRecyclerView;
    private ComicDisplayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic);

        Log.i(TAG, "onCreate: ");

        Uri bData = getIntent().getData();//browser data
        Bundle aData = getIntent().getExtras();//app data
        String comicid = null;

        //using id from browser
        if (bData != null && bData.isHierarchical())
            comicid = bData.getLastPathSegment();
            //using id from app
        else if (aData != null)
            comicid = aData.getString("comicid");

        Log.i(TAG, "comicid: " + comicid);
        setupComicShowing(comicid);


    }

    @Override
    protected void onStop() {
        //save this comic to history
        MyApi.Companion.updateHistory(this, comicShowing, lastVisibleItemPosition);
        Log.i(TAG, "onStop: lastVisibleItemPosition: " + lastVisibleItemPosition);

        super.onStop();
    }


    private void setupComicShowing(String comicid) {
        Bundle data = getIntent().getExtras();
        ///TODO seems nothing to do
        if (data != null && data.getParcelable("Comic") != null) {
            comicShowing = data.getParcelable("Comic");
            comicShowing = MyApi.Companion.getComicByMid(comicShowing.getMid(), getApplicationContext());
        }

        VolleyCallback callback = new VolleyCallback() {
            @Override
            public void onResponse(ArrayList<Comic> comics) {
                Comic c = comics.get(0);
                comicShowing = c;
                comicShowing.setId(comicid);
                MyApi.Companion.addToHistory(getApplicationContext(), comicShowing, 0);
                setupRecycleView();
            }
        };

        MyApi.Companion.getComicById(comicid, getApplicationContext(), callback);
        ///TODO disabled history function, comic id needed to be set

    }

    private void setupRecycleView() {
        //^^Get pasted Comic object From MainActivity^^

        mAdapter = new ComicDisplayAdapter(comicShowing);

        mRecyclerView = findViewById(R.id.rvComic);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);


        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {//dy > 0 = move down, dy < 0 = move up
                if (mLayoutManager.findFirstCompletelyVisibleItemPosition() != -1) {
                    lastVisibleItemPosition = mLayoutManager.findFirstCompletelyVisibleItemPosition() + 1;

                    int itemCount = mLayoutManager.getItemCount() - 1;
                    ProgressBar bar = findViewById(R.id.pbComic);
                    double progress = 100.0 * lastVisibleItemPosition / itemCount;

                    bar.setProgress((int) progress);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        mAdapter.notifyDataSetChanged();

        Log.i(TAG, "setupRecycleView: Finished");
    }

    public class ComicDisplayAdapter extends RecyclerView.Adapter<ComicDisplayAdapter.ViewHolder> {
        private Comic comic;
        private String TAG = "From ComicDisplayAdapter";

        ComicDisplayAdapter(Comic c) {
            comic = c;

            //Log.i(TAG, "ComicDisplayAdapter: comic title: " + comic.getTitle());
        }

        public void addPage(String pageLink) {
            comic.addPage(pageLink);
        }

        public void addAllPages(ArrayList<String> pageLinks) {
            comic.addAllPages(pageLinks);
        }

        ArrayList<String> getAllPages() {
            return comic.getPages();
        }

        @NonNull
        @Override
        public ComicDisplayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // create a new view
            View v = (View) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.comic_item, parent, false);


            return new ComicDisplayAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ComicDisplayAdapter.ViewHolder holder, final int position) {
            String url = comic.getPages().get(position);
            Log.i("onBindViewHolder", "Binded img url: " + url);

            //build each item


            //for showing
            GlideApp.with(holder.itemView)
                    .load(url)
                    .customFormat()
                    .transition(withCrossFade())
                    .into(holder.ivPage);

//            for (int i = -5; i < 5; i++) {
//                if (position + i > 0 && position + i < comic.getPages().size()) {
//
//                    url = comic.getPages().get(position + i);
//                    GlideApp.with(holder.itemView)
//                            .load(url)
//                            .customFormat()
//                            .transition(withCrossFade())
//                            .preload();
//                }
//            }


            holder.tvPage.setText(String.valueOf(position + 1));

        }

        @Override
        public int getItemCount() {
            return comic.getTotalPage();
        }

        public int getTotalPage() {
            return comic.getTotalPage();
        }

        public void setTotalPage(int totalPage) {
            comic.setTotalPage(totalPage);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public ImageView ivPage;
            public TextView tvPage;

            ViewHolder(View v) {
                super(v);
                ivPage = v.findViewById(R.id.ivPage);
                tvPage = v.findViewById(R.id.tvPage);
            }
        }
    }

}//END class DisplayInnerPageActivity


//    private void downloadComic() {
//        int totalPage = mAdapter.getTotalPage();
//        ArrayList<String> pages = mAdapter.getAllPages();
//
//        for (int i = 0; i < pages.size(); i++) {
//            //for download
//            int pos = i + 1;
//            GlideApp.with(this)
//                    .asBitmap()
//                    .load(pages.get(i))
//                    .into(new SimpleTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                            //start download here
//                            SaveImageTask task = new SaveImageTask(comicShowing, pos, resource);
//                            task.execute();
//
//                            Log.i(TAG, "onResourceReady: resource ready: " + pos);
//                        }
//                    });
//        }
//        Log.i(TAG, "pages size: " + pages.size());
//        //Snackbar.make(mRecyclerView, "Downloaded", Snackbar.LENGTH_SHORT).show();
//
//    }