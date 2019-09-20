package com.github.ttdyce.nhviewer.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.ttdyce.nhviewer.Presenter.ComicPresenter;
import com.github.ttdyce.nhviewer.R;

public class ComicActivity extends AppCompatActivity implements ComicPresenter.ComicView {
    public static final String ARG_ID = "id";
    public static final String ARG_MID = "mid";
    public static final String ARG_NUM_OF_PAGES = "numOfPages";
    public static final String ARG_PAGE_TYPES = "pageTypes";

    private int id;
    private String mid;
    private int numOfPages;
    private String[] pageTypes;
    private ComicPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic);

        init();
    }

    private void init() {
        if (getIntent().getExtras() == null)
            return;
        Bundle extras = getIntent().getExtras();
        id = extras.getInt(ARG_ID);
        mid = extras.getString(ARG_MID);
        numOfPages = extras.getInt(ARG_NUM_OF_PAGES);
        pageTypes = extras.getStringArray(ARG_PAGE_TYPES);

        presenter = new ComicPresenter(this, mid, pageTypes, numOfPages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView rvComic = findViewById(R.id.rvComic);

        rvComic.setHasFixedSize(true);
        rvComic.setAdapter(presenter.getAdapter());
        rvComic.setLayoutManager(layoutManager);
    }

    @Override
    public ComicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comic, parent, false);
        return new ComicViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ComicViewHolder holder, String url) {
        Glide.with(this)
                .load(url)
                .into(holder.ivComicPage);
    }
}
