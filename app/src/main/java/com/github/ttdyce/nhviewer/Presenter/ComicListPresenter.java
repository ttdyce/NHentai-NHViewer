package com.github.ttdyce.nhviewer.Presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ttdyce.nhviewer.Model.API.NHAPI;
import com.github.ttdyce.nhviewer.Model.API.ResponseCallback;
import com.github.ttdyce.nhviewer.Model.Comic.Comic;
import com.github.ttdyce.nhviewer.View.ComicActivity;
import com.github.ttdyce.nhviewer.View.ComicListViewHolder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;

public class ComicListPresenter {
    private ComicListView comicListView;
    private String collectionName, query;
    private boolean sortedPopularNow = false, hasNextPage = true;
    private int pageNow = 1;

    private ComicListAdapter adapter;
    private ResponseCallback callback;

    public ComicListPresenter(final ComicListView comicListView, String collectionName, String query) {
        this.collectionName = collectionName;
        this.query = query;

        this.comicListView = comicListView;
        this.adapter = new ComicListAdapter();
        this.callback = new ResponseCallback() {
            @Override
            public void onReponse(String result) {
                JsonArray array = new JsonParser().parse(result).getAsJsonArray();
                Gson gson = new Gson();

                if (array.size() == 0)
                    hasNextPage = false;
                else
                    for (JsonElement jsonElement : array) {

                        Comic c = gson.fromJson(jsonElement, Comic.class);
                        adapter.addComic(c);
                    }

                adapter.notifyDataSetChanged();
                comicListView.updateList();
            }
        };

        NHAPI nhapi = new NHAPI(comicListView.getContext());
        nhapi.getComicList(query, sortedPopularNow, callback);
    }

    public ComicListAdapter getAdapter() {
        return adapter;
    }

    public void onComicItemClick(int position) {
        Context activity = comicListView.getContext();
        Intent intent = new Intent(activity, ComicActivity.class);
        Comic c = adapter.comics.get(position);
        Bundle args = new Bundle();

        intent.putExtra(ComicActivity.ARG_ID, c.getId());
        intent.putExtra(ComicActivity.ARG_MID, c.getMid());
        intent.putExtra(ComicActivity.ARG_NUM_OF_PAGES, c.getNumOfPages());
        intent.putExtra(ComicActivity.ARG_PAGE_TYPES, c.getPageTypes());

        activity.startActivity(intent, args);
    }

    public void onCollectClick(int position) {
        comicListView.showAdded(false, "F Testing");

    }

    public void onFavoriteClick(int position) {
        comicListView.showAdded(true, "N/A");
    }

    public void onSortClick() {
        //toggle sort by popular
        sortedPopularNow = !sortedPopularNow;
        adapter.clear();
        comicListView.updateList();

        NHAPI nhapi = new NHAPI(comicListView.getContext());
        nhapi.getComicList(query, sortedPopularNow, callback);
    }

    public void onJumpToPageClick() {

    }

    public void loadNextPage() {
        NHAPI nhapi = new NHAPI(comicListView.getContext());
        nhapi.getComicList(query, ++pageNow,sortedPopularNow, callback);

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
            String thumbUrl = NHAPI.URLs.getThumbnail(c.getMid(), c.getImages().getThumbnail().getType());
            int numOfPages = c.getNumOfPages();

            comicListView.onBindViewHolder(holder, position, title, thumbUrl, numOfPages);
        }

        @Override
        public int getItemCount() {
            return comics.size();
        }

        public void addComic(Comic c) {
            comics.add(c);
        }

        public void clear() {
            comics.clear();
        }
    }

    public interface ComicListView {

        ComicListViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

        void onBindViewHolder(ComicListViewHolder holder, int position, String title, String thumbUrl, int numOfPages);

        void updateList();

        Context getContext();

        void showAdded(boolean isAdded, String collectionName);

    }
}
