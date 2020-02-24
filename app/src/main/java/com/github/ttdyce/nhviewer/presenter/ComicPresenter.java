package com.github.ttdyce.nhviewer.presenter;

import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ttdyce.nhviewer.model.api.NHAPI;
import com.github.ttdyce.nhviewer.model.api.ResponseCallback;
import com.github.ttdyce.nhviewer.model.comic.Comic;
import com.github.ttdyce.nhviewer.model.comic.factory.NHApiComicFactory;
import com.github.ttdyce.nhviewer.model.room.AppDatabase;
import com.github.ttdyce.nhviewer.view.ComicViewHolder;
import com.github.ttdyce.nhviewer.view.MainActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ComicPresenter {
    public static final String ARG_ID = "id";
    public static final String ARG_MID = "mid";
    public static final String ARG_TITLE = "title";
    public static final String ARG_NUM_OF_PAGES = "numOfPages";
    public static final String ARG_PAGE_TYPES = "pageTypes";

    private ComicView view;
    private final AppDatabase db;
    private ComicAdapter adapter;
    private Comic comic;

    public ComicPresenter(ComicView view,int id, String mid, String title, int numOfPages, String[] types) {
        this.view = view;
        this.db = MainActivity.getAppDatabase();
        this.comic = new Comic(id, mid, new Comic.Title(title), numOfPages, types);

        String[] urls = new String[numOfPages];
        for (int i = 0; i < numOfPages; i++) {
            urls[i] = NHAPI.URLs.getPage(mid, i+1, types[i]);
        }
        this.adapter = new ComicAdapter(urls);

        addToHistory();
    }

    private void addToHistory() {
        new ComicListPresenter.EditCollectionComicTask(db, AppDatabase.COL_COLLECTION_HISTORY, comic).execute();
    }

    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    // process id from browser || id from comic list fragment
    public static ComicPresenter factory(final Context context, final ComicView comicView, Bundle extras, int idFromBrowser, final RecyclerView rvComic){
        if(idFromBrowser != -1){
            NHApiComicFactory.getComicById(new NHAPI(context), idFromBrowser, new ResponseCallback() {
                @Override
                public void onReponse(String response) {
                    JsonObject object = new JsonParser().parse(response).getAsJsonObject();
                    Gson gson = new Gson();
                    Comic c = gson.fromJson(object, Comic.class);

                    int id = c.getId();
                    String mid = c.getMid();
                    String title = c.getTitle().toString();
                    int numOfPages = c.getNumOfPages();
                    String[] pageTypes = c.getPageTypes();

                    ComicPresenter presenter = new ComicPresenter(comicView, id, mid, title, numOfPages, pageTypes);
                    rvComic.setAdapter(presenter.getAdapter());
                }
            });

        }else{
            int id = extras.getInt(ARG_ID);
            String mid = extras.getString(ARG_MID);
            String title = extras.getString(ARG_TITLE);
            int numOfPages = extras.getInt(ARG_NUM_OF_PAGES);
            String[] pageTypes = extras.getStringArray(ARG_PAGE_TYPES);
            ComicPresenter presenter = new ComicPresenter(comicView, id, mid, title, numOfPages, pageTypes);
            rvComic.setAdapter(presenter.getAdapter());

            return presenter;
        }

        return null;//non reachable
    }

    private class ComicAdapter extends RecyclerView.Adapter<ComicViewHolder> {
        private String[] pagesUrl;

        public ComicAdapter(String[] urls) {
            pagesUrl = urls;

        }

        @NonNull
        @Override
        public ComicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return view.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull ComicViewHolder holder, int position) {
            String url = pagesUrl[position];

            view.onBindViewHolder(holder, position, url);
        }

        @Override
        public int getItemCount() {
            return pagesUrl.length;
        }
    }

    public interface ComicView {

        ComicViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

        void onBindViewHolder(ComicViewHolder holder, int position, String url);

    }
}
