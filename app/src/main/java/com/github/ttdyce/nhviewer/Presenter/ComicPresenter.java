package com.github.ttdyce.nhviewer.Presenter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ttdyce.nhviewer.Model.API.NHAPI;
import com.github.ttdyce.nhviewer.View.ComicViewHolder;

public class ComicPresenter {
    private ComicView view;
    private ComicAdapter adapter;

    public ComicPresenter(ComicView view, String mid, String[] types, int numOfPages) {
        this.view = view;
        String[] urls = new String[numOfPages];
        for (int i = 0; i < numOfPages; i++) {
            urls[i] = NHAPI.URLs.getPage(mid, i+1, types[i]);
        }
        this.adapter = new ComicAdapter(urls);
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
