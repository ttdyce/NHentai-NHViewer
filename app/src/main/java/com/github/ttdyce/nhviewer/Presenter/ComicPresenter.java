package com.github.ttdyce.nhviewer.Presenter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ttdyce.nhviewer.Model.API.NHAPI;
import com.github.ttdyce.nhviewer.Model.Comic.Comic;
import com.github.ttdyce.nhviewer.Model.Room.AppDatabase;
import com.github.ttdyce.nhviewer.View.ComicViewHolder;
import com.github.ttdyce.nhviewer.View.MainActivity;

public class ComicPresenter {
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
        new ComicListPresenter.AddToCollectionTask(db, AppDatabase.COL_COLLECTION_HISTORY, comic).execute();
    }

    public RecyclerView.Adapter getAdapter() {
        return adapter;
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
