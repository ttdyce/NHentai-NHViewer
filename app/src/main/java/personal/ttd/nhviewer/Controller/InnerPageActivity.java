package personal.ttd.nhviewer.Controller;

import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.util.FixedPreloadSizeProvider;

import java.util.Collections;
import java.util.List;

import personal.ttd.nhviewer.Model.ListReturnCallBack;
import personal.ttd.nhviewer.Model.Saver.SaverMaker;
import personal.ttd.nhviewer.Model.api.NHTranlator;
import personal.ttd.nhviewer.Model.comic.Comic;
import personal.ttd.nhviewer.Model.comic.ComicMaker;
import personal.ttd.nhviewer.R;
import personal.ttd.nhviewer.glide.GlideApp;
import personal.ttd.nhviewer.glide.GlideRequests;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


public class InnerPageActivity extends AppCompatActivity {

    private final String TAG = "Inpage Activity";
    private int lastVisibleItemPosition;
    private int width, height;

    private Comic comicShowing;
    private RecyclerView mRecyclerView;
    private ComicDisplayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic);

        init();
    }

    @Override
    protected void onStop() {
        //save this comic to history
        if (lastVisibleItemPosition > 1)
            NHTranlator.Companion.updateHistory(this, comicShowing, lastVisibleItemPosition);

        super.onStop();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void init() {
        String comicid = getComicId();

        initComicShowing(comicid);
        initWidthHeight();
        initToolbar();
    }

    private void initWidthHeight() {
        Display display = getWindowManager(). getDefaultDisplay();
        Point size = new Point();
        display. getSize(size);

        //size is 1080,2030(x,y) in my case (Mi mix 2)
        width = size. x;
        height = size. y;
    }

    private String getComicId() {
        String comicid = "";
        Uri browserData = getIntent().getData();//data from browser, contains only comicid (from url)
        Bundle appData = getIntent().getExtras();//data from this app, contains Comic Object

        if (browserData != null && browserData.isHierarchical()) {//using id from browser
            comicid = browserData.getLastPathSegment();
        } else if (appData != null) {//using id from app
            Comic c = appData.getParcelable("Comic");
            comicid = c.getId();
            SaverMaker.getDefaultSaver().addHistory(c);
        }

        return comicid;
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.tbInnerPage);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() == null)
            return;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //setTitle(comicShowing.getTitle());
    }


    private void initComicShowing(String comicid) {
        ListReturnCallBack returnCallBack = comics -> {
            comicShowing = (Comic) comics.get(0);
            comicShowing.setId(comicid);

            //dont know if work
            NHTranlator.Companion.addToHistory(this, comicShowing, 0);
            initRecycleView();
        };

        ComicMaker.getComicById(comicid, this, returnCallBack);
    }

    //comicShowing is returned
    private void initRecycleView() {
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ComicDisplayAdapter(comicShowing, GlideApp.with(this));
        mRecyclerView = findViewById(R.id.rvComic);

        //mAdapter.notifyDataSetChanged();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);

        ListPreloader.PreloadSizeProvider sizeProvider = new FixedPreloadSizeProvider(width, height);
        ListPreloader.PreloadModelProvider modelProvider = new MyPreloadModelProvider();
        RecyclerViewPreloader<ContactsContract.Contacts.Photo> preloader =
                new RecyclerViewPreloader<ContactsContract.Contacts.Photo>(
                        Glide.with(this), modelProvider, sizeProvider, 10 /*maxPreload*/);

        mRecyclerView.addOnScrollListener(preloader);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {//dy > 0 = move down, dy < 0 = move up
                super.onScrolled(recyclerView, dx, dy);

                if (mLayoutManager.findFirstCompletelyVisibleItemPosition() == -1)
                    return;

                lastVisibleItemPosition = mLayoutManager.findFirstCompletelyVisibleItemPosition() + 1;
                int itemCount = mLayoutManager.getItemCount() - 1;
                int progress = 100 * lastVisibleItemPosition / itemCount;
                ProgressBar bar = findViewById(R.id.pbComic);

                bar.setProgress(progress);

            }
        });

        /*
         * check if last seen page exist.
         * if exist, show a Snackbar with a button "Go to page"
         * */
        int seenPage = SaverMaker.getDBSaver().getSeenPageDB(this, comicShowing.getId());
        Snackbar snackbarSeen = Snackbar.make(mRecyclerView, String.format("You have seen page %s", seenPage + 1), Snackbar.LENGTH_LONG)
                .setAction("Go to page", v -> mRecyclerView.scrollToPosition(seenPage));;

        if (seenPage > 0)
            snackbarSeen.show();

    }

    public class ComicDisplayAdapter extends RecyclerView.Adapter<ComicDisplayAdapter.ViewHolder> {
        private Comic comic;
        private GlideRequests glideRequests;

        ComicDisplayAdapter(Comic c, GlideRequests r) {
            comic = c;
            glideRequests = r;
        }

        @NonNull
        @Override
        public ComicDisplayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_comic, parent, false);


            return new ComicDisplayAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ComicDisplayAdapter.ViewHolder holder, final int position) {
            String url = comic.getPages().get(position);
            Log.i("onBindViewHolder", "Binded img url: " + url);

            //for showing
            glideRequests.load(url)
                    .customFormat()
                    .transition(withCrossFade())
                    .override(width, height)
                    .into(holder.ivPage);

            holder.tvPage.setText(String.valueOf(position + 1));

        }

        @Override
        public int getItemCount() {
            return comic.getTotalPage();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivPage;
            TextView tvPage;

            ViewHolder(View v) {
                super(v);
                ivPage = v.findViewById(R.id.ivPage);
                tvPage = v.findViewById(R.id.tvPage);
            }
        }
    }

    private class MyPreloadModelProvider implements ListPreloader.PreloadModelProvider {
        @NonNull
        @Override
        public List getPreloadItems(int position) {
            String url = mAdapter.comic.getPages().get(position);
            return Collections.singletonList(url);
        }

        @Nullable
        @Override
        public RequestBuilder getPreloadRequestBuilder(@NonNull Object item) {
            return Glide.with(mRecyclerView).load((String) item);
        }
    }

}//END class