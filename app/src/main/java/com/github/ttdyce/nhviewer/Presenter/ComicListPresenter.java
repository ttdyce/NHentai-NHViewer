package com.github.ttdyce.nhviewer.Presenter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ttdyce.nhviewer.Model.API.NHAPI;
import com.github.ttdyce.nhviewer.Model.API.ResponseCallback;
import com.github.ttdyce.nhviewer.Model.Comic.Comic;
import com.github.ttdyce.nhviewer.View.ComicListViewHolder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;

public class ComicListPresenter {
    private ComicListView comicListView;
    private ComicListAdapter adapter;

    public ComicListPresenter(final ComicListView comicListView) {
        this.comicListView = comicListView;
        this.adapter = new ComicListAdapter();

        NHAPI nhapi = new NHAPI(comicListView.getContext());
        ResponseCallback callback = new ResponseCallback() {
            @Override
            public void onReponse(String result) {
                JsonArray array = new JsonParser().parse(result).getAsJsonArray();
                Gson gson = new Gson();
                for (JsonElement jsonElement : array) {

                    Comic c = gson.fromJson(jsonElement, Comic.class);
                    adapter.addComic(c);
                }

                adapter.notifyDataSetChanged();
                comicListView.updateList();
            }
        };

        nhapi.getComicList("language:chinese", true, callback);
    }

    public ComicListAdapter getAdapter() {
        return adapter;
    }

    private class ComicListAdapter extends RecyclerView.Adapter<ComicListViewHolder> {
        private ArrayList<Comic> comics = new ArrayList<>();

        @NonNull
        @Override
        public ComicListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return comicListView.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull ComicListViewHolder holder, int position) {
            Comic c = comics.get(position);
            String title = c.getTitle().toString();
            String thumbUrl = NHAPI.URLs.getGetThumbnail(c.getMid(), c.getImages().getThumbnail().getType());
            int numOfPages = c.getNumOfPages();

            comicListView.onBindViewHolder(holder, title, thumbUrl, numOfPages);
        }

        @Override
        public int getItemCount() {
            return comics.size();
        }

        public void addComic(Comic c) {
            comics.add(c);
        }
    }

    public interface ComicListView {

        ComicListViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

        void onBindViewHolder(ComicListViewHolder holder, String title, String thumbUrl, int numOfPages);

        void updateList();

        Context getContext();

    }
}
