package com.github.ttdyce.nhviewer.view;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.ttdyce.nhviewer.R;
import com.github.ttdyce.nhviewer.presenter.ComicPresenter;

import jp.wasabeef.glide.transformations.SupportRSBlurTransformation;

public class ComicActivity extends AppCompatActivity implements ComicPresenter.ComicView {
    private int id;
    private String mid;
    private String title;
    private int numOfPages;
    private String[] pageTypes;
    private ComicPresenter presenter;

    private RecyclerView rvComic;
    private ProgressBar pbComic;
    private LinearLayoutManager layoutManager;
    private CircularProgressDrawable circularProgressDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic);

        init();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private int getComicIdFromBrowser() {
        int comicid = -1;
        Uri browserData = getIntent().getData();//data from browser, contains only comicid (from url)

        if (browserData != null && browserData.isHierarchical()) {//using id from browser
            comicid = Integer.parseInt(browserData.getLastPathSegment());
        }

        return comicid;
    }


    private void init() {
        if (getIntent().getExtras() == null)
            return;
        final Bundle extras = getIntent().getExtras();
        final int idFromBrowser = getComicIdFromBrowser();
        rvComic = findViewById(R.id.rvComic);
        pbComic = findViewById(R.id.pbComic);

        layoutManager = new LinearLayoutManager(this);
        final RecyclerView rvComic = findViewById(R.id.rvComic);
        rvComic.setHasFixedSize(true);
        rvComic.setLayoutManager(layoutManager);

        presenter = ComicPresenter.factory(this, this, extras, idFromBrowser, rvComic);

        //set appbar
        Toolbar toolbar = findViewById(R.id.toolbar_comic);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public ComicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comic, parent, false);
        return new ComicViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ComicViewHolder holder, int position, String url) {
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        circularProgressDrawable = new CircularProgressDrawable(this);// TODO: 2019/10/27 many drawable object is created, may hurt performance & loading speed
        circularProgressDrawable.setStrokeWidth(10f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        //determine blur image or not
        if (pref.getBoolean(MainActivity.KEY_PREF_DEMO_MODE, false))
            Glide.with(this)
                    .load(url)
                    .placeholder(circularProgressDrawable)
                    .apply(RequestOptions.bitmapTransform(new SupportRSBlurTransformation(16, 5)))
                    .into(holder.ivComicPage);
        else
            Glide.with(this)
                    .load(url)
                    .placeholder(circularProgressDrawable)
                    .into(holder.ivComicPage);

        holder.tvComicPage.setText(String.valueOf(position + 1));

        int pos = layoutManager.findLastVisibleItemPosition();
        pbComic.setProgress(100 * pos / layoutManager.getItemCount());
    }
}
